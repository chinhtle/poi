package com.cmpe277.poi.app;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.text.Html;
import android.util.Log;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DirectionsActivity extends Activity {

    private ArrayList<String> listShow = new ArrayList<String>();
    private ArrayList<String> locationList = new ArrayList<String>();
   private Map<String,String> finalMap = new HashMap<String, String>();
   //private Set<String> finalSet = new HashSet<String>();

    private final String API_KEY="AIzaSyCDqRAPjBga2wA0zwVzQpfElhn_ZqmLJ1Q";
    private final String URL="https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private final String TYPES="store";
    private final String RADIUS="500";
    private final String SENSOR="false";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        ArrayList<String> resultList = null;

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
            ///
            for (String s: locationList){
                Log.i("BlocationList",s);
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

       // added to pass locationList
        Button btnPoi = (Button) findViewById(R.id.buttonPoi);
        btnPoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONArray jArr;
                ArrayList<String> urlList= new ArrayList<String>();
             //   Map<KeyPOI, String> mapPOIs = new HashMap<KeyPOI, String>();

                //gets https urls for each location list
                if(locationList.isEmpty()){
                    listShow.add("no POI available");
                }
                else{
                    jArr=getJsonLngLat(locationList);
                    for (int i=0; i<jArr.length(); i++){
Log.i("lSize",String.valueOf(jArr.length()));
                        try {
                            urlList.add(getHttpsUrl(jArr.getJSONObject(i)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }



            new GetPOIs().execute(urlList);



            }
        });
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

                       ///
                       String open = jObj2.getJSONObject("opening_hours").getString("open_now");
                       if (open.equals("true"))
                           open = "Yes";
                       else
                           open = "No";


                       StringBuilder poiSb = new StringBuilder();
                       poiSb.append(jObj2.getString("name") + "\n");
                       poiSb.append(jObj2.getString("vicinity") + "\n");
                       poiSb.append("Rating: " + jObj2.getString("rating") + "\n");
                       poiSb.append("Open Now: " + open);
                       String poisNew = poiSb.toString();
                       ///

                       finalMap.put(id,poisNew);
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
             //       Log.i("jsonWhole",jsonResults.toString());




                    in.close();
                    conn.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

                          for(Map.Entry<String,String> entry: finalMap.entrySet()){
                        listShow.add(entry.getValue());
                    }



            return null ;
        }


        @Override
        protected void onPostExecute(String result) {
            Intent intent = new Intent(DirectionsActivity.this, POIActivity.class);
            intent.putStringArrayListExtra("listShow", listShow);
            startActivity(intent);

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
