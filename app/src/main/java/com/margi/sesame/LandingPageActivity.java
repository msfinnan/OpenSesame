package com.margi.sesame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LandingPageActivity extends AppCompatActivity {
    private Button openNowButton;
    private Button openLaterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        openNowButton = findViewById(R.id.landing_page_open_now);
        openLaterButton = findViewById(R.id.landing_page_open_later);

        openNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
}
