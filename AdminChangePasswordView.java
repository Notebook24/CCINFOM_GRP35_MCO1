import javax.swing.*;
import java.awt.*;

/**
 * Designs the Admin Change Password Page in line with its controller.
 */
public class AdminChangePasswordView {
    private JFrame frame;
    private JPanel headerPanel, logoPanel, navPanel;
    private JButton profileButton, logoutButton;
    private JButton changeButton, backButton;
    private JTextField currentPasswordField, newPasswordField, confirmPasswordField;
    private JLabel warningLabel, logoLabel;

    /**
     * Constructor for AdminChangePasswordView class
     */
    public AdminChangePasswordView() {
        frame = new JFrame("Change Password");
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

        // Right: Navigation buttons - Only Profile and Logout
        navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 50, 20));
        navPanel.setBackground(Color.WHITE);

        profileButton = makeNavButton("Profile");
        logoutButton = makeNavButton("Log Out");

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
        // PASSWORD FIELDS - LABELS BESIDE FIELDS
        //------------------------------------
        currentPasswordField = createTextField();
        newPasswordField = createTextField();
        confirmPasswordField = createTextField();

        centerPanel.add(createHorizontalField("Current Password", currentPasswordField));
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(createHorizontalField("New Password", newPasswordField));
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(createHorizontalField("Confirm Password", confirmPasswordField));
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        //------------------------------------
        // BUTTONS
        //------------------------------------
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnRow.setBackground(Color.WHITE);

        changeButton = createRoundedButton("Change Password");
        changeButton.setPreferredSize(new Dimension(180, 40));

        btnRow.add(changeButton);
        centerPanel.add(btnRow);

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

    public boolean validateInputs() {
        String oldPass = getOldPassword();
        String newPass = getNewPassword();
        String confirmPass = getConfirmPassword();

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            warningLabel.setText("Please fill out all fields.");
            return false;
        }

        if (newPass.equals(oldPass)) {
            warningLabel.setText("New password must be different from old password.");
            return false;
        }

        if (!newPass.equals(confirmPass)) {
            warningLabel.setText("New passwords do not match.");
            return false;
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

    public JButton getChangeButton() {
        return changeButton;
    }

    public JButton getBackButton() {
        return backButton;
    }

    public JButton getProfileButton() {
        return profileButton;
    }

    public JButton getLogoutButton() {
        return logoutButton;
    }

    public String getOldPassword() {
        return currentPasswordField.getText().trim();
    }

    public String getNewPassword() {
        return newPasswordField.getText().trim();
    }

    public String getConfirmPassword() {
        return confirmPasswordField.getText().trim();
    }

    public JLabel getWarningLabel() {
        return warningLabel;
    }
}