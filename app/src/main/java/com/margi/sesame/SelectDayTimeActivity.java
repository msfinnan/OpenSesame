package com.margi.sesame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import util.AppController;

public class SelectDayTimeActivity extends AppCompatActivity {
    private Button displayDate;
    private Button displayTime;
    private Button goToLocations;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;
    private AppController appController = AppController.getInstance();

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_day_time);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle("See What's Open Later");
            supportActionBar.show();
        }

        displayDate = findViewById(R.id.select_day_time_text);
        displayTime = findViewById(R.id.select_time_text);
        goToLocations = findViewById(R.id.select_day_time_go_to_locations);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        final Intent intent = new Intent(SelectDayTimeActivity.this, LocationListActivity.class);

        displayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(SelectDayTimeActivity.this,
                        android.R.style.Theme_Material_Dialog,
                        dateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            //BE CAREFUL, month starts with 0
                LocalDate localDate = new LocalDate( year, (month + 1), day);
                String dayOfWeek = localDate.property(DateTimeFieldType.dayOfWeek()).getAsText();
                displayDate.setText( dayOfWeek + ", " + (month + 1) + "/" + day + "/" + year);

                //todo set year month and day to AppCOntroller for calendar export
                //save day of week to global singleton AppController to access later in RecyclerAdapter
                appController.setFutureDay(dayOfWeek);
                appController.setFutureYear(year);
                appController.setFutureMonth(month); //may need to add to one
                appController.setFutureDayOfMonth(day);

            }
        };

        displayTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hourOfDay = 0;
                int minute = 0 ;




                TimePickerDialog dialog = new TimePickerDialog(SelectDayTimeActivity.this,
                        android.R.style.Theme_Material,
                        timeSetListener, hourOfDay, minute, false); //last variable is bool for is24HourView
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                LocalTime timeToDisplay = new LocalTime(hour, min);
//                displayTime.setText(hour + ":" + min);
                displayTime.setText(timeToDisplay.toString("h:mm a"));
                goToLocations.setVisibility(View.VISIBLE);

                //save hour and min to global singleton AppController to access later in RecyclerAdapter
                LocalTime localTime = new LocalTime(hour, min);
                appController.setFutureHourMin(localTime);

                appController.setFutureHour(hour);
                appController.setFutureMin(min);
            }
        };

        goToLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_sign_out :
                if (user != null && firebaseAuth != null){
                    firebaseAuth.signOut();

                    startActivity(new Intent(SelectDayTimeActivity.this,
                            MainActivity.class));

//                    finish(); //come back to this.
                }
                break;
            case R.id.open_now :
                startActivity(new Intent(SelectDayTimeActivity.this,
                        LocationListActivity.class));
                break;
            case R.id.open_later :
                startActivity(new Intent(SelectDayTimeActivity.this,
                        SelectDayTimeActivity.class));
                break;
            case R.id.add_location_from_menu :
                startActivity(new Intent(SelectDayTimeActivity.this,
                        AddLocationActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);

    }
}
