import javax.swing.*;
import java.awt.*;

/**
 * Customer Checkout Page Layout in line with its controller
 */
public class CustomerCheckoutSessionView {
    private JFrame frame;
    private JPanel headerPanel, bodyPanel, footerPanel;
    private JButton logoutButton, settingsButton, placeOrderButton;
    private JLabel logoLabel, addressLabel, summaryLabel, productLabel, priceLabel, quantityLabel;
    private JLabel totalPriceLabel, totalPrepLabel;

    /**
     * Constructor for CustomerCheckoutSessionView class
     */
    public CustomerCheckoutSessionView(){
        frame = new JFrame("Customer Checkout Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 400);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel leftHeader = new JPanel();
        leftHeader.setLayout(new BoxLayout(leftHeader, BoxLayout.Y_AXIS));
        leftHeader.setOpaque(false);

        logoLabel = new JLabel("Logo of incorporation");
        addressLabel = new JLabel("User Address");
        leftHeader.add(logoLabel);
        leftHeader.add(addressLabel);

        JPanel rightHeader = new JPanel();
        rightHeader.setLayout(new BoxLayout(rightHeader, BoxLayout.Y_AXIS));
        rightHeader.setOpaque(false);
        logoutButton = new JButton("Log out button");
        settingsButton = new JButton("Settings button");
        rightHeader.add(logoutButton);
        rightHeader.add(settingsButton);

        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(rightHeader, BorderLayout.EAST);
        frame.add(headerPanel, BorderLayout.NORTH);

        bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        summaryLabel = new JLabel("Summary of Items");
        summaryLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        summaryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyPanel.add(summaryLabel);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel tableHeader = new JPanel(new GridLayout(1, 3));
        productLabel = new JLabel("Product name");
        priceLabel = new JLabel("Price");
        quantityLabel = new JLabel("Quantity");
        tableHeader.add(productLabel);
        tableHeader.add(priceLabel);
        tableHeader.add(quantityLabel);

        bodyPanel.add(tableHeader);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 100)));

        frame.add(bodyPanel, BorderLayout.CENTER);

        footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel leftFooter = new JPanel();
        leftFooter.setLayout(new BoxLayout(leftFooter, BoxLayout.Y_AXIS));
        leftFooter.setOpaque(false);

        totalPriceLabel = new JLabel("Total Price");
        totalPrepLabel = new JLabel("Total Prep Time:");
        leftFooter.add(totalPriceLabel);
        leftFooter.add(totalPrepLabel);

        placeOrderButton = new JButton("Place Order");

        footerPanel.add(leftFooter, BorderLayout.WEST);
        footerPanel.add(placeOrderButton, BorderLayout.EAST);
        frame.add(footerPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public JButton getLogoutButton(){
        return logoutButton;
    }

    public JButton getSettingsButton(){
        return settingsButton;
    }

    public JButton getPlaceOrderButton(){
        return placeOrderButton;
    }
}
