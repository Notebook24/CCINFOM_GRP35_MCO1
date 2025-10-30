import javax.swing.*;
import java.awt.*;

/**
 * Designs the Sign Up Page in line with its controller.
 */
public class AccountSignUpView {
    private JFrame frame;
    private JTextField usernameField, emailField, addressField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton signUpButton;
    private JLabel titleLabel;
    private JPanel formPanel;

    /**
     * Constructor for AccountSignUpView class.
     */
    public AccountSignUpView() {
        // frame config
        frame = new JFrame("Sign Up Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 350);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        // title at top
        titleLabel = new JLabel("Sign Up Page", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        frame.add(titleLabel, BorderLayout.NORTH);

        // form panel (center)
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

        // Email row
        JPanel emailPanel = new JPanel(new BorderLayout(10, 0));
        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField();
        emailPanel.add(emailLabel, BorderLayout.WEST);
        emailPanel.add(emailField, BorderLayout.CENTER);
        formPanel.add(emailPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Address row
        JPanel addressPanel = new JPanel(new BorderLayout(10, 0));
        JLabel addressLabel = new JLabel("Address:");
        addressField = new JTextField();
        addressPanel.add(addressLabel, BorderLayout.WEST);
        addressPanel.add(addressField, BorderLayout.CENTER);
        formPanel.add(addressPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Password row
        JPanel passwordPanel = new JPanel(new BorderLayout(10, 0));
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        passwordPanel.add(passwordLabel, BorderLayout.WEST);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        formPanel.add(passwordPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Confirm Password row
        JPanel confirmPasswordPanel = new JPanel(new BorderLayout(10, 0));
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordField = new JPasswordField();
        confirmPasswordPanel.add(confirmPasswordLabel, BorderLayout.WEST);
        confirmPasswordPanel.add(confirmPasswordField, BorderLayout.CENTER);
        formPanel.add(confirmPasswordPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Sign Up button (bottom)
        signUpButton = new JButton("Sign Up");
        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(signUpButton);

        frame.add(formPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }
}
