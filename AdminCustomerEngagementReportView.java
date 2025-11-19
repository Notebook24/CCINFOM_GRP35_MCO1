import javax.swing.*;
import java.awt.*;

/**
 * Displays the Admin Customer Engagement Page of the Incorporation App.
 */
public class AdminCustomerEngagementReportView {
    private JFrame frame;
    private JPanel headerPanel, tablePanel, footerPanel;
    private JButton logoutButton, settingsButton;
    private JLabel logoLabel;
    private JLabel totalExpenditureLabel, yearExpenditureLabel, monthExpenditureLabel, dayExpenditureLabel;

    /**
     * Constructor for AdminCustomerEngagementView class.
     */
    public AdminCustomerEngagementReportView() {
        // Frame setup
        frame = new JFrame("Admin Customer Engagement Page");
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

        // ===== BODY / TABLE =====
        tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        // Column headers
        String[] columnNames = {"Customer Name", "# Orders Placed", "# Spent Money"};
        // Example empty data — can be replaced with dynamic data later
        Object[][] data = {
                {"", "", ""},
                {"", "", ""},
                {"", "", ""},
                {"", "", ""}
        };

        JTable table = new JTable(data, columnNames);
        table.setEnabled(false); // Read-only for now
        table.setRowHeight(25);
        JScrollPane tableScrollPane = new JScrollPane(table);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        frame.add(tablePanel, BorderLayout.CENTER);

        // ===== FOOTER / TOTALS =====
        footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));

        totalExpenditureLabel = new JLabel("Total Expenditure: XXX PHP");
        yearExpenditureLabel = new JLabel("Total Expenditure this year: XXX PHP");
        monthExpenditureLabel = new JLabel("Total Expenditure this month: XXX PHP");
        dayExpenditureLabel = new JLabel("Total Expenditure this day: XXX PHP");

        footerPanel.add(totalExpenditureLabel);
        footerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        footerPanel.add(yearExpenditureLabel);
        footerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        footerPanel.add(monthExpenditureLabel);
        footerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        footerPanel.add(dayExpenditureLabel);

        frame.add(footerPanel, BorderLayout.SOUTH);

        // ===== SHOW FRAME =====
        frame.setVisible(true);
    }

    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminCustomerEngagementReportView::new);
    }
}
