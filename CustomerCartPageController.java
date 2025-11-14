import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.time.LocalTime;

public class CustomerCartPageController {
    private CustomerCartPageView view;
    private int customerId;
    private Map<Integer, Integer> cartMap;
    private List<MenuProduct> products;
    private Connection conn;

    //Ang map is nimamatch nya ung menu Id sa quantity ng menu na pinili ng user using key avlue pairs
    //Ung mga inadd lang ni user sa cart ung nasa loob ng map
    //Sample: User added to cart 2 spaggetti (menu_id = 1), 1 bear brand (menu_id = 2)

    //Map<Integer, Integer> = {1 : 2, 2 : 1}
    //Pinapasa to sa cart controller and vice versa para maretain ung mga iandd to cart ni suer pag bumalik sa menu

    
    public CustomerCartPageController(CustomerCartPageView view, int customerId, Map<Integer, Integer> cartMap) {
        this.view = view;
        this.customerId = customerId;
        this.cartMap = cartMap;
        this.products = new ArrayList<>();

        loadCartProducts();

        view.getReturnButton().addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                view.getFrame().dispose();

                CustomerMenuPageView menuView = new CustomerMenuPageView();
                new CustomerMenuPageController(menuView, customerId, cartMap);
            }
        });

        view.getCheckOutButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                int orderID = saveOrder();
                int flag = saverOrderLines(orderID);

                if (orderID != -1 && flag != -1) {
                    JOptionPane.showMessageDialog(null, "Order placed successfully!");
                    view.getFrame().dispose();

                    // clear cart after successful order
                    cartMap.clear();

                    CustomerPaymentSessionView menuView = new CustomerPaymentSessionView();
                    new CustomerPaymentSessionController (menuView, customerId, orderID);
                } else {
                    JOptionPane.showMessageDialog(null, "Error placing order. Please try again.");
                }
            }
        });
    }

    private void loadCartProducts(){
        if (cartMap.isEmpty()){
            JOptionPane.showMessageDialog(null, "Your cart is empty!");
            return;
        }

        try{
            conn = DBConnection.getConnection();  //tatawgin DB app para maassign ung connection from app to MySQL to avriable conn

            String sql = "SELECT * FROM Menus WHERE menu_id IN (" +
                         String.join(",", Collections.nCopies(cartMap.size(), "?")) + ")";  //SQL statement, ung ? is the value to be passed
            PreparedStatement ps = conn.prepareStatement(sql);  //MySQL reads statement and returns the actual statement (the one w/o the ?)

            int index = 1;
            for (Integer id : cartMap.keySet()){
                ps.setInt(index++, id);
            }

            ResultSet rs = ps.executeQuery();  //Execute ung statement
            while (rs.next()){
                products.add(new MenuProduct(
                    rs.getInt("menu_id"),
                    rs.getString("menu_name"),
                    rs.getString("menu_description"),
                    rs.getDouble("unit_price"),
                    rs.getString("preparation_time"),
                    rs.getBoolean("is_available")
                ));
            }

            view.displayCartItems(products, cartMap);
            updateTotals();
        } 
        catch (SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading cart items: " + e.getMessage());
        }
    }

    private void updateTotals(){
        double totalCost = 0;
        int totalSeconds = 0;

        for (MenuProduct product : products){
            if (cartMap.containsKey(product.getId())){
                int qty = cartMap.get(product.getId());
                totalCost += product.getPrice() * qty;

                String[] parts = product.getPrepTime().split(":");
                int h = Integer.parseInt(parts[0]);
                int m = Integer.parseInt(parts[1]);
                int s = Integer.parseInt(parts[2]);
                totalSeconds += (h * 3600 + m * 60 + s) * qty;
            }
        }

    int h = totalSeconds / 3600;
    int m = (totalSeconds % 3600) / 60;
    int s = totalSeconds % 60;

    StringBuilder formatted = new StringBuilder();

    if (h > 0) 
    {
        formatted.append(h).append(" hr");
        
        if (h > 1) 
            formatted.append("s");
    }

    if (m > 0) 
    {
        if (formatted.length() > 0) {
            formatted.append(h > 0 && s == 0 ? " and " : ", ");
        }
            formatted.append(m).append(" min");
            
        if (m > 1) 
            formatted.append("s");
    }

        
    if (s > 0) 
    {
        if (formatted.length() > 0) {
            formatted.append(" and ");
        }
            formatted.append(s).append(" sec");
            
        if (s > 1) 
            formatted.append("s");
        }

        // handle the case where all are zero
        if (formatted.length() == 0) {
            formatted.append("0 secs");
        }

        view.getTotalCostLabel().setText("Total Price: ₱" + String.format("%.2f", totalCost));
        view.getTotalPrepTimeLabel().setText("Total Prep Time: " + formatted);
    }

    public int saveOrder() {
        double totalCost = 0;
        int totalSeconds = 0;

        // recompute totals for validity
        for (MenuProduct product : products) {
            int qty = cartMap.get(product.getId());
            totalCost += product.getPrice() * qty;

            String[] p = product.getPrepTime().split(":");
            int h = Integer.parseInt(p[0]);
            int m = Integer.parseInt(p[1]);
            int s = Integer.parseInt(p[2]);

            totalSeconds += (h * 3600 + m * 60 + s) * qty;
        }

        // convert prep to seconds with no TIMEZONE EFFECT
        LocalTime prepTime = LocalTime.ofSecondOfDay(totalSeconds);

        try (Connection conn = DBConnection.getConnection()) {

            // fetch delivery time based on customer's city
            String cityQuery =
                    "SELECT cdg.city_delivery_time_minutes " +
                    "FROM Customers c " +
                    "JOIN Cities ct ON c.city_id = ct.city_id " +
                    "JOIN City_Delivery_Groups cdg ON ct.city_delivery_group_id = cdg.city_delivery_group_id " +
                    "WHERE c.customer_id = ?";

            int deliveryMinutes = 0;

            try (PreparedStatement ps = conn.prepareStatement(cityQuery)) {
                ps.setInt(1, customerId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    deliveryMinutes = rs.getInt("city_delivery_time_minutes");
                }
            }

            // no timezone effect
            LocalTime deliveryTime = LocalTime.ofSecondOfDay(deliveryMinutes * 60);


            // INSERT enum Status declaration as pending here
            // insert into orders table
            String orderSql =
                    "INSERT INTO Orders (preparation_time, delivery_time, total_price, customer_id) " +
                    "VALUES (?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {

                pstmt.setString(1, prepTime.toString());        // correct: "HH:MM:SS"
                pstmt.setString(2, deliveryTime.toString());    // correct: "HH:MM:SS"
                pstmt.setDouble(3, totalCost);
                // enum status is 'Pending' by default parameter 4
                pstmt.setInt(4, customerId);

                int rows = pstmt.executeUpdate();

                if (rows > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            return rs.getInt(1);  // return new order_id
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saving order: " + e.getMessage());
        }

        return -1; // failed
    }



   public int saverOrderLines(int orderId) {
    String sql = "INSERT INTO Order_Lines (order_id, menu_id, menu_quantity, menu_price) " +
                 "VALUES (?, ?, ?, ?)";

    try (Connection conn = DBConnection.getConnection()) {

        for (MenuProduct product : products) {

            int qty = cartMap.get(product.getId());

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, orderId);
                pstmt.setInt(2, product.getId());
                pstmt.setInt(3, qty);
                pstmt.setDouble(4, product.getPrice());

                pstmt.executeUpdate();
            }
        }

        return 1; // success

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error saving order lines: " + e.getMessage());
        return -1;
    }
}

}
