import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AdminViewProductsView {
    private JFrame frame;
    private JPanel headerPanel, productPanel, footerPanel;
    private JButton logoutButton, settingsButton, addButton, backButton;
    private JLabel logoLabel, titleLabel;
    private List<JButton> productButtons = new ArrayList<>();
    private List<JLabel> availabilityLabels = new ArrayList<>();

    public AdminViewProductsView() {
        frame = new JFrame("Admin - Manage Products");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        // ================= HEADER ==================
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        headerPanel.setBackground(Color.WHITE);

        // Left: Logo
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logoPanel.setBackground(Color.WHITE);
        logoLabel = new JLabel("Logo of Incorporation");
        logoLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        logoLabel.setForeground(new Color(230, 0, 0));
        logoPanel.add(logoLabel);

        // Right: Navigation buttons - USING CUSTOMER NAV BAR STYLE
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 0));
        navPanel.setBackground(Color.WHITE);

        // Apply customer nav bar font style to settings and logout buttons
        settingsButton = makeNavButton("Settings");
        logoutButton = makeNavButton("Log Out");

        navPanel.add(settingsButton);
        navPanel.add(logoutButton);

        headerPanel.add(logoPanel, BorderLayout.WEST);
        headerPanel.add(navPanel, BorderLayout.EAST);

        frame.add(headerPanel, BorderLayout.NORTH);

        // ================= TITLE ==================
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        
        titleLabel = new JLabel("Menu Management");
        titleLabel.setFont(new Font("Helvetica Neue", Font.BOLD, 32));
        titleLabel.setForeground(new Color(230, 0, 0));
        titlePanel.add(titleLabel);

        // ================= PRODUCTS PANEL ==================
        productPanel = new JPanel();
        productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.Y_AXIS));
        productPanel.setBackground(Color.WHITE);
        productPanel.setBorder(BorderFactory.createEmptyBorder(10, 80, 10, 80));
        
        JScrollPane scrollPane = new JScrollPane(productPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // ================= FOOTER ==================
        footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 40, 0));

        // Apply "Add Order" button style to Back button
        backButton = new JButton("Back to Home");
        backButton.setFont(new Font("Helvetica Neue", Font.BOLD, 18));
        backButton.setForeground(Color.BLACK);
        backButton.setBackground(new Color(255, 182, 182));
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(12, 40, 12, 40));

        // Add Product button - same prominent style
        addButton = new JButton("Add New Product");
        addButton.setFont(new Font("Helvetica Neue", Font.BOLD, 18));
        addButton.setForeground(Color.BLACK);
        addButton.setBackground(new Color(255, 182, 182));
        addButton.setFocusPainted(false);
        addButton.setBorder(BorderFactory.createEmptyBorder(12, 40, 12, 40));

        footerPanel.add(backButton);
        footerPanel.add(addButton);

        // Add components to frame
        frame.add(titlePanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(footerPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    /**
     * Creates navigation buttons with customer nav bar font style
     */
    private JButton makeNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setForeground(new Color(230, 0, 0));
        return button;
    }

    public void displayProducts(List<MenuProduct> products){
        productPanel.removeAll();
        productButtons.clear();
        availabilityLabels.clear();

        if (products.isEmpty()) {
            JLabel emptyLabel = new JLabel("No menus found. Click 'Add New Product' to get started.");
            emptyLabel.setFont(new Font("Helvetica Neue", Font.PLAIN, 16));
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyLabel.setForeground(Color.GRAY);
            productPanel.add(emptyLabel);
        } else {
            for (MenuProduct p : products){
                JPanel row = createProductRow(p);
                productPanel.add(row);
                productPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacing between rows
            }
        }

        productPanel.revalidate();
        productPanel.repaint();
    }

    private JPanel createProductRow(MenuProduct product) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        row.setBackground(Color.WHITE);
        // REMOVED: row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        // Let the row size be determined by its content naturally

        // Product info on the left - using separate labels for each field
        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        infoPanel.setBackground(Color.WHITE);
        
        // Product Name - with proper vertical spacing
        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        nameLabel.setForeground(new Color(60, 60, 60));
        
        // Price with label
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pricePanel.setBackground(Color.WHITE);
        JLabel priceLabel = new JLabel("Price:");
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        priceLabel.setForeground(Color.DARK_GRAY);
        JLabel priceValue = new JLabel(String.format("$%.2f", product.getPrice()));
        priceValue.setFont(new Font("SansSerif", Font.PLAIN, 12));
        priceValue.setForeground(Color.BLACK);
        pricePanel.add(priceLabel);
        pricePanel.add(priceValue);
        
        // Preparation Time with label
        JPanel prepTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        prepTimePanel.setBackground(Color.WHITE);
        JLabel prepTimeLabel = new JLabel("Preparation Time:");
        prepTimeLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        prepTimeLabel.setForeground(Color.DARK_GRAY);
        JLabel prepTimeValue = new JLabel(product.getPrepTime());
        prepTimeValue.setFont(new Font("SansSerif", Font.PLAIN, 12));
        prepTimeValue.setForeground(Color.BLACK);
        prepTimePanel.add(prepTimeLabel);
        prepTimePanel.add(prepTimeValue);
        
        // Description with label - improved layout for better text display
        JPanel descPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        descPanel.setBackground(Color.WHITE);
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        descLabel.setForeground(Color.DARK_GRAY);
        String description = product.getDescription().length() > 60 ? 
            product.getDescription().substring(0, 60) + "..." : 
            product.getDescription();
        JLabel descValue = new JLabel("<html>" + description + "</html>"); // Use HTML for better text wrapping
        descValue.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descValue.setForeground(Color.BLACK);
        descPanel.add(descLabel);
        descPanel.add(descValue);
        
        infoPanel.add(nameLabel);
        infoPanel.add(pricePanel);
        infoPanel.add(prepTimePanel);
        infoPanel.add(descPanel);

        // Buttons panel on the right
        JPanel buttonsPanel = new JPanel(new GridLayout(3, 1, 8, 8));
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.setPreferredSize(new Dimension(120, 100));

        JButton editButton = new JButton("Edit");
        JLabel availability = new JLabel(
            product.isAvailable() ? "Available" : "Unavailable",
            SwingConstants.CENTER
        );
        
        // Style buttons and availability label
        editButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        editButton.setBackground(new Color(200, 220, 255));
        editButton.setFocusPainted(false);
        editButton.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        
        availability.setFont(new Font("SansSerif", Font.BOLD, 12));
        availability.setOpaque(true);
        availability.setBackground(product.isAvailable() ? new Color(220, 255, 220) : new Color(255, 220, 220));
        availability.setForeground(product.isAvailable() ? new Color(0, 100, 0) : new Color(150, 0, 0));
        availability.setCursor(new Cursor(Cursor.HAND_CURSOR));
        availability.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(product.isAvailable() ? Color.GREEN : Color.RED, 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        buttonsPanel.add(editButton);
        buttonsPanel.add(availability);

        row.add(infoPanel, BorderLayout.CENTER);
        row.add(buttonsPanel, BorderLayout.EAST);

        // Store references for controller
        productButtons.add(editButton);
        availabilityLabels.add(availability);

        return row;
    }

    public JFrame getFrame(){
        return frame;
    }

    public JButton getLogoutButton(){
        return logoutButton;
    }

    public JButton getSettingsButton(){
        return settingsButton;
    }

    public JButton getAddButton(){
        return addButton;
    }

    public JButton getBackButton(){
        return backButton;
    }

    public List<JButton> getProductButtons(){
        return productButtons;
    }

    public List<JLabel> getAvailabilityLabels(){
        return availabilityLabels;
    }

    public JLabel getTitleLabel() {
        return titleLabel;
    }
}