package com.example.shushandroid;

import com.google.android.gms.maps.model.LatLng;

/**
 * @apiNote Shush Object helper class
 * @author  Akash Veerappan
 * @version 1.0
 * @since   2020-7-18
 * @resources
 */
public class ShushObject {

    public static class Key {
        public static final String NULL = "N/A";
    }

    private String name;
    private String time;
    private String date;
    private String location;
    private String radius;
    private String UUID;
    private String rep;

    private LatLng latLng;

    public ShushObject() {

    }

    public ShushObject(String name, String time, String date, String rep, String location, String radius, String UUID) {
        this.name = name;
        this.time = time;
        this.date = date;
        this.rep = rep;
        this.location = location;
        this.radius = radius;
        this.UUID = UUID;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRep() {
        return rep;
    }

    public void setRep(String rep) {
        this.rep = rep;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    @Override
    public String toString() {
        return "ShushObject{" +
                "name='" + name + '\'' +
                ", time='" + time + '\'' +
                ", date='" + date + '\'' +
                ", rep='" + rep + '\'' +
                ", location='" + location + '\'' +
                ", radius='" + radius + '\'' +
                ", UUID='" + UUID + '\'' +
                ", LatLng='" + latLng + '\'' +
                '}';
    }
}
