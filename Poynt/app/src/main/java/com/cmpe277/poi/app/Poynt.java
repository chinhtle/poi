package com.cmpe277.poi.app;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

/*
 * An extension of ParseObject that makes
 * it more convenient to access information
 * about a given Poynt object
 */

@ParseClassName("Poynt")
public class Poynt extends ParseObject {

    public Poynt() {
        // A default constructor is required.
    }

    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        put("name", name);
    }

    public double getRating() {
        return getDouble("rating");
    }

    public void setRating(double rating) {
        put("rating", rating);
    }

    public String getAddress() {
        return getString("address");
    }

    public void setAddress(String address) {
        put("address", address);
    }

    public String getOpenNow() {
        return getString("opennow");
    }

    public void setOpenNow(String open) {
        put("opennow", open);
    }

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }
}
