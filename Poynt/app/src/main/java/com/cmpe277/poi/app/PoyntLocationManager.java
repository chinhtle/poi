package com.cmpe277.poi.app;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

public class PoyntLocationManager
{
    private Activity ownerActivity;
    private LocationManager locationManager;
    private Criteria locationCriteria;

    private final static int LOCATION_CHANGE_DURATION_MS = 2000;
    private final static int LOCATION_CHANGE_METER = 10;

    private final LocationListener locationListener = new LocationListener()
    {
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status,
                                    Bundle extras) {
        }
    };

    public PoyntLocationManager(Activity owner, boolean update, boolean singleRequest)
    {
        ownerActivity = owner;

        setupLocationServices(update, singleRequest);
    }

    public void setupLocationServices(boolean update, boolean singleRequest)
    {
        String svcName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager)ownerActivity.getSystemService(svcName);
        String provider = setupLocationCriteria(locationManager);

        if(singleRequest)
        {
            getLocationSingleUpdate();
        }
        else
        {
            locationManager.requestLocationUpdates(provider,
                    LOCATION_CHANGE_DURATION_MS,
                    LOCATION_CHANGE_METER,
                    locationListener);
        }

        if(update) {
            Location l = locationManager.getLastKnownLocation(provider);
            updateWithNewLocation(l);
        }
    }

    private void getLocationSingleUpdate()
    {
        if((locationManager != null) && (locationCriteria != null))
        {
            locationManager.requestSingleUpdate(locationCriteria,
                                                locationListener,
                                                null /*looper*/);
        }
    }

    private String setupLocationCriteria(LocationManager lm)
    {
        locationCriteria = new Criteria();
        locationCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        locationCriteria.setPowerRequirement(Criteria.POWER_LOW);
        locationCriteria.setAltitudeRequired(false);
        locationCriteria.setBearingRequired(false);
        locationCriteria.setSpeedRequired(false);
        locationCriteria.setCostAllowed(true);

        String provider = lm.getBestProvider(locationCriteria, true);
        return provider;
    }

    private void updateWithNewLocation(Location location) {
        TextView currentLocationText;
        currentLocationText = (TextView)ownerActivity.findViewById(R.id.currentLocation);
        String latLongString = "No location found";

        if (location != null)
        {
            double lat = location.getLatitude();
            double lng = location.getLongitude(); latLongString = "Lat:" + lat + "\nLong:" + lng;
        }

        currentLocationText.setText("Your Current Position is:\n" + latLongString);
    }
}
