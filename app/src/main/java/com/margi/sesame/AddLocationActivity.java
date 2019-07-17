package com.margi.sesame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import model.Location;
import util.UserInfo;

public class AddLocationActivity extends AppCompatActivity {
    private static final String TAG = "AddLocationActivity";
    private Button saveButton;
    private ProgressBar progressBar;
    private EditText locationNameEditText;
    private EditText groupNameEditText;

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
        groupNameEditText = findViewById(R.id.groupNameEditText);
        saveButton = findViewById(R.id.addLocationButton);

        progressBar.setVisibility(View.INVISIBLE);

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
        String groupName = groupNameEditText.getText().toString().trim();

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
