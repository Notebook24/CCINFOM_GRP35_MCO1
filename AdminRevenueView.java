import javax.swing.*;
import java.awt.*;

/**
 * Displays the Admin Revenue Page of the Incorporation App.
 */
public class AdminRevenueView {
    private JFrame frame;
    private JPanel headerPanel, bodyPanel, footerPanel;
    private JButton logoutButton, settingsButton;
    private JLabel logoLabel;
    private JLabel product2Label, product3Label, product4Label;
    private JLabel totalProfitLabel, profitYearLabel, profitMonthLabel, profitTodayLabel;

    /**
     * Constructor for AdminRevenueView class.
     */
    public AdminRevenueView() {
        // Frame setup
        frame = new JFrame("Admin Revenue Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        // ===== HEADER =====
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        logoLabel = new JLabel("Logo of Incorporation");
        logoutButton = new JButton("Log out");
        settingsButton = new JButton("Settings");

        JPanel rightHeaderPanel = new JPanel();
        rightHeaderPanel.add(logoutButton);
        rightHeaderPanel.add(settingsButton);

        headerPanel.add(logoLabel, BorderLayout.WEST);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);
        frame.add(headerPanel, BorderLayout.NORTH);

        // ===== BODY =====
        bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        // Product revenue lines
        product2Label = new JLabel("#2");
        JLabel product3LabelRight = new JLabel("Earned total: XXX PHP");
        JLabel product4LabelRight = new JLabel("Earned total: XXX PHP");

        // Use panels for side-by-side layout of products and earnings
        JPanel row1 = new JPanel(new BorderLayout());
        row1.add(new JLabel("#1"), BorderLayout.WEST);

        JPanel row2 = new JPanel(new BorderLayout());
        row2.add(new JLabel("#2"), BorderLayout.WEST);

        JPanel row3 = new JPanel(new BorderLayout());
        row3.add(new JLabel("#3"), BorderLayout.WEST);
        row3.add(product3LabelRight, BorderLayout.EAST);

        JPanel row4 = new JPanel(new BorderLayout());
        row4.add(new JLabel("#4"), BorderLayout.WEST);
        row4.add(product4LabelRight, BorderLayout.EAST);

        bodyPanel.add(row1);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        bodyPanel.add(row2);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        bodyPanel.add(row3);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        bodyPanel.add(row4);

        frame.add(bodyPanel, BorderLayout.CENTER);

        // ===== FOOTER / PROFIT SUMMARY =====
        footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));

        totalProfitLabel = new JLabel("Total profit: XXX PHP");
        profitYearLabel = new JLabel("Total profit this year: XXX PHP");
        profitMonthLabel = new JLabel("Total profit this month: XXX PHP");
        profitTodayLabel = new JLabel("Total profit today: XXX PHP");

        footerPanel.add(totalProfitLabel);
        footerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        footerPanel.add(profitYearLabel);
        footerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        footerPanel.add(profitMonthLabel);
        footerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        footerPanel.add(profitTodayLabel);

        frame.add(footerPanel, BorderLayout.SOUTH);

        // ===== SHOW FRAME =====
        frame.setVisible(true);
    }

    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminRevenueView::new);
    }
}
