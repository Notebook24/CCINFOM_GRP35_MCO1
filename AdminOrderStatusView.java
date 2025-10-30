import javax.swing.*;
import java.awt.*;

/**
 * Displays the Admin Order Status Page of the Incorporation App.
 */
public class AdminOrderStatusView {
    private JFrame frame;
    private JPanel headerPanel, tablePanel;
    private JButton logoutButton, settingsButton;
    private JLabel logoLabel;

    /**
     * Constructor for AdminOrderStatusView class.
     */
    public AdminOrderStatusView() {
        // Frame setup
        frame = new JFrame("Admin Order Status Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 400);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        // ===== HEADER =====
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        logoLabel = new JLabel("Logo of Incorporation");
        logoutButton = new JButton("Log Out");
        settingsButton = new JButton("Settings");

        JPanel rightHeaderPanel = new JPanel();
        rightHeaderPanel.add(logoutButton);
        rightHeaderPanel.add(settingsButton);

        headerPanel.add(logoLabel, BorderLayout.WEST);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);
        frame.add(headerPanel, BorderLayout.NORTH);

        // ===== TABLE SECTION =====
        tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Table headers
        String[] columnNames = {"Order ID", "Ordered By", "Ordered Menu", "Total", "Order Status"};
        // Placeholder data
        Object[][] data = {
                {"", "", "", "", ""},
                {"", "", "", "", ""},
                {"", "", "", "", ""}
        };

        JTable orderTable = new JTable(data, columnNames);
        orderTable.setEnabled(false); // Read-only
        orderTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(orderTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        frame.add(tablePanel, BorderLayout.CENTER);

        // ===== SHOW FRAME =====
        frame.setVisible(true);
    }

    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminOrderStatusView::new);
    }
}
