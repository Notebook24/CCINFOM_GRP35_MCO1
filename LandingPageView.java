import javax.swing.*;
import java.awt.*;

/**
 * Designs the Landing Page in line with its controller.
 */
public class LandingPageView {
    private JFrame frame;
    private JPanel headerPanel;
    private JPanel bodyPanel;
    private JButton loginAsCustomerButton, loginAsAdminButton, signUpAsCustomer, signUpAsAdmin;
    private JPanel footerPanel;

    /**
     * Constructor for LandingPageView class.
     */
    public LandingPageView() {
        frame = new JFrame("Landing Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel logoLabel = new JLabel("Logo of Incorporation");
        loginAsCustomerButton = new JButton("Login as Customer");
        loginAsAdminButton = new JButton("Login as Admin");

        JPanel rightHeaderPanel = new JPanel();
        rightHeaderPanel.add(loginAsCustomerButton);
        rightHeaderPanel.add(loginAsAdminButton);

        headerPanel.add(logoLabel, BorderLayout.WEST);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);

        bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));

        JLabel signInAsLabel = new JLabel("Sign up as:");
        signInAsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        signUpAsCustomer = new JButton("Sign up as Customer");
        signUpAsAdmin = new JButton("Sign up as Admin");

        signUpAsCustomer.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpAsAdmin.setAlignmentX(Component.CENTER_ALIGNMENT);

        bodyPanel.add(signInAsLabel);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        bodyPanel.add(signUpAsCustomer);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        bodyPanel.add(signUpAsAdmin);

        footerPanel = new JPanel();
        JLabel footerLabel = new JLabel("© 2025 My Restaurant App");
        footerPanel.add(footerLabel);

        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(bodyPanel, BorderLayout.CENTER);
        frame.add(footerPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public JButton getLoginAsCustomerButton(){
        return loginAsCustomerButton;
    }

    public JButton getLoginAsAdminButton(){
        return loginAsAdminButton;
    }

    public JButton getSignUpAsCustomer(){
        return signUpAsCustomer;
    }

    public JButton getSignUpAsAdmin(){
        return signUpAsAdmin;
    }

    public JFrame getFrame(){
        return frame;
    }
}
