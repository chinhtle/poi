package com.cmpe277.poi.app;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/*
 * The FavoritePoyntsAdapter is an extension of ParseQueryAdapter
 * that has a custom layout for Poynt objects
 */

public class FavoritePoyntsAdapter extends ParseQueryAdapter<Poynt> {
    private final String TAG = "FavoritePoyntsAdapter";
    private ListView poyntsList;

    public FavoritePoyntsAdapter(Context context, ListView poyntsList) {
        super(context, new ParseQueryAdapter.QueryFactory<Poynt>() {
            public ParseQuery<Poynt> create() {
                ParseQuery query = new ParseQuery("Poynt");
                query.whereEqualTo("user", ParseUser.getCurrentUser());
                return query;
            }
        });
        this.poyntsList = poyntsList;
    }

    public void transferPoyntToDetailsActivity(Poynt poynt)
    {
        String poyntName = poynt.getName();
        double poyntRating = poynt.getRating();
        String poyntAddress = poynt.getAddress();
        String poyntOpenNow = poynt.getOpenNow();

        Log.v(TAG, "transferPoyntToDetailsActivity");

        // Store it in the extras
        Intent intent = new Intent(getContext(), PoyntDetailsActivity.class);
        intent.putExtra("name", poyntName);
        intent.putExtra("rating", poyntRating);

        Log.v(TAG, "transferPoyntToDetailsActivity - name: " + poyntName);
        Log.v(TAG, "transferPoyntToDetailsActivity - rating: " + poyntRating);

        if((poyntAddress != null) && !poyntAddress.isEmpty()) {
            intent.putExtra("address", poyntAddress);
            Log.v(TAG, "transferPoyntToDetailsActivity - address: " + poyntAddress);
        }
        else
            intent.putExtra("address", "N/A");

        if((poyntOpenNow != null) && !poyntOpenNow.isEmpty()) {
            Log.v(TAG, "transferPoyntToDetailsActivity - opennow: " + poyntOpenNow);
            intent.putExtra("opennow", poyntOpenNow);
        }
        else
            intent.putExtra("opennow", "N/A");

        getContext().startActivity(intent);
    }

    @Override
    public View getItemView(Poynt poynt, View v, ViewGroup parent) {
        boolean imageSet = false;
        boolean notesSet = false;

        if (v == null) {
            v = View.inflate(getContext(), R.layout.favorite_list_item, null);
        }

        super.getItemView(poynt, v, parent);

        // Set the Poynt's name
        TextView nameView = (TextView) v.findViewById(R.id.poyntName);
        nameView.setText(poynt.getName());

        // Set the Poynt's rating
        TextView ratingView = (TextView) v.findViewById(R.id.poyntRating);
        ratingView.setText("" + poynt.getRating());

        // Set the Poynt's address
        TextView addressView = (TextView)v.findViewById(R.id.poyntAddress);
        addressView.setText(poynt.getAddress());

        // Set the onClickListeners for viewing the point of interest's details in the
        // PoyntDetailsActivity
        LinearLayout poyntListItem = (LinearLayout)v.findViewById(R.id.poyntListItem);
        poyntListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = poyntsList.getPositionForView((View) v.getParent());
                Log.v(TAG, "Poynt clicked, row " + position);

                // Get the Poynt object according to this position
                Poynt poynt = (Poynt) poyntsList.getItemAtPosition(position);

                Log.v(TAG, "Poynt's objectId " + poynt.getObjectId());

                transferPoyntToDetailsActivity(poynt);
            }
        });

        return v;
    }

    private void updatePoyntList() {
        loadObjects();
        poyntsList.setAdapter(this);
    }
}
