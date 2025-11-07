import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

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

        view.getLogoutButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                view.getFrame().dispose();

                LandingPageView landingPageView = new LandingPageView();
                new LandingPageController(landingPageView);
            }
        });

        view.getSettingsButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                view.getFrame().dispose();

                CustomerSettingsView settingsView = new CustomerSettingsView();
                new CustomerSettingsController(settingsView, customerId);
            }
        });

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
                view.getFrame().dispose();

                CustomerCheckoutSessionView menuView = new CustomerCheckoutSessionView();
                //Tatawagin ung CustomerCheckoutSessionview para mareflect ung screen

                //new CustomerCheckoutSessionController(menuView, customerId);
                //Dapat ganyan parameters ng checkout session controller constructor, pacomplete nalang sa CustomerCheckoutController since sya ung logic
                //need kasi unf customerId sa paggawa ng orders and order lines
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
        String formatted = String.format("%02d:%02d:%02d", h, m, s);

        view.getTotalCostLabel().setText("Total Price: ₱" + String.format("%.2f", totalCost));
        view.getTotalPrepTimeLabel().setText("Total Prep Time: " + formatted);
    }
}
