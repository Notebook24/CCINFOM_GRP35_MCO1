import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class CustomerDeliveryTrackerController {

    private CustomerDeliveryTrackerView view;
    private int customerId;
    private Map<Integer, Timer> orderTimers = new HashMap<>();
    private static final ZoneId PH_ZONE = ZoneId.of("Asia/Manila");

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

                // CRITICAL FIX: If order is paid and still shows as Pending, update it to Preparing
                String actualStatus = status;
                if ("Pending".equals(status) && isPaid) {
                    actualStatus = "Preparing";
                    // Update the database immediately
                    updateOrderStatus(orderId, "Preparing");
                }

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

    private void updateOrderStatus(int orderId, String newStatus) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE Orders SET status = ? WHERE order_id = ?")) {
            
            ps.setString(1, newStatus);
            ps.setInt(2, orderId);
            ps.executeUpdate();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
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

        switch (status) {
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
                
                // FIX: No timer for pending orders
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
                
                // FIX: Only start timer for paid orders that are actually preparing
                if (isPaid) {
                    startPrepTimer(row, orderId, prepTimeStr);
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
                row.actionButton.setEnabled(true); // Enabled
                row.actionButton.addActionListener(e -> viewReceipt(orderId)); // FIXED: Now opens receipt
                
                row.cancelButton.setText("Cancel Order");
                row.cancelButton.setBackground(Color.GRAY);
                row.cancelButton.setForeground(Color.WHITE);
                row.cancelButton.setEnabled(false);
                
                startDeliveryTimer(row, orderId, prepTimeStr, deliveryTimeStr);
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
                break;
        }
    }

    private void startPrepTimer(CustomerDeliveryTrackerView.OrderRow row,
                               int orderId,
                               String prepTimeStr) {
        
        // Parse the preparation time to seconds
        long totalPrepSeconds = parseTimeToSeconds(prepTimeStr);
        
        // Get the payment date instead of order date
        Timestamp paymentTimestamp = getPaymentDate(orderId);
        
        if (paymentTimestamp == null) {
            // No payment date found, don't start timer
            row.timeLabel.setText("--:--:--");
            return;
        }
        
        // Convert payment timestamp to PH timezone
        Instant paymentInstant = paymentTimestamp.toInstant()
                            .atZone(ZoneId.of("UTC"))
                            .withZoneSameLocal(PH_ZONE)
                            .toInstant();
        Instant nowInstant = Instant.now();
        
        // Calculate elapsed time since payment was made
        long elapsedSeconds = Duration.between(paymentInstant, nowInstant).getSeconds();
        
        // Calculate remaining time
        long remainingSeconds = totalPrepSeconds - elapsedSeconds;
        
        // If preparation time has already passed, move to next status
        if (remainingSeconds <= 0) {
            updateOrderStatus(orderId, "In Transit", row);
            return;
        }

        // Calculate the actual end time as Instant (based on payment time)
        Instant endInstant = paymentInstant.plusSeconds(totalPrepSeconds);

        Timer timer = new Timer(1000, e -> {
            long secondsLeft = Duration.between(Instant.now(), endInstant).getSeconds();

            if (secondsLeft <= 0) {
                ((Timer) e.getSource()).stop();
                orderTimers.remove(orderId);
                updateOrderStatus(orderId, "In Transit", row);
            } else {
                row.timeLabel.setText(formatTime(secondsLeft));
            }
        });

        // Show initial time
        row.timeLabel.setText(formatTime(remainingSeconds));
        timer.start();
        orderTimers.put(orderId, timer);
    }

    private void startDeliveryTimer(CustomerDeliveryTrackerView.OrderRow row,
                                   int orderId,
                                   String prepTimeStr,
                                   String deliveryTimeStr) {
        
        // Parse both time strings to seconds
        long totalPrepSeconds = parseTimeToSeconds(prepTimeStr);
        long totalDeliverySeconds = parseTimeToSeconds(deliveryTimeStr);
        
        // Calculate total time
        long totalSeconds = totalPrepSeconds + totalDeliverySeconds;
        
        // Get the payment date instead of order date
        Timestamp paymentTimestamp = getPaymentDate(orderId);
        
        if (paymentTimestamp == null) {
            // No payment date found, don't start timer
            row.timeLabel.setText("--:--:--");
            return;
        }
        
        // Convert payment timestamp to PH timezone
        Instant paymentInstant = paymentTimestamp.toInstant()
                            .atZone(ZoneId.of("UTC"))
                            .withZoneSameLocal(PH_ZONE)
                            .toInstant();
        Instant nowInstant = Instant.now();
        
        // Calculate elapsed time since payment was made
        long elapsedSeconds = Duration.between(paymentInstant, nowInstant).getSeconds();
        
        // Calculate remaining time
        long remainingSeconds = totalSeconds - elapsedSeconds;
        
        // If delivery time has already passed, mark as delivered
        if (remainingSeconds <= 0) {
            updateOrderStatus(orderId, "Delivered", row);
            return;
        }

        // Calculate the actual end time as Instant (based on payment time)
        Instant endInstant = paymentInstant.plusSeconds(totalSeconds);

        Timer timer = new Timer(1000, e -> {
            long secondsLeft = Duration.between(Instant.now(), endInstant).getSeconds();

            if (secondsLeft <= 0) {
                ((Timer) e.getSource()).stop();
                orderTimers.remove(orderId);
                updateOrderStatus(orderId, "Delivered", row);
            } else {
                row.timeLabel.setText(formatTime(secondsLeft));
            }
        });

        // Show initial time
        row.timeLabel.setText(formatTime(remainingSeconds));
        timer.start();
        orderTimers.put(orderId, timer);
    }

    // Parse time string (HH:MM:SS) to total seconds
    private long parseTimeToSeconds(String timeStr) {
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

    private void updateOrderStatus(int orderId, String newStatus, CustomerDeliveryTrackerView.OrderRow row) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE Orders SET status = ? WHERE order_id = ?")) {
            
            ps.setString(1, newStatus);
            ps.setInt(2, orderId);
            ps.executeUpdate();

            // Update UI based on new status
            switch (newStatus) {
                case "In Transit":
                    row.timeLabel.setForeground(new Color(255, 150, 0));
                    row.statusLabel.setText("IN TRANSIT");
                    row.statusLabel.setForeground(new Color(255, 150, 0));
                    row.actionButton.setText("View Receipt");
                    row.actionButton.setBackground(new Color(0, 150, 0)); // FIXED: Green instead of Gray
                    row.actionButton.setForeground(Color.WHITE);
                    row.actionButton.setEnabled(true); // FIXED: Enabled instead of disabled
                    
                    // Clear existing listeners and add the correct one
                    for (ActionListener al : row.actionButton.getActionListeners()) {
                        row.actionButton.removeActionListener(al);
                    }
                    row.actionButton.addActionListener(e -> viewReceipt(orderId)); // FIXED: view receipt
                    
                    row.cancelButton.setText("Cancel Order");
                    row.cancelButton.setBackground(Color.GRAY);
                    row.cancelButton.setForeground(Color.WHITE);
                    row.cancelButton.setEnabled(false);
                    
                    // Restart timer for delivery phase
                    restartDeliveryTimer(orderId, row);
                    break;
                    
                case "Delivered":
                    row.timeLabel.setForeground(new Color(0, 100, 0));
                    row.statusLabel.setText("DELIVERED");
                    row.statusLabel.setForeground(new Color(0, 130, 0));
                    row.timeLabel.setText("Finished!");
                    row.actionButton.setText("View Receipt");
                    row.actionButton.setBackground(new Color(0, 150, 0));
                    row.actionButton.setForeground(Color.WHITE);
                    row.actionButton.setEnabled(true);
                    
                    row.cancelButton.setText("Cancel Order");
                    row.cancelButton.setBackground(Color.GRAY);
                    row.cancelButton.setForeground(Color.WHITE);
                    row.cancelButton.setEnabled(false);
                    
                    // Clear existing listeners and add new one
                    for (ActionListener al : row.actionButton.getActionListeners()) {
                        row.actionButton.removeActionListener(al);
                    }
                    row.actionButton.addActionListener(e -> viewReceipt(orderId));
                    break;
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void restartDeliveryTimer(int orderId, CustomerDeliveryTrackerView.OrderRow row) {
        String sql = "SELECT o.delivery_time, o.preparation_time " +
                     "FROM Orders o WHERE o.order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String deliveryTimeStr = rs.getTime("delivery_time").toString();
                String prepTimeStr = rs.getTime("preparation_time").toString();
                
                startDeliveryTimer(row, orderId, prepTimeStr, deliveryTimeStr);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Button action methods
    private void openPaymentPage(int orderId) {
        JOptionPane.showMessageDialog(view.getFrame(), "Payments page coming soon!"); //PLACE PAYMENTS PAGE HERE
        
        //view.getFrame().dispose(); UNCOMMENT IF ALREADY CONNECTED TO PAYMENTS PAGE
    }

    private void cancelOrder(int orderId, CustomerDeliveryTrackerView.OrderRow row) {
        int confirm = JOptionPane.showConfirmDialog(view.getFrame(), 
            "Cancel this order?", "Confirm", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "UPDATE Orders SET status = 'Cancelled' WHERE order_id = ?")) {
                
                ps.setInt(1, orderId);
                ps.executeUpdate();

                // Stop timer
                Timer timer = orderTimers.get(orderId);
                if (timer != null) {
                    timer.stop();
                    orderTimers.remove(orderId);
                }

                // Update UI - KEEP "View Payments" button for cancelled orders
                row.statusLabel.setText("CANCELLED");
                row.statusLabel.setForeground(Color.RED);
                row.timeLabel.setText("--:--:--");
                
                // FIX: Keep "View Payments" button instead of disabling it
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
}