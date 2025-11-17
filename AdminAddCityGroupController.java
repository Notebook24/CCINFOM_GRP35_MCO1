// AdminAddCityGroupController.java
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class AdminAddCityGroupController {
    private AdminAddCityGroupView view;
    private int adminId;

    public AdminAddCityGroupController(AdminAddCityGroupView view, int adminId) {
        this.view = view;
        this.adminId = adminId;
        initializeController();
    }

    private void initializeController() {
        view.getAddButton().addActionListener(e -> {
            addCityGroup();
        });

        view.getCancelButton().addActionListener(e -> {
            view.getFrame().dispose();
            // Return to main city group view
            AdminACityGroupReadView mainView = new AdminACityGroupReadView();
            new AdminACityGroupReadController(mainView, adminId);
        });
    }

    private void addCityGroup() {
        String feeText = view.getFee();
        String timeText = view.getTime();

        // Validation
        if (feeText.isEmpty() || timeText.isEmpty()) {
            view.showErrorMessage("Please fill in all fields.");
            return;
        }

        try {
            double fee = Double.parseDouble(feeText);
            int time = Integer.parseInt(timeText);

            if (fee < 0) {
                view.showErrorMessage("Delivery fee cannot be negative.");
                return;
            }

            if (time <= 0) {
                view.showErrorMessage("Delivery time must be positive.");
                return;
            }

            // Save to database
            if (saveCityGroup(fee, time)) {
                view.showSuccessMessage();
                view.clearFields();
                
                // Close and return to main view
                view.getFrame().dispose();
                AdminACityGroupReadView mainView = new AdminACityGroupReadView();
                new AdminACityGroupReadController(mainView, adminId);
            } else {
                view.showErrorMessage("Failed to add city group. Please try again.");
            }

        } catch (NumberFormatException e) {
            view.showErrorMessage("Please enter valid numbers for fee and time.");
        } catch (SQLException e) {
            e.printStackTrace();
            view.showErrorMessage("Database error: " + e.getMessage());
        }
    }

    private boolean saveCityGroup(double fee, int time) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO City_Delivery_Groups (city_delivery_fee, city_delivery_time_minutes) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, fee);
            ps.setInt(2, time);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }
}