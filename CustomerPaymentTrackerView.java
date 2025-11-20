import javax.swing.*;
import java.awt.*;

public class CustomerPaymentTrackerView {

    private JFrame frame;
    private JButton homeButton;
    private JButton paymentsButton;
    private JButton ordersButton;
    private JButton profileButton;
    private JButton logoutButton;
    protected JPanel rowsPanel;

    public CustomerPaymentTrackerView() {
        frame = new JFrame("Customer Payment Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1600, 900);
        frame.setLayout(new BorderLayout());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));

        // Logo
        ImageIcon rawLogo = new ImageIcon("design_images/koreanexpress-logo.png");
        Image scaledLogo = rawLogo.getImage().getScaledInstance(240, 72, Image.SCALE_SMOOTH);
        JLabel logo = new JLabel(new ImageIcon(scaledLogo));

        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(Color.WHITE);
        logoPanel.add(logo);

        // Nav Buttons
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 40, 15));
        nav.setBackground(Color.WHITE);

        homeButton = makeNavButton("Home");
        paymentsButton = makeNavButton("Payments");
        ordersButton = makeNavButton("Orders");
        profileButton = makeNavButton("Profile");
        logoutButton = makeNavButton("Log Out");

        nav.add(homeButton);
        nav.add(paymentsButton);
        nav.add(ordersButton);
        nav.add(profileButton);
        nav.add(logoutButton);

        header.add(logoPanel, BorderLayout.WEST);
        header.add(nav, BorderLayout.EAST);

        frame.add(header, BorderLayout.NORTH);

        // Center Panel
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Color.WHITE);
        center.setBorder(BorderFactory.createEmptyBorder(0, 130, 20, 130));

        // Table Header
        JPanel headerRow = new JPanel(new GridBagLayout());
        headerRow.setBackground(Color.WHITE);

        Font colFont = new Font("SansSerif", Font.BOLD, 20);
        Color red = new Color(230, 0, 0);

        addColumn(headerRow, "Order Created", 0, colFont, red, 220);
        addColumn(headerRow, "Payment Status", 1, colFont, red, 180);
        addColumn(headerRow, "Price", 2, colFont, red, 120);
        addColumn(headerRow, "Paid On", 3, colFont, red, 220);
        addColumn(headerRow, "", 4, colFont, red, 160);

        // Rows Panel
        rowsPanel = new JPanel();
        rowsPanel.setLayout(new BoxLayout(rowsPanel, BoxLayout.Y_AXIS));
        rowsPanel.setBackground(Color.WHITE);

        // === FIX: Put header + rows into the SAME scroll container ===
        JPanel tableContainer = new JPanel();
        tableContainer.setLayout(new BoxLayout(tableContainer, BoxLayout.Y_AXIS));
        tableContainer.setBackground(Color.WHITE);
        tableContainer.add(headerRow);
        tableContainer.add(rowsPanel);

        JScrollPane scrollPaneFixed = new JScrollPane(tableContainer);
        scrollPaneFixed.setBorder(BorderFactory.createEmptyBorder());
        scrollPaneFixed.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        center.add(scrollPaneFixed, BorderLayout.CENTER);
        frame.add(center, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    public JButton addPaymentRow(String date, String status, String price, String paidOn, boolean isPaid) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));

        Font font = new Font("SansSerif", Font.PLAIN, 18);

        addRowLabel(row, date, 0, 220, font, Color.BLACK);
        addRowLabel(row, status, 1, 180, new Font("SansSerif", Font.BOLD, 18),
                isPaid ? new Color(0, 180, 0) : new Color(230, 0, 0));
        addRowLabel(row, price, 2, 120, font, Color.BLACK);
        addRowLabel(row, paidOn, 3, 220, new Font("SansSerif", Font.BOLD, 18), Color.BLACK);

        JButton btn = new JButton(isPaid ? "View Receipt" : "Pay Now");
        btn.setPreferredSize(new Dimension(160, 45));
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBackground(isPaid ? new Color(140, 220, 140)
                                 : new Color(255, 170, 170));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.insets = new Insets(5, 10, 5, 10);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        row.add(btn, gbc);

        rowsPanel.add(row);
        rowsPanel.add(new JSeparator());

        rowsPanel.revalidate();
        rowsPanel.repaint();

        return btn;
    }

    // HELPERS
    private JButton makeNavButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 18));
        b.setForeground(new Color(230, 0, 0));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        return b;
    }

    private void addColumn(JPanel parent, String text, int col, Font font, Color color, int width) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(width, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = col;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 15, 5, 15);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        parent.add(label, gbc);
    }

    protected void addRowLabel(JPanel row, String text, int col, int width, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setPreferredSize(new Dimension(width, 30));
        label.setHorizontalAlignment(SwingConstants.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = col;
        gbc.insets = new Insets(5, 15, 5, 15);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        row.add(label, gbc);
    }

    // GETTERS
    public JFrame getFrame() { return frame; }
    public JButton getHomeButton() { return homeButton; }
    public JButton getPaymentsButton() { return paymentsButton; }
    public JButton getOrdersButton() { return ordersButton; }
    public JButton getProfileButton() { return profileButton; }
    public JButton getLogoutButton() { return logoutButton; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerPaymentTrackerView());
    }
}
