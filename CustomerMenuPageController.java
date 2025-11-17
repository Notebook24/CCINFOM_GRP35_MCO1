import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class CustomerMenuPageController {
    private CustomerMenuPageView view;
    private int customerId;
    private List<MenuProduct> products;
    private Connection conn;
    private Map<Integer, Integer> cartMap;

    public CustomerMenuPageController(CustomerMenuPageView view, int id){
        this(view, id, null);
    }

    public CustomerMenuPageController(CustomerMenuPageView view, int id, Map<Integer, Integer> cartMap){
        this.view = view;
        this.customerId = id;
        this.cartMap = (cartMap != null) ? cartMap : new HashMap<>();
        loadAvailableProducts();
        attachMenuListeners();
        setupNavigation(); // Add navigation setup
    }

    private void loadAvailableProducts(){
        products = new ArrayList<>();
        try{
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM Menus WHERE is_available = 1";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                MenuProduct product = new MenuProduct(
                    rs.getInt("menu_id"),
                    rs.getString("menu_name"),
                    rs.getString("menu_description"),
                    rs.getDouble("unit_price"),
                    rs.getString("preparation_time"),
                    rs.getBoolean("is_available"),
                    rs.getString("image") != null ? rs.getString("image") : ""
                );
                products.add(product);
            }

            view.displayProducts(products, cartMap);
            updateTotals();
        } 
        catch (SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading menu: " + e.getMessage());
        }
    }

    private void setupNavigation() {
        // Home button - go to home page
        view.getHomeButton().addActionListener(e -> {
            view.getFrame().dispose();
            CustomerHomePageView homeView = new CustomerHomePageView();
            new CustomerHomePageController(homeView, customerId);
        });

        // Payments button - show message (can be implemented later)
        view.getPaymentsButton().addActionListener(e -> {
            JOptionPane.showMessageDialog(view.getFrame(), 
                "Payments functionality coming soon!");
        });

        // Orders button - go to order tracking page
        view.getOrdersButton().addActionListener(e -> {
            view.getFrame().dispose();
            CustomerDeliveryTrackerView trackerView = new CustomerDeliveryTrackerView();
            new CustomerDeliveryTrackerController(trackerView, customerId);
        });

        // Profile button - go to settings page
        view.getProfileButton().addActionListener(e -> {
            view.getFrame().dispose();
            CustomerSettingsView settingsView = new CustomerSettingsView();
            new CustomerSettingsController(settingsView, customerId);
        });

        // Logout button - confirm and go to landing page
        view.getLogoutButton().addActionListener(e -> {
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

    private void attachMenuListeners() {
        // Checkout button
        view.getCheckoutButton().addActionListener(e -> {
            if (cartMap.isEmpty()) {
                JOptionPane.showMessageDialog(view.getFrame(), "Your cart is empty!");
                return;
            }
            view.getFrame().dispose();
            CustomerCartPageView cartPageView = new CustomerCartPageView();
            new CustomerCartPageController(cartPageView, customerId, cartMap);
        });

        // Cart buttons
        List<JButton> cartButtons = view.getCartButtons();
        List<JButton> plusButtons = view.getPlusButtons();
        List<JButton> minusButtons = view.getMinusButtons();
        List<JLabel> quantityLabels = view.getQuantityLabels();

        for (int i = 0; i < products.size(); i++) {
            final int index = i;
            MenuProduct product = products.get(i);
            JButton cartBtn = cartButtons.get(i);
            JButton plusBtn = plusButtons.get(i);
            JButton minusBtn = minusButtons.get(i);
            JLabel qtyLabel = quantityLabels.get(i);

            cartBtn.addActionListener(ev -> {
                if (cartMap.containsKey(product.getId())) {
                    cartMap.remove(product.getId());
                    view.updateCartItemState(index, 0, false);
                } else {
                    cartMap.put(product.getId(), 1);
                    view.updateCartItemState(index, 1, true);
                }
                updateTotals();
            });

            plusBtn.addActionListener(ev -> {
                if (cartMap.containsKey(product.getId())) {
                    int qty = cartMap.get(product.getId()) + 1;
                    cartMap.put(product.getId(), qty);
                    view.updateCartItemState(index, qty, true);
                    updateTotals();
                }
            });

            minusBtn.addActionListener(ev -> {
                if (cartMap.containsKey(product.getId())) {
                    int qty = cartMap.get(product.getId()) - 1;
                    if (qty <= 0) {
                        cartMap.remove(product.getId());
                        view.updateCartItemState(index, 0, false);
                    } else {
                        cartMap.put(product.getId(), qty);
                        view.updateCartItemState(index, qty, true);
                    }
                    updateTotals();
                }
            });
        }
    }

    private void updateTotals(){
        double totalCost = 0;
        int totalPrepSeconds = 0;

        for (MenuProduct product : products){
            if (cartMap.containsKey(product.getId())){
                int qty = cartMap.get(product.getId());
                totalCost += product.getPrice() * qty;

                String time = product.getPrepTime();
                String[] parts = time.split(":");
                int h = Integer.parseInt(parts[0]);
                int m = Integer.parseInt(parts[1]);
                int s = Integer.parseInt(parts[2]);
                totalPrepSeconds += (h * 3600 + m * 60 + s) * qty;
            }
        }

        int h = totalPrepSeconds / 3600;
        int m = (totalPrepSeconds % 3600) / 60;
        int s = totalPrepSeconds % 60;

        StringBuilder formattedPrep = new StringBuilder();
        if (h > 0) formattedPrep.append(h).append(" hr").append(h>1?"s":"");
        if (m > 0) {
            if (formattedPrep.length()>0) formattedPrep.append(h>0 && s==0?" and ":", ");
            formattedPrep.append(m).append(" min").append(m>1?"s":"");
        }
        if (s > 0) {
            if (formattedPrep.length()>0) formattedPrep.append(" and ");
            formattedPrep.append(s).append(" sec").append(s>1?"s":"");
        }
        if (formattedPrep.length()==0) formattedPrep.append("0 secs");

        view.getTotalCostLabel().setText("TOTAL COST: ₱" + String.format("%.2f", totalCost));
        view.getPrepTimeLabel().setText("PREP TIME: " + formattedPrep);
    }
}