import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class AdminMenuReportController {
    private AdminMenuReportView view;
    private static AdminMenuReportView currentInstance;
    private Timer autoRefreshTimer;

    public AdminMenuReportController() {
        if (currentInstance != null) {
            currentInstance.getFrame().dispose();
        }
        
        this.view = new AdminMenuReportView(this);
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
            DefaultTableModel menuModel = getMenuReportData(filterType);
            DefaultTableModel cityModel = getCityBreakdownData(filterType);
            
            view.updateSummaryPanel(summaryData);
            view.updateMenuTable(menuModel);
            view.updateCityTable(cityModel);
            
        } catch (SQLException e) {
            view.showErrorMessage("Error loading report data: " + e.getMessage());
        }
    }

    private Map<String, Object> getSummaryData(String filterType) throws SQLException {
        Map<String, Object> summaryData = new HashMap<>();
        
        try (Connection conn = DBConnection.getConnection()) {
            String dateCondition = getDateCondition(filterType);
            
            // Get basic counts
            String summaryQuery = 
                "SELECT " +
                "COUNT(DISTINCT m.menu_id) as total_menus, " +
                "COUNT(DISTINCT m.menu_category_id) as total_menu_groups, " +
                "SUM(CASE WHEN m.is_available = 1 THEN 1 ELSE 0 END) as available_menus, " +
                "COUNT(DISTINCT CASE WHEN m.is_available = 1 THEN m.menu_category_id END) as available_menu_groups, " +
                "COUNT(DISTINCT ol.menu_id) as sold_menus, " +
                "COALESCE(SUM(ol.menu_price * ol.menu_quantity), 0) as total_revenue " +
                "FROM Menus m " +
                "LEFT JOIN Order_Lines ol ON m.menu_id = ol.menu_id " +
                "LEFT JOIN Orders o ON ol.order_id = o.order_id AND " + dateCondition;

            PreparedStatement stmt = conn.prepareStatement(summaryQuery);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                summaryData.put("total_menus", rs.getInt("total_menus"));
                summaryData.put("total_menu_groups", rs.getInt("total_menu_groups"));
                summaryData.put("available_menus", rs.getInt("available_menus"));
                summaryData.put("available_menu_groups", rs.getInt("available_menu_groups"));
                summaryData.put("sold_menus", rs.getInt("sold_menus"));
                summaryData.put("total_revenue", rs.getDouble("total_revenue"));
            }
            rs.close();
            stmt.close();
            
            // Get most sold menu
            String mostSoldMenuQuery = 
                "SELECT m.menu_name " +
                "FROM Menus m " +
                "JOIN Order_Lines ol ON m.menu_id = ol.menu_id " +
                "JOIN Orders o ON ol.order_id = o.order_id " +
                "WHERE " + dateCondition +
                "GROUP BY m.menu_id, m.menu_name " +
                "ORDER BY SUM(ol.menu_quantity) DESC " +
                "LIMIT 1";
            
            PreparedStatement menuStmt = conn.prepareStatement(mostSoldMenuQuery);
            ResultSet menuRs = menuStmt.executeQuery();
            summaryData.put("most_sold_menu", menuRs.next() ? menuRs.getString("menu_name") : "None");
            menuRs.close();
            menuStmt.close();
            
            // Get most sold menu group
            String mostSoldGroupQuery = 
                "SELECT mc.menu_category_name " +
                "FROM Menu_Category mc " +
                "JOIN Menus m ON mc.menu_category_id = m.menu_category_id " +
                "JOIN Order_Lines ol ON m.menu_id = ol.menu_id " +
                "JOIN Orders o ON ol.order_id = o.order_id " +
                "WHERE " + dateCondition +
                "GROUP BY mc.menu_category_id, mc.menu_category_name " +
                "ORDER BY SUM(ol.menu_quantity) DESC " +
                "LIMIT 1";
            
            PreparedStatement groupStmt = conn.prepareStatement(mostSoldGroupQuery);
            ResultSet groupRs = groupStmt.executeQuery();
            summaryData.put("most_sold_group", groupRs.next() ? groupRs.getString("menu_category_name") : "None");
            groupRs.close();
            groupStmt.close();
        }
        
        return summaryData;
    }

    private DefaultTableModel getMenuReportData(String filterType) throws SQLException {
        String[] columnNames = {
            "Menu ID", "Menu Name", "Menu Group", "Unit Price", 
            "Total Sold", "Total Orders", "Revenue", "Avg Qty/Order", "Availability"
        };
        
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        try (Connection conn = DBConnection.getConnection()) {
            String dateCondition = getDateCondition(filterType);
            
            String menuQuery = 
                "SELECT m.menu_id, m.menu_name, mc.menu_category_name, m.unit_price, " +
                "COALESCE(SUM(ol.menu_quantity), 0) as total_sold, " +
                "COUNT(DISTINCT ol.order_id) as total_orders, " +
                "COALESCE(SUM(ol.menu_price * ol.menu_quantity), 0) as revenue, " +
                "CASE WHEN COUNT(DISTINCT ol.order_id) > 0 THEN " +
                "ROUND(COALESCE(SUM(ol.menu_quantity), 0) / COUNT(DISTINCT ol.order_id), 2) ELSE 0 END as avg_qty_per_order, " +
                "CASE WHEN m.is_available = 1 THEN 'Available' ELSE 'Unavailable' END as availability " +
                "FROM Menus m " +
                "LEFT JOIN Menu_Category mc ON m.menu_category_id = mc.menu_category_id " +
                "LEFT JOIN Order_Lines ol ON m.menu_id = ol.menu_id " +
                "LEFT JOIN Orders o ON ol.order_id = o.order_id AND " + dateCondition +
                "GROUP BY m.menu_id, m.menu_name, mc.menu_category_name, m.unit_price, m.is_available " +
                "ORDER BY total_sold DESC";
            
            PreparedStatement stmt = conn.prepareStatement(menuQuery);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("menu_id"),
                    rs.getString("menu_name"),
                    rs.getString("menu_category_name"),
                    String.format("₱%.2f", rs.getDouble("unit_price")),
                    rs.getInt("total_sold"),
                    rs.getInt("total_orders"),
                    String.format("₱%.2f", rs.getDouble("revenue")),
                    String.format("%.2f", rs.getDouble("avg_qty_per_order")),
                    rs.getString("availability")
                };
                model.addRow(row);
            }
            
            rs.close();
            stmt.close();
        }
        
        return model;
    }

    private DefaultTableModel getCityBreakdownData(String filterType) throws SQLException {
        String[] columnNames = {"City", "Total Sold", "Revenue"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        try (Connection conn = DBConnection.getConnection()) {
            String dateCondition = getDateCondition(filterType);
            
            // Show ALL cities, even those with 0 sales
            String cityQuery = 
                "SELECT c.city_name, " +
                "COALESCE(SUM(ol.menu_quantity), 0) as total_sold, " +
                "COALESCE(SUM(ol.menu_price * ol.menu_quantity), 0) as revenue " +
                "FROM Cities c " +
                "LEFT JOIN Customers cust ON c.city_id = cust.city_id " +
                "LEFT JOIN Orders o ON cust.customer_id = o.customer_id AND " + dateCondition +
                "LEFT JOIN Order_Lines ol ON o.order_id = ol.order_id " +
                "GROUP BY c.city_id, c.city_name " +
                "ORDER BY total_sold DESC, c.city_name ASC";
            
            PreparedStatement stmt = conn.prepareStatement(cityQuery);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getString("city_name"),
                    rs.getInt("total_sold"),
                    String.format("₱%.2f", rs.getDouble("revenue"))
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