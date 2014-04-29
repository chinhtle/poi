package com.cmpe277.poi.app;

public class KeyPOI {
    private final double lat;
    private final double lng;

    public KeyPOI(double lat, double lng){
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KeyPOI)) return false;
        KeyPOI key = (KeyPOI) o;
        return lat == key.lat && lng == key.lng;
    }

    @Override
    public int hashCode() {
        int result = (int)lat;
        result = 31*result+ (int)lng;
        return result;
    }
}
