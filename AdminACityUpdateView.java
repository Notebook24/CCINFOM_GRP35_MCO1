import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminACityUpdateView {
    private JFrame frame;
    private JTextField cityNameField;
    private JComboBox<String> groupComboBox;
    private JButton updateButton;
    private JButton cancelButton;
    private JButton toggleAvailabilityButton;
    private JLabel titleLabel;
    private JLabel availabilityLabel;
    private List<CityGroup> cityGroups;

    public AdminACityUpdateView(List<CityGroup> cityGroups) {
        this.cityGroups = cityGroups;
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Update City");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 450);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Title
        titleLabel = new JLabel("Update City", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(230, 0, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        JLabel nameLabel = new JLabel("City Name:");
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        cityNameField = new JTextField();
        cityNameField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        
        JLabel groupLabel = new JLabel("City Group:");
        groupLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        
        // Create combo box with group options
        groupComboBox = new JComboBox<>();
        groupComboBox.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JLabel availabilityTextLabel = new JLabel("Status:");
        availabilityTextLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        availabilityLabel = new JLabel();
        availabilityLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        formPanel.add(nameLabel);
        formPanel.add(cityNameField);
        formPanel.add(groupLabel);
        formPanel.add(groupComboBox);
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
        
        updateButton = new JButton("Update City");
        cancelButton = new JButton("Cancel");
        
        JButton[] buttons = {updateButton, cancelButton};
        for (JButton btn : buttons) {
            btn.setFont(new Font("SansSerif", Font.BOLD, 14));
            btn.setBackground(new Color(255, 180, 180));
            btn.setForeground(Color.BLACK);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        }
        
        actionPanel.add(updateButton);
        actionPanel.add(cancelButton);
        
        buttonPanel.add(togglePanel);
        buttonPanel.add(actionPanel);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
    }

    public void setCityData(City city, List<CityGroup> cityGroups) {
        titleLabel.setText("Update City: " + city.getName());
        cityNameField.setText(city.getName());
        
        // Populate group combo box
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        for (CityGroup group : cityGroups) {
            String status = group.isAvailable() ? "Available" : "Unavailable";
            String groupText = String.format("Group %d (₱%.2f, %d mins) - %s", 
                group.getId(), group.getDeliveryFee(), group.getDeliveryTime(), status);
            model.addElement(groupText);
        }
        groupComboBox.setModel(model);
        
        // Find and select the current group in the combo box
        for (int i = 0; i < cityGroups.size(); i++) {
            if (cityGroups.get(i).getId() == city.getGroupId()) {
                groupComboBox.setSelectedIndex(i);
                break;
            }
        }
        
        updateAvailabilityDisplay(city.isAvailable());
    }

    private void updateAvailabilityDisplay(boolean isAvailable) {
        if (isAvailable) {
            availabilityLabel.setText("Available");
            availabilityLabel.setForeground(new Color(0, 150, 0));
            toggleAvailabilityButton.setText("Disable City");
            toggleAvailabilityButton.setBackground(new Color(255, 100, 100));
            toggleAvailabilityButton.setForeground(Color.WHITE);
        } else {
            availabilityLabel.setText("Unavailable");
            availabilityLabel.setForeground(Color.RED);
            toggleAvailabilityButton.setText("Enable City");
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

    public String getCityName() {
        return cityNameField.getText().trim();
    }

    public int getSelectedGroupId() { 
        return cityGroups.get(groupComboBox.getSelectedIndex()).getId(); 
    }
}