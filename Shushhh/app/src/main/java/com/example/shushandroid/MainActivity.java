package com.example.shushandroid;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main Activity";
    private ViewPager2 viewPager2;
    private CustomPagerAdapter adapter;
    private ArrayList<Fragment> arrayList;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager2 = findViewById(R.id.tabviewpager);
        tabLayout = findViewById(R.id.tabview);

        arrayList = new ArrayList<>(Arrays.asList(new PlaceTab(), new TimeTab()));

        adapter = new CustomPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            if (position == 0) {
                tab.setText("Location");
            } else {
                tab.setText("Time");
            }
        }).attach();

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
        else return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
