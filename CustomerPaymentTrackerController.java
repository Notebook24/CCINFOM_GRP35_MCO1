import javax.swing.*;
import java.sql.*;

public class CustomerPaymentTrackerController {
    private CustomerPaymentTrackerView view;
    private int customerId;

    public CustomerPaymentTrackerController(CustomerPaymentTrackerView view, int customerId) {
        this.view = view;
        this.customerId = customerId;

        setupNavigation();
        loadPayments();
    }

    private void loadPayments() {
        // Show all payments including refunded ones
        String sql =
            "SELECT p.payment_id, p.order_id, p.delivery_fee, p.total_price, " +
            "p.is_paid, p.is_refunded, p.paid_date, o.order_date, o.status " +  
            "FROM Payments p " +
            "JOIN Orders o ON p.order_id = o.order_id " +
            "WHERE p.customer_id = ? " +
            "ORDER BY p.is_paid ASC, o.order_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int paymentId = rs.getInt("payment_id");
                int orderId = rs.getInt("order_id");
                boolean isPaid = rs.getBoolean("is_paid");
                boolean isRefunded = rs.getBoolean("is_refunded");
                Timestamp orderCreated = rs.getTimestamp("order_date");
                Timestamp paidDate = rs.getTimestamp("paid_date");
                double total = rs.getDouble("total_price");

                String orderDateStr = (orderCreated == null)
                        ? "N/A"
                        : formatDateTime(orderCreated);

                String paidOnStr = (paidDate == null)
                        ? "N/A"
                        : formatDateTime(paidDate);

                String statusTemp;
                boolean showReceiptButton = false;

                // Determine status based on payment and refund status
                if (isRefunded) {
                    statusTemp = "REFUNDED";
                    showReceiptButton = true; // Allow viewing receipt for refunded orders
                } else if (isPaid) {
                    statusTemp = "PAID";
                    showReceiptButton = true;
                } else {
                    statusTemp = "PENDING";
                    showReceiptButton = false;
                }                
                
                String price = String.format("₱%.2f", total);

                // Add row to View
                JButton btn = view.addPaymentRow(
                        orderDateStr,
                        statusTemp,
                        price,
                        paidOnStr,
                        isPaid || isRefunded // Enable button for paid or refunded orders
                );

                if (showReceiptButton) {
                    // PAID or REFUNDED → View Receipt
                    btn.addActionListener(e -> {
                        view.getFrame().dispose();
                        CustomerReceiptPageView receiptView = new CustomerReceiptPageView();
                        new CustomerReceiptPageController(receiptView, customerId, orderId, paymentId);
                    });
                } else {
                    // PENDING → Pay Now
                    btn.addActionListener(e -> {
                        view.getFrame().dispose();
                        CustomerPaymentSessionView payView = new CustomerPaymentSessionView();
                        new CustomerPaymentSessionController(payView, customerId, orderId);
                    });
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view.getFrame(), "Error loading payments: " + e.getMessage());
        }
    }

    // NAVIGATION BAR
    private void setupNavigation() {
        view.getHomeButton().addActionListener(e -> {
            view.getFrame().dispose();
            CustomerHomePageView home = new CustomerHomePageView();
            new CustomerHomePageController(home, customerId);
        });

        view.getPaymentsButton().addActionListener(e -> {
            view.getFrame().dispose();
            CustomerPaymentTrackerView view = new CustomerPaymentTrackerView();
            new CustomerPaymentTrackerController(view, customerId);
        });

        view.getOrdersButton().addActionListener(e -> {
            view.getFrame().dispose();
            CustomerDeliveryTrackerView trackerView = new CustomerDeliveryTrackerView();
            new CustomerDeliveryTrackerController(trackerView, customerId);
        });

        view.getProfileButton().addActionListener(e -> {
            view.getFrame().dispose();
            CustomerSettingsView settings = new CustomerSettingsView();
            new CustomerSettingsController(settings, customerId);
        });

        view.getLogoutButton().addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    view.getFrame(),
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                view.getFrame().dispose();
                LandingPageView landing = new LandingPageView();
                new LandingPageController(landing);
            }
        });
    }

    private String formatDateTime(Timestamp ts) {
        return ts.toLocalDateTime().minusHours(8).toString().replace("T", " ");
    }
}