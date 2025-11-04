import java.awt.event.*;
import java.sql.*;
import javax.swing.JOptionPane;

public class AdminAddProductController {
    private AdminAddProductView addProductView;
    private int adminId;

    public AdminAddProductController(AdminAddProductView view, int adminId){
        this.addProductView = view;
        this.adminId = adminId;

        addProductView.getAddProductButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                if (!addProductView.validateInputs()){
                    return;
                }

                String name = addProductView.getProductName().trim();
                String desc = addProductView.getDescription().trim();
                String priceText = addProductView.getPrice().trim();
                String prepTime = addProductView.getPrepTime().trim();

                String sql = "INSERT INTO Menus (menu_name, menu_description, unit_price, preparation_time) VALUES (?, ?, ?, ?)";

                try (Connection conn = DBConnection.getConnection()){                    
                     PreparedStatement pstmt = conn.prepareStatement(sql);

                    pstmt.setString(1, name);
                    pstmt.setString(2, desc);
                    pstmt.setBigDecimal(3, new java.math.BigDecimal(priceText));
                    pstmt.setTime(4, java.sql.Time.valueOf(prepTime));

                    int rowsInserted = pstmt.executeUpdate();

                    if (rowsInserted > 0){
                        JOptionPane.showMessageDialog(addProductView.getFrame(),
                                             "Product added successfully!",
                                               "Success",
                                                     JOptionPane.INFORMATION_MESSAGE);
                    } 
                    else{
                        JOptionPane.showMessageDialog(addProductView.getFrame(),
                                             "Product not added due to an unexpected issue.",
                                               "Error",
                                                      JOptionPane.ERROR_MESSAGE);
                    }

                } 
                catch (SQLException ex){
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(addProductView.getFrame(),
                                                  "Error: Unable to add product.\n" + ex.getMessage(),
                                           "Database Error",
                                                  JOptionPane.ERROR_MESSAGE);
                } 
                catch (IllegalArgumentException ex){
                    JOptionPane.showMessageDialog(addProductView.getFrame(),
                                         "Invalid format for price or preparation time.",
                                           "Input Error",
                                                  JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        addProductView.getBackButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                addProductView.getFrame().dispose();

                AdminHomePageView homeView = new AdminHomePageView();
                new AdminHomePageController(homeView, adminId);
            }
        });
    }
}
