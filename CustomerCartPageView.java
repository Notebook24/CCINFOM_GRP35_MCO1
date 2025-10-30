import javax.swing.*;
import java.awt.*;

/**
 * Designs the Customer Cart Page of the Restaurant App.
 */
public class CustomerCartPageView {
    private JFrame frame;
    private JPanel headerPanel, cartPanel, footerPanel;
    private JButton logoutButton, settingsButton, checkoutButton;
    private JLabel logoLabel, cartTitleLabel, totalLabel;

    /**
     * Constructor for CustomerCartPageView class.
     */
    public CustomerCartPageView() {
        // Frame setup
        frame = new JFrame("Customer Cart Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        // Header (Restaurant logo + right buttons)
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        logoLabel = new JLabel("Restaurant Logo");

        logoutButton = new JButton("Log out");
        settingsButton = new JButton("Settings");

        JPanel rightHeaderPanel = new JPanel();
        rightHeaderPanel.add(logoutButton);
        rightHeaderPanel.add(settingsButton);

        headerPanel.add(logoLabel, BorderLayout.WEST);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);
        frame.add(headerPanel, BorderLayout.NORTH);

        // Main cart section
        cartPanel = new JPanel();
        cartPanel.setLayout(new BorderLayout());
        cartPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Title
        cartTitleLabel = new JLabel("Shopping Cart");
        cartTitleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(cartTitleLabel, BorderLayout.WEST);

        // Item section (sample restaurant items)
        JPanel itemsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Example placeholder items
        for (int i = 1; i <= 3; i++) {
            JPanel itemPanel = new JPanel();
            itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
            itemPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            JLabel restaurantName = new JLabel("Restaurant name");
            JLabel itemsLabel = new JLabel("items");
            restaurantName.setAlignmentX(Component.CENTER_ALIGNMENT);
            itemsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            itemPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            itemPanel.add(restaurantName);
            itemPanel.add(itemsLabel);
            itemPanel.add(Box.createRigidArea(new Dimension(0, 10)));

            itemsPanel.add(itemPanel);
        }

        cartPanel.add(titlePanel, BorderLayout.NORTH);
        cartPanel.add(itemsPanel, BorderLayout.CENTER);
        frame.add(cartPanel, BorderLayout.CENTER);

        // Footer (Checkout button + Total)
        footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        checkoutButton = new JButton("Checkout");
        totalLabel = new JLabel("Total: ₱0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));

        footerPanel.add(checkoutButton, BorderLayout.WEST);
        footerPanel.add(totalLabel, BorderLayout.EAST);

        frame.add(footerPanel, BorderLayout.SOUTH);

        // Display frame
        frame.setVisible(true);
    }

    // Main method to test UI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CustomerCartPageView::new);
    }
}
