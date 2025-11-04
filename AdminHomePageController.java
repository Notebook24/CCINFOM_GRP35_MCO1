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

        adminHomePageView.getViewProductButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adminHomePageView.getFrame().dispose();

                AdminViewProductsView viewProductsView = new AdminViewProductsView();
                new AdminViewProductsController(viewProductsView, adminId);
            }
        });

        adminHomePageView.getAddProductButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adminHomePageView.getFrame().dispose();

                AdminAddProductView addProductView = new AdminAddProductView();
                new AdminAddProductController(addProductView, adminId);
            }
        });
    }
}
