// AdminViewProductsController.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.List;
import java.util.*;

public class AdminViewProductsController {
    private AdminViewProductsView view;
    private int adminId;
    private Integer categoryId; // Optional: filter by category
    private Connection conn;

    public AdminViewProductsController(AdminViewProductsView view, int adminId) {
        this(view, adminId, null);
    }

    public AdminViewProductsController(AdminViewProductsView view, int adminId, Integer categoryId) {
        this.view = view;
        this.adminId = adminId;
        this.categoryId = categoryId;

        // Update title if filtered by category
        if (categoryId != null) {
            updateTitleForCategory();
        }

        loadProducts();

        // Add Product button now opens the proper add product view
        view.getAddButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                // Open the dedicated Add Product view with category pre-selected if available
                openAddProductView();
            }
        });

        view.getBackButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                view.getFrame().dispose();
                if (categoryId != null) {
                    // Go back to category management
                    AdminMenuGroupView categoryView = new AdminMenuGroupView();
                    new AdminMenuGroupController(categoryView, adminId);
                } else {
                    // Go back to admin home
                    AdminHomePageView homePageView = new AdminHomePageView();
                    new AdminHomePageController(homePageView, adminId);
                }
            }
        });

        // Settings button listener
        view.getSettingsButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.getFrame().dispose();
                AdminSettingsView adminSettingsView = new AdminSettingsView();
                new AdminSettingsController(adminSettingsView, adminId);
            }
        });

        // Logout button listener
        view.getLogoutButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.getFrame().dispose();
                LandingPageView landingPageView = new LandingPageView();
                new LandingPageController(landingPageView);
            }
        });
    }

    private void updateTitleForCategory() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT menu_category_name FROM Menu_Category WHERE menu_category_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String categoryName = rs.getString("menu_category_name");
                view.getTitleLabel().setText("Product Management - " + categoryName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void openAddProductView() {
        // Close current view
        view.getFrame().dispose();
        
        // Open the dedicated Add Product view with category pre-selected if available
        AdminAddProductView addProductView = new AdminAddProductView();
        if (categoryId != null) {
            // Pass the category ID to the add product controller
            new AdminAddProductController(addProductView, adminId, categoryId);
        } else {
            new AdminAddProductController(addProductView, adminId);
        }
    }

    private void loadProducts(){
        List<MenuProduct> products = new ArrayList<>();
        try{
            conn = DBConnection.getConnection();
            
            String sql;
            if (categoryId != null) {
                sql = "SELECT * FROM Menus WHERE menu_category_id = ? ORDER BY menu_name";
            } else {
                sql = "SELECT * FROM Menus ORDER BY menu_name";
            }
            
            PreparedStatement ps = conn.prepareStatement(sql);
            if (categoryId != null) {
                ps.setInt(1, categoryId);
            }
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                products.add(new MenuProduct(
                    rs.getInt("menu_id"),
                    rs.getString("menu_name"),
                    rs.getString("menu_description"),
                    rs.getDouble("unit_price"),
                    rs.getString("preparation_time"),
                    rs.getBoolean("is_available")
                ));
            }

            view.displayProducts(products);

            // Add action listeners to dynamic buttons
            for (int i = 0; i < products.size(); i++){
                final MenuProduct product = products.get(i);

                // Edit button listener
                view.getProductButtons().get(i).addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e){
                        view.getFrame().dispose();
                        AdminUpdateProductView updateView = new AdminUpdateProductView();
                        new AdminUpdateProductController(updateView, adminId, product.getId());
                    }
                });

                // Availability toggle listener
                view.getAvailabilityLabels().get(i).addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e){
                        toggleAvailability(product);
                    }
                });
            }
        } 
        catch (SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading products: " + ex.getMessage());
        }
    }

    private void deleteProduct(int id){
        try{
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM Menus WHERE menu_id = ?");
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(null, "Product deleted successfully!");
                loadProducts();
            }
        } 
        catch (SQLException ex){
            JOptionPane.showMessageDialog(null, "Error deleting product: " + ex.getMessage());
        }
    }

    private void toggleAvailability(MenuProduct product){
        boolean newStatus = !product.isAvailable();
        try{
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE Menus SET is_available=? WHERE menu_id=?");
            ps.setInt(1, newStatus ? 1 : 0);
            ps.setInt(2, product.getId());
            ps.executeUpdate();

            product.setAvailable(newStatus);
            JOptionPane.showMessageDialog(null, "Product is now " + (newStatus ? "Available" : "Unavailable"));
            loadProducts();
        } 
        catch (SQLException ex){
            JOptionPane.showMessageDialog(null, "Error toggling availability: " + ex.getMessage());
        }
    }
}