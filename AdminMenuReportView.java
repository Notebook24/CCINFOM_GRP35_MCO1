import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

public class AdminMenuReportView {
    private JFrame frame;
    private JPanel mainPanel, headerPanel, filterPanel, summaryPanel;
    private JButton backButton;
    private JComboBox<String> filterComboBox;
    private JTable menuTable, cityTable;
    private JLabel[] summaryLabels;
    
    private AdminMenuReportController controller;

    public AdminMenuReportView(AdminMenuReportController controller) {
        this.controller = controller;
        initializeUI();
    }

    private void initializeUI() {
        // Frame setup
        frame = new JFrame("Menu Report");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
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
        
        // Menu table
        menuTable = new JTable();
        menuTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane menuScroll = new JScrollPane(menuTable);
        menuScroll.setPreferredSize(new Dimension(800, 250));
        tabbedPane.addTab("Menu Sales", menuScroll);
        
        // City table
        cityTable = new JTable();
        JScrollPane cityScroll = new JScrollPane(cityTable);
        cityScroll.setPreferredSize(new Dimension(800, 250));
        tabbedPane.addTab("City Breakdown", cityScroll);
        
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
        JLabel titleLabel = new JLabel("KOREAN EXPRESS - MENU REPORTS", SwingConstants.CENTER);
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
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary Statistics"));
        summaryPanel.setBackground(Color.WHITE);

        String[] summaryTitles = {
            "Total Menus", "Menu Groups", "Available Menus", "Available Groups",
            "Sold Menus", "Total Revenue", "Most Sold Menu", "Most Sold Group"
        };

        summaryLabels = new JLabel[8];
        
        for (int i = 0; i < 8; i++) {
            JPanel statPanel = new JPanel(new BorderLayout());
            statPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 0, 0)));
            statPanel.setBackground(new Color(255, 240, 240));
            
            JLabel titleLabel = new JLabel(summaryTitles[i]);
            titleLabel.setFont(new Font("Arial", Font.PLAIN, 11));
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
            summaryLabels[0].setText(String.valueOf(summaryData.getOrDefault("total_menus", 0)));
            summaryLabels[1].setText(String.valueOf(summaryData.getOrDefault("total_menu_groups", 0)));
            summaryLabels[2].setText(String.valueOf(summaryData.getOrDefault("available_menus", 0)));
            summaryLabels[3].setText(String.valueOf(summaryData.getOrDefault("available_menu_groups", 0)));
            summaryLabels[4].setText(String.valueOf(summaryData.getOrDefault("sold_menus", 0)));
            summaryLabels[5].setText(String.format("₱%.2f", summaryData.getOrDefault("total_revenue", 0.0)));
            
            String mostSoldMenu = (String) summaryData.getOrDefault("most_sold_menu", "None");
            summaryLabels[6].setText(mostSoldMenu.length() > 15 ? mostSoldMenu.substring(0, 15) + "..." : mostSoldMenu);
            summaryLabels[6].setToolTipText(mostSoldMenu);
            
            String mostSoldGroup = (String) summaryData.getOrDefault("most_sold_group", "None");
            summaryLabels[7].setText(mostSoldGroup.length() > 15 ? mostSoldGroup.substring(0, 15) + "..." : mostSoldGroup);
            summaryLabels[7].setToolTipText(mostSoldGroup);
        });
    }

    public void updateMenuTable(DefaultTableModel model) {
        SwingUtilities.invokeLater(() -> {
            menuTable.setModel(model);
            menuTable.getTableHeader().setBackground(new Color(220, 0, 0));
            menuTable.getTableHeader().setForeground(Color.WHITE);
            menuTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
            
            // Set column widths for better display
            if (menuTable.getColumnCount() >= 9) {
                menuTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Menu ID
                menuTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Menu Name
                menuTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Menu Group
                menuTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Unit Price
                menuTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Total Sold
                menuTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Total Orders
                menuTable.getColumnModel().getColumn(6).setPreferredWidth(120); // Revenue
                menuTable.getColumnModel().getColumn(7).setPreferredWidth(120); // Avg Qty/Order
                menuTable.getColumnModel().getColumn(8).setPreferredWidth(100); // Availability
            }
        });
    }

    public void updateCityTable(DefaultTableModel model) {
        SwingUtilities.invokeLater(() -> {
            cityTable.setModel(model);
            cityTable.getTableHeader().setBackground(new Color(220, 0, 0));
            cityTable.getTableHeader().setForeground(Color.WHITE);
            cityTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
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