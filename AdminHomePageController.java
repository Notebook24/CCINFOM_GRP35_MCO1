import java.awt.event.*;
import java.sql.*;
import javax.swing.JOptionPane;

public class AdminHomePageController {
    private AdminHomePageView adminHomePageView;
    private int adminId;

    public AdminHomePageController(AdminHomePageView view, int id) {
        adminHomePageView = view;
        adminId = id;

        adminHomePageView.getLogoutButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                // Add confirmation dialog before logout
                int confirm = JOptionPane.showConfirmDialog(
                    adminHomePageView.getFrame(), 
                    "Are you sure you want to logout?", 
                    "Confirm Logout", 
                    JOptionPane.YES_NO_OPTION
                );
                
                if (confirm == JOptionPane.YES_OPTION) {
                    adminHomePageView.getFrame().dispose();
                    LandingPageView landingPageView = new LandingPageView();
                    new LandingPageController(landingPageView);
                }
            }
        });

        adminHomePageView.getProfileButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                adminHomePageView.getFrame().dispose();
                AdminSettingsView adminSettingsView = new AdminSettingsView();
                new AdminSettingsController(adminSettingsView, adminId);
            }
        });

        // Manage Products button listener
        adminHomePageView.getManageProductsButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adminHomePageView.getFrame().dispose();
                AdminMenuGroupView viewMenuCategoryView = new AdminMenuGroupView();
                new AdminMenuGroupController(viewMenuCategoryView, adminId);
            }
        });

        // City Groups button listener
        adminHomePageView.getManageCityGroupsButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adminHomePageView.getFrame().dispose();
                AdminACityGroupReadView cityGroupView = new AdminACityGroupReadView();
                new AdminACityGroupReadController(cityGroupView, adminId);
                cityGroupView.setVisible(true);
            }
        });

        // Menu Report button listener
        adminHomePageView.getCheckMenuStatusButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adminHomePageView.getFrame().dispose();
                // Create and show the Menu Report system
                AdminMenuReportController menuReportController = new AdminMenuReportController();
            }
        });

        // ORDER FREQUENCY REPORT button listener - NEW
        adminHomePageView.getCheckOrderStatusButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adminHomePageView.getFrame().dispose();
                // Create and show the Order Report system
                AdminOrderReportController orderReportController = new AdminOrderReportController();
            }
        });

        // Optional: Add action listeners for other report buttons if needed
        adminHomePageView.getCheckProfitButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adminHomePageView.getFrame().dispose();
                AdminRevenueReportController revenueReportController = new AdminRevenueReportController();
            }
        });

        adminHomePageView.getCheckEngagementButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(adminHomePageView.getFrame(),     //Put custoemr engagement controller here
                    "Customer Engagement Report feature coming soon!",  
                    "Feature Preview", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }
}