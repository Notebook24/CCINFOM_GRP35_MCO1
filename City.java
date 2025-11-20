public class City {
    private int id;
    private String name;
    private int groupId;
    private boolean isAvailable;
    
    public City(int id, String name, int groupId, boolean isAvailable) {
        this.id = id;
        this.name = name;
        this.groupId = groupId;
        this.isAvailable = isAvailable;
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getGroupId() {
        return groupId;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }
    
    @Override
    public String toString() {
        String status = isAvailable ? "Available" : "Unavailable";
        return name + " - " + status;
    }
}