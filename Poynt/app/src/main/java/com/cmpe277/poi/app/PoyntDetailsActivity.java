package com.cmpe277.poi.app;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class PoyntDetailsActivity extends Activity {
    private Poynt currPoynt = new Poynt();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poynt_details);

        Intent intent = getIntent();

        String poyntName = intent.getStringExtra("name");
        double poyntRating = intent.getDoubleExtra("rating", 0.0);
        String poyntAddress = "";
        if(intent.hasExtra("address")) {
            poyntAddress = intent.getStringExtra("address");
        }

        String poyntOpenNow = "";

        if(intent.hasExtra("opennow")) {
            poyntOpenNow = intent.getStringExtra("opennow");
        }

        // Save it as part of the Poynt object
        currPoynt.setName(poyntName);
        currPoynt.setRating(poyntRating);
        currPoynt.setAddress(poyntAddress);
        currPoynt.setOpenNow(poyntOpenNow);

        // Add it to the view
        TextView nameView = (TextView)findViewById(R.id.poyntName);
        TextView ratingView = (TextView)findViewById(R.id.poyntRating);
        TextView addressView = (TextView)findViewById(R.id.poyntAddress);
        TextView openNowView = (TextView)findViewById(R.id.poyntOpenNow);

        nameView.setText(poyntName);
        ratingView.setText(Double.toString(poyntRating));
        addressView.setText(poyntAddress);
        openNowView.setText(poyntOpenNow);
    }

    public void onClickSavePoynt(View view)
    {
        // Save the current user associated with this Poynt
        currPoynt.setUser(ParseUser.getCurrentUser());

        // Set it so that only the current user can access it
        currPoynt.setACL(new ParseACL(ParseUser.getCurrentUser()));

        // Save the Poynt data and return
        currPoynt.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "Saved successfully.",
                                   Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error saving: " + e.getMessage(),
                                   Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
