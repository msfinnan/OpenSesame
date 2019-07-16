package ui;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.margi.sesame.R;

import java.util.List;

import model.Location;

public class LocationRecyclerAdapter extends RecyclerView.Adapter<LocationRecyclerAdapter.ViewHolder> {
    private Context context; //chose android context, can choose firebase context also...
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
        Location location = locationList.get(position);

        viewHolder.locationName.setText(location.getLocationName());

    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView locationName;
        String userId;
        String userName;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            locationName = itemView.findViewById(R.id.location_name_list);
        }
    }
}
