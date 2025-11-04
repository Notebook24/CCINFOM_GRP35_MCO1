import java.awt.event.*;
import java.sql.*;
import javax.swing.JOptionPane;

public class AdminLoginController {
    private AdminLoginView accountLoginView;

    public AdminLoginController(AdminLoginView view){
        accountLoginView = view;

        accountLoginView.getLoginButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                if(!accountLoginView.validateInputs()){
                    return;
                }
                else{
                    int adminId = getAccount();
                    if (adminId != -1){
                        openHomePage(adminId);
                    }
                }
            }
        });

        accountLoginView.getBackButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                accountLoginView.getFrame().dispose();

                LandingPageView landingPage = new LandingPageView();

                new LandingPageController(landingPage);
            }
        });
    }

    public int getAccount(){
        String email = accountLoginView.getEmail().trim();
        String password = accountLoginView.getPassword().trim();

        String sql = "SELECT * FROM admins WHERE email = ? AND password = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(sql)){

            pst.setString(1, email);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            if (rs.next()){
                JOptionPane.showMessageDialog(accountLoginView.getFrame(), 
                                     "Login Successful!",
                                       "Success",
                                              JOptionPane.INFORMATION_MESSAGE);
                return rs.getInt(1);
            }
            else{
                JOptionPane.showMessageDialog(accountLoginView.getFrame(), 
                                     "Invalid username or password",
                                       "Error",
                                              JOptionPane.ERROR_MESSAGE);
            }

            System.out.println("Email: '" + email + "'");
            System.out.println("Password: '" + password + "'");
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
        return -1;
    }

    public void openHomePage(int adminId){
        accountLoginView.getFrame().dispose();

        AdminHomePageView homePageView = new AdminHomePageView();
        new AdminHomePageController(homePageView, adminId);
    }
}
