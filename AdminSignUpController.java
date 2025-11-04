import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class AdminSignUpController {
    private AdminSignUpView accountSignUpView;

    public AdminSignUpController(AdminSignUpView view){
        accountSignUpView = view;

        accountSignUpView.getSignUp().addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if (accountSignUpView.validateInputs() == false){
                    return;
                }
                else{
                    int adminId = saveAccount();
                    if (adminId != -1){
                        openHomePage(adminId);
                    }
                }
            }
        });

        accountSignUpView.getBackButton().addActionListener(new ActionListener(){
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

        String sql = "INSERT INTO Admins (last_name, first_name, email, password) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){
            
            pstmt.setString(1, lastName);
            pstmt.setString(2, firstName);
            pstmt.setString(3, email);
            pstmt.setString(4, password);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0){
                try (ResultSet rs = pstmt.getGeneratedKeys()){
                    if (rs.next()){
                        int adminId = rs.getInt(1);
                        JOptionPane.showMessageDialog(accountSignUpView.getFrame(),
                            "Account successfully created!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        return adminId;
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

    public void openHomePage(int adminId){
        accountSignUpView.getFrame().dispose();
        AdminHomePageView homePageView = new AdminHomePageView();
        new AdminHomePageController(homePageView, adminId);
    }
}
