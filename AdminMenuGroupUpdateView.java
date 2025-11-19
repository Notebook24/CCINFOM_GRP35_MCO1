// AdminMenuGroupUpdateView.java
import javax.swing.*;
import java.awt.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class AdminMenuGroupUpdateView {
    private JFrame frame;
    private JTextField nameField;
    private JTextField timeStartField;
    private JTextField timeEndField;
    private JCheckBox availableCheckBox;
    private JButton updateButton;
    private JButton cancelButton;
    private JLabel titleLabel;

    public AdminMenuGroupUpdateView() {
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Update Menu Category");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Title
        titleLabel = new JLabel("Update Menu Category", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(230, 0, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 15, 15));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Category Name (Required) - Limited to 50 characters
        JLabel nameLabel = new JLabel("Category Name *:");
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        nameField = new JTextField();
        nameField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        ((AbstractDocument) nameField.getDocument()).setDocumentFilter(new LengthFilter(50));
        
        // Time Start - Required
        JLabel timeStartLabel = new JLabel("Time Start * (HH:MM:SS):");
        timeStartLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        timeStartField = new JTextField();
        timeStartField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        timeStartField.setToolTipText("Format: HH:MM:SS (24-hour format) - Required");
        ((AbstractDocument) timeStartField.getDocument()).setDocumentFilter(new LengthFilter(8));
        
        // Time End - Required
        JLabel timeEndLabel = new JLabel("Time End * (HH:MM:SS):");
        timeEndLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        timeEndField = new JTextField();
        timeEndField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        timeEndField.setToolTipText("Format: HH:MM:SS (24-hour format) - Required");
        ((AbstractDocument) timeEndField.getDocument()).setDocumentFilter(new LengthFilter(8));
        
        // Availability
        JLabel availableLabel = new JLabel("Available:");
        availableLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        availableCheckBox = new JCheckBox("Yes");
        availableCheckBox.setBackground(Color.WHITE);
        
        // Instructions
        JLabel instructionLabel = new JLabel("<html><i>* Required fields. Time Start must be before Time End.</i></html>");
        instructionLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        instructionLabel.setForeground(Color.GRAY);
        
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(timeStartLabel);
        formPanel.add(timeStartField);
        formPanel.add(timeEndLabel);
        formPanel.add(timeEndField);
        formPanel.add(availableLabel);
        formPanel.add(availableCheckBox);
        formPanel.add(new JLabel()); // Empty cell
        formPanel.add(instructionLabel);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        updateButton = new JButton("Update Category");
        cancelButton = new JButton("Cancel");
        
        JButton[] buttons = {updateButton, cancelButton};
        for (JButton btn : buttons) {
            btn.setFont(new Font("SansSerif", Font.BOLD, 14));
            btn.setBackground(new Color(255, 180, 180));
            btn.setForeground(Color.BLACK);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        }
        
        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
    }

    public void setCategoryData(MenuCategory category) {
        titleLabel.setText("Update Category " + category.getCategoryId());
        nameField.setText(category.getCategoryName());
        timeStartField.setText(category.getTimeStart().toString());
        timeEndField.setText(category.getTimeEnd().toString());
        availableCheckBox.setSelected(category.isAvailable());
    }

    public void showSuccessMessage() {
        JOptionPane.showMessageDialog(frame, "Menu category successfully updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Getters
    public JFrame getFrame() {
        return frame;
    }

    public JButton getUpdateButton() {
        return updateButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public String getCategoryName() {
        return nameField.getText().trim();
    }

    public String getTimeStart() {
        return timeStartField.getText().trim();
    }

    public String getTimeEnd() {
        return timeEndField.getText().trim();
    }

    public boolean isAvailable() {
        return availableCheckBox.isSelected();
    }
    
    // Document filter to limit input length
    private class LengthFilter extends DocumentFilter {
        private int maxLength;
        
        public LengthFilter(int maxLength) {
            this.maxLength = maxLength;
        }
        
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
                throws BadLocationException {
            int currentLength = fb.getDocument().getLength();
            int overLimit = (currentLength - length) + text.length() - maxLength;
            
            if (overLimit > 0) {
                text = text.substring(0, text.length() - overLimit);
            }
            
            if (text.length() > 0) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
        
        @Override
        public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) 
                throws BadLocationException {
            int currentLength = fb.getDocument().getLength();
            if (currentLength + text.length() <= maxLength) {
                super.insertString(fb, offset, text, attr);
            }
        }
    }
}