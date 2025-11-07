import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomerMenuPageView {
    private JFrame frame;
    private JPanel headerPanel, cartPanel, footerPanel;
    private JButton logoutButton, settingsButton, checkoutButton;
    private JLabel logoLabel, totalCostLabel, prepTimeLabel;

    private List<JButton> cartButtons;
    private List<JButton> plusButtons;
    private List<JButton> minusButtons;
    private List<JLabel> quantityLabels;
    private List<MenuProduct> products;

    public CustomerMenuPageView(){
        frame = new JFrame("Customer Menu Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        logoLabel = new JLabel("Restaurant Logo");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 12));

        logoutButton = new JButton("Log out");
        settingsButton = new JButton("Settings");
        JPanel rightHeaderPanel = new JPanel();
        rightHeaderPanel.add(logoutButton);
        rightHeaderPanel.add(settingsButton);

        headerPanel.add(logoLabel, BorderLayout.WEST);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);

        frame.add(headerPanel, BorderLayout.NORTH);

        cartPanel = new JPanel();
        cartPanel.setLayout(new GridLayout(0, 1, 10, 10));
        cartPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        JScrollPane scrollPane = new JScrollPane(cartPanel);
        frame.add(scrollPane, BorderLayout.CENTER);

        footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        totalCostLabel = new JLabel("Total Cost:");
        prepTimeLabel = new JLabel("Total Prep time:");
        infoPanel.add(totalCostLabel);
        infoPanel.add(prepTimeLabel);

        checkoutButton = new JButton("Checkout");
        JPanel checkoutPanel = new JPanel();
        checkoutPanel.add(checkoutButton);

        footerPanel.add(infoPanel, BorderLayout.WEST);
        footerPanel.add(checkoutPanel, BorderLayout.EAST);

        frame.add(footerPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public void displayProducts(List<MenuProduct> products, Map<Integer, Integer> cartMap){
        this.products = products;
        cartPanel.removeAll();

        cartButtons = new ArrayList<>();
        plusButtons = new ArrayList<>();
        minusButtons = new ArrayList<>();
        quantityLabels = new ArrayList<>();

        JPanel headerRow = new JPanel(new GridLayout(1, 6));
        headerRow.add(new JLabel("Product name"));
        headerRow.add(new JLabel("Description"));
        headerRow.add(new JLabel("Price"));
        headerRow.add(new JLabel("Prep Time"));
        headerRow.add(new JLabel("Add to Cart"));
        headerRow.add(new JLabel("Quantity", SwingConstants.CENTER));
        cartPanel.add(headerRow);

        for (int i = 0; i < products.size(); i++){
            MenuProduct product = products.get(i);
            JPanel itemPanel = new JPanel(new GridLayout(1, 6, 10, 10));
            itemPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

            JLabel nameLabel = new JLabel(product.getName());
            JLabel descLabel = new JLabel(product.getDescription());
            JLabel priceLabel = new JLabel("₱" + String.format("%.2f", product.getPrice()));
            JLabel prepTimeLabel = new JLabel(product.getPrepTime());

            JButton cartButton = new JButton("Add to Cart");
            JButton plusButton = new JButton("+");
            JButton minusButton = new JButton("-");
            JLabel quantityLabel = new JLabel("0", SwingConstants.CENTER);
            
            if (cartMap != null && cartMap.containsKey(product.getId())){
                int quantity = cartMap.get(product.getId());
                cartButton.setText("Remove from Cart");
                quantityLabel.setText(String.valueOf(quantity));
                plusButton.setEnabled(true);
                minusButton.setEnabled(true);
            } 
            else{
                plusButton.setEnabled(false);
                minusButton.setEnabled(false);
            }

            JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            quantityPanel.add(minusButton);
            quantityPanel.add(quantityLabel);
            quantityPanel.add(plusButton);

            itemPanel.add(nameLabel);
            itemPanel.add(descLabel);
            itemPanel.add(priceLabel);
            itemPanel.add(prepTimeLabel);
            itemPanel.add(cartButton);
            itemPanel.add(quantityPanel);

            cartPanel.add(itemPanel);

            cartButtons.add(cartButton);
            plusButtons.add(plusButton);
            minusButtons.add(minusButton);
            quantityLabels.add(quantityLabel);
        }

        cartPanel.revalidate();
        cartPanel.repaint();
    }

    public void updateCartItemState(int productIndex, int quantity, boolean isInCart){
        if (productIndex >= 0 && productIndex < cartButtons.size()){
            JButton cartButton = cartButtons.get(productIndex);
            JLabel quantityLabel = quantityLabels.get(productIndex);
            JButton plusButton = plusButtons.get(productIndex);
            JButton minusButton = minusButtons.get(productIndex);
            
            if (isInCart){
                cartButton.setText("Remove from Cart");
                quantityLabel.setText(String.valueOf(quantity));
                plusButton.setEnabled(true);
                minusButton.setEnabled(true);
            } 
            else{
                cartButton.setText("Add to Cart");
                quantityLabel.setText("0");
                plusButton.setEnabled(false);
                minusButton.setEnabled(false);
            }
        }
    }

    public JFrame getFrame(){
        return frame;
    }

    public List<JButton> getCartButtons(){
        return cartButtons;
    }

    public List<JButton> getPlusButtons(){
        return plusButtons;
    }

    public List<JButton> getMinusButtons(){
        return minusButtons;
    }

    public List<JLabel> getQuantityLabels(){
        return quantityLabels;
    }

    public JButton getCheckoutButton(){
        return checkoutButton;
    }

    public JButton getLogoutButton() {
        return logoutButton;
    }

    public JButton getSettingsButton() {
        return settingsButton;
    }

    public JLabel getTotalCostLabel(){
        return totalCostLabel;
    }

    public JLabel getPrepTimeLabel(){
        return prepTimeLabel;
    }
}