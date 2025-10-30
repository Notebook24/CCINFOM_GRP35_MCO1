import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Designs the Log in page of the Calendar App in line with its controller
 */
public class AccountLoginView{
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton backButton, loginButton;
    private JLabel messageLabel, userLabel, passLabel;
    private JPanel formPanel, userPanel, passPanel, buttonPanel, southPanel;

    /**
     * Constructor for LoginView class
     */
    public AccountLoginView() {
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

        // username row with label and text field
        userPanel = new JPanel(new BorderLayout(10, 0));
        userLabel = new JLabel("Username:");
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(200, 25));
        userPanel.add(userLabel, BorderLayout.WEST);
        userPanel.add(usernameField, BorderLayout.CENTER);

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

        // feedback message labels
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setForeground(Color.RED);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        southPanel.add(buttonPanel);
        southPanel.add(Box.createVerticalStrut(5)); // separation between button panel and the feedback message
        southPanel.add(messageLabel);

        frame.add(southPanel, BorderLayout.SOUTH);
    }
}