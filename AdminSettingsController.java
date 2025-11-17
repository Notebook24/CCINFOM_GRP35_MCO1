import java.awt.event.*;
import java.sql.*;
import javax.swing.JOptionPane;

public class AdminSettingsController {
    private AdminSettingsView adminSettingsView;
    private int adminId;

    public AdminSettingsController(AdminSettingsView view, int id) {
        adminSettingsView = view;
        adminId = id;

        loadAdminDetails();

        adminSettingsView.getConfirm().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!adminSettingsView.validateInputs()) {
                    return;
                } else {
                    updateAdminDetails();
                    // Removed openHomePage() - user stays on settings page
                }
            }
        });

        adminSettingsView.getBackButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openHomePage();
            }
        });

        adminSettingsView.getChangePassword().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adminSettingsView.getFrame().dispose();
                
                AdminChangePasswordView changePasswordView = new AdminChangePasswordView();
                new AdminChangePasswordController(changePasswordView, adminId);
            }
        });

        adminSettingsView.getLogoutButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adminSettingsView.getFrame().dispose();
                LandingPageView landingPageView = new LandingPageView();
                new LandingPageController(landingPageView);
            }
        });
    }

    private void loadAdminDetails() {
        String sql = "SELECT last_name, first_name, email FROM admins WHERE admin_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, adminId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                adminSettingsView.getFirstNameField().setText(rs.getString("first_name"));
                adminSettingsView.getLastNameField().setText(rs.getString("last_name"));
                adminSettingsView.getEmailField().setText(rs.getString("email"));
            }
        } 
        catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(adminSettingsView.getFrame(),
                    "Failed to load your details.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateAdminDetails() {
        String firstName = adminSettingsView.getFirstName().trim();
        String lastName = adminSettingsView.getLastName().trim();
        String email = adminSettingsView.getEmail().trim();

        String sql = "UPDATE admins SET first_name = ?, last_name = ?, email = ? WHERE admin_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setInt(4, adminId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(adminSettingsView.getFrame(),
                        "Details updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                // User stays on settings page - no navigation needed
                // The fields already show the updated values since they're bound to the text fields
            }
        } 
        catch (SQLException ex) {
            if (ex.getMessage().contains("Duplicate entry")) {
                JOptionPane.showMessageDialog(adminSettingsView.getFrame(),
                        "Email is already registered.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } 
            else {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(adminSettingsView.getFrame(),
                        "Failed to update details.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openHomePage() {
        adminSettingsView.getFrame().dispose();
        AdminHomePageView homePageView = new AdminHomePageView();
        new AdminHomePageController(homePageView, adminId);
    }
}