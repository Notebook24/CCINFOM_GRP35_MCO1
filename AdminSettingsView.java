import javax.swing.*;
import java.awt.*;

/**
 * Displays the Admin Settings Page of the Restaurant App.
 */
public class AdminSettingsView {
    private JFrame frame;
    private JPanel headerPanel, logoPanel, navPanel;
    private JButton profileButton, logoutButton;
    private JButton backButton, confirmButton, changePasswordButton, deleteButton;
    private JTextField firstNameField, lastNameField, emailField;
    private JLabel warningLabel, logoLabel;

    /**
     * Constructor for AdminSettingsView class.
     */
    public AdminSettingsView() {
        frame = new JFrame("Admin Settings");
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
        // INPUT FIELDS - LABELS BESIDE FIELDS
        //------------------------------------
        firstNameField = createTextField();
        lastNameField = createTextField();
        emailField = createTextField();

        centerPanel.add(createHorizontalField("First Name", firstNameField));
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(createHorizontalField("Last Name", lastNameField));
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(createHorizontalField("Email Address", emailField));
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        //------------------------------------
        // BUTTONS
        //------------------------------------
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnRow.setBackground(Color.WHITE);

        confirmButton = createRoundedButton("Confirm Profile");
        changePasswordButton = createRoundedButton("Change Password");
        deleteButton = createRoundedButton("Delete Account");
        deleteButton.setForeground(new Color(140, 0, 0));

        confirmButton.setPreferredSize(new Dimension(180, 40));
        changePasswordButton.setPreferredSize(new Dimension(180, 40));
        deleteButton.setPreferredSize(new Dimension(180, 40));

        btnRow.add(confirmButton);
        btnRow.add(changePasswordButton);

        centerPanel.add(btnRow);

        JPanel deleteRow = new JPanel();
        deleteRow.setBackground(Color.WHITE);
        deleteRow.add(deleteButton);
        centerPanel.add(deleteRow);

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
        String email = emailField.getText().trim();

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            warningLabel.setText("Please enter a valid email address.");
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

    public String getFirstName() {
        return firstNameField.getText().trim();
    }

    public String getLastName() {
        return lastNameField.getText().trim();
    }

    public String getEmail() {
        return emailField.getText().trim();
    }

    public JButton getConfirmButton() {
        return confirmButton;
    }

    public JButton getBackButton() {
        return backButton;
    }

    public JButton getChangePasswordButton() {
        return changePasswordButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }

    public JButton getProfileButton() {
        return profileButton;
    }

    public JButton getLogoutButton() {
        return logoutButton;
    }

    public JTextField getFirstNameField() {
        return firstNameField;
    }

    public JTextField getLastNameField() {
        return lastNameField;
    }

    public JTextField getEmailField() {
        return emailField;
    }

    public JLabel getWarningLabel() {
        return warningLabel;
    }
}