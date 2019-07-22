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
import com.google.api.LogDescriptor;
import com.margi.sesame.BuildConfig;
import com.margi.sesame.GroupListActivity;
import com.margi.sesame.LocationDetailsActivity;
import com.margi.sesame.R;

import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import model.Location;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class LocationRecyclerAdapter extends RecyclerView.Adapter<LocationRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Location> locationList;
    private PlacesClient placesClient;
    private Boolean openBool;
    private Boolean openStatus;
    private OnLocationNameListener mOnLocationNameListener;
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

//                Log.d("RecyclerAdapter", "onSuccess: " + place.getOpeningHours());

//                Date closingTime = null;
//                try {
//                    //get closing hour and minute
//                    //currently hard coded
//                    //todo add logic to pull in hour and minute of closing for today
//                    Date today = new Date();
//                    Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
//                    calendar.setTime(today);   // assigns calendar to given date
//
//                    List<Period> periods = place.getOpeningHours().getPeriods();
//                    //todo loop through periods List and do something meaningful with open closed times
//
//
//                    Log.d("LocationRecyclerAdapter", "onSuccess: Name " + place.getName());
//                    Log.d("LocationRecyclerAdapter", "onSuccess: " + periods.);
//
//
//                    closingTime = new SimpleDateFormat("k-m").parse("15-59");
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    //get todays hour and minute
//                    Date today = new Date();
//                    Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
//                    calendar.setTime(today);   // assigns calendar to given date
//                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
//                    int minute = calendar.get(Calendar.MINUTE);
//                    String hourString = Integer.toString(hour);
//                    String minuteString = Integer.toString(minute);
//                    String source = hourString + "-" + minuteString;
//
//                    Date currentTime = new SimpleDateFormat("k-m").parse(source);
//
//                    long diff =  closingTime.getTime() - currentTime.getTime();
//                    int numOfDays = (int) (diff / (1000 * 60 * 60 * 24));
//                    int hours = (int) (diff / (1000 * 60 * 60));
//                    int minutes = (int) (diff / (1000 * 60));
//                    int seconds = (int) (diff / (1000));
//
//                    Log.d("RecyclerAdapter", "onSuccess: " + minutes);
//
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//

//                Log.d("RecyclerAdapter", "onSuccess: " + today.toString());



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