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
    
    // Background scheduler for updating order statuses
    private static ScheduledExecutorService statusUpdateScheduler;
    private static final Map<Integer, OrderTrackingInfo> trackedOrders = new HashMap<>();

    static {
        // Initialize the background scheduler
        statusUpdateScheduler = Executors.newScheduledThreadPool(1);
        statusUpdateScheduler.scheduleAtFixedRate(() -> updateAllOrderStatuses(), 0, 30, TimeUnit.SECONDS);
    }

    // Inner class to track order information for background updates
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
        
        // Load orders in background thread to prevent UI freeze
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
        // Home button
        view.homeButton.addActionListener(e -> {
            // Stop all timers before navigating
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

        // Payments button
        view.paymentsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(view.getFrame(), "Payments page coming soon!");
        });

        // Orders button - reload current page
        view.ordersButton.addActionListener(e -> {
            // Stop all timers before navigating
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

        // Profile button
        view.profileButton.addActionListener(e -> {
            // Stop all timers before navigating
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

        // Logout button
        view.logoutButton.addActionListener(e -> {
            // Stop all timers before logging out
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
        // Clear existing timers
        for (Timer timer : orderTimers.values()) {
            if (timer != null) timer.stop();
        }
        orderTimers.clear();

        // Clear existing UI rows
        view.clearOrderRows();

        // FIXED SQL QUERY: Get only orders, check payment status separately
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
                
                // Get the original UTC timestamp from database
                Timestamp orderTimestamp = rs.getTimestamp("order_date");
                
                // Convert to PH time for display only
                ZonedDateTime utcDateTime = orderTimestamp.toInstant().atZone(ZoneOffset.UTC);
                ZonedDateTime phDateTime = utcDateTime.withZoneSameInstant(PH_ZONE);
                LocalDateTime orderDateForDisplay = phDateTime.toLocalDateTime().minusHours(8);
                
                String prepTimeStr = rs.getTime("preparation_time").toString();
                String deliveryTimeStr = rs.getTime("delivery_time").toString();
                
                double subtotal = rs.getDouble("total_price");
                
                String status = rs.getString("status");

                // Check payment status separately to avoid duplicates
                boolean isPaid = checkPaymentStatus(orderId);
                double deliveryFee = getDeliveryFee(orderId);
                double totalPrice = subtotal + deliveryFee;

                // Get payment timestamp for background tracking
                Timestamp paymentTimestamp = getPaymentDate(orderId);
                
                // Register order for background status updates
                registerOrderForBackgroundUpdates(orderId, prepTimeStr, deliveryTimeStr, status, paymentTimestamp, isPaid);

                // CRITICAL FIX: Calculate actual status based on payment time and elapsed time
                String actualStatus = calculateCurrentStatus(orderId, status, isPaid, prepTimeStr, deliveryTimeStr);

                // Only create a row for the order
                CustomerDeliveryTrackerView.OrderRow row = view.addOrderRow();

                // Set basic values with PH time for display
                row.dateLabel.setText(orderDateForDisplay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")));
                row.priceLabel.setText("₱" + String.format("%.2f", totalPrice));

                updateRowStatus(row, orderId, actualStatus, orderTimestamp, prepTimeStr, deliveryTimeStr, isPaid);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // NEW METHOD: Register order for background status updates
    private void registerOrderForBackgroundUpdates(int orderId, String prepTimeStr, String deliveryTimeStr, 
                                                 String currentStatus, Timestamp paymentTimestamp, boolean isPaid) {
        trackedOrders.put(orderId, new OrderTrackingInfo(orderId, prepTimeStr, deliveryTimeStr, 
                                                        currentStatus, paymentTimestamp, isPaid));
    }

    // NEW METHOD: Background task to update all order statuses
    private static void updateAllOrderStatuses() {
        if (trackedOrders.isEmpty()) {
            return;
        }

        System.out.println("DEBUG: Running background order status update for " + trackedOrders.size() + " orders");
        
        for (OrderTrackingInfo orderInfo : trackedOrders.values()) {
            try {
                // Skip if order is in final state
                if ("Delivered".equals(orderInfo.currentStatus) || "Cancelled".equals(orderInfo.currentStatus)) {
                    continue;
                }

                // Skip if not paid
                if (!orderInfo.isPaid || orderInfo.paymentTimestamp == null) {
                    continue;
                }

                // Calculate what the status should be
                String calculatedStatus = calculateBackgroundStatus(orderInfo);
                
                // Update database if status changed
                if (!calculatedStatus.equals(orderInfo.currentStatus)) {
                    updateOrderStatusInDatabase(orderInfo.orderId, calculatedStatus);
                    orderInfo.currentStatus = calculatedStatus;
                    System.out.println("DEBUG: Background update - Order " + orderInfo.orderId + " -> " + calculatedStatus);
                    
                    // Notify admin report
                    notifyAdminReport();
                }
            } catch (Exception e) {
                System.err.println("Error updating order " + orderInfo.orderId + " in background: " + e.getMessage());
            }
        }
    }

    // NEW METHOD: Calculate status for background updates
    private static String calculateBackgroundStatus(OrderTrackingInfo orderInfo) {
        if (!orderInfo.isPaid || orderInfo.paymentTimestamp == null) {
            return orderInfo.currentStatus;
        }

        // Parse time strings to seconds
        long prepSeconds = parseTimeToSeconds(orderInfo.prepTimeStr);
        long deliverySeconds = parseTimeToSeconds(orderInfo.deliveryTimeStr);
        long totalSeconds = prepSeconds + deliverySeconds;

        // Convert payment timestamp to PH timezone
        Instant paymentInstant = orderInfo.paymentTimestamp.toInstant()
                            .atZone(ZoneId.of("UTC"))
                            .withZoneSameLocal(ZoneId.of("Asia/Manila"))
                            .toInstant();
        Instant nowInstant = Instant.now();
        
        // Calculate elapsed time since payment was made
        long elapsedSeconds = Duration.between(paymentInstant, nowInstant).getSeconds();

        // Determine status based on elapsed time
        if (elapsedSeconds <= prepSeconds) {
            return "Preparing";
        } else if (elapsedSeconds <= totalSeconds) {
            return "In Transit";
        } else {
            return "Delivered";
        }
    }

    // NEW METHOD: Update database status if needed
    private void updateStatusIfNeeded(int orderId, String calculatedStatus, String currentStatus) {
        if (!calculatedStatus.equals(currentStatus)) {
            System.out.println("DEBUG: Updating order " + orderId + " from " + currentStatus + " to " + calculatedStatus);
            updateOrderStatusInDatabase(orderId, calculatedStatus);
            
            // Update the tracked order's current status
            OrderTrackingInfo orderInfo = trackedOrders.get(orderId);
            if (orderInfo != null) {
                orderInfo.currentStatus = calculatedStatus;
            }
        }
    }

    // Helper method to check payment status
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

    // Helper method to get delivery fee
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

    // NEW METHOD: Get payment date for an order
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

    // UPDATED METHOD: Update order status in database
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

    // FIXED METHOD: Notify Admin Order Report to refresh
    private static void notifyAdminReport() {
        try {
            // Use the enhanced notification system
            if (AdminOrderReportController.getInstance() != null) {
                AdminOrderReportController.getInstance().forceRefresh();
            }
        } catch (Exception e) {
            // If admin report isn't open, just ignore - the 5-second refresh will catch it
            System.out.println("Admin report not open, changes will show on next auto-refresh");
        }
    }

    private void updateRowStatus(CustomerDeliveryTrackerView.OrderRow row,
                                 int orderId, String status,
                                 Timestamp orderTimestamp,
                                 String prepTimeStr,
                                 String deliveryTimeStr,
                                 boolean isPaid) {

        // Clear any existing action listeners from both buttons
        for (ActionListener al : row.actionButton.getActionListeners()) {
            row.actionButton.removeActionListener(al);
        }
        for (ActionListener al : row.cancelButton.getActionListeners()) {
            row.cancelButton.removeActionListener(al);
        }

        // Stop existing timer for this order
        Timer existingTimer = orderTimers.get(orderId);
        if (existingTimer != null) {
            existingTimer.stop();
            orderTimers.remove(orderId);
        }

        // Reset styling
        row.statusLabel.setForeground(Color.BLACK);
        row.timeLabel.setText("--:--:--");

        // Reset button styling
        row.actionButton.setBackground(null);
        row.actionButton.setForeground(Color.BLACK);
        row.actionButton.setOpaque(true);
        row.actionButton.setEnabled(true);
        
        row.cancelButton.setBackground(null);
        row.cancelButton.setForeground(Color.BLACK);
        row.cancelButton.setOpaque(true);
        row.cancelButton.setEnabled(true);

        // Update database if status needs to be changed
        String calculatedStatus = calculateCurrentStatus(orderId, status, isPaid, prepTimeStr, deliveryTimeStr);
        updateStatusIfNeeded(orderId, calculatedStatus, status);
        
        // Use the calculated status for display
        String displayStatus = calculatedStatus;

        switch (displayStatus) {
            case "Pending":
                row.statusLabel.setText("PENDING");
                row.statusLabel.setForeground(Color.RED);
                row.actionButton.setText("View Payments");
                row.actionButton.setBackground(Color.ORANGE);
                row.actionButton.setForeground(Color.WHITE);
                row.actionButton.addActionListener(e -> openPaymentPage(orderId));
                
                row.cancelButton.setText("Cancel Order");
                row.cancelButton.setBackground(Color.RED);
                row.cancelButton.setForeground(Color.WHITE);
                row.cancelButton.addActionListener(e -> cancelOrder(orderId, row));
                
                row.timeLabel.setText("--:--:--");
                break;

            case "Preparing":
                row.statusLabel.setText("PREPARING");
                row.statusLabel.setForeground(Color.ORANGE);
                row.actionButton.setText("View Receipt");
                row.actionButton.setBackground(new Color(0, 150, 0)); // Green
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
                row.actionButton.setBackground(new Color(0, 150, 0)); // Green
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
                row.actionButton.setBackground(new Color(0, 150, 0)); // Green
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
                
                row.actionButton.setText("View Payments");
                row.actionButton.setBackground(Color.ORANGE);
                row.actionButton.setForeground(Color.WHITE);
                row.actionButton.setEnabled(true);
                row.actionButton.addActionListener(e -> openPaymentPage(orderId));
                
                row.cancelButton.setText("Cancelled");
                row.cancelButton.setBackground(Color.GRAY);
                row.cancelButton.setForeground(Color.WHITE);
                row.cancelButton.setEnabled(false);
                
                // Remove from background tracking when cancelled
                trackedOrders.remove(orderId);
                break;
        }
    }

    // NEW METHOD: Start countdown timer that shows remaining time
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

        // Parse time strings to seconds
        long prepSeconds = parseTimeToSeconds(prepTimeStr);
        long deliverySeconds = parseTimeToSeconds(deliveryTimeStr);
        long totalSeconds = prepSeconds + deliverySeconds;

        // Convert payment timestamp to PH timezone
        Instant paymentInstant = paymentTimestamp.toInstant()
                            .atZone(ZoneId.of("UTC"))
                            .withZoneSameLocal(PH_ZONE)
                            .toInstant();
        
        // Calculate end time based on current status
        Instant endInstant;
        if ("Preparing".equals(currentStatus)) {
            endInstant = paymentInstant.plusSeconds(prepSeconds);
        } else { // "In Transit"
            endInstant = paymentInstant.plusSeconds(totalSeconds);
        }

        Timer timer = new Timer(1000, e -> {
            long secondsLeft = Duration.between(Instant.now(), endInstant).getSeconds();

            if (secondsLeft <= 0) {
                ((Timer) e.getSource()).stop();
                orderTimers.remove(orderId);
                // Reload the page to get updated status
                loadOrders();
            } else {
                row.timeLabel.setText(formatTime(secondsLeft));
            }
        });

        // Show initial time
        long initialSecondsLeft = Duration.between(Instant.now(), endInstant).getSeconds();
        if (initialSecondsLeft > 0) {
            row.timeLabel.setText(formatTime(initialSecondsLeft));
            timer.start();
            orderTimers.put(orderId, timer);
        } else {
            row.timeLabel.setText("--:--:--");
        }
    }

    // Parse time string (HH:MM:SS) to total seconds
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

    // Button action methods
    private void openPaymentPage(int orderId) {
        JOptionPane.showMessageDialog(view.getFrame(), "Payments page coming soon!");
    }

    private void cancelOrder(int orderId, CustomerDeliveryTrackerView.OrderRow row) {
        int confirm = JOptionPane.showConfirmDialog(view.getFrame(), 
            "Cancel this order?", "Confirm", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "UPDATE Orders SET status = 'Cancelled' WHERE order_id = ?")) {
                
                ps.setInt(1, orderId);
                int rowsUpdated = ps.executeUpdate();
                System.out.println("DEBUG: Cancelled order " + orderId + " (" + rowsUpdated + " rows affected)");

                // NOTIFY ADMIN REPORT
                notifyAdminReport();

                // Stop timer
                Timer timer = orderTimers.get(orderId);
                if (timer != null) {
                    timer.stop();
                    orderTimers.remove(orderId);
                }

                // Remove from background tracking
                trackedOrders.remove(orderId);

                // Update UI
                row.statusLabel.setText("CANCELLED");
                row.statusLabel.setForeground(Color.RED);
                row.timeLabel.setText("--:--:--");
                
                row.actionButton.setText("View Payments");
                row.actionButton.setBackground(Color.ORANGE);
                row.actionButton.setForeground(Color.WHITE);
                row.actionButton.setEnabled(true);
                
                // Clear existing listeners and add the payment page listener
                for (ActionListener al : row.actionButton.getActionListeners()) {
                    row.actionButton.removeActionListener(al);
                }
                row.actionButton.addActionListener(e -> openPaymentPage(orderId));
                
                row.cancelButton.setText("Cancelled");
                row.cancelButton.setBackground(Color.GRAY);
                row.cancelButton.setForeground(Color.WHITE);
                row.cancelButton.setEnabled(false);
                
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void viewReceipt(int orderId) {
        // Stop timers before navigating
        for (Timer timer : orderTimers.values()) {
            if (timer != null) {
                timer.stop();
            }
        }
        orderTimers.clear();
        
        // Get payment ID for this order
        int paymentId = getPaymentIdForOrder(orderId);
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

    // NEW METHOD: Calculate current status based on payment time and elapsed time
    private String calculateCurrentStatus(int orderId, String currentStatus, boolean isPaid, String prepTimeStr, String deliveryTimeStr) {
        // If not paid or already in final state, return current status
        if (!isPaid || "Delivered".equals(currentStatus) || "Cancelled".equals(currentStatus)) {
            return currentStatus;
        }

        Timestamp paymentTimestamp = getPaymentDate(orderId);
        if (paymentTimestamp == null) {
            return currentStatus;
        }

        // Parse time strings to seconds
        long prepSeconds = parseTimeToSeconds(prepTimeStr);
        long deliverySeconds = parseTimeToSeconds(deliveryTimeStr);
        long totalSeconds = prepSeconds + deliverySeconds;

        // Convert payment timestamp to PH timezone
        Instant paymentInstant = paymentTimestamp.toInstant()
                            .atZone(ZoneId.of("UTC"))
                            .withZoneSameLocal(PH_ZONE)
                            .toInstant();
        Instant nowInstant = Instant.now();
        
        // Calculate elapsed time since payment was made
        long elapsedSeconds = Duration.between(paymentInstant, nowInstant).getSeconds();

        // Determine status based on elapsed time
        if (elapsedSeconds <= prepSeconds) {
            return "Preparing";
        } else if (elapsedSeconds <= totalSeconds) {
            return "In Transit";
        } else {
            return "Delivered";
        }
    }
}