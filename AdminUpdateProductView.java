import javax.swing.*;
import java.awt.*;

/**
 * Designs the Admin Update Products in line with its controller.
 */
public class AdminUpdateProductView {
    private JFrame frame;
    private JPanel headerPanel, formPanel, footerPanel;
    private JButton logoutButton, settingsButton, updateProductButton, backButton;
    private JTextField nameField, priceField, prepTimeField;
    private JTextArea descriptionArea;
    private JLabel logoLabel, warningLabel;

    /**
     * Constructor for AdminUpdateProductView class.
     */
    public AdminUpdateProductView(){
        frame = new JFrame("Update Products");
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

        footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        updateProductButton = new JButton("Update Product");
        backButton = new JButton("Back");

        footerPanel.add(updateProductButton, BorderLayout.WEST);
        footerPanel.add(backButton, BorderLayout.EAST);

        frame.add(footerPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
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
