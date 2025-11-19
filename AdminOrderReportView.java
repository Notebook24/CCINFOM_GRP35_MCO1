import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

public class AdminOrderReportView {
    private JFrame frame;
    private JPanel mainPanel, headerPanel, filterPanel, summaryPanel;
    private JButton backButton;
    private JComboBox<String> filterComboBox;
    private JTable ordersTable, customersTable, citiesTable;
    private JLabel[] summaryLabels;
    
    private AdminOrderReportController controller;

    public AdminOrderReportView(AdminOrderReportController controller) {
        this.controller = controller;
        initializeUI();
    }

    private void initializeUI() {
        // Frame setup
        frame = new JFrame("Order Report");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);

        // Main panel with scroll
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        frame.add(scrollPane);

        // Header with back button
        createHeaderPanel();
        mainPanel.add(headerPanel);

        // Filter panel
        createFilterPanel();
        mainPanel.add(filterPanel);

        // Summary panel
        createSummaryPanel();
        mainPanel.add(summaryPanel);

        // Tables in tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Orders table with wider columns
        ordersTable = new JTable();
        ordersTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane ordersScroll = new JScrollPane(ordersTable);
        ordersScroll.setPreferredSize(new Dimension(900, 200));
        tabbedPane.addTab("Order Details", ordersScroll);
        
        // Customers table
        customersTable = new JTable();
        JScrollPane customersScroll = new JScrollPane(customersTable);
        customersScroll.setPreferredSize(new Dimension(800, 200));
        tabbedPane.addTab("Customer Orders", customersScroll);
        
        // Cities table
        citiesTable = new JTable();
        JScrollPane citiesScroll = new JScrollPane(citiesTable);
        citiesScroll.setPreferredSize(new Dimension(800, 200));
        tabbedPane.addTab("City Orders", citiesScroll);
        
        mainPanel.add(tabbedPane);

        frame.setVisible(true);
    }

    private void createHeaderPanel() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.setBackground(new Color(220, 0, 0));

        // Back button
        backButton = new JButton("Back to Home");
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(new Color(220, 0, 0));
        backButton.setFocusPainted(false);

        // Title
        JLabel titleLabel = new JLabel("KOREAN EXPRESS - ORDER REPORTS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
    }

    private void createFilterPanel() {
        filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter Options"));
        filterPanel.setBackground(Color.WHITE);
        
        JLabel filterLabel = new JLabel("Time Period:");
        filterComboBox = new JComboBox<>(new String[]{"Today", "This Month", "This Year"});
        
        // Auto-refresh when filter changes
        filterComboBox.addActionListener(e -> controller.refreshData(this.getFilterType()));
        
        filterPanel.add(filterLabel);
        filterPanel.add(filterComboBox);
    }

    private void createSummaryPanel() {
        summaryPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Order Summary Statistics"));
        summaryPanel.setBackground(Color.WHITE);

        String[] summaryTitles = {
            "Total Orders", "Pending Orders", "Preparing Orders", "In Transit Orders",
            "Delivered Orders", "Cancelled Orders", "Top Customer Orders", "Top City Orders"
        };

        summaryLabels = new JLabel[8];
        
        for (int i = 0; i < 8; i++) {
            JPanel statPanel = new JPanel(new BorderLayout());
            statPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 0, 0)));
            statPanel.setBackground(new Color(255, 240, 240));
            
            JLabel titleLabel = new JLabel(summaryTitles[i]);
            titleLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            summaryLabels[i] = new JLabel("0", SwingConstants.CENTER);
            summaryLabels[i].setFont(new Font("Arial", Font.BOLD, 12));
            summaryLabels[i].setForeground(new Color(220, 0, 0));
            
            statPanel.add(titleLabel, BorderLayout.NORTH);
            statPanel.add(summaryLabels[i], BorderLayout.CENTER);
            
            summaryPanel.add(statPanel);
        }
    }

    public void updateSummaryPanel(Map<String, Object> summaryData) {
        SwingUtilities.invokeLater(() -> {
            summaryLabels[0].setText(String.valueOf(summaryData.getOrDefault("total_orders", 0)));
            summaryLabels[1].setText(String.valueOf(summaryData.getOrDefault("pending_orders", 0)));
            summaryLabels[2].setText(String.valueOf(summaryData.getOrDefault("preparing_orders", 0)));
            summaryLabels[3].setText(String.valueOf(summaryData.getOrDefault("in_transit_orders", 0)));
            summaryLabels[4].setText(String.valueOf(summaryData.getOrDefault("delivered_orders", 0)));
            summaryLabels[5].setText(String.valueOf(summaryData.getOrDefault("cancelled_orders", 0)));
            
            String topCustomer = (String) summaryData.getOrDefault("top_customer", "None");
            summaryLabels[6].setText(topCustomer.length() > 15 ? topCustomer.substring(0, 15) + "..." : topCustomer);
            summaryLabels[6].setToolTipText(topCustomer);
            
            String topCity = (String) summaryData.getOrDefault("top_city", "None");
            summaryLabels[7].setText(topCity.length() > 15 ? topCity.substring(0, 15) + "..." : topCity);
            summaryLabels[7].setToolTipText(topCity);
        });
    }

    public void updateOrdersTable(DefaultTableModel model) {
        SwingUtilities.invokeLater(() -> {
            ordersTable.setModel(model);
            ordersTable.getTableHeader().setBackground(new Color(220, 0, 0));
            ordersTable.getTableHeader().setForeground(Color.WHITE);
            ordersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
            
            // Set column widths for better date display
            if (ordersTable.getColumnCount() >= 8) {
                ordersTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Order ID
                ordersTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Status
                ordersTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Order Lines
                ordersTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Avg Lines/Order
                ordersTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Customer ID
                ordersTable.getColumnModel().getColumn(5).setPreferredWidth(150); // Customer Name
                ordersTable.getColumnModel().getColumn(6).setPreferredWidth(120); // City
                ordersTable.getColumnModel().getColumn(7).setPreferredWidth(180); // Order Date (even wider)
            }
        });
    }

    public void updateCustomersTable(DefaultTableModel model) {
        SwingUtilities.invokeLater(() -> {
            customersTable.setModel(model);
            customersTable.getTableHeader().setBackground(new Color(220, 0, 0));
            customersTable.getTableHeader().setForeground(Color.WHITE);
            customersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        });
    }

    public void updateCitiesTable(DefaultTableModel model) {
        SwingUtilities.invokeLater(() -> {
            citiesTable.setModel(model);
            citiesTable.getTableHeader().setBackground(new Color(220, 0, 0));
            citiesTable.getTableHeader().setForeground(Color.WHITE);
            citiesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        });
    }

    // Getter methods
    public JFrame getFrame() {
        return frame;
    }

    public JButton getBackButton() {
        return backButton;
    }

    public JComboBox<String> getFilterComboBox() {
        return filterComboBox;
    }

    public String getFilterType() {
        return (String) filterComboBox.getSelectedItem();
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}