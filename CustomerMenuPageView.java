import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomerMenuPageView {
    private JFrame frame;
    private JPanel headerPanel, cartPanel, footerPanel;
    private JButton logoutButton, settingsButton, checkoutButton;
    private JButton paymentsButton, ordersButton, profileButton;
    private JLabel logoLabel, totalCostLabel, prepTimeLabel;

    private List<JButton> cartButtons;
    private List<JButton> plusButtons;
    private List<JButton> minusButtons;
    private List<JLabel> quantityLabels;
    private List<MenuProduct> products;

    public CustomerMenuPageView(){
        frame = new JFrame("Customer Menu Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1920, 1080);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Header
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 100, 20, 100));
        headerPanel.setBackground(Color.WHITE);

        // Left: Logo
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(Color.WHITE);
        ImageIcon rawLogo = new ImageIcon("design_images/koreanexpress-logo.png");
        Image scaledLogo = rawLogo.getImage().getScaledInstance(300, 90, Image.SCALE_SMOOTH);
        ImageIcon logoIcon = new ImageIcon(scaledLogo);
        logoLabel = new JLabel(logoIcon);
        logoPanel.add(logoLabel);

        // Right: Navigation Bar (uniform across pages)
        JPanel navPanel = new JPanel();
        navPanel.setBackground(Color.WHITE);
        navPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 50, 20));

        paymentsButton = new JButton("Payments");
        ordersButton = new JButton("Orders");
        profileButton = new JButton("Profile");
        logoutButton = new JButton("Log Out");

        Font navFont = new Font("SansSerif", Font.BOLD, 20);
        Color navColor = new Color(230, 0, 0);

        JButton[] navButtons = {paymentsButton, ordersButton, profileButton, logoutButton};
        for (JButton b : navButtons) {
            b.setFont(navFont);
            b.setForeground(navColor);
            b.setFocusPainted(false);
            b.setContentAreaFilled(false);
            b.setBorderPainted(false);
        }

        navPanel.add(paymentsButton);
        navPanel.add(ordersButton);
        navPanel.add(profileButton);
        navPanel.add(logoutButton);

        headerPanel.add(logoPanel, BorderLayout.WEST);
        headerPanel.add(navPanel, BorderLayout.EAST);

        frame.add(headerPanel, BorderLayout.NORTH);

        // cart panel
        cartPanel = new JPanel();
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS));
        cartPanel.setBackground(Color.WHITE);
        cartPanel.setBorder(BorderFactory.createEmptyBorder(30, 200, 30, 200));

        JScrollPane scrollPane = new JScrollPane(cartPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setBorder(null);
        frame.add(scrollPane, BorderLayout.CENTER);

        // footer panel
        footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));
        footerPanel.setBackground(Color.WHITE);

        totalCostLabel = new JLabel("TOTAL COST: ");
        totalCostLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalCostLabel.setForeground(new Color(230, 0, 0));
        totalCostLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 50));
        prepTimeLabel = new JLabel("TOTAL PREP TIME: ");
        prepTimeLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        prepTimeLabel.setForeground(new Color(230, 0, 0));
        prepTimeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 50));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.add(totalCostLabel);
        infoPanel.add(prepTimeLabel);

        checkoutButton = new JButton("Checkout");
        checkoutButton.setFont(new Font("Helvetica Nueue", Font.BOLD, 18));
        checkoutButton.setBackground(new Color(255, 130, 130));
        checkoutButton.setForeground(Color.BLACK);
        checkoutButton.setFocusPainted(false);
        checkoutButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        JPanel checkoutPanel = new JPanel();
        checkoutPanel.setBackground(Color.WHITE);
        checkoutPanel.add(checkoutButton);

        footerPanel.add(infoPanel, BorderLayout.WEST);
        footerPanel.add(checkoutPanel, BorderLayout.EAST);

        frame.add(footerPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    /**
     * Displays all products in a scrollable list with images and action buttons.
     */
    public void displayProducts(List<MenuProduct> products, Map<Integer, Integer> cartMap){
        this.products = products;
        cartPanel.removeAll();

        cartButtons = new ArrayList<>();
        plusButtons = new ArrayList<>();
        minusButtons = new ArrayList<>();
        quantityLabels = new ArrayList<>();

        for (int i = 0; i < products.size(); i++){
            MenuProduct product = products.get(i);

            // Parent container for each product
            JPanel itemPanel = new JPanel(new BorderLayout(20, 10));
            itemPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY));
            itemPanel.setBackground(Color.WHITE);
            itemPanel.setMaximumSize(new Dimension(1400, 250));

            // LEFT: Image
            ImageIcon productImage = new ImageIcon(product.getImagePath());
            Image scaledImage = productImage.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            imageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            itemPanel.add(imageLabel, BorderLayout.WEST);

            // CENTER: Info
            JPanel infoPanel = new JPanel();
            infoPanel.setBackground(Color.WHITE);
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

            JLabel nameLabel = new JLabel(product.getName());
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
            nameLabel.setForeground(new Color(230, 0, 0));

            JLabel descLabel = new JLabel(product.getDescription());
            descLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));

            JLabel prepLabel = new JLabel("Preparation Time: " + product.getPrepTime());
            prepLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

            infoPanel.add(nameLabel);
            infoPanel.add(Box.createVerticalStrut(10));
            infoPanel.add(descLabel);
            infoPanel.add(Box.createVerticalStrut(20));
            infoPanel.add(prepLabel);
            itemPanel.add(infoPanel, BorderLayout.CENTER);

            // RIGHT: Price and Cart Controls
            JPanel controlPanel = new JPanel();
            controlPanel.setBackground(Color.WHITE);
            controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
            controlPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JLabel priceLabel = new JLabel("₱" + String.format("%.2f", product.getPrice()));
            priceLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
            priceLabel.setForeground(new Color(230, 0, 0));
            priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JButton cartButton = new JButton("Add to Cart");
            cartButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            cartButton.setBackground(new Color(255, 160, 160));
            cartButton.setFocusPainted(false);

            JButton plusButton = new JButton("+");
            JButton minusButton = new JButton("-");
            JLabel quantityLabel = new JLabel("0", SwingConstants.CENTER);
            quantityLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

            JPanel quantityPanel = new JPanel();
            quantityPanel.setBackground(Color.WHITE);
            quantityPanel.add(minusButton);
            quantityPanel.add(quantityLabel);
            quantityPanel.add(plusButton);

            // Enable cart state
            if (cartMap != null && cartMap.containsKey(product.getId())) {
                int quantity = cartMap.get(product.getId());
                cartButton.setText("Remove from Cart");
                quantityLabel.setText(String.valueOf(quantity));
                plusButton.setEnabled(true);
                minusButton.setEnabled(true);
            } else {
                plusButton.setEnabled(false);
                minusButton.setEnabled(false);
            }

            controlPanel.add(priceLabel);
            controlPanel.add(Box.createVerticalStrut(20));
            controlPanel.add(cartButton);
            controlPanel.add(Box.createVerticalStrut(20));
            controlPanel.add(quantityPanel);
            itemPanel.add(controlPanel, BorderLayout.EAST);

            cartPanel.add(itemPanel);
            cartPanel.add(Box.createVerticalStrut(20));

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
