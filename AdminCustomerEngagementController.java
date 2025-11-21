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

public class AdminCustomerEngagementController {
    private AdminCustomerEngagementView view;
    private static AdminCustomerEngagementView currentInstance;
    private Timer autoRefreshTimer;
    private int adminId;

    public AdminCustomerEngagementController(int adminId) {
        this.adminId = adminId;
        if (currentInstance != null) {
            currentInstance.getFrame().dispose();
        }
        
        this.view = new AdminCustomerEngagementView(this);
        currentInstance = view;
        
        initializeController();
        startAutoRefresh();
    }

    private void initializeController() {
        view.getBackButton().addActionListener(e -> {
            stopAutoRefresh();
            goBackToAdminHome();
        });

        view.getApplyFilterButton().addActionListener(e -> {
            applyFilter();
        });

        // Load initial data with current date
        refreshDataWithCurrentFilter();
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

    private void refreshDataWithCurrentFilter() {
        try {
            String filterType = view.getFilterType();
            Map<String, Object> summaryData = getSummaryData(filterType);
            DefaultTableModel customerModel = getCustomerData(filterType);
            DefaultTableModel cityModel = getCityData(filterType);
            
            view.updateSummaryPanel(summaryData);
            view.updateCustomerTable(customerModel);
            view.updateCityTable(cityModel);
            
        } catch (SQLException e) {
            e.printStackTrace();
            view.showErrorMessage("Error loading report data: " + e.getMessage());
        }
    }

    private void startAutoRefresh() {
        autoRefreshTimer = new Timer(true);
        autoRefreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    refreshDataWithCurrentFilter();
                });
            }
        }, 30000, 30000);
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
        new AdminHomePageController(homePageView, adminId);
    }

    private Map<String, Object> getSummaryData(String filterType) throws SQLException {
        Map<String, Object> summaryData = new HashMap<>();
        
        try (Connection conn = DBConnection.getConnection()) {
            String dateCondition = getDateCondition(filterType);
            String customerDateFilter = getCustomerDateFilterForExistence(filterType);
            
            // Build WHERE clauses with proper table references
            String orderWhereClause = dateCondition.isEmpty() ? "" : " WHERE " + dateCondition;
            String customerActiveWhereClause = customerDateFilter.isEmpty() ? 
                " WHERE c.is_active = 1" : " WHERE c.is_active = 1 AND " + customerDateFilter;
            String customerDeletedWhereClause = customerDateFilter.isEmpty() ? 
                " WHERE c.is_active = 0" : " WHERE c.is_active = 0 AND " + customerDateFilter;
            
            // Fixed Summary Statistics Query - using proper table references
            String summaryQuery = 
                "SELECT " +
                "(SELECT COUNT(*) FROM Customers c" + customerActiveWhereClause + ") as active_customers, " +
                "(SELECT COUNT(*) FROM Customers c" + customerDeletedWhereClause + ") as deleted_accounts, " +
                "(SELECT COUNT(*) FROM Orders o" + orderWhereClause + ") as total_orders, " +
                "(SELECT COUNT(*) FROM Payments p JOIN Orders o ON p.order_id = o.order_id" + 
                (orderWhereClause.isEmpty() ? "" : orderWhereClause.replace("WHERE", "AND")) + ") as total_payments, " +
                "(SELECT COALESCE(SUM(p.amount_paid), 0) FROM Payments p JOIN Orders o ON p.order_id = o.order_id" + 
                (orderWhereClause.isEmpty() ? "" : orderWhereClause.replace("WHERE", "AND")) + ") as total_amount_spent, " +
                "(SELECT COALESCE(SUM(p.amount_paid), 0) FROM Payments p JOIN Orders o ON p.order_id = o.order_id " + 
                (orderWhereClause.isEmpty() ? " WHERE p.is_refunded = 1" : " WHERE p.is_refunded = 1 AND " + dateCondition) + ") as total_amount_refunded, " +
                "(SELECT MAX(o.order_date) FROM Orders o" + orderWhereClause + ") as last_purchase_date, " +
                "(SELECT COUNT(*) FROM Payments p JOIN Orders o ON p.order_id = o.order_id " + 
                (orderWhereClause.isEmpty() ? " WHERE p.is_paid = 1" : " WHERE p.is_paid = 1 AND " + dateCondition) + ") as paid_count, " +
                "(SELECT COUNT(*) FROM Payments p JOIN Orders o ON p.order_id = o.order_id" + 
                (orderWhereClause.isEmpty() ? "" : orderWhereClause.replace("WHERE", "AND")) + ") as payment_count";
            
            PreparedStatement stmt = conn.prepareStatement(summaryQuery);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                summaryData.put("active_customers", rs.getInt("active_customers"));
                summaryData.put("deleted_accounts", rs.getInt("deleted_accounts"));
                summaryData.put("total_orders", rs.getInt("total_orders"));
                summaryData.put("total_payments", rs.getInt("total_payments"));
                summaryData.put("total_amount_spent", rs.getDouble("total_amount_spent"));
                summaryData.put("total_amount_refunded", rs.getDouble("total_amount_refunded"));
                summaryData.put("last_purchase_date", rs.getTimestamp("last_purchase_date"));
                
                int paidCount = rs.getInt("paid_count");
                int paymentCount = rs.getInt("payment_count");
                double completionRate = paymentCount > 0 ? (paidCount * 100.0 / paymentCount) : 0;
                summaryData.put("payment_completion_rate", completionRate);
            }
            rs.close();
            stmt.close();
        }
        
        return summaryData;
    }

    private DefaultTableModel getCustomerData(String filterType) throws SQLException {
        String[] columnNames = {
            "Customer ID", "Full Name", "Email", "City", 
            "Total Orders", "Products Purchased", "Total Payments", "Total Amount Spent", "Last Purchase"
        };
        
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        try (Connection conn = DBConnection.getConnection()) {
            String customerDateFilter = getCustomerDateFilterForExistence(filterType);
            String orderDateCondition = getDateCondition(filterType);
            
            String customerQuery = 
                "SELECT c.customer_id, " +
                "CONCAT(c.first_name, ' ', c.last_name) as full_name, " +
                "c.email, " +
                "ct.city_name, " +
                "COUNT(DISTINCT o.order_id) as total_orders, " +
                "COALESCE(SUM(ol.menu_quantity), 0) as products_purchased, " +
                "COUNT(DISTINCT p.payment_id) as total_payments, " +
                "COALESCE(SUM(p.amount_paid), 0) as total_amount_spent, " +
                "MAX(o.order_date) as last_purchase " +
                "FROM Customers c " +
                "LEFT JOIN Cities ct ON c.city_id = ct.city_id " +
                "LEFT JOIN Orders o ON c.customer_id = o.customer_id " +
                "LEFT JOIN Order_Lines ol ON o.order_id = ol.order_id " +
                "LEFT JOIN Payments p ON o.order_id = p.order_id " +
                "WHERE c.is_active = 1 " +
                (customerDateFilter.isEmpty() ? "" : " AND " + customerDateFilter) + " " +
                (orderDateCondition.isEmpty() ? "" : " AND (" + orderDateCondition + " OR o.order_id IS NULL)") + " " +
                "GROUP BY c.customer_id, c.first_name, c.last_name, c.email, ct.city_name " +
                "ORDER BY c.customer_id ASC";
            
            PreparedStatement stmt = conn.prepareStatement(customerQuery);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("customer_id"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("city_name"),
                    rs.getInt("total_orders"),
                    rs.getInt("products_purchased"),
                    rs.getInt("total_payments"),
                    String.format("₱%.2f", rs.getDouble("total_amount_spent")),
                    rs.getTimestamp("last_purchase") != null ? 
                        new java.text.SimpleDateFormat("yyyy-MM-dd").format(rs.getTimestamp("last_purchase")) : "Never"
                };
                model.addRow(row);
            }
            
            rs.close();
            stmt.close();
        }
        
        return model;
    }

    private DefaultTableModel getCityData(String filterType) throws SQLException {
        String[] columnNames = {
            "City ID", "City Name", "Active Customers", "Total Orders", 
            "Total Revenue", "Payment Completion Rate", "Last Purchase", "Delivery Fee"
        };
        
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        try (Connection conn = DBConnection.getConnection()) {
            String customerDateFilter = getCustomerDateFilterForExistence(filterType);
            String orderDateCondition = getDateCondition(filterType);
            String cityDateFilter = getCityDateFilter(filterType);
            
            // Fixed city query with proper city date filtering
            String cityQuery = 
                "SELECT ct.city_id, ct.city_name, cdg.city_delivery_fee, " +
                "COUNT(DISTINCT c.customer_id) as active_customers, " +
                "COUNT(DISTINCT o.order_id) as total_orders, " +
                "COALESCE(SUM(p.amount_paid), 0) as total_revenue, " +
                "MAX(o.order_date) as last_purchase, " +
                "COUNT(DISTINCT CASE WHEN p.is_paid = 1 THEN p.payment_id END) as paid_count, " +
                "COUNT(DISTINCT p.payment_id) as payment_count " +
                "FROM Cities ct " +
                "LEFT JOIN City_Delivery_Groups cdg ON ct.city_delivery_group_id = cdg.city_delivery_group_id " +
                "LEFT JOIN Customers c ON ct.city_id = c.city_id AND c.is_active = 1 " +
                (customerDateFilter.isEmpty() ? "" : " AND " + customerDateFilter) + " " +
                "LEFT JOIN Orders o ON c.customer_id = o.customer_id " +
                (orderDateCondition.isEmpty() ? "" : " AND " + orderDateCondition) + " " +
                "LEFT JOIN Payments p ON o.order_id = p.order_id " +
                "WHERE ct.is_available = 1 " +
                (cityDateFilter.isEmpty() ? "" : " AND " + cityDateFilter) + " " +
                "GROUP BY ct.city_id, ct.city_name, cdg.city_delivery_fee " +
                "ORDER BY ct.city_id ASC";
            
            PreparedStatement stmt = conn.prepareStatement(cityQuery);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int paidCount = rs.getInt("paid_count");
                int paymentCount = rs.getInt("payment_count");
                double completionRate = paymentCount > 0 ? (paidCount * 100.0 / paymentCount) : 0;
                
                Object[] row = {
                    rs.getInt("city_id"),
                    rs.getString("city_name"),
                    rs.getInt("active_customers"),
                    rs.getInt("total_orders"),
                    String.format("₱%.2f", rs.getDouble("total_revenue")),
                    String.format("%.2f%%", completionRate),
                    rs.getTimestamp("last_purchase") != null ? 
                        new java.text.SimpleDateFormat("yyyy-MM-dd").format(rs.getTimestamp("last_purchase")) : "Never",
                    String.format("₱%.2f", rs.getDouble("city_delivery_fee"))
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
                    int dayMonth = Integer.parseInt(view.getDayMonth());
                    int dayDay = Integer.parseInt(view.getDayDay());
                    int dayYear = Integer.parseInt(view.getDayYear());
                    return String.format("DATE(o.order_date) = '%04d-%02d-%02d'", dayYear, dayMonth, dayDay);
                    
                case "Month":
                    int monthMonth = Integer.parseInt(view.getMonthMonth());
                    int monthYear = Integer.parseInt(view.getMonthYear());
                    return String.format("YEAR(o.order_date) = %d AND MONTH(o.order_date) = %d", monthYear, monthMonth);
                    
                case "Year":
                    int yearYear = Integer.parseInt(view.getYearYear());
                    return String.format("YEAR(o.order_date) = %d", yearYear);
                    
                default:
                    return "";
            }
        } catch (NumberFormatException e) {
            return "";
        }
    }

    private String getCustomerDateFilterForExistence(String filterType) {
        try {
            switch (filterType) {
                case "Day":
                    int dayMonth = Integer.parseInt(view.getDayMonth());
                    int dayDay = Integer.parseInt(view.getDayDay());
                    int dayYear = Integer.parseInt(view.getDayYear());
                    return String.format("DATE(c.created_date) <= '%04d-%02d-%02d'", dayYear, dayMonth, dayDay);
                    
                case "Month":
                    int monthMonth = Integer.parseInt(view.getMonthMonth());
                    int monthYear = Integer.parseInt(view.getMonthYear());
                    return String.format("c.created_date <= LAST_DAY('%04d-%02d-01')", monthYear, monthMonth);
                    
                case "Year":
                    int yearYear = Integer.parseInt(view.getYearYear());
                    return String.format("c.created_date <= '%04d-12-31'", yearYear);
                    
                default:
                    return "";
            }
        } catch (NumberFormatException e) {
            return "";
        }
    }

    private String getCityDateFilter(String filterType) {
        try {
            switch (filterType) {
                case "Day":
                    int dayMonth = Integer.parseInt(view.getDayMonth());
                    int dayDay = Integer.parseInt(view.getDayDay());
                    int dayYear = Integer.parseInt(view.getDayYear());
                    return String.format("DATE(ct.created_date) <= '%04d-%02d-%02d'", dayYear, dayMonth, dayDay);
                    
                case "Month":
                    int monthMonth = Integer.parseInt(view.getMonthMonth());
                    int monthYear = Integer.parseInt(view.getMonthYear());
                    return String.format("ct.created_date <= LAST_DAY('%04d-%02d-01')", monthYear, monthMonth);
                    
                case "Year":
                    int yearYear = Integer.parseInt(view.getYearYear());
                    return String.format("ct.created_date <= '%04d-12-31'", yearYear);
                    
                default:
                    return "";
            }
        } catch (NumberFormatException e) {
            return "";
        }
    }
}