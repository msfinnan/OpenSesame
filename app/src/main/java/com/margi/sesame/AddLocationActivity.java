package com.margi.sesame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.Location;
import ui.LocationRecyclerAdapter;
import util.UserInfo;

public class AddLocationActivity extends AppCompatActivity {


    //todo make dynamic get groupNames from firestore. Get all group names fom users id, add them to array, remove dups
//    String[] groupNames = {"Dog Parks", "Neighborhood Favorites", "Dog Friendly Bars", "Date Night"};
    private HashSet<String> groupNames;
    private ArrayList<String> groupNamesArray;

    private static final String TAG = "AddLocationActivity";
    private Button saveButton;
    private ProgressBar progressBar;
    private EditText locationNameEditText;
    private AutoCompleteTextView groupNameAutoComplete;

    private String currentUserId;
    private String currentUserName;

    //firebase auth instance variable
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //get connection to firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //in Paulos activity there is an image that goes to database so the Storage is instantiated here, I don't need to do that for collection
//    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("Locations"); //reference to the collection called Collecton (collection of locations)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);


        //if we need access to the user id or username can use UserInfo class like below
//        UserInfo.getInstance().getUserId();
        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.addLocationProgressBar);
        locationNameEditText = findViewById(R.id.locationNameEditText);
        groupNameAutoComplete = findViewById(R.id.groupNameAutoComplete);
        saveButton = findViewById(R.id.addLocationButton);

        progressBar.setVisibility(View.INVISIBLE);

        groupNames = new HashSet<>();
        groupNamesArray = new ArrayList<>();

        //get all groupNames from Firestore for groupNames array that I use for drop down in activity_add_location.xml
        collectionReference.whereEqualTo("userId", UserInfo.getInstance()
                .getUserId()) //gets back all of users locations
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
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                                    (AddLocationActivity.this, android.R.layout.select_dialog_item, groupNamesArray);

                            //Getting the instance of AutoCompleteTextView
                            AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.groupNameAutoComplete);
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


        Log.d("AddLocationActivity", "onSuccess: groupNames " + groupNames);
        Log.d("AddLocationActivity", "onCreate: groupNamesArray " + groupNamesArray );



        //paulo has a section here on getting the username bc that is displayed in his app, mine doesn't display that so i'm skipping it
        //still need userId
        if (UserInfo.getInstance() != null) {
            currentUserId = UserInfo.getInstance().getUserId();
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
                saveLocation();
            }
        });

    }



    @Override
    protected void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);


//        //get all groupNames from Firestore for groupNames array that I use for drop down in activity_add_location.xml
//        collectionReference.whereEqualTo("userId", UserInfo.getInstance()
//                .getUserId()) //gets back all of users locations
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        if (!queryDocumentSnapshots.isEmpty()) {
//                            for (QueryDocumentSnapshot locations : queryDocumentSnapshots) {
//                                Location location = locations.toObject(Location.class);
//                                groupNames.add(location.getGroupName());
//                                Log.d("AddLocationActivity", "onSuccess: groupNames array " + groupNames);
//                            }
//
//                        }else {
//                            //document is empty (no collections)
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
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

    private void saveLocation() {
        //get text view with collection name
        String locationName = locationNameEditText.getText().toString().trim();
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
}
