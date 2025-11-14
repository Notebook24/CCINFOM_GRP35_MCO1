import javax.swing.*;
import java.awt.*;

/**
 * This class designs the Landing Page of Korean Express in line with its controller.
 */
public class LandingPageView {
    // GUI Components
    private JFrame frame;
    private JPanel headerPanel;
    private JPanel bodyPanel;
    private JButton loginAsCustomerButton, loginAsAdminButton, signUpAsCustomer, signUpAsAdmin;
    private JPanel footerPanel;

    /**
     * Constructor for LandingPageView class.
     */
    public LandingPageView() {
        frame = new JFrame("Welcome to Korean Express!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        // header panel
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        headerPanel.setBackground(Color.WHITE);

        // logo section
        JLabel logoLabel = new JLabel();
        ImageIcon logoIcon = new ImageIcon("design_images/koreanexpress-logo.png");
        Image scaledLogo = logoIcon.getImage().getScaledInstance(300, 150, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(scaledLogo));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT); 

        // tagline section
        JLabel taglineLabel = new JLabel("<html><b><i>Your Fast Lane to Korean Cuisine.</i></b></html>", SwingConstants.CENTER);
        taglineLabel.setFont(new Font("Helvetica Neue", Font.ITALIC, 18));
        taglineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // container for logo and tagline that will be added to the panel later on
        JPanel headerContainer = new JPanel();
        headerContainer.setLayout(new BoxLayout(headerContainer, BoxLayout.Y_AXIS));
        headerContainer.setBackground(Color.WHITE);
        headerContainer.add(Box.createVerticalStrut(20)); // vertical strut for spacing
        headerContainer.add(logoLabel);
        headerContainer.add(Box.createVerticalStrut(10));
        headerContainer.add(taglineLabel);
        headerContainer.add(Box.createVerticalStrut(10));

        headerPanel.add(headerContainer, BorderLayout.CENTER); // add container to header panel

        bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBackground(Color.WHITE);
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        loginAsCustomerButton = new JButton("Log In as User");
        loginAsAdminButton = new JButton("Log In as Admin");
        signUpAsCustomer = new JButton("Sign Up as User");
        signUpAsAdmin = new JButton("Sign Up as Admin");

        Dimension buttonSize = new Dimension(220, 45);

        // temporary array of JButtons to apply common styles
        JButton[] buttons = {loginAsCustomerButton, loginAsAdminButton, signUpAsCustomer, signUpAsAdmin};

        // set font text of the button as helvetica neue, bold, size 14
        for (JButton button : buttons) {
            button.setFont(new Font("Helvetica Neue", Font.BOLD, 14));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(buttonSize);
            button.setFocusPainted(false); // removes the focus border when clicked
        }

        loginAsCustomerButton.setBackground(new Color(255, 138, 128)); // red
        loginAsAdminButton.setBackground(new Color(255, 171, 145)); // orange
        signUpAsCustomer.setBackground(new Color(255, 138, 128)); // red
        signUpAsAdmin.setBackground(new Color(255, 171, 145)); // orange

        // assemble body panel
        bodyPanel.add(loginAsCustomerButton);
        bodyPanel.add(Box.createVerticalStrut(10));
        bodyPanel.add(loginAsAdminButton);
        bodyPanel.add(Box.createVerticalStrut(20));

        // horizontal line separator
        JSeparator separator = new JSeparator();
        separator.setForeground(Color.LIGHT_GRAY);
        bodyPanel.add(separator);
        bodyPanel.add(Box.createVerticalStrut(20));

        // new here sections
        JLabel newHereLabel = new JLabel("<html><b><i>New here?</i></b> Join the Korean craving wave!</html>"); // html used for direct bold and italic
        newHereLabel.setFont(new Font("Helvetica Neue", Font.PLAIN, 14));
        newHereLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        newHereLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bodyPanel.add(newHereLabel);
        bodyPanel.add(Box.createVerticalStrut(20));

        // assemble sign up buttons to bodyPanel
        bodyPanel.add(signUpAsCustomer);
        bodyPanel.add(Box.createVerticalStrut(10));
        bodyPanel.add(signUpAsAdmin);

        // footer section with the copyright information
        footerPanel = new JPanel();
        footerPanel.setBackground(Color.WHITE);
        JLabel footerLabel = new JLabel("2025 © Korean Express Inc.  All Rights Reserved.");
        footerLabel.setFont(new Font("Helvetica Neue", Font.PLAIN, 12));
        footerLabel.setForeground(Color.GRAY);
        footerPanel.add(footerLabel);

        // assemble all panels to the frame
        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(bodyPanel, BorderLayout.CENTER);
        frame.add(footerPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    /**
     * Getter for the Login as Customer button.
     * @return JButton for Login as Customer
     */
    public JButton getLoginAsCustomerButton(){
        return loginAsCustomerButton;
    }

    /**
     * Getter for the Login as Admin button.
     * @return JButton for Login as Admin
     */
    public JButton getLoginAsAdminButton(){
        return loginAsAdminButton;
    }

    /**
     * Getter for the Sign Up as Customer button.
     * @return JButton for Sign Up as Customer
     */
    public JButton getSignUpAsCustomer(){
        return signUpAsCustomer;
    }

    /**
     * Getter for the Sign Up as Admin button.
     * @return JButton for Sign Up as Admin
     */
    public JButton getSignUpAsAdmin(){
        return signUpAsAdmin;
    }

    /**
     * Getter for the main frame.
     * @return JFrame of the Landing Page
     */
    public JFrame getFrame(){
        return frame;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LandingPageView::new);
    }
}
