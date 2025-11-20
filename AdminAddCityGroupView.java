// AdminAddCityGroupView.java
import javax.swing.*;
import java.awt.*;

public class AdminAddCityGroupView {
    private JFrame frame;
    private JTextField feeField;
    private JTextField timeField;
    private JButton addButton;
    private JButton cancelButton;

    public AdminAddCityGroupView() {
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Add City Group");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Add New City Group");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(230, 0, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel feeLabel = new JLabel("Delivery Fee (₱):");
        feeLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        feeField = new JTextField();
        feeField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        
        JLabel timeLabel = new JLabel("Delivery Time (minutes):");
        timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        timeField = new JTextField();
        timeField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        
        formPanel.add(feeLabel);
        formPanel.add(feeField);
        formPanel.add(timeLabel);
        formPanel.add(timeField);
        formPanel.add(new JLabel()); // Empty cell
        formPanel.add(new JLabel()); // Empty cell

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        addButton = new JButton("Add Group");
        cancelButton = new JButton("Cancel");
        
        JButton[] buttons = {addButton, cancelButton};
        for (JButton btn : buttons) {
            btn.setFont(new Font("SansSerif", Font.BOLD, 14));
            btn.setBackground(new Color(255, 180, 180));
            btn.setForeground(Color.BLACK);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        }
        
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
    }

    public void clearFields() {
        feeField.setText("");
        timeField.setText("");
    }

    public void showSuccessMessage() {
        JOptionPane.showMessageDialog(frame, "City group successfully added!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Getters
    public JFrame getFrame() {
        return frame;
    }

    public JButton getAddButton() {
        return addButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public String getFee() {
        return feeField.getText().trim();
    }

    public String getTime() {
        return timeField.getText().trim();
    }
}