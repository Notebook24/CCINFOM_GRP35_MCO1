// CustomerReceiptPageController.java
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

            String menuQuery =
                "SELECT m.menu_name, ol.menu_quantity, ol.menu_price " +
                "FROM Order_Lines ol " +
                "JOIN Menus m ON m.menu_id = ol.menu_id " +
                "WHERE ol.order_id = ?";

            PreparedStatement psMenu = conn.prepareStatement(menuQuery);
            psMenu.setInt(1, orderId);
            ResultSet rsMenu = psMenu.executeQuery();

            while (rsMenu.next()) {
                String menuName = rsMenu.getString("menu_name");
                int qty = rsMenu.getInt("menu_quantity");
                double price = rsMenu.getDouble("menu_price");
                double lineTotal = qty * price;
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
                double change = amountPaid - totalPrice;
                String ref = rsPay.getString("reference_number");

                view.setReferenceNumber(ref);
                view.addReceiptRow("Delivery Fee", String.format("₱%.2f", deliveryFee));
                view.addReceiptRow("Total", String.format("₱%.2f", totalPrice));
                view.addReceiptRow("Amount Paid", String.format("₱%.2f", amountPaid));
                view.addReceiptRow("Change", String.format("₱%.2f", change));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
