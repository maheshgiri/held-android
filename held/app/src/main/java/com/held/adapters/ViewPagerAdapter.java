package com.held.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.held.fragment.ActivityFeedFragment;
import com.held.fragment.DownloadRequestFragment;
import com.held.fragment.FriendRequestFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragments;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragments = new ArrayList<>();
        mFragments.add(new FriendRequestFragment());
        mFragments.add(new DownloadRequestFragment());
        mFragments.add(new ActivityFeedFragment());
        Log.i("Aadapter", "ViewPagerAdapter");
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
