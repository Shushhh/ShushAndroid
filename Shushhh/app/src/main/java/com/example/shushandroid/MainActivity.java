package com.example.shushandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationManager;
import android.content.Intent;

import android.os.Build;
import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import android.widget.LinearLayout;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
        public static final int PERMISSION_BACKGROUND_LOCATION = 99;
    }

    private final String CHECK = "MainActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    private FloatingActionButton floatingActionButton;

    private BottomAppBar bottomAppBar;
    private VoicemailBottomSheetDialogFragment voicemailBottomSheetDialogFragment;

    static private RecyclerView recyclerView;
    static private ArrayList<ShushObject> shushObjectArrayList = new ArrayList<>();
    static private DatabaseManager databaseManager;
    static private ShushRecyclerAdapter shushRecyclerAdapter;

    private boolean isFineLocationGranted = false;
    private boolean isBackgroundLocationGranted = false;

    private boolean isBackFromBottomMenu = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomAppBar = findViewById(R.id.bottomappbar);

        voicemailBottomSheetDialogFragment = new VoicemailBottomSheetDialogFragment();

        bottomAppBar.setNavigationOnClickListener((View v) -> {
            voicemailBottomSheetDialogFragment.show(getSupportFragmentManager(), getResources().getString(R.string.bottom_sheet));
        });

        recyclerView = findViewById(R.id.recyclerView);
        shushRecyclerAdapter = new ShushRecyclerAdapter(shushObjectArrayList, getSupportFragmentManager());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        databaseManager = new DatabaseManager(this);

        updateRecyclerView();

        if (isServicesOK()) {
            init();
        }

        requestAudioPermissions();

        Intent intent = new Intent(this, ForegroundServiceManager.class);
        startService(intent);
    }

    public void requestAudioPermissions () {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted()) {
            new AlertDialog.Builder(this)
                    .setTitle("Grant Notification access")
                    .setMessage("In order for Shush to silence and de-silence your phone, it will need notification policy access.")
                    .setPositiveButton("Grant Access", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .create().show();
        }
    }

    public static void updateRecyclerView () {
        shushObjectArrayList = databaseManager.retrieveWithCursor();
        shushRecyclerAdapter.setShushObjectArrayList(shushObjectArrayList);
        recyclerView.setAdapter(shushRecyclerAdapter);
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
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.i("Permissions", "Not granted - fine");
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
                isFineLocationGranted = true;
                Log.i("Permissions", "Granted - fine");
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    isBackgroundLocationGranted = true;
                    Log.i("Permissions", "Granted - Background");
                } else {
                    Log.i("Permissions", "Not granted - Background");
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) { // shows after denial
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Location Permission")
                                .setMessage("To set location constraints to silence your phone, we will need to access your location in the background. Note that all location data stays in your phone, thereby protecting your privacy.")
                                .setPositiveButton("Ok", (DialogInterface dialogInterface, int i) -> {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, PermissionRequestCodes.PERMISSION_BACKGROUND_LOCATION);
                                }).create().show();
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, PermissionRequestCodes.PERMISSION_BACKGROUND_LOCATION);
                    }
                }

                if (isFineLocationGranted && isBackgroundLocationGranted) {
                    ShushDialog timeDialog = new ShushDialog();
                    timeDialog.show(getSupportFragmentManager(), "");
                }
            } else {
                if (isFineLocationGranted) {
                    ShushDialog timeDialog = new ShushDialog();
                    timeDialog.show(getSupportFragmentManager(), "");
                }
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
        if (requestCode == PermissionRequestCodes.PERMISSION_BACKGROUND_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ShushDialog timeDialog = new ShushDialog();
                timeDialog.show(getSupportFragmentManager(), "");
                isBackgroundLocationGranted = true;
            }
        }
        if (requestCode == PermissionRequestCodes.PERMISSION_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) { // shows after denial
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Location Permission")
                                        .setMessage("To set location constraints to silence your phone, we will need to access your location in the background to monitor if you are within vicinity of constrained location. Note that all location data stays in your phone, thereby protecting your privacy.")
                                        .setPositiveButton("Ok", (DialogInterface dialogInterface, int i) -> {
                                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, PermissionRequestCodes.PERMISSION_BACKGROUND_LOCATION);
                                        }).create().show();
                            } else {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, PermissionRequestCodes.PERMISSION_BACKGROUND_LOCATION);
                            }
                        }
                    } else {
                        ShushDialog timeDialog = new ShushDialog();
                        timeDialog.show(getSupportFragmentManager(), "");
                        isBackgroundLocationGranted = true;
                    }
                }
            }
        }

    }

    public static class VoicemailBottomSheetDialogFragment extends BottomSheetDialogFragment {

        private LinearLayout settingsView, feedbackView, aboutUsView;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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

            feedbackView.setOnClickListener(v -> {
                getActivity().startActivity(new Intent (getActivity(), FeedbackActivity.class));
            });

            aboutUsView.setOnClickListener(v -> {
                getActivity().startActivity(new Intent(getActivity(), AboutPage.class));
            });

            return view;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onCancel(@NonNull DialogInterface dialog) {
            super.onCancel(dialog);
            getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.colorAccentDarker));
        }
    }
}

