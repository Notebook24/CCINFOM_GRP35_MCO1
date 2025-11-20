import javax.swing.*;
import java.awt.*;
import java.util.Map;
import javax.swing.table.DefaultTableModel;
import javax.swing.JRadioButton;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


/**
 * Displays the Admin Revenue Page of the Incorporation App.
 */
public class AdminRevenueReportView {
    private JFrame frame;
    private JPanel headerPanel, summaryPanel, filterPanel, mainPanel;
    private JButton logoutButton, settingsButton, backButton;
    private JLabel logoLabel;
    private JLabel product2Label, product3Label, product4Label;
    private JLabel totalProfitLabel, profitYearLabel, profitMonthLabel, profitTodayLabel;
    private JTable citiesTable, customersTable, paymentsTable;
    private JComboBox<String> filterComboBox;
    private JLabel[] summaryLabels;
    private AdminRevenueReportController controller;

    /**
     * Constructor for AdminRevenueView class.
     */
    public AdminRevenueReportView(AdminRevenueReportController controller) {
        this.controller = controller;
        initializeUI();
    }

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

    public void initializeUI()
    {
        frame = new JFrame("Revenue Report");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        frame.add(scrollPane);

        createHeaderPanel();
        mainPanel.add(headerPanel);

        createFilterPanel();
        mainPanel.add(filterPanel);

        createSummaryPanel();
        mainPanel.add(summaryPanel); 

        JTabbedPane tabbedPane = new JTabbedPane();

        citiesTable = new JTable();
        citiesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane citiesScroll = new JScrollPane(citiesTable);
        citiesScroll.setPreferredSize(new Dimension(800, 200));
        tabbedPane.addTab("City Revenue", wrapTable(citiesScroll));

        paymentsTable = new JTable();
        JScrollPane paymentsScroll = new JScrollPane(paymentsTable);
        paymentsScroll.setPreferredSize(new Dimension(800, 200));
        tabbedPane.addTab("Payments", wrapTable(paymentsScroll));

        customersTable = new JTable();
        JScrollPane customersScroll = new JScrollPane(customersTable);
        customersScroll.setPreferredSize(new Dimension(800, 200));
        tabbedPane.addTab("Customer Orders", wrapTable(customersScroll));


        mainPanel.add(tabbedPane);
        frame.setVisible(true);
    }

    public void createHeaderPanel()
    {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.setBackground(new Color(220, 0, 0));

        backButton = new JButton("Back to Home");
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(new Color(220, 0, 0));
        backButton.setFocusPainted(false);

        JLabel titleLabel = new JLabel("KOREAN EXPRESS - REVENUE REPORTS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
    }

    public void createFilterPanel() 
    {
        filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter Options"));
        filterPanel.setBackground(Color.WHITE);

         ButtonGroup group = new ButtonGroup();

        LocalDate today = LocalDate.now();
        DateTimeFormatter mmFmt = DateTimeFormatter.ofPattern("MM");
        DateTimeFormatter ddFmt = DateTimeFormatter.ofPattern("dd");
        DateTimeFormatter yyyyFmt = DateTimeFormatter.ofPattern("yyyy");

        JRadioButton dayRadio = new JRadioButton("Day:");
        dayRadio.setBackground(Color.WHITE);
        dayRadio.setSelected(true); 

        JTextField dayMonthField = new JTextField(2);
        JTextField dayDayField   = new JTextField(2);
        JTextField dayYearField  = new JTextField(4);

        dayMonthField.setText(today.format(mmFmt));
        dayDayField  .setText(today.format(ddFmt));
        dayYearField .setText(today.format(yyyyFmt));

        JPanel dayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dayPanel.setBackground(Color.WHITE);
        dayPanel.add(dayRadio);
        dayPanel.add(dayMonthField);
        dayPanel.add(new JLabel("-"));
        dayPanel.add(dayDayField);
        dayPanel.add(new JLabel("-"));
        dayPanel.add(dayYearField);

            JRadioButton monthRadio = new JRadioButton("Month:");
        monthRadio.setBackground(Color.WHITE);

        JTextField monthMonthField = new JTextField(2);
        JTextField monthDayField   = new JTextField(2);
        JTextField monthYearField  = new JTextField(4);

        JPanel monthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        monthPanel.setBackground(Color.WHITE);
        monthPanel.add(monthRadio);
        monthPanel.add(monthMonthField);
        monthPanel.add(new JLabel("-"));
        monthPanel.add(monthDayField);
        monthPanel.add(new JLabel("-"));
        monthPanel.add(monthYearField);

        JRadioButton yearRadio = new JRadioButton("Year:");
        yearRadio.setBackground(Color.WHITE);

        JTextField yearMonthField = new JTextField(2);
        JTextField yearDayField   = new JTextField(2);
        JTextField yearYearField  = new JTextField(4);

        JPanel yearPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        yearPanel.setBackground(Color.WHITE);
        yearPanel.add(yearRadio);
        yearPanel.add(yearMonthField);
        yearPanel.add(new JLabel("-"));
        yearPanel.add(yearDayField);
        yearPanel.add(new JLabel("-"));
        yearPanel.add(yearYearField);

        group.add(dayRadio);
        group.add(monthRadio);
        group.add(yearRadio);

        filterPanel.add(dayPanel);
        filterPanel.add(monthPanel);
        filterPanel.add(yearPanel);
    }


    public void createSummaryPanel()
{
    summaryPanel = new JPanel(new GridLayout(0, 4, 10, 10));
    summaryPanel.setBorder(BorderFactory.createTitledBorder("Revenue Statistics"));
    summaryPanel.setBackground(Color.WHITE);

    String[] summaryTitles = {
        "Paid Payments", "Unpaid Payments", "Total Payments", "Total Revenue",
        "Average Revenue", "Highest Customer Payment", "Highest City Revenue"
    };

    summaryLabels = new JLabel[7];

    for (int i = 0; i < summaryTitles.length; i++) {
        JPanel statPanel = new JPanel(new BorderLayout());
        statPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 0, 0)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        statPanel.setBackground(new Color(255, 240, 240));
        statPanel.setPreferredSize(new Dimension(160, 60));

        JLabel titleLabel = new JLabel(summaryTitles[i], SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 10));

        summaryLabels[i] = new JLabel("0", SwingConstants.CENTER);
        summaryLabels[i].setFont(new Font("Arial", Font.BOLD, 14));
        summaryLabels[i].setForeground(new Color(220, 0, 0));

        statPanel.add(titleLabel, BorderLayout.NORTH);
        statPanel.add(summaryLabels[i], BorderLayout.CENTER);
        summaryPanel.add(statPanel);
    }
}


     public void updateSummaryPanel(Map<String, Object> summaryData) {
        SwingUtilities.invokeLater(() -> {
            summaryLabels[0].setText(String.valueOf(summaryData.getOrDefault("paid_payments", 0)));
            summaryLabels[1].setText(String.valueOf(summaryData.getOrDefault("unpaid_payments", 0)));
            summaryLabels[2].setText(String.valueOf(summaryData.getOrDefault("total_payments", 0)));
            summaryLabels[3].setText(String.valueOf(summaryData.getOrDefault("total_revenue", 0)));
            summaryLabels[4].setText(String.valueOf(summaryData.getOrDefault("average_revenue", 0)));
            
            String highestCustomer = (String) summaryData.getOrDefault("highestCustomer", "None");
            summaryLabels[5].setText(highestCustomer.length() > 15 ? highestCustomer.substring(0, 15) + "..." : highestCustomer);
            summaryLabels[5].setToolTipText(highestCustomer);
            
            String highCity = (String) summaryData.getOrDefault("highest_city", "None");
            summaryLabels[6].setText(highCity.length() > 15 ? highCity.substring(0, 15) + "..." : highCity);
            summaryLabels[6].setToolTipText(highCity);
        });
    }

    public void updatePaymentsTable(DefaultTableModel model) {
        SwingUtilities.invokeLater(() -> {
            paymentsTable.setModel(model);
            paymentsTable.getTableHeader().setBackground(new Color(220, 0, 0));
            paymentsTable.getTableHeader().setForeground(Color.WHITE);
            paymentsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
            
            // Set column widths for better date display
            if (paymentsTable.getColumnCount() >= 8) {
                paymentsTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Payment ID
                paymentsTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Paid or not
                paymentsTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Price
                paymentsTable.getColumnModel().getColumn(3).setPreferredWidth(120); // City ID
                paymentsTable.getColumnModel().getColumn(4).setPreferredWidth(80); // City Name
                paymentsTable.getColumnModel().getColumn(5).setPreferredWidth(150); // Customer ID
                paymentsTable.getColumnModel().getColumn(6).setPreferredWidth(120); // Customer Name
                paymentsTable.getColumnModel().getColumn(7).setPreferredWidth(180); // Revenue made by customer
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

    private JPanel wrapTable(JScrollPane tableScroll) 
    {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    panel.add(tableScroll, BorderLayout.CENTER);
    return panel;
    }
    
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
