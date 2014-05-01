package com.cmpe277.poi.app;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.parse.ParseUser;


public class FavoritePoyntsActivity extends Activity {
    private FavoritePoyntsAdapter poyntsListAdapter;
    private ListView poyntsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_poynts);

        // Get the list view and set our custom poynts list adapter
        poyntsList = (ListView)findViewById(R.id.poyntsListView);

        // Subclass of ParseQueryAdapter
        poyntsListAdapter = new FavoritePoyntsAdapter(this, poyntsList);

        if(poyntsList != null)
            poyntsList.setAdapter(poyntsListAdapter);
    }

    public void onClickSearch(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    public void onClickLogout(View view)
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
}
