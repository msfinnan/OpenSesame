package util;

import android.app.Application;
import android.content.Context;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
//JournalApi in Paulo's tutorial
//this global class will hold user id and username
//it is a singleton
//added this to manifest (it is a singleton that can be used anywhere in the application)


public class AppController extends Application {
    private String username;
    private String userId;
    private static AppController instance; //singleton

    //getInstance method of type AppController
    public static AppController getInstance() {
        if (instance == null)
            instance = new AppController();

        return instance;
    }

    public AppController(){} //empty constructor


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
