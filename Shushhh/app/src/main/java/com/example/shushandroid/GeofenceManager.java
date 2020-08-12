package com.example.shushandroid;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class GeofenceManager {

    private static String GEOFENCE_ID = "SOME_GEOFENCE_ID";
    private static Context context;

    // only three times: save, delete, onCreate

    public static void addGeofences(ArrayList<ShushObject> shushObjects, Context context) {
        Log.i("Geofence", shushObjects.toString());
        for (ShushObject shushObject : shushObjects) {
            if (shushObject.getLatLng() != null) {
                Log.i("Geofence", "In");
                GeofencingClient geofencingClient = LocationServices.getGeofencingClient(context);
                GeofenceHelper geofenceHelper = new GeofenceHelper(context);
                Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, shushObject.getLatLng(), Float.parseFloat(shushObject.getRadius().substring(0, shushObject.getRadius().length()-1)), Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
                GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
                PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Log.i("Geofence", "Granted");
                    geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                            .addOnSuccessListener(aVoid -> Log.d("Geofence", "onSuccess: Geofence Added..."))
                            .addOnFailureListener(e -> {
                                String errorMessage = geofenceHelper.getErrorString(e);
                                Log.d("Geofence", "onFailure: " + errorMessage);
                            });
                } else {
                    Log.i("Geofence", "Out");
                }

            }
        }
    }

}
