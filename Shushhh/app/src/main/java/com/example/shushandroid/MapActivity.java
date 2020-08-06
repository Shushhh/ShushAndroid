package com.example.shushandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FloatingActionButton checkFloatingActionButton;
    private ImageView gpsLocateImageView;
    private EditText searchEditText;
    private Spinner spinner;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location myLocation;
    private GoogleMap map;

    private static final String TAG = "MapActivity";
    private static final float DEFAULT_ZOOM = 15f;
    private int radius = 10;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        radius = 10;

        searchEditText = findViewById(R.id.searchTextField);
        gpsLocateImageView = findViewById(R.id.currentLocationButton);
        checkFloatingActionButton = findViewById(R.id.checkFloatingActionButton);

        String apiKey = getString(R.string.google_maps_API_key);

        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        searchEditText.setFocusable(false);
        searchEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(MapActivity.this);
                startActivityForResult(intent, 100);
            }
        });

        checkFloatingActionButton.setOnClickListener(view -> {
            if (!searchEditText.getText().toString().isEmpty()) {
                String radiusString = radius + "m";
                Intent intent = new Intent();
                Log.i("Data", searchEditText.getText().toString());
                intent.putExtra(TimeDialog.LocationDataTransferItem.LOCATION, searchEditText.getText().toString());
                intent.putExtra(TimeDialog.LocationDataTransferItem.RADIUS, radiusString);
                setResult(Activity.RESULT_OK, intent);
                finish();
            } else {
                new AlertDialog.Builder(MapActivity.this).setTitle("No Location Found")
                        .setMessage("Please search for a valid location so that our services can mute your phone appropriately.")
                        .setPositiveButton("Ok", null)
                        .create().show();
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

        spinner.setSelection(0);

        // If they click on the gps button: take them to their location, update the textfield with their current location (set text of searchbar)
        // when they load this activity, the searchbar will already have their location
        // if they delete it on purpose check for isEmpty
        // implement in search method and onclick for gps button
        // always update the searchbar so that we can confidently get the text from the searchbar and update our data in the location dialog
        // advanced feature (to do later): check how legitimate the location is

        initMap();
        search();

        // string has the location data from the dialog activity
        if (getIntent().getStringExtra("Location") != null) {
            Log.i("Location", getIntent().getStringExtra("Location"));
            searchEditText.setText(getIntent().getStringExtra("Location"));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            searchEditText.setText(place.getAddress());
            geoLocate();
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void search() {
        Log.d(TAG, "starting search services");
        searchEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH || i == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == keyEvent.ACTION_DOWN || keyEvent.getAction() == keyEvent.KEYCODE_ENTER) {
                geoLocate();
            }
            return false;
        });
        gpsLocateImageView.setOnClickListener(view -> {
            Log.d(TAG, "transferred to device location after clicking gps");
            getDeviceLocation();
        });
        hideKeyBoard();
    }

    private void geoLocate() {
        Log.d(TAG, "geolocating");
        String searchField = searchEditText.getText().toString();
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
                    radius = 10;
                    map.addCircle(new CircleOptions().center(new LatLng(address.getLatitude(), address.getLongitude())).radius(radius).strokeColor(Color.RED).fillColor(Color.argb(70, 150, 50, 50))).isClickable();
                }
            });


        }
    }

    private void getDeviceLocation() {
        Log.i("Context", this.toString());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            Task getLocation = fusedLocationProviderClient.getLastLocation();
            getLocation.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "found location");
                    myLocation = (Location) task.getResult();
                    if (myLocation != null) {
                        String cityName = null;
                        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
                        List<Address> addresses;
                        try {
                            addresses = gcd.getFromLocation(myLocation.getLatitude(),
                                    myLocation.getLongitude(), 1);
                            if (addresses.size() > 0) {
                                System.out.println(addresses.get(0).getLocality());
                                cityName = addresses.get(0).getAddressLine(0);
                            }
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        searchEditText.setText(cityName);
                        moveCamera(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), DEFAULT_ZOOM, "My Location");
                        map.clear();
                        radius = 10;
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
                                radius = 10;
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
        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
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

    public Integer getRadius () {
        return radius;
    }

}
