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

        // FIXED: Extract city from address (3rd value after 2 commas) - same as settings
        String city = null;
        String[] addressParts = address.split(",");
        if (addressParts.length >= 3) {
            city = addressParts[2].trim();  // Get the 3rd part (index 2)
            // Remove "City" in ANY case format
            city = city.replaceAll("(?i)\\s*city$", "").trim();
        } else {
            // If address doesn't have enough parts, show error
            JOptionPane.showMessageDialog(
                accountSignUpView.getFrame(),
                "Address must be in format: House/Street, Barangay, City",
                "Invalid Address Format",
                JOptionPane.ERROR_MESSAGE
            );
            return -1;
        }

        // Validate if city exists in database
        if (!isValidCity(city)) {
            JOptionPane.showMessageDialog(
                accountSignUpView.getFrame(),
                "City must be one of the valid NCR or available cities.",
                "Invalid City",
                JOptionPane.ERROR_MESSAGE
            );
            return -1;
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

            // insert into Customers
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
    
    // Method to validate if city exists in database
    private boolean isValidCity(String city) {
        if (city == null || city.isEmpty()) {
            return false;
        }

        String query = "SELECT COUNT(*) FROM Cities WHERE city_name LIKE ? AND is_available = 1";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, "%" + city + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                accountSignUpView.getFrame(),
                "Error validating city. Please try again.",
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
        
        return false;
    }
    
    public void openHomePage(int customerId){
        accountSignUpView.getFrame().dispose();

        CustomerHomePageView homePageView = new CustomerHomePageView();
        new CustomerHomePageController(homePageView, customerId);
    }
}