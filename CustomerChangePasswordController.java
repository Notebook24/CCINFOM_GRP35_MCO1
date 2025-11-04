import java.awt.event.*;
import java.sql.*;
import javax.swing.JOptionPane;

public class CustomerChangePasswordController {
    private CustomerChangePasswordView accountChangePasswordView;
    private int userId;

    public CustomerChangePasswordController(CustomerChangePasswordView view, int id){
        accountChangePasswordView = view;
        userId = id;

        accountChangePasswordView.getConfirm().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!accountChangePasswordView.validateInputs()) {
                    return;
                } else {
                    changePassword();
                }
            }
        });

        accountChangePasswordView.getBackButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                openSettingsPage();
            }
        });

        accountChangePasswordView.getLogoutButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accountChangePasswordView.getFrame().dispose();
                CustomerLoginView loginView = new CustomerLoginView();
                new CustomerLoginController(loginView);
            }
        });

        accountChangePasswordView.getSettingsButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSettingsPage();
            }
        });
    }

    private void changePassword() {
        String oldPassword = accountChangePasswordView.getOldPassword();
        String newPassword = accountChangePasswordView.getNewPassword();

        String checkSql = "SELECT password FROM customers WHERE customer_id = ?";
        String updateSql = "UPDATE customers SET password = ? WHERE customer_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, userId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()){
                String currentPassword = rs.getString("password");

                if (!currentPassword.equals(oldPassword)){
                    accountChangePasswordView.getWarningLabel().setText("Old password is incorrect.");
                    return;
                }

                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)){
                    updateStmt.setString(1, newPassword);
                    updateStmt.setInt(2, userId);
                    int rowsUpdated = updateStmt.executeUpdate();

                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(accountChangePasswordView.getFrame(),
                                "Password updated successfully!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        openSettingsPage();
                    } 
                    else{
                        JOptionPane.showMessageDialog(accountChangePasswordView.getFrame(),
                                "Failed to update password.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } 
            else{
                accountChangePasswordView.getWarningLabel().setText("User not found.");
            }

        } 
        catch (SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(accountChangePasswordView.getFrame(),
                    "An error occurred while changing password.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openSettingsPage(){
        accountChangePasswordView.getFrame().dispose();

        CustomerSettingsView settingsView = new CustomerSettingsView();
        new CustomerSettingsController(settingsView, userId);
    }
}
