import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class AdminACityGroupUpdateController {
    private AdminACityGroupUpdateView view;
    private int groupId;
    private int adminId;
    private CityGroup currentGroup;

    public AdminACityGroupUpdateController(AdminACityGroupUpdateView view, int groupId, int adminId) {
        this.view = view;
        this.groupId = groupId;
        this.adminId = adminId;
        
        initializeController();
        loadGroupData();
    }

    private void initializeController() {
        view.getUpdateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCityGroup();
            }
        });

        view.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnToCityGroupsView();
            }
        });
    }

    private void loadGroupData() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM City_Delivery_Groups WHERE city_delivery_group_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, groupId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                currentGroup = new CityGroup(
                    rs.getInt("city_delivery_group_id"),
                    rs.getDouble("city_delivery_fee"),
                    rs.getInt("city_delivery_time_minutes")
                );
                view.setGroupData(currentGroup);
            } else {
                view.showErrorMessage("City group not found!");
                returnToCityGroupsView();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            view.showErrorMessage("Error loading city group data: " + e.getMessage());
        }
    }

    private void updateCityGroup() {
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

            // Update in database
            if (updateCityGroupInDatabase(fee, time)) {
                view.showSuccessMessage();
                returnToCityGroupsView();
            } else {
                view.showErrorMessage("Failed to update city group. Please try again.");
            }

        } catch (NumberFormatException e) {
            view.showErrorMessage("Please enter valid numbers for fee and time.");
        } catch (SQLException e) {
            e.printStackTrace();
            view.showErrorMessage("Database error: " + e.getMessage());
        }
    }

    private boolean updateCityGroupInDatabase(double fee, int time) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE City_Delivery_Groups SET city_delivery_fee = ?, city_delivery_time_minutes = ? WHERE city_delivery_group_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, fee);
            ps.setInt(2, time);
            ps.setInt(3, groupId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    private void returnToCityGroupsView() {
        view.getFrame().dispose();
        AdminACityGroupReadView groupView = new AdminACityGroupReadView();
        new AdminACityGroupReadController(groupView, adminId);
        groupView.setVisible(true);
    }
}