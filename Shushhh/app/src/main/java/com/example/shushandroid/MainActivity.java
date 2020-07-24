package com.example.shushandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {

    public static String TAG = ShushObject.ShushObjectType.Location.getDescription();

    private ViewPager2 viewPager2;
    private CustomPagerAdapter adapter;
    private ArrayList<Fragment> arrayList;
    private TabLayout tabLayout;
    private FloatingActionButton floatingActionButton;

    private BottomAppBar bottomAppBar;
    private VoicemailBottomSheetDialogFragment voicemailBottomSheetDialogFragment;

    private PlaceTab placeTab;
    private TimeTab timeTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager2 = findViewById(R.id.tabviewpager);
        tabLayout = findViewById(R.id.tabview);
        bottomAppBar = findViewById(R.id.bottomappbar);

        voicemailBottomSheetDialogFragment = new VoicemailBottomSheetDialogFragment();
        arrayList = new ArrayList<>(Arrays.asList(new PlaceTab(), new TimeTab()));
        adapter = new CustomPagerAdapter(getSupportFragmentManager(), getLifecycle());

        timeTab = new TimeTab();
        placeTab = new PlaceTab();

        viewPager2.setAdapter(adapter);

        databaseTest();

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
                if (tab.getText().toString().equals(ShushObject.ShushObjectType.Location.getDescription())) {
                    TAG = ShushObject.ShushObjectType.Location.getDescription();
                } else if (tab.getText().toString().equals(ShushObject.ShushObjectType.Time.getDescription())) {
                    TAG = ShushObject.ShushObjectType.Time.getDescription();
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
            if (TAG.equals(ShushObject.ShushObjectType.Location.getDescription())) {
                DialogFragment dialog = LocationDialog.newInstance();
                dialog.show(getSupportFragmentManager(), "tag");
            } else if (TAG.equals(ShushObject.ShushObjectType.Time.getDescription())) {
                DialogFragment dialog = TimeDialog.newInstance();
                dialog.show(getSupportFragmentManager(), "tag");
            }
        });
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
            Log.i("Fragment", "Created");
            if (position == 0) {

                return new PlaceTab();
            }
            else if (position == 1) {

                return new TimeTab();
            }
            else {

                return new PlaceTab();
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }

    }


}

