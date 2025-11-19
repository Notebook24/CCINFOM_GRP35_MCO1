import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class AdminOrderReportController {
    private AdminOrderReportView view;
    private static AdminOrderReportView currentInstance;
    private Timer autoRefreshTimer;

    public AdminOrderReportController() {
        if (currentInstance != null) {
            currentInstance.getFrame().dispose();
        }
        
        this.view = new AdminOrderReportView(this);
        currentInstance = view;
        
        initializeController();
        startAutoRefresh();
    }

    private void initializeController() {
        view.getBackButton().addActionListener(e -> {
            stopAutoRefresh();
            goBackToAdminHome();
        });

        refreshData("Today");
    }

    private void startAutoRefresh() {
        // Auto-refresh every 30 seconds to catch updates from other admins
        autoRefreshTimer = new Timer(true);
        autoRefreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    refreshData(view.getFilterType());
                });
            }
        }, 30000, 30000); // 30 seconds delay, repeat every 30 seconds
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

    public void refreshData(String filterType) {
        try {
            Map<String, Object> summaryData = getSummaryData(filterType);
            DefaultTableModel ordersModel = getOrdersData(filterType);
            DefaultTableModel customersModel = getCustomersData(filterType);
            DefaultTableModel citiesModel = getCitiesData(filterType);
            
            view.updateSummaryPanel(summaryData);
            view.updateOrdersTable(ordersModel);
            view.updateCustomersTable(customersModel);
            view.updateCitiesTable(citiesModel);
            
        } catch (SQLException e) {
            view.showErrorMessage("Error loading order report data: " + e.getMessage());
        }
    }

    // ... (rest of the database methods remain the same as previous version)
    private Map<String, Object> getSummaryData(String filterType) throws SQLException {
        Map<String, Object> summaryData = new HashMap<>();
        
        try (Connection conn = DBConnection.getConnection()) {
            String dateCondition = getDateCondition(filterType);
            
            // Get order counts by status
            String statusQuery = 
                "SELECT " +
                "COUNT(*) as total_orders, " +
                "SUM(CASE WHEN status = 'Pending' THEN 1 ELSE 0 END) as pending_orders, " +
                "SUM(CASE WHEN status = 'Preparing' THEN 1 ELSE 0 END) as preparing_orders, " +
                "SUM(CASE WHEN status = 'In Transit' THEN 1 ELSE 0 END) as in_transit_orders, " +
                "SUM(CASE WHEN status = 'Delivered' THEN 1 ELSE 0 END) as delivered_orders, " +
                "SUM(CASE WHEN status = 'Cancelled' THEN 1 ELSE 0 END) as cancelled_orders " +
                "FROM Orders " +
                "WHERE " + dateCondition.replace("o.", "");

            PreparedStatement statusStmt = conn.prepareStatement(statusQuery);
            ResultSet statusRs = statusStmt.executeQuery();
            
            if (statusRs.next()) {
                summaryData.put("total_orders", statusRs.getInt("total_orders"));
                summaryData.put("pending_orders", statusRs.getInt("pending_orders"));
                summaryData.put("preparing_orders", statusRs.getInt("preparing_orders"));
                summaryData.put("in_transit_orders", statusRs.getInt("in_transit_orders"));
                summaryData.put("delivered_orders", statusRs.getInt("delivered_orders"));
                summaryData.put("cancelled_orders", statusRs.getInt("cancelled_orders"));
            }
            statusRs.close();
            statusStmt.close();
            
            // Get top customer
            String topCustomerQuery = 
                "SELECT CONCAT(c.first_name, ' ', c.last_name) as customer_name, " +
                "COUNT(o.order_id) as order_count " +
                "FROM Customers c " +
                "JOIN Orders o ON c.customer_id = o.customer_id " +
                "WHERE " + dateCondition.replace("o.", "") +
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
                "SELECT c.city_name, COUNT(o.order_id) as order_count " +
                "FROM Cities c " +
                "JOIN Customers cust ON c.city_id = cust.city_id " +
                "JOIN Orders o ON cust.customer_id = o.customer_id " +
                "WHERE " + dateCondition.replace("o.", "") +
                "GROUP BY c.city_id, c.city_name " +
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
            "Order ID", "Status", "Order Lines", "Avg Lines/Order", 
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
            
            String ordersQuery = 
                "SELECT o.order_id, o.status, " +
                "COUNT(ol.order_line_id) as order_lines_count, " +
                "ROUND(AVG(line_counts.avg_lines), 2) as avg_lines_per_order, " +
                "c.customer_id, CONCAT(c.first_name, ' ', c.last_name) as customer_name, " +
                "city.city_name, DATE_FORMAT(o.order_date, '%Y-%m-%d %H:%i:%s') as formatted_order_date " +
                "FROM Orders o " +
                "JOIN Customers c ON o.customer_id = c.customer_id " +
                "JOIN Cities city ON c.city_id = city.city_id " +
                "LEFT JOIN Order_Lines ol ON o.order_id = ol.order_id " +
                "LEFT JOIN ( " +
                "    SELECT order_id, COUNT(order_line_id) as avg_lines " +
                "    FROM Order_Lines " +
                "    GROUP BY order_id " +
                ") line_counts ON o.order_id = line_counts.order_id " +
                "WHERE " + dateCondition.replace("o.", "") +
                "GROUP BY o.order_id, o.status, c.customer_id, c.first_name, c.last_name, city.city_name, o.order_date " +
                "ORDER BY o.order_date DESC";
            
            PreparedStatement stmt = conn.prepareStatement(ordersQuery);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("order_id"),
                    rs.getString("status"),
                    rs.getInt("order_lines_count"),
                    rs.getDouble("avg_lines_per_order"),
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
            
            String customersQuery = 
                "SELECT c.customer_id, CONCAT(c.first_name, ' ', c.last_name) as customer_name, " +
                "city.city_name, COUNT(o.order_id) as total_orders " +
                "FROM Customers c " +
                "JOIN Cities city ON c.city_id = city.city_id " +
                "LEFT JOIN Orders o ON c.customer_id = o.customer_id AND " + dateCondition.replace("o.", "") +
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
            
            // Show ALL cities, even those with 0 orders
            String citiesQuery = 
                "SELECT c.city_id, c.city_name, " +
                "COALESCE(COUNT(o.order_id), 0) as total_orders " +
                "FROM Cities c " +
                "LEFT JOIN Customers cust ON c.city_id = cust.city_id " +
                "LEFT JOIN Orders o ON cust.customer_id = o.customer_id AND " + dateCondition.replace("o.", "") +
                "GROUP BY c.city_id, c.city_name " +
                "ORDER BY total_orders DESC, c.city_name ASC";
            
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
        switch (filterType) {
            case "Today":
                return "DATE(o.order_date) = CURDATE()";
            case "This Month":
                return "YEAR(o.order_date) = YEAR(CURDATE()) AND MONTH(o.order_date) = MONTH(CURDATE())";
            case "This Year":
                return "YEAR(o.order_date) = YEAR(CURDATE())";
            default:
                return "1=1";
        }
    }
}