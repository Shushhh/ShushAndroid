package com.example.shushandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
 * @resources
 */
public class MainActivity extends AppCompatActivity {

    public static String TAG = ShushObject.ShushObjectType.LOCATION.getDescription();

    private ViewPager2 viewPager2;
    private CustomPagerAdapter adapter;
    private ArrayList<Fragment> fragmentArrayList;
    private TabLayout tabLayout;
    private FloatingActionButton floatingActionButton;

    private BottomAppBar bottomAppBar;
    private VoicemailBottomSheetDialogFragment voicemailBottomSheetDialogFragment;

    private DatabaseManager databaseManager;
    private PlaceTab placeTab;
    private TimeTab timeTab;

    FusedLocationProviderClient fusedLocationProviderClient;


    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager2 = findViewById(R.id.tabviewpager);
        tabLayout = findViewById(R.id.tabview);
        bottomAppBar = findViewById(R.id.bottomappbar);

        voicemailBottomSheetDialogFragment = new VoicemailBottomSheetDialogFragment();
        databaseManager = new DatabaseManager(this);

        adapter = new CustomPagerAdapter(getSupportFragmentManager(), getLifecycle());

        timeTab = new TimeTab();
        placeTab = new PlaceTab();

        viewPager2.setAdapter(adapter);

        databaseManager = new DatabaseManager(this);
        Log.i("DB", "" + databaseManager.retrieveWithTAG(ShushObject.ShushObjectType.TIME.getDescription()).size());

        /**
         *
         */
        bottomAppBar.setNavigationOnClickListener((View v) -> {
            voicemailBottomSheetDialogFragment.show(getSupportFragmentManager(), "dialog_fragment");
        });

        /**
         *
         */
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            if (position == 0) {
                tab.setText("Location");
            } else {
                tab.setText("Time");
            }
        }).attach();

        bottomAppBar.findViewById(R.id.bottomappbar);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            /**
             *
             * @param tab
             */
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().toString().equals(ShushObject.ShushObjectType.LOCATION.getDescription())) {
                    TAG = ShushObject.ShushObjectType.LOCATION.getDescription();
                } else if (tab.getText().toString().equals(ShushObject.ShushObjectType.TIME.getDescription())) {
                    TAG = ShushObject.ShushObjectType.TIME.getDescription();
                }
            }

            /**
             *
             * @param tab
             */
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            /**
             *
             * @param tab
             */
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        floatingActionButton = findViewById(R.id.floatingactionbutton);
        /**
         *
         */
        floatingActionButton.setOnClickListener(v -> {
            if (TAG.equals(ShushObject.ShushObjectType.LOCATION.getDescription())) {
                //Check Settings -> if you deny too many times, it will remain denied and the notification won't pop up anymore
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Log.i("Test", "Working");
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
                DialogFragment dialog = LocationDialog.newInstance();
                dialog.show(getSupportFragmentManager(), ShushObject.ShushObjectType.LOCATION.getDescription());
            } else if (TAG.equals(ShushObject.ShushObjectType.TIME.getDescription())) {
                TimeDialog dialog = TimeDialog.newInstance();
                dialog.show(getSupportFragmentManager(), ShushObject.ShushObjectType.TIME.getDescription(), "fab");
            }
        });
    }

    /**
     *
     */
    public static class VoicemailBottomSheetDialogFragment extends BottomSheetDialogFragment {
        /**
         *
         * @param inflater
         * @param container
         * @param savedInstanceState
         * @return
         */
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.bottom_sheet_voicemail, container, false);
            getDialog().setCanceledOnTouchOutside(true);
            return view;
        }
    }

    /**
     *
     */
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

        /**
         *
         * @return
         */
        @Override
        public int getItemCount() {
            return 2;
        }

    }


}

