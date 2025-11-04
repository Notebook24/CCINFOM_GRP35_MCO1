public class AppDriver {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LandingPageView landingView = new LandingPageView();
                new LandingPageController(landingView);
            }
        });
    }
}
