import javax.swing.*;
import java.awt.*;

/**
 * Designs the Change Password Page in line with its controller.
 */
public class AccountChangePasswordView {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField oldPasswordField, newPasswordField, confirmPasswordField;
    private JButton confirmButton, backButton, logoutButton, settingsButton;
    private JLabel titleLabel;
    private JPanel formPanel;

    /**
     * Constructor for AccountChangePasswordView class.
     */
    public AccountChangePasswordView() {
        // Frame configuration
        frame = new JFrame("Change Password");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        // Title at top
        titleLabel = new JLabel("Change Password", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        frame.add(titleLabel, BorderLayout.NORTH);

        // Top-right buttons (Logout, Settings)
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        logoutButton = new JButton("Log out");
        settingsButton = new JButton("Settings");
        topRightPanel.add(logoutButton);
        topRightPanel.add(settingsButton);
        frame.add(topRightPanel, BorderLayout.EAST);

        // Form panel (center)
        formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        // Username row
        JPanel usernamePanel = new JPanel(new BorderLayout(10, 0));
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        usernamePanel.add(usernameLabel, BorderLayout.WEST);
        usernamePanel.add(usernameField, BorderLayout.CENTER);
        formPanel.add(usernamePanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Old Password row
        JPanel oldPasswordPanel = new JPanel(new BorderLayout(10, 0));
        JLabel oldPasswordLabel = new JLabel("Old Password:");
        oldPasswordField = new JPasswordField();
        oldPasswordPanel.add(oldPasswordLabel, BorderLayout.WEST);
        oldPasswordPanel.add(oldPasswordField, BorderLayout.CENTER);
        formPanel.add(oldPasswordPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // New Password row
        JPanel newPasswordPanel = new JPanel(new BorderLayout(10, 0));
        JLabel newPasswordLabel = new JLabel("New Password:");
        newPasswordField = new JPasswordField();
        newPasswordPanel.add(newPasswordLabel, BorderLayout.WEST);
        newPasswordPanel.add(newPasswordField, BorderLayout.CENTER);
        formPanel.add(newPasswordPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Confirm Password row
        JPanel confirmPasswordPanel = new JPanel(new BorderLayout(10, 0));
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordField = new JPasswordField();
        confirmPasswordPanel.add(confirmPasswordLabel, BorderLayout.WEST);
        confirmPasswordPanel.add(confirmPasswordField, BorderLayout.CENTER);
        formPanel.add(confirmPasswordPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Buttons (Confirm and Back)
        JPanel buttonPanel = new JPanel(new BorderLayout(10, 0));
        confirmButton = new JButton("Confirm");
        backButton = new JButton("Back");
        buttonPanel.add(confirmButton, BorderLayout.WEST);
        buttonPanel.add(backButton, BorderLayout.EAST);
        formPanel.add(buttonPanel);

        // Add form panel to frame
        frame.add(formPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }
}
