// AdminAddCityView.java
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminAddCityView {
    private JFrame frame;
    private JTextField cityNameField;
    private JComboBox<String> groupComboBox;
    private JButton addButton;
    private JButton cancelButton;
    private List<CityGroup> cityGroups;

    public AdminAddCityView(List<CityGroup> cityGroups) {
        this.cityGroups = cityGroups;
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Add City");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 350);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Add New City");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
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
        
        addButton = new JButton("Add City");
        cancelButton = new JButton("Cancel");
        
        JButton[] buttons = {addButton, cancelButton};
        for (JButton btn : buttons) {
            btn.setFont(new Font("SansSerif", Font.BOLD, 14));
            btn.setBackground(new Color(255, 180, 180));
            btn.setForeground(Color.BLACK);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        }
        
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
    }

    public void clearFields() {
        cityNameField.setText("");
        groupComboBox.setSelectedIndex(0);
    }

    public void showSuccessMessage() {
        JOptionPane.showMessageDialog(frame, "City successfully added!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Getters
    public JFrame getFrame() {
        return frame;
    }

    public JButton getAddButton() {
        return addButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public String getCityName() {
        return cityNameField.getText().trim();
    }
    
    public int getSelectedGroupId() { 
        return cityGroups.get(groupComboBox.getSelectedIndex()).getId(); 
    }
}