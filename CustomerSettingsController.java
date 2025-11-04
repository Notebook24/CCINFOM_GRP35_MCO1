import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class CustomerSettingsController {
    private CustomerSettingsView customerSettingsView;
    private int customerId;

    public CustomerSettingsController(CustomerSettingsView view, int id){
        customerSettingsView = view;
        customerId = id;

        loadCustomerDetails();

        customerSettingsView.getConfirm().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                if (!customerSettingsView.validateInputs()){
                    return;
                } else {
                    updateCustomerDetails();
                    openHomePage();
                }
            }
        });

        customerSettingsView.getBackButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                openHomePage();
            }
        });

        customerSettingsView.getChangePassword().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                customerSettingsView.getFrame().dispose();
                
                CustomerChangePasswordView changePasswordView = new CustomerChangePasswordView();
                new CustomerChangePasswordController(changePasswordView, customerId);
            }
        });
    }

    private void loadCustomerDetails(){
        String sql = "SELECT last_name, first_name, email, address FROM customers WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                customerSettingsView.getFirstNameField().setText(rs.getString("first_name"));
                customerSettingsView.getLastNameField().setText(rs.getString("last_name"));
                customerSettingsView.getEmailField().setText(rs.getString("email"));
                customerSettingsView.getAddressField().setText(rs.getString("address"));
            }
        } 
        catch (SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(customerSettingsView.getFrame(),
                    "Failed to load your details.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCustomerDetails(){
        String firstName = customerSettingsView.getFirstName().trim();
        String lastName = customerSettingsView.getLastName().trim();
        String email = customerSettingsView.getEmail().trim();
        String address = customerSettingsView.getAddress().trim();

        String sql = "UPDATE customers SET first_name = ?, last_name = ?, email = ?, address = ? WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, address);
            pstmt.setInt(5, customerId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0){
                JOptionPane.showMessageDialog(customerSettingsView.getFrame(),
                        "Details updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } 
        catch (SQLException ex){
            if (ex.getMessage().contains("Duplicate entry")){
                JOptionPane.showMessageDialog(customerSettingsView.getFrame(),
                        "Email is already registered.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } 
            else{
                ex.printStackTrace();
                JOptionPane.showMessageDialog(customerSettingsView.getFrame(),
                        "Failed to update details.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openHomePage(){
        customerSettingsView.getFrame().dispose();

        CustomerHomePageView homePageView = new CustomerHomePageView();
        new CustomerHomePageController(homePageView, customerId);
    }
}
