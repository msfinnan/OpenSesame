package ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.margi.sesame.R;

import java.util.List;

import model.Collection;

public class CollectionRecyclerAdapter extends RecyclerView.Adapter<CollectionRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Collection> collectionList;

    public CollectionRecyclerAdapter(Context context, List<Collection> collectionList) {
        this.context = context;
        this.collectionList = collectionList;
    }

    @NonNull
    @Override
    public CollectionRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.collection_row, viewGroup, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionRecyclerAdapter.ViewHolder viewHolder, int position) {
        //get collection object
        Collection collection = collectionList.get(position);

        viewHolder.collectionName.setText(collection.getCollectionName());

    }

    @Override
    public int getItemCount() {
        return collectionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView collectionName;
        String userId;
        String userName;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            //pass context so we can go to next activity ctx.startActiity...
            super(itemView);
            context = ctx;

            collectionName = itemView.findViewById(R.id.collection_name_list);

        }
    }
}
