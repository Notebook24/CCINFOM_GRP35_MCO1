import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomerCartPageView {
    private JFrame frame;
    private JPanel headerPanel, summaryPanel, footerPanel;
    private JButton returnButton, checkOutButton;
    private JLabel logoLabel, totalCostLabel, totalPrepTimeLabel, totalDeliveryTimeLabel;

    private List<JLabel> nameLabels;
    private List<JLabel> priceLabels;
    private List<JLabel> quantityLabels;

    public CustomerCartPageView() {
        frame = new JFrame("Customer Cart Page");
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

        // Right: Title
        JLabel cartTitle = new JLabel("Your Cart — so far.");
        cartTitle.setFont(new Font("SansSerif", Font.BOLD, 40));
        cartTitle.setForeground(new Color(230, 0, 0));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 20));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(cartTitle);

        headerPanel.add(logoPanel, BorderLayout.WEST);
        headerPanel.add(titlePanel, BorderLayout.EAST);
        frame.add(headerPanel, BorderLayout.NORTH);

        // Summary Panel
        summaryPanel = new JPanel();
        summaryPanel.setLayout(new GridBagLayout());
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(40, 150, 40, 150));

        JScrollPane scrollPane = new JScrollPane(summaryPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Footer
        footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 150, 40, 150));
        footerPanel.setBackground(Color.WHITE);

        // Info panel (Subtotal, Prep Time, Delivery Time)
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);

        totalCostLabel = new JLabel("SUBTOTAL: ₱0.00");
        totalCostLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        totalCostLabel.setForeground(new Color(230, 0, 0));

        totalPrepTimeLabel = new JLabel("TOTAL PREP TIME: 0 hrs");
        totalPrepTimeLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        totalPrepTimeLabel.setForeground(new Color(230, 0, 0));

        totalDeliveryTimeLabel = new JLabel("DELIVERY TIME: 0 mins");
        totalDeliveryTimeLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        totalDeliveryTimeLabel.setForeground(new Color(230, 0, 0));

        infoPanel.add(totalCostLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(totalPrepTimeLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(totalDeliveryTimeLabel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 20));
        buttonPanel.setBackground(Color.WHITE);

        returnButton = new JButton("Back to Menu");
        checkOutButton = new JButton("Place Order");

        JButton[] buttons = {returnButton, checkOutButton};
        for (JButton btn : buttons) {
            btn.setFont(new Font("SansSerif", Font.BOLD, 18));
            btn.setBackground(new Color(255, 180, 180));
            btn.setForeground(Color.BLACK);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        }

        buttonPanel.add(returnButton);
        buttonPanel.add(checkOutButton);

        footerPanel.add(infoPanel, BorderLayout.WEST);
        footerPanel.add(buttonPanel, BorderLayout.EAST);
        frame.add(footerPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public void displayCartItems(List<MenuProduct> products, Map<Integer, Integer> cartMap) {
        summaryPanel.removeAll();

        nameLabels = new ArrayList<>();
        priceLabels = new ArrayList<>();
        quantityLabels = new ArrayList<>();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(10, 20, 10, 20);

        // ===== Table Header =====
        gbc.gridy = 0;
        gbc.gridx = 0;
        JLabel menuHeader = new JLabel("Menu", SwingConstants.LEFT);
        menuHeader.setFont(new Font("SansSerif", Font.BOLD, 22));
        menuHeader.setForeground(new Color(230, 0, 0));
        summaryPanel.add(menuHeader, gbc);

        gbc.gridx = 1;
        JLabel prepHeader = new JLabel("Prep Time", SwingConstants.CENTER);
        prepHeader.setFont(new Font("SansSerif", Font.BOLD, 22));
        prepHeader.setForeground(new Color(230, 0, 0));
        summaryPanel.add(prepHeader, gbc);

        gbc.gridx = 2;
        JLabel qtyHeader = new JLabel("Qty", SwingConstants.CENTER);
        qtyHeader.setFont(new Font("SansSerif", Font.BOLD, 22));
        qtyHeader.setForeground(new Color(230, 0, 0));
        summaryPanel.add(qtyHeader, gbc);

        gbc.gridx = 3;
        JLabel priceHeader = new JLabel("Price", SwingConstants.RIGHT);
        priceHeader.setFont(new Font("SansSerif", Font.BOLD, 22));
        priceHeader.setForeground(new Color(230, 0, 0));
        summaryPanel.add(priceHeader, gbc);

        gbc.gridy++;

        // ===== Cart Items =====
        for (MenuProduct product : products) {
            if (cartMap.containsKey(product.getId())) {
                int qty = cartMap.get(product.getId());

                gbc.gridx = 0;
                JLabel nameLabel = new JLabel(product.getName(), SwingConstants.LEFT);
                nameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
                summaryPanel.add(nameLabel, gbc);

                gbc.gridx = 1;
                JLabel prepLabel = new JLabel(formatPrepTime(product.getPrepTime()), SwingConstants.CENTER);
                prepLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
                summaryPanel.add(prepLabel, gbc);

                gbc.gridx = 2;
                JLabel quantityLabel = new JLabel(String.valueOf(qty), SwingConstants.CENTER);
                quantityLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
                summaryPanel.add(quantityLabel, gbc);

                gbc.gridx = 3;
                JLabel priceLabel = new JLabel("₱" + String.format("%.2f", product.getPrice()), SwingConstants.RIGHT);
                priceLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
                summaryPanel.add(priceLabel, gbc);

                gbc.gridy++;

                nameLabels.add(nameLabel);
                priceLabels.add(priceLabel);
                quantityLabels.add(quantityLabel);
            }
        }

        summaryPanel.revalidate();
        summaryPanel.repaint();
    }

    private String formatPrepTime(String prepTime) {
        try {
            String[] parts = prepTime.split(":");
            int h = Integer.parseInt(parts[0]);
            int m = Integer.parseInt(parts[1]);
            if (h > 0 && m > 0) return h + " hr and " + m + " mins";
            else if (h > 0) return h + " hr";
            else return m + " mins";
        } catch (Exception e) {
            return prepTime;
        }
    }

    // ===== Getters formatted properly =====
    public JFrame getFrame() {
        return frame;
    }

    public JButton getReturnButton() {
        return returnButton;
    }

    public JButton getCheckOutButton() {
        return checkOutButton;
    }

    public JLabel getTotalCostLabel() {
        return totalCostLabel;
    }

    public JLabel getTotalPrepTimeLabel() {
        return totalPrepTimeLabel;
    }

    public JLabel getTotalDeliveryTimeLabel() {
        return totalDeliveryTimeLabel;
    }
}
