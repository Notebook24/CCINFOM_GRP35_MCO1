import javax.swing.*;
import java.awt.*;
import java.util.regex.Pattern;


/**
 * Designs the Log in page in line with its controller
 */
public class AdminLoginView{
    private JFrame frame;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton backButton, loginButton;
    private JLabel warningLabel, userLabel, passLabel;
    private JPanel formPanel, userPanel, passPanel, buttonPanel, southPanel;

    /**
     * Constructor for Sign Up View class.
     */
    public AdminLoginView() {
        // frame config
        frame = new JFrame("Log In");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null); 

        // form panel or the central panel for credentials
        formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40)); // padding

        warningLabel = new JLabel("", SwingConstants.CENTER);
        warningLabel.setForeground(Color.RED);
        warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(warningLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // email row with label and text field
        userPanel = new JPanel(new BorderLayout(10, 0));
        userLabel = new JLabel("Email:");
        emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(200, 25));
        userPanel.add(userLabel, BorderLayout.WEST);
        userPanel.add(emailField, BorderLayout.CENTER);

        // password row with label and text field
        passPanel = new JPanel(new BorderLayout(10, 0));
        passLabel = new JLabel("Password:");
        passwordField = new JPasswordField(); // masked for security
        passwordField.setPreferredSize(new Dimension(200, 25));
        passPanel.add(passLabel, BorderLayout.WEST);
        passPanel.add(passwordField, BorderLayout.CENTER);
        formPanel.add(userPanel);
        formPanel.add(Box.createVerticalStrut(10)); // spacing horizontally
        formPanel.add(passPanel);

        frame.add(formPanel, BorderLayout.CENTER);

        // button panel for the buttons
        backButton = new JButton("Back");
        loginButton = new JButton("Log In");
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(backButton);
        buttonPanel.add(loginButton);

        southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        southPanel.add(buttonPanel);

        frame.add(southPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    public boolean validateInputs(){
        String email = emailField.getText().trim();

        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", email)) {
            warningLabel.setText("Please enter a valid email address.");
            return false;
        }

        return true;
    }

    public JButton getBackButton(){
        return backButton;
    }

    public JButton getLoginButton(){
        return loginButton;
    }

    public JFrame getFrame(){
        return frame;
    }

    public String getEmail(){
        return emailField.getText();
    }

    public String getPassword(){
        return new String(passwordField.getPassword());
    }
}