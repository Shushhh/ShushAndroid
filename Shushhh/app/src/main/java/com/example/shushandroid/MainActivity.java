package com.example.shushandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Build;
import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;

/**
 * @apiNote Main Activity class
 * @author  Sahil Sudhir and Akash Veerappan
 * @version 1.0
 * @since   2020-7-18
 */

public class MainActivity extends AppCompatActivity {

    public static class PermissionRequestCodes {
        public static final int PERMISSION_FINE_LOCATION = 44;
        public static final int PERMISSION_READ_PHONE_STATE = 99;
    }

    public static String TAG = ShushObject.ShushObjectType.LOCATION.getDescription();
    private final String CHECK = "MainActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    private ViewPager2 viewPager2;
    private CustomPagerAdapter adapter;
    private TabLayout tabLayout;
    private FloatingActionButton floatingActionButton;

    private BottomAppBar bottomAppBar;
    private VoicemailBottomSheetDialogFragment voicemailBottomSheetDialogFragment;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager2 = findViewById(R.id.tabviewpager);
        tabLayout = findViewById(R.id.tabview);
        bottomAppBar = findViewById(R.id.bottomappbar);

        voicemailBottomSheetDialogFragment = new VoicemailBottomSheetDialogFragment();

        adapter = new CustomPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager2.setAdapter(adapter);

        bottomAppBar.setNavigationOnClickListener((View v) -> {
            voicemailBottomSheetDialogFragment.show(getSupportFragmentManager(), getResources().getString(R.string.bottom_sheet));
        });

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            if (position == 0) {
                tab.setText("Location");
            } else {
                tab.setText("Time");
            }
        }).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().toString().equals(ShushObject.ShushObjectType.LOCATION.getDescription())) {
                    TAG = ShushObject.ShushObjectType.LOCATION.getDescription();
                } else if (tab.getText().toString().equals(ShushObject.ShushObjectType.TIME.getDescription())) {
                    TAG = ShushObject.ShushObjectType.TIME.getDescription();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if (isServicesOK()) {
            init();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init() {

        floatingActionButton = findViewById(R.id.floatingactionbutton);

        floatingActionButton.setOnClickListener(v -> {
            /*
             * If the tag is location, then check if the permission is not granted, if not (when the app is launched for the first time)
             * then then there will be no need to provide a further explanation, so ask for the user permission and if not granted and
             * when the fab is clicked again, then provide further information as it had been denied previously and on "Ok" click,
             * request permission again.
             */
            if (TAG.equals(ShushObject.ShushObjectType.LOCATION.getDescription())) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) { // shows after denial
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Location Permission")
                                .setMessage("To set location constraints to silence your phone, we will need to access your location in the foreground. Note that all location data stays in your phone, thereby protecting your privacy.")
                                .setPositiveButton("Ok", (DialogInterface dialogInterface, int i) -> {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PermissionRequestCodes.PERMISSION_FINE_LOCATION);
                                }).create().show();
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PermissionRequestCodes.PERMISSION_FINE_LOCATION);
                    }
                } else {
                    LocationDialog locationDialog = new LocationDialog();
                    locationDialog.show(getSupportFragmentManager(), ShushObject.ShushObjectType.LOCATION.getDescription());
                }
            } else if (TAG.equals(ShushObject.ShushObjectType.TIME.getDescription())) {
                TimeDialog dialog = TimeDialog.newInstance();
                dialog.show(getSupportFragmentManager(), ShushObject.ShushObjectType.TIME.getDescription(), "fab");
            }
        });

    }

    //will use after fixing current error
    public boolean isServicesOK() {
        Log.d(CHECK, "isServicesOK: Checking Google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //Connection is find and user can make requests
            Log.d(CHECK, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //error occurred but is fixable
            Log.d(CHECK, "isServicesOK: error occurred but is fixable");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            //unknown error and is not resolvable
            Toast.makeText(this, "Unable to make map requests",Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /**
     * @param requestCode respective requestCode for different permissions
     * @param permissions list of permissions requested for
     * @param grantResults results of the permission requests
     * @implNote runs after the user clicks on either of the several options when permission dialog is shown
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionRequestCodes.PERMISSION_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    LocationDialog locationDialog = new LocationDialog();
                    locationDialog.show(getSupportFragmentManager(), ShushObject.ShushObjectType.LOCATION.getDescription());
                }
            }
        }
    }

    public void serviceTest() {
        Intent intent = new Intent(this, ForegroundServiceManager.class);
        startService(intent);
    }

    public static class VoicemailBottomSheetDialogFragment extends BottomSheetDialogFragment {

        private LinearLayout settingsView, feedbackView, aboutUsView;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);
            getDialog().setCanceledOnTouchOutside(true);

            settingsView = view.findViewById(R.id.settingsView);
            feedbackView = view.findViewById(R.id.feedback_view);
            aboutUsView = view.findViewById(R.id.about_us_view);

            settingsView.setOnClickListener(v -> {
                if (getActivity() != null)
                    getActivity().startActivityForResult(new Intent(getActivity(), SettingsActivity.class), 10);
            });

            return view;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("Result", "Activity");
        if (requestCode == 10) {
            if (resultCode == 21) {
                Log.i("Result", "result");
                String s = this.getSharedPreferences(getPackageName(), MODE_PRIVATE).getString(getResources().getString(R.string.settings_radio_string), null);
                if (s != null) {
                    Log.i("Result", s);
                }
            }
        }

    }

    class CustomPagerAdapter extends FragmentStateAdapter {

        /**
         *
         * @param fm
         * @param lifecycle
         */
        public CustomPagerAdapter(@NonNull FragmentManager fm, @NonNull Lifecycle lifecycle) {
            super(fm, lifecycle);
        }

        /**
         *
         * @param position
         * @return
         */
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0)
                return new PlaceTab();
            else if (position == 1)
                return new TimeTab();
            else return new PlaceTab();
        }

        @Override
        public int getItemCount() {
            return 2;
        }

    }

}

