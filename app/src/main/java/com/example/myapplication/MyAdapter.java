package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private Context context;
    private JSONObject[] mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View view;
        public TextView title;
        public TextView venue;
        public TextView description;
        public Button tickets;
        public MyViewHolder(View v) {
            super(v);
            view = v;
            title = view.findViewById(R.id.title);
            venue = view.findViewById(R.id.venue);
            description = view.findViewById(R.id.description);
            tickets = view.findViewById(R.id.tickets);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Context context, JSONObject[] myDataset) {
        this.context = context;
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        try {
            holder.title.setText(mDataset[position].get("datetime").toString());
            JSONObject venue = new JSONObject(mDataset[position].get("venue").toString());
            final JSONArray offers = new JSONArray(mDataset[position].get("offers").toString());
            final JSONObject offer = new JSONObject(offers.get(0).toString());

            holder.venue.setText(venue.get("name").toString());
            holder.description.setText(mDataset[position].get("description").toString());
            holder.tickets.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = null;
                    try {
                        url = offer.get("url").toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse(url));
                    context.startActivity(intent);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

}