import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDeliveryTrackerView {

    private JFrame frame;
    public JPanel headerPanel, ordersContainer;
    private JScrollPane scrollPane;

    public JButton homeButton, paymentsButton, ordersButton, profileButton, logoutButton;
    private JLabel logoLabel;

    private List<OrderRow> orderRows = new ArrayList<>();

    public CustomerDeliveryTrackerView() {
        frame = new JFrame("Order Tracking");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1920, 1080);
        frame.setLayout(new BorderLayout());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        buildHeader();
        buildOrderList();

        frame.setVisible(true);
    }

    // ===========================================================
    // HEADER
    // ===========================================================
    private void buildHeader() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 100, 20, 100));
        headerPanel.setBackground(Color.WHITE);

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logoPanel.setBackground(Color.WHITE);

        ImageIcon rawLogo = new ImageIcon("design_images/koreanexpress-logo.png");
        Image scaledLogo = rawLogo.getImage().getScaledInstance(220, 70, Image.SCALE_SMOOTH);
        logoLabel = new JLabel(new ImageIcon(scaledLogo));
        logoPanel.add(logoLabel);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 50, 20));
        navPanel.setBackground(Color.WHITE);

        homeButton = makeNavButton("Home");
        paymentsButton = makeNavButton("Payments");
        ordersButton = makeNavButton("Orders");
        profileButton = makeNavButton("Profile");
        logoutButton = makeNavButton("Log Out");

        navPanel.add(homeButton);
        navPanel.add(paymentsButton);
        navPanel.add(ordersButton);
        navPanel.add(profileButton);
        navPanel.add(logoutButton);

        headerPanel.add(logoPanel, BorderLayout.WEST);
        headerPanel.add(navPanel, BorderLayout.EAST);

        frame.add(headerPanel, BorderLayout.NORTH);
    }

    private JButton makeNavButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 20));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setForeground(new Color(230, 0, 0));
        return b;
    }

    // ===========================================================
    // ORDER TABLE
    // ===========================================================
    private void buildOrderList() {
        ordersContainer = new JPanel();
        ordersContainer.setLayout(new BoxLayout(ordersContainer, BoxLayout.Y_AXIS));
        ordersContainer.setBackground(Color.WHITE);

        // Create header with 5 columns matching the row layout
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        header.setPreferredSize(new Dimension(1200, 50));
        header.setMaximumSize(new Dimension(1200, 50));

        // COLUMN 1: Timer (empty header)
        JPanel timerHeaderPanel = new JPanel(new BorderLayout());
        timerHeaderPanel.setPreferredSize(new Dimension(150, 50));
        timerHeaderPanel.setBackground(Color.WHITE);
        timerHeaderPanel.add(new JLabel(""), BorderLayout.CENTER);

        // COLUMN 2: Order Created (centered over date column)
        JPanel dateHeaderPanel = new JPanel(new BorderLayout());
        dateHeaderPanel.setPreferredSize(new Dimension(200, 50)); // Match date column width
        dateHeaderPanel.setBackground(Color.WHITE);
        JLabel dateHeader = makeHeaderLabel("Order Created");
        dateHeaderPanel.add(dateHeader, BorderLayout.CENTER);

        // COLUMN 3: Status (centered over status column)
        JPanel statusHeaderPanel = new JPanel(new BorderLayout());
        statusHeaderPanel.setPreferredSize(new Dimension(150, 50));
        statusHeaderPanel.setBackground(Color.WHITE);
        JLabel statusHeader = makeHeaderLabel("Status");
        statusHeaderPanel.add(statusHeader, BorderLayout.CENTER);

        // COLUMN 4: Price (centered over price column)
        JPanel priceHeaderPanel = new JPanel(new BorderLayout());
        priceHeaderPanel.setPreferredSize(new Dimension(120, 50));
        priceHeaderPanel.setBackground(Color.WHITE);
        JLabel priceHeader = makeHeaderLabel("Price");
        priceHeaderPanel.add(priceHeader, BorderLayout.CENTER);

        // COLUMN 5: Action (empty header)
        JPanel actionHeaderPanel = new JPanel(new BorderLayout());
        actionHeaderPanel.setPreferredSize(new Dimension(170, 50)); // Increased for larger button
        actionHeaderPanel.setBackground(Color.WHITE);
        actionHeaderPanel.add(new JLabel(""), BorderLayout.CENTER);

        // Add all header columns with the same spacing as rows
        header.add(timerHeaderPanel);
        header.add(Box.createHorizontalStrut(140)); // spacing
        header.add(dateHeaderPanel);
        header.add(Box.createHorizontalStrut(130)); // spacing  
        header.add(statusHeaderPanel);
        header.add(Box.createHorizontalStrut(130)); // spacing
        header.add(priceHeaderPanel);
        header.add(Box.createHorizontalStrut(140)); // spacing
        header.add(actionHeaderPanel);

        ordersContainer.add(header);
        ordersContainer.add(new JSeparator());

        scrollPane = new JScrollPane(ordersContainer);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        frame.add(scrollPane, BorderLayout.CENTER);
    }

    private JLabel makeHeaderLabel(String text) {
        JLabel lbl = new JLabel(text, JLabel.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        lbl.setForeground(Color.RED);
        return lbl;
    }

    // ===========================================================
    // ADD ROW (CONTROLLER USES THIS)
    // ===========================================================
    public OrderRow addOrderRow() {
        OrderRow row = new OrderRow();
        orderRows.add(row);
        ordersContainer.add(row);
        ordersContainer.add(new JSeparator());
        ordersContainer.revalidate();
        ordersContainer.repaint();
        return row;
    }

    // NEW METHOD: Clear all order rows
    public void clearOrderRows() {
        for (OrderRow row : orderRows) {
            ordersContainer.remove(row);
        }
        orderRows.clear();
        
        // Remove all separators
        Component[] components = ordersContainer.getComponents();
        for (Component comp : components) {
            if (comp instanceof JSeparator) {
                ordersContainer.remove(comp);
            }
        }
        
        ordersContainer.revalidate();
        ordersContainer.repaint();
    }

    public JFrame getFrame() { 
        return frame; 
    }
    
    public List<OrderRow> getOrderRows() { 
        return orderRows; 
    }

    // ===========================================================
    // ORDER ROW - 5 COLUMNS LAYOUT
    // ===========================================================
    public static class OrderRow extends JPanel {

        public JLabel timeLabel;
        public JLabel dateLabel;
        public JLabel statusLabel;
        public JLabel priceLabel;
        public JButton actionButton;

        public OrderRow() {
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
            setPreferredSize(new Dimension(1200, 80));
            setMaximumSize(new Dimension(1200, 80));

            // COLUMN 1: Timer (fixed width)
            JPanel timerPanel = new JPanel(new BorderLayout());
            timerPanel.setPreferredSize(new Dimension(150, 80));
            timerPanel.setBackground(Color.WHITE);
            timeLabel = new JLabel("00:00:00", JLabel.CENTER);
            timeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            timeLabel.setForeground(new Color(100, 0, 0));
            timerPanel.add(timeLabel, BorderLayout.CENTER);

            // COLUMN 2: Order Date (fixed width for better alignment)
            JPanel datePanel = new JPanel(new BorderLayout());
            datePanel.setPreferredSize(new Dimension(200, 80)); // Fixed width for better alignment
            datePanel.setBackground(Color.WHITE);
            dateLabel = new JLabel("YYYY-MM-DD 12:00 PM", JLabel.CENTER);
            dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            datePanel.add(dateLabel, BorderLayout.CENTER);

            // COLUMN 3: Status (fixed width - aligned with "Status" header)
            JPanel statusPanel = new JPanel(new BorderLayout());
            statusPanel.setPreferredSize(new Dimension(150, 80));
            statusPanel.setBackground(Color.WHITE);
            statusLabel = new JLabel("PREPARING", JLabel.CENTER);
            statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            statusPanel.add(statusLabel, BorderLayout.CENTER);

            // COLUMN 4: Price (fixed width - aligned with "Price" header)
            JPanel pricePanel = new JPanel(new BorderLayout());
            pricePanel.setPreferredSize(new Dimension(120, 80));
            pricePanel.setBackground(Color.WHITE);
            priceLabel = new JLabel("₱0.00", JLabel.CENTER);
            priceLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            pricePanel.add(priceLabel, BorderLayout.CENTER);

            // COLUMN 5: Action Button (increased width by 30)
            JPanel actionPanel = new JPanel(new BorderLayout());
            actionPanel.setPreferredSize(new Dimension(170, 80)); // Increased for larger button
            actionPanel.setBackground(Color.WHITE);
            actionButton = new JButton("Cancel Order");
            actionButton.setFont(new Font("SansSerif", Font.BOLD, 12));
            actionButton.setPreferredSize(new Dimension(120, 35)); // Increased from 90 to 120
            actionButton.setFocusPainted(false);
            actionPanel.add(actionButton, BorderLayout.CENTER);

            // Add all columns with spacing (same as header)
            add(timerPanel);
            add(Box.createHorizontalStrut(150)); // spacing
            add(datePanel);
            add(Box.createHorizontalStrut(150)); // spacing  
            add(statusPanel);
            add(Box.createHorizontalStrut(140)); // spacing
            add(pricePanel);
            add(Box.createHorizontalStrut(140)); // spacing
            add(actionPanel);
        }
    }
}