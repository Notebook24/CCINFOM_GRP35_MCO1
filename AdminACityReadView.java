// AdminACityReadView.java
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminACityReadView {
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel contentPanel;
    private JScrollPane scrollPane;
    private JButton addButton;
    private JButton backButton;
    private JLabel titleLabel;
    
    // Components for dynamic buttons (will be accessed by controller)
    private java.util.List<JButton> updateButtons;
    private java.util.List<City> currentCities;
    
    public AdminACityReadView() {
        initializeUI();
        updateButtons = new java.util.ArrayList<>();
        currentCities = new java.util.ArrayList<>();
    }
    
    private void initializeUI() {
        frame = new JFrame("Manage Cities");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        
        backButton = new JButton("← Back to Groups");
        backButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        backButton.setBackground(Color.LIGHT_GRAY);
        backButton.setFocusPainted(false);
        
        titleLabel = new JLabel("Cities in Group", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(230, 0, 0));
        
        titlePanel.add(backButton, BorderLayout.WEST);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Content panel for cities
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        
        scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        addButton = new JButton("Add New City");
        addButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        addButton.setBackground(new Color(255, 180, 180));
        addButton.setForeground(Color.BLACK);
        addButton.setFocusPainted(false);
        addButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        buttonPanel.add(addButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        frame.add(mainPanel);
    }
    
    public void displayCities(List<City> cities, String groupInfo) {
        contentPanel.removeAll();
        updateButtons.clear();
        currentCities.clear();
        currentCities.addAll(cities);
        
        titleLabel.setText("Cities in " + groupInfo);
        
        if (cities.isEmpty()) {
            JLabel emptyLabel = new JLabel("No cities found in this group.");
            emptyLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(emptyLabel);
        } else {
            for (int i = 0; i < cities.size(); i++) {
                City city = cities.get(i);
                JPanel cityPanel = createCityPanel(city, i);
                contentPanel.add(cityPanel);
                contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private JPanel createCityPanel(City city, int index) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        // City info on the left
        JLabel infoLabel = new JLabel(city.getName());
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        
        // Buttons panel on the right
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(Color.WHITE);
        
        JButton updateButton = new JButton("Update");
        
        // Style buttons
        JButton[] buttons = {updateButton};
        for (JButton btn : buttons) {
            btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
            btn.setBackground(Color.LIGHT_GRAY);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        }
        
        buttonsPanel.add(updateButton);
        
        panel.add(infoLabel, BorderLayout.WEST);
        panel.add(buttonsPanel, BorderLayout.EAST);
        
        // Store buttons for controller to access
        updateButtons.add(updateButton);
        
        return panel;
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
    
    public JButton getAddButton() { 
        return addButton; 
    }
    
    public JButton getBackButton() { 
        return backButton; 
    }
    
    public java.util.List<JButton> getUpdateButtons() {
        return updateButtons;
    }
    
    public java.util.List<City> getCurrentCities() {
        return currentCities;
    }
    
    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }
}