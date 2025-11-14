import java.awt.event.*;

public class CustomerHomePageController {
    private CustomerHomePageView customerHomePageView;
    private int customerId;

    public CustomerHomePageController(CustomerHomePageView view, int id){
        customerHomePageView = view;
        customerId = id;

        customerHomePageView.getLogoutButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                customerHomePageView.getFrame().dispose();

                LandingPageView landingPageView = new LandingPageView();
                new LandingPageController(landingPageView);
            }
        });

        customerHomePageView.getPaymentsButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                // Add action for payments button
            }
        });

        customerHomePageView.getOrdersButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                // Add action for orders button
            }
        });

        customerHomePageView.getProfileButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                customerHomePageView.getFrame().dispose();

                CustomerSettingsView settingsView = new CustomerSettingsView();
                new CustomerSettingsController(settingsView, customerId);
            }
        });

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
