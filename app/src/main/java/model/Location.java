package model;

public class Location {
    private String locationName;
    private String userId;

    public Location() {
    }

    public Location(String locationName, String userId) {
        this.locationName = locationName;
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
}
