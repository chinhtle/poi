package com.cmpe277.poi.app;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

/*
 * An extension of ParseObject that makes
 * it more convenient to access information
 * about a given Task
 */

@ParseClassName("UserDestination")
public class UserDestination extends ParseObject {

    public UserDestination() {
        // A default constructor is required.
    }

    // TODO: Need to add more information about destination.. Just a template for now.

    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String title) {
        put("title", title);
    }

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }
}
