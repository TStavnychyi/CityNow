
package com.tstv.infofrom.model.weather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Weather extends RealmObject {

    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("current")
    @Expose
    private Current current;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Current getCurrent() {
        return current;
    }

    public void setCurrent(Current current) {
        this.current = current;
    }

}
