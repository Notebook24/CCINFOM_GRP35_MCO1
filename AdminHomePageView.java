import javax.swing.*;
import java.awt.*;

/**
 * Designs the Admin Home Page in line with its constructor.
 */
public class AdminHomePageView {
    private JFrame frame;
    private JPanel headerPanel, bodyPanel, footerPanel;
    private JButton logoutButton, settingsButton;
    private JButton viewProductButton, addProductButton, checkProfitButton;
    private JButton checkEngagementButton, checkMenuStatusButton, checkOrderStatusButton;
    private JLabel logoLabel, footerLabel;

    /**
     * Constructor for AdminHomePageView class.
     */
    public AdminHomePageView() {
        frame = new JFrame("Admin Home Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        logoLabel = new JLabel("Logo of Restaurant");

        JPanel rightHeaderPanel = new JPanel();
        logoutButton = new JButton("Log out");
        settingsButton = new JButton("Settings");
        rightHeaderPanel.add(settingsButton);
        rightHeaderPanel.add(logoutButton);

        headerPanel.add(logoLabel, BorderLayout.WEST);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);
        frame.add(headerPanel, BorderLayout.NORTH);

        bodyPanel = new JPanel(new GridLayout(4, 2, 40, 20));
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(40, 100, 40, 100));

        viewProductButton = new JButton("VIEW PRODUCTS");
        addProductButton = new JButton("ADD PRODUCT");
        checkProfitButton = new JButton("CHECK PROFIT");

        checkEngagementButton = new JButton("CHECK CUSTOMER ENGAGEMENT");
        checkOrderStatusButton = new JButton("CHECK ORDER STATUS");
        checkMenuStatusButton = new JButton("CHECK MENU STATUS");

        bodyPanel.add(viewProductButton);
        bodyPanel.add(checkEngagementButton);
        bodyPanel.add(addProductButton);
        bodyPanel.add(checkOrderStatusButton);
        bodyPanel.add(checkMenuStatusButton);
        bodyPanel.add(checkProfitButton);
        bodyPanel.add(new JLabel(""));

        frame.add(bodyPanel, BorderLayout.CENTER);

        footerPanel = new JPanel();
        footerLabel = new JLabel("© 2025 My Restaurant App");
        footerPanel.add(footerLabel);
        frame.add(footerPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
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

    public JButton getViewProductButton(){
        return viewProductButton;
    }

    public JButton getAddProductButton(){
        return addProductButton;
    }

    public JButton getCheckProfitButton(){
        return checkProfitButton;
    }

    public JButton getCheckEngagementButton(){
        return checkEngagementButton;
    }

    public JButton getCheckMenuStatusButton(){
        return checkMenuStatusButton;
    }

    public JButton getCheckOrderStatusButton(){
        return checkOrderStatusButton;
    }
}
