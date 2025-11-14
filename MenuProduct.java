public class MenuProduct {
    private int id;
    private String name, description, prepTime;
    private double price;
    private boolean available;
    private String imagePath;

    // 6-arg constructor (used by controller) — imagePath defaults to empty
    public MenuProduct(int id, String name, String description, double price, String prepTime, boolean available) {
        this(id, name, description, price, prepTime, available, "");
    }

    // 7-arg constructor (keep if you later fetch image from DB)
    public MenuProduct(int id, String name, String description, double price, String prepTime, boolean available, String imagePath) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.prepTime = prepTime;
        this.available = available;
        this.imagePath = imagePath;
    }

    public int getId(){ return id; }
    public String getName(){ return name; }
    public String getDescription(){ return description; }
    public double getPrice(){ return price; }
    public String getPrepTime(){ return prepTime; }
    public boolean isAvailable(){ return available; }
    public void setAvailable(boolean available){ this.available = available; }

    public String getImagePath() {
        if (this.imagePath == null) return "";
        String p = this.imagePath.trim();
        if (p.isEmpty()) return "";
        p = p.replace("\\", "/");
        if (p.startsWith("./")) p = p.substring(2);
        while (p.startsWith("/")) p = p.substring(1);
        return p;
    }

    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}
