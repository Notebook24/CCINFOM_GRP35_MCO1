import javax.swing.*;
import java.awt.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.io.File;

/**
 * Designs the Admin Update Products in line with its controller.
 */
public class AdminUpdateProductView {
    private JFrame frame;
    private JPanel headerPanel, formPanel, footerPanel;
    private JButton updateProductButton, backButton, browseImageButton;
    private JTextField nameField, priceField, prepTimeField, imagePathField;
    private JTextArea descriptionArea;
    private JLabel logoLabel, warningLabel, charCountLabel, imagePreviewLabel;
    private JComboBox<String> categoryComboBox;

    /**
     * Constructor for AdminUpdateProductView class.
     */
    public AdminUpdateProductView(){
        frame = new JFrame("Update Product");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 650); // Reduced height
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        // ================= HEADER ==================
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40)); // Reduced padding
        headerPanel.setBackground(Color.WHITE);

        // Logo only - removed navigation buttons
        ImageIcon rawLogo = new ImageIcon("design_images/koreanexpress-logo.png");
        Image scaledLogo = rawLogo.getImage().getScaledInstance(200, 60, Image.SCALE_SMOOTH); // Smaller logo
        logoLabel = new JLabel(new ImageIcon(scaledLogo));

        headerPanel.add(logoLabel, BorderLayout.WEST);
        frame.add(headerPanel, BorderLayout.NORTH);

        // ================= FORM ==================
        formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80)); // Reduced padding
        formPanel.setBackground(Color.WHITE);

        // Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("UPDATE PRODUCT");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22)); // Smaller font
        titleLabel.setForeground(new Color(230, 0, 0));
        titlePanel.add(titleLabel);
        titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titlePanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Reduced space

        // Warning label
        warningLabel = new JLabel("", SwingConstants.CENTER);
        warningLabel.setFont(new Font("SansSerif", Font.BOLD, 12)); // Smaller font
        warningLabel.setForeground(Color.RED);
        warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(warningLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Reduced space

        // Name field
        formPanel.add(createFormField("Product Name:", nameField = new JTextField(), 40)); // Smaller height
        formPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Reduced space

        // Category dropdown
        formPanel.add(createCategoryField());
        formPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Reduced space

        // Price field
        formPanel.add(createFormField("Price (₱):", priceField = new JTextField(), 40)); // Smaller height
        formPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Reduced space

        // Description field - smaller
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setBackground(Color.WHITE);
        descPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        descLabel.setPreferredSize(new Dimension(120, 25)); // Smaller
        
        descriptionArea = new JTextArea(4, 40); // Fewer rows
        descriptionArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        
        // Character limit
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

        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setPreferredSize(new Dimension(500, 100)); // Smaller
        descScroll.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(6, 6, 6, 6) // Reduced padding
        ));

        charCountLabel = new JLabel("0/200 characters");
        charCountLabel.setFont(new Font("SansSerif", Font.PLAIN, 11)); // Smaller
        charCountLabel.setForeground(Color.GRAY);
        charCountLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel descContentPanel = new JPanel(new BorderLayout());
        descContentPanel.setBackground(Color.WHITE);
        descContentPanel.add(descScroll, BorderLayout.CENTER);
        descContentPanel.add(charCountLabel, BorderLayout.SOUTH);
        
        descPanel.add(descLabel, BorderLayout.WEST);
        descPanel.add(descContentPanel, BorderLayout.CENTER);
        formPanel.add(descPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Reduced space

        // Preparation time field
        formPanel.add(createFormField("Preparation Time (HH:MM:SS):", prepTimeField = new JTextField(), 40)); // Smaller height
        formPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Reduced space

        // Image upload field - more compact
        formPanel.add(createImageUploadField());
        formPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Reduced space

        frame.add(formPanel, BorderLayout.CENTER);

        // ================= FOOTER ==================
        footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15)); // Reduced padding
        footerPanel.setBackground(Color.WHITE);

        backButton = createActionButton("BACK", new Color(150, 150, 150));
        updateProductButton = createActionButton("UPDATE PRODUCT", new Color(230, 0, 0));

        footerPanel.add(backButton);
        footerPanel.add(updateProductButton);

        frame.add(footerPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    /**
     * Creates category dropdown field
     */
    private JPanel createCategoryField() {
        JPanel fieldPanel = new JPanel(new BorderLayout(10, 0)); // Reduced gap
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel("Category:");
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setPreferredSize(new Dimension(200, 35)); // Smaller

        categoryComboBox = new JComboBox<>();
        categoryComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        categoryComboBox.setPreferredSize(new Dimension(500, 35)); // Smaller
        categoryComboBox.setMinimumSize(new Dimension(500, 35));
        categoryComboBox.setMaximumSize(new Dimension(500, 35));

        fieldPanel.add(label, BorderLayout.WEST);
        fieldPanel.add(categoryComboBox, BorderLayout.CENTER);

        return fieldPanel;
    }

    /**
     * Creates image upload field with preview
     */
    private JPanel createImageUploadField() {
        JPanel imagePanel = new JPanel(new BorderLayout(10, 5)); // Reduced gaps
        imagePanel.setBackground(Color.WHITE);
        imagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel("Product Image:");
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setPreferredSize(new Dimension(200, 25)); // Smaller

        // Image path field and browse button
        JPanel pathPanel = new JPanel(new BorderLayout(5, 0)); // Reduced gap
        pathPanel.setBackground(Color.WHITE);
        
        imagePathField = new JTextField();
        imagePathField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        imagePathField.setPreferredSize(new Dimension(350, 30)); // Smaller
        imagePathField.setEditable(false);
        
        browseImageButton = new JButton("Browse");
        browseImageButton.setFont(new Font("SansSerif", Font.BOLD, 11)); // Smaller
        browseImageButton.setPreferredSize(new Dimension(70, 30)); // Smaller
        
        pathPanel.add(imagePathField, BorderLayout.CENTER);
        pathPanel.add(browseImageButton, BorderLayout.EAST);

        // Image preview - smaller
        imagePreviewLabel = new JLabel();
        imagePreviewLabel.setPreferredSize(new Dimension(100, 100)); // Smaller preview
        imagePreviewLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        imagePreviewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagePreviewLabel.setText("No Image");
        imagePreviewLabel.setForeground(Color.GRAY);
        imagePreviewLabel.setFont(new Font("SansSerif", Font.PLAIN, 11)); // Smaller

        JPanel previewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        previewPanel.setBackground(Color.WHITE);
        previewPanel.add(imagePreviewLabel);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(pathPanel, BorderLayout.NORTH);
        contentPanel.add(previewPanel, BorderLayout.CENTER);

        imagePanel.add(label, BorderLayout.WEST);
        imagePanel.add(contentPanel, BorderLayout.CENTER);

        return imagePanel;
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
     * Updates the image preview
     */
    public void updateImagePreview(String imagePath) {
        if (imagePath != null && !imagePath.trim().isEmpty()) {
            try {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    ImageIcon originalIcon = new ImageIcon(imagePath);
                    Image scaledImage = originalIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH); // Smaller
                    imagePreviewLabel.setIcon(new ImageIcon(scaledImage));
                    imagePreviewLabel.setText("");
                } else {
                    imagePreviewLabel.setIcon(null);
                    imagePreviewLabel.setText("Image Not Found");
                }
            } catch (Exception e) {
                imagePreviewLabel.setIcon(null);
                imagePreviewLabel.setText("Invalid Image");
            }
        } else {
            imagePreviewLabel.setIcon(null);
            imagePreviewLabel.setText("No Image");
        }
    }

    /**
     * Creates a form field with label and input
     */
    private JPanel createFormField(String labelText, JTextField textField, int height) {
        JPanel fieldPanel = new JPanel(new BorderLayout(10, 0)); // Reduced gap
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setPreferredSize(new Dimension(200, height));

        textField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        textField.setPreferredSize(new Dimension(500, height));
        textField.setMinimumSize(new Dimension(500, height));
        textField.setMaximumSize(new Dimension(500, height));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(6, 8, 6, 8) // Reduced padding
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
        button.setFont(new Font("SansSerif", Font.BOLD, 14)); // Smaller
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(180, 40)); // Smaller
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

        if (getSelectedCategoryId() == -1) {
            warningLabel.setText("Please select a category.");
            return false;
        }

        warningLabel.setText("");
        return true;
    }

    // Getters
    public JFrame getFrame(){
        return frame;
    }

    public JButton getUpdateProductButton(){
        return updateProductButton;
    }

    public JButton getBackButton(){
        return backButton;
    }

    public JButton getBrowseImageButton(){
        return browseImageButton;
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

    public String getImagePath(){
        return imagePathField.getText();
    }

    public void setImagePath(String path){
        imagePathField.setText(path);
        updateImagePreview(path);
    }

    public int getSelectedCategoryId(){
        Object selectedItem = categoryComboBox.getSelectedItem();
        if (selectedItem != null) {
            String item = selectedItem.toString();
            try {
                return Integer.parseInt(item.split(" - ")[0]);
            } catch (Exception e) {
                return -1;
            }
        }
        return -1;
    }

    public void setSelectedCategoryId(int categoryId){
        for (int i = 0; i < categoryComboBox.getItemCount(); i++) {
            String item = categoryComboBox.getItemAt(i);
            try {
                int itemId = Integer.parseInt(item.split(" - ")[0]);
                if (itemId == categoryId) {
                    categoryComboBox.setSelectedIndex(i);
                    return;
                }
            } catch (Exception e) {
                // Continue searching
            }
        }
    }

    public JComboBox<String> getCategoryComboBox(){
        return categoryComboBox;
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