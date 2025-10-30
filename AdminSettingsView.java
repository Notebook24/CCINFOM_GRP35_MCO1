import javax.swing.*;
import java.awt.*;

/**
 * Displays the Admin Settings Page of the Incorporation App.
 */
public class AdminSettingsView {
    private JFrame frame;
    private JPanel headerPanel, formPanel;
    private JButton logoutButton, settingsButton, editProfileButton;
    private JLabel logoLabel, firstNameLabel, lastNameLabel, passwordLabel;
    private JTextField firstNameField, lastNameField;
    private JPasswordField passwordField;

    /**
     * Constructor for AdminSettingsView class.
     */
    public AdminSettingsView() {
        // Frame setup
        frame = new JFrame("Admin Settings Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        // ===== HEADER =====
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

        // ===== BODY / FORM =====
        formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        firstNameLabel = new JLabel("First Name:");
        firstNameField = new JTextField(15);

        lastNameLabel = new JLabel("Last Name:");
        lastNameField = new JTextField(15);

        passwordLabel = new JLabel("Password: (masked); changeable");
        passwordField = new JPasswordField(15);
        passwordField.setEchoChar('*');

        editProfileButton = new JButton("Edit Profile");

        // Add components to form
        formPanel.add(firstNameLabel);
        formPanel.add(firstNameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(lastNameLabel);
        formPanel.add(lastNameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        formPanel.add(editProfileButton);
        editProfileButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        frame.add(formPanel, BorderLayout.CENTER);

        // ===== SHOW FRAME =====
        frame.setVisible(true);
    }

    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminSettingsView::new);
    }
}
