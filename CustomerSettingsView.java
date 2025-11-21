import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerSettingsView {

    private JFrame frame;
    private JPanel headerPanel, logoPanel, navPanel;
    private JButton homeButton, paymentsButton, ordersButton, profileButton, logoutButton;
    private JButton backButton, confirmButton, changePasswordButton, deleteButton;
    private JTextField firstNameField, lastNameField, emailField, addressField;
    private JLabel warningLabel, logoLabel;
    private List<String> validCities = new ArrayList<>();

    public CustomerSettingsView() {
        frame = new JFrame("Customer Settings");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1920, 1080);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // ================= HEADER ==================
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 100, 20, 100));
        headerPanel.setBackground(Color.WHITE);

        // Left: Logo
        logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logoPanel.setBackground(Color.WHITE);

        // Load and resize logo image safely
        ImageIcon rawLogo = new ImageIcon("design_images/koreanexpress-logo.png");
        Image scaledLogo = rawLogo.getImage().getScaledInstance(300, 90, Image.SCALE_SMOOTH);
        ImageIcon logoIcon = new ImageIcon(scaledLogo);

        logoLabel = new JLabel(logoIcon);
        logoPanel.add(logoLabel);

        // Right: Navigation buttons
        navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 50, 20));
        navPanel.setBackground(Color.WHITE);

        homeButton = makeNavButton("Home");
        paymentsButton = makeNavButton("Payments");
        ordersButton = makeNavButton("Orders");
        profileButton = makeNavButton("Profile");
        logoutButton = makeNavButton("Log Out");

        navPanel.add(homeButton);
        navPanel.add(paymentsButton);
        navPanel.add(ordersButton);
        navPanel.add(profileButton);
        navPanel.add(logoutButton);

        headerPanel.add(logoPanel, BorderLayout.WEST);
        headerPanel.add(navPanel, BorderLayout.EAST);
        frame.add(headerPanel, BorderLayout.NORTH);
        
        //------------------------------------
        // CENTER FORM PANEL
        //------------------------------------
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 400, 50, 400));

        // Back button
        backButton = createRoundedButton("Back");
        backButton.setPreferredSize(new Dimension(100, 35));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel backPanel = new JPanel();
        backPanel.setBackground(Color.WHITE);
        backPanel.add(backButton);
        centerPanel.add(backPanel);

        // Warning label
        warningLabel = new JLabel("", SwingConstants.CENTER);
        warningLabel.setForeground(Color.RED);
        warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        warningLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        centerPanel.add(warningLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        //------------------------------------
        // INPUT FIELDS - LABELS BESIDE FIELDS
        //------------------------------------
        firstNameField = createTextField();
        lastNameField = createTextField();
        emailField = createTextField();
        addressField = createTextField();

        centerPanel.add(createHorizontalField("First Name", firstNameField));
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(createHorizontalField("Last Name", lastNameField));
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(createHorizontalField("Email Address", emailField));
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(createHorizontalField("Address", addressField));
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Address format hint
        JLabel addressHint = new JLabel("Format: Street, Barangay, City");
        addressHint.setFont(new Font("SansSerif", Font.ITALIC, 12));
        addressHint.setForeground(Color.RED);
        addressHint.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(addressHint);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        //------------------------------------
        // BUTTONS
        //------------------------------------
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnRow.setBackground(Color.WHITE);

        confirmButton = createRoundedButton("Confirm Profile");
        changePasswordButton = createRoundedButton("Change Password");
        deleteButton = createRoundedButton("Delete Account");
        deleteButton.setForeground(new Color(140, 0, 0));

        confirmButton.setPreferredSize(new Dimension(180, 40));
        changePasswordButton.setPreferredSize(new Dimension(180, 40));
        deleteButton.setPreferredSize(new Dimension(180, 40));

        btnRow.add(confirmButton);
        btnRow.add(changePasswordButton);

        centerPanel.add(btnRow);

        JPanel deleteRow = new JPanel();
        deleteRow.setBackground(Color.WHITE);
        deleteRow.add(deleteButton);
        centerPanel.add(deleteRow);

        frame.add(centerPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private JButton makeNavButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 20));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setForeground(new Color(230, 0, 0));
        return b;
    }

    private JPanel createHorizontalField(String label, JTextField field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(600, 40));
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        lbl.setPreferredSize(new Dimension(150, 30));
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setForeground(Color.RED);
        
        field.setPreferredSize(new Dimension(300, 35));
        field.setHorizontalAlignment(JTextField.LEFT);
        
        panel.add(lbl);
        panel.add(field);
        
        return panel;
    }

    private JTextField createTextField() {
        JTextField f = new JTextField();
        f.setPreferredSize(new Dimension(300, 35));
        f.setHorizontalAlignment(JTextField.LEFT);
        f.setFont(new Font("SansSerif", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return f;
    }

    private JButton createRoundedButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setForeground(Color.BLACK);
        button.setBackground(new Color(255, 180, 180));
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 150, 150), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        return button;
    }

    // Validation method with character limits
    public boolean validateInputs() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();

        // Clear previous warnings
        warningLabel.setText("");

        // Length validations based on SQL VARCHAR limits
        if (firstName.isEmpty() || lastName.isEmpty()) {
            warningLabel.setText("First name and last name are required.");
            return false;
        }

        if (firstName.length() > 50) {
            warningLabel.setText("First name must be 50 characters or less.");
            return false;
        }

        if (lastName.length() > 50) {
            warningLabel.setText("Last name must be 50 characters or less.");
            return false;
        }

        if (email.length() > 50) {
            warningLabel.setText("Email must be 50 characters or less.");
            return false;
        }

        if (address.length() > 50) {
            warningLabel.setText("Address must be 50 characters or less.");
            return false;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            warningLabel.setText("Please enter a valid email address.");
            return false;
        }

        // Enhanced address format validation
        if (address.isEmpty()) {
            warningLabel.setText("Address is required.");
            return false;
        }

        // Check for basic address components
        if (!address.matches(".*\\d+.*")) {
            warningLabel.setText("Address must include house/street number.");
            return false;
        }

        // Check for comma-separated format with at least 2 commas
        String[] addressParts = address.split(",");
        if (addressParts.length < 3) {
            warningLabel.setText("Address must be in format: Street, Barangay, City");
            return false;
        }

        // Check that each part is not empty
        for (int i = 0; i < Math.min(3, addressParts.length); i++) {
            if (addressParts[i].trim().isEmpty()) {
                warningLabel.setText("Address parts cannot be empty. Format: Street, Barangay, City");
                return false;
            }
        }

        warningLabel.setText("");
        return true;
    }

    //------------------------------------
    // GETTERS
    //------------------------------------

    public JFrame getFrame() {
        return frame;
    }

    public JButton getConfirmButton() {
        return confirmButton;
    }

    public JButton getBackButton() {
        return backButton;
    }

    public JButton getChangePasswordButton() {
        return changePasswordButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }

    public JButton getHomeButton() {
        return homeButton;
    }

    public JButton getPaymentsButton() {
        return paymentsButton;
    }

    public JButton getOrdersButton() {
        return ordersButton;
    }

    public JButton getProfileButton() {
        return profileButton;
    }

    public JButton getLogoutButton() {
        return logoutButton;
    }

    public JTextField getFirstNameField() {
        return firstNameField;
    }

    public JTextField getLastNameField() {
        return lastNameField;
    }

    public JTextField getEmailField() {
        return emailField;
    }

    public JTextField getAddressField() {
        return addressField;
    }

    public void setValidCities(List<String> cities) {
        validCities = cities;
    }
}