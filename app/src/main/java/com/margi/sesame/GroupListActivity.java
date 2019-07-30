package com.margi.sesame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

import model.Location;
import ui.GroupRecyclerAdapter;
import ui.LocationRecyclerAdapter;
import util.AppController;

public class GroupListActivity extends AppCompatActivity implements GroupRecyclerAdapter.OnDeleteFromGroupListener {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //    private StorageReference storageReference;
    // don't think I need this bc I don't have images, maybe once I add locations to the collection
    private List<Location> locationList;
    private RecyclerView recyclerView;
    private GroupRecyclerAdapter groupRecyclerAdapter;

    private CollectionReference collectionReference = db.collection("Locations");
    private String groupName;

//    private TextView noLocationEntry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        locationList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerViewCollectionList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        groupName = intent.getStringExtra("groupName");
        Log.d("GroupList", "onCreate: value of groupName is " + groupName);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(groupName);
            supportActionBar.show();
        }

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

                    startActivity(new Intent(GroupListActivity.this,
                            MainActivity.class));

//                    finish(); //come back to this.
                }
                break;
            case R.id.open_now :
                startActivity(new Intent(GroupListActivity.this,
                        LocationListActivity.class));
                break;
            case R.id.open_later :
                startActivity(new Intent(GroupListActivity.this,
                        SelectDayTimeActivity.class));
                break;
            case R.id.add_location_from_menu :
                startActivity(new Intent(GroupListActivity.this,
                        AddLocationActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onStart() {
        super.onStart();
        final Context context;
        //get all Collections from Firestore
        collectionReference.whereEqualTo("userId", AppController.getInstance()
                .getUserId()) //gets back all of users locations
                .whereEqualTo("groupName", groupName) //filters down
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot locations : queryDocumentSnapshots) {
                                Location location = locations.toObject(Location.class);
                                locationList.add(location);
                            }

//                            invoke recycler view
                            groupRecyclerAdapter = new GroupRecyclerAdapter(GroupListActivity.this, locationList, GroupListActivity.this);
                            recyclerView.setAdapter(groupRecyclerAdapter);
                            groupRecyclerAdapter.notifyDataSetChanged();
                        }else {
                            //document is empty (no collections)
//                            noLocationEntry.setVisibility(View.VISIBLE);
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
    public void onDeleteFromGroup(int position) {
        final Location currentLocation = locationList.get(position);
        db.collection("Locations").document(currentLocation.getLocationName() + ":" + user.getUid()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("GroupListActivity", "onSuccess: Successfully deleted " + currentLocation.getLocationName() );
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("GroupListActivity", "onFailure: Error deleting " + currentLocation.getLocationName() + " " + e.getMessage());
                    }
                });

        locationList.remove(position);
        groupRecyclerAdapter.notifyDataSetChanged();

    }
}
