import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Designs the Admin Add/Delete Products Page.
 */
public class AdminAddProductView {
   private JFrame frame;
    private JPanel headerPanel, formPanel, footerPanel;
    private JButton logoutButton, settingsButton, addProductButton, backButton, uploadImageButton;
    private JTextField nameField, priceField, prepTimeField;
    private JTextArea descriptionArea;
    private JLabel logoLabel, warningLabel, imagePreviewLabel;
    private String savedImagePath = "";

    /**
     * Constructor for AdminAddProductsView class.
     */
    public AdminAddProductView(){
        frame = new JFrame("Add Products");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        logoLabel = new JLabel("Logo of Restaurant");
        logoutButton = new JButton("Log out");
        settingsButton = new JButton("Settings");

        JPanel rightHeaderPanel = new JPanel();
        rightHeaderPanel.add(logoutButton);
        rightHeaderPanel.add(settingsButton);

        headerPanel.add(logoLabel, BorderLayout.WEST);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);
        frame.add(headerPanel, BorderLayout.NORTH);

        formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        warningLabel = new JLabel("", SwingConstants.CENTER);
        warningLabel.setForeground(Color.RED);
        warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(warningLabel);

        JPanel namePanel = new JPanel(new BorderLayout(10, 0));
        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField();
        namePanel.add(nameLabel, BorderLayout.WEST);
        namePanel.add(nameField, BorderLayout.CENTER);
        formPanel.add(namePanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel pricePanel = new JPanel(new BorderLayout(10, 0));
        JLabel priceLabel = new JLabel("Price:");
        priceField = new JTextField();
        pricePanel.add(priceLabel, BorderLayout.WEST);
        pricePanel.add(priceField, BorderLayout.CENTER);
        formPanel.add(pricePanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel descPanel = new JPanel(new BorderLayout(10, 0));
        JLabel descLabel = new JLabel("Description:");
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descPanel.add(descLabel, BorderLayout.WEST);
        descPanel.add(descScroll, BorderLayout.CENTER);
        formPanel.add(descPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel prepPanel = new JPanel(new BorderLayout(10, 0));
        JLabel prepLabel = new JLabel("Prep Time (HH:MM:SS):");
        prepTimeField = new JTextField();
        prepPanel.add(prepLabel, BorderLayout.WEST);
        prepPanel.add(prepTimeField, BorderLayout.CENTER);
        formPanel.add(prepPanel);

        frame.add(formPanel, BorderLayout.CENTER);

        JPanel imagePanel = new JPanel(new BorderLayout(10, 0));
        JLabel imageLabel = new JLabel("Product Image:");
        uploadImageButton = new JButton("Upload Image");
        imagePreviewLabel = new JLabel();
        imagePreviewLabel.setPreferredSize(new Dimension(150, 100));
        imagePanel.add(imageLabel, BorderLayout.WEST);
        imagePanel.add(uploadImageButton, BorderLayout.CENTER);
        imagePanel.add(imagePreviewLabel, BorderLayout.EAST);
        formPanel.add(imagePanel);

        // Action for image upload
        uploadImageButton.addActionListener(e -> chooseAndSaveImage());

        footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        addProductButton = new JButton("Add Product");
        backButton = new JButton("Back");

        footerPanel.add(addProductButton, BorderLayout.WEST);
        footerPanel.add(backButton, BorderLayout.EAST);

        frame.add(footerPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
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
                        .getImage().getScaledInstance(100, 80, Image.SCALE_SMOOTH));
                imagePreviewLabel.setIcon(icon);

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

    public JButton getLogoutButton(){
        return logoutButton;
    }

    public JButton getSettingsButton(){
        return settingsButton;
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
        savedImagePath = "";
    }
}
