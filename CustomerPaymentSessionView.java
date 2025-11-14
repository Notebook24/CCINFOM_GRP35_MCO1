import javax.swing.*;
import java.awt.*;

/**
 * Customer Payment Session Page Layout in line with its controller
 */
public class CustomerPaymentSessionView {
    private JFrame frame;
    private JPanel headerPanel, sessionPanel, buttonPanel;
    private JButton payLaterButton, payNowButton;
    private JLabel logoLabel;
    private JLabel subtotalValueLabel, deliveryValueLabel, totalValueLabel;
    private JTextField paymentField;
    private JLabel deliveryDescriptionLabel;

    public CustomerPaymentSessionView() {
        frame = new JFrame("Payment Gateway");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1920, 1080);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

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

        // Right: "Payment Gateway"
        JLabel titleLabel = new JLabel("Payment Gateway");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 40));
        titleLabel.setForeground(new Color(230, 0, 0));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 20));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(titleLabel);

        headerPanel.add(logoPanel, BorderLayout.WEST);
        headerPanel.add(titlePanel, BorderLayout.EAST);

        frame.add(headerPanel, BorderLayout.NORTH);

        // ================= PAYMENT SESSION PANEL ==================
        sessionPanel = new JPanel();
        sessionPanel.setLayout(new BoxLayout(sessionPanel, BoxLayout.Y_AXIS));
        sessionPanel.setBorder(BorderFactory.createEmptyBorder(40, 300, 40, 300));
        sessionPanel.setBackground(Color.WHITE);

        // Subtotal row
        JPanel subtotalPanel = new JPanel(new BorderLayout());
        subtotalPanel.setBackground(Color.WHITE);

        JLabel subtotalLabel = new JLabel("Subtotal");
        subtotalLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        subtotalValueLabel = new JLabel("₱0.00", SwingConstants.RIGHT);
        subtotalValueLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));

        subtotalPanel.add(subtotalLabel, BorderLayout.WEST);
        subtotalPanel.add(subtotalValueLabel, BorderLayout.EAST);

        // Divider line
        JPanel divider1 = new JPanel();
        divider1.setBackground(new Color(200, 200, 200));
        divider1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));

        // Delivery row
        JPanel deliveryPanel = new JPanel(new BorderLayout());
        deliveryPanel.setBackground(Color.WHITE);

        deliveryDescriptionLabel = new JLabel("Delivery Price (Manila to Caloocan)");
        deliveryDescriptionLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        deliveryPanel.add(deliveryDescriptionLabel, BorderLayout.WEST);

        deliveryValueLabel = new JLabel("₱0.00", SwingConstants.RIGHT);
        deliveryValueLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));

        deliveryPanel.add(deliveryDescriptionLabel, BorderLayout.WEST);
        deliveryPanel.add(deliveryValueLabel, BorderLayout.EAST);

        // Divider line
        JPanel divider2 = new JPanel();
        divider2.setBackground(new Color(200, 200, 200));
        divider2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));

        // TOTAL
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(Color.WHITE);
        totalPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));

        JLabel totalLabel = new JLabel("TOTAL:");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        totalLabel.setForeground(new Color(230, 0, 0));

        totalValueLabel = new JLabel("₱0.00", SwingConstants.RIGHT);
        totalValueLabel.setFont(new Font("SansSerif", Font.BOLD, 28));

        totalPanel.add(totalLabel, BorderLayout.WEST);
        totalPanel.add(totalValueLabel, BorderLayout.EAST);

        // Enter Payment Amount Label
        JLabel enterLabel = new JLabel("ENTER YOUR PAYMENT BELOW");
        enterLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        enterLabel.setForeground(new Color(230, 0, 0));
        enterLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Input Field
        paymentField = new JTextField();
        paymentField.setPreferredSize(new Dimension(400, 40));
        paymentField.setMaximumSize(new Dimension(600, 40));
        paymentField.setBorder(BorderFactory.createLineBorder(new Color(200, 0, 0), 2));
        paymentField.setAlignmentX(Component.CENTER_ALIGNMENT);
        paymentField.setHorizontalAlignment(JTextField.CENTER);

        // Add components to session panel
        sessionPanel.add(subtotalPanel);
        sessionPanel.add(Box.createVerticalStrut(10));
        sessionPanel.add(divider1);
        sessionPanel.add(Box.createVerticalStrut(20));
        sessionPanel.add(deliveryPanel);
        sessionPanel.add(Box.createVerticalStrut(10));
        sessionPanel.add(divider2);
        sessionPanel.add(Box.createVerticalStrut(30));
        sessionPanel.add(totalPanel);
        sessionPanel.add(Box.createVerticalStrut(30));
        sessionPanel.add(enterLabel);
        sessionPanel.add(Box.createVerticalStrut(10));
        sessionPanel.add(paymentField);
        sessionPanel.add(Box.createVerticalStrut(40));

        frame.add(sessionPanel, BorderLayout.CENTER);

        // buttons
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 20));
        buttonPanel.setBackground(Color.WHITE);

        payLaterButton = new JButton("Pay Later");
        payNowButton = new JButton("Pay Now");

        JButton[] buttons = {payLaterButton, payNowButton};

        for (JButton btn : buttons) {
            btn.setFont(new Font("SansSerif", Font.BOLD, 20));
            btn.setBackground(new Color(255, 170, 170));
            btn.setForeground(Color.BLACK);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        }

        buttonPanel.add(payLaterButton);
        buttonPanel.add(payNowButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // ============= GETTERS =============
    public JButton getPayLaterButton() { 
        return payLaterButton; 
    }

    public JButton getPayNowButton() { 
        return payNowButton; 
    }

    public JLabel getSubtotalValueLabel() { 
        return subtotalValueLabel; 
    }

    public JLabel getDeliveryValueLabel() { 
        return deliveryValueLabel; 
    }

    public JLabel getTotalValueLabel() { 
        return totalValueLabel; 
    }

    public JTextField getPaymentField() { return paymentField; }
    public JFrame getFrame() { return frame; }
    public void setDeliveryLabelText(String text) {
        deliveryDescriptionLabel.setText(text);
    }

}
