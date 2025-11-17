import java.awt.event.*;
import java.sql.*;
import javax.swing.JOptionPane;

public class AdminChangePasswordController {
    private AdminChangePasswordView adminChangePasswordView;
    private int adminId;

    public AdminChangePasswordController(AdminChangePasswordView view, int id) {
        adminChangePasswordView = view;
        adminId = id;

        adminChangePasswordView.getConfirm().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!adminChangePasswordView.validateInputs()) {
                    return;
                } else {
                    changePassword();
                }
            }
        });

        adminChangePasswordView.getBackButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSettingsPage();
            }
        });

        adminChangePasswordView.getLogoutButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adminChangePasswordView.getFrame().dispose();
                // Assuming you have an AdminLoginView or LandingPageView
                LandingPageView landingPageView = new LandingPageView();
                new LandingPageController(landingPageView);
            }
        });

        adminChangePasswordView.getSettingsButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSettingsPage();
            }
        });
    }

    private void changePassword() {
        String oldPassword = adminChangePasswordView.getOldPassword();
        String newPassword = adminChangePasswordView.getNewPassword();

        String checkSql = "SELECT password FROM admins WHERE admin_id = ?";
        String updateSql = "UPDATE admins SET password = ? WHERE admin_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, adminId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                String currentPassword = rs.getString("password");

                if (!currentPassword.equals(oldPassword)) {
                    adminChangePasswordView.getWarningLabel().setText("Old password is incorrect.");
                    return;
                }

                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, newPassword);
                    updateStmt.setInt(2, adminId);
                    int rowsUpdated = updateStmt.executeUpdate();

                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(adminChangePasswordView.getFrame(),
                                "Password updated successfully!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        openSettingsPage();
                    } 
                    else {
                        JOptionPane.showMessageDialog(adminChangePasswordView.getFrame(),
                                "Failed to update password.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } 
            else {
                adminChangePasswordView.getWarningLabel().setText("Admin not found.");
            }

        } 
        catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(adminChangePasswordView.getFrame(),
                    "An error occurred while changing password.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openSettingsPage() {
        adminChangePasswordView.getFrame().dispose();
        AdminSettingsView settingsView = new AdminSettingsView();
        new AdminSettingsController(settingsView, adminId);
    }
}