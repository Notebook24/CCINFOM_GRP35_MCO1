import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class AdminRevenueReportController {
    private AdminRevenueReportView view;
    private static AdminRevenueReportView currInstance;
    private Timer autoRefreshTimer;

    public AdminRevenueReportController() {
        if(currInstance != null) {
            currInstance.getFrame().dispose();
        }

        this.view = new AdminRevenueReportView(this);
        currInstance = view;

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

        refreshDataWithCurrentFilter();
    }

    private void applyFilter() {
        if (!view.validateFilterInput()) {
            return;
        }
        
        refreshDataWithCurrentFilter();
        view.showSuccessMessage("Filter applied successfully!");
    }

    private void refreshDataWithCurrentFilter() {
        try {
            String filterType = view.getFilterType();
            Map<String, Object> summaryData = getSummaryData(filterType);
            DefaultTableModel paymentsModel = getPaymentsData(filterType);
            DefaultTableModel citiesModel = getCitiesData(filterType);
            DefaultTableModel customersModel = getCustomersData(filterType);

            view.updateSummaryPanel(summaryData);
            view.updatePaymentsTable(paymentsModel);
            view.updateCitiesTable(citiesModel);
            view.updateCustomersTable(customersModel);
        } catch (SQLException e) {
            e.printStackTrace(); // Add this for debugging
            view.showErrorMessage("Error loading revenue report data: " + e.getMessage());
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
        new AdminHomePageController(homePageView, 1);
    }

    private Map<String, Object> getSummaryData(String filterType) throws SQLException {
        Map<String, Object> summaryData = new HashMap<>();

        try (Connection conn = DBConnection.getConnection()) {
            String dateCondition = getDateCondition(filterType);  
            String cityDateFilter = getCityDateFilterForExistence(filterType, "ci");
            String customerDateFilter = getCustomerDateFilterForExistence(filterType, "c");
         
            String statusQuery =
                "SELECT " +
                "COUNT(*) AS total_payments, " +
                "SUM(CASE WHEN p.is_paid = 1 THEN 1 ELSE 0 END) AS total_paid, " +
                "SUM(CASE WHEN p.is_paid = 0 THEN 1 ELSE 0 END) AS total_unpaid, " +
                "SUM(CASE WHEN p.is_refunded = 1 THEN 1 ELSE 0 END) AS total_refunded, " +
                "SUM(CASE WHEN p.is_paid = 1 THEN p.amount_paid ELSE 0 END) AS total_paid_amount, " +
                "SUM(p.total_price + p.delivery_fee) AS total_revenue, " +
                "SUM(CASE WHEN p.is_paid = 1 THEN (p.total_price + p.delivery_fee) ELSE 0 END) AS net_revenue, " +
                "AVG(p.total_price + p.delivery_fee) AS avg_revenue " +
                "FROM Payments p " +
                "JOIN Customers c ON p.customer_id = c.customer_id " +
                "JOIN Cities ci ON c.city_id = ci.city_id " +
                "WHERE " + dateCondition + " " +
                "AND " + customerDateFilter + " " +
                "AND " + cityDateFilter;

            PreparedStatement statusStmt = conn.prepareStatement(statusQuery);
            ResultSet statusRs = statusStmt.executeQuery();

            if (statusRs.next()) {
                summaryData.put("total_payments", statusRs.getInt("total_payments"));
                summaryData.put("total_paid", statusRs.getInt("total_paid"));
                summaryData.put("total_unpaid", statusRs.getInt("total_unpaid"));
                summaryData.put("total_refunded", statusRs.getInt("total_refunded"));
                summaryData.put("total_paid_amount", statusRs.getDouble("total_paid_amount"));
                summaryData.put("total_revenue", statusRs.getDouble("total_revenue"));
                summaryData.put("net_revenue", statusRs.getDouble("net_revenue"));
                summaryData.put("avg_revenue", statusRs.getDouble("avg_revenue"));
            }

            statusRs.close();
            statusStmt.close();

            String topCustomerQuery =
                "SELECT CONCAT(c.first_name, ' ', c.last_name) AS customer_name, " +
                "MAX(p.amount_paid) AS highest_payment " +
                "FROM Payments p " +
                "JOIN Customers c ON p.customer_id = c.customer_id " +
                "JOIN Cities ci ON c.city_id = ci.city_id " +
                "WHERE " + dateCondition + " AND p.is_paid = 1 " +
                "AND " + customerDateFilter + " " +
                "AND " + cityDateFilter + " " +
                "GROUP BY c.customer_id, c.first_name, c.last_name " +
                "ORDER BY highest_payment DESC " +
                "LIMIT 1";

            PreparedStatement customerStmt = conn.prepareStatement(topCustomerQuery);
            ResultSet customerRs = customerStmt.executeQuery();

            if (customerRs.next()) {
                String info = customerRs.getString("customer_name") +
                              " (₱" + String.format("%.2f", customerRs.getDouble("highest_payment")) + ")";
                summaryData.put("highest_payment_customer", info);
            } else {
                summaryData.put("highest_payment_customer", "None");
            }

            customerRs.close();
            customerStmt.close();

            String topCityQuery =
                "SELECT ci.city_name, SUM(p.total_price + p.delivery_fee) AS city_revenue " +
                "FROM Payments p " +
                "JOIN Customers cu ON p.customer_id = cu.customer_id " +
                "JOIN Cities ci ON cu.city_id = ci.city_id " +
                "WHERE " + dateCondition + " AND p.is_paid = 1 " +
                "AND " + getCustomerDateFilterForExistence(filterType, "cu") + " " +
                "AND " + cityDateFilter + " " +
                "GROUP BY ci.city_id, ci.city_name " +
                "ORDER BY city_revenue DESC " +
                "LIMIT 1";

            PreparedStatement cityStmt = conn.prepareStatement(topCityQuery);
            ResultSet cityRs = cityStmt.executeQuery();

            if (cityRs.next()) {
                String info = cityRs.getString("city_name") +
                              " (₱" + String.format("%.2f", cityRs.getDouble("city_revenue")) + ")";
                summaryData.put("highest_revenue_city", info);
            } else {
                summaryData.put("highest_revenue_city", "None");
            }

            cityRs.close();
            cityStmt.close();
        }

        return summaryData;
    }

    private DefaultTableModel getCitiesData(String filterType) throws SQLException {
        String[] columnNames = {"City ID", "City Name", "Total Revenue", "Refunded Payments", "Unpaid Payments"};
        
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try (Connection conn = DBConnection.getConnection()) {
            String dateCondition = getDateCondition(filterType);
            String cityDateFilter = getCityDateFilterForExistence(filterType, "ci");
            String customerDateFilter = getCustomerDateFilterForExistence(filterType, "cust");

            String citiesQuery =
                "SELECT ci.city_id, ci.city_name, " +
                "COALESCE(SUM(CASE WHEN p.is_paid = 1 THEN (p.total_price + p.delivery_fee) ELSE 0 END), 0) AS total_revenue, " +
                "SUM(CASE WHEN p.is_refunded = 1 THEN 1 ELSE 0 END) AS refunded_payments, " +
                "SUM(CASE WHEN p.is_paid = 0 THEN 1 ELSE 0 END) AS unpaid_payments " +
                "FROM Cities ci " +
                "LEFT JOIN Customers cust ON ci.city_id = cust.city_id AND " + customerDateFilter + " " +
                "LEFT JOIN Payments p ON cust.customer_id = p.customer_id AND " + dateCondition + " " +
                "WHERE " + cityDateFilter + " " +
                "GROUP BY ci.city_id, ci.city_name " +
                "ORDER BY total_revenue DESC, ci.city_name ASC";

            PreparedStatement stmt = conn.prepareStatement(citiesQuery);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("city_id"),
                    rs.getString("city_name"),
                    String.format("₱%.2f", rs.getDouble("total_revenue")),
                    rs.getInt("refunded_payments"),
                    rs.getInt("unpaid_payments")
                };
                model.addRow(row);
            }

            rs.close();
            stmt.close();
        }

        return model;
    }

    private DefaultTableModel getCustomersData(String filterType) throws SQLException {
        String[] columnNames = {"Customer ID", "Customer Name", "Customer City", "Total Revenue", "Refunded Payments", "Unpaid Payments"};

        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try (Connection conn = DBConnection.getConnection()) {
            String dateCondition = getDateCondition(filterType);
            String customerDateFilter = getCustomerDateFilterForExistence(filterType, "cu");
            String cityDateFilter = getCityDateFilterForExistence(filterType, "ci");

            String customersQuery =
                "SELECT cu.customer_id, " +
                "CONCAT(cu.first_name, ' ', cu.last_name) AS customer_name, " +
                "ci.city_name, " +
                "COALESCE(SUM(CASE WHEN p.is_paid = 1 THEN (p.total_price + p.delivery_fee) ELSE 0 END), 0) AS total_revenue, " +
                "SUM(CASE WHEN p.is_refunded = 1 THEN 1 ELSE 0 END) AS refunded_payments, " +
                "SUM(CASE WHEN p.is_paid = 0 THEN 1 ELSE 0 END) AS unpaid_payments " +
                "FROM Customers cu " +
                "JOIN Cities ci ON cu.city_id = ci.city_id AND " + cityDateFilter + " " +
                "LEFT JOIN Payments p ON cu.customer_id = p.customer_id AND " + dateCondition + " " +
                "WHERE " + customerDateFilter + " " +
                "GROUP BY cu.customer_id, cu.first_name, cu.last_name, ci.city_name " +
                "ORDER BY total_revenue DESC";

            PreparedStatement stmt = conn.prepareStatement(customersQuery);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("customer_id"),
                    rs.getString("customer_name"),
                    rs.getString("city_name"),
                    String.format("₱%.2f", rs.getDouble("total_revenue")),
                    rs.getInt("refunded_payments"),
                    rs.getInt("unpaid_payments")
                };
                model.addRow(row);
            }

            rs.close();
            stmt.close();
        }

        return model;
    }

    private DefaultTableModel getPaymentsData(String filterType) throws SQLException {
        String[] columnNames = {
            "Payment ID", "Paid Status", "Refund Status", "Total Order Price", 
            "Delivery Price", "Total Price", "Amount Paid", "Change", "Net Revenue"
        };
        
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        try (Connection conn = DBConnection.getConnection()) {
            String dateCondition = getDateCondition(filterType);
            String customerDateFilter = getCustomerDateFilterForExistence(filterType, "c");
            String cityDateFilter = getCityDateFilterForExistence(filterType, "ci");
            
            String paymentsQuery =
                "SELECT p.payment_id, " +
                "CASE WHEN p.is_paid = 1 THEN 'Paid' ELSE 'Unpaid' END AS paid_status, " +
                "CASE WHEN p.is_refunded = 1 THEN 'Refunded' ELSE 'Not Refunded' END AS refund_status, " +
                "p.total_price AS order_price, " +
                "p.delivery_fee AS delivery_price, " +
                "(p.total_price + p.delivery_fee) AS total_price, " +
                "p.amount_paid, " +
                "CASE WHEN p.is_paid = 1 THEN (p.amount_paid - (p.total_price + p.delivery_fee)) ELSE 0 END AS change_amount, " +
                "CASE WHEN p.is_paid = 1 AND p.is_refunded = 0 THEN (p.total_price + p.delivery_fee) ELSE 0 END AS net_revenue " +
                "FROM Payments p " +
                "JOIN Customers c ON p.customer_id = c.customer_id " +
                "JOIN Cities ci ON c.city_id = ci.city_id " +
                "WHERE " + dateCondition + " " +
                "AND " + customerDateFilter + " " +
                "AND " + cityDateFilter + " " +
                "ORDER BY p.payment_id DESC";

            PreparedStatement stmt = conn.prepareStatement(paymentsQuery);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("payment_id"),
                    rs.getString("paid_status"),
                    rs.getString("refund_status"),
                    String.format("₱%.2f", rs.getDouble("order_price")),
                    String.format("₱%.2f", rs.getDouble("delivery_price")),
                    String.format("₱%.2f", rs.getDouble("total_price")),
                    String.format("₱%.2f", rs.getDouble("amount_paid")),
                    String.format("₱%.2f", rs.getDouble("change_amount")),
                    String.format("₱%.2f", rs.getDouble("net_revenue"))
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
                    return String.format("DATE(p.date) = '%04d-%02d-%02d'", dayYear, dayMonth, dayDay);
                    
                case "Month":
                    int monthMonth = Integer.parseInt(view.getMonthMonth());
                    int monthYear = Integer.parseInt(view.getMonthYear());
                    return String.format("YEAR(p.date) = %d AND MONTH(p.date) = %d", monthYear, monthMonth);
                    
                case "Year":
                    int yearYear = Integer.parseInt(view.getYearYear());
                    return String.format("YEAR(p.date) = %d", yearYear);
                    
                default:
                    return "1=1";
            }
        } catch (NumberFormatException e) {
            return "1=1";
        }
    }

    private String getCityDateFilterForExistence(String filterType, String tableAlias) {
        try {
            switch (filterType) {
                case "Day":
                    int dayMonth = Integer.parseInt(view.getDayMonth());
                    int dayDay = Integer.parseInt(view.getDayDay());
                    int dayYear = Integer.parseInt(view.getDayYear());
                    return String.format("DATE(%s.created_date) <= '%04d-%02d-%02d'", tableAlias, dayYear, dayMonth, dayDay);
                    
                case "Month":
                    int monthMonth = Integer.parseInt(view.getMonthMonth());
                    int monthYear = Integer.parseInt(view.getMonthYear());
                    return String.format("%s.created_date <= LAST_DAY('%04d-%02d-01')", tableAlias, monthYear, monthMonth);
                    
                case "Year":
                    int yearYear = Integer.parseInt(view.getYearYear());
                    return String.format("%s.created_date <= '%04d-12-31'", tableAlias, yearYear);
                    
                default:
                    return "1=1";
            }
        } catch (NumberFormatException e) {
            return "1=1";
        }
    }

    private String getCustomerDateFilterForExistence(String filterType, String tableAlias) {
        try {
            switch (filterType) {
                case "Day":
                    int dayMonth = Integer.parseInt(view.getDayMonth());
                    int dayDay = Integer.parseInt(view.getDayDay());
                    int dayYear = Integer.parseInt(view.getDayYear());
                    return String.format("DATE(%s.created_date) <= '%04d-%02d-%02d'", tableAlias, dayYear, dayMonth, dayDay);
                    
                case "Month":
                    int monthMonth = Integer.parseInt(view.getMonthMonth());
                    int monthYear = Integer.parseInt(view.getMonthYear());
                    return String.format("%s.created_date <= LAST_DAY('%04d-%02d-01')", tableAlias, monthYear, monthMonth);
                    
                case "Year":
                    int yearYear = Integer.parseInt(view.getYearYear());
                    return String.format("%s.created_date <= '%04d-12-31'", tableAlias, yearYear);
                    
                default:
                    return "1=1";
        }
        } catch (NumberFormatException e) {
            return "1=1";
        }
    }
}