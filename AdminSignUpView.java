import javax.swing.*;
import java.awt.*;
import java.util.regex.Pattern;

/**
 * Designs the Sign up page in line with its controller
 */
public class AdminSignUpView {
    private JFrame frame;
    private JTextField firstNameField, lastNameField, emailField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton backButton, signUpButton;
    private JLabel titleLabel, warningLabel;
    private JPanel formPanel, buttonPanel;

    /**
     * Constructor for Sign Up View class in ine with its controller
     */
    public AdminSignUpView() {
        frame = new JFrame("Sign Up Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 450);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        titleLabel = new JLabel("Sign Up Page", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        frame.add(titleLabel, BorderLayout.NORTH);

        formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        warningLabel = new JLabel("", SwingConstants.CENTER);
        warningLabel.setForeground(Color.RED);
        warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(warningLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel firstNamePanel = new JPanel(new BorderLayout(10, 0));
        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameField = new JTextField();
        firstNamePanel.add(firstNameLabel, BorderLayout.WEST);
        firstNamePanel.add(firstNameField, BorderLayout.CENTER);
        formPanel.add(firstNamePanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel lastNamePanel = new JPanel(new BorderLayout(10, 0));
        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameField = new JTextField();
        lastNamePanel.add(lastNameLabel, BorderLayout.WEST);
        lastNamePanel.add(lastNameField, BorderLayout.CENTER);
        formPanel.add(lastNamePanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel emailPanel = new JPanel(new BorderLayout(10, 0));
        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField();
        emailPanel.add(emailLabel, BorderLayout.WEST);
        emailPanel.add(emailField, BorderLayout.CENTER);
        formPanel.add(emailPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel passwordPanel = new JPanel(new BorderLayout(10, 0));
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        passwordPanel.add(passwordLabel, BorderLayout.WEST);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        formPanel.add(passwordPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel confirmPasswordPanel = new JPanel(new BorderLayout(10, 0));
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordField = new JPasswordField();
        confirmPasswordPanel.add(confirmPasswordLabel, BorderLayout.WEST);
        confirmPasswordPanel.add(confirmPasswordField, BorderLayout.CENTER);
        formPanel.add(confirmPasswordPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        backButton = new JButton("Back");    
        signUpButton = new JButton("Sign Up");
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(backButton);
        buttonPanel.add(signUpButton);
        formPanel.add(buttonPanel);

        frame.add(formPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public boolean validateInputs() {
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String email = emailField.getText().trim();

        if (!password.equals(confirmPassword)) {
            warningLabel.setText("Confirm password and password do not align.");
            return false;
        }

        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", email)) {
            warningLabel.setText("Please enter a valid email address.");
            return false;
        }

        return true;
    }

    public JFrame getFrame() {
        return frame;
    }

    public String getFirstName() {
        return firstNameField.getText().trim();
    }

    public String getLastName() {
        return lastNameField.getText().trim();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public String getEmail() {
        return emailField.getText().trim();
    }

    public JButton getSignUp() {
        return signUpButton;
    }

    public JButton getBackButton() {
        return backButton;
    }
}
