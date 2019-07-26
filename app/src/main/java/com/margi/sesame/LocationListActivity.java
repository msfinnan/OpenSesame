package com.margi.sesame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

import model.Location;
import ui.LocationRecyclerAdapter;
import util.AppController;

public class LocationListActivity extends AppCompatActivity {
    //implements LocationRecyclerAdapter.OnLocationNameListener

    private static final String TAG = "LocationListActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private TextView noLocationEntry;
    private List<Location> locationList;
    private RecyclerView recyclerView;
    private LocationRecyclerAdapter locationRecyclerAdapter;

    private HashSet<String> groupNames;
    private ArrayList<String> groupNamesArray;
    private Spinner spinner;


    private CollectionReference collectionReference = db.collection("Locations");

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


        groupNames = new HashSet<>();
        groupNames.add("View All Locations");
        groupNamesArray = new ArrayList<>();
        spinner = findViewById(R.id.group_spinner);

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
                                    (LocationListActivity.this, android.R.layout.simple_spinner_item, groupNamesArray);

                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(adapter);


                            int spinnerPosition = adapter.getPosition("View All Locations");

                            //set the default according to value
                            spinner.setSelection(spinnerPosition);


                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    if (adapterView.getItemAtPosition(i).toString().equals("View All Locations")){
                                        //stay on the same page

                                    }else {
                                        Intent intent = new Intent(LocationListActivity.this, GroupListActivity.class);
                                        //add group name to intent
                                        String groupName = adapterView.getItemAtPosition(i).toString();
                                        intent.putExtra("groupName", groupName);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });
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
        collectionReference.whereEqualTo("userId", AppController.getInstance()
                .getUserId()) //gets back all of users locations
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            locationList.clear();
                            for (QueryDocumentSnapshot locations : queryDocumentSnapshots) {
                                Location location = locations.toObject(Location.class);
                                locationList.add(location);
                            }

                            //invoke recycler view
                            locationRecyclerAdapter = new LocationRecyclerAdapter(LocationListActivity.this, locationList);
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

//    @Override
//    public void onLocationNameClick(int position) {
//        //get location object
//        Location currentLocation = locationList.get(position);
//        Intent intent = new Intent(this, LocationDetailsActivity.class);
//        //add location id to intent
//        intent.putExtra("locationId", currentLocation.getLocationId());
//        startActivity(intent);
//    }

}
