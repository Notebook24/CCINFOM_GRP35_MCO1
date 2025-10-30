import javax.swing.*;
import java.awt.*;

/**
 * Designs the Customer Menu Page of the Restaurant App.
 */
public class CustomerMenuPageView {
    private JFrame frame;
    private JPanel headerPanel, menuPanel;
    private JButton logoutButton, settingsButton;
    private JLabel logoLabel, titleLabel;

    /**
     * Constructor for CustomerMenuPageView class.
     */
    public CustomerMenuPageView() {
        // Frame setup
        frame = new JFrame("Customer Menu Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        // Header section
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        logoLabel = new JLabel("Restaurant Logo");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 12));

        // Right side buttons
        logoutButton = new JButton("Log out");
        settingsButton = new JButton("Settings");
        JPanel rightHeaderPanel = new JPanel();
        rightHeaderPanel.add(logoutButton);
        rightHeaderPanel.add(settingsButton);

        headerPanel.add(logoLabel, BorderLayout.WEST);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);

        frame.add(headerPanel, BorderLayout.NORTH);

        // Menu title
        titleLabel = new JLabel("Menu", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        frame.add(titleLabel, BorderLayout.CENTER);

        // Food items grid
        menuPanel = new JPanel(new GridLayout(3, 4, 20, 20)); // 3 rows x 4 columns
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        for (int i = 1; i <= 12; i++) {
            JPanel itemPanel = new JPanel(new BorderLayout());
            JLabel foodLabel = new JLabel("Food", SwingConstants.CENTER);
            JLabel picLabel = new JLabel("(pic)", SwingConstants.CENTER);
            itemPanel.add(foodLabel, BorderLayout.NORTH);
            itemPanel.add(picLabel, BorderLayout.CENTER);
            menuPanel.add(itemPanel);
        }

        frame.add(menuPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CustomerMenuPageView::new);
    }
}
