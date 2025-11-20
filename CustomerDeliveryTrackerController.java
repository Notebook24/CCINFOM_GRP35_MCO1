import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CustomerDeliveryTrackerController {

    private CustomerDeliveryTrackerView view;
    private int customerId;
    private Map<Integer, Timer> orderTimers = new HashMap<>();
    private static final ZoneId PH_ZONE = ZoneId.of("Asia/Manila");
    
    private static ScheduledExecutorService statusUpdateScheduler;
    private static final Map<Integer, OrderTrackingInfo> trackedOrders = new HashMap<>();

    static {
        statusUpdateScheduler = Executors.newScheduledThreadPool(1);
        statusUpdateScheduler.scheduleAtFixedRate(() -> updateAllOrderStatuses(), 0, 30, TimeUnit.SECONDS);
    }

    private static class OrderTrackingInfo {
        int orderId;
        String prepTimeStr;
        String deliveryTimeStr;
        String currentStatus;
        Timestamp paymentTimestamp;
        boolean isPaid;
        
        OrderTrackingInfo(int orderId, String prepTimeStr, String deliveryTimeStr, 
                         String currentStatus, Timestamp paymentTimestamp, boolean isPaid) {
            this.orderId = orderId;
            this.prepTimeStr = prepTimeStr;
            this.deliveryTimeStr = deliveryTimeStr;
            this.currentStatus = currentStatus;
            this.paymentTimestamp = paymentTimestamp;
            this.isPaid = isPaid;
        }
    }

    public CustomerDeliveryTrackerController(CustomerDeliveryTrackerView view, int customerId) {
        this.view = view;
        this.customerId = customerId;
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                loadOrders();
                return null;
            }
        };
        worker.execute();
        
        setupNavigation();
    }

    private void setupNavigation() {
        view.homeButton.addActionListener(e -> {
            for (Timer timer : orderTimers.values()) {
                if (timer != null) {
                    timer.stop();
                }
            }
            orderTimers.clear();
            
            view.getFrame().dispose();
            CustomerHomePageView homeView = new CustomerHomePageView();
            new CustomerHomePageController(homeView, customerId);
        });

        view.paymentsButton.addActionListener(e -> {
            view.getFrame().dispose();
            CustomerPaymentTrackerView view = new CustomerPaymentTrackerView();
            new CustomerPaymentTrackerController(view, customerId);
        });

        view.ordersButton.addActionListener(e -> {
            for (Timer timer : orderTimers.values()) {
                if (timer != null) {
                    timer.stop();
                }
            }
            orderTimers.clear();
            
            view.getFrame().dispose();
            CustomerDeliveryTrackerView newTrackerView = new CustomerDeliveryTrackerView();
            new CustomerDeliveryTrackerController(newTrackerView, customerId);
        });

        view.profileButton.addActionListener(e -> {
            for (Timer timer : orderTimers.values()) {
                if (timer != null) {
                    timer.stop();
                }
            }
            orderTimers.clear();
            
            view.getFrame().dispose();
            CustomerSettingsView settingsView = new CustomerSettingsView();
            new CustomerSettingsController(settingsView, customerId);
        });

        view.logoutButton.addActionListener(e -> {
            for (Timer timer : orderTimers.values()) {
                if (timer != null) {
                    timer.stop();
                }
            }
            orderTimers.clear();
            
            int confirm = JOptionPane.showConfirmDialog(
                view.getFrame(), 
                "Are you sure you want to logout?", 
                "Confirm Logout", 
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                view.getFrame().dispose();
                LandingPageView landingPageView = new LandingPageView();
                new LandingPageController(landingPageView);
            }
        });
    }

    private void loadOrders() {
        for (Timer timer : orderTimers.values()) {
            if (timer != null) timer.stop();
        }
        orderTimers.clear();

        view.clearOrderRows();

        String sql = "SELECT o.order_id, o.preparation_time, o.delivery_time, " +
                     "o.total_price, o.status, o.order_date " +
                     "FROM Orders o " +
                     "WHERE o.customer_id = ? " +
                     "ORDER BY o.order_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                
                Timestamp orderTimestamp = rs.getTimestamp("order_date");
                
                ZonedDateTime utcDateTime = orderTimestamp.toInstant().atZone(ZoneOffset.UTC);
                ZonedDateTime phDateTime = utcDateTime.withZoneSameInstant(PH_ZONE);
                LocalDateTime orderDateForDisplay = phDateTime.toLocalDateTime().minusHours(8);
                
                String prepTimeStr = rs.getTime("preparation_time").toString();
                String deliveryTimeStr = rs.getTime("delivery_time").toString();
                
                double subtotal = rs.getDouble("total_price");
                
                String status = rs.getString("status");

                boolean isPaid = checkPaymentStatus(orderId);
                double deliveryFee = getDeliveryFee(orderId);
                double totalPrice = subtotal + deliveryFee;

                Timestamp paymentTimestamp = getPaymentDate(orderId);
                
                registerOrderForBackgroundUpdates(orderId, prepTimeStr, deliveryTimeStr, status, paymentTimestamp, isPaid);

                String actualStatus = calculateCurrentStatus(orderId, status, isPaid, prepTimeStr, deliveryTimeStr);

                CustomerDeliveryTrackerView.OrderRow row = view.addOrderRow();

                row.dateLabel.setText(orderDateForDisplay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")));
                row.priceLabel.setText("₱" + String.format("%.2f", totalPrice));

                updateRowStatus(row, orderId, actualStatus, orderTimestamp, prepTimeStr, deliveryTimeStr, isPaid);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void registerOrderForBackgroundUpdates(int orderId, String prepTimeStr, String deliveryTimeStr, 
                                                 String currentStatus, Timestamp paymentTimestamp, boolean isPaid) {
        trackedOrders.put(orderId, new OrderTrackingInfo(orderId, prepTimeStr, deliveryTimeStr, 
                                                        currentStatus, paymentTimestamp, isPaid));
    }

    private static void updateAllOrderStatuses() {
        if (trackedOrders.isEmpty()) {
            return;
        }

        System.out.println("DEBUG: Running background order status update for " + trackedOrders.size() + " orders");
        
        for (OrderTrackingInfo orderInfo : trackedOrders.values()) {
            try {
                if ("Delivered".equals(orderInfo.currentStatus) || "Cancelled".equals(orderInfo.currentStatus) || "Discarded".equals(orderInfo.currentStatus)) {
                    continue;
                }

                if (!orderInfo.isPaid || orderInfo.paymentTimestamp == null) {
                    continue;
                }

                String calculatedStatus = calculateBackgroundStatus(orderInfo);
                
                if (!calculatedStatus.equals(orderInfo.currentStatus)) {
                    updateOrderStatusInDatabase(orderInfo.orderId, calculatedStatus);
                    orderInfo.currentStatus = calculatedStatus;
                    System.out.println("DEBUG: Background update - Order " + orderInfo.orderId + " -> " + calculatedStatus);
                    
                    notifyAdminReport();
                }
            } catch (Exception e) {
                System.err.println("Error updating order " + orderInfo.orderId + " in background: " + e.getMessage());
            }
        }
    }

    private static String calculateBackgroundStatus(OrderTrackingInfo orderInfo) {
        if (!orderInfo.isPaid || orderInfo.paymentTimestamp == null) {
            return orderInfo.currentStatus;
        }

        long prepSeconds = parseTimeToSeconds(orderInfo.prepTimeStr);
        long deliverySeconds = parseTimeToSeconds(orderInfo.deliveryTimeStr);
        long totalSeconds = prepSeconds + deliverySeconds;

        Instant paymentInstant = orderInfo.paymentTimestamp.toInstant()
                            .atZone(ZoneId.of("UTC"))
                            .withZoneSameLocal(ZoneId.of("Asia/Manila"))
                            .toInstant();
        Instant nowInstant = Instant.now();
        
        long elapsedSeconds = Duration.between(paymentInstant, nowInstant).getSeconds();

        if (elapsedSeconds <= prepSeconds) {
            return "Preparing";
        } else if (elapsedSeconds <= totalSeconds) {
            return "In Transit";
        } else {
            return "Delivered";
        }
    }

    private void updateStatusIfNeeded(int orderId, String calculatedStatus, String currentStatus) {
        if (!calculatedStatus.equals(currentStatus)) {
            System.out.println("DEBUG: Updating order " + orderId + " from " + currentStatus + " to " + calculatedStatus);
            updateOrderStatusInDatabase(orderId, calculatedStatus);
            
            OrderTrackingInfo orderInfo = trackedOrders.get(orderId);
            if (orderInfo != null) {
                orderInfo.currentStatus = calculatedStatus;
            }
        }
    }

    private boolean checkPaymentStatus(int orderId) {
        String sql = "SELECT is_paid, paid_date FROM Payments WHERE order_id = ? ORDER BY payment_id DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("is_paid") == 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private double getDeliveryFee(int orderId) {
        String sql = "SELECT delivery_fee FROM Payments WHERE order_id = ? ORDER BY payment_id DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("delivery_fee");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private Timestamp getPaymentDate(int orderId) {
        String sql = "SELECT paid_date FROM Payments WHERE order_id = ? AND is_paid = 1 ORDER BY payment_id DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getTimestamp("paid_date");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void updateOrderStatusInDatabase(int orderId, String newStatus) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE Orders SET status = ? WHERE order_id = ?")) {
            
            ps.setString(1, newStatus);
            ps.setInt(2, orderId);
            int rowsUpdated = ps.executeUpdate();
            
            System.out.println("DEBUG: Updated order " + orderId + " to " + newStatus + " (" + rowsUpdated + " rows affected)");
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void notifyAdminReport() {
        try {
            if (AdminOrderReportController.getInstance() != null) {
                AdminOrderReportController.getInstance().forceRefresh();
            }
        } catch (Exception e) {
            System.out.println("Admin report not open, changes will show on next auto-refresh");
        }
    }

    private void updateRowStatus(CustomerDeliveryTrackerView.OrderRow row,
                                 int orderId, String status,
                                 Timestamp orderTimestamp,
                                 String prepTimeStr,
                                 String deliveryTimeStr,
                                 boolean isPaid) {

        for (ActionListener al : row.actionButton.getActionListeners()) {
            row.actionButton.removeActionListener(al);
        }
        for (ActionListener al : row.cancelButton.getActionListeners()) {
            row.cancelButton.removeActionListener(al);
        }

        Timer existingTimer = orderTimers.get(orderId);
        if (existingTimer != null) {
            existingTimer.stop();
            orderTimers.remove(orderId);
        }

        row.statusLabel.setForeground(Color.BLACK);
        row.timeLabel.setText("--:--:--");

        row.actionButton.setBackground(null);
        row.actionButton.setForeground(Color.BLACK);
        row.actionButton.setOpaque(true);
        row.actionButton.setEnabled(true);
        row.actionButton.setVisible(true);
        
        row.cancelButton.setBackground(null);
        row.cancelButton.setForeground(Color.BLACK);
        row.cancelButton.setOpaque(true);
        row.cancelButton.setEnabled(true);

        String calculatedStatus = calculateCurrentStatus(orderId, status, isPaid, prepTimeStr, deliveryTimeStr);
        updateStatusIfNeeded(orderId, calculatedStatus, status);
        
        String displayStatus = calculatedStatus;

        switch (displayStatus) {
            case "Pending":
                row.statusLabel.setText("PENDING");
                row.statusLabel.setForeground(Color.RED);
                row.actionButton.setText("View Payments");
                row.actionButton.setBackground(Color.ORANGE);
                row.actionButton.setForeground(Color.WHITE);
                row.actionButton.addActionListener(e -> openPaymentPage(orderId));
                
                row.cancelButton.setText("Discard Order");
                row.cancelButton.setBackground(Color.RED);
                row.cancelButton.setForeground(Color.WHITE);
                row.cancelButton.addActionListener(e -> cancelOrder(orderId, row));
                
                row.timeLabel.setText("--:--:--");
                break;

            case "Preparing":
                row.statusLabel.setText("PREPARING");
                row.statusLabel.setForeground(Color.ORANGE);
                row.actionButton.setText("View Receipt");
                row.actionButton.setBackground(new Color(0, 150, 0));
                row.actionButton.setForeground(Color.WHITE);
                row.actionButton.addActionListener(e -> viewReceipt(orderId));
                
                row.cancelButton.setText("Cancel Order");
                row.cancelButton.setBackground(Color.RED);
                row.cancelButton.setForeground(Color.WHITE);
                row.cancelButton.addActionListener(e -> cancelOrder(orderId, row));
                
                if (isPaid) {
                    startCountdownTimer(row, orderId, prepTimeStr, deliveryTimeStr, displayStatus);
                } else {
                    row.timeLabel.setText("--:--:--");
                }
                break;

            case "In Transit":
                row.statusLabel.setText("IN TRANSIT");
                row.statusLabel.setForeground(new Color(255, 150, 0));
                row.actionButton.setText("View Receipt");
                row.actionButton.setBackground(new Color(0, 150, 0));
                row.actionButton.setForeground(Color.WHITE);
                row.actionButton.setEnabled(true);
                row.actionButton.addActionListener(e -> viewReceipt(orderId));
                
                row.cancelButton.setText("Cancel Order");
                row.cancelButton.setBackground(Color.GRAY);
                row.cancelButton.setForeground(Color.WHITE);
                row.cancelButton.setEnabled(false);
                
                if (isPaid) {
                    startCountdownTimer(row, orderId, prepTimeStr, deliveryTimeStr, displayStatus);
                } else {
                    row.timeLabel.setText("--:--:--");
                }
                break;

            case "Delivered":
                row.statusLabel.setText("DELIVERED");
                row.statusLabel.setForeground(new Color(0, 130, 0));
                row.timeLabel.setText("Finished!");
                row.actionButton.setText("View Receipt");
                row.actionButton.setBackground(new Color(0, 150, 0));
                row.actionButton.setForeground(Color.WHITE);
                row.actionButton.addActionListener(e -> viewReceipt(orderId));
                
                row.cancelButton.setText("Cancel Order");
                row.cancelButton.setBackground(Color.GRAY);
                row.cancelButton.setForeground(Color.WHITE);
                row.cancelButton.setEnabled(false);
                break;
                
            case "Cancelled":
                row.statusLabel.setText("CANCELLED");
                row.statusLabel.setForeground(Color.RED);
                row.timeLabel.setText("--:--:--");
                
                row.actionButton.setText("View Receipt");
                row.actionButton.setBackground(Color.ORANGE);
                row.actionButton.setForeground(Color.WHITE);
                row.actionButton.setEnabled(true);
                row.actionButton.addActionListener(e -> viewReceipt(orderId));
                
                row.cancelButton.setText("Cancelled");
                row.cancelButton.setBackground(Color.GRAY);
                row.cancelButton.setForeground(Color.WHITE);
                row.cancelButton.setEnabled(false);
                
                trackedOrders.remove(orderId);
                break;
                
            case "Discarded":
                row.statusLabel.setText("DISCARDED");
                row.statusLabel.setForeground(Color.GRAY);
                row.timeLabel.setText("--:--:--");
                
                row.actionButton.setVisible(false);
                
                row.cancelButton.setText("Discarded");
                row.cancelButton.setBackground(Color.GRAY);
                row.cancelButton.setForeground(Color.WHITE);
                row.cancelButton.setEnabled(false);
                
                trackedOrders.remove(orderId);
                break;
        }
    }

    private void startCountdownTimer(CustomerDeliveryTrackerView.OrderRow row,
                                   int orderId,
                                   String prepTimeStr,
                                   String deliveryTimeStr,
                                   String currentStatus) {
        
        Timestamp paymentTimestamp = getPaymentDate(orderId);
        if (paymentTimestamp == null) {
            row.timeLabel.setText("--:--:--");
            return;
        }

        long prepSeconds = parseTimeToSeconds(prepTimeStr);
        long deliverySeconds = parseTimeToSeconds(deliveryTimeStr);
        long totalSeconds = prepSeconds + deliverySeconds;

        Instant paymentInstant = paymentTimestamp.toInstant()
                            .atZone(ZoneId.of("UTC"))
                            .withZoneSameLocal(PH_ZONE)
                            .toInstant();
        
        Instant endInstant;
        if ("Preparing".equals(currentStatus)) {
            endInstant = paymentInstant.plusSeconds(prepSeconds);
        } else {
            endInstant = paymentInstant.plusSeconds(totalSeconds);
        }

        Timer timer = new Timer(1000, e -> {
            long secondsLeft = Duration.between(Instant.now(), endInstant).getSeconds();

            if (secondsLeft <= 0) {
                ((Timer) e.getSource()).stop();
                orderTimers.remove(orderId);
                loadOrders();
            } else {
                row.timeLabel.setText(formatTime(secondsLeft));
            }
        });

        long initialSecondsLeft = Duration.between(Instant.now(), endInstant).getSeconds();
        if (initialSecondsLeft > 0) {
            row.timeLabel.setText(formatTime(initialSecondsLeft));
            timer.start();
            orderTimers.put(orderId, timer);
        } else {
            row.timeLabel.setText("--:--:--");
        }
    }

    private static long parseTimeToSeconds(String timeStr) {
        try {
            String[] parts = timeStr.split(":");
            long hours = Long.parseLong(parts[0]);
            long minutes = Long.parseLong(parts[1]);
            long seconds = parts.length > 2 ? Long.parseLong(parts[2]) : 0;
            
            long totalSeconds = (hours * 3600) + (minutes * 60) + seconds;
            return totalSeconds;
        } catch (Exception e) {
            return 0;
        }
    }

    private void openPaymentPage(int orderId) {
        view.getFrame().dispose();
        CustomerPaymentTrackerView view = new CustomerPaymentTrackerView();
        new CustomerPaymentTrackerController(view, customerId);
    }

    private void cancelOrder(int orderId, CustomerDeliveryTrackerView.OrderRow row) {
        String currentStatus = getCurrentOrderStatus(orderId);
        boolean isPaid = checkPaymentStatus(orderId);
        
        String newStatus;
        String message;
        
        if ("Pending".equals(currentStatus) && !isPaid) {
            newStatus = "Discarded";
            message = "Your order has been discarded.";
        } else if ("Preparing".equals(currentStatus) && isPaid) {
            double orderTotal = getOrderTotalPrice(orderId);
            newStatus = "Cancelled";
            message = String.format("You have cancelled your order. You are refunded ₱%.2f.", orderTotal);
            
            // Update the payment record to mark as refunded
            updateReceiptForRefund(orderId);
            
            // NEW: Automatically open the updated receipt
            autoOpenUpdatedReceipt(orderId, message);
            return; // Return early since we're navigating to receipt
        } else {
            newStatus = "Cancelled";
            message = "Your order has been cancelled.";
        }
        
        int confirm = JOptionPane.showConfirmDialog(view.getFrame(), 
            "Are you sure you want to cancel this order?", "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "UPDATE Orders SET status = ? WHERE order_id = ?")) {
                
                ps.setString(1, newStatus);
                ps.setInt(2, orderId);
                int rowsUpdated = ps.executeUpdate();
                System.out.println("DEBUG: Updated order " + orderId + " to " + newStatus + " (" + rowsUpdated + " rows affected)");

                JOptionPane.showMessageDialog(view.getFrame(), message);

                notifyAdminReport();

                Timer timer = orderTimers.get(orderId);
                if (timer != null) {
                    timer.stop();
                    orderTimers.remove(orderId);
                }

                trackedOrders.remove(orderId);

                updateUIAfterCancellation(row, newStatus);

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // NEW METHOD: Automatically open the updated receipt after cancellation
    private void autoOpenUpdatedReceipt(int orderId, String message) {
        int confirm = JOptionPane.showConfirmDialog(view.getFrame(), 
            "Are you sure you want to cancel this order?", "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "UPDATE Orders SET status = ? WHERE order_id = ?")) {
                
                ps.setString(1, "Cancelled");
                ps.setInt(2, orderId);
                int rowsUpdated = ps.executeUpdate();
                System.out.println("DEBUG: Updated order " + orderId + " to Cancelled (" + rowsUpdated + " rows affected)");

                notifyAdminReport();

                // Stop any timers for this order
                Timer timer = orderTimers.get(orderId);
                if (timer != null) {
                    timer.stop();
                    orderTimers.remove(orderId);
                }

                trackedOrders.remove(orderId);

                // Show confirmation message
                JOptionPane.showMessageDialog(view.getFrame(), message);

                // Automatically open the receipt with updated refund information
                SwingUtilities.invokeLater(() -> {
                    for (Timer t : orderTimers.values()) {
                        if (t != null) {
                            t.stop();
                        }
                    }
                    orderTimers.clear();
                    
                    int paymentId = getPaymentIdForOrder(orderId);
                    
                    // Create new receipt view and controller to ensure fresh data
                    CustomerReceiptPageView receiptView = new CustomerReceiptPageView();
                    new CustomerReceiptPageController(receiptView, customerId, orderId, paymentId);
                    view.getFrame().dispose();
                });

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private String getCurrentOrderStatus(int orderId) {
        String sql = "SELECT status FROM Orders WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getString("status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Pending";
    }

    private double getOrderTotalPrice(int orderId) {
        String sql = "SELECT total_price FROM Orders WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total_price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private void updateReceiptForRefund(int orderId) {
        // Update the payment record to mark as refunded when order in "Preparing" status is cancelled
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE Payments SET is_refunded = 1 WHERE order_id = ?")) {
            
            ps.setInt(1, orderId);
            int rowsUpdated = ps.executeUpdate();
            System.out.println("DEBUG: Order " + orderId + " marked as refunded in Payments table (" + rowsUpdated + " rows affected)");
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateUIAfterCancellation(CustomerDeliveryTrackerView.OrderRow row, String status) {
        if ("Discarded".equals(status)) {
            row.statusLabel.setText("DISCARDED");
            row.statusLabel.setForeground(Color.GRAY);
            row.timeLabel.setText("--:--:--");
            
            row.actionButton.setVisible(false);
            
            row.cancelButton.setText("Discarded");
            row.cancelButton.setBackground(Color.GRAY);
            row.cancelButton.setForeground(Color.WHITE);
            row.cancelButton.setEnabled(false);
            
        } else {
            row.statusLabel.setText("CANCELLED");
            row.statusLabel.setForeground(Color.RED);
            row.timeLabel.setText("--:--:--");
            
            row.actionButton.setText("View Receipt");
            row.actionButton.setBackground(Color.ORANGE);
            row.actionButton.setForeground(Color.WHITE);
            row.actionButton.setEnabled(true);
            row.actionButton.setVisible(true);
            
            for (ActionListener al : row.actionButton.getActionListeners()) {
                row.actionButton.removeActionListener(al);
            }
            row.actionButton.addActionListener(e -> viewReceipt(getOrderIdFromRow(row)));
            
            row.cancelButton.setText("Cancelled");
            row.cancelButton.setBackground(Color.GRAY);
            row.cancelButton.setForeground(Color.WHITE);
            row.cancelButton.setEnabled(false);
        }
    }

    private int getOrderIdFromRow(CustomerDeliveryTrackerView.OrderRow row) {
        for (int orderId : trackedOrders.keySet()) {
            if (trackedOrders.get(orderId) != null) {
                return orderId;
            }
        }
        return -1;
    }

    private void viewReceipt(int orderId) {
        for (Timer timer : orderTimers.values()) {
            if (timer != null) {
                timer.stop();
            }
        }
        orderTimers.clear();
        
        int paymentId = getPaymentIdForOrder(orderId);
        
        // Create new receipt view and controller to ensure fresh data
        CustomerReceiptPageView receiptView = new CustomerReceiptPageView();
        new CustomerReceiptPageController(receiptView, customerId, orderId, paymentId);
        view.getFrame().dispose();
    }

    private int getPaymentIdForOrder(int orderId) {
        String sql = "SELECT payment_id FROM Payments WHERE order_id = ? ORDER BY payment_id DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("payment_id");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    private String formatTime(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private String calculateCurrentStatus(int orderId, String currentStatus, boolean isPaid, String prepTimeStr, String deliveryTimeStr) {
        if (!isPaid || "Delivered".equals(currentStatus) || "Cancelled".equals(currentStatus) || "Discarded".equals(currentStatus)) {
            return currentStatus;
        }

        Timestamp paymentTimestamp = getPaymentDate(orderId);
        if (paymentTimestamp == null) {
            return currentStatus;
        }

        long prepSeconds = parseTimeToSeconds(prepTimeStr);
        long deliverySeconds = parseTimeToSeconds(deliveryTimeStr);
        long totalSeconds = prepSeconds + deliverySeconds;

        Instant paymentInstant = paymentTimestamp.toInstant()
                            .atZone(ZoneId.of("UTC"))
                            .withZoneSameLocal(PH_ZONE)
                            .toInstant();
        Instant nowInstant = Instant.now();
        
        long elapsedSeconds = Duration.between(paymentInstant, nowInstant).getSeconds();

        if (elapsedSeconds <= prepSeconds) {
            return "Preparing";
        } else if (elapsedSeconds <= totalSeconds) {
            return "In Transit";
        } else {
            return "Delivered";
        }
    }
}