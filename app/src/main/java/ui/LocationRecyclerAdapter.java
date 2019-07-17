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

import com.margi.sesame.GroupListActivity;
import com.margi.sesame.R;

import java.util.List;

import model.Location;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class LocationRecyclerAdapter extends RecyclerView.Adapter<LocationRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Location> locationList;


    public LocationRecyclerAdapter(Context context, List<Location> locationList) {
        this.context = context;
        this.locationList = locationList;

    }

    @NonNull
    @Override
    public LocationRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.location_row, viewGroup, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationRecyclerAdapter.ViewHolder viewHolder, int position) {
        //get location object
        Location location = locationList.get(position);

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
        String userId;
        String userName;


        public ViewHolder(@NonNull View itemView, Context ctx) {
            //pass context so we can go to next activity ctx.startActivity...
            super(itemView);
            context = ctx;

            locationName = itemView.findViewById(R.id.location_name_list);
            groupName = itemView.findViewById(R.id.group_name_list);

            groupName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, GroupListActivity.class);
                    TextView textView = (TextView) view.findViewById(R.id.group_name_list);
                    String message = textView.getText().toString();
                    intent.putExtra("groupName", message);
                    context.startActivity(intent);
                    Log.d("LocationRecycler", "onClick: message is: " + message);

//                    context.startActivity(new Intent(context, GroupListActivity.class));

                }
            });

        }
    }
}
