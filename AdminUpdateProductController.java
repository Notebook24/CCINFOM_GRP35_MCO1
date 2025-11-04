import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class AdminUpdateProductController {
    private AdminUpdateProductView view;
    private int adminId;
    private int menuId;

    public AdminUpdateProductController(AdminUpdateProductView view, int adminId, int menuId){
        this.view = view;
        this.adminId = adminId;
        this.menuId = menuId;
        loadProductDetails();
        initController();
    }

    public void initController(){
        view.getUpdateProductButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                handleUpdate();
            }
        });

        view.getBackButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                view.getFrame().dispose();

                AdminHomePageView home = new AdminHomePageView();
                new AdminHomePageController(home, adminId);
            }
        });

        view.getLogoutButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                view.getFrame().dispose();

                LandingPageView landing = new LandingPageView();
                new LandingPageController(landing);
            }
        });

        view.getSettingsButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
               view.getFrame().dispose();

                AdminSettingsView settings = new AdminSettingsView();
                //new AdminSettingsController(settings);
            }
        });
    }

    public void loadProductDetails(){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            conn = DBConnection.getConnection();
            String query = "SELECT menu_name, menu_description, unit_price, preparation_time FROM menus WHERE menu_id = ?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, menuId);
            rs = stmt.executeQuery();

            if(rs.next()){
                view.getProductField().setText(rs.getString("menu_name"));
                view.getDescriptionField().setText(rs.getString("menu_description"));
                view.getPriceField().setText(String.valueOf(rs.getDouble("unit_price")));
                view.getPreparationField().setText(rs.getString("preparation_time"));
            } 
            else{
                JOptionPane.showMessageDialog(null, "Menu item not found!");
                view.getFrame().dispose();
            }
        } 
        catch(SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading product details: " + e.getMessage());
        } 
        finally{
            try{
                if(rs != null) rs.close();
                if(stmt != null) stmt.close();
                if(conn != null) conn.close();
            } 
            catch(SQLException ex){
                ex.printStackTrace();
            }
        }
    }

    public void handleUpdate(){
        if(!view.validateInputs()){
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            null,
            "Are you sure you want to update this product?",
            "Confirm Update",
            JOptionPane.YES_NO_OPTION
        );

        if(confirm == JOptionPane.YES_OPTION){
            updateProduct();
            loadProductDetails();
        }
    }

    public void updateProduct(){
        Connection conn = null;
        PreparedStatement stmt = null;

        try{
            conn = DBConnection.getConnection();
            String query = "UPDATE menus SET menu_name=?, menu_description=?, unit_price=?, preparation_time=? WHERE menu_id=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, view.getProductName());
            stmt.setString(2, view.getDescription());
            stmt.setDouble(3, Double.parseDouble(view.getPrice()));
            stmt.setString(4, view.getPrepTime());
            stmt.setInt(5, menuId);

            int rows = stmt.executeUpdate();    
            if(rows > 0){
                JOptionPane.showMessageDialog(null, "Product updated successfully!");
            } 
            else{
                JOptionPane.showMessageDialog(null, "Update failed. Product not found.");
            }
        } 
        catch(SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating product: " + e.getMessage());
        } 
        finally{
            try{
                if(stmt != null) stmt.close();
                if(conn != null) conn.close();
            } 
            catch(SQLException ex){
                ex.printStackTrace();
            }
        }
    }
}
