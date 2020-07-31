package com.example.shushandroid;

/**
 * @apiNote Shush Object helper class
 * @author  Akash Veerappan
 * @version 1.0
 * @since   2020-7-18
 * @resources
 */
public class ShushObject {
    private String name;
    private String time;
    private String dateRep;
    private String location;
    private String radius;
    private String UUID;

    public ShushObject() {

    }

    public ShushObject(String name, String time, String dateRep, String location, String radius, String UUID) {
        this.name = name;
        this.time = time;
        this.dateRep = dateRep;
        this.location = location;
        this.radius = radius;
        this.UUID = UUID;
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

    public String getDateRep() {
        return dateRep;
    }

    public void setDateRep(String dateRep) {
        this.dateRep = dateRep;
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
                ", dateRep='" + dateRep + '\'' +
                ", location='" + location + '\'' +
                ", radius='" + radius + '\'' +
                ", UUID='" + UUID + '\'' +
                '}';
    }
}
