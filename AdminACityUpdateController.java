import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class AdminACityUpdateController {
    private AdminACityUpdateView view;
    private int cityId;
    private int adminId;
    private City currentCity;
    private List<CityGroup> cityGroups;

    public AdminACityUpdateController(AdminACityUpdateView view, int cityId, int adminId) {
        this.view = view;
        this.cityId = cityId;
        this.adminId = adminId;
        this.cityGroups = new ArrayList<>();
        
        initializeController();
        loadCityData();
    }

    private void initializeController() {
        view.getUpdateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCity();
            }
        });

        view.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnToCitiesView();
            }
        });

        view.getToggleAvailabilityButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleAvailability();
            }
        });
    }

    private void loadCityData() {
        try (Connection conn = DBConnection.getConnection()) {
            // First load all city groups for the dropdown
            loadAllCityGroups();
            
            // Then load the specific city data
            String sql = "SELECT * FROM Cities WHERE city_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, cityId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                currentCity = new City(
                    rs.getInt("city_id"),
                    rs.getString("city_name"),
                    rs.getInt("city_delivery_group_id"),
                    rs.getBoolean("is_available")
                );
                view.setCityData(currentCity, cityGroups);
            } else {
                view.showErrorMessage("City not found!");
                returnToCitiesView();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            view.showErrorMessage("Error loading city data: " + e.getMessage());
        }
    }

    private void loadAllCityGroups() {
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

        } catch (SQLException e) {
            e.printStackTrace();
            view.showErrorMessage("Error loading city groups: " + e.getMessage());
        }
    }

    private void updateCity() {
        String cityName = view.getCityName();
        int groupId = view.getSelectedGroupId();

        // Validation
        if (cityName.isEmpty()) {
            view.showErrorMessage("Please enter a city name.");
            return;
        }

        if (cityName.length() > 50) {
            view.showErrorMessage("City name must be 50 characters or less.");
            return;
        }

        // Check if city name already exists (excluding current city)
        if (isCityNameExists(cityName, cityId)) {
            view.showErrorMessage("City name already exists. Please choose a different name.");
            return;
        }

        // Check if selected group is available
        CityGroup selectedGroup = getCityGroupById(groupId);
        if (selectedGroup != null && !selectedGroup.isAvailable()) {
            view.showErrorMessage("Cannot assign city to an unavailable city group!");
            return;
        }

        // Update in database
        try {
            if (updateCityInDatabase(cityName, groupId, currentCity.isAvailable())) {
                view.showSuccessMessage("City details successfully updated!");
                loadCityData(); // Reload data to refresh view
            } else {
                view.showErrorMessage("Failed to update city. Please try again.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            view.showErrorMessage("Database error: " + e.getMessage());
        }
    }

    private void toggleAvailability() {
        // Check if city group is available
        CityGroup cityGroup = getCityGroupById(currentCity.getGroupId());
        if (cityGroup != null && !cityGroup.isAvailable()) {
            view.showErrorMessage("Cannot enable city because its city group is unavailable!");
            return;
        }

        boolean newAvailability = !currentCity.isAvailable();
        
        int confirm = JOptionPane.showConfirmDialog(
            view.getFrame(),
            "Are you sure you want to " + (newAvailability ? "enable" : "disable") + " this city?",
            "Confirm Availability Change",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (toggleCityAvailability(newAvailability)) {
                    view.showSuccessMessage("City " + (newAvailability ? "enabled" : "disabled") + " successfully!");
                    loadCityData(); // Reload data to refresh view
                } else {
                    view.showErrorMessage("Failed to update city availability.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                view.showErrorMessage("Database error: " + e.getMessage());
            }
        }
    }

    private boolean isCityNameExists(String cityName, int excludeCityId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) as count FROM Cities WHERE city_name = ? AND city_id != ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, cityName);
            ps.setInt(2, excludeCityId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private CityGroup getCityGroupById(int groupId) {
        for (CityGroup group : cityGroups) {
            if (group.getId() == groupId) {
                return group;
            }
        }
        return null;
    }

    private boolean updateCityInDatabase(String cityName, int groupId, boolean isAvailable) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE Cities SET city_name = ?, city_delivery_group_id = ?, is_available = ? WHERE city_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, cityName);
            ps.setInt(2, groupId);
            ps.setBoolean(3, isAvailable);
            ps.setInt(4, cityId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    private boolean toggleCityAvailability(boolean newAvailability) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE Cities SET is_available = ? WHERE city_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBoolean(1, newAvailability);
            ps.setInt(2, cityId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    private void returnToCitiesView() {
        view.getFrame().dispose();
        // Return to the cities list view for the current city's group
        if (currentCity != null) {
            AdminACityReadView cityView = new AdminACityReadView();
            new AdminACityReadController(cityView, currentCity.getGroupId(), cityGroups, adminId);
            cityView.setVisible(true);
        } else {
            // Fallback to city groups view if current city is not available
            AdminACityGroupReadView groupView = new AdminACityGroupReadView();
            new AdminACityGroupReadController(groupView, adminId);
        }
    }
}