package util;

import android.app.Application;
//JournalApi in Paulo's tutorial
//this global class will hold user id and username
//it is a singleton
//added this to manifest (it is a singleton that can be used anywhere in the application)

public class UserInfo extends Application {
    private String username;
    private String userId;
    private static UserInfo instance; //singleton

    //getInstance method of type UserInfo
    public static UserInfo getInstance() {
        if (instance == null)
            instance = new UserInfo();

        return instance;
    }

    public UserInfo(){} //empty constructor


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
