package com.cmpe277.poi.app;

import android.app.ListActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import java.util.ArrayList;



public class POIActivity extends ListActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayList<String> listShow = new ArrayList<String>();
        listShow = getIntent().getStringArrayListExtra("listShow");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.activity_poi, listShow);
        setListAdapter(adapter);

        ListView listView = getListView();
        listView.setTextFilterEnabled(true);

    }





}
