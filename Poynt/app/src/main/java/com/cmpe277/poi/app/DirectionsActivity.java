package com.cmpe277.poi.app;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.text.Html;
import android.util.Log;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DirectionsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);
        ArrayList<String> resultList = null;
        ArrayList<String> locationList = null;
        String LOG_TAG = "DirectionsActivity";
        String list = getIntent().getStringExtra("resultList");
        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(list.toString());
            JSONArray routesJsonArray = jsonObj.getJSONArray("routes");

            // Extract the instructions and end locations from the results
            //Getting legs JSONArray
            for(int i = 0; i < routesJsonArray.length(); i++) {
                JSONObject routesJsonObject = routesJsonArray.getJSONObject(i);
                JSONArray legsJsonArray = routesJsonObject.getJSONArray("legs");
                //Getting each steps JSONArray
                for(int j = 0; j < legsJsonArray.length(); j++){
                    JSONObject legsJsonObject = legsJsonArray.getJSONObject(j);
                    JSONArray stepsJsonArray = legsJsonObject.getJSONArray("steps");
                    resultList = new ArrayList<String>(stepsJsonArray.length());
                    locationList = new ArrayList<String>(stepsJsonArray.length());
                    //Getting html_instructions and end_location from each steps array
                    for (int k = 0; k < stepsJsonArray.length(); k++) {
                        resultList.add(stepsJsonArray.getJSONObject(k).getString("html_instructions"));
                        locationList.add(stepsJsonArray.getJSONObject(k).getString("end_location"));
                    }
                }
            }
        } catch (JSONException e) {
            Log.v(LOG_TAG, "Cannot process JSON results", e);
        }
        LinearLayout ll = (LinearLayout) findViewById(R.id.scrolllayout);
        for(int i = 0; i < resultList.size(); i++)
        {
            TextView tv = new TextView(this);
            tv.setText(Html.fromHtml(resultList.get(i)));
            tv.setTextSize(20);
            ll.addView(tv);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.directions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
