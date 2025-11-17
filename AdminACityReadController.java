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
        // Add button action
        view.getAddButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.getFrame().dispose();
                openAddCityView();
            }
        });

        // Back button action
        view.getBackButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.getFrame().dispose();
                AdminACityGroupReadView groupView = new AdminACityGroupReadView();
                new AdminACityGroupReadController(groupView, adminId);
            }
        });

        // Set up update button listeners
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

        // Set up delete button listeners
        for (int i = 0; i < view.getDeleteButtons().size(); i++) {
            final int cityIndex = i;
            view.getDeleteButtons().get(i).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    view.getFrame().dispose();
                    handleDeleteCity(cityIndex);
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
                    groupRs.getInt("city_delivery_time_minutes")
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
                    rs.getInt("city_delivery_group_id")
                ));
            }

            String groupInfo = String.format("Group %d (₱%.2f, %d mins)", 
                currentGroup.getId(), currentGroup.getDeliveryFee(), currentGroup.getDeliveryTime());
            view.displayCities(cities, groupInfo);
            
            // Re-initialize controller to set up listeners for new buttons
            initializeController();

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
        new AdminAddCityController(addView, groupId); // Pass current group ID
        addView.getFrame().setVisible(true);
    }

    private void handleUpdateCity(int cityIndex) {
        if (cityIndex >= 0 && cityIndex < cities.size()) {
            City city = cities.get(cityIndex);
            view.getFrame().dispose();
            // Open city update view (you'll need to create this)
            AdminACityUpdateView updateView = new AdminACityUpdateView(cityGroups);
            new AdminACityUpdateController(updateView, city.getId(), adminId);
            updateView.getFrame().setVisible(true);
        }
    }

    private void handleDeleteCity(int cityIndex) {
        if (cityIndex >= 0 && cityIndex < cities.size()) {
            City cityToDelete = cities.get(cityIndex);
            
            int response = view.showDeleteConfirmation(cityToDelete.getName());
            
            if (response == JOptionPane.YES_OPTION) {
                if (deleteCity(cityToDelete.getId())) {
                    view.showSuccessMessage("City deleted successfully!");
                    refreshCities();
                } else {
                    view.showErrorMessage("Error deleting city. It may have customers associated with it.");
                }
            }
        }
    }

    private boolean deleteCity(int cityId) {
        try (Connection conn = DBConnection.getConnection()) {
            // Check if there are customers in this city
            String checkSql = "SELECT COUNT(*) as customer_count FROM Customers WHERE city_id = ?";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setInt(1, cityId);
            ResultSet rs = checkPs.executeQuery();
            
            if (rs.next() && rs.getInt("customer_count") > 0) {
                // Customers will have their city_id set to NULL due to ON DELETE SET NULL
                // We can proceed with deletion
            }

            // Delete the city
            String deleteSql = "DELETE FROM Cities WHERE city_id = ?";
            PreparedStatement deletePs = conn.prepareStatement(deleteSql);
            deletePs.setInt(1, cityId);
            
            int rowsAffected = deletePs.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
                    rs.getInt("city_delivery_time_minutes")
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