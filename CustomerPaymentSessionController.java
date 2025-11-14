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

            String insertSQL =
                "INSERT INTO Payments (delivery_fee, total_price, amount_paid, reference_number, is_paid, order_id, customer_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
            ps.setDouble(1, deliveryFee);
            ps.setDouble(2, subtotal + deliveryFee);
            ps.setDouble(3, amountPaid);
            ps.setString(4, ref);
            ps.setInt(5, isPayNow ? 1 : 0);
            ps.setInt(6, orderId);
            ps.setInt(7, customerId);

            int rows = ps.executeUpdate();

            // Get generated payment ID
            int paymentId = -1;

            ResultSet keys = ps.getGeneratedKeys();

            if (keys.next()) 
                paymentId = keys.getInt(1);

            if (rows > 0) {
                if (isPayNow) {
                    JOptionPane.showMessageDialog(null, "Payment Successful!\nReference: " + ref);
                    view.getFrame().dispose();

                    CustomerReceiptPageView receiptView = new CustomerReceiptPageView();
                    new CustomerReceiptPageController(receiptView, customerId, orderId, paymentId);

                } else {
                    JOptionPane.showMessageDialog(null, "Order saved. Pay Later selected.");
                    view.getFrame().dispose();

                    CustomerHomePageView homeView = new CustomerHomePageView();
                    new CustomerHomePageController(homeView, customerId);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
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
