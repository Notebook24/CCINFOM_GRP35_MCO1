// AdminMenuGroupController.java
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class AdminMenuGroupController {
    private AdminMenuGroupView view;
    private List<MenuCategory> menuCategories;
    private static AdminMenuGroupView currentInstance;
    private int adminId;

    public AdminMenuGroupController(AdminMenuGroupView view, int adminId) {
        this.view = view;
        this.adminId = adminId;
        this.menuCategories = new ArrayList<>();
        
        // Close previous instance if exists
        if (currentInstance != null && currentInstance != view) {
            currentInstance.getFrame().dispose();
        }
        currentInstance = view;
        
        initializeController();
        loadMenuCategories();
    }

    private void initializeController() {
        // Add button action
        view.getAddButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAddMenuCategoryView();
            }
        });

        // Back button action listener
        view.getBackButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBackToAdminHome();
            }
        });
    }

    private void loadMenuCategories() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Menu_Category ORDER BY menu_category_id";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            menuCategories.clear();
            while (rs.next()) {
                menuCategories.add(new MenuCategory(
                    rs.getInt("menu_category_id"),
                    rs.getString("menu_category_name"),
                    rs.getTime("time_start"),
                    rs.getTime("time_end"),
                    rs.getBoolean("is_available")
                ));
            }

            view.displayMenuCategories(menuCategories);
            
            // Set up listeners for dynamic buttons AFTER data is loaded and displayed
            setupDynamicButtonListeners();

        } catch (SQLException e) {
            e.printStackTrace();
            view.showErrorMessage("Error loading menu categories: " + e.getMessage());
        }
    }

    private void setupDynamicButtonListeners() {
        // Set up listeners for dynamic view buttons
        for (int i = 0; i < view.getViewButtons().size(); i++) {
            final int categoryIndex = i;
            view.getViewButtons().get(i).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleViewMenus(categoryIndex);
                }
            });
        }

        // Set up listeners for dynamic update buttons
        for (int i = 0; i < view.getUpdateButtons().size(); i++) {
            final int categoryIndex = i;
            view.getUpdateButtons().get(i).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleUpdateMenuCategory(categoryIndex);
                }
            });
        }

        // Set up listeners for dynamic delete buttons
        for (int i = 0; i < view.getDeleteButtons().size(); i++) {
            final int categoryIndex = i;
            view.getDeleteButtons().get(i).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleDeleteMenuCategory(categoryIndex);
                }
            });
        }
    }

    private void goBackToAdminHome() {
        view.getFrame().dispose();
        AdminHomePageView homePageView = new AdminHomePageView();
        new AdminHomePageController(homePageView, adminId);
    }

    private void openAddMenuCategoryView() {
        view.getFrame().dispose();
        AdminMenuGroupAddView addView = new AdminMenuGroupAddView();
        new AdminMenuGroupAddController(addView, adminId);
        addView.getFrame().setVisible(true);
    }

    private void handleViewMenus(int categoryIndex) {
        if (categoryIndex >= 0 && categoryIndex < menuCategories.size()) {
            MenuCategory category = menuCategories.get(categoryIndex);
            
            // Close current category view and open the products view filtered by this category
            view.getFrame().dispose();
            AdminViewProductsView productsView = new AdminViewProductsView();
            new AdminViewProductsController(productsView, adminId, category.getCategoryId());
        }
    }

    private void handleUpdateMenuCategory(int categoryIndex) {
        if (categoryIndex >= 0 && categoryIndex < menuCategories.size()) {
            MenuCategory category = menuCategories.get(categoryIndex);
            view.getFrame().dispose();
            AdminMenuGroupUpdateView updateView = new AdminMenuGroupUpdateView();
            new AdminMenuGroupUpdateController(updateView, category.getCategoryId(), adminId);
        }
    }

    private void handleDeleteMenuCategory(int categoryIndex) {
        if (categoryIndex >= 0 && categoryIndex < menuCategories.size()) {
            MenuCategory categoryToDelete = menuCategories.get(categoryIndex);
            
            int response = view.showDeleteConfirmation(categoryToDelete.toString());
            
            if (response == JOptionPane.YES_OPTION) {
                if (deleteMenuCategory(categoryToDelete.getCategoryId())) {
                    view.showSuccessMessage("Menu category deleted successfully!");
                    refreshMenuCategories();
                } else {
                    view.showErrorMessage("Error deleting menu category. It may have menus associated with it.");
                }
            }
        }
    }

    private boolean deleteMenuCategory(int categoryId) {
        try (Connection conn = DBConnection.getConnection()) {
            // First check if there are any menus associated with this category
            String checkSql = "SELECT COUNT(*) as menu_count FROM Menus WHERE menu_category_id = ?";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setInt(1, categoryId);
            ResultSet rs = checkPs.executeQuery();
            
            if (rs.next() && rs.getInt("menu_count") > 0) {
                int confirm = JOptionPane.showConfirmDialog(
                    view.getFrame(),
                    "This category has " + rs.getInt("menu_count") + " menu(s) associated with it. " +
                    "Deleting this category will also delete all associated menus.\n\n" +
                    "Are you sure you want to continue?",
                    "Confirm Category Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                
                if (confirm != JOptionPane.YES_OPTION) {
                    return false;
                }
            }
            
            // Delete the category (cascade delete will handle associated menus)
            String deleteSql = "DELETE FROM Menu_Category WHERE menu_category_id = ?";
            PreparedStatement deletePs = conn.prepareStatement(deleteSql);
            deletePs.setInt(1, categoryId);
            
            int rowsAffected = deletePs.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getSQLState() != null && e.getSQLState().equals("23000")) { // Foreign key constraint violation
                view.showErrorMessage("Cannot delete category. There are menus associated with this category.");
            } else {
                view.showErrorMessage("Database error: " + e.getMessage());
            }
            return false;
        }
    }

    public void refreshMenuCategories() {
        loadMenuCategories();
    }
    
    public static void openMenuCategoryView(int adminId) {
        // Close existing instance if any
        if (currentInstance != null) {
            currentInstance.getFrame().dispose();
            currentInstance = null;
        }
        
        // Create new instance
        AdminMenuGroupView view = new AdminMenuGroupView();
        new AdminMenuGroupController(view, adminId);
        view.setVisible(true);
    }
}