package com.margi.sesame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import io.grpc.Context;
import util.UserInfo;

public class AddCollectionActivity extends AppCompatActivity implements View.OnClickListener{
    private Button saveButton;
    private ProgressBar progressBar;
    private EditText collectionNameEditText;

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
    private CollectionReference collectionReference = db.collection("Collection"); //reference to the collection called Collecton (collection of locations)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_collection);

        //if we need access to the user id or username can use UserInfo class like below
//        UserInfo.getInstance().getUserId();
        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.addCollectionProgressBar);
        collectionNameEditText = findViewById(R.id.collectionNameEditText);

        progressBar.setVisibility(View.INVISIBLE);

        //paulo has a section here on getting the username bc that is displayed in his app, mine doesn't display that so i'm skipping it

        authStateListener =new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {

                }else {

                }
            }
        };

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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.addCollectionButton:
                //save Collection
                saveCollection();
                break;
        }
    }

    private void saveCollection() {
        //get text view with collection name
        String collectionName = collectionNameEditText.getText().toString().trim();

        if (!TextUtils.isEmpty(collectionName)){
            // add collectionName to *Database Collection collection in firestore
            //don't need to deal with image like in Paulo's app (he is storing image in *Storage)
        }else {

        }
    }
}
