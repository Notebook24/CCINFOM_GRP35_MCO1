import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AdminViewProductsView {
    private JFrame frame;
    private JPanel headerPanel, productPanel, footerPanel;
    private JButton logoutButton, settingsButton, addButton, backButton;
    private JLabel logoLabel;
    private List<JButton> productButtons = new ArrayList<>();
    private List<JButton> deleteButtons = new ArrayList<>();
    private List<JLabel> availabilityLabels = new ArrayList<>();

    public AdminViewProductsView() {
        frame = new JFrame("Admin - View Products");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLayout(new BorderLayout());
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

        productPanel = new JPanel();
        productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(productPanel);

        footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        addButton = new JButton("Add Product");
        backButton = new JButton("Back");
        footerPanel.add(addButton);
        footerPanel.add(backButton);

        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(footerPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public void displayProducts(List<MenuProduct> products){
        productPanel.removeAll();
        productButtons.clear();
        deleteButtons.clear();
        availabilityLabels.clear();

        for (MenuProduct p : products){
            JPanel row = new JPanel(new GridLayout(1, 3, 10, 5));

            JButton nameButton = new JButton(p.getName());
            JButton deleteButton = new JButton("Delete");
            JLabel availability = new JLabel(
                p.isAvailable() ? "Available" : "Unavailable",
                SwingConstants.CENTER
            );
            availability.setForeground(p.isAvailable() ? Color.GREEN : Color.RED);
            availability.setCursor(new Cursor(Cursor.HAND_CURSOR));

            row.add(nameButton);
            row.add(deleteButton);
            row.add(availability);

            productButtons.add(nameButton);
            deleteButtons.add(deleteButton);
            availabilityLabels.add(availability);

            productPanel.add(row);
        }

        productPanel.revalidate();
        productPanel.repaint();
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

    public JButton getAddButton(){
        return addButton;
    }

    public JButton getBackButton(){
        return backButton;
    }

    public List<JButton> getProductButtons(){
        return productButtons;
    }

    public List<JButton> getDeleteButtons(){
        return deleteButtons;
    }

    public List<JLabel> getAvailabilityLabels(){
        return availabilityLabels;
    }
}
