public class MenuProduct {
    private int id;
    private String name, description, prepTime;
    private double price;
    private boolean available;

    public MenuProduct(int id, String name, String description, double price, String prepTime, boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.prepTime = prepTime;
        this.available = available;
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    public double getPrice(){
        return price;
    }

    public String getPrepTime(){
        return prepTime;
    }

    public boolean isAvailable(){
        return available;
    }

    public void setAvailable(boolean available){
        this.available = available;
    }
}
