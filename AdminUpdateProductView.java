import javax.swing.*;
import java.awt.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Designs the Admin Update Products in line with its controller.
 */
public class AdminUpdateProductView {
    private JFrame frame;
    private JPanel headerPanel, formPanel, footerPanel;
    private JButton logoutButton, profileButton, updateProductButton, backButton;
    private JTextField nameField, priceField, prepTimeField;
    private JTextArea descriptionArea;
    private JLabel logoLabel, warningLabel, charCountLabel;

    /**
     * Constructor for AdminUpdateProductView class.
     */
    public AdminUpdateProductView(){
        frame = new JFrame("Update Product");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700); // Increased height to accommodate larger fields
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        // ================= HEADER ==================
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        headerPanel.setBackground(Color.WHITE);

        // Logo
        ImageIcon rawLogo = new ImageIcon("design_images/koreanexpress-logo.png");
        Image scaledLogo = rawLogo.getImage().getScaledInstance(250, 75, Image.SCALE_SMOOTH);
        logoLabel = new JLabel(new ImageIcon(scaledLogo));

        // Navigation buttons
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        navPanel.setBackground(Color.WHITE);
        profileButton = makeNavButton("Profile");
        logoutButton = makeNavButton("Log Out");
        navPanel.add(profileButton);
        navPanel.add(logoutButton);

        headerPanel.add(logoLabel, BorderLayout.WEST);
        headerPanel.add(navPanel, BorderLayout.EAST);
        frame.add(headerPanel, BorderLayout.NORTH);

        // ================= FORM ==================
        formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 80, 30, 80)); // Reduced side padding
        formPanel.setBackground(Color.WHITE);

        // Title - Smaller font and wider container
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("UPDATE PRODUCT");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24)); // Smaller font
        titleLabel.setForeground(new Color(230, 0, 0));
        titlePanel.add(titleLabel);
        titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titlePanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Warning label
        warningLabel = new JLabel("", SwingConstants.CENTER);
        warningLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        warningLabel.setForeground(Color.RED);
        warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(warningLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Name field - taller
        formPanel.add(createFormField("Product Name:", nameField = new JTextField(), 45));
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Price field - taller
        formPanel.add(createFormField("Price (₱):", priceField = new JTextField(), 45));
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Description field - much taller with character limit
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setBackground(Color.WHITE);
        descPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        descLabel.setPreferredSize(new Dimension(120, 30));
        
        // Create description area with character limit
        descriptionArea = new JTextArea(6, 40); // More rows
        descriptionArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        
        // Add character limit of 200
        ((AbstractDocument) descriptionArea.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
                    throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                int newLength = currentText.length() - length + text.length();
                if (newLength <= 200) {
                    super.replace(fb, offset, length, text, attrs);
                    updateCharCount();
                }
            }

            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attrs) 
                    throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                int newLength = currentText.length() + text.length();
                if (newLength <= 200) {
                    super.insertString(fb, offset, text, attrs);
                    updateCharCount();
                }
            }

            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                super.remove(fb, offset, length);
                updateCharCount();
            }
        });

        // Create scroll pane with proper styling
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setPreferredSize(new Dimension(500, 150)); // Much taller
        descScroll.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        // Character count label
        charCountLabel = new JLabel("0/200 characters");
        charCountLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        charCountLabel.setForeground(Color.GRAY);
        charCountLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel descContentPanel = new JPanel(new BorderLayout());
        descContentPanel.setBackground(Color.WHITE);
        descContentPanel.add(descScroll, BorderLayout.CENTER);
        descContentPanel.add(charCountLabel, BorderLayout.SOUTH);
        
        descPanel.add(descLabel, BorderLayout.WEST);
        descPanel.add(descContentPanel, BorderLayout.CENTER);
        formPanel.add(descPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Preparation time field - taller
        formPanel.add(createFormField("Preparation Time (HH:MM:SS):", prepTimeField = new JTextField(), 45));
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        frame.add(formPanel, BorderLayout.CENTER);

        // ================= FOOTER ==================
        footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        footerPanel.setBackground(Color.WHITE);

        backButton = createActionButton("BACK", new Color(150, 150, 150));
        updateProductButton = createActionButton("UPDATE PRODUCT", new Color(230, 0, 0));

        footerPanel.add(backButton);
        footerPanel.add(updateProductButton);

        frame.add(footerPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    /**
     * Updates the character count label
     */
    private void updateCharCount() {
        int length = descriptionArea.getText().length();
        charCountLabel.setText(length + "/200 characters");
        if (length > 180) {
            charCountLabel.setForeground(Color.RED);
        } else {
            charCountLabel.setForeground(Color.GRAY);
        }
    }

    /**
     * Creates a styled navigation button
     */
    private JButton makeNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setForeground(new Color(230, 0, 0));
        return button;
    }

    /**
     * Creates a form field with label and input
     */
    private JPanel createFormField(String labelText, JTextField textField, int height) {
        JPanel fieldPanel = new JPanel(new BorderLayout(15, 0));
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setPreferredSize(new Dimension(200, height));

        textField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        textField.setPreferredSize(new Dimension(500, height)); // Wider and taller
        textField.setMinimumSize(new Dimension(500, height));
        textField.setMaximumSize(new Dimension(500, height));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        fieldPanel.add(label, BorderLayout.WEST);
        fieldPanel.add(textField, BorderLayout.CENTER);

        return fieldPanel;
    }

    /**
     * Creates an action button with consistent styling
     */
    private JButton createActionButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(200, 45));
        return button;
    }

    public boolean validateInputs(){
        if (getProductName().trim().isEmpty() ||
            getDescription().trim().isEmpty() ||
            getPrice().trim().isEmpty() ||
            getPrepTime().trim().isEmpty()){

            warningLabel.setText("Please fill in all fields before updating the product.");
            return false;
        }

        if (!priceField.getText().matches("\\d+(\\.\\d{1,2})?")) {
            warningLabel.setText("Price must be a valid number (ex: 99.99).");
            return false;
        }

        if (!prepTimeField.getText().matches("^\\d{2}:\\d{2}:\\d{2}$")) {
            warningLabel.setText("Preparation time must be in HH:MM:SS format (ex: 00:15:00).");
            return false;
        }
        warningLabel.setText("");
        return true;
    }

    public JFrame getFrame(){
        return frame;
    }

    public JButton getLogoutButton(){
        return logoutButton;
    }

    public JButton getProfileButton(){
        return profileButton;
    }

    public JButton getUpdateProductButton(){
        return updateProductButton;
    }

    public JButton getBackButton(){
        return backButton;
    }

    public String getProductName(){
        return nameField.getText();
    }

    public String getDescription(){
        return descriptionArea.getText();
    }

    public String getPrice(){
        return priceField.getText();
    }

    public String getPrepTime(){
        return prepTimeField.getText();
    }

    public JLabel getWarningLabel(){
        return warningLabel;
    }

    public JTextField getProductField(){
        return nameField;
    }

    public JTextField getPriceField(){
        return priceField;
    }

    public JTextArea getDescriptionField(){
        return descriptionArea;
    }

    public JTextField getPreparationField(){
        return prepTimeField;
    }
}