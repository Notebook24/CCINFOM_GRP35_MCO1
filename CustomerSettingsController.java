import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class CustomerSettingsController {
    private CustomerSettingsView customerSettingsView;
    private int customerId;
    private List<String> validCities = new ArrayList<>();

    public CustomerSettingsController(CustomerSettingsView view, int id){
        customerSettingsView = view;
        customerId = id;

        loadValidCities(); // Load cities when controller is created
        loadCustomerDetails();
        setupNavigation();
        setupActionListeners();
    }

    private void loadValidCities() {
        validCities.clear();
        String sql = "SELECT city_name FROM Cities WHERE is_available = 1 ORDER BY city_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String city = rs.getString("city_name");
                if (city != null && !city.trim().isEmpty()) {
                    validCities.add(city.trim());
                }
            }
            // Set the valid cities in the view
            customerSettingsView.setValidCities(validCities);
        } catch (SQLException ex) {
            System.err.println("Warning: unable to load cities: " + ex.getMessage());
            JOptionPane.showMessageDialog(customerSettingsView.getFrame(),
                    "Warning: Unable to load city data. Address validation may not work properly.",
                    "Database Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void setupNavigation() {
        // Home button - go to home page
        customerSettingsView.getHomeButton().addActionListener(e -> {
            customerSettingsView.getFrame().dispose();
            CustomerHomePageView homePageView = new CustomerHomePageView();
            new CustomerHomePageController(homePageView, customerId);
        });

        // Payments button - go to payment tracker
        customerSettingsView.getPaymentsButton().addActionListener(e -> {
            customerSettingsView.getFrame().dispose();
            CustomerPaymentTrackerView paymentView = new CustomerPaymentTrackerView();
            new CustomerPaymentTrackerController(paymentView, customerId);
        });

        // Orders button - go to order tracking page
        customerSettingsView.getOrdersButton().addActionListener(e -> {
            customerSettingsView.getFrame().dispose();
            CustomerDeliveryTrackerView trackerView = new CustomerDeliveryTrackerView();
            new CustomerDeliveryTrackerController(trackerView, customerId);
        });

        // Profile button - refresh current page (already on profile)
        customerSettingsView.getProfileButton().addActionListener(e -> {
            customerSettingsView.getFrame().dispose();
            CustomerSettingsView settingsView = new CustomerSettingsView();
            new CustomerSettingsController(settingsView, customerId);
        });

        // Logout button - confirm and go to landing page
        customerSettingsView.getLogoutButton().addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                customerSettingsView.getFrame(), 
                "Are you sure you want to logout?", 
                "Confirm Logout", 
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                customerSettingsView.getFrame().dispose();
                LandingPageView landingPageView = new LandingPageView();
                new LandingPageController(landingPageView);
            }
        });
    }

    private void setupActionListeners() {
        customerSettingsView.getConfirmButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                if (hasActiveOrders()) {
                    JOptionPane.showMessageDialog(customerSettingsView.getFrame(),
                            "Personal details cannot be updated while an order is being processed.",
                            "Update Disabled",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (!validateInputs()){
                    return;
                } else {
                    updateCustomerDetails();
                }
            }
        });

        customerSettingsView.getBackButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                openHomePage();
            }
        });

        customerSettingsView.getChangePasswordButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                if (hasActiveOrders()) {
                    JOptionPane.showMessageDialog(customerSettingsView.getFrame(),
                            "Password cannot be changed while an order is being processed.",
                            "Action Disabled",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                customerSettingsView.getFrame().dispose();
                
                CustomerChangePasswordView changePasswordView = new CustomerChangePasswordView();
                new CustomerChangePasswordController(changePasswordView, customerId);
            }
        });

        // Add delete button listener
        customerSettingsView.getDeleteButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                deleteAccount();
            }
        });
    }

    private boolean validateInputs() {
        // Get field values
        String firstName = customerSettingsView.getFirstNameField().getText().trim();
        String lastName = customerSettingsView.getLastNameField().getText().trim();
        String email = customerSettingsView.getEmailField().getText().trim();
        String address = customerSettingsView.getAddressField().getText().trim();

        // Check for empty fields
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(customerSettingsView.getFrame(),
                    "All fields are required.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(customerSettingsView.getFrame(),
                    "Please enter a valid email address.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate address format (should contain at least 2 commas for street, city, etc.)
        String[] addressParts = address.split(",");
        if (addressParts.length < 3) {
            JOptionPane.showMessageDialog(customerSettingsView.getFrame(),
                    "Please enter a complete address in format: Street, Barangay, City",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Extract and validate city - using same technique as sign up
        String cityName = addressParts[2].trim();
        if (!isValidCity(cityName)) {
            JOptionPane.showMessageDialog(customerSettingsView.getFrame(),
                    "City '" + cityName + "' is not serviced in our area.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private boolean isValidCity(String cityName) {
        // Use the same technique as sign up: remove "City" suffix and check against database
        String normalizedCity = cityName.replaceAll("(?i)\\s*city$", "").trim();
        
        String query = "SELECT COUNT(*) FROM Cities WHERE city_name LIKE ? AND is_available = 1";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, "%" + normalizedCity + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                customerSettingsView.getFrame(),
                "Error validating city. Please try again.",
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
        
        return false;
    }

    private void loadCustomerDetails(){
        String sql = "SELECT last_name, first_name, email, address FROM customers WHERE customer_id = ? AND is_active = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                customerSettingsView.getFirstNameField().setText(rs.getString("first_name"));
                customerSettingsView.getLastNameField().setText(rs.getString("last_name"));
                customerSettingsView.getEmailField().setText(rs.getString("email"));
                customerSettingsView.getAddressField().setText(rs.getString("address"));
            } else {
                // Customer not found or already deleted
                JOptionPane.showMessageDialog(customerSettingsView.getFrame(),
                        "Customer account not found or has been deleted.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                openLoginPage();
            }
        } 
        catch (SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(customerSettingsView.getFrame(),
                    "Failed to load your details.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCustomerDetails(){
        String firstName = customerSettingsView.getFirstNameField().getText().trim();
        String lastName = customerSettingsView.getLastNameField().getText().trim();
        String email = customerSettingsView.getEmailField().getText().trim();
        String address = customerSettingsView.getAddressField().getText().trim();

        // Extract city from address (3rd value after 2 commas) - same technique as sign up
        String[] parts = address.split(",");
        String cityName = parts.length >= 3 ? parts[2].trim() : "";
        
        // Normalize city name by removing "City" suffix
        String normalizedCity = cityName.replaceAll("(?i)\\s*city$", "").trim();

        // Get city_id from Cities table using same technique as sign up
        int cityId = getCityId(normalizedCity);
        
        String sql = "UPDATE customers SET first_name = ?, last_name = ?, email = ?, address = ?, city_id = ? WHERE customer_id = ? AND is_active = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, address);
            pstmt.setInt(5, cityId); // update city_id
            pstmt.setInt(6, customerId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0){
                JOptionPane.showMessageDialog(customerSettingsView.getFrame(),
                        "Details updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(customerSettingsView.getFrame(),
                        "Failed to update details. Account may be deleted.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } 
        catch (SQLException ex){
            if (ex.getMessage().contains("Duplicate entry")){
                JOptionPane.showMessageDialog(customerSettingsView.getFrame(),
                        "Email is already registered.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } 
            else{
                ex.printStackTrace();
                JOptionPane.showMessageDialog(customerSettingsView.getFrame(),
                        "Failed to update details.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteAccount() {
        // Check for active orders first - if any exist, prevent deletion
        if (hasActiveOrders()) {
            JOptionPane.showMessageDialog(customerSettingsView.getFrame(),
                    "Cannot delete account while orders are being processed.\n\n" +
                    "Please wait until your current orders are delivered or cancelled\n" +
                    "before deleting your account.",
                    "Deletion Disabled",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirm with user before deletion
        int confirm = JOptionPane.showConfirmDialog(
            customerSettingsView.getFrame(),
            "Are you sure you want to delete your account?\n\n" +
            "This will:\n" +
            "• Replace your email with a deleted identifier\n" +
            "• Make your account inactive\n\n" +
            "You will be logged out immediately.",
            "Confirm Account Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Perform deletion
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Get current email to format the deleted email
            String currentEmail = getCurrentEmail();
            String deletedEmail = formatDeletedEmail(currentEmail);

            // Soft delete customer by setting is_active to 0 and updating email
            String sql = "UPDATE customers SET is_active = 0, email = ? WHERE customer_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, deletedEmail);
                pstmt.setInt(2, customerId);
                int rowsUpdated = pstmt.executeUpdate();
                
                if (rowsUpdated > 0) {
                    conn.commit();
                    JOptionPane.showMessageDialog(customerSettingsView.getFrame(),
                            "Account deleted successfully. Thank you for using our service.",
                            "Account Deleted",
                            JOptionPane.INFORMATION_MESSAGE);
                    
                    // Log out and return to login page
                    openLoginPage();
                } else {
                    conn.rollback();
                    JOptionPane.showMessageDialog(customerSettingsView.getFrame(),
                            "Failed to delete account.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            ex.printStackTrace();
            JOptionPane.showMessageDialog(customerSettingsView.getFrame(),
                    "An error occurred while deleting your account.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private String getCurrentEmail() throws SQLException {
        String sql = "SELECT email FROM customers WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("email");
            }
        }
        return "";
    }

    private String formatDeletedEmail(String originalEmail) {
        // Extract the username part (before @)
        String username = originalEmail.split("@")[0];
        
        // Get current timestamp
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
        String timestamp = now.format(formatter);
        
        // Format: username_deleted_timestamp@gmail.com
        return username + "_deleted_" + timestamp + "@gmail.com";
    }

    // Helper method to get city_id from Cities table - using same technique as sign up
    private int getCityId(String cityName){
        int id = 0; // default in case city not found
        if (cityName.isEmpty()) return id;
        
        String sql = "SELECT city_id FROM Cities WHERE city_name LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, "%" + cityName + "%");
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                id = rs.getInt("city_id");
            }
        } catch (SQLException ex){
            ex.printStackTrace();
        }
        return id;
    }

    private boolean hasActiveOrders() {
        String sql = "SELECT COUNT(*) as active_orders FROM orders WHERE customer_id = ? AND status IN ('Pending', 'Preparing', 'In Transit')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int activeOrders = rs.getInt("active_orders");
                return activeOrders > 0;
            }
        } 
        catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(customerSettingsView.getFrame(),
                    "Error checking order status.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return true; // Return true on error to be safe
    }

    private void openHomePage(){
        customerSettingsView.getFrame().dispose();
        CustomerHomePageView homePageView = new CustomerHomePageView();
        new CustomerHomePageController(homePageView, customerId);
    }

    private void openLoginPage(){
        customerSettingsView.getFrame().dispose();
        CustomerLoginView loginView = new CustomerLoginView();
        new CustomerLoginController(loginView);
    }
}