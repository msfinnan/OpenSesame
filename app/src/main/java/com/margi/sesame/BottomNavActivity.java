package com.margi.sesame;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.view.MenuItem;
import android.widget.TextView;

public class BottomNavActivity extends AppCompatActivity {
//    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
//                    mTextMessage.setText("Home");
                    Intent a = new Intent(BottomNavActivity.this, LandingPageActivity.class);
                    startActivity(a);
                case R.id.navigation_dashboard:
//                    mTextMessage.setText("Add Location");
                    Intent b = new Intent(BottomNavActivity.this, AddLocationActivity.class);
                    startActivity(b);
                case R.id.navigation_notifications:
//                    mTextMessage.setText("Logout");
                    return true;
                    //cannot sign out from all pages... think about what to do .
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);
        BottomNavigationView navView = findViewById(R.id.nav_view);
//        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
