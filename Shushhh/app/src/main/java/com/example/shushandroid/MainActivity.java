package com.example.shushandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main Activity";
    private ViewPager2 viewPager2;
    private CustomPagerAdapter adapter;
    private ArrayList<Fragment> fragmentArrayList;
    private TabLayout tabLayout;

    private BottomAppBar bottomAppBar;
    private VoicemailBottomSheetDialogFragment voicemailBottomSheetDialogFragment;

    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager2 = findViewById(R.id.tabviewpager);
        tabLayout = findViewById(R.id.tabview);
        bottomAppBar = findViewById(R.id.bottomappbar);

        voicemailBottomSheetDialogFragment = new VoicemailBottomSheetDialogFragment();

        fragmentArrayList = new ArrayList<>(Arrays.asList(new PlaceTab(), new TimeTab()));
        databaseManager = new DatabaseManager(this);

        adapter = new CustomPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager2.setAdapter(adapter);

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

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//               if (tab.getText().toString().equals(ShushObject.ShushObjectType.LOCATION.getDescription())) {
//                   ((PlaceTab) fragmentArrayList.get(0)).setShushObjectArrayList(databaseManager.retrieve());
//               } else if (tab.getText().toString().equals(ShushObject.ShushObjectType.TIME.getDescription())) {
//                   ((TimeTab) fragmentArrayList.get(1)).setShushObjectArrayList(databaseManager.retrieve());
//               }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        bottomAppBar.findViewById(R.id.bottomappbar);

        //databaseTest();

    }

    /**
     * @apiNote use method to test the functionality of the database
     */
    public void databaseTest() {
        DatabaseManager databaseManager = new DatabaseManager(getApplicationContext());
        System.out.println(databaseManager.insert(new ShushObject("this", "that", "that", "this")));
        List l = databaseManager.retrieve();
        Log.i("Database Information", String.valueOf(l));
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

}
