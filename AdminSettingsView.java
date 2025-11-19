import javax.swing.*;
import java.awt.*;

/**
 * Displays the Admin Settings Page of the Restaurant App.
 */
public class AdminSettingsView {
    private JFrame frame;
    private JPanel headerPanel, formPanel;
    private JButton logoutButton, changePasswordButton;
    private JButton confirmButton, backButton, deactivateButton;
    private JTextField firstNameField, lastNameField, emailField;
    private JLabel logoLabel, warningLabel;

    /**
     * Constructor for AdminSettingsView class.
     */
    public AdminSettingsView() {
        frame = new JFrame("Admin Settings Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 450);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        // ===== HEADER =====
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        logoLabel = new JLabel("Logo of Restaurant");
        
        logoutButton = new JButton("Log out");
        changePasswordButton = new JButton("Change Password");

        JPanel rightHeaderPanel = new JPanel();
        rightHeaderPanel.add(logoutButton);
        rightHeaderPanel.add(changePasswordButton);

        headerPanel.add(logoLabel, BorderLayout.WEST);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);
        frame.add(headerPanel, BorderLayout.NORTH);

        // ===== BODY / FORM =====
        formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        warningLabel = new JLabel("", SwingConstants.CENTER);
        warningLabel.setForeground(Color.RED);
        warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(warningLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        firstNameField = new JTextField();
        addField(formPanel, "First Name:", firstNameField);

        lastNameField = new JTextField();
        addField(formPanel, "Last Name:", lastNameField);

        emailField = new JTextField();
        addField(formPanel, "Email:", emailField);

        // Main action buttons
        confirmButton = new JButton("Confirm");
        backButton = new JButton("Back");
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonsPanel.add(confirmButton);
        buttonsPanel.add(backButton);
        formPanel.add(buttonsPanel);

        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Deactivate button (separated for safety)
        deactivateButton = new JButton("Deactivate Account");
        deactivateButton.setForeground(Color.WHITE);
        deactivateButton.setBackground(Color.RED);
        deactivateButton.setOpaque(true);
        deactivateButton.setBorderPainted(false);
        deactivateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel deactivatePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        deactivatePanel.add(deactivateButton);
        formPanel.add(deactivatePanel);

        frame.add(formPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void addField(JPanel panel, String label, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.add(new JLabel(label), BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        panel.add(row);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
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

    public JButton getConfirm() {
        return confirmButton;
    }

    public JButton getBackButton() {
        return backButton;
    }

    public JButton getChangePassword() {
        return changePasswordButton;
    }

    public JButton getLogoutButton() {
        return logoutButton;
    }

    public JButton getDeactivateButton() {
        return deactivateButton;
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