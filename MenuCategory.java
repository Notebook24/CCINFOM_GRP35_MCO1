// MenuCategory.java
import java.sql.Time;

public class MenuCategory {
    private int categoryId;
    private String categoryName;
    private Time timeStart;
    private Time timeEnd;
    private boolean isAvailable;
    
    public MenuCategory(int categoryId, String categoryName, Time timeStart, Time timeEnd, boolean isAvailable) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.isAvailable = isAvailable;
    }
    
    // Getters and setters
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Time getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Time timeStart) {
        this.timeStart = timeStart;
    }

    public Time getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Time timeEnd) {
        this.timeEnd = timeEnd;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }
    
    @Override
    public String toString() {
        String timeRange = " | Time: " + timeStart + " - " + timeEnd;
        return "Category ID: " + categoryId + " | Name: " + categoryName + 
               timeRange + " | Available: " + (isAvailable ? "Yes" : "No");
    }
}