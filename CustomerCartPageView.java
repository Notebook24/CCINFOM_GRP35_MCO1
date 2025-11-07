import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomerCartPageView {
    private JFrame frame;
    private JPanel headerPanel, summaryPanel, footerPanel;
    private JButton logoutButton, settingsButton, returnButton, checkOutButton;
    private JLabel logoLabel, totalCostLabel, totalPrepTimeLabel;

    private List<JLabel> nameLabels;
    private List<JLabel> priceLabels;
    private List<JLabel> quantityLabels;

    public CustomerCartPageView() {
        frame = new JFrame("Customer Cart Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        logoLabel = new JLabel("Logo of Incorporation");
        logoutButton = new JButton("Log out");
        settingsButton = new JButton("Settings");

        JPanel rightHeader = new JPanel();
        rightHeader.add(logoutButton);
        rightHeader.add(settingsButton);

        headerPanel.add(logoLabel, BorderLayout.WEST);
        headerPanel.add(rightHeader, BorderLayout.EAST);
        frame.add(headerPanel, BorderLayout.NORTH);

        summaryPanel = new JPanel();
        summaryPanel.setLayout(new GridLayout(0, 3, 10, 10));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JScrollPane scrollPane = new JScrollPane(summaryPanel);
        frame.add(scrollPane, BorderLayout.CENTER);

        footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        totalCostLabel = new JLabel("Total Price: ₱0.00");
        totalPrepTimeLabel = new JLabel("Total Prep Time: 00:00:00");
        infoPanel.add(totalCostLabel);
        infoPanel.add(totalPrepTimeLabel);

        JPanel buttonPanel = new JPanel();
        returnButton = new JButton("Return to Menu");
        checkOutButton = new JButton("Check out");
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

        summaryPanel.add(new JLabel("Product Name", SwingConstants.CENTER));
        summaryPanel.add(new JLabel("Price", SwingConstants.CENTER));
        summaryPanel.add(new JLabel("Quantity", SwingConstants.CENTER));

        for (MenuProduct product : products){
            if (cartMap.containsKey(product.getId())){
                int qty = cartMap.get(product.getId());

                JLabel nameLabel = new JLabel(product.getName());
                JLabel priceLabel = new JLabel("₱" + String.format("%.2f", product.getPrice()));
                JLabel quantityLabel = new JLabel(String.valueOf(qty), SwingConstants.CENTER);

                summaryPanel.add(nameLabel);
                summaryPanel.add(priceLabel);
                summaryPanel.add(quantityLabel);

                nameLabels.add(nameLabel);
                priceLabels.add(priceLabel);
                quantityLabels.add(quantityLabel);
            }
        }

        summaryPanel.revalidate();
        summaryPanel.repaint();
    }

    public JFrame getFrame(){
        return frame;
    }

    public JButton getLogoutButton(){
        return logoutButton;
    }

    public JButton getSettingsButton(){
        return settingsButton;
    }

    public JButton getReturnButton(){
        return returnButton;
    }

    public JButton getCheckOutButton(){
        return checkOutButton;
    }

    public JLabel getTotalCostLabel(){
        return totalCostLabel;
    }

    public JLabel getTotalPrepTimeLabel(){
        return totalPrepTimeLabel;
    }
}
