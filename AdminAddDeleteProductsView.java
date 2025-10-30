import javax.swing.*;
import java.awt.*;

/**
 * Designs the Admin Add/Delete Products Page.
 */
public class AdminAddDeleteProductsView {
    private JFrame frame;
    private JPanel headerPanel, formPanel, footerPanel;
    private JButton logoutButton, settingsButton, addAmountButton, deleteAmountButton;
    private JTextField nameField, priceField, prepTimeField;
    private JTextArea descriptionArea;
    private JLabel logoLabel;

    /**
     * Constructor for AdminAddDeleteProductsView class.
     */
    public AdminAddDeleteProductsView() {
        // Frame setup
        frame = new JFrame("Admin - Add/Delete Products");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        // Header panel (logo + right buttons)
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        logoLabel = new JLabel("Logo of Restaurant");
        logoutButton = new JButton("Log out");
        settingsButton = new JButton("Settings");

        JPanel rightHeaderPanel = new JPanel();
        rightHeaderPanel.add(logoutButton);
        rightHeaderPanel.add(settingsButton);

        headerPanel.add(logoLabel, BorderLayout.WEST);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);
        frame.add(headerPanel, BorderLayout.NORTH);

        // Form panel (center)
        formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        // Name row
        JPanel namePanel = new JPanel(new BorderLayout(10, 0));
        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField();
        namePanel.add(nameLabel, BorderLayout.WEST);
        namePanel.add(nameField, BorderLayout.CENTER);
        formPanel.add(namePanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Price row
        JPanel pricePanel = new JPanel(new BorderLayout(10, 0));
        JLabel priceLabel = new JLabel("Price:");
        priceField = new JTextField();
        pricePanel.add(priceLabel, BorderLayout.WEST);
        pricePanel.add(priceField, BorderLayout.CENTER);
        formPanel.add(pricePanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Description row
        JPanel descPanel = new JPanel(new BorderLayout(10, 0));
        JLabel descLabel = new JLabel("Description:");
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descPanel.add(descLabel, BorderLayout.WEST);
        descPanel.add(descScroll, BorderLayout.CENTER);
        formPanel.add(descPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Prep Time row
        JPanel prepPanel = new JPanel(new BorderLayout(10, 0));
        JLabel prepLabel = new JLabel("Prep Time:");
        prepTimeField = new JTextField();
        prepPanel.add(prepLabel, BorderLayout.WEST);
        prepPanel.add(prepTimeField, BorderLayout.CENTER);
        formPanel.add(prepPanel);

        frame.add(formPanel, BorderLayout.CENTER);

        // Footer panel (Add/Delete buttons)
        footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        addAmountButton = new JButton("Add amount");
        deleteAmountButton = new JButton("Delete amount");

        footerPanel.add(addAmountButton, BorderLayout.WEST);
        footerPanel.add(deleteAmountButton, BorderLayout.EAST);

        frame.add(footerPanel, BorderLayout.SOUTH);

        // Display frame
        frame.setVisible(true);
    }

    // Main method to test the UI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminAddDeleteProductsView::new);
    }
}
