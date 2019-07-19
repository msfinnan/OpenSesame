package ui;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.margi.sesame.BuildConfig;
import com.margi.sesame.GroupListActivity;
import com.margi.sesame.R;

import java.util.Arrays;
import java.util.List;

import model.Location;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class LocationRecyclerAdapter extends RecyclerView.Adapter<LocationRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Location> locationList;
    private PlacesClient placesClient;
    private Boolean openBool;
    private Boolean openStatus;
//    private String apiKey;


    public LocationRecyclerAdapter(Context context, List<Location> locationList) {
        this.context = context;
        this.locationList = locationList;

    }


    @NonNull
    @Override
    public LocationRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.location_row, viewGroup, false);

        // Initialize the SDK
        String apiKey = BuildConfig.ApiKey;
        Places.initialize(context.getApplicationContext(), apiKey);

        // Create a new Places client instance
        placesClient = Places.createClient(context);

        return new ViewHolder(view, context);
    }


    @Override
    public void onBindViewHolder(@NonNull final LocationRecyclerAdapter.ViewHolder viewHolder, int position) {
        final Location location = locationList.get(position);
        //set Open status on location
        String placeId = location.getLocationId();


// Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.OPENING_HOURS, Place.Field.UTC_OFFSET);

// Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                Place place = fetchPlaceResponse.getPlace();
                Log.d("LocationRecyclerAdapter", "onSuccess: Place found " + place.isOpen());
                openStatus = place.isOpen();

                location.setOpen(openStatus);
                Log.d("LocationRecyclerAdapter", "fetchLocation: openStatus " + openStatus);
                viewHolder.openClosed.setText(location.getOpen().toString());
                if (openStatus) {
                    viewHolder.openClosed.setText("Open");
                    viewHolder.openClosed.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                }else{
                    viewHolder.openClosed.setText("Closed");
                    viewHolder.openClosed.setTextColor(context.getResources().getColor(R.color.colorAccent));

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("LocationRecyclerAdapter", "onFailure: Place not found" + e.getMessage());
            }
        });


        //get location object


        viewHolder.locationName.setText(location.getLocationName());
        viewHolder.groupName.setText(location.getGroupName());


    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView locationName;
        public TextView groupName;
        public TextView openClosed;
        String userId;
        String userName;


        public ViewHolder(@NonNull View itemView, Context ctx) {
            //pass context so we can go to next activity ctx.startActivity...
            super(itemView);
            context = ctx;


            locationName = itemView.findViewById(R.id.location_name_list);
            groupName = itemView.findViewById(R.id.group_name_list);
            openClosed = itemView.findViewById(R.id.open_closed_list);

            groupName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, GroupListActivity.class);
                    TextView textView = view.findViewById(R.id.group_name_list);
                    String message = textView.getText().toString();
                    intent.putExtra("groupName", message);
                    context.startActivity(intent);
                    Log.d("LocationRecycler", "onClick: message is: " + message);

                }
            });

        }
    }

//        private boolean fetchOpenStatus(){
//initialize
//        if (!Places.isInitialized()) {
//            Places.initialize(this, this.getString(R.string.apiKey));
//        }
//        PlacesClient placesClient = Places.createClient(this);
        // Define a Place ID.
//        String placeId = ;
//
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
//                Log.d("LocationRecyclerAdapter", "onSuccess: Place found " + place.isOpen());
//                openStatus = place.isOpen();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d("LocationRecyclerAdapter", "onFailure: Place not found" + e.getMessage());
//            }
//        });
//
//            Log.d("LocationRecyclerAdapter", "fetchLocation: openStatus " + openStatus);
//        return openStatus;

//    }
}
