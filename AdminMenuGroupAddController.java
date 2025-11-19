// AdminMenuGroupAddController.java
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class AdminMenuGroupAddController {
    private AdminMenuGroupAddView view;
    private int adminId;

    public AdminMenuGroupAddController(AdminMenuGroupAddView view, int adminId) {
        this.view = view;
        this.adminId = adminId;
        initializeController();
    }

    private void initializeController() {
        view.getAddButton().addActionListener(e -> {
            addMenuCategory();
        });

        view.getCancelButton().addActionListener(e -> {
            view.getFrame().dispose();
            AdminMenuGroupView mainView = new AdminMenuGroupView();
            new AdminMenuGroupController(mainView, adminId);
        });
    }

    private void addMenuCategory() {
        String categoryName = view.getCategoryName();
        String timeStart = view.getTimeStart();
        String timeEnd = view.getTimeEnd();
        boolean isAvailable = view.isAvailable();

        // Validation
        if (categoryName.isEmpty()) {
            view.showErrorMessage("Please enter a category name.");
            return;
        }

        if (categoryName.length() > 50) {
            view.showErrorMessage("Category name cannot exceed 50 characters.");
            return;
        }

        // Time validation - both are required now
        if (timeStart.isEmpty() || timeEnd.isEmpty()) {
            view.showErrorMessage("Both Time Start and Time End are required.");
            return;
        }

        // Validate time format
        if (!isValidTimeFormat(timeStart) || !isValidTimeFormat(timeEnd)) {
            view.showErrorMessage("Invalid time format. Please use HH:MM:SS format (24-hour).");
            return;
        }

        // Validate time order (time_start < time_end)
        try {
            Time startTime = Time.valueOf(timeStart);
            Time endTime = Time.valueOf(timeEnd);
            if (!startTime.before(endTime)) {
                view.showErrorMessage("Time Start must be before Time End.");
                return;
            }
        } catch (IllegalArgumentException e) {
            view.showErrorMessage("Invalid time values.");
            return;
        }

        // Save to database
        if (saveMenuCategory(categoryName, timeStart, timeEnd, isAvailable)) {
            view.showSuccessMessage();
            view.clearFields();
            
            // Close and return to main view
            view.getFrame().dispose();
            AdminMenuGroupView mainView = new AdminMenuGroupView();
            new AdminMenuGroupController(mainView, adminId);
        } else {
            view.showErrorMessage("Failed to add menu category. Please try again.");
        }
    }

    private boolean isValidTimeFormat(String time) {
        if (time == null || time.isEmpty()) return false;
        
        try {
            String[] parts = time.split(":");
            if (parts.length != 3) return false;
            
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            int seconds = Integer.parseInt(parts[2]);
            
            return hours >= 0 && hours <= 23 && 
                   minutes >= 0 && minutes <= 59 && 
                   seconds >= 0 && seconds <= 59;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean saveMenuCategory(String categoryName, String timeStart, String timeEnd, boolean isAvailable) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO Menu_Category (menu_category_name, time_start, time_end, is_available) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, categoryName);
            ps.setTime(2, Time.valueOf(timeStart));
            ps.setTime(3, Time.valueOf(timeEnd));
            ps.setBoolean(4, isAvailable);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getSQLState() != null && e.getSQLState().equals("45000")) {
                view.showErrorMessage("Time Start must be before Time End.");
            } else {
                view.showErrorMessage("Database error: " + e.getMessage());
            }
            return false;
        } catch (IllegalArgumentException e) {
            view.showErrorMessage("Invalid time format. Please use HH:MM:SS format.");
            return false;
        }
    }
}