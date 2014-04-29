package com.cmpe277.poi.app;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.parse.ParseUser;


public class MainActivity extends Activity {
    PoyntLocationManager poyntLocation;
    private final static boolean LOCATION_UPDATE_ON_CREATE = true;
    private final static boolean LOCATION_SINGLE_UPDATE = true;

    private AutoCompleteTextView from;
    private AutoCompleteTextView to;
    private String directions = null;

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
    }

    public void loadDirections(View view){
        new requestDirections().execute();
    }

    private class requestDirections extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... arg0) {
            String LOG_TAG = "MainActivity";
            HttpURLConnection conn = null;
            StringBuilder jsonResults = new StringBuilder();
            try {
                StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?sensor=false&key=" + "AIzaSyCYj21MIxpMSrZQLJBfBrzF6ET6CG5MiMg");
                sb.append("&origin=" + from.getText());
                sb.append("&destination=" + to.getText());
                String url_string = sb.toString();
                url_string = url_string.replace(" ","+");
                URL url = new URL(url_string);
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());
                // Load the results into a StringBuilder
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
            } catch (MalformedURLException e) {
                Log.v(LOG_TAG, "Error processing Directions API URL", e);
            } catch (IOException e) {
                Log.v(LOG_TAG, "Error connecting to Directions API", e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            return jsonResults.toString();
        }
        protected void onPostExecute(String result) {
            directions = result;
            Intent intent = new Intent(MainActivity.this, DirectionsActivity.class);
            intent.putExtra("resultList", directions);
            startActivity(intent);
        }

    }

    // TODO: Add logout button
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


    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
    */
}
