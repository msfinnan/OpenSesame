package com.margi.sesame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import model.Location;
import ui.LocationRecyclerAdapter;
import util.UserInfo;

public class LocationListActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
//    private StorageReference storageReference;
    // don't think I need this bc I don't have images, maybe once I add locations to the collection
    private List<Location> locationList;
    private RecyclerView recyclerView;
    private LocationRecyclerAdapter locationRecyclerAdapter;

    private CollectionReference collectionReference = db.collection("Locations");
    private TextView noLocationEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        noLocationEntry = findViewById(R.id.list_no_locations);

        locationList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerViewCollectionList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add :
                //take users to AddLocationActivity
                //todo might need to change this once users can add locations to a collection
                if (user != null && firebaseAuth != null) {
                    startActivity(new Intent(LocationListActivity.this,
                            AddLocationActivity.class));
//                    finish(); //come back to this
                }

                break;
            case R.id.action_sign_out :
                if (user != null && firebaseAuth != null){
                    firebaseAuth.signOut();

                    startActivity(new Intent(LocationListActivity.this,
                            MainActivity.class));

//                    finish(); //come back to this.
                }
                break;
                }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //get all Collections from Firestore
        collectionReference.whereEqualTo("userId", UserInfo.getInstance()
                .getUserId()) //gets back all of users locations
//                .whereEqualTo("groupName", "Dog Friendly Bars") //filters down 
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot locations : queryDocumentSnapshots) {
                                Location location = locations.toObject(Location.class);
                                locationList.add(location);
                            }

                            //invoke recycler view
                            locationRecyclerAdapter = new LocationRecyclerAdapter(LocationListActivity.this,
                                    locationList);
                            recyclerView.setAdapter(locationRecyclerAdapter);
                            locationRecyclerAdapter.notifyDataSetChanged();

                        }else {
                            //document is empty (no collections)
                            noLocationEntry.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }
}
