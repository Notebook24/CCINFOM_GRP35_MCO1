import javax.swing.*;
import java.awt.*;
import java.util.regex.Pattern;

/**
 * Designs the Settings page in line with its controller
 */
public class CustomerSettingsView {
    private JFrame frame;
    private JPanel headerPanel, formPanel;
    private JButton logoutButton, changePasswordButton;
    private JButton confirmButton, backButton;
    private JTextField firstNameField, lastNameField, emailField, addressField;
    private JLabel logoLabel, warningLabel;

    private final String[] validCities = {
        "Caloocan", "Las Piñas", "Makati", "Malabon", "Mandaluyong", "Manila",
        "Marikina", "Muntinlupa", "Navotas", "Parañaque", "Pasay", "Pasig",
        "Quezon City", "San Juan", "Taguig", "Valenzuela"
    };

    /**
     * Constructor for seeting controller class.
     */
    public CustomerSettingsView(){
        frame = new JFrame("Customer Settings Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        logoLabel = new JLabel("Logo of Incorporation");
        logoutButton = new JButton("Log out");
        changePasswordButton = new JButton("Change Password");

        JPanel rightHeaderPanel = new JPanel();
        rightHeaderPanel.add(logoutButton);
        rightHeaderPanel.add(changePasswordButton);

        headerPanel.add(logoLabel, BorderLayout.WEST);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);
        frame.add(headerPanel, BorderLayout.NORTH);

        formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        warningLabel = new JLabel("", SwingConstants.CENTER);
        warningLabel.setForeground(Color.RED);
        warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(warningLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        firstNameField = new JTextField();
        addField(formPanel, "First Name:", firstNameField);

        lastNameField = new JTextField();
        addField(formPanel, "Last Name:", lastNameField);

        emailField = new JTextField();
        addField(formPanel, "Email:", emailField);

        addressField = new JTextField();
        addField(formPanel, "Address:", addressField);

        confirmButton = new JButton("Confirm");
        backButton = new JButton("Back");
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonsPanel.add(confirmButton);
        buttonsPanel.add(backButton);
        formPanel.add(buttonsPanel);

        frame.add(formPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void addField(JPanel panel, String label, JTextField field){
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.add(new JLabel(label), BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        panel.add(row);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    public boolean validateInputs() {
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();

        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", email)){
            warningLabel.setText("Please enter a valid email address.");
            return false;
        }

        boolean validCity = false;
        for (String city : validCities){
            if (address.toLowerCase().contains(city.toLowerCase())) {
                validCity = true;
                break;
            }
        }

        if (!validCity){
            warningLabel.setText("Address must contain a valid NCR city.");
            return false;
        }

        if (!address.matches(".*\\d+.*") || !address.contains(",")){
            warningLabel.setText("Address must include house/street, barangay/subdivision, and city.");
            return false;
        }

        warningLabel.setText("");
        return true;
    }

    public JFrame getFrame(){
        return frame;
    }

    public String getFirstName(){
        return firstNameField.getText().trim();
    }

    public String getLastName(){
        return lastNameField.getText().trim();
    }

    public String getEmail(){
        return emailField.getText().trim();
    }

    public String getAddress(){
        return addressField.getText().trim();
    }

    public JButton getConfirm(){
        return confirmButton;
    }

    public JButton getBackButton(){
        return backButton;
    }

    public JButton getChangePassword(){
        return changePasswordButton;
    }

     public JTextField getFirstNameField(){
        return firstNameField;
    }

    public JTextField getLastNameField(){
        return lastNameField;
    }

    public JTextField getEmailField(){
        return emailField;
    }

    public JTextField getAddressField(){
        return addressField;
    }
}
