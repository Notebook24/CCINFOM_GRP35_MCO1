import javax.swing.*;
import java.awt.*;

public class OrderStatusView extends JFrame {

    public OrderStatusView() {
        setTitle("Order Status");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main container
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        add(mainPanel);

        // Title
        JLabel title = new JLabel("Order Created", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.RED);
        mainPanel.add(title, BorderLayout.NORTH);

        // Order list container
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        mainPanel.add(listPanel, BorderLayout.CENTER);

        // Add 3 sample orders
        for (int i = 1; i <= 3; i++) {
            listPanel.add(createOrderRow("12:15:3" + i, "2025-02-18", "12:00PM"));
            listPanel.add(Box.createVerticalStrut(10));
        }
    }

    private JPanel createOrderRow(String time, String date, String formattedTime) {
        JPanel row = new JPanel();
        row.setLayout(new BorderLayout(10, 10));

        // Left: Time + Date panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(2, 1));

        JLabel timeLabel = new JLabel("Time: " + time);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        timeLabel.setForeground(Color.RED);

        JLabel dateLabel = new JLabel("Date: " + date + "   " + formattedTime);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        infoPanel.add(timeLabel);
        infoPanel.add(dateLabel);

        row.add(infoPanel, BorderLayout.WEST);

        // Right: Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        JButton receiptBtn = new JButton("View Receipt");
        receiptBtn.setBackground(new Color(0, 180, 0));  // Green
        receiptBtn.setForeground(Color.WHITE);
        receiptBtn.setFocusPainted(false);

        JButton cancelBtn = new JButton("Cancel Order");
        cancelBtn.setBackground(new Color(200, 0, 0));   // Red
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);

        buttonPanel.add(receiptBtn);
        buttonPanel.add(cancelBtn);

        row.add(buttonPanel, BorderLayout.EAST);

        // Divider line
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        return row;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OrderStatusView().setVisible(true));
    }
}
