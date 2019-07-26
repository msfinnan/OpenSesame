package util;

import android.app.Application;
import android.content.Context;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.joda.time.LocalTime;
//JournalApi in Paulo's tutorial
//this global class will hold user id and username
//it is a singleton
//added this to manifest (it is a singleton that can be used anywhere in the application)


public class AppController extends Application {
    private String username;
    private String userId;
    private String futureDay;
    private LocalTime futureHourMin;
    private int futureYear;
    private int futureMonth;
    private int futureDayOfMonth;
    private int futureHour;
    private int futureMin;

    private static AppController instance; //singleton

    //getInstance method of type AppController
    public static AppController getInstance() {
        if (instance == null)
            instance = new AppController();

        return instance;
    }

    public AppController(){} //empty constructor

    public String getFutureDay() {
        return futureDay;
    }

    public void setFutureDay(String futureDay) {
        this.futureDay = futureDay;
    }

    public LocalTime getFutureHourMin() {
        return futureHourMin;
    }

    public void setFutureHourMin(LocalTime futureHourMin) {
        this.futureHourMin = futureHourMin;
    }

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

    public int getFutureYear() {
        return futureYear;
    }

    public void setFutureYear(int futureYear) {
        this.futureYear = futureYear;
    }

    public int getFutureMonth() {
        return futureMonth;
    }

    public void setFutureMonth(int futureMonth) {
        this.futureMonth = futureMonth;
    }

    public int getFutureDayOfMonth() {
        return futureDayOfMonth;
    }

    public void setFutureDayOfMonth(int futureDayOfMonth) {
        this.futureDayOfMonth = futureDayOfMonth;
    }

    public int getFutureHour() {
        return futureHour;
    }

    public void setFutureHour(int futureHour) {
        this.futureHour = futureHour;
    }

    public int getFutureMin() {
        return futureMin;
    }

    public void setFutureMin(int futureMin) {
        this.futureMin = futureMin;
    }
}
