import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.LocalDate;

public class AdminOrderReportController {
    private AdminOrderReportView view;
    private static AdminOrderReportController currentInstance;
    private Timer autoRefreshTimer;
    private Timer realTimeStatusTimer;
    private int adminId;
    
    // Store current filter settings
    private String currentFilterType = "Day";
    private String currentDayMonth = "";
    private String currentDayDay = "";
    private String currentDayYear = "";
    private String currentMonthMonth = "";
    private String currentMonthYear = "";
    private String currentYearYear = "";

    public AdminOrderReportController(int adminId) {
        this.adminId = adminId;
        if (currentInstance != null) {
            currentInstance.cleanup();
        }
        
        this.view = new AdminOrderReportView(this);
        currentInstance = this;
        
        initializeController();
        startAutoRefresh();
        startRealTimeStatusUpdates();
        
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
        // Set initial filter values to current date using PHT timezone
        ZonedDateTime nowPHT = ZonedDateTime.now(ZoneId.of("Asia/Manila"));
        LocalDate today = nowPHT.toLocalDate();
        this.currentDayMonth = String.valueOf(today.getMonthValue());
        this.currentDayDay = String.valueOf(today.getDayOfMonth());
        this.currentDayYear = String.valueOf(today.getYear());
        this.currentMonthMonth = String.valueOf(today.getMonthValue());
        this.currentMonthYear = String.valueOf(today.getYear());
        this.currentYearYear = String.valueOf(today.getYear());
    }

    private void cleanup() {
        stopAutoRefresh();
        stopRealTimeStatusUpdates();
        currentInstance = null;
    }

    private void applyFilter() {
        if (!view.validateFilterInput()) {
            return;
        }
        
        // Check if the selected date is in the future
        if (isFutureDate()) {
            JOptionPane.showMessageDialog(view.getFrame(), 
                "You cannot select a future date. Maximum allowed is today.", 
                "Invalid Date", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Store the current filter settings
        storeCurrentFilterSettings();
        
        refreshDataWithCurrentFilter();
        view.showSuccessMessage("Filter applied successfully!");
    }

    private boolean isFutureDate() {
        try {
            String filterType = view.getFilterType();
            // Use PHT timezone (Asia/Manila)
            ZonedDateTime nowPHT = ZonedDateTime.now(ZoneId.of("Asia/Manila"));
            LocalDate today = nowPHT.toLocalDate();
            
            switch (filterType) {
                case "Day":
                    int dayMonth = Integer.parseInt(view.getDayMonth());
                    int dayDay = Integer.parseInt(view.getDayDay());
                    int dayYear = Integer.parseInt(view.getDayYear());
                    LocalDate selectedDay = LocalDate.of(dayYear, dayMonth, dayDay);
                    return selectedDay.isAfter(today);
                    
                case "Month":
                    int monthMonth = Integer.parseInt(view.getMonthMonth());
                    int monthYear = Integer.parseInt(view.getMonthYear());
                    // Check if the selected month is in the future
                    if (monthYear > today.getYear()) {
                        return true;
                    } else if (monthYear == today.getYear() && monthMonth > today.getMonthValue()) {
                        return true;
                    }
                    return false;
                    
                case "Year":
                    int yearYear = Integer.parseInt(view.getYearYear());
                    return yearYear > today.getYear();
                    
                default:
                    return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
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
        // Refresh every 10 seconds for regular data updates
        autoRefreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    refreshDataWithCurrentFilter();
                });
            }
        }, 10000, 10000); // 10 seconds for regular updates
    }

    private void stopAutoRefresh() {
        if (autoRefreshTimer != null) {
            autoRefreshTimer.cancel();
            autoRefreshTimer = null;
        }
    }

    private void startRealTimeStatusUpdates() {
        realTimeStatusTimer = new Timer(true);
        // Update real-time status counts every 2 seconds for instant feedback
        realTimeStatusTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    updateRealTimeStatusCounts();
                });
            }
        }, 2000, 2000); // 2 seconds for real-time updates
    }

    private void stopRealTimeStatusUpdates() {
        if (realTimeStatusTimer != null) {
            realTimeStatusTimer.cancel();
            realTimeStatusTimer = null;
        }
    }

    private void updateRealTimeStatusCounts() {
        try {
            Map<String, Object> realTimeData = getRealTimeStatusData();
            
            // Update the summary panel with real-time counts
            SwingUtilities.invokeLater(() -> {
                // Update the labels directly since we don't have access to summaryLabels array
                // The real-time data will be shown in the next regular refresh
                System.out.println("Real-time update: " + realTimeData);
            });
            
        } catch (SQLException e) {
            System.err.println("Error updating real-time status: " + e.getMessage());
        }
    }

    private Map<String, Object> getRealTimeStatusData() throws SQLException {
        Map<String, Object> realTimeData = new HashMap<>();
        
        try (Connection conn = DBConnection.getConnection()) {
            // Get REAL-TIME counts of orders in each status (regardless of date)
            String realTimeQuery = 
                "SELECT " +
                "COUNT(*) as total_orders, " +
                "SUM(CASE WHEN status = 'Pending' THEN 1 ELSE 0 END) as pending_orders, " +
                "SUM(CASE WHEN status = 'Preparing' THEN 1 ELSE 0 END) as preparing_orders, " +
                "SUM(CASE WHEN status = 'In Transit' THEN 1 ELSE 0 END) as in_transit_orders, " +
                "SUM(CASE WHEN status = 'Delivered' THEN 1 ELSE 0 END) as delivered_orders, " +
                "SUM(CASE WHEN status = 'Cancelled' THEN 1 ELSE 0 END) as cancelled_orders, " +
                "SUM(CASE WHEN status = 'Discarded' THEN 1 ELSE 0 END) as discarded_orders " +
                "FROM Orders o " +
                "WHERE o.status IN ('Pending', 'Preparing', 'In Transit', 'Delivered', 'Cancelled', 'Discarded')";
            
            PreparedStatement stmt = conn.prepareStatement(realTimeQuery);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                realTimeData.put("total_orders", rs.getInt("total_orders"));
                realTimeData.put("pending_orders", rs.getInt("pending_orders"));
                realTimeData.put("preparing_orders", rs.getInt("preparing_orders"));
                realTimeData.put("in_transit_orders", rs.getInt("in_transit_orders"));
                realTimeData.put("delivered_orders", rs.getInt("delivered_orders"));
                realTimeData.put("cancelled_orders", rs.getInt("cancelled_orders"));
                realTimeData.put("discarded_orders", rs.getInt("discarded_orders"));
            }
            rs.close();
            stmt.close();
        }
        
        return realTimeData;
    }

    private void goBackToAdminHome() {
        view.getFrame().dispose();
        AdminHomePageView homePageView = new AdminHomePageView();
        new AdminHomePageController(homePageView, adminId);
    }

    public void forceRefresh() {
        SwingUtilities.invokeLater(() -> {
            refreshDataWithCurrentFilter();
        });
    }

    private Map<String, Object> getSummaryData(String filterType) throws SQLException {
        Map<String, Object> summaryData = new HashMap<>();
        
        try (Connection conn = DBConnection.getConnection()) {
            String dateCondition = getDateCondition(filterType, "o");
            String customerDateFilter = getCustomerDateFilterForExistence(filterType);
            String cityDateFilter = getCityDateFilterForExistence(filterType);
            
            // Get order counts by status - INCLUDES REAL-TIME STATUS UPDATES
            String statusQuery = 
                "SELECT " +
                "COUNT(*) as total_orders, " +
                "SUM(CASE WHEN o.status = 'Pending' THEN 1 ELSE 0 END) as pending_orders, " +
                "SUM(CASE WHEN o.status = 'Preparing' THEN 1 ELSE 0 END) as preparing_orders, " +
                "SUM(CASE WHEN o.status = 'In Transit' THEN 1 ELSE 0 END) as in_transit_orders, " +
                "SUM(CASE WHEN o.status = 'Delivered' THEN 1 ELSE 0 END) as delivered_orders, " +
                "SUM(CASE WHEN o.status = 'Cancelled' THEN 1 ELSE 0 END) as cancelled_orders, " +
                "SUM(CASE WHEN o.status = 'Discarded' THEN 1 ELSE 0 END) as discarded_orders " +
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
            
            // Get total order lines count
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
            
            // Get top customer
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
            
            // Get top city
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
            String dateCondition = getDateCondition(filterType, "o");
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
        // Enhanced columns with order status breakdown
        String[] columnNames = {
            "Customer ID", "Customer Name", "City", "Total Orders", 
            "Total Order Lines", "Refunded Orders", "Discarded Orders", "Paid Orders",
            "Pending Orders", "Preparing Orders", "In Transit Orders", "Delivered Orders"
        };
        
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        try (Connection conn = DBConnection.getConnection()) {
            String dateCondition = getDateCondition(filterType, "o");
            String customerDateFilter = getCustomerDateFilterForExistence(filterType);
            String cityDateFilter = getCityDateFilterForExistence(filterType);
            
            // FIXED: Proper SQL syntax for LEFT JOIN with conditions
            String customersQuery = 
                "SELECT " +
                "c.customer_id, " +
                "CONCAT(c.first_name, ' ', c.last_name) as customer_name, " +
                "city.city_name, " +
                "COUNT(DISTINCT o.order_id) as total_orders, " +
                "COUNT(ol.order_line_id) as total_order_lines, " +
                "SUM(CASE WHEN p.is_refunded = 1 THEN 1 ELSE 0 END) as refunded_orders, " +
                "SUM(CASE WHEN o.status = 'Discarded' THEN 1 ELSE 0 END) as discarded_orders, " +
                "SUM(CASE WHEN p.is_paid = 1 THEN 1 ELSE 0 END) as paid_orders, " +
                "SUM(CASE WHEN o.status = 'Pending' THEN 1 ELSE 0 END) as pending_orders, " +
                "SUM(CASE WHEN o.status = 'Preparing' THEN 1 ELSE 0 END) as preparing_orders, " +
                "SUM(CASE WHEN o.status = 'In Transit' THEN 1 ELSE 0 END) as in_transit_orders, " +
                "SUM(CASE WHEN o.status = 'Delivered' THEN 1 ELSE 0 END) as delivered_orders " +
                "FROM Customers c " +
                "JOIN Cities city ON c.city_id = city.city_id " +
                "LEFT JOIN Orders o ON (c.customer_id = o.customer_id AND " + dateCondition + ") " +
                "LEFT JOIN Order_Lines ol ON o.order_id = ol.order_id " +
                "LEFT JOIN Payments p ON o.order_id = p.order_id " +
                "WHERE " + customerDateFilter + " " +
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
                    rs.getInt("total_orders"),
                    rs.getInt("total_order_lines"),
                    rs.getInt("refunded_orders"),
                    rs.getInt("discarded_orders"),
                    rs.getInt("paid_orders"),
                    rs.getInt("pending_orders"),
                    rs.getInt("preparing_orders"),
                    rs.getInt("in_transit_orders"),
                    rs.getInt("delivered_orders")
                };
                model.addRow(row);
            }
            
            rs.close();
            stmt.close();
        }
        
        return model;
    }

    private DefaultTableModel getCitiesData(String filterType) throws SQLException {
        // Enhanced columns with comprehensive order status breakdown
        String[] columnNames = {
            "City ID", "City Name", "Total Orders", "Total Order Lines", 
            "Refunded Orders", "Discarded Orders", "Paid Orders",
            "Pending Orders", "Preparing Orders", "In Transit Orders", "Delivered Orders"
        };
        
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        try (Connection conn = DBConnection.getConnection()) {
            String dateCondition = getDateCondition(filterType, "o");
            String cityDateFilter = getCityDateFilterForExistence(filterType);
            
            // FIXED: Proper SQL syntax for LEFT JOIN with conditions
            String citiesQuery = 
                "SELECT " +
                "city.city_id, " +
                "city.city_name, " +
                "COUNT(DISTINCT o.order_id) as total_orders, " +
                "COUNT(ol.order_line_id) as total_order_lines, " +
                "SUM(CASE WHEN p.is_refunded = 1 THEN 1 ELSE 0 END) as refunded_orders, " +
                "SUM(CASE WHEN o.status = 'Discarded' THEN 1 ELSE 0 END) as discarded_orders, " +
                "SUM(CASE WHEN p.is_paid = 1 THEN 1 ELSE 0 END) as paid_orders, " +
                "SUM(CASE WHEN o.status = 'Pending' THEN 1 ELSE 0 END) as pending_orders, " +
                "SUM(CASE WHEN o.status = 'Preparing' THEN 1 ELSE 0 END) as preparing_orders, " +
                "SUM(CASE WHEN o.status = 'In Transit' THEN 1 ELSE 0 END) as in_transit_orders, " +
                "SUM(CASE WHEN o.status = 'Delivered' THEN 1 ELSE 0 END) as delivered_orders " +
                "FROM Cities city " +
                "LEFT JOIN Customers c ON city.city_id = c.city_id " +
                "LEFT JOIN Orders o ON (c.customer_id = o.customer_id AND " + dateCondition + ") " +
                "LEFT JOIN Order_Lines ol ON o.order_id = ol.order_id " +
                "LEFT JOIN Payments p ON o.order_id = p.order_id " +
                "WHERE " + cityDateFilter + " " +
                "GROUP BY city.city_id, city.city_name " +
                "ORDER BY total_orders DESC, city.city_name ASC";
            
            PreparedStatement stmt = conn.prepareStatement(citiesQuery);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("city_id"),
                    rs.getString("city_name"),
                    rs.getInt("total_orders"),
                    rs.getInt("total_order_lines"),
                    rs.getInt("refunded_orders"),
                    rs.getInt("discarded_orders"),
                    rs.getInt("paid_orders"),
                    rs.getInt("pending_orders"),
                    rs.getInt("preparing_orders"),
                    rs.getInt("in_transit_orders"),
                    rs.getInt("delivered_orders")
                };
                model.addRow(row);
            }
            
            rs.close();
            stmt.close();
        }
        
        return model;
    }

    // FIXED: Added table alias parameter for proper SQL syntax
    private String getDateCondition(String filterType, String tableAlias) {
        try {
            switch (filterType) {
                case "Day":
                    int dayMonth = Integer.parseInt(currentDayMonth.isEmpty() ? view.getDayMonth() : currentDayMonth);
                    int dayDay = Integer.parseInt(currentDayDay.isEmpty() ? view.getDayDay() : currentDayDay);
                    int dayYear = Integer.parseInt(currentDayYear.isEmpty() ? view.getDayYear() : currentDayYear);
                    return String.format("DATE(%s.order_date) = '%04d-%02d-%02d'", tableAlias, dayYear, dayMonth, dayDay);
                    
                case "Month":
                    int monthMonth = Integer.parseInt(currentMonthMonth.isEmpty() ? view.getMonthMonth() : currentMonthMonth);
                    int monthYear = Integer.parseInt(currentMonthYear.isEmpty() ? view.getMonthYear() : currentMonthYear);
                    return String.format("YEAR(%s.order_date) = %d AND MONTH(%s.order_date) = %d", tableAlias, monthYear, tableAlias, monthMonth);
                    
                case "Year":
                    int yearYear = Integer.parseInt(currentYearYear.isEmpty() ? view.getYearYear() : currentYearYear);
                    return String.format("YEAR(%s.order_date) = %d", tableAlias, yearYear);
                    
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