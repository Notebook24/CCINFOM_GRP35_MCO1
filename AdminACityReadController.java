import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class AdminACityReadController {
    private AdminACityReadView view;
    private int groupId;
    private List<City> cities;
    private List<CityGroup> cityGroups;
    private int adminId;
    private CityGroup currentGroup;
    private boolean controllersInitialized = false;

    public AdminACityReadController(AdminACityReadView view, int groupId, List<CityGroup> cityGroups, int adminId) {
        this.view = view;
        this.groupId = groupId;
        this.cities = new ArrayList<>();
        this.cityGroups = cityGroups;
        this.adminId = adminId;
        
        initializeController();
        loadCities();
    }

    private void initializeController() {
        // Only set up static listeners once
        if (!controllersInitialized) {
            // Add button action - SET UP ONLY ONCE
            view.getAddButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    view.getFrame().dispose();
                    openAddCityView();
                }
            });

            // Back button action - SET UP ONLY ONCE
            view.getBackButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    view.getFrame().dispose();
                    AdminACityGroupReadView groupView = new AdminACityGroupReadView();
                    new AdminACityGroupReadController(groupView, adminId);
                }
            });
            
            controllersInitialized = true;
        }
        
        // Set up update button listeners for current cities
        setupUpdateButtonListeners();
    }

    private void setupUpdateButtonListeners() {
        // Clear existing listeners from update buttons
        for (JButton button : view.getUpdateButtons()) {
            for (ActionListener al : button.getActionListeners()) {
                button.removeActionListener(al);
            }
        }
        
        // Add new listeners for current cities
        for (int i = 0; i < view.getUpdateButtons().size(); i++) {
            final int cityIndex = i;
            view.getUpdateButtons().get(i).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    view.getFrame().dispose();
                    handleUpdateCity(cityIndex);
                }
            });
        }
    }

    private void loadCities() {
        try (Connection conn = DBConnection.getConnection()) {
            // First, get group info for the title
            String groupSql = "SELECT * FROM City_Delivery_Groups WHERE city_delivery_group_id = ?";
            PreparedStatement groupPs = conn.prepareStatement(groupSql);
            groupPs.setInt(1, groupId);
            ResultSet groupRs = groupPs.executeQuery();
            
            if (groupRs.next()) {
                currentGroup = new CityGroup(
                    groupRs.getInt("city_delivery_group_id"),
                    groupRs.getDouble("city_delivery_fee"),
                    groupRs.getInt("city_delivery_time_minutes"),
                    groupRs.getBoolean("is_available")
                );
            }

            // Then, get cities for this group
            String citySql = "SELECT * FROM Cities WHERE city_delivery_group_id = ? ORDER BY city_name";
            PreparedStatement cityPs = conn.prepareStatement(citySql);
            cityPs.setInt(1, groupId);
            ResultSet rs = cityPs.executeQuery();

            cities.clear();
            while (rs.next()) {
                cities.add(new City(
                    rs.getInt("city_id"),
                    rs.getString("city_name"),
                    rs.getInt("city_delivery_group_id"),
                    rs.getBoolean("is_available")
                ));
            }

            String groupInfo = String.format("Group %d (₱%.2f, %d mins)", 
                currentGroup.getId(), currentGroup.getDeliveryFee(), currentGroup.getDeliveryTime());
            view.displayCities(cities, groupInfo);
            
            // Only set up update button listeners, NOT the main controller
            setupUpdateButtonListeners();

        } catch (SQLException e) {
            e.printStackTrace();
            view.showErrorMessage("Error loading cities: " + e.getMessage());
        }
    }

    private void openAddCityView() {
        // Get all available groups for the dropdown
        List<CityGroup> allGroups = getAllCityGroups();
        view.getFrame().dispose();
        AdminAddCityView addView = new AdminAddCityView(allGroups);
        new AdminAddCityController(addView, allGroups, adminId);
        addView.getFrame().setVisible(true);
    }

    private void handleUpdateCity(int cityIndex) {
        if (cityIndex >= 0 && cityIndex < cities.size()) {
            City city = cities.get(cityIndex);
            view.getFrame().dispose();
            // Open city update view
            AdminACityUpdateView updateView = new AdminACityUpdateView(cityGroups);
            new AdminACityUpdateController(updateView, city.getId(), adminId);
            updateView.getFrame().setVisible(true);
        }
    }

    private List<CityGroup> getAllCityGroups() {
        List<CityGroup> groups = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM City_Delivery_Groups ORDER BY city_delivery_group_id";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                groups.add(new CityGroup(
                    rs.getInt("city_delivery_group_id"),
                    rs.getDouble("city_delivery_fee"),
                    rs.getInt("city_delivery_time_minutes"),
                    rs.getBoolean("is_available")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    public void refreshCities() {
        loadCities();
    }
}