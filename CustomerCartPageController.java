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

    public CustomerCartPageController(CustomerCartPageView view, int customerId, Map<Integer, Integer> cartMap) {
        this.view = view;
        this.customerId = customerId;
        this.cartMap = cartMap;
        this.products = new ArrayList<>();

        loadCartProducts();

        view.getReturnButton().addActionListener(e -> {
            view.getFrame().dispose();
            CustomerMenuPageView menuView = new CustomerMenuPageView();
            new CustomerMenuPageController(menuView, customerId, cartMap);
        });

        view.getCheckOutButton().addActionListener(e -> {
            int orderID = saveOrder();
            int flag = saverOrderLines(orderID);

            if (orderID != -1 && flag != -1) {
                JOptionPane.showMessageDialog(null, "Order placed successfully!");
                view.getFrame().dispose();
                cartMap.clear();
                CustomerPaymentSessionView paymentView = new CustomerPaymentSessionView();
                new CustomerPaymentSessionController(paymentView, customerId, orderID);
            } else {
                JOptionPane.showMessageDialog(null, "Error placing order. Please try again.");
            }
        });
    }

    private void loadCartProducts() {
        if (cartMap.isEmpty()){
            JOptionPane.showMessageDialog(null, "Your cart is empty!");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Menus WHERE menu_id IN (" +
                         String.join(",", Collections.nCopies(cartMap.size(), "?")) + ")";
            PreparedStatement ps = conn.prepareStatement(sql);

            int index = 1;
            for (Integer id : cartMap.keySet()) ps.setInt(index++, id);

            ResultSet rs = ps.executeQuery();
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

        } catch (SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading cart items: " + e.getMessage());
        }
    }

    private void updateTotals() {
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

        // Format prep time
        int h = totalSeconds / 3600;
        int m = (totalSeconds % 3600) / 60;
        int s = totalSeconds % 60;

        StringBuilder formattedPrep = new StringBuilder();
        if (h > 0) formattedPrep.append(h).append(" hr").append(h>1?"s":"");
        if (m > 0) formattedPrep.append(formattedPrep.length()>0?", ":"").append(m).append(" min").append(m>1?"s":"");
        if (s > 0) formattedPrep.append(formattedPrep.length()>0?" and ":"").append(s).append(" sec").append(s>1?"s":"");
        if (formattedPrep.length()==0) formattedPrep.append("0 secs");

        view.getTotalCostLabel().setText("Total Price: ₱" + String.format("%.2f", totalCost));
        view.getTotalPrepTimeLabel().setText("Total Prep Time: " + formattedPrep);

        // Delivery Time
        int deliveryMinutes = getDeliveryTimeMinutes(customerId);
        view.getTotalDeliveryTimeLabel().setText("Delivery Time: " + deliveryMinutes + " min" + (deliveryMinutes>1?"s":""));
    }

    private int getDeliveryTimeMinutes(int customerId){
        int minutes = 0;
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT cdg.city_delivery_time_minutes " +
                         "FROM Customers c " +
                         "JOIN Cities ci ON c.city_id = ci.city_id " +
                         "JOIN City_Delivery_Groups cdg ON ci.city_delivery_group_id = cdg.city_delivery_group_id " +
                         "WHERE c.customer_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) minutes = rs.getInt("city_delivery_time_minutes");
        } catch (SQLException e){ e.printStackTrace(); }
        return minutes;
    }

    public int saveOrder() {
        double totalCost = 0;
        int totalSeconds = 0;

        for (MenuProduct product : products){
            int qty = cartMap.get(product.getId());
            totalCost += product.getPrice() * qty;
            String[] p = product.getPrepTime().split(":");
            int h = Integer.parseInt(p[0]);
            int m = Integer.parseInt(p[1]);
            int s = Integer.parseInt(p[2]);
            totalSeconds += (h*3600 + m*60 + s)*qty;
        }

        LocalTime prepTime = LocalTime.ofSecondOfDay(totalSeconds);
        int deliveryMinutes = getDeliveryTimeMinutes(customerId);
        LocalTime deliveryTime = LocalTime.ofSecondOfDay(deliveryMinutes*60);

        try (Connection conn = DBConnection.getConnection()){
            String orderSql = "INSERT INTO Orders (preparation_time, delivery_time, total_price, customer_id) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, prepTime.toString());
            pstmt.setString(2, deliveryTime.toString());
            pstmt.setDouble(3, totalCost);
            pstmt.setInt(4, customerId);

            int rows = pstmt.executeUpdate();
            if(rows>0){
                ResultSet rs = pstmt.getGeneratedKeys();
                if(rs.next()) return rs.getInt(1);
            }

        } catch(SQLException e){ e.printStackTrace(); JOptionPane.showMessageDialog(null,"Error saving order: "+e.getMessage()); }
        return -1;
    }

    public int saverOrderLines(int orderId){
        String sql = "INSERT INTO Order_Lines (order_id, menu_id, menu_quantity, menu_price) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            for(MenuProduct product : products){
                int qty = cartMap.get(product.getId());
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, orderId);
                pstmt.setInt(2, product.getId());
                pstmt.setInt(3, qty);
                pstmt.setDouble(4, product.getPrice());
                pstmt.executeUpdate();
            }
            return 1;
        } 
        catch(SQLException e){ 
            e.printStackTrace(); 
            JOptionPane.showMessageDialog(null,"Error saving order lines: "+e.getMessage()); 
            return -1; 
        }
    }
}
