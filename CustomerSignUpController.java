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

    public int saveAccount() {
        String firstName = accountSignUpView.getFirstName().trim();
        String lastName = accountSignUpView.getLastName().trim();
        String email = accountSignUpView.getEmail().trim();
        String password = accountSignUpView.getPassword().trim();
        String address = accountSignUpView.getAddress().trim();

        // extract city from address
        String city = null;
        String[] addressParts = address.split(",");
        if (addressParts.length >= 2) {
            city = addressParts[addressParts.length - 1].trim();
            // Remove "City" in ANY case format
            city = city.replaceAll("(?i)\\s*city$", "").trim();
        }

        String sql = "INSERT INTO Customers (last_name, first_name, email, password, address, city_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {

            // fetch city_id
            Integer cityId = null;

            if (city != null && !city.isEmpty()) {
                String cityQuery = "SELECT city_id FROM Cities WHERE city_name LIKE ?";
                try (PreparedStatement cityStmt = conn.prepareStatement(cityQuery)) {
                    cityStmt.setString(1, "%" + city + "%");

                    try (ResultSet rs = cityStmt.executeQuery()) {
                        if (rs.next()) {
                            cityId = rs.getInt("city_id");
                        }
                    }
                }
            }

            // 3. insert into Customers
            try (PreparedStatement pstmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

                pstmt.setString(1, lastName);
                pstmt.setString(2, firstName);
                pstmt.setString(3, email);
                pstmt.setString(4, password);
                pstmt.setString(5, address);

                if (cityId != null) {
                    pstmt.setInt(6, cityId);
                } else {
                    pstmt.setNull(6, java.sql.Types.INTEGER);
                }

                int rowsInserted = pstmt.executeUpdate();
                if (rowsInserted > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            int customerId = rs.getInt(1);

                            JOptionPane.showMessageDialog(
                                accountSignUpView.getFrame(),
                                "Account successfully created!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE
                            );

                            return customerId;
                        }
                    }
                }
            }

        } catch (SQLException ex) {
            if (ex.getMessage().contains("Duplicate entry")) {
                JOptionPane.showMessageDialog(
                    accountSignUpView.getFrame(),
                    "Email is already registered.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            } else {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                    accountSignUpView.getFrame(),
                    "An error occurred while saving account.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
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
