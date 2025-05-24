package com.example.wogprideanalog;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager2 viewPager = findViewById(R.id.view_pager);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (viewPager == null || bottomNavigationView == null) {
            throw new IllegalStateException("ViewPager2 or BottomNavigationView not found in activity_main.xml");
        }

        PagerAdapter pagerAdapter = new PagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Синхронізація BottomNavigationView з ViewPager2
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                viewPager.setCurrentItem(0);
            } else if (itemId == R.id.nav_store) {
                viewPager.setCurrentItem(1);
            } else if (itemId == R.id.nav_map) {
                viewPager.setCurrentItem(2);
            } else if (itemId == R.id.nav_profile) {
                viewPager.setCurrentItem(3);
            }
            return true;
        });

        // Синхронізація ViewPager2 з BottomNavigationView
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.nav_home);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.nav_store);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.nav_map);
                        break;
                    case 3:
                        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
                        break;
                }
            }
        });
    }
}