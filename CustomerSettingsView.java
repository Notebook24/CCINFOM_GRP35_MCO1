import javax.swing.*;
import java.awt.*;

/**
 * Designs the Customer Settings Page in line with its controller.
 */
public class CustomerSettingsView {
    private JFrame frame;
    private JPanel headerPanel, formPanel;
    private JButton logoutButton, settingsButton, changePasswordButton;
    private JTextField firstNameField, lastNameField, locationField;
    private JPasswordField passwordField;
    private JLabel logoLabel;

    /**
     * Constructor for CustomerSettingsView class.
     */
    public CustomerSettingsView() {
        // Frame setup
        frame = new JFrame("Customer Settings Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 350);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        // Header
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        logoLabel = new JLabel("Logo of Incorporation");
        logoutButton = new JButton("Log out");
        settingsButton = new JButton("Settings");

        JPanel rightHeaderPanel = new JPanel();
        rightHeaderPanel.add(logoutButton);
        rightHeaderPanel.add(settingsButton);

        headerPanel.add(logoLabel, BorderLayout.WEST);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);

        frame.add(headerPanel, BorderLayout.NORTH);

        // Form section
        formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // First Name
        JPanel firstNamePanel = new JPanel(new BorderLayout(10, 0));
        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameField = new JTextField();
        firstNamePanel.add(firstNameLabel, BorderLayout.WEST);
        firstNamePanel.add(firstNameField, BorderLayout.CENTER);
        formPanel.add(firstNamePanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Last Name
        JPanel lastNamePanel = new JPanel(new BorderLayout(10, 0));
        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameField = new JTextField();
        lastNamePanel.add(lastNameLabel, BorderLayout.WEST);
        lastNamePanel.add(lastNameField, BorderLayout.CENTER);
        formPanel.add(lastNamePanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Password (masked)
        JPanel passwordPanel = new JPanel(new BorderLayout(10, 0));
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        passwordField.setText(""); // masked by default
        passwordPanel.add(passwordLabel, BorderLayout.WEST);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        formPanel.add(passwordPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Location / Address
        JPanel locationPanel = new JPanel(new BorderLayout(10, 0));
        JLabel locationLabel = new JLabel("Location/Address:");
        locationField = new JTextField();
        locationPanel.add(locationLabel, BorderLayout.WEST);
        locationPanel.add(locationField, BorderLayout.CENTER);
        formPanel.add(locationPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Change Password Button
        changePasswordButton = new JButton("Change Password");
        changePasswordButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(changePasswordButton);

        frame.add(formPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CustomerSettingsView::new);
    }
}
