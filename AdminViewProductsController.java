import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class AdminViewProductsController {
    private AdminViewProductsView view;
    private int adminId;
    private Connection conn;

    public AdminViewProductsController(AdminViewProductsView view, int adminId) {
        this.view = view;
        this.adminId = adminId;

        loadProducts();

        view.getAddButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                view.getFrame().dispose();
                AdminAddProductView addProductView = new AdminAddProductView();
                new AdminAddProductController(addProductView, adminId);
            }
        });

        view.getBackButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                view.getFrame().dispose();
                AdminHomePageView homePageView = new AdminHomePageView();
                new AdminHomePageController(homePageView, adminId);
            }
        });
    }

    private void loadProducts(){
        List<MenuProduct> products = new ArrayList<>();
        try{
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM Menus";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                products.add(new MenuProduct(
                    rs.getInt("menu_id"),
                    rs.getString("menu_name"),
                    rs.getString("menu_description"),
                    rs.getDouble("unit_price"),
                    rs.getString("preparation_time"),
                    rs.getBoolean("is_available")
                ));
            }

            view.displayProducts(products);

            for (int i = 0; i < products.size(); i++){
                MenuProduct product = products.get(i);

                view.getDeleteButtons().get(i).addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e){
                        int choice = JOptionPane.showConfirmDialog(null,
                            "Are you sure you want to delete " + product.getName() + "?",
                            "Confirm Delete", JOptionPane.YES_NO_OPTION);
                        if (choice == JOptionPane.YES_OPTION){
                            deleteProduct(product.getId());
                        }
                    }
                });

                view.getAvailabilityLabels().get(i).addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e){
                        toggleAvailability(product);
                    }
                });

                view.getProductButtons().get(i).addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e){
                        view.getFrame().dispose();
                        AdminUpdateProductView updateView = new AdminUpdateProductView();
                        new AdminUpdateProductController(updateView, adminId, product.getId());
                    }
                });
            }
        } 
        catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    private void deleteProduct(int id){
        try{
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM Menus WHERE menu_id = ?");
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(null, "Product deleted successfully!");
                loadProducts();
            }
        } 
        catch (SQLException ex){
            JOptionPane.showMessageDialog(null, "Error deleting product: " + ex.getMessage());
        }
    }

    private void toggleAvailability(MenuProduct product){
        boolean newStatus = !product.isAvailable();
        try{
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE Menus SET is_available=? WHERE menu_id=?");
            ps.setInt(1, newStatus ? 1 : 0);
            ps.setInt(2, product.getId());
            ps.executeUpdate();

            product.setAvailable(newStatus);
            JOptionPane.showMessageDialog(null, "Product is now " + (newStatus ? "Available" : "Unavailable"));
            loadProducts();
        } 
        catch (SQLException ex){
            JOptionPane.showMessageDialog(null, "Error toggling availability: " + ex.getMessage());
        }
    }
}
