package model;

public class Collection {
    private String collectionName;
    private String userId;
    //what else do I need? An array of locations? or do I add that later

    public Collection() { } //must have empty constructor for firestore for work

    public Collection(String collectionName, String userId) {
        this.collectionName = collectionName;
        this.userId = userId;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
