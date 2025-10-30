import javax.swing.*;
import java.awt.*;

/**
 * Designs the Customer Receipt Page of the Restaurant App.
 */
public class CustomerReceiptPageView {
    private JFrame frame;
    private JPanel headerPanel, bodyPanel;
    private JButton logoutButton, settingsButton, backButton;
    private JLabel logoLabel, titleLabel, successLabel;

    /**
     * Constructor for CustomerReceiptPageView class.
     */
    public CustomerReceiptPageView() {
        // Frame setup
        frame = new JFrame("Customer Receipt Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        // Header section
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        logoLabel = new JLabel("Logo of Incorporation");
        logoLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        logoutButton = new JButton("Log out");
        settingsButton = new JButton("Settings");

        JPanel rightHeaderPanel = new JPanel();
        rightHeaderPanel.add(logoutButton);
        rightHeaderPanel.add(settingsButton);

        headerPanel.add(logoLabel, BorderLayout.WEST);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);

        frame.add(headerPanel, BorderLayout.NORTH);

        // Body section
        bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));

        titleLabel = new JLabel("RECEIPT");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        successLabel = new JLabel("SUCCESS TEXT");
        successLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        successLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        bodyPanel.add(titleLabel);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        bodyPanel.add(successLabel);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        bodyPanel.add(backButton);

        frame.add(bodyPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CustomerReceiptPageView::new);
    }
}
