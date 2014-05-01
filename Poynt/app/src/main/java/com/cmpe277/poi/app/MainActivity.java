package com.cmpe277.poi.app;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends Activity {
    private final static String TAG = "MainActivity";

    PoyntLocationManager poyntLocation;
    private final static boolean LOCATION_UPDATE_ON_CREATE = false;
    private final static boolean LOCATION_SINGLE_UPDATE = true;

    private AutoCompleteTextView from;
    private AutoCompleteTextView to;
    private ListView poiListView;
    private String directions = null;

    private ArrayList<String> listShow = new ArrayList<String>();
    private ArrayList<String> locationList = new ArrayList<String>();
    private ArrayList<String> resultList = new ArrayList<String>();
    private Map<String,String> finalMap = new HashMap<String, String>();
    private ArrayList<Poynt> poyntList = new ArrayList<Poynt>();

    //private Set<String> finalSet = new HashSet<String>();

    private final String API_KEY="AIzaSyCDqRAPjBga2wA0zwVzQpfElhn_ZqmLJ1Q";
    private final String URL="https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private final String TYPES="store";
    private final String RADIUS="500";
    private final String SENSOR="true";

    private ArrayAdapter<String> poiListViewAdapter;

    // Indicates whether user has clicked current location button.  Resets when user brings focus
    // to the textfield.
    private boolean currentLocationClicked = false;
    public String currentLocationStr = "";

    private enum PostDirExecType {
        POST_EXEC_TYPE_UNKNOWN,
        POST_EXEC_TYPE_POI,
        POST_EXEC_TYPE_DIRECTIONS
    };

    private PostDirExecType execType = PostDirExecType.POST_EXEC_TYPE_UNKNOWN;

    // TODO
    // private DestListAdapter destListAdapter;
    // private ListView destList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        poyntLocation = new PoyntLocationManager(this,
                LOCATION_UPDATE_ON_CREATE,
                LOCATION_SINGLE_UPDATE);

        /* TODO: Need to implement custom Parse adapters.
        // Get the list view and set our custom task list adapter
        destList = (ListView)findViewById(R.id.destList);

        // Subclass of ParseQueryAdapter
        destListAdapter = new DestListAdapter(this, destList);

        if(destList != null)
            destList.setAdapter(destListAdapter);
        */

        from = (AutoCompleteTextView) findViewById(R.id.from);
        from.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_items));
        to = (AutoCompleteTextView) findViewById(R.id.to);
        to.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_items));
        poiListView = (ListView) findViewById(R.id.poiListView);

        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Only clear if current location was clicked.  We don't want to clear the user's
                // selection!
                if(currentLocationClicked) {
                    // Reset the current location flag
                    currentLocationClicked = false;

                    // Clear the textfield for user to enter new location
                    from.setText("");
                }
            }
        });

        // Set action listeners for the to textfield for "search" or "done" from keyboard
        // event.  Invokes retrieving POIs
        to.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                if((arg1 == EditorInfo.IME_ACTION_SEARCH) ||
                   (arg1 == EditorInfo.IME_ACTION_DONE))
                {
                    // search pressed and perform your functionality.
                    getPois();
                }
                return false;
        } });

        // Setup click listener for the POI listview
        poiListViewAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_selectable_list_item);

        poiListView.setAdapter(poiListViewAdapter);
        poiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> listView, View itemView,
                                    int itemPosition, long itemId)
            {
                // Transfer current item to PoyntDetailsActivity
                Log.v(TAG, "onItemClick - position: " + itemPosition);

                // Retrieve the current item's details
                Poynt retrievedPoynt = poyntList.get(itemPosition);

                String poyntName = retrievedPoynt.getName();
                double poyntRating = retrievedPoynt.getRating();
                String poyntAddress = retrievedPoynt.getAddress();
                String poyntOpenNow = retrievedPoynt.getOpenNow();

                // Store it in the extras
                Intent intent = new Intent(MainActivity.this, PoyntDetailsActivity.class);
                intent.putExtra("name", poyntName);
                intent.putExtra("rating", poyntRating);

                Log.v(TAG, "onClickItem - name: " + poyntName);
                Log.v(TAG, "onClickItem - rating: " + poyntRating);

                if((poyntAddress != null) && !poyntAddress.isEmpty()) {
                    intent.putExtra("address", poyntAddress);
                    Log.v(TAG, "onClickItem - address: " + poyntAddress);
                }
                else
                    intent.putExtra("address", "N/A");

                if((poyntOpenNow != null) && !poyntOpenNow.isEmpty()) {
                    Log.v(TAG, "onClickItem - opennow: " + poyntOpenNow);
                    intent.putExtra("opennow", poyntOpenNow);
                }
                else
                    intent.putExtra("opennow", "N/A");

                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });
    }

    public boolean validateLocationFields()
    {
        boolean success = true;

        if(!((from.getText().length() > 0) && (to.getText().length() > 0)))
        {
            // Display an error
            Toast.makeText(this, "All fields must be filled out", Toast.LENGTH_LONG).show();
            success = false;
        }

        Log.v(TAG, "Validation successful: " + success);
        return success;
    }

    public void getDirections()
    {
        // First we need to validate the to and from fields!
        if(validateLocationFields())
        {
            // Load the directions string into local buffer
            loadDirections();
        }
    }

    public void getPois()
    {
        Log.v(TAG, "Entry point of retrieving the POIs.");
        execType = PostDirExecType.POST_EXEC_TYPE_POI;
        getDirections();
    }

    public void executeGetPoisPostDirections()
    {
        JSONArray jArr;
        ArrayList<String> urlList = new ArrayList<String>();
        //   Map<KeyPOI, String> mapPOIs = new HashMap<KeyPOI, String>();

        // Clear in case there are existing data.
        poiListViewAdapter.clear();
        poyntList.clear();

        //gets https urls for each location list
        if (locationList.isEmpty()) {
            listShow.add("No POIs Available.");

            // Then add everything to the adapter again.
            poiListViewAdapter.addAll(listShow);
        } else {
            jArr = getJsonLngLat(locationList);
            for (int i = 0; i < jArr.length(); i++) {
                Log.i("lSize", String.valueOf(jArr.length()));
                try {
                    urlList.add(getHttpsUrl(jArr.getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            new GetPOIs().execute(urlList);
        }
    }

    private JSONArray getJsonLngLat(ArrayList<String> list){
        JSONArray jArr = new JSONArray();

        for(String s: list) {
            try {
                JSONObject jobj = new JSONObject(s);
                jArr.put(jobj);
                //
                Log.i("slocationList",s);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return jArr;
    }

    private String getHttpsUrl(JSONObject jObj){
        StringBuilder sb = new StringBuilder();
        sb.append(URL);
        sb.append("location=");

        String lng=null;
        String lat=null;
        try {
            lng = String.valueOf(jObj.getDouble("lng"));
            lat = String.valueOf(jObj.getDouble("lat"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sb.append(lat+",");
        sb.append(lng+"&radius=");

        sb.append(RADIUS+"&types=");
        sb.append(TYPES+"&sensor=");
        sb.append(SENSOR+"&key=");
        sb.append(API_KEY);

        return sb.toString();
    }

    private void getValuesPOI(String result){

        try {
            JSONObject jObj = new JSONObject(result);

            if(jObj.has("error_message")){
                String msg1 = jObj.getString("error_message"+" Free tier allows 1000 calls in 24 hours");
                String msg2 = jObj.getString("status");

                Toast.makeText(getApplicationContext(),msg1+"\n"+msg2,Toast.LENGTH_LONG).show();
                listShow.add(msg1);
                listShow.add(msg2);

            }

            JSONArray jArr = jObj.getJSONArray("results");

            for (int k=0; k<jArr.length();k++){
                JSONObject jObj2 = jArr.getJSONObject(k);
                String id = jObj2.getString("id");
                if (!finalMap.containsKey(id)) {
                    Poynt newPoynt = new Poynt();

                    StringBuilder poiSb = new StringBuilder();
                    String tempValue = "";

                    if(jObj2.has("name")) {
                        tempValue = jObj2.getString("name");
                        poiSb.append(tempValue + "\n");
                        newPoynt.setName(tempValue);
                    }

                    if(jObj2.has("vicinity")) {
                        tempValue = jObj2.getString("vicinity");
                        poiSb.append(tempValue + "\n");
                        newPoynt.setAddress(tempValue);
                    }

                    if(jObj2.has("rating")) {
                        tempValue = jObj2.getString("rating");
                        //poiSb.append(tempValue + "\n");
                        newPoynt.setRating(Double.parseDouble(tempValue));
                    }

                    if(jObj2.has("opening_hours") && jObj2.has("open_now"))
                    {
                        ///
                        String open = jObj2.getJSONObject("opening_hours").getString("open_now");
                        if (open.equals("true"))
                            open = "Yes";
                        else
                            open = "No";

                        newPoynt.setOpenNow(open);
                        //poiSb.append("Open Now: " + open);
                    }

                    String poisNew = poiSb.toString();
                    Log.v(TAG, "Adding point of interest: " + poisNew);

                    finalMap.put(id,poisNew);
                    listShow.add(poisNew);

                    // Add it to the poynt objects list
                    poyntList.add(newPoynt);
                }
            }
//            for(String s: finalSet){
//                Log.i("fs",s);
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private class GetPOIs extends AsyncTask<ArrayList<String>, Void, String> {


        @Override
        protected String doInBackground(ArrayList<String>... params) {

            ArrayList<String> tempList = new ArrayList<String>();
            tempList = params[0];
            Log.i("number",String.valueOf(tempList.size()));
            for (int i=0; i<tempList.size(); i++) {
                try {
                    StringBuilder jsonResults = new StringBuilder();
                    URL url = new URL(tempList.get(i));
                    Log.i("urlafter",tempList.get(i));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    InputStreamReader in = new InputStreamReader(conn.getInputStream());
                    int read;
                    char[] buff = new char[1024];
                    while ((read = in.read(buff)) != -1) {
                        jsonResults.append(buff, 0, read);
                    }
                    getValuesPOI(jsonResults.toString());

                    in.close();
                    conn.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

//            for(Map.Entry<String,String> entry: finalMap.entrySet()){
//                listShow.add(entry.getValue());
//            }

            return null ;
        }


        @Override
        protected void onPostExecute(String result) {
            if(!listShow.isEmpty()) {
                // Clear in case there are existing data.
                poiListViewAdapter.clear();

                // Then add everything to the adapter again.
                poiListViewAdapter.addAll(listShow);
            }
        }
    }

    public void parseDirectionsToLists()
    {
        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(directions);
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
                        String tempHtmlInstructions = Html.fromHtml(stepsJsonArray.getJSONObject(k).getString("html_instructions")).toString();
                        resultList.add(tempHtmlInstructions);
                        locationList.add(stepsJsonArray.getJSONObject(k).getString("end_location"));
                    }
                }
            }
            ///
            for (String s: locationList){
                Log.i("locationList",s);
            }
        } catch (JSONException e) {
            Log.v(TAG, "Cannot process JSON results", e);
        }
    }

    public void loadDirections() {
        Log.v(TAG, "Loading directions..");
        new requestDirections().execute();
    }

    private class requestDirections extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... arg0) {
            Log.v(TAG, "Requesting directions in background..");

            HttpURLConnection conn = null;
            StringBuilder jsonResults = new StringBuilder();
            try {
                StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?sensor=" + SENSOR + "&key=" + "AIzaSyCYj21MIxpMSrZQLJBfBrzF6ET6CG5MiMg");

                // Grab the current location long/lat if exists.
                if(currentLocationClicked)
                    sb.append("&origin=" + currentLocationStr);
                else
                    sb.append("&origin=" + from.getText());

                sb.append("&destination=" + to.getText());

                String url_string = sb.toString();
                url_string = url_string.replace(" ","+");

                Log.v(TAG, "Google Places API Request String: " + url_string);

                URL url = new URL(url_string);
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());
                // Load the results into a StringBuilder
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
                Log.v(TAG, "Directions results: " + jsonResults.toString());
            } catch (MalformedURLException e) {
                Log.v(TAG, "Error processing Directions API URL", e);
            } catch (IOException e) {
                Log.v(TAG, "Error connecting to Directions API", e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            return jsonResults.toString();
        }
        protected void onPostExecute(String result) {
            directions = result;

            // Parse the obtained JSON results
            parseDirectionsToLists();

            switch(execType)
            {
                case POST_EXEC_TYPE_POI:
                    executeGetPoisPostDirections();
                    break;
                case POST_EXEC_TYPE_DIRECTIONS:
                {
                    Intent intent = new Intent(MainActivity.this, DirectionsActivity.class);
                    intent.putStringArrayListExtra("resultList", resultList);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                    break;
                }
                default:
                    // Unhandled error case, do nothing.
                    Log.v(TAG, "Received unhandled exec type on post directions execution.");
                    return;
            }
        }

    }

    public void onClickSignout(View view)
    {
        ParseUser.logOut();

        // return to the authentication page
        showAuthActivity();
    }

    // When logging out, show auth activity again.
    public void showAuthActivity() {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    public void onClickDirections(View view)
    {
        execType = PostDirExecType.POST_EXEC_TYPE_DIRECTIONS;
        getDirections();
    }

    public void onClickCurrentLocation(View view)
    {
        Log.v(TAG, "Retrieving the current location..");
        currentLocationClicked = true;
        poyntLocation.getLocationSingleUpdate();
    }

    public void onClickFavorites(View view)
    {
        Intent intent = new Intent(MainActivity.this, FavoritePoyntsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }
}
