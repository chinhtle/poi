package com.cmpe277.poi.app;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;

public class PoyntApplication extends Application {
    private final String TAG = "PoiApplication";

    @Override
    public void onCreate()
    {
        super.onCreate();

        Log.v(TAG, "onCreate()");

        // Register our parse classes for easy data retrieval/storage with Parse services.
        // TODO: Need to add other objects once they're created
        ParseObject.registerSubclass(UserDestination.class);

        // Initialize Parse services with provided Parse App-ID and Client-ID
        Parse.initialize(this,
                "dg5ib463a6si9I8AWGdIqEY4wfDg3C8Bd14ogwvJ" /*app-id*/,
                "ANu6zoXmVh0hFvO2wslSbyDF8JdSiIA48F9p0Tjg" /*client-id*/);

		/*
		 * This app lets an anonymous user create and save photos of meals
		 * they've eaten. An anonymous user is a user that can be created
		 * without a username and password but still has all of the same
		 * capabilities as any other ParseUser.
		 *
		 * After logging out, an anonymous user is abandoned, and its data is no
		 * longer accessible. In your own app, you can convert anonymous users
		 * to regular users so that data persists.
		 *
		 * Learn more about the ParseUser class:
		 * https://www.parse.com/docs/android_guide#users
		 */
        //ParseUser.enableAutomaticUser();

		/*
		 * For more information on app security and Parse ACL:
		 * https://www.parse.com/docs/android_guide#security-recommendations
		 */
        ParseACL defaultACL = new ParseACL();

		/*
		 * If you would like all objects to be private by default, remove this
		 * line
		 */
        //defaultACL.setPublicReadAccess(true);
        //defaultACL.setPublicWriteAccess(true);  // For now, we will make it publicly available!

        ParseACL.setDefaultACL(defaultACL, true);
    }
}
