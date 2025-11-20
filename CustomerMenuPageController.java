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
        setupNavigation();
    }

    private void loadAvailableProducts(){
        products = new ArrayList<>();
        try{
            conn = DBConnection.getConnection();
            
            // Get current time to check against category time ranges
            java.util.Date currentDate = new java.util.Date();
            Time currentTime = new Time(currentDate.getTime());
            
            // SQL query to get available menus with category information
            String sql = "SELECT m.*, mc.menu_category_id, mc.menu_category_name, mc.time_start, mc.time_end " +
                        "FROM Menus m " +
                        "LEFT JOIN Menu_Category mc ON m.menu_category_id = mc.menu_category_id " +
                        "WHERE m.is_available = 1 " +
                        "AND (mc.menu_category_id IS NULL OR mc.is_available = 1) " +
                        "AND ( " +
                        "    mc.menu_category_id IS NULL OR " + // If no category assigned, show menu
                        "    mc.time_start IS NULL OR " + // If category has no time restrictions, show menu
                        "    mc.time_end IS NULL OR " + // If category has no time restrictions, show menu
                        "    (? BETWEEN mc.time_start AND mc.time_end) " + // Current time within category time range
                        ") " +
                        "ORDER BY " +
                        "    CASE WHEN mc.menu_category_name IS NULL THEN 1 ELSE 0 END, " + // Uncategorized items first
                        "    mc.menu_category_name, " + // Then by category name
                        "    m.menu_name"; // Then by menu name within category
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setTime(1, currentTime);
            ResultSet rs = ps.executeQuery();

            // Group products by category for display
            Map<String, List<MenuProduct>> categorizedProducts = new LinkedHashMap<>();
            
            boolean hasProducts = false;
            while (rs.next()){
                hasProducts = true;
                // Get category name or use "Uncategorized" for items without category
                String categoryName = rs.getString("menu_category_name");
                if (categoryName == null || categoryName.trim().isEmpty()) {
                    categoryName = "Uncategorized";
                }
                
                // Get category ID (can be null)
                Integer categoryId = null;
                try {
                    categoryId = rs.getInt("menu_category_id");
                    if (rs.wasNull()) {
                        categoryId = null;
                    }
                } catch (SQLException e) {
                    categoryId = null;
                }
                
                // Create product using the 8-argument constructor
                MenuProduct product = new MenuProduct(
                    rs.getInt("menu_id"),
                    rs.getString("menu_name"),
                    rs.getString("menu_description"),
                    rs.getDouble("unit_price"),
                    rs.getString("preparation_time"),
                    rs.getBoolean("is_available"),
                    rs.getString("image"), // image path from database
                    categoryId
                );
                
                // Add to appropriate category list
                if (!categorizedProducts.containsKey(categoryName)) {
                    categorizedProducts.put(categoryName, new ArrayList<>());
                }
                categorizedProducts.get(categoryName).add(product);
                
                // Also add to flat list for cart operations
                products.add(product);
            }

            // FIX: Simple check - if no products found, show empty state
            if (!hasProducts) {
                // Create empty map to show no products message
                view.displayCategorizedProducts(new LinkedHashMap<>(), cartMap);
                updateTotals();
                return;
            }

            // Display categorized products using the updated view method
            view.displayCategorizedProducts(categorizedProducts, cartMap);
            updateTotals();
            
        } 
        catch (SQLException e){
            System.out.println();
        }
        catch (Exception e){
            System.out.println();
        }
        finally {
            // Close connection if needed
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println();
            }
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
            view.getFrame().dispose();
            CustomerPaymentTrackerView view = new CustomerPaymentTrackerView();
            new CustomerPaymentTrackerController(view, customerId);
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

        // Only attach listeners if we have products
        if (products == null || products.isEmpty()) {
            return;
        }

        // Cart buttons - using the existing view's list-based approach
        List<JButton> cartButtons = view.getCartButtons();
        List<JButton> plusButtons = view.getPlusButtons();
        List<JButton> minusButtons = view.getMinusButtons();
        List<JLabel> quantityLabels = view.getQuantityLabels();

        // FIX: Additional safety check for button lists
        if (cartButtons == null || cartButtons.size() != products.size()) {
            return;
        }

        for (int i = 0; i < products.size(); i++) {
            final int index = i;
            MenuProduct product = products.get(i);
            
            // FIX: Safety checks for button existence
            if (i >= cartButtons.size() || i >= plusButtons.size() || 
                i >= minusButtons.size() || i >= quantityLabels.size()) {
                continue;
            }
            
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

        // FIX: Safety check for products list
        if (products != null) {
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