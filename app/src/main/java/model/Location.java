package model;

public class Location {
    private String locationName;
    private String groupName;
    private String userId;
    private String locationId;
//    private Boolean isOpen;

    public Location() { } //must have empty constructor for firestore for work

    public Location(String locationName, String groupName, String userId, String locationId) {
        this.locationName = locationName;
        this.groupName = groupName;
        this.userId = userId;
        this.locationId = locationId;
//        this.isOpen = isOpen;
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

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

//    public Boolean getOpen() {
//        return isOpen;
//    }
//
//    public void setOpen(Boolean open) {
//        isOpen = open;
//    }
}
