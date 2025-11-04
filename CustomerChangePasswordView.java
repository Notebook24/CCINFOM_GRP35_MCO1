import javax.swing.*;
import java.awt.*;

/**
 * Designs the Change Password Page in line with its controller.
 */
public class CustomerChangePasswordView {
    private JFrame frame;
    private JPanel headerPanel, formPanel;
    private JButton logoutButton, settingsButton;
    private JButton confirmButton, backButton;
    private JPasswordField oldPasswordField, newPasswordField, confirmPasswordField;
    private JLabel logoLabel, warningLabel;

    /**
     * Constructor for change password view class
     */
    public CustomerChangePasswordView() {
        frame = new JFrame("Change Password");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 350);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        logoLabel = new JLabel("Logo of Incorporation");
        logoutButton = new JButton("Log out");
        settingsButton = new JButton("Back");

        JPanel rightHeaderPanel = new JPanel();
        rightHeaderPanel.add(logoutButton);
        rightHeaderPanel.add(settingsButton);

        headerPanel.add(logoLabel, BorderLayout.WEST);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);
        frame.add(headerPanel, BorderLayout.NORTH);

        formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        warningLabel = new JLabel("", SwingConstants.CENTER);
        warningLabel.setForeground(Color.RED);
        warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(warningLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        oldPasswordField = new JPasswordField();
        addField(formPanel, "Old Password:", oldPasswordField);

        newPasswordField = new JPasswordField();
        addField(formPanel, "New Password:", newPasswordField);

        confirmPasswordField = new JPasswordField();
        addField(formPanel, "Confirm Password:", confirmPasswordField);

        confirmButton = new JButton("Confirm");
        backButton = new JButton("Back");
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonsPanel.add(confirmButton);
        buttonsPanel.add(backButton);
        formPanel.add(buttonsPanel);

        frame.add(formPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void addField(JPanel panel, String label, JPasswordField field){
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.add(new JLabel(label), BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        panel.add(row);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    public boolean validateInputs(){
        String oldPass = getOldPassword();
        String newPass = getNewPassword();
        String confirmPass = getConfirmPassword();

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()){
            warningLabel.setText("Please fill out all fields.");
            return false;
        }

        if (newPass.equals(oldPass)){
            warningLabel.setText("New password must be different from old password.");
            return false;
        }

        if (!newPass.equals(confirmPass)){
            warningLabel.setText("New passwords do not match.");
            return false;
        }

        warningLabel.setText("");
        return true;
    }

    public JFrame getFrame(){
        return frame;
    }

    public JButton getConfirm(){
        return confirmButton;
    }

    public JButton getBackButton(){
        return backButton;
    }

    public JButton getLogoutButton(){
        return logoutButton;
    }

    public JButton getSettingsButton(){
        return settingsButton;
    }

    public String getOldPassword(){
        return new String(oldPasswordField.getPassword());
    }

    public String getNewPassword(){
        return new String(newPasswordField.getPassword());
    }

    public String getConfirmPassword(){
        return new String(confirmPasswordField.getPassword());
    }

    public JLabel getWarningLabel(){
        return warningLabel;
    }
}
