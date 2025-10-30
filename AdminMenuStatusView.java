import javax.swing.*;
import java.awt.*;

/**
 * Displays the Admin Menu Status Page of the Incorporation App.
 */
public class AdminMenuStatusView {
    private JFrame frame;
    private JPanel headerPanel, tablePanel;
    private JButton logoutButton, settingsButton;
    private JLabel logoLabel;

    /**
     * Constructor for AdminMenuStatusView class.
     */
    public AdminMenuStatusView() {
        // Frame setup
        frame = new JFrame("Admin Menu Status Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
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
        String[] columnNames = {"Menu ID", "Menu Name", "Total Sold", "Order ID"};
        // Example placeholder data
        Object[][] data = {
                {"", "", "", ""},
                {"", "", "", ""},
                {"", "", "", ""}
        };

        JTable menuTable = new JTable(data, columnNames);
        menuTable.setEnabled(false); // Read-only
        menuTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(menuTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        frame.add(tablePanel, BorderLayout.CENTER);

        // ===== SHOW FRAME =====
        frame.setVisible(true);
    }

    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminMenuStatusView::new);
    }
}
