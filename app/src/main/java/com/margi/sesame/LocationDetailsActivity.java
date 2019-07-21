package com.margi.sesame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.protobuf.StringValue;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import model.Location;

public class LocationDetailsActivity extends AppCompatActivity {


    private static final int PERMISSION_CODE = 1 ;
    TextView locationNameTextView;
    TextView locationAddressTextView;
    ImageView locationImageView;
    TextView sundayHoursTextView;
    TextView mondayHoursTextView;
    TextView tuesdayHoursTextView;
    TextView wednesdayHoursTextView;
    TextView thursdayHoursTextView;
    TextView fridayHoursTextView;
    TextView saturdayHoursTextView;
    TextView phoneNumberTextView;
    TextView websiteTextView;

    private PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);

        //get google places id from intent
        Intent intent = getIntent();
        String placeId = intent.getStringExtra("locationId");

        locationNameTextView = findViewById(R.id.location_details_name_textview);

        locationAddressTextView = findViewById(R.id.location_address);

        sundayHoursTextView = findViewById(R.id.opening_hours_sunday);
        mondayHoursTextView = findViewById(R.id.opening_hours_monday);
        tuesdayHoursTextView = findViewById(R.id.opening_hours_tuesday);
        wednesdayHoursTextView = findViewById(R.id.opening_hours_wednesday);
        thursdayHoursTextView = findViewById(R.id.opening_hours_thursday);
        fridayHoursTextView = findViewById(R.id.opening_hours_friday);
        saturdayHoursTextView = findViewById(R.id.opening_hours_saturday);

        locationImageView = findViewById(R.id.location_image);

        websiteTextView = findViewById(R.id.web_address);

        phoneNumberTextView = findViewById(R.id.phone_number);


        //get places details from google place sdk

        // Initialize the SDK
        String apiKey = BuildConfig.ApiKey;
        Places.initialize(this.getApplicationContext(), apiKey);

        // Create a new Places client instance
        placesClient = Places.createClient(this);

        // Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.OPENING_HOURS, Place.Field.UTC_OFFSET, Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI, Place.Field.PHOTO_METADATAS, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS);

// Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                final Place place = fetchPlaceResponse.getPlace();

                locationNameTextView.setText(place.getName());

                locationAddressTextView.setText(place.getAddress());

                String sundayHours = place.getOpeningHours().getWeekdayText().get(6);
                sundayHoursTextView.setText(sundayHours);

                String mondayHours = place.getOpeningHours().getWeekdayText().get(0);
                mondayHoursTextView.setText(mondayHours);

                String tuesdayHours = place.getOpeningHours().getWeekdayText().get(1);
                tuesdayHoursTextView.setText(tuesdayHours);

                String wednesdayHours = place.getOpeningHours().getWeekdayText().get(2);
                wednesdayHoursTextView.setText(wednesdayHours);

                String thursdayHours = place.getOpeningHours().getWeekdayText().get(3);
                thursdayHoursTextView.setText(thursdayHours);

                String fridayHours = place.getOpeningHours().getWeekdayText().get(4);
                fridayHoursTextView.setText(fridayHours);

                String saturdayHours = place.getOpeningHours().getWeekdayText().get(5);
                saturdayHoursTextView.setText(saturdayHours);

                if (place.getWebsiteUri() != null) {
                    String website = place.getWebsiteUri().toString();
                    String strippedWebsite = website.replace("http://", "").replace("http:// www.", "").replace("www.", "").replaceFirst("/*$", "");
                    websiteTextView.setText(strippedWebsite);
                    websiteTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Uri uriUrl = place.getWebsiteUri();
                            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                            startActivity(launchBrowser);
                        }
                    });
                }

                if (place.getPhoneNumber() != null) {
                    final String phoneNumber = place.getPhoneNumber();
                    phoneNumberTextView.setText(phoneNumber);
                    phoneNumberTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent launchPhone = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                            if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(LocationDetailsActivity.this,
                                        new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CODE );
                                return;
                                //todo add catch for if user does not accept calling permissions
                            }
                            startActivity(launchPhone);
                        }
                    });
                }


                // Get the photo metadata.
                PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);

                // Get the attribution text.
                String attributions = photoMetadata.getAttributions();

                // Create a FetchPhotoRequest.
                FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                        .setMaxWidth(1000) // Optional.
                        .setMaxHeight(800) // Optional.
                        .build();
                placesClient.fetchPhoto(photoRequest).addOnSuccessListener(new OnSuccessListener<FetchPhotoResponse>() {
                    @Override
                    public void onSuccess(FetchPhotoResponse fetchPhotoResponse) {
                        Bitmap bitmap = fetchPhotoResponse.getBitmap();
                        locationImageView.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            Log.d("LocationDetailsActivity", "onFailure: " + e.getMessage());

                    }
                }

            });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("LocationDetailsActivity", "onFailure: Place not found" + e.getMessage());
            }
        });



    }
}
