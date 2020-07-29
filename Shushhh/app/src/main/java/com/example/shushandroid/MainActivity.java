package com.example.shushandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
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

        viewPager2.setAdapter(adapter);

        databaseManager = new DatabaseManager(this);
        Log.i("DB", "" + databaseManager.retrieveWithTAG(ShushObject.ShushObjectType.TIME.getDescription()).toString());

        bottomAppBar.setNavigationOnClickListener((View v) -> {
            voicemailBottomSheetDialogFragment.show(getSupportFragmentManager(), "dialog_fragment");
        });

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            if (position == 0) {
                tab.setText("Location");
            } else {
                tab.setText("Time");
            }
        }).attach();

        bottomAppBar.findViewById(R.id.bottomappbar);

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


        floatingActionButton = findViewById(R.id.floatingactionbutton);
        floatingActionButton.setOnClickListener(v -> {
            if (TAG.equals(ShushObject.ShushObjectType.LOCATION.getDescription())) {
                DialogFragment dialog = LocationDialog.newInstance();
                dialog.show(getSupportFragmentManager(), ShushObject.ShushObjectType.LOCATION.getDescription());
            } else if (TAG.equals(ShushObject.ShushObjectType.TIME.getDescription())) {
                TimeDialog dialog = TimeDialog.newInstance();
                dialog.show(getSupportFragmentManager(), ShushObject.ShushObjectType.TIME.getDescription(), "fab");
            }
        });

        try {
            alarmTest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static class VoicemailBottomSheetDialogFragment extends BottomSheetDialogFragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.bottom_sheet_voicemail, container, false);
            getDialog().setCanceledOnTouchOutside(true);
            return view;
        }
    }

    class CustomPagerAdapter extends FragmentStateAdapter {

        public CustomPagerAdapter(@NonNull FragmentManager fm, @NonNull Lifecycle lifecycle) {
            super(fm, lifecycle);
        }

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

    public void alarmTest () throws InterruptedException {
        Intent intent = new Intent(this, AudioManagerBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0); // study this
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pendingIntent);
        Toast.makeText(this, "Execute in 5000 ms", Toast.LENGTH_LONG).show();
        long t = System.currentTimeMillis();
        while (System.currentTimeMillis() < t + 5000) {
            Log.i("Broadcast", "Loading...");
            Thread.sleep(1000);
        }
    }


}

