package model;

public class Location {
    private String locationName;
    private String groupName;
    private String userId;
    //what else do I need? An array of locations? or do I add that later

    public Location() { } //must have empty constructor for firestore for work

    public Location(String locationName, String groupName, String userId) {
        this.locationName = locationName;
        this.groupName = groupName;
        this.userId = userId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
