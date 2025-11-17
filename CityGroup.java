// CityGroup.java
public class CityGroup {
    private int id;
    private double deliveryFee;
    private int deliveryTimeMinutes;
    
    public CityGroup(int id, double deliveryFee, int deliveryTimeMinutes) {
        this.id = id;
        this.deliveryFee = deliveryFee;
        this.deliveryTimeMinutes = deliveryTimeMinutes;
    }
    
    // Getters and setters
    public int getId(){ 
        return id; 
    }
    public double getDeliveryFee(){ 
        return deliveryFee; 
    }
    public int getDeliveryTime(){ 
        return deliveryTimeMinutes; 
    }
    
    public void setDeliveryFee(double fee){ 
        this.deliveryFee = fee; 
    }
    public void setDeliveryTime(int time){ 
        this.deliveryTimeMinutes = time; 
    }
    
    @Override
    public String toString(){
        return String.format("Group %d - Fee: ₱%.2f - Delivery Time: %d minutes", 
                           id, deliveryFee, deliveryTimeMinutes);
    }
}