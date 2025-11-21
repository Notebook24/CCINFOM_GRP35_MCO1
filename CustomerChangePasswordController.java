import java.awt.event.*;
import java.sql.*;
import javax.swing.JOptionPane;

public class CustomerChangePasswordController {
    private CustomerChangePasswordView changePasswordView;
    private int userId;

    public CustomerChangePasswordController(CustomerChangePasswordView view, int id){
        changePasswordView = view;
        userId = id;

        setupNavigation();
        setupActionListeners();
    }

    private void setupNavigation() {
        // Home button - go to home page
        changePasswordView.getHomeButton().addActionListener(e -> {
            changePasswordView.getFrame().dispose();
            CustomerHomePageView homePageView = new CustomerHomePageView();
            new CustomerHomePageController(homePageView, userId);
        });

        // Payments button - go to payment tracker
        changePasswordView.getPaymentsButton().addActionListener(e -> {
            changePasswordView.getFrame().dispose();
            CustomerPaymentTrackerView paymentView = new CustomerPaymentTrackerView();
            new CustomerPaymentTrackerController(paymentView, userId);
        });

        // Orders button - go to order tracking page
        changePasswordView.getOrdersButton().addActionListener(e -> {
            changePasswordView.getFrame().dispose();
            CustomerDeliveryTrackerView trackerView = new CustomerDeliveryTrackerView();
            new CustomerDeliveryTrackerController(trackerView, userId);
        });

        // Profile button - go to settings page
        changePasswordView.getProfileButton().addActionListener(e -> {
            changePasswordView.getFrame().dispose();
            CustomerSettingsView settingsView = new CustomerSettingsView();
            new CustomerSettingsController(settingsView, userId);
        });

        // Logout button - confirm and go to landing page
        changePasswordView.getLogoutButton().addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                changePasswordView.getFrame(), 
                "Are you sure you want to logout?", 
                "Confirm Logout", 
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                changePasswordView.getFrame().dispose();
                LandingPageView landingPageView = new LandingPageView();
                new LandingPageController(landingPageView);
            }
        });
    }

    private void setupActionListeners() {
        changePasswordView.getChangeButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!changePasswordView.validateInputs()) {
                    return;
                } else {
                    changePassword();
                }
            }
        });

        changePasswordView.getBackButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                openSettingsPage();
            }
        });
    }

    private void changePassword() {
        String oldPassword = changePasswordView.getOldPassword();
        String newPassword = changePasswordView.getNewPassword();

        String checkSql = "SELECT password FROM customers WHERE customer_id = ?";
        String updateSql = "UPDATE customers SET password = ? WHERE customer_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, userId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()){
                String currentPassword = rs.getString("password");

                if (!currentPassword.equals(oldPassword)){
                    changePasswordView.getWarningLabel().setText("Old password is incorrect.");
                    return;
                }

                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)){
                    updateStmt.setString(1, newPassword);
                    updateStmt.setInt(2, userId);
                    int rowsUpdated = updateStmt.executeUpdate();

                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(changePasswordView.getFrame(),
                                "Password updated successfully!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        openSettingsPage();
                    } 
                    else{
                        JOptionPane.showMessageDialog(changePasswordView.getFrame(),
                                "Failed to update password.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } 
            else{
                changePasswordView.getWarningLabel().setText("User not found.");
            }

        } 
        catch (SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(changePasswordView.getFrame(),
                    "An error occurred while changing password.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openSettingsPage(){
        changePasswordView.getFrame().dispose();
        CustomerSettingsView settingsView = new CustomerSettingsView();
        new CustomerSettingsController(settingsView, userId);
    }
}