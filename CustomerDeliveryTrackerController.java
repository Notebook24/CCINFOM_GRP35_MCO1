public class CustomerDeliveryTrackerController {
 
    private CustomerDeliveryTrackerView view;
    private int customerId;

    public CustomerDeliveryTrackerController(CustomerDeliveryTrackerView view, int customerId) {
        this.view = view;
        this.customerId = customerId;
    }
}
