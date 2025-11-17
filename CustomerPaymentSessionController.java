import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class CustomerPaymentSessionController {

    private CustomerPaymentSessionView view;
    private int customerId;
    private int orderId;

    public CustomerPaymentSessionController(CustomerPaymentSessionView view, int customerId, int orderId) {
        this.view = view;
        this.customerId = customerId;
        this.orderId = orderId;

        loadPaymentDetails();

        view.getPayNowButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handlePayment(true);
            }
        });

        view.getPayLaterButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handlePayment(false);
            }
        });
    }

    // LOAD TOTAL + DELIVERY FEE + CITY
    private void loadPaymentDetails() {
        String subtotalQuery =
        "SELECT SUM(menu_quantity * menu_price) AS subtotal " +
        "FROM Order_Lines " +
        "WHERE order_id = ?";


        String cityQuery =
            "SELECT ct.city_name, cdg.city_delivery_fee " +
            "FROM Customers c " +
            "LEFT JOIN Cities ct ON c.city_id = ct.city_id " +
            "LEFT JOIN City_Delivery_Groups cdg ON ct.city_delivery_group_id = cdg.city_delivery_group_id " +
            "WHERE c.customer_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps1 = conn.prepareStatement(subtotalQuery);
             PreparedStatement ps2 = conn.prepareStatement(cityQuery)) {

            // 1. Subtotal
            ps1.setInt(1, orderId);
            ResultSet rs1 = ps1.executeQuery(); 
            double subtotal = 0;
            if (rs1.next()) subtotal = rs1.getDouble("subtotal");

            // 2. City + Fee
            ps2.setInt(1, customerId);
            ResultSet rs2 = ps2.executeQuery();

            String cityName = "Unknown";
            double deliveryFee = 0;

            if (rs2.next()) {
                if (rs2.getString("city_name") != null)
                    cityName = rs2.getString("city_name");

                deliveryFee = rs2.getDouble("city_delivery_fee");
                
                    if (rs2.wasNull()) 
                    deliveryFee = 0;
            }

            // Total
            double total = subtotal + deliveryFee;

            // Update text
            view.getSubtotalValueLabel().setText(String.format("₱%.2f", subtotal));
            view.getDeliveryValueLabel().setText(String.format("₱%.2f", deliveryFee));
            view.getTotalValueLabel().setText(String.format("₱%.2f", total));

            // update delivery description label
            view.setDeliveryLabelText("Delivery Price (Manila to " + cityName + ")");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // PAYMENT HANDLER
    private void handlePayment(boolean isPayNow) {
        String amountText = view.getPaymentField().getText().trim();
        
        if (amountText.isEmpty() && isPayNow) {
            JOptionPane.showMessageDialog(null, "Please enter payment amount!");
            return;
        }

        double amountPaid = isPayNow ? Double.parseDouble(amountText) : 0;
        double subtotal = Double.parseDouble(view.getSubtotalValueLabel().getText().replace("₱", ""));
        double deliveryFee = Double.parseDouble(view.getDeliveryValueLabel().getText().replace("₱", ""));
        double total = subtotal + deliveryFee;

        if (isPayNow && amountPaid < total) {
            JOptionPane.showMessageDialog(null, "Insufficient amount!");
            return;
        }

        String ref = generateReference();

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // First, check if there's already a payment for this order
                String checkPaymentSQL = "SELECT payment_id FROM Payments WHERE order_id = ?";
                boolean paymentExists = false;
                int existingPaymentId = -1;
                
                try (PreparedStatement psCheck = conn.prepareStatement(checkPaymentSQL)) {
                    psCheck.setInt(1, orderId);
                    ResultSet rs = psCheck.executeQuery();
                    
                    if (rs.next()) {
                        paymentExists = true;
                        existingPaymentId = rs.getInt("payment_id");
                    }
                }

                if (paymentExists) {
                    // Payment already exists, update it with paid_date if paying now
                    String updateSQL;
                    if (isPayNow) {
                        updateSQL = "UPDATE Payments SET delivery_fee = ?, total_price = ?, " +
                                   "amount_paid = ?, reference_number = ?, is_paid = 1, " +
                                   "paid_date = NOW() WHERE order_id = ?";
                    } else {
                        updateSQL = "UPDATE Payments SET delivery_fee = ?, total_price = ?, " +
                                   "amount_paid = ?, reference_number = ?, is_paid = 0, " +
                                   "paid_date = NULL WHERE order_id = ?";
                    }
                    
                    try (PreparedStatement psUpdate = conn.prepareStatement(updateSQL)) {
                        psUpdate.setDouble(1, deliveryFee);
                        psUpdate.setDouble(2, subtotal + deliveryFee);
                        psUpdate.setDouble(3, amountPaid);
                        psUpdate.setString(4, ref);
                        psUpdate.setInt(5, orderId);
                        psUpdate.executeUpdate();
                    }
                } else {
                    // No payment exists, insert new one
                    String insertSQL;
                    if (isPayNow) {
                        insertSQL = "INSERT INTO Payments (delivery_fee, total_price, amount_paid, " +
                                   "reference_number, is_paid, paid_date, order_id, customer_id) " +
                                   "VALUES (?, ?, ?, ?, 1, NOW(), ?, ?)";
                    } else {
                        insertSQL = "INSERT INTO Payments (delivery_fee, total_price, amount_paid, " +
                                   "reference_number, is_paid, paid_date, order_id, customer_id) " +
                                   "VALUES (?, ?, ?, ?, 0, NULL, ?, ?)";
                    }
                    
                    try (PreparedStatement psInsert = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
                        psInsert.setDouble(1, deliveryFee);
                        psInsert.setDouble(2, subtotal + deliveryFee);
                        psInsert.setDouble(3, amountPaid);
                        psInsert.setString(4, ref);
                        psInsert.setInt(5, orderId);
                        psInsert.setInt(6, customerId);
                        psInsert.executeUpdate();
                    }
                }

                // Update order status if paying now
                if (isPayNow) {
                    String updateOrderSQL = "UPDATE Orders SET status = 'Preparing' WHERE order_id = ?";
                    try (PreparedStatement psOrder = conn.prepareStatement(updateOrderSQL)) {
                        psOrder.setInt(1, orderId);
                        psOrder.executeUpdate();
                    }
                }

                conn.commit();

                // Get the payment ID for receipt
                int paymentId = existingPaymentId;
                if (!paymentExists) {
                    try (PreparedStatement psGetId = conn.prepareStatement("SELECT payment_id FROM Payments WHERE order_id = ?")) {
                        psGetId.setInt(1, orderId);
                        ResultSet rs = psGetId.executeQuery();
                        if (rs.next()) {
                            paymentId = rs.getInt("payment_id");
                        }
                    }
                }

                if (isPayNow) {
                    JOptionPane.showMessageDialog(null, "Payment Successful!\nReference: " + ref);
                    view.getFrame().dispose();

                    // Go to receipt page
                    CustomerReceiptPageView receiptView = new CustomerReceiptPageView();
                    new CustomerReceiptPageController(receiptView, customerId, orderId, paymentId);

                } else {
                    JOptionPane.showMessageDialog(null, "Order saved. Pay Later selected.");
                    view.getFrame().dispose();

                    CustomerHomePageView homeView = new CustomerHomePageView();
                    new CustomerHomePageController(homeView, customerId);
                }

            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Payment failed: " + ex.getMessage());
        }
    }

    private String generateReference() {
        Random rand = new Random();
        String digits = "0123456789";
        StringBuilder code = new StringBuilder("KRP-");
        for (int i = 0; i < 6; i++) {
            code.append(digits.charAt(rand.nextInt(digits.length())));
        }
        return code.toString();
    }
}