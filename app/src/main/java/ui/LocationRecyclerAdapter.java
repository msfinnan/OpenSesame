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
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.margi.sesame.BuildConfig;
import com.margi.sesame.GroupListActivity;
import com.margi.sesame.R;
import com.margi.sesame.TimeRange;

import org.joda.time.LocalTime;

import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import model.Location;

public class LocationRecyclerAdapter extends RecyclerView.Adapter<LocationRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Location> locationList;
    private PlacesClient placesClient;
    private Boolean openBool;
    private Boolean openStatus;
    private OnLocationNameListener mOnLocationNameListener;
    private HashMap<String, ArrayList<TimeRange>> openHoursHashMap;
//    private ArrayList<TimeRange> rangesArray;
    private ArrayList<Place> placesArray;
    private static String TAG = "LocationRecyclerAdapter";
//    private String apiKey;


    public LocationRecyclerAdapter(Context context, List<Location> locationList, OnLocationNameListener onLocationNameListener) {
        this.context = context;
        this.locationList = locationList;
        this.mOnLocationNameListener = onLocationNameListener;

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

        return new ViewHolder(view, context, mOnLocationNameListener);
    }


    @Override
    public void onBindViewHolder(@NonNull final LocationRecyclerAdapter.ViewHolder viewHolder, int position) {
        final Location location = locationList.get(position);
//        rangesArray = new ArrayList<TimeRange>();
       //set Open status on location
        String placeId = location.getLocationId();


        // Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.OPENING_HOURS, Place.Field.UTC_OFFSET);

        // Construct a request object, passing the place ID and fields array.
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                Place place = fetchPlaceResponse.getPlace();
                openStatus = place.isOpen();
                openHoursHashMap = new HashMap<>();
                placesArray = new ArrayList<>();

                //hard coded requested time (hour and min) & day of week
                LocalTime requestedTimeHourMin = new LocalTime(9, 15);
                String requestedDayOfWeek = "TUESDAY";

                //add JSON data for each location to a HashMap
                if (place.getOpeningHours() != null) {
                    for (Period place1 : place.getOpeningHours().getPeriods()) {

                        ArrayList<TimeRange> rangesArray = new ArrayList<>();


                        String openDay = place1.getOpen().getDay().toString();
                        String closeDay = place1.getClose().getDay().toString();
                        int openHours = place1.getOpen().getTime().getHours();
                        int openMinutes = place1.getOpen().getTime().getMinutes();
                        int closeHours = place1.getClose().getTime().getHours();
                        int closeMinutes = place1.getClose().getTime().getMinutes();


                        TimeRange timeRange;
                        TimeRange timeRange2;

                        if (openDay.equals(closeDay)) { //not open overnight
                            timeRange = new TimeRange(openHours, openMinutes, closeHours, closeMinutes);

                            if (!openHoursHashMap.containsKey(openDay)) {
                                rangesArray.add(timeRange);
                                openHoursHashMap.put(openDay, rangesArray);
                            } else {
                                openHoursHashMap.get(openDay).add(timeRange);
                            }
                        } else { //open overnight
                            timeRange = new TimeRange(openHours, openMinutes, 23, 59);

                            if (!openHoursHashMap.containsKey(openDay)) {
                                rangesArray.add(timeRange);
                                openHoursHashMap.put(openDay, rangesArray);
                            } else {
                                openHoursHashMap.get(openDay).add(timeRange);
                            }
                            timeRange2 = new TimeRange(0, 00, closeHours, closeMinutes);

                            if (!openHoursHashMap.containsKey(closeDay)) {
                                rangesArray.add(timeRange2);
                                openHoursHashMap.put(closeDay, rangesArray);
                            } else {
                                openHoursHashMap.get(closeDay).add(timeRange2);
                            }
                        }

                    }
                }else {
                    //if its null
                    //todo handle this
                    Log.d(TAG, "onSuccess: No open hours for: " + place.getName());
                }


//                Log.d(TAG, "onSuccess: Tuesday @ " + place.getName() + openHoursHashMap.get("TUESDAY"));
//
//                ArrayList<TimeRange> hashValues;
//                hashValues = openHoursHashMap.get(requestedDayOfWeek);


//                for (int i = 0; i < hashValues.size(); i++){
//                    if (hashValues.get(i).rangeIncludes(requestedTimeHourMin)){
//                        Log.d(TAG, "onSuccess: " + "TRUE " + place.getName() );
//                    }else{
//                        Log.d(TAG, "onSuccess: " + "FALSE" + place.getName());
//                    }
//                }

                if (openStatus != null) {
//                    location.setOpen(openStatus);
//                    Log.d("LocationRecyclerAdapter", "fetchLocation: openStatus " + openStatus);
//                    viewHolder.openClosed.setText(location.getOpen().toString());
                    if (openStatus) {
                        viewHolder.openClosedTextView.setText("Open");
                        viewHolder.openClosedTextView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                    }else {
                        viewHolder.openClosedTextView.setText("Closed");
                        viewHolder.openClosedTextView.setTextColor(context.getResources().getColor(R.color.colorAccent));
                    }
                }else {
                    viewHolder.openClosedTextView.setText("No hours provided");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("LocationRecyclerAdapter", "onFailure: Place not found" + e.getMessage());
            }
        });


        viewHolder.locationName.setText(location.getLocationName());
        viewHolder.groupName.setText(location.getGroupName());


    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView locationName;
        public TextView groupName;
        public TextView openClosedTextView;

        OnLocationNameListener onLocationNameListener;



        public ViewHolder(@NonNull View itemView, Context ctx, OnLocationNameListener onLocationNameListener) {
            //pass context so we can go to next activity ctx.startActivity...
            super(itemView);
            context = ctx;
            this.onLocationNameListener = onLocationNameListener;




            locationName = itemView.findViewById(R.id.location_name_list);
            groupName = itemView.findViewById(R.id.group_name_list);
            openClosedTextView = itemView.findViewById(R.id.open_closed_list);


            locationName.setOnClickListener(this);
//            groupName.setOnClickListener(this);

            groupName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, GroupListActivity.class);
                    TextView textView = view.findViewById(R.id.group_name_list);
                    String message = textView.getText().toString();
                    intent.putExtra("groupName", message);
                    context.startActivity(intent);

                }
            });

        }

        @Override
        public void onClick(View view) {
//            onLocationNameListener.onGroupNameClick(getAdapterPosition());

            onLocationNameListener.onLocationNameClick(getAdapterPosition());

        }
    }
    public interface OnLocationNameListener {
        void onLocationNameClick(int position);
//        void onGroupNameClick(int position);
    }

}

