package com.margi.sesame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.LocalDate;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SelectDayTimeActivity extends AppCompatActivity {
    private TextView displayDate;
    private TextView displayTime;
    private Button goToLocations;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_day_time);

        displayDate = findViewById(R.id.select_day_time_text);
        displayTime = findViewById(R.id.select_time_text);
        goToLocations = findViewById(R.id.select_day_time_go_to_locations);

        displayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(SelectDayTimeActivity.this,
                        android.R.style.Theme_DeviceDefault_Dialog,
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

            }
        };

        displayTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hourOfDay = 0;
                int minute = 0 ;

                TimePickerDialog dialog = new TimePickerDialog(SelectDayTimeActivity.this,
                        android.R.style.Theme_DeviceDefault_Dialog,
                        timeSetListener, hourOfDay, minute, true); //last variable is bool for is24HourView
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                displayTime.setText(hour + ":" + min);
                goToLocations.setVisibility(View.VISIBLE);


            }
        };
    }

}
