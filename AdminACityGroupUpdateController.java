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

        view.getToggleAvailabilityButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleAvailability();
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
                    rs.getInt("city_delivery_time_minutes"),
                    rs.getBoolean("is_available")
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
            if (updateCityGroupInDatabase(fee, time, currentGroup.isAvailable())) {
                view.showSuccessMessage("City group details successfully updated!");
                loadGroupData(); // Reload data to refresh view
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

    private void toggleAvailability() {
        boolean newAvailability = !currentGroup.isAvailable();
        
        int confirm = JOptionPane.showConfirmDialog(
            view.getFrame(),
            "Are you sure you want to " + (newAvailability ? "enable" : "disable") + 
            " this city group? " + (newAvailability ? "" : 
            "\n\nWARNING: This will also disable all cities under this group!"),
            "Confirm Availability Change",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (toggleCityGroupAvailability(newAvailability)) {
                    // If disabling, also disable all cities in this group
                    if (!newAvailability) {
                        disableAllCitiesInGroup();
                    }
                    
                    view.showSuccessMessage("City group " + (newAvailability ? "enabled" : "disabled") + " successfully!");
                    loadGroupData(); // Reload data to refresh view
                } else {
                    view.showErrorMessage("Failed to update city group availability.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                view.showErrorMessage("Database error: " + e.getMessage());
            }
        }
    }

    private boolean updateCityGroupInDatabase(double fee, int time, boolean isAvailable) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE City_Delivery_Groups SET city_delivery_fee = ?, city_delivery_time_minutes = ?, is_available = ? WHERE city_delivery_group_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, fee);
            ps.setInt(2, time);
            ps.setBoolean(3, isAvailable);
            ps.setInt(4, groupId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    private boolean toggleCityGroupAvailability(boolean newAvailability) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE City_Delivery_Groups SET is_available = ? WHERE city_delivery_group_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBoolean(1, newAvailability);
            ps.setInt(2, groupId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    private void disableAllCitiesInGroup() throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE Cities SET is_available = 0 WHERE city_delivery_group_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, groupId);
            ps.executeUpdate();
        }
    }

    private void returnToCityGroupsView() {
        view.getFrame().dispose();
        AdminACityGroupReadView groupView = new AdminACityGroupReadView();
        new AdminACityGroupReadController(groupView, adminId);
        groupView.setVisible(true);
    }
}