package com.example.shushandroid;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final float DEFAULT_ZOOM = 15f;
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private int radius;

    private EditText searchBar;
    private ImageView gpsLocate;

    Spinner spinner;

    private static final String TAG = "MapActivity";

    private FloatingActionButton checkFloatingActionButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        searchBar = findViewById(R.id.searchTextField);
        gpsLocate = findViewById(R.id.currentLocationButton);
        checkFloatingActionButton = findViewById(R.id.checkFloatingActionButton);

        checkFloatingActionButton.setOnClickListener(view -> {
            if (!searchBar.getText().toString().isEmpty()) {
                String radiusString = radius + "m";
                LocationDialog.LocationDataTransferItem.DATA = searchBar.getText().toString();
                LocationDialog.LocationDataTransferItem.SUPPLEMENTAL_DATA = radiusString;
                finish();
            }
        });
        spinner = findViewById(R.id.radiusSpinner);

        List<String> radiusList = new ArrayList<>();
        radiusList.add("10m");
        radiusList.add("100m");
        radiusList.add("1000m");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MapActivity.this, android.R.layout.simple_spinner_item, radiusList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        // If they click on the gps button: take them to their location, update the textfield with their current location (set text of searchbar)
        // when they load this activity, the searchbar will already have their location
        // if they delete it on purpose check for isEmpty
        // implement in search method and onclick for gps button
        // always update the searchbar so that we can confidently get the text from the searchbar and update our data in the location dialog
        // advanced feature (to do later): check how legitimate the location is

        initMap();
        search();
    }

    private void search() {
        Log.d(TAG, "starting search services");
        searchBar.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH || i == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == keyEvent.ACTION_DOWN || keyEvent.getAction() == keyEvent.KEYCODE_ENTER) {
                geoLocate();
            }
            return false;
        });
        gpsLocate.setOnClickListener(view -> {
            Log.d(TAG, "transferred to device location after clicking gps");
            getDeviceLocation();
        });
        hideKeyBoard();
    }

    private void geoLocate() {
        Log.d(TAG, "geolocating");
        String searchField = searchBar.getText().toString();
        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchField, 1);
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        }
        if (list.size() > 0) {
            Address address = list.get(0);
            Log.d(TAG, "Found address: " + address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
            map.clear();
            radius = 500;
            map.addCircle(new CircleOptions().center(new LatLng(address.getLatitude(), address.getLongitude())).radius(radius).strokeColor(Color.RED).fillColor(Color.argb(70, 150, 50, 50))).isClickable();
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (adapterView.getSelectedItem().equals("10m")) {
                        Log.d(TAG, "10m selected");
                        map.clear();
                        radius = 10;
                        map.addCircle(new CircleOptions().center(new LatLng(address.getLatitude(), address.getLongitude())).radius(radius).strokeColor(Color.RED).fillColor(Color.argb(70, 150, 50, 50))).isClickable();
                    } else if (adapterView.getSelectedItem().equals("100m")) {
                        Log.d(TAG, "100m selected");
                        map.clear();
                        radius = 100;
                        map.addCircle(new CircleOptions().center(new LatLng(address.getLatitude(), address.getLongitude())).radius(radius).strokeColor(Color.RED).fillColor(Color.argb(70, 150, 50, 50))).isClickable();
                    } else {
                        Log.d(TAG, "1000m selected");
                        map.clear();
                        radius = 1000;
                        map.addCircle(new CircleOptions().center(new LatLng(address.getLatitude(), address.getLongitude())).radius(radius).strokeColor(Color.RED).fillColor(Color.argb(70, 150, 50, 50))).isClickable();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    map.clear();
                    radius = 500;
                    map.addCircle(new CircleOptions().center(new LatLng(address.getLatitude(), address.getLongitude())).radius(radius).strokeColor(Color.RED).fillColor(Color.argb(70, 150, 50, 50))).isClickable();
                }
            });


        }
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getting the device's location");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            Task getLocation = fusedLocationProviderClient.getLastLocation();
            getLocation.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "found location");
                    Location myLocation = (Location) task.getResult();
                    if (myLocation != null) {
                        moveCamera(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), DEFAULT_ZOOM, "My Location");
                        map.clear();
                        radius = 500;
                        map.addCircle(new CircleOptions().center(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())).radius(radius).strokeColor(Color.RED).fillColor(Color.argb(70, 150, 50, 50))).isClickable();
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                if (adapterView.getSelectedItem().equals("10m")) {
                                    Log.d(TAG, "10m selected");
                                    map.clear();
                                    radius = 10;
                                    map.addCircle(new CircleOptions().center(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())).radius(radius).strokeColor(Color.RED).fillColor(Color.argb(70, 150, 50, 50))).isClickable();
                                } else if (adapterView.getSelectedItem().equals("100m")) {
                                    Log.d(TAG, "100m selected");
                                    map.clear();
                                    radius = 100;
                                    map.addCircle(new CircleOptions().center(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())).radius(radius).strokeColor(Color.RED).fillColor(Color.argb(70, 150, 50, 50))).isClickable();
                                } else {
                                    Log.d(TAG, "1000m selected");
                                    map.clear();
                                    radius = 1000;
                                    map.addCircle(new CircleOptions().center(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())).radius(radius).strokeColor(Color.RED).fillColor(Color.argb(70, 150, 50, 50))).isClickable();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                                map.clear();
                                radius = 500;
                                map.addCircle(new CircleOptions().center(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())).radius(radius).strokeColor(Color.RED).fillColor(Color.argb(70, 150, 50, 50))).isClickable();
                            }
                        });
                    } else {
                        Log.e("Location", "null"); //ALERT: LOOK AT THIS LATER
                    }
                } else {
                    Log.d(TAG, "current location is not found");
                    Toast.makeText(MapActivity.this, "unable to find current location", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (SecurityException s) {
            Log.e(TAG, "Security Exception: " + s.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moving camera to latitude " + latLng.latitude + " and longitude " + latLng.longitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if (!title.equals("My Location")) {
            MarkerOptions options = new MarkerOptions().position(latLng).title(title);
            map.addMarker(options);
        }
        hideKeyBoard();
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        map = googleMap;
        getDeviceLocation();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);

    }
}
