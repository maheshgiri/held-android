package com.held.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.held.activity.R;
import com.held.adapters.ViewPagerAdapter;

public class NotificationFragment extends ParentFragment {

    public static final String TAG = NotificationFragment.class.getSimpleName();
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;

    public static NotificationFragment newInstance() {
        return new NotificationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    protected void initialiseView(View view, Bundle savedInstanceState) {
        mTabLayout = (TabLayout) view.findViewById(R.id.NOTIFY_tab_layout);
        mTabLayout.addTab(mTabLayout.newTab().setText("Friend Requests"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Download Requests"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Activity Feed"));
        mViewPager = (ViewPager) view.findViewById(R.id.NOTIFY_view_pager);
        mViewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void bindListeners(View view) {

    }

    @Override
    public void onClicked(View v) {

    }
}
