import javax.swing.*;
import java.awt.*;

/**
 * Designs the Customer Home Page of the Restaurant App.
 */
public class CustomerHomePageView {
    private JFrame frame;
    private JPanel headerPanel, bodyPanel, footerPanel;
    private JButton logoutButton, settingsButton;
    private JLabel logoLabel, cartLogoLabel, homeTitleLabel, samplePhotosLabel, footerLabel;

    /**
     * Constructor for CustomerHomePageView class.
     */
    public CustomerHomePageView() {
        // Frame setup
        frame = new JFrame("Customer Home Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        // Header panel (logo, restaurant name, cart logo, logout/settings)
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Left side (logo + restaurant name + cart)
        JPanel leftHeaderPanel = new JPanel();
        leftHeaderPanel.setLayout(new BoxLayout(leftHeaderPanel, BoxLayout.Y_AXIS));
        logoLabel = new JLabel("Logo and Restaurant Name");
        cartLogoLabel = new JLabel("Cart Logo");
        leftHeaderPanel.add(logoLabel);
        leftHeaderPanel.add(cartLogoLabel);

        // Right side (logout and settings buttons)
        JPanel rightHeaderPanel = new JPanel();
        logoutButton = new JButton("Log out");
        settingsButton = new JButton("Settings");
        rightHeaderPanel.add(logoutButton);
        rightHeaderPanel.add(settingsButton);

        headerPanel.add(leftHeaderPanel, BorderLayout.WEST);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);
        frame.add(headerPanel, BorderLayout.NORTH);

        // Body panel (home title + sample food photos)
        bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));

        homeTitleLabel = new JLabel("HOME PAGE", SwingConstants.CENTER);
        homeTitleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        homeTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        samplePhotosLabel = new JLabel("Sample Food Photos", SwingConstants.CENTER);
        samplePhotosLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        samplePhotosLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        bodyPanel.add(homeTitleLabel);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        bodyPanel.add(samplePhotosLabel);

        frame.add(bodyPanel, BorderLayout.CENTER);

        // Footer panel (copyright symbol)
        footerPanel = new JPanel();
        footerLabel = new JLabel("© 2025 My Restaurant App");
        footerPanel.add(footerLabel);
        frame.add(footerPanel, BorderLayout.SOUTH);

        // Display frame
        frame.setVisible(true);
    }

    // Main method to test UI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CustomerHomePageView::new);
    }
}
