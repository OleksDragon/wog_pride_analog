package com.example.wogprideanalog;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.wogprideanalog.ui.fragments.HomeFragment;
import com.example.wogprideanalog.ui.fragments.MapFragment;
import com.example.wogprideanalog.ui.fragments.ProfileFragment;
import com.example.wogprideanalog.ui.fragments.StoreFragment;

public class PagerAdapter extends FragmentStateAdapter {

    private static final int NUM_TABS = 4;

    public PagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new StoreFragment();
            case 2:
                return new MapFragment();
            case 3:
                return new ProfileFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return NUM_TABS;
    }
}