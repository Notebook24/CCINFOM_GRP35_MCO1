import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Designs the Admin Add Products Page.
 */
public class AdminAddProductView {
    private JFrame frame;
    private JPanel headerPanel, formPanel, footerPanel;
    private JButton addProductButton, backButton, uploadImageButton;
    private JTextField nameField, priceField, prepTimeField;
    private JTextArea descriptionArea;
    private JLabel logoLabel, warningLabel, imagePreviewLabel, charCountLabel;
    private String savedImagePath = "";

    /**
     * Constructor for AdminAddProductView class.
     */
    public AdminAddProductView(){
        frame = new JFrame("Add Product");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 650); // Reduced height
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        // ================= HEADER ==================
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        headerPanel.setBackground(Color.WHITE);

        // Logo
        ImageIcon rawLogo = new ImageIcon("design_images/koreanexpress-logo.png");
        Image scaledLogo = rawLogo.getImage().getScaledInstance(200, 60, Image.SCALE_SMOOTH);
        logoLabel = new JLabel(new ImageIcon(scaledLogo));

        headerPanel.add(logoLabel, BorderLayout.WEST);
        frame.add(headerPanel, BorderLayout.NORTH);

        // ================= FORM ==================
        formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 60, 15, 60)); // Reduced padding
        formPanel.setBackground(Color.WHITE);

        // Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("ADD NEW PRODUCT");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(230, 0, 0));
        titlePanel.add(titleLabel);
        titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titlePanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Warning label
        warningLabel = new JLabel("", SwingConstants.CENTER);
        warningLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        warningLabel.setForeground(Color.RED);
        warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(warningLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Name field
        formPanel.add(createFormField("Product Name:", nameField = new JTextField(), 35));
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Price field
        formPanel.add(createFormField("Price (₱):", priceField = new JTextField(), 35));
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Description field
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setBackground(Color.WHITE);
        descPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        descLabel.setPreferredSize(new Dimension(100, 25));
        
        // Create description area with character limit
        descriptionArea = new JTextArea(3, 35); // Fewer rows
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
        descScroll.setPreferredSize(new Dimension(450, 80)); // Smaller height
        descScroll.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Character count label
        charCountLabel = new JLabel("0/200 characters");
        charCountLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        charCountLabel.setForeground(Color.GRAY);
        charCountLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel descContentPanel = new JPanel(new BorderLayout());
        descContentPanel.setBackground(Color.WHITE);
        descContentPanel.add(descScroll, BorderLayout.CENTER);
        descContentPanel.add(charCountLabel, BorderLayout.SOUTH);
        
        descPanel.add(descLabel, BorderLayout.WEST);
        descPanel.add(descContentPanel, BorderLayout.CENTER);
        formPanel.add(descPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Preparation time field
        formPanel.add(createFormField("Preparation Time (HH:MM:SS):", prepTimeField = new JTextField(), 35));
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Image upload section - COMPACT VERSION
        JPanel imagePanel = new JPanel(new BorderLayout(10, 0));
        imagePanel.setBackground(Color.WHITE);
        imagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel imageLabel = new JLabel("Product Image:");
        imageLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        imageLabel.setPreferredSize(new Dimension(120, 25)); // Smaller label
        
        JPanel imageRightPanel = new JPanel(new BorderLayout(10, 0));
        imageRightPanel.setBackground(Color.WHITE);
        
        // COMPACT upload button
        uploadImageButton = new JButton("UPLOAD IMAGE");
        uploadImageButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        uploadImageButton.setBackground(new Color(0, 100, 200));
        uploadImageButton.setForeground(Color.WHITE);
        uploadImageButton.setFocusPainted(false);
        uploadImageButton.setBorderPainted(false);
        uploadImageButton.setOpaque(true);
        uploadImageButton.setPreferredSize(new Dimension(140, 28)); // Much smaller height
        uploadImageButton.setMinimumSize(new Dimension(140, 28));
        uploadImageButton.setMaximumSize(new Dimension(140, 28));
        
        // COMPACT image preview area
        imagePreviewLabel = new JLabel("No image", SwingConstants.CENTER);
        imagePreviewLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        imagePreviewLabel.setForeground(Color.GRAY);
        imagePreviewLabel.setPreferredSize(new Dimension(120, 80)); // Much smaller height
        imagePreviewLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        imagePreviewLabel.setOpaque(true);
        imagePreviewLabel.setBackground(Color.WHITE);
        
        imageRightPanel.add(uploadImageButton, BorderLayout.WEST);
        imageRightPanel.add(imagePreviewLabel, BorderLayout.CENTER);
        
        imagePanel.add(imageLabel, BorderLayout.WEST);
        imagePanel.add(imageRightPanel, BorderLayout.CENTER);
        formPanel.add(imagePanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Add scroll pane to form panel to handle overflow
        JScrollPane formScrollPane = new JScrollPane(formPanel);
        formScrollPane.setBorder(BorderFactory.createEmptyBorder());
        formScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        frame.add(formScrollPane, BorderLayout.CENTER);

        // ================= FOOTER ==================
        footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        footerPanel.setBackground(Color.WHITE);

        backButton = createActionButton("BACK", new Color(150, 150, 150));
        addProductButton = createActionButton("ADD PRODUCT", new Color(230, 0, 0));

        footerPanel.add(backButton);
        footerPanel.add(addProductButton);

        frame.add(footerPanel, BorderLayout.SOUTH);

        // Action for image upload
        uploadImageButton.addActionListener(e -> chooseAndSaveImage());

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
     * Creates a form field with label and input
     */
    private JPanel createFormField(String labelText, JTextField textField, int height) {
        JPanel fieldPanel = new JPanel(new BorderLayout(10, 0));
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setPreferredSize(new Dimension(180, height));

        textField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        textField.setPreferredSize(new Dimension(400, height));
        textField.setMinimumSize(new Dimension(400, height));
        textField.setMaximumSize(new Dimension(400, height));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
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
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(160, 35)); // Smaller footer buttons
        return button;
    }

    private void chooseAndSaveImage(){
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Product Image");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int result = chooser.showOpenDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();

            try {
                File folder = new File("product_images");
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                String fileName = selectedFile.getName();
                Path destinationPath = Paths.get("product_images", fileName);
                Files.copy(selectedFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

                savedImagePath = "product_images/" + fileName;

                ImageIcon icon = new ImageIcon(new ImageIcon(savedImagePath)
                        .getImage().getScaledInstance(110, 70, Image.SCALE_SMOOTH)); // Smaller preview
                imagePreviewLabel.setIcon(icon);
                imagePreviewLabel.setText("");

                JOptionPane.showMessageDialog(frame, "Image uploaded successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error uploading image: " + ex.getMessage());
            }
        }
    }

    public boolean validateInputs(){
        if (getProductName().trim().isEmpty() ||
            getDescription().trim().isEmpty() ||
            getPrice().trim().isEmpty() ||
            getPrepTime().trim().isEmpty()){

            warningLabel.setText("Please fill in all fields before adding a product.");
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

    public JButton getAddProductButton(){
        return addProductButton;
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

    public String getSavedImagePath() {
        return savedImagePath;
    }

    public void clearForm() {
        nameField.setText("");
        descriptionArea.setText("");
        priceField.setText("");
        prepTimeField.setText("");
        imagePreviewLabel.setIcon(null);
        imagePreviewLabel.setText("No image selected");
        savedImagePath = "";
        updateCharCount(); // Reset character count
    }
}