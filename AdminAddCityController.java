import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.util.List;

public class AdminAddCityController {
    private AdminAddCityView view;
    private int adminId;
    private List<CityGroup> cityGroups;

    public AdminAddCityController(AdminAddCityView view, List<CityGroup> cityGroups, int adminId) {
        this.view = view;
        this.adminId = adminId;
        this.cityGroups = cityGroups;
        initializeController();
    }

    private void initializeController() {
        view.getAddButton().addActionListener(e -> {
            addCity();
        });

        view.getCancelButton().addActionListener(e -> {
            view.getFrame().dispose();
            // Return to main city group view
            AdminACityGroupReadView mainView = new AdminACityGroupReadView();
            new AdminACityGroupReadController(mainView, adminId);
            mainView.setVisible(true);
        });
    }

    private void addCity() {
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

        // Save to database
        try {
            if (saveCity(cityName, groupId)) {
                view.showSuccessMessage();
                view.clearFields();
                
                // Close and return to main city group view
                view.getFrame().dispose();
                AdminACityGroupReadView mainView = new AdminACityGroupReadView();
                new AdminACityGroupReadController(mainView, adminId);
                mainView.setVisible(true);
            } else {
                view.showErrorMessage("Failed to add city. The city name might already exist.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getErrorCode() == 1062) { // MySQL duplicate entry error code
                view.showErrorMessage("City name already exists. Please choose a different name.");
            } else {
                view.showErrorMessage("Database error: " + e.getMessage());
            }
        }
    }

    private boolean saveCity(String cityName, int groupId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO Cities (city_name, city_delivery_group_id) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, cityName);
            ps.setInt(2, groupId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }
}