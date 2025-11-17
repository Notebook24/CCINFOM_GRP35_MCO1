import javax.swing.*;
import java.awt.*;

/**
 * Designs the Customer Home Page in line with its controller.
 */
public class CustomerHomePageView {
    private JFrame frame;
    private JPanel headerPanel, logoPanel, navPanel, bodyPanel, footerPanel;
    public JButton homeButton, paymentsButton, ordersButton, profileButton, logoutButton, viewMenuButton;
    private JLabel logoLabel, taglineLabel, subtextLabel, foodIconsLabel, footerLabel;

    /**
     * Constructor for CustomerHomePageView class.
     */
    public CustomerHomePageView() {
        frame = new JFrame("Customer Home Page");
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

        // Load and resize logo image safely - using original size
        ImageIcon rawLogo = new ImageIcon("design_images/koreanexpress-logo.png");
        Image scaledLogo = rawLogo.getImage().getScaledInstance(300, 90, Image.SCALE_SMOOTH);
        ImageIcon logoIcon = new ImageIcon(scaledLogo);

        logoLabel = new JLabel(logoIcon);
        logoPanel.add(logoLabel);

        // Right: Navigation buttons
        navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 50, 20));
        navPanel.setBackground(Color.WHITE);

        homeButton = makeNavButton("Home");
        paymentsButton = makeNavButton("Payments");
        ordersButton = makeNavButton("Orders");
        profileButton = makeNavButton("Profile");
        logoutButton = makeNavButton("Log Out");

        navPanel.add(homeButton);
        navPanel.add(paymentsButton);
        navPanel.add(ordersButton);
        navPanel.add(profileButton);
        navPanel.add(logoutButton);

        headerPanel.add(logoPanel, BorderLayout.WEST);
        headerPanel.add(navPanel, BorderLayout.EAST);

        frame.add(headerPanel, BorderLayout.NORTH);

        // ================= BODY ==================
        bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBackground(Color.WHITE);

        bodyPanel.add(Box.createVerticalStrut(100));

        // Food icons - using original image without compression
        ImageIcon foodIcons = new ImageIcon("design_images/foodicons.png");
        if (foodIcons.getIconWidth() > 0) {
            // Use original image size or appropriate scaling
            foodIconsLabel = new JLabel(foodIcons);
        } else {
            foodIconsLabel = new JLabel("🍜 🍱 🍛");
            foodIconsLabel.setFont(new Font("SansSerif", Font.PLAIN, 60));
        }
        foodIconsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tagline - original font and size
        taglineLabel = new JLabel("Your Fast Lane to Korean Cuisine.");
        taglineLabel.setFont(new Font("Helvetica Neue", Font.BOLD | Font.ITALIC, 32));
        taglineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtext - original font and size
        subtextLabel = new JLabel("DLSU - Manila's newest pioneer of Korean cloud kitchen.");
        subtextLabel.setFont(new Font("Helvetica Neue", Font.PLAIN, 20));
        subtextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        bodyPanel.add(foodIconsLabel);
        bodyPanel.add(Box.createVerticalStrut(40));
        bodyPanel.add(taglineLabel);
        bodyPanel.add(Box.createVerticalStrut(10));
        bodyPanel.add(subtextLabel);
        bodyPanel.add(Box.createVerticalStrut(30));

        // View Menu Button - original styling
        viewMenuButton = new JButton("View Menus");
        viewMenuButton.setFont(new Font("Helvetica Neue", Font.BOLD, 22));
        viewMenuButton.setForeground(Color.BLACK);
        viewMenuButton.setBackground(new Color(255, 182, 182));
        viewMenuButton.setFocusPainted(false);
        viewMenuButton.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));
        viewMenuButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        bodyPanel.add(viewMenuButton);
        bodyPanel.add(Box.createVerticalStrut(70));

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

    private JButton makeNavButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 20));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setForeground(new Color(230, 0, 0));
        return b;
    }

    // ================= GETTERS ==================
    public JFrame getFrame() {
        return frame;
    }

    public JButton getViewMenuButton() {
        return viewMenuButton;
    }
}