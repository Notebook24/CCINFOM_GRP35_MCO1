// AdminHomePageView.java
import javax.swing.*;
import java.awt.*;

/**
 * Designs the Admin Home Page in line with its constructor.
 */
public class AdminHomePageView {
    private JFrame frame;
    private JPanel headerPanel, bodyPanel, footerPanel;
    private JPanel logoPanel, navPanel;
    public JButton logoutButton, profileButton;
    private JButton manageProductsButton, checkProfitButton;
    private JButton checkEngagementButton, checkMenuStatusButton, checkOrderStatusButton;
    private JButton manageCityGroupsButton;
    private JLabel logoLabel, footerLabel;

    /**
     * Constructor for AdminHomePageView class.
     */
    public AdminHomePageView() {
        frame = new JFrame("Admin Home Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1920, 1080);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // ================= HEADER ==================
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 100, 20, 100));
        headerPanel.setBackground(Color.WHITE);

        // Left: Logo
        logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logoPanel.setBackground(Color.WHITE);

        // Load and resize logo image safely
        ImageIcon rawLogo = new ImageIcon("design_images/koreanexpress-logo.png");
        Image scaledLogo = rawLogo.getImage().getScaledInstance(300, 90, Image.SCALE_SMOOTH);
        ImageIcon logoIcon = new ImageIcon(scaledLogo);

        logoLabel = new JLabel(logoIcon);
        logoPanel.add(logoLabel);

        // Right: Navigation buttons
        navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 50, 20));
        navPanel.setBackground(Color.WHITE);

        profileButton = makeNavButton("Profile");
        logoutButton = makeNavButton("Log Out");

        navPanel.add(profileButton);
        navPanel.add(logoutButton);

        headerPanel.add(logoPanel, BorderLayout.WEST);
        headerPanel.add(navPanel, BorderLayout.EAST);
        frame.add(headerPanel, BorderLayout.NORTH);

        // ================= BODY ==================
        bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(30, 150, 30, 150));
        bodyPanel.setBackground(Color.WHITE);

        // Management Section
        JPanel managementSection = createSectionPanel("MANAGEMENT", new Color(230, 240, 255));
        
        JPanel managementButtonsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        managementButtonsPanel.setBackground(new Color(230, 240, 255));
        managementButtonsPanel.add(createButtonPanel(manageProductsButton = new JButton("MANAGE MENU CATEGORIES"), new Color(230, 240, 255)));
        managementButtonsPanel.add(createButtonPanel(manageCityGroupsButton = new JButton("MANAGE CITY GROUPS"), new Color(230, 240, 255)));
        managementSection.add(managementButtonsPanel);
        
        // Reports Section
        JPanel reportsSection = createSectionPanel("REPORTS", new Color(255, 240, 230));
        
        JPanel reportsButtonsPanel = new JPanel(new GridLayout(2, 2, 20, 15));
        reportsButtonsPanel.setBackground(new Color(255, 240, 230));
        reportsButtonsPanel.add(createButtonPanel(checkProfitButton = new JButton("REVENUE REPORT"), new Color(255, 240, 230)));
        reportsButtonsPanel.add(createButtonPanel(checkEngagementButton = new JButton("CUSTOMER ENGAGEMENT REPORT"), new Color(255, 240, 230)));
        reportsButtonsPanel.add(createButtonPanel(checkOrderStatusButton = new JButton("ORDER FREQUENCY REPORT"), new Color(255, 240, 230)));
        reportsButtonsPanel.add(createButtonPanel(checkMenuStatusButton = new JButton("MENU REPORT"), new Color(255, 240, 230)));
        reportsSection.add(reportsButtonsPanel);

        // Add sections to body with spacing
        bodyPanel.add(managementSection);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        bodyPanel.add(reportsSection);

        frame.add(bodyPanel, BorderLayout.CENTER);

        // ================= FOOTER ==================
        footerPanel = new JPanel();
        footerPanel.setBackground(Color.WHITE);
        footerLabel = new JLabel("2025 © Korean Express Inc. All Rights Reserved.");
        footerLabel.setFont(new Font("Helvetica Neue", Font.BOLD, 14));
        footerPanel.add(footerLabel);

        frame.add(footerPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    /**
     * Creates a styled navigation button
     */
    private JButton makeNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 20));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setForeground(new Color(230, 0, 0));
        return button;
    }

    /**
     * Creates a section panel with title and background color
     */
    private JPanel createSectionPanel(String title, Color backgroundColor) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBackground(backgroundColor);
        sectionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(25, 40, 25, 40)
        ));

        // Section title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(80, 80, 80));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sectionPanel.add(titleLabel);
        sectionPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        return sectionPanel;
    }

    /**
     * Creates a panel for a button with proper styling
     */
    private JPanel createButtonPanel(JButton button, Color backgroundColor) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.setBackground(backgroundColor); // Use provided background color
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        // Style the button
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(new Color(230, 0, 0));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        
        // Set button size - wider to accommodate longer text
        button.setPreferredSize(new Dimension(400, 45));
        button.setMinimumSize(new Dimension(400, 45));
        button.setMaximumSize(new Dimension(400, 45));
        
        // Center the button in the panel
        buttonPanel.add(button, BorderLayout.CENTER);
        return buttonPanel;
    }

    public JFrame getFrame(){
        return frame;
    }

    public JButton getLogoutButton(){
        return logoutButton;
    }

    public JButton getProfileButton(){
        return profileButton;
    }

    public JButton getManageProductsButton(){
        return manageProductsButton;
    }

    public JButton getCheckProfitButton(){
        return checkProfitButton;
    }

    public JButton getCheckEngagementButton(){
        return checkEngagementButton;
    }

    public JButton getCheckMenuStatusButton(){
        return checkMenuStatusButton;
    }

    public JButton getCheckOrderStatusButton(){
        return checkOrderStatusButton;
    }

    public JButton getManageCityGroupsButton() {
        return manageCityGroupsButton;
    }
}