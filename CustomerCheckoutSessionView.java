import javax.swing.*;
import java.awt.*;

/**
 * Designs the Customer Checkout Session Page of the Restaurant App.
 */
public class CustomerCheckoutSessionView {
    private JFrame frame;
    private JPanel headerPanel, bodyPanel, footerPanel;
    private JButton logoutButton, settingsButton, placeOrderButton;
    private JLabel logoLabel, summaryLabel, addressLabel, paymentLabel, totalLabel;
    private JTextField addressField;
    private JComboBox<String> paymentOptions;

    /**
     * Constructor for CustomerCheckoutSessionView class.
     */
    public CustomerCheckoutSessionView() {
        // Frame setup
        frame = new JFrame("Customer Checkout Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        // Header panel
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        logoLabel = new JLabel("Logo of Incorporation");

        logoutButton = new JButton("Log out");
        settingsButton = new JButton("Settings");

        JPanel rightHeaderPanel = new JPanel();
        rightHeaderPanel.add(logoutButton);
        rightHeaderPanel.add(settingsButton);

        headerPanel.add(logoLabel, BorderLayout.WEST);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);
        frame.add(headerPanel, BorderLayout.NORTH);

        // Body panel
        bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        // Summary of items label
        summaryLabel = new JLabel("Summary of Items");
        summaryLabel.setFont(new Font("Arial", Font.BOLD, 18));
        summaryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyPanel.add(summaryLabel);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Address section
        addressLabel = new JLabel("Address:");
        addressLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        addressField = new JTextField();
        addressField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        bodyPanel.add(addressLabel);
        bodyPanel.add(addressField);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Payment options section
        paymentLabel = new JLabel("Payment options:");
        paymentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        String[] paymentMethods = {"Cash on Delivery", "Credit Card", "Gcash", "PayPal"};
        paymentOptions = new JComboBox<>(paymentMethods);
        paymentOptions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        bodyPanel.add(paymentLabel);
        bodyPanel.add(paymentOptions);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        frame.add(bodyPanel, BorderLayout.CENTER);

        // Footer panel (Total + Place order button)
        footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        totalLabel = new JLabel("Total: ₱0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));

        placeOrderButton = new JButton("Place Order");

        footerPanel.add(totalLabel, BorderLayout.WEST);
        footerPanel.add(placeOrderButton, BorderLayout.EAST);
        frame.add(footerPanel, BorderLayout.SOUTH);

        // Display frame
        frame.setVisible(true);
    }

    // Main method to test UI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CustomerCheckoutSessionView::new);
    }
}
