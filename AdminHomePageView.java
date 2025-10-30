import javax.swing.*;
import java.awt.*;

/**
 * Designs the Admin Home Page of the Restaurant App.
 */
public class AdminHomePageView {
    private JFrame frame;
    private JPanel headerPanel, bodyPanel;
    private JButton logoutButton, settingsButton;
    private JButton addProductButton, deleteProductButton, editProductButton, checkProfitButton;
    private JButton checkCustomerEngagementButton, checkOrderStatusButton;
    private JLabel restaurantNameLabel;

    /**
     * Constructor for AdminHomePageView class.
     */
    public AdminHomePageView() {
        // Frame setup
        frame = new JFrame("Admin Home Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        // Header section
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        restaurantNameLabel = new JLabel("RESTAURANT NAME (already set, cannot be changed)");
        logoutButton = new JButton("Log out");
        settingsButton = new JButton("Settings");

        JPanel rightHeaderPanel = new JPanel();
        rightHeaderPanel.add(logoutButton);
        rightHeaderPanel.add(settingsButton);

        headerPanel.add(restaurantNameLabel, BorderLayout.WEST);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);
        frame.add(headerPanel, BorderLayout.NORTH);

        // Body section
        bodyPanel = new JPanel(new GridLayout(4, 2, 40, 20));
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));

        addProductButton = new JButton("ADD PRODUCT");
        deleteProductButton = new JButton("DELETE PRODUCT");
        editProductButton = new JButton("EDIT PRODUCT");
        checkProfitButton = new JButton("CHECK PROFIT");
        checkCustomerEngagementButton = new JButton("CHECK CUSTOMER ENGAGEMENT");
        checkOrderStatusButton = new JButton("CHECK ORDER STATUS");

        // Add buttons to grid
        bodyPanel.add(addProductButton);
        bodyPanel.add(checkCustomerEngagementButton);
        bodyPanel.add(deleteProductButton);
        bodyPanel.add(checkOrderStatusButton);
        bodyPanel.add(editProductButton);
        bodyPanel.add(new JLabel()); // Empty space for alignment
        bodyPanel.add(checkProfitButton);

        frame.add(bodyPanel, BorderLayout.CENTER);

        // Make frame visible
        frame.setVisible(true);
    }

    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminHomePageView::new);
    }
}
