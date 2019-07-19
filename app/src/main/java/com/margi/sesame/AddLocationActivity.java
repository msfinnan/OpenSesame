package com.margi.sesame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import model.Location;
import util.AppController;

public class AddLocationActivity extends AppCompatActivity {

    private HashSet<String> groupNames;
    private ArrayList<String> groupNamesArray;
    private PlacesClient placesClient;


    private static final String TAG = "AddLocationActivity";
    private Button saveButton;
    private ProgressBar progressBar;
//    private EditText locationNameEditText;
    private AutoCompleteTextView groupNameAutoComplete;
    private String locationName;
    private String locationId;

    private String currentUserId;
//    private String currentUserName;

    private LatLng latLng;

    //firebase auth instance variable
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //get connection to firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Locations"); //reference to the collection called Collecton (collection of locations)
    String apiKey = BuildConfig.ApiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        latLng = new LatLng(47.608013,-122.335167);


        // Initialize the SDK
        Places.initialize(getApplicationContext(), apiKey);

        // Create a new Places client instance
        PlacesClient placesClient = Places.createClient(this);

        final AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        assert autocompleteFragment != null;
        autocompleteFragment.setHint("Location Name (ex. Roscoe's)");

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.OPENING_HOURS, Place.Field.UTC_OFFSET));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                autocompleteFragment.setText(place.getName());
                locationName = place.getName();
                locationId = place.getId();
                Log.d("Success", "Place: " + place.isOpen() + ", " + place.getName());
                Log.d("Success", "locationName: " + locationName);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("error", "An error occurred: " + status);
            }
        });

        // If we need access to the user id or username can use AppController class like below
        // AppController.getInstance().getUserId();
        firebaseAuth = FirebaseAuth.getInstance();


        progressBar = findViewById(R.id.addLocationProgressBar);
//        locationNameEditText = findViewById(R.id.locationNameEditText);
        groupNameAutoComplete = findViewById(R.id.groupNameAutoComplete);
        saveButton = findViewById(R.id.addLocationButton);

        progressBar.setVisibility(View.INVISIBLE);

        groupNames = new HashSet<>();
        groupNamesArray = new ArrayList<>();

        //get all groupNames from Firestore for groupNames array that I use for autocomplete drop down in activity_add_location.xml
        collectionReference.whereEqualTo("userId", AppController.getInstance()
                .getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot locations : queryDocumentSnapshots) {
                                Location location = locations.toObject(Location.class);
                                groupNames.add(location.getGroupName());
                            }
                            //convert hashset to arraylist
                            groupNamesArray = new ArrayList<>(groupNames);

                            //Creating the instance of ArrayAdapter containing list of group Names
                            ArrayAdapter<String> adapter = new ArrayAdapter<>
                                    (AddLocationActivity.this, android.R.layout.select_dialog_item, groupNamesArray);

                            //Getting the instance of AutoCompleteTextView
                            AutoCompleteTextView actv = findViewById(R.id.groupNameAutoComplete);
                            actv.setThreshold(1);//will start working from first character
                            actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView

                        }else {
                            //document is empty (no collections)
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        if (AppController.getInstance() != null) {
            currentUserId = AppController.getInstance().getUserId();
        }

        authStateListener =new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {

                }else {

                }
            }
        };

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveLocation(locationName, locationId);
//                fetchLocation();
            }
        });



    }



    @Override
    protected void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }


    //    @Override
//    public void onClick(View view) {
//        switch (view.getId()){
//            case R.id.addCollectionButton:
//                //save Location
//                saveCollection();
//                break;
//        }
//    }

    private void saveLocation(String locationName, String locationId) {
        //get text view with collection name
//        String locationName = locationNameEditText.getText().toString().trim();
        String groupName = groupNameAutoComplete.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(locationName)){
            // add collectionName to *Database Location location in firestore
            //don't need to deal with image like in Paulo's app (he is storing image in *Storage)
            //create a journal object - model
            Location location = new Location();
            location.setLocationName(locationName);
            location.setGroupName(groupName);
            location.setUserId(currentUserId);
            location.setLocationId(locationId);

            //invoke location reference in forestore database

            collectionReference.document(locationName).set(location)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.setVisibility(View.INVISIBLE);

                            //take user to list
                            startActivity(new Intent(AddLocationActivity.this,
                                    LocationListActivity.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e.getMessage());

                        }
                    });

            //save a location instance
        }else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//    }

//    private void getPlaceId() {
//        // Initialize the AutocompleteSupportFragment.
//        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
//                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//
//// Specify the types of place data to return.
//        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
//
//// Set up a PlaceSelectionListener to handle the response.
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                // TODO: Get info about the selected place.
//                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
//            }
//
//            @Override
//            public void onError(Status status) {
//                // TODO: Handle the error.
//                Log.i(TAG, "An error occurred: " + status);
//            }
//        });
//    }

//    private void fetchLocation(){
////initialize
////        if (!Places.isInitialized()) {
////            Places.initialize(this, this.getString(R.string.apiKey));
////        }
////        PlacesClient placesClient = Places.createClient(this);
//        // Define a Place ID.
//        String placeId = "ChIJGzsYf0wVkFQR0QnPNo9YzKA";
//
//// Specify the fields to return.
//        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.OPENING_HOURS, Place.Field.UTC_OFFSET);
//
//// Construct a request object, passing the place ID and fields array.
//        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
//
//        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
//            @Override
//            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
//                Place place = fetchPlaceResponse.getPlace();
//                Log.d(TAG, "onSuccess: Place found " + place.isOpen());
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d(TAG, "onFailure: Place not found" + e.getMessage());
//            }
//        });
//
//    }



}
