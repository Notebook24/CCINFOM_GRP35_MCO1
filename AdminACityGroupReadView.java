// AdminACityGroupView.java
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class AdminACityGroupReadView {
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel contentPanel;
    private JScrollPane scrollPane;
    private JButton addButton, backButton; // ADDED backButton
    
    // Store references to dynamic buttons
    private List<JButton> viewButtons;
    private List<JButton> updateButtons;
    private List<JButton> deleteButtons;
    private List<CityGroup> currentGroups;
    
    public AdminACityGroupReadView() {
        initializeUI();
        viewButtons = new ArrayList<>();
        updateButtons = new ArrayList<>();
        deleteButtons = new ArrayList<>();
        currentGroups = new ArrayList<>();
    }
    
    private void initializeUI() {
        frame = new JFrame("Manage City Groups");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel("City Groups Management");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(230, 0, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Content panel for city groups
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        
        scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add button panel - UPDATED to include back button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Back button with same style as Add button
        backButton = new JButton("Back to Home");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        backButton.setBackground(new Color(255, 180, 180));
        backButton.setForeground(Color.BLACK);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        addButton = new JButton("Add New City Group");
        addButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        addButton.setBackground(new Color(255, 180, 180));
        addButton.setForeground(Color.BLACK);
        addButton.setFocusPainted(false);
        addButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        buttonPanel.add(backButton);
        buttonPanel.add(addButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        frame.add(mainPanel);
        frame.setVisible(true);
    }
    
    public void displayCityGroups(List<CityGroup> cityGroups) {
        contentPanel.removeAll();
        viewButtons.clear();
        updateButtons.clear();
        deleteButtons.clear();
        currentGroups.clear();
        currentGroups.addAll(cityGroups);
        
        if (cityGroups.isEmpty()) {
            JLabel emptyLabel = new JLabel("No city groups found.");
            emptyLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(emptyLabel);
        } else {
            for (int i = 0; i < cityGroups.size(); i++) {
                CityGroup group = cityGroups.get(i);
                JPanel groupPanel = createCityGroupPanel(group, i);
                contentPanel.add(groupPanel);
                contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private JPanel createCityGroupPanel(CityGroup group, int index) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        // Group info on the left
        JLabel infoLabel = new JLabel(group.toString());
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        
        // Buttons panel on the right
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(Color.WHITE);
        
        JButton viewButton = new JButton("View Cities");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        
        // Style buttons
        JButton[] buttons = {viewButton, updateButton, deleteButton};
        for (JButton btn : buttons) {
            btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
            btn.setBackground(Color.LIGHT_GRAY);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        }
        
        // Make delete button red
        deleteButton.setBackground(new Color(255, 150, 150));
        
        buttonsPanel.add(viewButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(deleteButton);
        
        panel.add(infoLabel, BorderLayout.WEST);
        panel.add(buttonsPanel, BorderLayout.EAST);
        
        // Store buttons for controller access
        viewButtons.add(viewButton);
        updateButtons.add(updateButton);
        deleteButtons.add(deleteButton);
        
        return panel;
    }
    
    public void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(frame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public int showDeleteConfirmation(String groupInfo) {
        return JOptionPane.showConfirmDialog(
            frame,
            "Are you sure to delete this group?\n" + groupInfo + 
            "\n\nAll cities under this group will be deleted as well.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
    }
    
    // Getters for all buttons
    public JFrame getFrame() { 
        return frame; 
    }
    
    public JButton getAddButton() { 
        return addButton; 
    }
    
    // ADDED getter for back button
    public JButton getBackButton() {
        return backButton;
    }
    
    public List<JButton> getViewButtons() {
        return viewButtons;
    }
    
    public List<JButton> getUpdateButtons() {
        return updateButtons;
    }
    
    public List<JButton> getDeleteButtons() {
        return deleteButtons;
    }
    
    public List<CityGroup> getCurrentGroups() {
        return currentGroups;
    }
    
    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }
}