package com.margi.sesame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import util.AppController;

public class LandingPageActivity extends AppCompatActivity {
    private Button openNowButton;
    private Button openLaterButton;
    private AppController appController = AppController.getInstance();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        openNowButton = findViewById(R.id.landing_page_open_now);
        openLaterButton = findViewById(R.id.landing_page_open_later);

        openNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set requested day and time to null in AppController
                appController.setFutureHourMin(null);
                appController.setFutureDay(null);

                Intent intent = new Intent(LandingPageActivity.this,
                        LocationListActivity.class);
                startActivity(intent);
            }
        });

        openLaterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LandingPageActivity.this, SelectDayTimeActivity.class);
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

                    startActivity(new Intent(LandingPageActivity.this,
                            MainActivity.class));

//                    finish(); //come back to this.
                }
                break;
            case R.id.open_now :
                startActivity(new Intent(LandingPageActivity.this,
                        LocationListActivity.class));
                break;
            case R.id.open_later :
                startActivity(new Intent(LandingPageActivity.this,
                        SelectDayTimeActivity.class));
                break;
            case R.id.add_location_from_menu :
                startActivity(new Intent(LandingPageActivity.this,
                        AddLocationActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);

    }
}
