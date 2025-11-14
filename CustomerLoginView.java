import javax.swing.*;
import java.awt.*;
import java.util.regex.Pattern;

/**
 * Designs the Log in page in line with its controller.
 * SAME UI as Admin Login View
 */
public class CustomerLoginView {
    private JFrame frame;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton backButton, loginButton;
    private JLabel warningLabel, userLabel, passLabel;
    private JPanel formPanel, userPanel, passPanel, buttonPanel, southPanel, headerPanel;

    /**
     * Constructor for Sign Up View class.
     */
    public CustomerLoginView() {
        // frame config
        frame = new JFrame("Log In");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(Color.WHITE);

        // header panel for logo and branding
        headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // logo
        JLabel logoLabel = new JLabel();
        ImageIcon logoIcon = new ImageIcon("design_images/koreanexpress-logo.png");
        Image scaledLogo = logoIcon.getImage().getScaledInstance(250, 120, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(scaledLogo));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // add logo to header panel and apply spacing
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(logoLabel);
        headerPanel.add(Box.createVerticalStrut(20));

        // add header panel to frame
        frame.add(headerPanel, BorderLayout.NORTH);

        // form panel or the central panel for login credentials
        formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40)); // padding
        formPanel.setBackground(Color.WHITE);

        // warning label for invalid inputs
        warningLabel = new JLabel("", SwingConstants.CENTER);
        warningLabel.setForeground(Color.RED);
        warningLabel.setFont(new Font("Helvetica Neue", Font.PLAIN, 13));
        warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(warningLabel);
        formPanel.add(Box.createVerticalStrut(10));

        // email row with label and text field
        userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBackground(Color.WHITE);

        userLabel = new JLabel("Email Address");
        userLabel.setFont(new Font("Helvetica Neue", Font.BOLD, 13));
        userLabel.setForeground(new Color(198, 40, 40));
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(200, 30));
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        emailField.setFont(new Font("Helvetica Neue", Font.PLAIN, 13));
        emailField.setBorder(BorderFactory.createLineBorder(new Color(198, 40, 40), 1));

        userPanel.add(userLabel);
        userPanel.add(Box.createVerticalStrut(5));
        userPanel.add(emailField);

        // password row with label and text field
        passPanel = new JPanel();
        passPanel.setLayout(new BoxLayout(passPanel, BoxLayout.Y_AXIS));
        passPanel.setBackground(Color.WHITE);

        passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Helvetica Neue", Font.BOLD, 13));
        passLabel.setForeground(new Color(198, 40, 40));
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // password field with masking
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 30));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        passwordField.setFont(new Font("Helvetica Neue", Font.PLAIN, 13));
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(198, 40, 40), 1));

        // add components to password panel
        passPanel.add(passLabel);
        passPanel.add(Box.createVerticalStrut(5));
        passPanel.add(passwordField);

        // add user and password panels to form panel with spacing
        formPanel.add(userPanel);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(passPanel);

        // add spacing before buttons
        frame.add(formPanel, BorderLayout.CENTER);

        // button panel for the buttons
        backButton = new JButton("Back");
        loginButton = new JButton("Log In");

        Dimension buttonSize = new Dimension(180, 40);
        JButton[] buttons = {loginButton, backButton};
        for (JButton button : buttons) {
            button.setFocusPainted(false);
            button.setFont(new Font("Helvetica Neue", Font.BOLD, 14));
            button.setPreferredSize(buttonSize);
            button.setMaximumSize(buttonSize);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setForeground(Color.BLACK);
        }

        loginButton.setBackground(new Color(255, 138, 128)); //red 
        backButton.setBackground(new Color(255, 171, 145)); // orange

        // button panel with the buttons added
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // add buttons to button panel with spacing
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(backButton);

        // south panel to hold button panel
        southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        southPanel.setBackground(Color.WHITE);

        // add button panel to south panel
        southPanel.add(buttonPanel);

        // add south panel to frame
        frame.add(southPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public boolean validateInputs() {
        String email = emailField.getText().trim();

        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", email)) {
            warningLabel.setText("Please enter a valid email address.");
            return false;
        }

        return true;
    }

    public JButton getBackButton() {
        return backButton;
    }

    public JButton getLoginButton() {
        return loginButton;
    }

    public JFrame getFrame() {
        return frame;
    }

    public String getEmail() {
        return emailField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }
}
