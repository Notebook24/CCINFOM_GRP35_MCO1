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

public class AdminMenuReportController {
    private AdminMenuReportView view;
    private static AdminMenuReportView currentInstance;
    private Timer autoRefreshTimer;
    private int adminId;

    public AdminMenuReportController(int adminId) {
        this.adminId = adminId;
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
            DefaultTableModel categoryModel = getCategoryBreakdownData(filterType);
            DefaultTableModel menuModel = getMenuReportData(filterType);
            
            view.updateSummaryPanel(summaryData);
            view.updateCategoryTable(categoryModel);
            view.updateMenuTable(menuModel);
            
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
            String menuDateFilter = getMenuDateFilterForExistence(filterType);
            String categoryDateFilter = getCategoryDateFilterForExistence(filterType);
            
            // FIXED: Simplified query without complex JOIN conditions
            String summaryQuery = 
                "SELECT " +
                "(SELECT COUNT(*) FROM Menus m WHERE " + menuDateFilter + ") as total_menus, " +
                "(SELECT COUNT(DISTINCT menu_category_id) FROM Menus m WHERE " + menuDateFilter + ") as total_menu_groups, " +
                "(SELECT COUNT(*) FROM Menus m WHERE " + menuDateFilter + " AND m.is_available = 1) as available_menus, " +
                "(SELECT COUNT(DISTINCT menu_category_id) FROM Menus m WHERE " + menuDateFilter + " AND m.is_available = 1) as available_menu_groups, " +
                "COUNT(DISTINCT CASE WHEN o.order_id IS NOT NULL THEN ol.menu_id END) as sold_menus, " +
                "COALESCE(SUM(CASE WHEN o.order_id IS NOT NULL THEN ol.menu_price * ol.menu_quantity ELSE 0 END), 0) as total_revenue " +
                "FROM Menus m " +
                "LEFT JOIN Order_Lines ol ON m.menu_id = ol.menu_id " +
                "LEFT JOIN Orders o ON ol.order_id = o.order_id " +
                (dateCondition.isEmpty() ? "" : " AND " + dateCondition) +
                " WHERE " + menuDateFilter;
            
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
            
            // FIXED: Only count orders within the date range for menus that existed then
            String mostSoldMenuQuery = 
                "SELECT m.menu_name, SUM(ol.menu_quantity) as total_quantity " +
                "FROM Menus m " +
                "JOIN Order_Lines ol ON m.menu_id = ol.menu_id " +
                "JOIN Orders o ON ol.order_id = o.order_id " +
                " WHERE " + menuDateFilter + 
                (dateCondition.isEmpty() ? "" : " AND " + dateCondition) +
                " GROUP BY m.menu_id, m.menu_name " +
                " ORDER BY total_quantity DESC " +
                " LIMIT 1";
            
            PreparedStatement menuStmt = conn.prepareStatement(mostSoldMenuQuery);
            ResultSet menuRs = menuStmt.executeQuery();
            if (menuRs.next() && menuRs.getInt("total_quantity") > 0) {
                summaryData.put("most_sold_menu", menuRs.getString("menu_name"));
            } else {
                summaryData.put("most_sold_menu", "None");
            }
            menuRs.close();
            menuStmt.close();
            
            // FIXED: Only count orders within the date range for categories that existed then
            String mostSoldGroupQuery = 
                "SELECT mc.menu_category_name, SUM(ol.menu_quantity) as total_quantity " +
                "FROM Menu_Category mc " +
                "JOIN Menus m ON mc.menu_category_id = m.menu_category_id " +
                "JOIN Order_Lines ol ON m.menu_id = ol.menu_id " +
                "JOIN Orders o ON ol.order_id = o.order_id " +
                " WHERE " + categoryDateFilter + " AND " + menuDateFilter +
                (dateCondition.isEmpty() ? "" : " AND " + dateCondition) +
                " GROUP BY mc.menu_category_id, mc.menu_category_name " +
                " ORDER BY total_quantity DESC " +
                " LIMIT 1";
            
            PreparedStatement groupStmt = conn.prepareStatement(mostSoldGroupQuery);
            ResultSet groupRs = groupStmt.executeQuery();
            if (groupRs.next() && groupRs.getInt("total_quantity") > 0) {
                summaryData.put("most_sold_group", groupRs.getString("menu_category_name"));
            } else {
                summaryData.put("most_sold_group", "None");
            }
            groupRs.close();
            groupStmt.close();
        }
        
        return summaryData;
    }

    // FIXED: Category breakdown now properly filters by creation date and availability
    private DefaultTableModel getCategoryBreakdownData(String filterType) throws SQLException {
        String[] columnNames = {
            "Category ID", "Category Name", "Availability", "Total Menus", 
            "Available Menus", "Unavailable Menus", "Total Sold", "Total Revenue", "Avg Revenue"
        };
        
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        try (Connection conn = DBConnection.getConnection()) {
            String dateCondition = getDateCondition(filterType);
            String categoryDateFilter = getCategoryDateFilterForExistence(filterType);
            String menuDateFilter = getMenuDateFilterForExistence(filterType);
            
            // FIXED: Simplified category query
            String categoryQuery = 
                "SELECT " +
                "mc.menu_category_id, " +
                "mc.menu_category_name, " +
                "CASE WHEN mc.is_available = 1 THEN 'Available' ELSE 'Unavailable' END as category_availability, " +
                "(SELECT COUNT(*) FROM Menus m WHERE m.menu_category_id = mc.menu_category_id AND " + menuDateFilter + ") as total_menus, " +
                "(SELECT COUNT(*) FROM Menus m WHERE m.menu_category_id = mc.menu_category_id AND " + menuDateFilter + " AND m.is_available = 1) as available_menus, " +
                "(SELECT COUNT(*) FROM Menus m WHERE m.menu_category_id = mc.menu_category_id AND " + menuDateFilter + " AND m.is_available = 0) as unavailable_menus, " +
                "COALESCE(SUM(CASE WHEN o.order_id IS NOT NULL THEN ol.menu_quantity ELSE 0 END), 0) as total_sold, " +
                "COALESCE(SUM(CASE WHEN o.order_id IS NOT NULL THEN ol.menu_price * ol.menu_quantity ELSE 0 END), 0) as total_revenue, " +
                "CASE WHEN (SELECT COUNT(*) FROM Menus m WHERE m.menu_category_id = mc.menu_category_id AND " + menuDateFilter + ") > 0 THEN " +
                "COALESCE(SUM(CASE WHEN o.order_id IS NOT NULL THEN ol.menu_price * ol.menu_quantity ELSE 0 END), 0) / " +
                "(SELECT COUNT(*) FROM Menus m WHERE m.menu_category_id = mc.menu_category_id AND " + menuDateFilter + ") ELSE 0 END as avg_revenue " +
                "FROM Menu_Category mc " +
                "LEFT JOIN Menus m ON mc.menu_category_id = m.menu_category_id AND " + menuDateFilter +
                "LEFT JOIN Order_Lines ol ON m.menu_id = ol.menu_id " +
                "LEFT JOIN Orders o ON ol.order_id = o.order_id " +
                (dateCondition.isEmpty() ? "" : " AND " + dateCondition) +
                " WHERE " + categoryDateFilter +
                " GROUP BY mc.menu_category_id, mc.menu_category_name, mc.is_available " +
                " ORDER BY total_revenue DESC";
            
            PreparedStatement stmt = conn.prepareStatement(categoryQuery);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("menu_category_id"),
                    rs.getString("menu_category_name"),
                    rs.getString("category_availability"),
                    rs.getInt("total_menus"),
                    rs.getInt("available_menus"),
                    rs.getInt("unavailable_menus"),
                    rs.getInt("total_sold"),
                    String.format("₱%.2f", rs.getDouble("total_revenue")),
                    String.format("₱%.2f", rs.getDouble("avg_revenue"))
                };
                model.addRow(row);
            }
            
            rs.close();
            stmt.close();
        }
        
        return model;
    }

    // FIXED: Menu report now properly filters by creation date and availability
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
            String menuDateFilter = getMenuDateFilterForExistence(filterType);
            String categoryDateFilter = getCategoryDateFilterForExistence(filterType);
            
            // FIXED: Simplified menu query
            String menuQuery = 
                "SELECT m.menu_id, m.menu_name, mc.menu_category_name, m.unit_price, " +
                "COALESCE(SUM(CASE WHEN o.order_id IS NOT NULL THEN ol.menu_quantity ELSE 0 END), 0) as total_sold, " +
                "COUNT(DISTINCT CASE WHEN o.order_id IS NOT NULL THEN ol.order_id END) as total_orders, " +
                "COALESCE(SUM(CASE WHEN o.order_id IS NOT NULL THEN ol.menu_price * ol.menu_quantity ELSE 0 END), 0) as revenue, " +
                "CASE WHEN COUNT(DISTINCT CASE WHEN o.order_id IS NOT NULL THEN ol.order_id END) > 0 THEN " +
                "ROUND(COALESCE(SUM(CASE WHEN o.order_id IS NOT NULL THEN ol.menu_quantity ELSE 0 END), 0) / " +
                "COUNT(DISTINCT CASE WHEN o.order_id IS NOT NULL THEN ol.order_id END), 2) ELSE 0 END as avg_qty_per_order, " +
                "CASE WHEN m.is_available = 1 THEN 'Available' ELSE 'Unavailable' END as availability " +
                "FROM Menus m " +
                "INNER JOIN Menu_Category mc ON m.menu_category_id = mc.menu_category_id AND " + categoryDateFilter +
                "LEFT JOIN Order_Lines ol ON m.menu_id = ol.menu_id " +
                "LEFT JOIN Orders o ON ol.order_id = o.order_id " +
                (dateCondition.isEmpty() ? "" : " AND " + dateCondition) +
                " WHERE " + menuDateFilter +
                " GROUP BY m.menu_id, m.menu_name, mc.menu_category_name, m.unit_price, m.is_available " +
                " ORDER BY total_sold DESC";
            
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

    // NEW: Separate methods for menu and category date filters
    private String getMenuDateFilterForExistence(String filterType) {
        try {
            switch (filterType) {
                case "Day":
                    int dayMonth = Integer.parseInt(view.getDayMonth());
                    int dayDay = Integer.parseInt(view.getDayDay());
                    int dayYear = Integer.parseInt(view.getDayYear());
                    return String.format("DATE(m.created_date) <= '%04d-%02d-%02d'", dayYear, dayMonth, dayDay);
                    
                case "Month":
                    int monthMonth = Integer.parseInt(view.getMonthMonth());
                    int monthYear = Integer.parseInt(view.getMonthYear());
                    return String.format("m.created_date <= LAST_DAY('%04d-%02d-01')", monthYear, monthMonth);
                    
                case "Year":
                    int yearYear = Integer.parseInt(view.getYearYear());
                    return String.format("m.created_date <= '%04d-12-31'", yearYear);
                    
                default:
                    return "1=1";
            }
        } catch (NumberFormatException e) {
            return "1=1";
        }
    }

    private String getCategoryDateFilterForExistence(String filterType) {
        try {
            switch (filterType) {
                case "Day":
                    int dayMonth = Integer.parseInt(view.getDayMonth());
                    int dayDay = Integer.parseInt(view.getDayDay());
                    int dayYear = Integer.parseInt(view.getDayYear());
                    return String.format("DATE(mc.created_date) <= '%04d-%02d-%02d'", dayYear, dayMonth, dayDay);
                    
                case "Month":
                    int monthMonth = Integer.parseInt(view.getMonthMonth());
                    int monthYear = Integer.parseInt(view.getMonthYear());
                    return String.format("mc.created_date <= LAST_DAY('%04d-%02d-01')", monthYear, monthMonth);
                    
                case "Year":
                    int yearYear = Integer.parseInt(view.getYearYear());
                    return String.format("mc.created_date <= '%04d-12-31'", yearYear);
                    
                default:
                    return "1=1";
            }
        } catch (NumberFormatException e) {
            return "1=1";
        }
    }
}