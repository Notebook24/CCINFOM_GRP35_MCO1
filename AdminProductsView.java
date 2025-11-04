import javax.swing.*;
import java.awt.*;

/**
 * Displays the Admin Products Page of the Incorporation App.
 */
public class AdminProductsView {
    private JFrame frame;
    private JPanel headerPanel, bodyPanel;
    private JButton logoutButton, settingsButton;
    private JLabel logoLabel;
    private JButton product1Button, product2Button, product3Button, product4Button;

    /**
     * Constructor for AdminProductsView class.
     */
    public AdminProductsView() {
        // Frame setup
        frame = new JFrame("Admin Products Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        // Header section
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

        // Body section
        bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Create product buttons
        product1Button = new JButton("#1   Clickable Product to Edit");
        product2Button = new JButton("#2");
        product3Button = new JButton("#3");
        product4Button = new JButton("#4");

        // Style buttons
        Dimension buttonSize = new Dimension(400, 35);
        for (JButton btn : new JButton[]{product1Button, product2Button, product3Button, product4Button}) {
            btn.setMaximumSize(buttonSize);
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setFocusPainted(false);
        }

        bodyPanel.add(product1Button);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        bodyPanel.add(product2Button);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        bodyPanel.add(product3Button);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        bodyPanel.add(product4Button);

        frame.add(bodyPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminProductsView::new);
    }
}
