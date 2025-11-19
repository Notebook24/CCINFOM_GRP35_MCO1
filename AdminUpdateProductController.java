import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.io.File;

public class AdminUpdateProductController {
    private AdminUpdateProductView view;
    private int adminId;
    private int menuId;
    private int currentCategoryId; // Store the current category ID for navigation

    public AdminUpdateProductController(AdminUpdateProductView view, int adminId, int menuId){
        this.view = view;
        this.adminId = adminId;
        this.menuId = menuId;
        loadCategories();
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
                returnToProductsView();
            }
        });

        view.getBrowseImageButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                browseImage();
            }
        });
    }

    /**
     * Load categories into the dropdown
     */
    private void loadCategories(){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            conn = DBConnection.getConnection();
            String query = "SELECT menu_category_id, menu_category_name FROM Menu_Category WHERE is_available = 1 ORDER BY menu_category_name";
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            while(rs.next()){
                String categoryItem = rs.getInt("menu_category_id") + " - " + rs.getString("menu_category_name");
                model.addElement(categoryItem);
            }
            view.getCategoryComboBox().setModel(model);

        } 
        catch(SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading categories: " + e.getMessage());
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

    public void loadProductDetails(){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            conn = DBConnection.getConnection();
            String query = "SELECT menu_name, menu_description, unit_price, preparation_time, image, menu_category_id FROM menus WHERE menu_id = ?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, menuId);
            rs = stmt.executeQuery();

            if(rs.next()){
                view.getProductField().setText(rs.getString("menu_name"));
                view.getDescriptionField().setText(rs.getString("menu_description"));
                view.getPriceField().setText(String.valueOf(rs.getDouble("unit_price")));
                view.getPreparationField().setText(rs.getString("preparation_time"));
                
                // Set image path and preview
                String imagePath = rs.getString("image");
                if (imagePath != null && !imagePath.trim().isEmpty()) {
                    view.setImagePath(imagePath);
                }
                
                // Set category and store current category ID for navigation
                int categoryId = rs.getInt("menu_category_id");
                view.setSelectedCategoryId(categoryId);
                this.currentCategoryId = categoryId; // Store for back navigation
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

    /**
     * Handle image browsing
     */
    private void browseImage(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Product Image");
        
        // Set file filter for images
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                String name = f.getName().toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".jpeg") || 
                       name.endsWith(".png") || name.endsWith(".gif");
            }
            
            public String getDescription() {
                return "Image Files (*.jpg, *.jpeg, *.png, *.gif)";
            }
        });
        
        int result = fileChooser.showOpenDialog(view.getFrame());
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            view.setImagePath(selectedFile.getAbsolutePath());
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
            // Update current category ID if it was changed
            this.currentCategoryId = view.getSelectedCategoryId();
        }
    }

    public void updateProduct(){
        Connection conn = null;
        PreparedStatement stmt = null;

        try{
            conn = DBConnection.getConnection();
            String query = "UPDATE menus SET menu_name=?, menu_description=?, unit_price=?, preparation_time=?, image=?, menu_category_id=? WHERE menu_id=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, view.getProductName());
            stmt.setString(2, view.getDescription());
            stmt.setDouble(3, Double.parseDouble(view.getPrice()));
            stmt.setString(4, view.getPrepTime());
            stmt.setString(5, view.getImagePath());
            stmt.setInt(6, view.getSelectedCategoryId());
            stmt.setInt(7, menuId);

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

    /**
     * Return to the products view with the correct category filter
     */
    private void returnToProductsView(){
        view.getFrame().dispose();
        
        // Use the current category ID (which might have been updated)
        int targetCategoryId = this.currentCategoryId;
        
        // Create the products view and controller with the specific category
        AdminViewProductsView productsView = new AdminViewProductsView();
        new AdminViewProductsController(productsView, adminId, targetCategoryId);
    }
}