import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;
import java.util.Calendar;

public class AdminOrderReportView {
    private JFrame frame;
    private JPanel mainPanel, headerPanel, filterPanel, summaryPanel;
    private JButton backButton, applyFilterButton;
    private JTable ordersTable, customersTable, citiesTable;
    private JLabel[] summaryLabels;
    
    // Filter components
    private JRadioButton dayRadio, monthRadio, yearRadio;
    private ButtonGroup filterGroup;
    private JTextField dayMonthField, dayDayField, dayYearField;
    private JTextField monthMonthField, monthYearField;
    private JTextField yearYearField;
    private JPanel dayPanel, monthPanel, yearPanel;
    
    private AdminOrderReportController controller;

    public AdminOrderReportView(AdminOrderReportController controller) {
        this.controller = controller;
        initializeUI();
    }

    private void initializeUI() {
        // Frame setup
        frame = new JFrame("Order Report");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1250, 700);
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
        
        // Orders table
        ordersTable = new JTable();
        ordersTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane ordersScroll = new JScrollPane(ordersTable);
        ordersScroll.setPreferredSize(new Dimension(1100, 150));
        tabbedPane.addTab("Order Details", ordersScroll);
        
        // Customers table
        customersTable = new JTable();
        customersTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane customersScroll = new JScrollPane(customersTable);
        customersScroll.setPreferredSize(new Dimension(1100, 150));
        tabbedPane.addTab("Customer Orders", customersScroll);
        
        // Cities table
        citiesTable = new JTable();
        citiesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane citiesScroll = new JScrollPane(citiesTable);
        citiesScroll.setPreferredSize(new Dimension(1100, 150));
        tabbedPane.addTab("City Orders", citiesScroll);
        
        mainPanel.add(tabbedPane);

        frame.setVisible(true);
    }

    private void createHeaderPanel() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        headerPanel.setBackground(new Color(220, 0, 0));
        headerPanel.setPreferredSize(new Dimension(1200, 40));

        // Back button
        backButton = new JButton("Back to Home");
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(new Color(220, 0, 0));
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(120, 30));

        // Title
        JLabel titleLabel = new JLabel("KOREAN EXPRESS - ORDER REPORTS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
    }

    private void createFilterPanel() {
        filterPanel = new JPanel(new BorderLayout());
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter Options"));
        filterPanel.setBackground(Color.WHITE);
        
        // Main filter container
        JPanel filterContainer = new JPanel(new GridLayout(4, 1, 5, 5));
        filterContainer.setBackground(Color.WHITE);
        
        // Initialize radio buttons
        dayRadio = new JRadioButton("Day:");
        monthRadio = new JRadioButton("Month:");
        yearRadio = new JRadioButton("Year:");
        
        filterGroup = new ButtonGroup();
        filterGroup.add(dayRadio);
        filterGroup.add(monthRadio);
        filterGroup.add(yearRadio);
        
        // Set default selection
        dayRadio.setSelected(true);
        
        // Create input panels
        createDayPanel();
        createMonthPanel();
        createYearPanel();
        
        // Apply filter button
        applyFilterButton = new JButton("Apply Filter");
        applyFilterButton.setBackground(new Color(220, 0, 0));
        applyFilterButton.setForeground(Color.WHITE);
        applyFilterButton.setFocusPainted(false);
        
        // Add components to container
        JPanel dayRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dayRow.add(dayRadio);
        dayRow.add(dayPanel);
        filterContainer.add(dayRow);
        
        JPanel monthRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        monthRow.add(monthRadio);
        monthRow.add(monthPanel);
        filterContainer.add(monthRow);
        
        JPanel yearRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        yearRow.add(yearRadio);
        yearRow.add(yearPanel);
        filterContainer.add(yearRow);
        
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonRow.add(applyFilterButton);
        filterContainer.add(buttonRow);
        
        filterPanel.add(filterContainer, BorderLayout.CENTER);
        
        // Add radio button listeners to enable/disable fields
        dayRadio.addActionListener(e -> updateFieldStates());
        monthRadio.addActionListener(e -> updateFieldStates());
        yearRadio.addActionListener(e -> updateFieldStates());
        
        // Set initial field states
        updateFieldStates();
        
        // Set current date as default
        setCurrentDateDefaults();
    }

    private void createDayPanel() {
        dayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        dayPanel.setBackground(Color.WHITE);
        
        dayMonthField = new JTextField(2);
        dayDayField = new JTextField(2);
        dayYearField = new JTextField(4);
        
        dayPanel.add(new JLabel("MM"));
        dayPanel.add(dayMonthField);
        dayPanel.add(new JLabel("- DD"));
        dayPanel.add(dayDayField);
        dayPanel.add(new JLabel("- YYYY"));
        dayPanel.add(dayYearField);
    }

    private void createMonthPanel() {
        monthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        monthPanel.setBackground(Color.WHITE);
        
        monthMonthField = new JTextField(2);
        monthYearField = new JTextField(4);
        
        monthPanel.add(new JLabel("MM"));
        monthPanel.add(monthMonthField);
        monthPanel.add(new JLabel("- YYYY"));
        monthPanel.add(monthYearField);
    }

    private void createYearPanel() {
        yearPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        yearPanel.setBackground(Color.WHITE);
        
        yearYearField = new JTextField(4);
        
        yearPanel.add(new JLabel("YYYY"));
        yearPanel.add(yearYearField);
    }

    private void updateFieldStates() {
        boolean daySelected = dayRadio.isSelected();
        boolean monthSelected = monthRadio.isSelected();
        boolean yearSelected = yearRadio.isSelected();
        
        // Enable/disable fields based on selection
        setFieldsEnabled(dayPanel, daySelected);
        setFieldsEnabled(monthPanel, monthSelected);
        setFieldsEnabled(yearPanel, yearSelected);
    }

    private void setFieldsEnabled(JPanel panel, boolean enabled) {
        Component[] components = panel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JTextField) {
                comp.setEnabled(enabled);
            }
        }
    }

    private void setCurrentDateDefaults() {
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH) + 1;
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);
        
        // Set current date in day fields
        dayMonthField.setText(String.valueOf(currentMonth));
        dayDayField.setText(String.valueOf(currentDay));
        dayYearField.setText(String.valueOf(currentYear));
        
        // Set current date in month fields
        monthMonthField.setText(String.valueOf(currentMonth));
        monthYearField.setText(String.valueOf(currentYear));
        
        // Set current year in year field
        yearYearField.setText(String.valueOf(currentYear));
    }

    private void createSummaryPanel() {
        summaryPanel = new JPanel(new GridLayout(2, 5, 5, 5));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Order Summary Statistics"));
        summaryPanel.setBackground(Color.WHITE);

        String[] summaryTitles = {
            "Total Orders", "Total Order Lines", "Pending Orders", "Prepared Orders", "In Transit Orders", 
            "Delivered Orders", "Cancelled Orders", "Discarded Orders", "Top Customer Orders", "Top City Orders"
        };

        summaryLabels = new JLabel[10];
        
        for (int i = 0; i < 10; i++) {
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
            summaryLabels[1].setText(String.valueOf(summaryData.getOrDefault("total_order_lines", 0)));
            summaryLabels[2].setText(String.valueOf(summaryData.getOrDefault("pending_orders", 0)));
            summaryLabels[3].setText(String.valueOf(summaryData.getOrDefault("prepared_orders", 0)));
            summaryLabels[4].setText(String.valueOf(summaryData.getOrDefault("in_transit_orders", 0)));
            summaryLabels[5].setText(String.valueOf(summaryData.getOrDefault("delivered_orders", 0)));
            summaryLabels[6].setText(String.valueOf(summaryData.getOrDefault("cancelled_orders", 0)));
            summaryLabels[7].setText(String.valueOf(summaryData.getOrDefault("discarded_orders", 0)));
            
            String topCustomer = (String) summaryData.getOrDefault("top_customer", "None");
            summaryLabels[8].setText(topCustomer.length() > 15 ? topCustomer.substring(0, 15) + "..." : topCustomer);
            summaryLabels[8].setToolTipText(topCustomer);
            
            String topCity = (String) summaryData.getOrDefault("top_city", "None");
            summaryLabels[9].setText(topCity.length() > 15 ? topCity.substring(0, 15) + "..." : topCity);
            summaryLabels[9].setToolTipText(topCity);
        });
    }

    public void updateOrdersTable(DefaultTableModel model) {
        SwingUtilities.invokeLater(() -> {
            ordersTable.setModel(model);
            ordersTable.getTableHeader().setBackground(new Color(220, 0, 0));
            ordersTable.getTableHeader().setForeground(Color.WHITE);
            ordersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
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

    public boolean validateFilterInput() {
        if (dayRadio.isSelected()) {
            return validateDayInput();
        } else if (monthRadio.isSelected()) {
            return validateMonthInput();
        } else if (yearRadio.isSelected()) {
            return validateYearInput();
        }
        return false;
    }

    private boolean validateDayInput() {
        try {
            String monthStr = dayMonthField.getText().trim();
            String dayStr = dayDayField.getText().trim();
            String yearStr = dayYearField.getText().trim();
            
            if (monthStr.isEmpty() || dayStr.isEmpty() || yearStr.isEmpty()) {
                showErrorMessage("Please fill all fields for Day filter");
                return false;
            }
            
            int month = Integer.parseInt(monthStr);
            int day = Integer.parseInt(dayStr);
            int year = Integer.parseInt(yearStr);
            
            if (month < 1 || month > 12) {
                showErrorMessage("Month must be between 1 and 12");
                return false;
            }
            
            if (day < 1 || day > 31) {
                showErrorMessage("Day must be between 1 and 31");
                return false;
            }
            
            if (year < 1900 || year > Calendar.getInstance().get(Calendar.YEAR)) {
                showErrorMessage("Year must be between 1900 and current year");
                return false;
            }
            
            if (!isValidDate(month, day, year)) {
                showErrorMessage("Invalid date");
                return false;
            }
            
            return true;
            
        } catch (NumberFormatException e) {
            showErrorMessage("Please enter valid numbers in all fields");
            return false;
        }
    }

    private boolean validateMonthInput() {
        try {
            String monthStr = monthMonthField.getText().trim();
            String yearStr = monthYearField.getText().trim();
            
            if (monthStr.isEmpty() || yearStr.isEmpty()) {
                showErrorMessage("Please fill all fields for Month filter");
                return false;
            }
            
            int month = Integer.parseInt(monthStr);
            int year = Integer.parseInt(yearStr);
            
            if (month < 1 || month > 12) {
                showErrorMessage("Month must be between 1 and 12");
                return false;
            }
            
            if (year < 1900 || year > Calendar.getInstance().get(Calendar.YEAR)) {
                showErrorMessage("Year must be between 1900 and current year");
                return false;
            }
            
            return true;
            
        } catch (NumberFormatException e) {
            showErrorMessage("Please enter valid numbers in all fields");
            return false;
        }
    }

    private boolean validateYearInput() {
        try {
            String yearStr = yearYearField.getText().trim();
            
            if (yearStr.isEmpty()) {
                showErrorMessage("Please enter a year");
                return false;
            }
            
            int year = Integer.parseInt(yearStr);
            
            if (year < 1900 || year > Calendar.getInstance().get(Calendar.YEAR)) {
                showErrorMessage("Year must be between 1900 and current year");
                return false;
            }
            
            return true;
            
        } catch (NumberFormatException e) {
            showErrorMessage("Please enter a valid year");
            return false;
        }
    }

    private boolean isValidDate(int month, int day, int year) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setLenient(false);
            cal.set(year, month - 1, day);
            cal.getTime();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public JFrame getFrame() {
        return frame;
    }

    public JButton getBackButton() {
        return backButton;
    }

    public JButton getApplyFilterButton() {
        return applyFilterButton;
    }

    public String getFilterType() {
        if (dayRadio.isSelected()) {
            return "Day";
        } else if (monthRadio.isSelected()) {
            return "Month";
        } else if (yearRadio.isSelected()) {
            return "Year";
        }
        return "Day";
    }

    public String getDayMonth() {
        return dayMonthField.getText().trim();
    }

    public String getDayDay() {
        return dayDayField.getText().trim();
    }

    public String getDayYear() {
        return dayYearField.getText().trim();
    }

    public String getMonthMonth() {
        return monthMonthField.getText().trim();
    }

    public String getMonthYear() {
        return monthYearField.getText().trim();
    }

    public String getYearYear() {
        return yearYearField.getText().trim();
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(frame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}