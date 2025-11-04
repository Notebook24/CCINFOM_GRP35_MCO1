import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class CustomerSignUpController {
    private CustomerSignUpView accountSignUpView;

    public CustomerSignUpController(CustomerSignUpView view){
        accountSignUpView = view;

        accountSignUpView.getSignUp().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                if (accountSignUpView.validateInputs() == false){
                    return;
                }
                else{
                    int customerId = saveAccount();
                    if (customerId != -1){
                        openHomePage(customerId);
                    }
                }
            }
        });

        accountSignUpView.getBackButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                accountSignUpView.getFrame().dispose();

                LandingPageView landingPage = new LandingPageView();

                new LandingPageController(landingPage);
            }
        });
    }

    public int saveAccount(){
        String firstName = accountSignUpView.getFirstName().trim();
        String lastName = accountSignUpView.getLastName().trim();
        String email = accountSignUpView.getEmail().trim();
        String password = accountSignUpView.getPassword().trim();
        String address = accountSignUpView.getAddress().trim();

        String sql = "INSERT INTO Customers (last_name, first_name, email, password, address) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            
            pstmt.setString(1, lastName);
            pstmt.setString(2, firstName);
            pstmt.setString(3, email);
            pstmt.setString(4, password);
            pstmt.setString(5, address);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0){
                try (ResultSet rs = pstmt.getGeneratedKeys()){
                    if (rs.next()){
                        int customerId = rs.getInt(1);
                        JOptionPane.showMessageDialog(accountSignUpView.getFrame(),
                                             "Account successfully created!",
                                               "Success",
                                                      JOptionPane.INFORMATION_MESSAGE);
                        return customerId;
                    }
                }
            }
        } 
        catch (SQLException ex){
            if (ex.getMessage().contains("Duplicate entry")){
                JOptionPane.showMessageDialog(accountSignUpView.getFrame(),
                        "Email is already registered.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } 
            else{
                ex.printStackTrace();
                JOptionPane.showMessageDialog(accountSignUpView.getFrame(),
                        "An error occurred while saving account.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        return -1;
    }

    public void openHomePage(int customerId){
        accountSignUpView.getFrame().dispose();

        CustomerHomePageView homePageView = new CustomerHomePageView();
        new CustomerHomePageController(homePageView, customerId);
    }
}
