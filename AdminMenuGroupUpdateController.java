// AdminMenuGroupUpdateController.java
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class AdminMenuGroupUpdateController {
    private AdminMenuGroupUpdateView view;
    private int categoryId;
    private int adminId;
    private MenuCategory currentCategory;

    public AdminMenuGroupUpdateController(AdminMenuGroupUpdateView view, int categoryId, int adminId) {
        this.view = view;
        this.categoryId = categoryId;
        this.adminId = adminId;
        
        initializeController();
        loadCategoryData();
    }

    private void initializeController() {
        view.getUpdateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateMenuCategory();
            }
        });

        view.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnToMenuCategoriesView();
            }
        });
    }

    private void loadCategoryData() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Menu_Category WHERE menu_category_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                currentCategory = new MenuCategory(
                    rs.getInt("menu_category_id"),
                    rs.getString("menu_category_name"),
                    rs.getTime("time_start"),
                    rs.getTime("time_end"),
                    rs.getBoolean("is_available")
                );
                view.setCategoryData(currentCategory);
                view.getFrame().setVisible(true);
            } else {
                view.showErrorMessage("Menu category not found!");
                returnToMenuCategoriesView();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            view.showErrorMessage("Error loading menu category data: " + e.getMessage());
        }
    }

    private void updateMenuCategory() {
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

        // Time validation - both are required
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

        // Update in database
        if (updateMenuCategoryInDatabase(categoryName, timeStart, timeEnd, isAvailable)) {
            view.showSuccessMessage();
            returnToMenuCategoriesView();
        } else {
            view.showErrorMessage("Failed to update menu category. Please try again.");
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

    private boolean updateMenuCategoryInDatabase(String categoryName, String timeStart, String timeEnd, boolean isAvailable) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE Menu_Category SET menu_category_name = ?, time_start = ?, time_end = ?, is_available = ? WHERE menu_category_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, categoryName);
            ps.setTime(2, Time.valueOf(timeStart));
            ps.setTime(3, Time.valueOf(timeEnd));
            ps.setBoolean(4, isAvailable);
            ps.setInt(5, categoryId);
            
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

    private void returnToMenuCategoriesView() {
        view.getFrame().dispose();
        AdminMenuGroupView categoryView = new AdminMenuGroupView();
        new AdminMenuGroupController(categoryView, adminId);
        categoryView.setVisible(true);
    }
}