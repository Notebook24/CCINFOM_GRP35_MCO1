import javax.swing.*;
import java.awt.event.*;

public class CustomerHomePageController {
    private CustomerHomePageView customerHomePageView;
    private int customerId;

    public CustomerHomePageController(CustomerHomePageView view, int id){
        customerHomePageView = view;
        customerId = id;

        setupNavigation();
        setupViewMenuButton();
    }

    private void setupNavigation() {
        // Home button - refresh current page
        customerHomePageView.homeButton.addActionListener(e -> {
            customerHomePageView.getFrame().dispose();
            CustomerHomePageView newHomeView = new CustomerHomePageView();
            new CustomerHomePageController(newHomeView, customerId);
        });

        // Payments button - show message (can be implemented later)
        customerHomePageView.paymentsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(customerHomePageView.getFrame(), 
                "Payments functionality coming soon!");
        });

        // Orders button - go to order tracking page
        customerHomePageView.ordersButton.addActionListener(e -> {
            customerHomePageView.getFrame().dispose();
            CustomerDeliveryTrackerView trackerView = new CustomerDeliveryTrackerView();
            new CustomerDeliveryTrackerController(trackerView, customerId);
        });

        // Profile button - go to settings page
        customerHomePageView.profileButton.addActionListener(e -> {
            customerHomePageView.getFrame().dispose();
            CustomerSettingsView settingsView = new CustomerSettingsView();
            new CustomerSettingsController(settingsView, customerId);
        });

        // Logout button - confirm and go to landing page
        customerHomePageView.logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                customerHomePageView.getFrame(), 
                "Are you sure you want to logout?", 
                "Confirm Logout", 
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                customerHomePageView.getFrame().dispose();
                LandingPageView landingPageView = new LandingPageView();
                new LandingPageController(landingPageView);
            }
        });
    }

    private void setupViewMenuButton() {
        customerHomePageView.getViewMenuButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                customerHomePageView.getFrame().dispose();
                CustomerMenuPageView cartPage = new CustomerMenuPageView();
                new CustomerMenuPageController(cartPage, customerId);
            }
        });
    }
}