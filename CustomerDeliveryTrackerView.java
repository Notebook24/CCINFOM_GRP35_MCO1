import javax.swing.*;
import java.awt.*;

/**
 * Designs the Customer Delivery Tracker Page of the Restaurant App.
 */
public class CustomerDeliveryTrackerView {
    private JFrame frame;
    private JPanel headerPanel, progressPanel, footerPanel;
    private JButton logoutButton, settingsButton, backButton;
    private JLabel titleLabel, preparingLabel, shippingLabel, deliveredLabel;
    private JLabel arrow1, arrow2;

    /**
     * Constructor for CustomerDeliveryTrackerView class.
     */
    public CustomerDeliveryTrackerView() {
        // Frame setup
        frame = new JFrame("Customer Delivery Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        // Header
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        titleLabel = new JLabel("Delivery progress:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));

        logoutButton = new JButton("Log out");
        settingsButton = new JButton("Settings");

        JPanel rightHeaderPanel = new JPanel();
        rightHeaderPanel.add(logoutButton);
        rightHeaderPanel.add(settingsButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);
        frame.add(headerPanel, BorderLayout.NORTH);

        // Delivery progress section
        progressPanel = new JPanel();
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        progressPanel.setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));

        preparingLabel = new JLabel("*Preparing");
        preparingLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        arrow1 = new JLabel("↓");
        arrow1.setAlignmentX(Component.CENTER_ALIGNMENT);

        shippingLabel = new JLabel("*Shipping");
        shippingLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        arrow2 = new JLabel("↓");
        arrow2.setAlignmentX(Component.CENTER_ALIGNMENT);

        deliveredLabel = new JLabel("*Delivered");
        deliveredLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        // Center alignment
        preparingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        shippingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        deliveredLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add to panel
        progressPanel.add(preparingLabel);
        progressPanel.add(arrow1);
        progressPanel.add(shippingLabel);
        progressPanel.add(arrow2);
        progressPanel.add(deliveredLabel);

        frame.add(progressPanel, BorderLayout.CENTER);

        // Footer
        footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        backButton = new JButton("Back");
        footerPanel.add(backButton);
        frame.add(footerPanel, BorderLayout.SOUTH);

        // Display
        frame.setVisible(true);
    }

    // Main method to test UI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CustomerDeliveryTrackerView::new);
    }
}
