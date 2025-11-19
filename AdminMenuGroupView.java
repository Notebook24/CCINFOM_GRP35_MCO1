// AdminMenuGroupView.java
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class AdminMenuGroupView {
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel contentPanel;
    private JScrollPane scrollPane;
    private JButton addButton, backButton;
    
    // Store references to dynamic buttons
    private List<JButton> viewButtons;
    private List<JButton> updateButtons;
    private List<JButton> deleteButtons;
    private List<MenuCategory> currentCategories;
    
    public AdminMenuGroupView() {
        initializeUI();
        viewButtons = new ArrayList<>();
        updateButtons = new ArrayList<>();
        deleteButtons = new ArrayList<>();
        currentCategories = new ArrayList<>();
    }
    
    private void initializeUI() {
        frame = new JFrame("Manage Menu Categories");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel("Menu Categories Management");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(230, 0, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Content panel for menu categories
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        
        scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(Color.WHITE);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Back button
        backButton = createStyledButton("Back to Home", new Color(255, 180, 180));
        
        addButton = createStyledButton("Add New Category", new Color(255, 180, 180));
        
        buttonPanel.add(backButton);
        buttonPanel.add(addButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        frame.add(mainPanel);
        frame.setVisible(true);
    }
    
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBackground(backgroundColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 100, 100), 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        
        // Add hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
        
        return button;
    }
    
    private JButton createActionButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(backgroundColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        // Add hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
        
        return button;
    }
    
    public void displayMenuCategories(List<MenuCategory> menuCategories) {
        contentPanel.removeAll();
        viewButtons.clear();
        updateButtons.clear();
        deleteButtons.clear();
        currentCategories.clear();
        currentCategories.addAll(menuCategories);
        
        if (menuCategories.isEmpty()) {
            JLabel emptyLabel = new JLabel("No menu categories found.");
            emptyLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
            contentPanel.add(emptyLabel);
        } else {
            for (int i = 0; i < menuCategories.size(); i++) {
                MenuCategory category = menuCategories.get(i);
                JPanel categoryPanel = createMenuCategoryPanel(category, i);
                contentPanel.add(categoryPanel);
                contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private JPanel createMenuCategoryPanel(MenuCategory category, int index) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 2),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        panel.setBackground(new Color(250, 250, 250));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120)); // Increased height for vertical layout
        
        // Add subtle shadow effect
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        // Left panel for category details (vertical layout)
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(new Color(250, 250, 250));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        // Category name
        JLabel nameLabel = new JLabel("Category: " + category.getCategoryName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        nameLabel.setForeground(new Color(60, 60, 60));
        
        // Time information
        JLabel timeLabel = new JLabel("Time: " + formatTime(category));
        timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        timeLabel.setForeground(new Color(80, 80, 80));
        
        // Availability status
        JLabel availabilityLabel = new JLabel("Available: " + (category.isAvailable() ? "Yes" : "No"));
        availabilityLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        availabilityLabel.setForeground(category.isAvailable() ? new Color(0, 150, 0) : new Color(200, 0, 0));
        
        // Add details to the details panel
        detailsPanel.add(nameLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(timeLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(availabilityLabel);
        
        // Buttons panel on the right
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBackground(new Color(250, 250, 250));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        JButton viewButton = createActionButton("View Menus", new Color(180, 220, 255));
        JButton updateButton = createActionButton("Update", new Color(255, 255, 180));
        JButton deleteButton = createActionButton("Delete", new Color(255, 180, 180));
        
        // Set preferred size for vertical buttons
        Dimension buttonSize = new Dimension(120, 35);
        viewButton.setPreferredSize(buttonSize);
        viewButton.setMaximumSize(buttonSize);
        updateButton.setPreferredSize(buttonSize);
        updateButton.setMaximumSize(buttonSize);
        deleteButton.setPreferredSize(buttonSize);
        deleteButton.setMaximumSize(buttonSize);
        
        buttonsPanel.add(viewButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        buttonsPanel.add(updateButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        buttonsPanel.add(deleteButton);
        
        panel.add(detailsPanel, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.EAST);
        
        // Store buttons for controller access
        viewButtons.add(viewButton);
        updateButtons.add(updateButton);
        deleteButtons.add(deleteButton);
        
        return panel;
    }
    
    private String formatTime(MenuCategory category) {
        // Assuming MenuCategory has getStartTime() and getEndTime() methods
        // You may need to adjust this based on your actual MenuCategory class structure
        try {
            return category.getTimeStart() + " - " + category.getTimeEnd();
        } catch (Exception e) {
            return "All Day";
        }
    }
    
    public void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(frame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public int showDeleteConfirmation(String categoryInfo) {
        return JOptionPane.showConfirmDialog(
            frame,
            "Are you sure to delete this category?\n" + categoryInfo + 
            "\n\nAll menus under this category will be deleted as well.",
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
    
    public List<MenuCategory> getCurrentCategories() {
        return currentCategories;
    }
    
    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }
}