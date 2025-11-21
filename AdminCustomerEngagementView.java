import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;
import java.util.Calendar;

public class AdminCustomerEngagementView {
    private JFrame frame;
    private JPanel mainPanel, headerPanel, filterPanel, summaryPanel;
    private JButton backButton, applyFilterButton;
    private JTable customerTable, cityTable;
    private JLabel[] summaryLabels;
    
    // Filter components (SAME STRUCTURE as Menu Report)
    private JRadioButton dayRadio, monthRadio, yearRadio;
    private ButtonGroup filterGroup;
    private JTextField dayMonthField, dayDayField, dayYearField;
    private JTextField monthMonthField, monthYearField;
    private JTextField yearYearField;
    private JPanel dayPanel, monthPanel, yearPanel;
    private AdminCustomerEngagementController controller;

    public AdminCustomerEngagementView(AdminCustomerEngagementController controller) {
        this.controller = controller;
        initializeUI();
    }

    private void initializeUI() {
        // Frame setup (SAME as Menu Report)
        frame = new JFrame("Customer Engagement Report");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 700);
        frame.setLocationRelativeTo(null);

        // Main panel with scroll (SAME as Menu Report)
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        frame.add(scrollPane);

        // Header with back button (SAME structure, different title)
        createHeaderPanel();
        mainPanel.add(headerPanel);

        // Filter panel (SAME as Menu Report)
        createFilterPanel();
        mainPanel.add(filterPanel);

        // Summary panel (DIFFERENT metrics)
        createSummaryPanel();
        mainPanel.add(summaryPanel);

        // Tables in tabs (SAME structure, different tables)
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Customer table - first tab
        customerTable = new JTable();
        customerTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane customerScroll = new JScrollPane(customerTable);
        customerScroll.setPreferredSize(new Dimension(1100, 300));
        tabbedPane.addTab("Customer Engagement Details", customerScroll);
        
        // City table - second tab
        cityTable = new JTable();
        cityTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane cityScroll = new JScrollPane(cityTable);
        cityScroll.setPreferredSize(new Dimension(1100, 300));
        tabbedPane.addTab("City Performance Breakdown", cityScroll);
        
        mainPanel.add(tabbedPane);

        frame.setVisible(true);
    }

    private void createHeaderPanel() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.setBackground(new Color(220, 0, 0));

        // Back button (SAME as Menu Report)
        backButton = new JButton("Back to Home");
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(new Color(220, 0, 0));
        backButton.setFocusPainted(false);

        // Title (DIFFERENT title)
        JLabel titleLabel = new JLabel("KOREAN EXPRESS - CUSTOMER ENGAGEMENT REPORTS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
    }

    // ALL FILTER METHODS REMAIN EXACTLY THE SAME as Menu Report
    private void createFilterPanel() {
        filterPanel = new JPanel(new BorderLayout());
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter Options"));
        filterPanel.setBackground(Color.WHITE);
        
        JPanel filterContainer = new JPanel(new GridLayout(4, 1, 5, 5));
        filterContainer.setBackground(Color.WHITE);
        
        dayRadio = new JRadioButton("Day:");
        monthRadio = new JRadioButton("Month:");
        yearRadio = new JRadioButton("Year:");
        
        filterGroup = new ButtonGroup();
        filterGroup.add(dayRadio);
        filterGroup.add(monthRadio);
        filterGroup.add(yearRadio);
        
        dayRadio.setSelected(true);
        
        createDayPanel();
        createMonthPanel();
        createYearPanel();
        
        applyFilterButton = new JButton("Apply Filter");
        applyFilterButton.setBackground(new Color(220, 0, 0));
        applyFilterButton.setForeground(Color.WHITE);
        applyFilterButton.setFocusPainted(false);
        
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
        
        dayRadio.addActionListener(e -> updateFieldStates());
        monthRadio.addActionListener(e -> updateFieldStates());
        yearRadio.addActionListener(e -> updateFieldStates());
        
        updateFieldStates();
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
        
        dayMonthField.setText(String.valueOf(currentMonth));
        dayDayField.setText(String.valueOf(currentDay));
        dayYearField.setText(String.valueOf(currentYear));
        
        monthMonthField.setText(String.valueOf(currentMonth));
        monthYearField.setText(String.valueOf(currentYear));
        
        yearYearField.setText(String.valueOf(currentYear));
    }

    private void createSummaryPanel() {
        summaryPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary Statistics"));
        summaryPanel.setBackground(Color.WHITE);

        // DIFFERENT summary titles for Customer Engagement
        String[] summaryTitles = {
            "Active Customers", "Deleted Accounts", "Total Orders", "Total Payments",
            "Total Amount Spent", "Total Amount Refunded", "Payment Completion Rate", "Last Purchase Date"
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
            summaryLabels[0].setText(String.valueOf(summaryData.getOrDefault("active_customers", 0)));
            summaryLabels[1].setText(String.valueOf(summaryData.getOrDefault("deleted_accounts", 0)));
            summaryLabels[2].setText(String.valueOf(summaryData.getOrDefault("total_orders", 0)));
            summaryLabels[3].setText(String.valueOf(summaryData.getOrDefault("total_payments", 0)));
            summaryLabels[4].setText(String.format("₱%.2f", summaryData.getOrDefault("total_amount_spent", 0.0)));
            summaryLabels[5].setText(String.format("₱%.2f", summaryData.getOrDefault("total_amount_refunded", 0.0)));
            summaryLabels[6].setText(String.format("%.2f%%", summaryData.getOrDefault("payment_completion_rate", 0.0)));
            
            Object lastPurchase = summaryData.get("last_purchase_date");
            if (lastPurchase instanceof java.sql.Timestamp) {
                java.sql.Timestamp ts = (java.sql.Timestamp) lastPurchase;
                summaryLabels[7].setText(new java.text.SimpleDateFormat("yyyy-MM-dd").format(ts));
            } else if (lastPurchase instanceof java.util.Date) {
                summaryLabels[7].setText(new java.text.SimpleDateFormat("yyyy-MM-dd").format((java.util.Date) lastPurchase));
            } else {
                summaryLabels[7].setText("Never");
            }
        });
    }

    public void updateCustomerTable(DefaultTableModel model) {
        SwingUtilities.invokeLater(() -> {
            customerTable.setModel(model);
            customerTable.getTableHeader().setBackground(new Color(220, 0, 0));
            customerTable.getTableHeader().setForeground(Color.WHITE);
            customerTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
            
            if (customerTable.getColumnCount() >= 9) {
                customerTable.getColumnModel().getColumn(0).setPreferredWidth(80);   // Customer ID
                customerTable.getColumnModel().getColumn(1).setPreferredWidth(200);  // Full Name
                customerTable.getColumnModel().getColumn(2).setPreferredWidth(200);  // Email
                customerTable.getColumnModel().getColumn(3).setPreferredWidth(150);  // City
                customerTable.getColumnModel().getColumn(4).setPreferredWidth(100);  // Total Orders
                customerTable.getColumnModel().getColumn(5).setPreferredWidth(120);  // Products Purchased
                customerTable.getColumnModel().getColumn(6).setPreferredWidth(100);  // Total Payments
                customerTable.getColumnModel().getColumn(7).setPreferredWidth(120);  // Total Amount Spent
                customerTable.getColumnModel().getColumn(8).setPreferredWidth(120);  // Last Purchase
            }
        });
    }

    public void updateCityTable(DefaultTableModel model) {
        SwingUtilities.invokeLater(() -> {
            cityTable.setModel(model);
            cityTable.getTableHeader().setBackground(new Color(220, 0, 0));
            cityTable.getTableHeader().setForeground(Color.WHITE);
            cityTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
            
            if (cityTable.getColumnCount() >= 8) {
                cityTable.getColumnModel().getColumn(0).setPreferredWidth(80);   // City ID
                cityTable.getColumnModel().getColumn(1).setPreferredWidth(200);  // City Name
                cityTable.getColumnModel().getColumn(2).setPreferredWidth(120);  // Active Customers
                cityTable.getColumnModel().getColumn(3).setPreferredWidth(120);  // Total Orders
                cityTable.getColumnModel().getColumn(4).setPreferredWidth(120);  // Total Revenue
                cityTable.getColumnModel().getColumn(5).setPreferredWidth(120);  // Payment Completion
                cityTable.getColumnModel().getColumn(6).setPreferredWidth(120);  // Last Purchase
                cityTable.getColumnModel().getColumn(7).setPreferredWidth(100);  // Delivery Fee
            }
        });
    }

    // ALL VALIDATION METHODS REMAIN EXACTLY THE SAME as Menu Report
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

    // GETTER METHODS REMAIN EXACTLY THE SAME as Menu Report
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