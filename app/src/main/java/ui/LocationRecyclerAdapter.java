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
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.margi.sesame.BuildConfig;
import com.margi.sesame.GroupListActivity;
import com.margi.sesame.R;
import com.margi.sesame.TimeRange;

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
    private HashMap<String, ArrayList> openHoursHashMap;
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
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                Place place = fetchPlaceResponse.getPlace();
                openStatus = place.isOpen();
                openHoursHashMap = new HashMap<>();
                placesArray = new ArrayList<>();



//                    for (int i = 0; i < place.getOpeningHours().getPeriods().size(); i++ ){
//                int i = 0;
//                Log.d(TAG, "onSuccess: Outside the loop and i is " + i );
                for(Period place1 : place.getOpeningHours().getPeriods()) {
                    ArrayList<TimeRange> rangesArray = new ArrayList<>();


                    String openDay = place1.getOpen().getDay().toString();
                        String closeDay = place1.getClose().getDay().toString();
                        int openHours = place1.getOpen().getTime().getHours();
                        int openMinutes = place1.getOpen().getTime().getMinutes();
                        int closeHours = place1.getClose().getTime().getHours();
                        int closeMinutes = place1.getClose().getTime().getMinutes();

//                    Log.d(TAG, "onSuccess: in the loop and i is " + i + " and open Day is " + openDay);

                        TimeRange timeRange = new TimeRange(openHours, openMinutes, closeHours, closeMinutes);
//                        timeRange.setStartTime(String.valueOf(openHours) + ":" + String.valueOf(openMinutes));
//                        timeRange.setEndTime(String.valueOf(closeHours) + ":" + String.valueOf(closeMinutes));

//                    Log.d(TAG, "onSuccess: in the loop and i is " + i + " and timeRange is " + timeRange);

//                        rangesArray.add(timeRange);

                        //if key with value of open day does not
                    if (!openHoursHashMap.containsKey(openDay)) {
                        rangesArray.add(timeRange);
                        openHoursHashMap.put(openDay, rangesArray);
                    } else {
                        openHoursHashMap.get(openDay).add(timeRange);
                        Log.d(TAG, "onSuccess: hitting the else " + openDay);

                    }
                        //if key with value of open day does exist add rangesArray to that key
//                        i++;
                    }
                Log.d("LocationRecycler", "onSuccess: openHoursHashMap" + openHoursHashMap);
                Log.d("LocationRecycler", "onSuccess: Saturday" + openHoursHashMap.get("SATURDAY"));


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

//        //get location object


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
//                    Log.d("LocationRecycler", "onClick: message is: " + message);

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


//date stuff
//                Date date= new Date();
//
//                long time = date.getTime();
//                Log.d("LocationRecycler", "Time in Milliseconds: " + time);
//
//                Log.d("LocationRecycler", "Day of week " + Calendar.DAY_OF_WEEK);

//                SimpleDateFormat simpleDataFormat = new SimpleDateFormat("EEE");
//                Date date = new Date();
//                DateFormat df = new SimpleDateFormat("hh:'00' a");
//                String hour = df.format(date);
//                Log.d("LocationRecycler", "onSuccess: hour " + hour);

//                java.sql.Timestamp ts = rs.getTimestamp(1);

//                Calendar now = Calendar.getInstance();
//                int year = now.get(Calendar.YEAR);
//                int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
//                int day = now.get(Calendar.DAY_OF_MONTH);
//                int dayOfWeek = now.get(Calendar.DAY_OF_WEEK); //sunday is 1
//                int hour = now.get(Calendar.HOUR_OF_DAY);
//                int minute = now.get(Calendar.MINUTE);
//                int second = now.get(Calendar.SECOND);

//                Log.d("RecyclerAdapter", "onSuccess: " + dayOfWeek + " " + day +  " " + hour + " " + minute);

//Get the calendar instance.
//                Calendar calendar = Calendar.getInstance();

//Set the time for the notification to occur.
//                calendar.set(Calendar.YEAR, 2019);
//                calendar.set(Calendar.MONTH, 7); //starts with 0!! 7 is Aug
//                calendar.set(Calendar.DAY_OF_MONTH, 17);
//                calendar.set(Calendar.HOUR_OF_DAY, 10);
//                calendar.set(Calendar.MINUTE, 45);
//                calendar.set(Calendar.SECOND, 0);



//                Log.d("RecyclerAdapter", "onSuccess: future date" + calendar.get(Calendar.DAY_OF_WEEK));
//                Log.d("RecyclerAdapter", "onSuccess: which is greater" + calendar.compareTo(now));