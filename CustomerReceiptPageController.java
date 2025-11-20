import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class CustomerReceiptPageController {

    private CustomerReceiptPageView view;
    private int customerId;
    private int orderId;
    private int paymentId;

    public CustomerReceiptPageController(CustomerReceiptPageView view, int customerId, int orderId, int paymentId) {
        this.view = view;
        this.customerId = customerId;
        this.orderId = orderId;
        this.paymentId = paymentId;

        loadReceiptData();

        view.getHomeButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.getFrame().dispose();
                CustomerHomePageView homeView = new CustomerHomePageView();
                new CustomerHomePageController(homeView, customerId);
            }
        });

        view.getTrackButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.getFrame().dispose();
                CustomerDeliveryTrackerView trackerView = new CustomerDeliveryTrackerView();
                new CustomerDeliveryTrackerController(trackerView, customerId);
            }
        });
    }

    private void loadReceiptData() {
        try (Connection conn = DBConnection.getConnection()) {

            // First, check if order was cancelled and refunded
            String orderStatusQuery = "SELECT status FROM Orders WHERE order_id = ?";
            PreparedStatement psStatus = conn.prepareStatement(orderStatusQuery);
            psStatus.setInt(1, orderId);
            ResultSet rsStatus = psStatus.executeQuery();
            
            boolean isCancelled = false;
            boolean isRefunded = false;
            
            if (rsStatus.next()) {
                String status = rsStatus.getString("status");
                isCancelled = "Cancelled".equals(status);
            }

            // Check if payment was refunded
            String paymentStatusQuery = "SELECT is_paid FROM Payments WHERE order_id = ? ORDER BY payment_id DESC LIMIT 1";
            PreparedStatement psPayment = conn.prepareStatement(paymentStatusQuery);
            psPayment.setInt(1, orderId);
            ResultSet rsPayment = psPayment.executeQuery();
            
            if (rsPayment.next()) {
                isRefunded = rsPayment.getInt("is_paid") == 0;
            }

            // Clear existing receipt rows
            view.clearReceiptRows();

            String menuQuery =
                "SELECT m.menu_name, ol.menu_quantity, ol.menu_price " +
                "FROM Order_Lines ol " +
                "JOIN Menus m ON m.menu_id = ol.menu_id " +
                "WHERE ol.order_id = ?";

            PreparedStatement psMenu = conn.prepareStatement(menuQuery);
            psMenu.setInt(1, orderId);
            ResultSet rsMenu = psMenu.executeQuery();

            double subtotal = 0.0;

            while (rsMenu.next()) {
                String menuName = rsMenu.getString("menu_name");
                int qty = rsMenu.getInt("menu_quantity");
                double price = rsMenu.getDouble("menu_price");
                double lineTotal = qty * price;
                subtotal += lineTotal;
                view.addReceiptRow(menuName + " (x" + qty + ")", String.format("₱%.2f", lineTotal));
            }

            String payQuery =
                "SELECT delivery_fee, total_price, amount_paid, reference_number " +
                "FROM Payments WHERE payment_id = ?";

            PreparedStatement psPay = conn.prepareStatement(payQuery);
            psPay.setInt(1, paymentId);
            ResultSet rsPay = psPay.executeQuery();

            if (rsPay.next()) {
                double deliveryFee = rsPay.getDouble("delivery_fee");
                double totalPrice = rsPay.getDouble("total_price");
                double amountPaid = rsPay.getDouble("amount_paid");
                String ref = rsPay.getString("reference_number");

                view.setReferenceNumber(ref);
                view.addReceiptRow("Delivery Fee", String.format("₱%.2f", deliveryFee));
                view.addReceiptRow("Subtotal", String.format("₱%.2f", totalPrice));
                
                if (isCancelled && isRefunded) {
                    // Show refund information prominently
                    view.addReceiptRow("REFUNDED", String.format("-₱%.2f", totalPrice));
                    view.addReceiptRow("Final Amount", "₱0.00");
                    
                    // Add refund message banner
                    view.addRefundMessage();
                    
                    // Update window title to show refund status
                    view.getFrame().setTitle("Order Receipt - REFUNDED - Order #" + orderId);
                } else {
                    double change = amountPaid - totalPrice;
                    view.addReceiptRow("Amount Paid", String.format("₱%.2f", amountPaid));
                    view.addReceiptRow("Change", String.format("₱%.2f", change));
                    
                    // Update window title
                    view.getFrame().setTitle("Order Receipt - Order #" + orderId);
                }
            }

            // Add status information
            view.addReceiptRow("", ""); // Empty row for spacing
            view.addReceiptRow("Order Status:", isCancelled ? "CANCELLED & REFUNDED" : "ACTIVE");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view.getFrame(), "Error loading receipt data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // NEW METHOD: Refresh receipt data
    private void refreshReceiptData() {
        loadReceiptData();
        JOptionPane.showMessageDialog(view.getFrame(), "Receipt data refreshed!", "Refreshed", JOptionPane.INFORMATION_MESSAGE);
    }
}