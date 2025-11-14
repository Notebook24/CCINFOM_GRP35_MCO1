import javax.swing.*;
import java.awt.*;
import java.util.regex.Pattern;

/**
 * Designs the Sign up page in line with its controller
 * ALMOST the same as CustomerSignUpView.java but without address field
 */
public class AdminSignUpView {
    private JFrame frame;
    private JTextField firstNameField, lastNameField, emailField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton backButton, signUpButton;
    private JLabel warningLabel;

    private JPanel headerPanel, formPanel, buttonPanel, mainPanel;

    /**
     * Constructor for Admin Sign Up View class
     */
    public AdminSignUpView() {
        // === FRAME CONFIG ===
        frame = new JFrame("Sign Up");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 650);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.getContentPane().setBackground(Color.WHITE);
        frame.setLayout(new BorderLayout());

        // header panel
        headerPanel = new JPanel();
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // logo
        ImageIcon logoIcon = new ImageIcon("design_images/koreanexpress-logo.png");
        Image scaledLogo = logoIcon.getImage().getScaledInstance(200, 100, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(logoLabel);
        headerPanel.add(Box.createVerticalStrut(15));

        // main form panel
        formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        formPanel.setBackground(Color.WHITE);

        warningLabel = new JLabel("", SwingConstants.CENTER);
        warningLabel.setForeground(Color.RED);
        warningLabel.setFont(new Font("Helvetica Neue", Font.PLAIN, 13));
        warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(warningLabel);
        formPanel.add(Box.createVerticalStrut(10));

        // Input fields via addField and addPasswordField helper methods
        addField(formPanel, "First Name", firstNameField = new JTextField());
        addField(formPanel, "Last Name", lastNameField = new JTextField());
        addField(formPanel, "Email Address", emailField = new JTextField());
        addPasswordField(formPanel, "Password", passwordField = new JPasswordField());
        addPasswordField(formPanel, "Confirm Password", confirmPasswordField = new JPasswordField());

        // button pannel
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        Dimension buttonSize = new Dimension(180, 40);

        backButton = new JButton("Back");
        signUpButton = new JButton("Sign Up");

        JButton[] buttons = {backButton, signUpButton};
        for (JButton btn : buttons) {
            btn.setFont(new Font("Helvetica Neue", Font.BOLD, 14));
            btn.setPreferredSize(buttonSize);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setOpaque(true);
            btn.setForeground(Color.BLACK);
        }

        backButton.setBackground(new Color(255, 171, 145));   // orange
        signUpButton.setBackground(new Color(255, 138, 128)); // red

        buttonPanel.add(backButton);
        buttonPanel.add(signUpButton);

        // === MAIN PANEL ASSEMBLY ===
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // Helper method to avoid code duplication for text fields since there are many input fields
    private void addField(JPanel parent, String labelText, JTextField field) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setBackground(Color.WHITE);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Helvetica Neue", Font.BOLD, 13));
        label.setForeground(new Color(198, 40, 40));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setFont(new Font("Helvetica Neue", Font.PLAIN, 13));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        field.setBorder(BorderFactory.createLineBorder(new Color(198, 40, 40), 1));

        fieldPanel.add(label);
        fieldPanel.add(Box.createVerticalStrut(5));
        fieldPanel.add(field);
        fieldPanel.add(Box.createVerticalStrut(15));

        parent.add(fieldPanel);
    }

    // Helper method for password fields to avoid code duplication since there are two password fields
    private void addPasswordField(JPanel parent, String labelText, JPasswordField field) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setBackground(Color.WHITE);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Helvetica Neue", Font.BOLD, 13));
        label.setForeground(new Color(198, 40, 40));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setFont(new Font("Helvetica Neue", Font.PLAIN, 13));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        field.setBorder(BorderFactory.createLineBorder(new Color(198, 40, 40), 1));

        fieldPanel.add(label);
        fieldPanel.add(Box.createVerticalStrut(5));
        fieldPanel.add(field);
        fieldPanel.add(Box.createVerticalStrut(15));

        parent.add(fieldPanel);
    }

    // === VALIDATION LOGIC (unchanged) ===
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

    // === GETTERS ===
    public JFrame getFrame() { return frame; }
    public String getFirstName() { return firstNameField.getText().trim(); }
    public String getLastName() { return lastNameField.getText().trim(); }
    public String getPassword() { return new String(passwordField.getPassword()); }
    public String getEmail() { return emailField.getText().trim(); }
    public JButton getSignUp() { return signUpButton; }
    public JButton getBackButton() { return backButton; }
}
