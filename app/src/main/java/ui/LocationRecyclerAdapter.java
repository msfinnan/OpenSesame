package ui;

import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.margi.sesame.LocationDetailsActivity;
import com.margi.sesame.R;
import com.margi.sesame.TimeRange;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import model.Location;
import util.AppController;

public class LocationRecyclerAdapter extends RecyclerView.Adapter<LocationRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Location> locationList;
    private PlacesClient placesClient;
    private Boolean openBool;
    private Boolean openStatus;
    private OnDeleteListener onDeleteListener;
    private HashMap<String, ArrayList<TimeRange>> openHoursHashMap;
    private ArrayList<Place> placesArray;
    private static String TAG = "LocationRecyclerAdapter";
    private AppController appController = AppController.getInstance();
    public ImageButton addToCalendarButton;
    public ImageButton addToCalendarNowButton;
    public ImageButton deleteLocationButton;
    public TextView locationName;


    public LocationRecyclerAdapter(Context context, List<Location> locationList, OnDeleteListener onDeleteListener) {
        this.context = context;
        this.locationList = locationList;
        this.onDeleteListener = onDeleteListener;

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

        return new ViewHolder(view, context, onDeleteListener);

    }


    @Override
    public void onBindViewHolder(@NonNull final LocationRecyclerAdapter.ViewHolder viewHolder, int position) {
        final Location location = locationList.get(position);

//        rangesArray = new ArrayList<TimeRange>();
       //set Open status on location
        String placeId = location.getLocationId();
        final LocalTime requestedTime = appController.getFutureHourMin();
        final String requestedDay = appController.getFutureDay();


        locationName = viewHolder.itemView.findViewById(R.id.location_name_list);

        locationName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LocationDetailsActivity.class);
                //add location id to intent
                intent.putExtra("locationId", location.getLocationId());
                context.startActivity(intent);
            }
        });


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

                if (requestedDay != null && requestedTime != null) { //there is a requested date & time
                    //make visible button
                    ArrayList<TimeRange> hashValues = openHoursHashMap.get(requestedDay.toUpperCase());
                    if (openStatus != null) {
                    for (int i = 0; i < hashValues.size(); i++) {
                        if (hashValues.get(i).rangeIncludes(requestedTime)) {
                            if (hashValues.get(i).getEndTime().getMinuteOfHour() != 59) {
                                viewHolder.openClosedTextView.setText("Will Be Open Until " + hashValues.get(i).getFormattedEndTime()); // add "until " + endOfRange
                                viewHolder.openClosedTextView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                                break;
                            } else{ //open overnight
                                switch (requestedDay.toUpperCase()) {
                                    case "SUNDAY":
                                        viewHolder.openClosedTextView.setText("Will Be Open Until " + openHoursHashMap.get("MONDAY").get(1).getFormattedEndTime());
                                        viewHolder.openClosedTextView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                                        break;
                                    case "MONDAY":
                                        viewHolder.openClosedTextView.setText("Will Be Open Until " + openHoursHashMap.get("TUESDAY").get(0).getFormattedEndTime());
                                        viewHolder.openClosedTextView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                                        break;
                                    case "TUESDAY":
                                        viewHolder.openClosedTextView.setText("Will Be Open Until " + openHoursHashMap.get("WEDNESDAY").get(0).getFormattedEndTime());
                                        viewHolder.openClosedTextView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                                        break;
                                    case "WEDNESDAY":
                                        viewHolder.openClosedTextView.setText("Will Be Open Until " + openHoursHashMap.get("THURSDAY").get(0).getFormattedEndTime());
                                        viewHolder.openClosedTextView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                                        break;
                                    case "THURSDAY":
                                        viewHolder.openClosedTextView.setText("Will Be Open Until " + openHoursHashMap.get("FRIDAY").get(0).getFormattedEndTime());
                                        viewHolder.openClosedTextView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                                        break;
                                    case "FRIDAY":
                                        viewHolder.openClosedTextView.setText("Will Be Open Until " + openHoursHashMap.get("SATURDAY").get(0).getFormattedEndTime());
                                        viewHolder.openClosedTextView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                                        break;
                                    case "SATURDAY":
                                        viewHolder.openClosedTextView.setText("Will Be Open Until " + openHoursHashMap.get("SUNDAY").get(0).getFormattedEndTime());
                                        viewHolder.openClosedTextView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                                        break;
                                }
                                break;
                            }

                        } else {
                            viewHolder.openClosedTextView.setText("Will Be Closed");
                            viewHolder.openClosedTextView.setTextColor(context.getResources().getColor(R.color.colorAccent));
                        }
                    }
                    }else {
                        viewHolder.openClosedTextView.setText("No hours provided");
                    }
//
                } else { //no requested date & time / see whats open now
                    String todayDayOfWeek = LocalDate.now().dayOfWeek().getAsText().toUpperCase();


                    ArrayList<TimeRange> hashValues = openHoursHashMap.get(todayDayOfWeek);
                    if (openStatus != null && hashValues !=null) {

                        for (int i = 0; i < hashValues.size(); i++) {
//                            if (hashValues.get(i).rangeIncludes(requestedTime)) {
                            if (openStatus){
                                Log.d(TAG, "onSuccess: is it open now bool" + place.isOpen() + " " + place.getName());

                                if (hashValues.get(i).getEndTime().getMinuteOfHour() != 59) {
                                    viewHolder.openClosedTextView.setText("Open Until " + hashValues.get(i).getFormattedEndTime()); // add "until " + endOfRange
                                    viewHolder.openClosedTextView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                                    break;
                                } else{ //open overnight

                                    switch (requestedDay.toUpperCase()) {
                                        case "SUNDAY":
                                            viewHolder.openClosedTextView.setText("Open Until " + openHoursHashMap.get("MONDAY").get(1).getFormattedEndTime());
                                            viewHolder.openClosedTextView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                                            break;
                                        case "MONDAY":
                                            viewHolder.openClosedTextView.setText("Open Until " + openHoursHashMap.get("TUESDAY").get(0).getFormattedEndTime());
                                            viewHolder.openClosedTextView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                                            break;
                                        case "TUESDAY":
                                            viewHolder.openClosedTextView.setText("Open Until " + openHoursHashMap.get("WEDNESDAY").get(0).getFormattedEndTime());
                                            viewHolder.openClosedTextView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                                            break;
                                        case "WEDNESDAY":
                                            viewHolder.openClosedTextView.setText("Open Until " + openHoursHashMap.get("THURSDAY").get(0).getFormattedEndTime());
                                            viewHolder.openClosedTextView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                                            break;
                                        case "THURSDAY":
                                            viewHolder.openClosedTextView.setText("Open Until " + openHoursHashMap.get("FRIDAY").get(0).getFormattedEndTime());
                                            viewHolder.openClosedTextView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                                            break;
                                        case "FRIDAY":
                                            viewHolder.openClosedTextView.setText("Open Until " + openHoursHashMap.get("SATURDAY").get(0).getFormattedEndTime());
                                            viewHolder.openClosedTextView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                                            break;
                                        case "SATURDAY":
                                            viewHolder.openClosedTextView.setText("Open Until " + openHoursHashMap.get("SUNDAY").get(0).getFormattedEndTime());
                                            viewHolder.openClosedTextView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                                            break;
                                    }

                                    break;

                                }

                            } else {
                                viewHolder.openClosedTextView.setText("Closed Now");
                                viewHolder.openClosedTextView.setTextColor(context.getResources().getColor(R.color.colorAccent));
                            }
                        }
                    } else {
                        viewHolder.openClosedTextView.setText("No hours provided");
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("LocationRecyclerAdapter", "onFailure: Place not found" + e.getMessage());
            }
        });


        locationName.setText(location.getLocationName());
        viewHolder.groupName.setText(location.getGroupName());


    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView groupName;
        public TextView openClosedTextView;

        OnDeleteListener onDeleteListener;


        public ViewHolder(@NonNull View itemView, Context ctx, OnDeleteListener onDeleteListener) {
            //pass context so we can go to next activity ctx.startActivity...
            super(itemView);
            context = ctx;



            this.onDeleteListener = onDeleteListener;
//            locationName = itemView.findViewById(R.id.location_name_list);
            groupName = itemView.findViewById(R.id.group_name_list);
            openClosedTextView = itemView.findViewById(R.id.open_closed_list);
            addToCalendarButton = itemView.findViewById(R.id.add_to_calendar_button);
            addToCalendarNowButton = itemView.findViewById(R.id.add_to_calendar_now_button);
            deleteLocationButton = itemView.findViewById(R.id.delete_location_button);

            //todo consider making these one button for better user experience 
            if (appController.getFutureDay() != null && appController.getFutureHourMin() != null ){ //looking at future times
                addToCalendarButton.setVisibility(View.VISIBLE);
            }else{
                addToCalendarNowButton.setVisibility(View.VISIBLE);
            }

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

            addToCalendarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addEventToCalendar(locationName.getText().toString());
                }
            });

            addToCalendarNowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addEventToCalendarNow(locationName.getText().toString());
                }
            });

            deleteLocationButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            onDeleteListener.onDeleteClick(getAdapterPosition());
        }
    }

    private void addEventToCalendar(String locationName) {
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(appController.getFutureYear(), appController.getFutureMonth(), appController.getFutureDayOfMonth(),
                appController.getFutureHour(), appController.getFutureMin());
        Calendar endTime = Calendar.getInstance();
        endTime.set(appController.getFutureYear(), appController.getFutureMonth(), appController.getFutureDayOfMonth(),
                (appController.getFutureHour() + 1) , appController.getFutureMin());
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, "Go to " + locationName)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, locationName);
//                .putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com");

        context.startActivity(intent);
    }

    private void addEventToCalendarNow(String locationName) {
        Calendar beginTime = Calendar.getInstance();
        LocalDate today = LocalDate.now();
        LocalTime timeNow = LocalTime.now();
        beginTime.set(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth(),
               timeNow.getHourOfDay() , timeNow.getMinuteOfHour());
        Calendar endTime = Calendar.getInstance();
        endTime.set(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth(),
                (timeNow.getHourOfDay() + 1) , timeNow.getMinuteOfHour());
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, "Go to " + locationName)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, locationName);
//                .putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com");

        context.startActivity(intent);
    }

    public interface OnDeleteListener {
        void onDeleteClick(int position);

    }

}

