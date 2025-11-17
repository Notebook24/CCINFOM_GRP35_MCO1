// AdminHomePageController.java
import java.awt.event.*;
import java.sql.*;

public class AdminHomePageController {
    private AdminHomePageView adminHomePageView;
    private int adminId;

    public AdminHomePageController(AdminHomePageView view, int id) {
        adminHomePageView = view;
        adminId = id;

        adminHomePageView.getLogoutButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                adminHomePageView.getFrame().dispose();
                LandingPageView landingPageView = new LandingPageView();
                new LandingPageController(landingPageView);
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

        // CHANGED: Updated to use manageProductsButton
        adminHomePageView.getManageProductsButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adminHomePageView.getFrame().dispose();
                AdminViewProductsView viewProductsView = new AdminViewProductsView();
                new AdminViewProductsController(viewProductsView, adminId);
            }
        });

        // REMOVED: Add Product button listener since it's now integrated in View Products

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
    }
}