import javax.swing.*;
import java.awt.*;

public class AdminACityGroupUpdateView {
    private JFrame frame;
    private JTextField feeField;
    private JTextField timeField;
    private JButton updateButton;
    private JButton cancelButton;
    private JButton toggleAvailabilityButton;
    private JLabel titleLabel;
    private JLabel availabilityLabel;

    public AdminACityGroupUpdateView() {
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Update City Group");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(450, 450);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Title
        titleLabel = new JLabel("Update City Group", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(230, 0, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 15, 20));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel feeLabel = new JLabel("Delivery Fee (₱):");
        feeLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        feeField = new JTextField();
        feeField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        feeField.setPreferredSize(new Dimension(feeField.getPreferredSize().width, 35));
        
        JLabel timeLabel = new JLabel("Delivery Time (minutes):");
        timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        timeField = new JTextField();
        timeField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        timeField.setPreferredSize(new Dimension(timeField.getPreferredSize().width, 35));
        
        JLabel availabilityTextLabel = new JLabel("Status:");
        availabilityTextLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        availabilityLabel = new JLabel();
        availabilityLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        formPanel.add(feeLabel);
        formPanel.add(feeField);
        formPanel.add(timeLabel);
        formPanel.add(timeField);
        formPanel.add(availabilityTextLabel);
        formPanel.add(availabilityLabel);
        formPanel.add(new JLabel());
        formPanel.add(new JLabel());

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        // Availability toggle button
        JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        togglePanel.setBackground(Color.WHITE);
        toggleAvailabilityButton = new JButton();
        toggleAvailabilityButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        toggleAvailabilityButton.setFocusPainted(false);
        toggleAvailabilityButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        togglePanel.add(toggleAvailabilityButton);
        
        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        actionPanel.setBackground(Color.WHITE);
        
        updateButton = new JButton("Update Group");
        cancelButton = new JButton("Cancel");
        
        JButton[] buttons = {updateButton, cancelButton};
        for (JButton btn : buttons) {
            btn.setFont(new Font("SansSerif", Font.BOLD, 14));
            btn.setBackground(new Color(255, 180, 180));
            btn.setForeground(Color.BLACK);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        }
        
        actionPanel.add(updateButton);
        actionPanel.add(cancelButton);
        
        buttonPanel.add(togglePanel);
        buttonPanel.add(actionPanel);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
    }

    public void setGroupData(CityGroup group) {
        titleLabel.setText("Update Group " + group.getId());
        feeField.setText(String.format("%.2f", group.getDeliveryFee()));
        timeField.setText(String.valueOf(group.getDeliveryTime()));
        
        updateAvailabilityDisplay(group.isAvailable());
    }

    private void updateAvailabilityDisplay(boolean isAvailable) {
        if (isAvailable) {
            availabilityLabel.setText("Available");
            availabilityLabel.setForeground(new Color(0, 150, 0));
            toggleAvailabilityButton.setText("Disable Group");
            toggleAvailabilityButton.setBackground(new Color(255, 100, 100));
            toggleAvailabilityButton.setForeground(Color.WHITE);
        } else {
            availabilityLabel.setText("Unavailable");
            availabilityLabel.setForeground(Color.RED);
            toggleAvailabilityButton.setText("Enable Group");
            toggleAvailabilityButton.setBackground(new Color(100, 200, 100));
            toggleAvailabilityButton.setForeground(Color.WHITE);
        }
    }

    public void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(frame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Getters
    public JFrame getFrame() {
        return frame;
    }

    public JButton getUpdateButton() {
        return updateButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JButton getToggleAvailabilityButton() {
        return toggleAvailabilityButton;
    }

    public String getFee() {
        return feeField.getText().trim();
    }

    public String getTime() {
        return timeField.getText().trim();
    }
}