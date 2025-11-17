// AdminACityUpdateView.java
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminACityUpdateView {
    private JFrame frame;
    private JTextField cityNameField;
    private JComboBox<String> groupComboBox;
    private JButton updateButton;
    private JButton cancelButton;
    private JLabel titleLabel;
    private List<CityGroup> cityGroups;

    public AdminACityUpdateView(List<CityGroup> cityGroups) {
        this.cityGroups = cityGroups;
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Update City");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 350);
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
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        JLabel nameLabel = new JLabel("City Name:");
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        cityNameField = new JTextField();
        cityNameField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        
        JLabel groupLabel = new JLabel("City Group:");
        groupLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        
        // Create combo box with group options
        String[] groupOptions = new String[cityGroups.size()];
        for (int i = 0; i < cityGroups.size(); i++) {
            CityGroup group = cityGroups.get(i);
            groupOptions[i] = String.format("Group %d (₱%.2f, %d mins)", 
                group.getId(), group.getDeliveryFee(), group.getDeliveryTime());
        }
        
        groupComboBox = new JComboBox<>(groupOptions);
        groupComboBox.setFont(new Font("SansSerif", Font.PLAIN, 16));

        formPanel.add(nameLabel);
        formPanel.add(cityNameField);
        formPanel.add(groupLabel);
        formPanel.add(groupComboBox);
        formPanel.add(new JLabel()); // Empty cell
        formPanel.add(new JLabel()); // Empty cell

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        
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
        
        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
    }

    public void setCityData(City city) {
        titleLabel.setText("Update City: " + city.getName());
        cityNameField.setText(city.getName());
        
        // Find and select the current group in the combo box
        for (int i = 0; i < cityGroups.size(); i++) {
            if (cityGroups.get(i).getId() == city.getGroupId()) {
                groupComboBox.setSelectedIndex(i);
                break;
            }
        }
    }

    public void showSuccessMessage() {
        JOptionPane.showMessageDialog(frame, "City details successfully updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
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

    public String getCityName() {
        return cityNameField.getText().trim();
    }

    public int getSelectedGroupId(){ 
        return cityGroups.get(groupComboBox.getSelectedIndex()).getId(); 
    }
}