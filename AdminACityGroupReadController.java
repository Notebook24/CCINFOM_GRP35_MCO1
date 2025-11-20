// AdminACityGroupReadController.java
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class AdminACityGroupReadController {
    private AdminACityGroupReadView view;
    private List<CityGroup> cityGroups;
    private static AdminACityGroupReadView currentInstance; // Track current instance
    private int adminId; // ADDED adminId to track the admin

    // UPDATED constructor to accept adminId
    public AdminACityGroupReadController(AdminACityGroupReadView view, int adminId) {
        this.view = view;
        this.adminId = adminId; // STORE adminId
        this.cityGroups = new ArrayList<>();
        
        // Close previous instance if exists
        if (currentInstance != null && currentInstance != view) {
            currentInstance.getFrame().dispose();
        }
        currentInstance = view;
        
        initializeController();
        loadCityGroups();
    }

    private void initializeController() {
        // Add button action
        view.getAddButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAddCityGroupView();
            }
        });

        // ADDED Back button action listener
        view.getBackButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBackToAdminHome();
            }
        });
    }

    private void loadCityGroups() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM City_Delivery_Groups ORDER BY city_delivery_group_id";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            cityGroups.clear();
            while (rs.next()) {
                cityGroups.add(new CityGroup(
                    rs.getInt("city_delivery_group_id"),
                    rs.getDouble("city_delivery_fee"),
                    rs.getInt("city_delivery_time_minutes"),
                    rs.getBoolean("is_available")
                ));
            }

            view.displayCityGroups(cityGroups);
            
            // Set up listeners for dynamic buttons AFTER data is loaded and displayed
            setupDynamicButtonListeners();

        } catch (SQLException e) {
            e.printStackTrace();
            view.showErrorMessage("Error loading city groups: " + e.getMessage());
        }
    }

    private void setupDynamicButtonListeners() {
        // Set up listeners for dynamic view buttons
        for (int i = 0; i < view.getViewButtons().size(); i++) {
            final int groupIndex = i;
            view.getViewButtons().get(i).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleViewCities(groupIndex);
                }
            });
        }

        // Set up listeners for dynamic update buttons
        for (int i = 0; i < view.getUpdateButtons().size(); i++) {
            final int groupIndex = i;
            view.getUpdateButtons().get(i).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleUpdateCityGroup(groupIndex);
                }
            });
        }
    }

    // ADDED method to handle back navigation
    private void goBackToAdminHome() {
        view.getFrame().dispose();
        AdminHomePageView homePageView = new AdminHomePageView();
        new AdminHomePageController(homePageView, adminId);
    }

    private void openAddCityGroupView() {
        view.getFrame().dispose();
        AdminAddCityGroupView addView = new AdminAddCityGroupView();
        new AdminAddCityGroupController(addView, adminId);
        addView.getFrame().setVisible(true);
    }

    private void handleViewCities(int groupIndex) {
        if (groupIndex >= 0 && groupIndex < cityGroups.size()) {
            CityGroup group = cityGroups.get(groupIndex);
            view.getFrame().dispose();
            AdminACityReadView cityView = new AdminACityReadView();
            new AdminACityReadController(cityView, group.getId(), cityGroups, adminId);
            cityView.setVisible(true);
        }
    }

    private void handleUpdateCityGroup(int groupIndex) {
        if (groupIndex >= 0 && groupIndex < cityGroups.size()) {
            CityGroup group = cityGroups.get(groupIndex);
            view.getFrame().dispose();
            AdminACityGroupUpdateView updateView = new AdminACityGroupUpdateView();
            new AdminACityGroupUpdateController(updateView, group.getId(), adminId);
            updateView.getFrame().setVisible(true);
        }
    }

    public void refreshCityGroups() {
        loadCityGroups();
    }
    
    // UPDATED static method to open the view (ensures single instance)
    public static void openCityGroupView(int adminId) {
        // Close existing instance if any
        if (currentInstance != null) {
            currentInstance.getFrame().dispose();
            currentInstance = null;
        }
        
        // Create new instance
        AdminACityGroupReadView view = new AdminACityGroupReadView();
        new AdminACityGroupReadController(view, adminId); // UPDATED to pass adminId
        view.setVisible(true);
    }
}