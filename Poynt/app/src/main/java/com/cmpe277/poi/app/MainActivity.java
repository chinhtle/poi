package com.cmpe277.poi.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.parse.ParseUser;


public class MainActivity extends Activity {
    // TODO
    // private DestListAdapter destListAdapter;
    // private ListView destList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* TODO: Need to implement custom Parse adapters.
        // Get the list view and set our custom task list adapter
        destList = (ListView)findViewById(R.id.destList);

        // Subclass of ParseQueryAdapter
        destListAdapter = new DestListAdapter(this, destList);

        if(destList != null)
            destList.setAdapter(destListAdapter);
        */
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
