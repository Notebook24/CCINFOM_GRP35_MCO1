import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LandingPageController {
    public LandingPageView landingPageView;

    public LandingPageController(LandingPageView view){
        landingPageView = view;

        landingPageView.getLoginAsCustomerButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                openLoginAsCustomerView();
            }
        });

        landingPageView.getLoginAsAdminButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                openLoginAsAdminView();
            }
        });

        landingPageView.getSignUpAsCustomer().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                openSignUpCustomerView();
            }
        });

        landingPageView.getSignUpAsAdmin().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                openSignUpAdminView();
            }
        });
    }

    private void openLoginAsCustomerView(){
        landingPageView.getFrame().dispose();

        CustomerLoginView loginView = new CustomerLoginView();
        new CustomerLoginController(loginView);
    }

    private void openLoginAsAdminView(){
        landingPageView.getFrame().dispose();

        AdminLoginView loginView = new AdminLoginView();
        new AdminLoginController(loginView);
    }

    private void openSignUpCustomerView(){
        landingPageView.getFrame().dispose();

        CustomerSignUpView signUpView = new CustomerSignUpView();
        new CustomerSignUpController(signUpView);
    }

    private void openSignUpAdminView(){
        landingPageView.getFrame().dispose();

        AdminSignUpView signUpView = new AdminSignUpView();
        new AdminSignUpController(signUpView);
    }
}
