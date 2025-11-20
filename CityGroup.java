public class CityGroup {
    private int id;
    private double deliveryFee;
    private int deliveryTimeMinutes;
    private boolean isAvailable;
    
    public CityGroup(int id, double deliveryFee, int deliveryTimeMinutes, boolean isAvailable) {
        this.id = id;
        this.deliveryFee = deliveryFee;
        this.deliveryTimeMinutes = deliveryTimeMinutes;
        this.isAvailable = isAvailable;
    }
    
    // Getters and setters
    public int getId() { 
        return id; 
    }
    
    public double getDeliveryFee() { 
        return deliveryFee; 
    }
    
    public int getDeliveryTime() { 
        return deliveryTimeMinutes; 
    }
    
    public boolean isAvailable() {
        return isAvailable;
    }
    
    public void setDeliveryFee(double fee) { 
        this.deliveryFee = fee; 
    }
    
    public void setDeliveryTime(int time) { 
        this.deliveryTimeMinutes = time; 
    }
    
    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }
    
    @Override
    public String toString() {
        String status = isAvailable ? "Available" : "Unavailable";
        return String.format("Group %d - Fee: ₱%.2f - Delivery Time: %d minutes - %s", 
                           id, deliveryFee, deliveryTimeMinutes, status);
    }
}