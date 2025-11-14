import javax.swing.*;
import java.awt.*;

public class CustomerReceiptPageView {

    private JFrame frame;
    private JLabel referenceNumberLabel;
    private JButton homeButton, trackButton;
    private JPanel rowsPanel;

    public CustomerReceiptPageView() {

        frame = new JFrame("Receipt");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1920, 1080);
        frame.setLayout(new BorderLayout());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));
        headerPanel.setBackground(Color.WHITE);

        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(Color.WHITE);
        ImageIcon rawLogo = new ImageIcon("design_images/koreanexpress-logo.png");
        Image scaledLogo = rawLogo.getImage().getScaledInstance(350, 110, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        logoPanel.add(logoLabel, BorderLayout.WEST);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 30));
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Receipt");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 50));
        titleLabel.setForeground(new Color(230, 0, 0));

        referenceNumberLabel = new JLabel("REFERENCE NUMBER: ---");
        referenceNumberLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(15));
        titlePanel.add(referenceNumberLabel);

        headerPanel.add(logoPanel, BorderLayout.WEST);
        headerPanel.add(titlePanel, BorderLayout.EAST);

        frame.add(headerPanel, BorderLayout.NORTH);

        rowsPanel = new JPanel();
        rowsPanel.setLayout(new BoxLayout(rowsPanel, BoxLayout.Y_AXIS));
        rowsPanel.setBackground(Color.WHITE);
        rowsPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        JScrollPane scrollPane = new JScrollPane(rowsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 80, 0, 80));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(28, Integer.MAX_VALUE));
        scrollPane.getVerticalScrollBar().setUnitIncrement(25);
        scrollPane.getVerticalScrollBar().setBackground(Color.WHITE);

        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 40, 0));

        homeButton = new JButton("Home");
        trackButton = new JButton("Track Order");

        styleButton(homeButton);
        styleButton(trackButton);

        buttonPanel.add(homeButton);
        buttonPanel.add(Box.createHorizontalStrut(60));
        buttonPanel.add(trackButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void styleButton(JButton btn) {
        btn.setPreferredSize(new Dimension(200, 50));
        btn.setBackground(new Color(255, 150, 150));
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    public JFrame getFrame() {
        return frame;
    }

    public JButton getHomeButton() {
        return homeButton;
    }

    public JButton getTrackButton() {
        return trackButton;
    }

    public void setReferenceNumber(String ref) {
        referenceNumberLabel.setText("REFERENCE NUMBER:   " + ref);
    }

    public void addReceiptRow(String name, String value) {

        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);

        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));
        row.setPreferredSize(new Dimension(0, 85));
        row.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        boolean bold = name.equalsIgnoreCase("Total")
                || name.equalsIgnoreCase("Amount Paid")
                || name.equalsIgnoreCase("Change");

        JLabel left = new JLabel(name);
        left.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, 20));

        JLabel right = new JLabel(value);
        right.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, 20));
        right.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel rightWrapper = new JPanel(new BorderLayout());
        rightWrapper.setBackground(Color.WHITE);
        rightWrapper.add(right, BorderLayout.EAST);

        row.add(left, BorderLayout.WEST);
        row.add(rightWrapper, BorderLayout.CENTER);

        rowsPanel.add(row);
        addSeparator();
    }

    /* ===========================
       SEPARATOR
    ============================== */
    private void addSeparator() {
        JPanel sepContainer = new JPanel(new BorderLayout());
        sepContainer.setBackground(Color.WHITE);

        sepContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        sepContainer.setPreferredSize(new Dimension(0, 22));
        sepContainer.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(160, 160, 160));

        sepContainer.add(sep, BorderLayout.CENTER);
        rowsPanel.add(sepContainer);
    }
}
