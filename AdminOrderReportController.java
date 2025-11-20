import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class AdminOrderReportController {
    private AdminOrderReportView view;
    private static AdminOrderReportController currentInstance;
    private Timer autoRefreshTimer;
    
    // Store current filter settings
    private String currentFilterType = "Day";
    private String currentDayMonth = "";
    private String currentDayDay = "";
    private String currentDayYear = "";
    private String currentMonthMonth = "";
    private String currentMonthYear = "";
    private String currentYearYear = "";

    public AdminOrderReportController() {
        if (currentInstance != null) {
            currentInstance.cleanup();
        }
        
        this.view = new AdminOrderReportView(this);
        currentInstance = this;
        
        initializeController();
        startAutoRefresh();
        
        // Initialize with current date values
        initializeFilterWithCurrentDate();
    }

    public static AdminOrderReportController getInstance() {
        return currentInstance;
    }

    private void initializeController() {
        view.getBackButton().addActionListener(e -> {
            cleanup();
            goBackToAdminHome();
        });

        view.getApplyFilterButton().addActionListener(e -> {
            applyFilter();
        });

        // Load initial data with current date
        refreshDataWithCurrentFilter();
    }

    private void initializeFilterWithCurrentDate() {
        // Set initial filter values to current date
        java.time.LocalDate now = java.time.LocalDate.now();
        this.currentDayMonth = String.valueOf(now.getMonthValue());
        this.currentDayDay = String.valueOf(now.getDayOfMonth());
        this.currentDayYear = String.valueOf(now.getYear());
        this.currentMonthMonth = String.valueOf(now.getMonthValue());
        this.currentMonthYear = String.valueOf(now.getYear());
        this.currentYearYear = String.valueOf(now.getYear());
    }

    private void cleanup() {
        stopAutoRefresh();
        currentInstance = null;
    }

    private void applyFilter() {
        if (!view.validateFilterInput()) {
            return;
        }
        
        // Store the current filter settings
        storeCurrentFilterSettings();
        
        refreshDataWithCurrentFilter();
        view.showSuccessMessage("Filter applied successfully!");
    }
    
    private void storeCurrentFilterSettings() {
        this.currentFilterType = view.getFilterType();
        
        // Store the current filter values from the view
        switch (currentFilterType) {
            case "Day":
                this.currentDayMonth = view.getDayMonth();
                this.currentDayDay = view.getDayDay();
                this.currentDayYear = view.getDayYear();
                break;
            case "Month":
                this.currentMonthMonth = view.getMonthMonth();
                this.currentMonthYear = view.getMonthYear();
                break;
            case "Year":
                this.currentYearYear = view.getYearYear();
                break;
        }
    }

    private void refreshDataWithCurrentFilter() {
        try {
            Map<String, Object> summaryData = getSummaryData(currentFilterType);
            DefaultTableModel ordersModel = getOrdersData(currentFilterType);
            DefaultTableModel customersModel = getCustomersData(currentFilterType);
            DefaultTableModel citiesModel = getCitiesData(currentFilterType);
            
            view.updateSummaryPanel(summaryData);
            view.updateOrdersTable(ordersModel);
            view.updateCustomersTable(customersModel);
            view.updateCitiesTable(citiesModel);
            
        } catch (SQLException e) {
            e.printStackTrace();
            view.showErrorMessage("Error loading order report data: " + e.getMessage());
        }
    }

    private void startAutoRefresh() {
        autoRefreshTimer = new Timer(true);
        // Refresh every 3 seconds for more real-time updates
        autoRefreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    refreshDataWithCurrentFilter();
                });
            }
        }, 3000, 3000); // 3 seconds for better real-time experience
    }

    private void stopAutoRefresh() {
        if (autoRefreshTimer != null) {
            autoRefreshTimer.cancel();
            autoRefreshTimer = null;
        }
    }

    private void goBackToAdminHome() {
        view.getFrame().dispose();
        AdminHomePageView homePageView = new AdminHomePageView();
        new AdminHomePageController(homePageView, 1);
    }

    public void forceRefresh() {
        SwingUtilities.invokeLater(() -> {
            refreshDataWithCurrentFilter();
        });
    }

    private Map<String, Object> getSummaryData(String filterType) throws SQLException {
        Map<String, Object> summaryData = new HashMap<>();
        
        try (Connection conn = DBConnection.getConnection()) {
            String dateCondition = getDateCondition(filterType);
            String customerDateFilter = getCustomerDateFilterForExistence(filterType);
            String cityDateFilter = getCityDateFilterForExistence(filterType);
            
            // Get order counts by status - INCLUDES REAL-TIME STATUS UPDATES
            String statusQuery = 
                "SELECT " +
                "COUNT(*) as total_orders, " +
                "SUM(CASE WHEN status = 'Pending' THEN 1 ELSE 0 END) as pending_orders, " +
                "SUM(CASE WHEN status = 'Preparing' THEN 1 ELSE 0 END) as preparing_orders, " +
                "SUM(CASE WHEN status = 'In Transit' THEN 1 ELSE 0 END) as in_transit_orders, " +
                "SUM(CASE WHEN status = 'Delivered' THEN 1 ELSE 0 END) as delivered_orders, " +
                "SUM(CASE WHEN status = 'Cancelled' THEN 1 ELSE 0 END) as cancelled_orders, " +
                "SUM(CASE WHEN status = 'Discarded' THEN 1 ELSE 0 END) as discarded_orders " +
                "FROM Orders o " +
                "JOIN Customers c ON o.customer_id = c.customer_id " +
                "JOIN Cities city ON c.city_id = city.city_id " +
                "WHERE " + dateCondition + " " +
                "AND " + customerDateFilter + " " +
                "AND " + cityDateFilter;

            PreparedStatement statusStmt = conn.prepareStatement(statusQuery);
            ResultSet statusRs = statusStmt.executeQuery();
            
            if (statusRs.next()) {
                summaryData.put("total_orders", statusRs.getInt("total_orders"));
                summaryData.put("pending_orders", statusRs.getInt("pending_orders"));
                summaryData.put("preparing_orders", statusRs.getInt("preparing_orders"));
                summaryData.put("in_transit_orders", statusRs.getInt("in_transit_orders"));
                summaryData.put("delivered_orders", statusRs.getInt("delivered_orders"));
                summaryData.put("cancelled_orders", statusRs.getInt("cancelled_orders"));
                summaryData.put("discarded_orders", statusRs.getInt("discarded_orders"));
            }
            statusRs.close();
            statusStmt.close();
            
            // Get total order lines count - SIMILAR TO ORDER COUNTER
            String orderLinesQuery = 
                "SELECT COUNT(*) as total_order_lines " +
                "FROM Order_Lines ol " +
                "JOIN Orders o ON ol.order_id = o.order_id " +
                "JOIN Customers c ON o.customer_id = c.customer_id " +
                "JOIN Cities city ON c.city_id = city.city_id " +
                "WHERE " + dateCondition + " " +
                "AND " + customerDateFilter + " " +
                "AND " + cityDateFilter;

            PreparedStatement orderLinesStmt = conn.prepareStatement(orderLinesQuery);
            ResultSet orderLinesRs = orderLinesStmt.executeQuery();
            
            if (orderLinesRs.next()) {
                summaryData.put("total_order_lines", orderLinesRs.getInt("total_order_lines"));
            }
            orderLinesRs.close();
            orderLinesStmt.close();
            
            // Get order lines by status - SIMILAR TO ORDER STATUS COUNTERS
            String orderLinesStatusQuery = 
                "SELECT " +
                "SUM(CASE WHEN o.status = 'Pending' THEN 1 ELSE 0 END) as pending_order_lines, " +
                "SUM(CASE WHEN o.status = 'Preparing' THEN 1 ELSE 0 END) as preparing_order_lines, " +
                "SUM(CASE WHEN o.status = 'In Transit' THEN 1 ELSE 0 END) as in_transit_order_lines, " +
                "SUM(CASE WHEN o.status = 'Delivered' THEN 1 ELSE 0 END) as delivered_order_lines, " +
                "SUM(CASE WHEN o.status = 'Cancelled' THEN 1 ELSE 0 END) as cancelled_order_lines, " +
                "SUM(CASE WHEN o.status = 'Discarded' THEN 1 ELSE 0 END) as discarded_order_lines " +
                "FROM Order_Lines ol " +
                "JOIN Orders o ON ol.order_id = o.order_id " +
                "JOIN Customers c ON o.customer_id = c.customer_id " +
                "JOIN Cities city ON c.city_id = city.city_id " +
                "WHERE " + dateCondition + " " +
                "AND " + customerDateFilter + " " +
                "AND " + cityDateFilter;

            PreparedStatement orderLinesStatusStmt = conn.prepareStatement(orderLinesStatusQuery);
            ResultSet orderLinesStatusRs = orderLinesStatusStmt.executeQuery();
            
            if (orderLinesStatusRs.next()) {
                summaryData.put("pending_order_lines", orderLinesStatusRs.getInt("pending_order_lines"));
                summaryData.put("preparing_order_lines", orderLinesStatusRs.getInt("preparing_order_lines"));
                summaryData.put("in_transit_order_lines", orderLinesStatusRs.getInt("in_transit_order_lines"));
                summaryData.put("delivered_order_lines", orderLinesStatusRs.getInt("delivered_order_lines"));
                summaryData.put("cancelled_order_lines", orderLinesStatusRs.getInt("cancelled_order_lines"));
                summaryData.put("discarded_order_lines", orderLinesStatusRs.getInt("discarded_order_lines"));
            }
            orderLinesStatusRs.close();
            orderLinesStatusStmt.close();
            
            // Get top customer - only active customers that existed during the period
            String topCustomerQuery = 
                "SELECT CONCAT(c.first_name, ' ', c.last_name) as customer_name, " +
                "COUNT(o.order_id) as order_count " +
                "FROM Customers c " +
                "JOIN Orders o ON c.customer_id = o.customer_id " +
                "JOIN Cities city ON c.city_id = city.city_id " +
                "WHERE " + dateCondition + " " +
                "AND " + customerDateFilter + " " +
                "AND " + cityDateFilter + " " +
                "GROUP BY c.customer_id, c.first_name, c.last_name " +
                "ORDER BY order_count DESC " +
                "LIMIT 1";
            
            PreparedStatement customerStmt = conn.prepareStatement(topCustomerQuery);
            ResultSet customerRs = customerStmt.executeQuery();
            if (customerRs.next()) {
                String customerInfo = customerRs.getString("customer_name") + " (" + customerRs.getInt("order_count") + ")";
                summaryData.put("top_customer", customerInfo);
            } else {
                summaryData.put("top_customer", "None");
            }
            customerRs.close();
            customerStmt.close();
            
            // Get top city - only cities that existed during the period
            String topCityQuery = 
                "SELECT city.city_name, COUNT(o.order_id) as order_count " +
                "FROM Cities city " +
                "JOIN Customers c ON city.city_id = c.city_id " +
                "JOIN Orders o ON c.customer_id = o.customer_id " +
                "WHERE " + dateCondition + " " +
                "AND " + customerDateFilter + " " +
                "AND " + cityDateFilter + " " +
                "GROUP BY city.city_id, city.city_name " +
                "ORDER BY order_count DESC " +
                "LIMIT 1";
            
            PreparedStatement cityStmt = conn.prepareStatement(topCityQuery);
            ResultSet cityRs = cityStmt.executeQuery();
            if (cityRs.next()) {
                String cityInfo = cityRs.getString("city_name") + " (" + cityRs.getInt("order_count") + ")";
                summaryData.put("top_city", cityInfo);
            } else {
                summaryData.put("top_city", "None");
            }
            cityRs.close();
            cityStmt.close();
        }
        
        return summaryData;
    }

    private DefaultTableModel getOrdersData(String filterType) throws SQLException {
        String[] columnNames = {
            "Order ID", "Status", "Order Lines", 
            "Customer ID", "Customer Name", "City", "Order Date"
        };
        
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        try (Connection conn = DBConnection.getConnection()) {
            String dateCondition = getDateCondition(filterType);
            String customerDateFilter = getCustomerDateFilterForExistence(filterType);
            String cityDateFilter = getCityDateFilterForExistence(filterType);
            
            String ordersQuery = 
                "SELECT o.order_id, o.status, " +
                "COUNT(ol.order_line_id) as order_lines_count, " +
                "c.customer_id, CONCAT(c.first_name, ' ', c.last_name) as customer_name, " +
                "city.city_name, DATE_FORMAT(o.order_date, '%Y-%m-%d %H:%i:%s') as formatted_order_date " +
                "FROM Orders o " +
                "JOIN Customers c ON o.customer_id = c.customer_id " +
                "JOIN Cities city ON c.city_id = city.city_id " +
                "LEFT JOIN Order_Lines ol ON o.order_id = ol.order_id " +
                "WHERE " + dateCondition + " " +
                "AND " + customerDateFilter + " " +
                "AND " + cityDateFilter + " " +
                "GROUP BY o.order_id, o.status, c.customer_id, c.first_name, c.last_name, city.city_name, o.order_date " +
                "ORDER BY o.order_date DESC";
            
            PreparedStatement stmt = conn.prepareStatement(ordersQuery);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("order_id"),
                    rs.getString("status"),
                    rs.getInt("order_lines_count"),
                    rs.getInt("customer_id"),
                    rs.getString("customer_name"),
                    rs.getString("city_name"),
                    rs.getString("formatted_order_date")
                };
                model.addRow(row);
            }
            
            rs.close();
            stmt.close();
        }
        
        return model;
    }

    private DefaultTableModel getCustomersData(String filterType) throws SQLException {
        String[] columnNames = {"Customer ID", "Customer Name", "City", "Total Orders"};
        
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        try (Connection conn = DBConnection.getConnection()) {
            String dateCondition = getDateCondition(filterType);
            String customerDateFilter = getCustomerDateFilterForExistence(filterType);
            String cityDateFilter = getCityDateFilterForExistence(filterType);
            
            String customersQuery = 
                "SELECT c.customer_id, CONCAT(c.first_name, ' ', c.last_name) as customer_name, " +
                "city.city_name, COUNT(o.order_id) as total_orders " +
                "FROM Customers c " +
                "JOIN Cities city ON c.city_id = city.city_id " +
                "LEFT JOIN Orders o ON c.customer_id = o.customer_id AND " + dateCondition +
                " WHERE " + customerDateFilter + " " +
                "AND " + cityDateFilter + " " +
                "GROUP BY c.customer_id, c.first_name, c.last_name, city.city_name " +
                "ORDER BY total_orders DESC";
            
            PreparedStatement stmt = conn.prepareStatement(customersQuery);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("customer_id"),
                    rs.getString("customer_name"),
                    rs.getString("city_name"),
                    rs.getInt("total_orders")
                };
                model.addRow(row);
            }
            
            rs.close();
            stmt.close();
        }
        
        return model;
    }

    private DefaultTableModel getCitiesData(String filterType) throws SQLException {
        String[] columnNames = {"City ID", "City Name", "Total Orders"};
        
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        try (Connection conn = DBConnection.getConnection()) {
            String dateCondition = getDateCondition(filterType);
            String customerDateFilter = getCustomerDateFilterForExistence(filterType);
            String cityDateFilter = getCityDateFilterForExistence(filterType);
            
            String citiesQuery = 
                "SELECT city.city_id, city.city_name, " +
                "COALESCE(COUNT(o.order_id), 0) as total_orders " +
                "FROM Cities city " +
                "LEFT JOIN Customers c ON city.city_id = c.city_id " +
                "LEFT JOIN Orders o ON c.customer_id = o.customer_id AND " + dateCondition +
                " WHERE " + customerDateFilter + " " +
                "AND " + cityDateFilter + " " +
                "GROUP BY city.city_id, city.city_name " +
                "ORDER BY total_orders DESC, city.city_name ASC";
            
            PreparedStatement stmt = conn.prepareStatement(citiesQuery);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("city_id"),
                    rs.getString("city_name"),
                    rs.getInt("total_orders")
                };
                model.addRow(row);
            }
            
            rs.close();
            stmt.close();
        }
        
        return model;
    }

    private String getDateCondition(String filterType) {
        try {
            switch (filterType) {
                case "Day":
                    // Use stored filter values instead of reading from view
                    int dayMonth = Integer.parseInt(currentDayMonth.isEmpty() ? view.getDayMonth() : currentDayMonth);
                    int dayDay = Integer.parseInt(currentDayDay.isEmpty() ? view.getDayDay() : currentDayDay);
                    int dayYear = Integer.parseInt(currentDayYear.isEmpty() ? view.getDayYear() : currentDayYear);
                    return String.format("DATE(o.order_date) = '%04d-%02d-%02d'", dayYear, dayMonth, dayDay);
                    
                case "Month":
                    // Use stored filter values instead of reading from view
                    int monthMonth = Integer.parseInt(currentMonthMonth.isEmpty() ? view.getMonthMonth() : currentMonthMonth);
                    int monthYear = Integer.parseInt(currentMonthYear.isEmpty() ? view.getMonthYear() : currentMonthYear);
                    return String.format("YEAR(o.order_date) = %d AND MONTH(o.order_date) = %d", monthYear, monthMonth);
                    
                case "Year":
                    // Use stored filter values instead of reading from view
                    int yearYear = Integer.parseInt(currentYearYear.isEmpty() ? view.getYearYear() : currentYearYear);
                    return String.format("YEAR(o.order_date) = %d", yearYear);
                    
                default:
                    return "1=1";
            }
        } catch (NumberFormatException e) {
            return "1=1";
        }
    }

    private String getCustomerDateFilterForExistence(String filterType) {
        try {
            switch (filterType) {
                case "Day":
                    int dayMonth = Integer.parseInt(currentDayMonth.isEmpty() ? view.getDayMonth() : currentDayMonth);
                    int dayDay = Integer.parseInt(currentDayDay.isEmpty() ? view.getDayDay() : currentDayDay);
                    int dayYear = Integer.parseInt(currentDayYear.isEmpty() ? view.getDayYear() : currentDayYear);
                    return String.format("DATE(c.created_date) <= '%04d-%02d-%02d' AND c.is_active = 1", dayYear, dayMonth, dayDay);
                    
                case "Month":
                    int monthMonth = Integer.parseInt(currentMonthMonth.isEmpty() ? view.getMonthMonth() : currentMonthMonth);
                    int monthYear = Integer.parseInt(currentMonthYear.isEmpty() ? view.getMonthYear() : currentMonthYear);
                    return String.format("c.created_date <= LAST_DAY('%04d-%02d-01') AND c.is_active = 1", monthYear, monthMonth);
                    
                case "Year":
                    int yearYear = Integer.parseInt(currentYearYear.isEmpty() ? view.getYearYear() : currentYearYear);
                    return String.format("c.created_date <= '%04d-12-31' AND c.is_active = 1", yearYear);
                    
                default:
                    return "c.is_active = 1";
            }
        } catch (NumberFormatException e) {
            return "c.is_active = 1";
        }
    }

    private String getCityDateFilterForExistence(String filterType) {
        try {
            switch (filterType) {
                case "Day":
                    int dayMonth = Integer.parseInt(currentDayMonth.isEmpty() ? view.getDayMonth() : currentDayMonth);
                    int dayDay = Integer.parseInt(currentDayDay.isEmpty() ? view.getDayDay() : currentDayDay);
                    int dayYear = Integer.parseInt(currentDayYear.isEmpty() ? view.getDayYear() : currentDayYear);
                    return String.format("DATE(city.created_date) <= '%04d-%02d-%02d'", dayYear, dayMonth, dayDay);
                    
                case "Month":
                    int monthMonth = Integer.parseInt(currentMonthMonth.isEmpty() ? view.getMonthMonth() : currentMonthMonth);
                    int monthYear = Integer.parseInt(currentMonthYear.isEmpty() ? view.getMonthYear() : currentMonthYear);
                    return String.format("city.created_date <= LAST_DAY('%04d-%02d-01')", monthYear, monthMonth);
                    
                case "Year":
                    int yearYear = Integer.parseInt(currentYearYear.isEmpty() ? view.getYearYear() : currentYearYear);
                    return String.format("city.created_date <= '%04d-12-31'", yearYear);
                    
                default:
                    return "1=1";
            }
        } catch (NumberFormatException e) {
            return "1=1";
        }
    }
}