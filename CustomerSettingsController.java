import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class CustomerSettingsController {
    private CustomerSettingsView customerSettingsView;
    private int customerId;

    public CustomerSettingsController(CustomerSettingsView view, int id){
        customerSettingsView = view;
        customerId = id;

        loadCustomerDetails();

        customerSettingsView.getConfirm().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                if (hasActiveOrders()) {
                    JOptionPane.showMessageDialog(customerSettingsView.getFrame(),
                            "Personal details cannot be updated while an order is being processed.",
                            "Update Disabled",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (!customerSettingsView.validateInputs()){
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

        customerSettingsView.getChangePassword().addActionListener(new ActionListener() {
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

        // Add deactivate button listener
        customerSettingsView.getDeactivateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                deactivateAccount();
            }
        });
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
                // Customer not found or already deactivated
                JOptionPane.showMessageDialog(customerSettingsView.getFrame(),
                        "Customer account not found or has been deactivated.",
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
        String firstName = customerSettingsView.getFirstName().trim();
        String lastName = customerSettingsView.getLastName().trim();
        String email = customerSettingsView.getEmail().trim();
        String address = customerSettingsView.getAddress().trim();

        // Extract city from address (3rd value after 2 commas)
        String[] parts = address.split(",");
        String cityName = parts.length >= 3 ? parts[2].trim() : "";

        // Get city_id from Cities table
        int cityId = getCityId(cityName);
        
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
                        "Failed to update details. Account may be deactivated.",
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

    private void deactivateAccount() {
        // Check for active orders first - if any exist, prevent deactivation
        if (hasActiveOrders()) {
            JOptionPane.showMessageDialog(customerSettingsView.getFrame(),
                    "Cannot deactivate account while orders are being processed.\n\n" +
                    "Please wait until your current orders are delivered or cancelled\n" +
                    "before deactivating your account.",
                    "Deactivation Disabled",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirm with user before deactivation
        int confirm = JOptionPane.showConfirmDialog(
            customerSettingsView.getFrame(),
            "Are you sure you want to deactivate your account?\n\n" +
            "This will:\n" +
            "• Set your email to null so it can be reused\n" +
            "• Make your account inactive\n\n" +
            "You will be logged out immediately.",
            "Confirm Account Deactivation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Perform deactivation
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Soft delete customer by setting is_active to 0 and email to null
            String sql = "UPDATE customers SET is_active = 0, email = NULL WHERE customer_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, customerId);
                int rowsUpdated = pstmt.executeUpdate();
                
                if (rowsUpdated > 0) {
                    conn.commit();
                    JOptionPane.showMessageDialog(customerSettingsView.getFrame(),
                            "Account deactivated successfully. Thank you for using our service.",
                            "Account Deactivated",
                            JOptionPane.INFORMATION_MESSAGE);
                    
                    // Log out and return to login page
                    openLoginPage();
                } else {
                    conn.rollback();
                    JOptionPane.showMessageDialog(customerSettingsView.getFrame(),
                            "Failed to deactivate account.",
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
                    "An error occurred while deactivating your account.",
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

    // Helper method to get city_id from Cities table
    private int getCityId(String cityName){
        int id = 0; // default in case city not found
        if (cityName.isEmpty()) return id;
        
        String sql = "SELECT city_id FROM Cities WHERE city_name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, cityName);
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