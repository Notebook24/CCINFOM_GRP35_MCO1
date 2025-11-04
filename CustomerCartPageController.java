import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class CustomerCartPageController {
    private CustomerCartPageView view;
    private int customerId;
    private List<MenuProduct> products;
    private Connection conn;
    private Map<Integer, Integer> cartMap;

    public CustomerCartPageController(CustomerCartPageView view, int id){
        this.view = view;
        customerId = id;
        this.cartMap = new HashMap<>();
        loadAvailableProducts();
    }

    private void loadAvailableProducts(){
        products = new ArrayList<>();
        try{
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM Menus WHERE is_available = 1";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
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
            attachListeners();

        } 
        catch (SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading menu: " + e.getMessage());
        }
    }

    private void attachListeners(){
        List<JButton> cartButtons = view.getCartButtons();
        List<JButton> plusButtons = view.getPlusButtons();
        List<JButton> minusButtons = view.getMinusButtons();
        List<JLabel> quantityLabels = view.getQuantityLabels();

        for (int i = 0; i < products.size(); i++) {
            MenuProduct product = products.get(i);
            JButton cartBtn = cartButtons.get(i);
            JButton plusBtn = plusButtons.get(i);
            JButton minusBtn = minusButtons.get(i);
            JLabel qtyLabel = quantityLabels.get(i);

            cartBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    if (cartMap.containsKey(product.getId())){
                        cartMap.remove(product.getId());
                        cartBtn.setText("Add to Cart");
                        qtyLabel.setText("0");
                        plusBtn.setEnabled(false);
                        minusBtn.setEnabled(false);
                    } 
                    else {
                        cartMap.put(product.getId(), 1);
                        cartBtn.setText("Remove from Cart");
                        qtyLabel.setText("1");
                        plusBtn.setEnabled(true);
                        minusBtn.setEnabled(true);
                    }
                    updateTotals();
                }
            });

            plusBtn.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e){
                    if (cartMap.containsKey(product.getId())){
                        int qty = Integer.parseInt(qtyLabel.getText());
                        qty++;
                        qtyLabel.setText(String.valueOf(qty));
                        cartMap.put(product.getId(), qty);
                        updateTotals();
                    }
                }
            });

            minusBtn.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e){
                    if (cartMap.containsKey(product.getId())){
                        int qty = Integer.parseInt(qtyLabel.getText());
                        if (qty > 1){
                            qty--;
                            qtyLabel.setText(String.valueOf(qty));
                            cartMap.put(product.getId(), qty);
                        } 
                        else {
                            cartMap.remove(product.getId());
                            cartBtn.setText("Add to Cart");
                            qtyLabel.setText("0");
                            plusBtn.setEnabled(false);
                            minusBtn.setEnabled(false);
                        }
                        updateTotals();
                    }
                }
            });
        }

        view.getCheckoutButton().addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                view.getFrame().dispose();

                CustomerCheckoutSessionView checkoutSessionView = new CustomerCheckoutSessionView();
                //new CustomerCheckoutSessionController(view, customerId);
            }
        });
    }

    private void updateTotals(){
        double totalCost = 0;
        int totalSeconds = 0;

        for (MenuProduct product : products){
            if (cartMap.containsKey(product.getId())){
                int qty = cartMap.get(product.getId());
                totalCost += product.getPrice() * qty;

                String time = product.getPrepTime();
                String[] parts = time.split(":");
                int h = Integer.parseInt(parts[0]);
                int m = Integer.parseInt(parts[1]);
                int s = Integer.parseInt(parts[2]);
                totalSeconds += (h * 3600 + m * 60 + s) * qty;
            }
        }

        int h = totalSeconds / 3600;
        int m = (totalSeconds % 3600) / 60;
        int s = totalSeconds % 60;
        String formatted = String.format("%02d:%02d:%02d", h, m, s);

        view.getTotalCostLabel().setText("Total Cost: ₱" + String.format("%.2f", totalCost));
        view.getPrepTimeLabel().setText("Prep Time: " + formatted);
    }
}
