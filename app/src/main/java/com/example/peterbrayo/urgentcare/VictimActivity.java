package com.example.peterbrayo.urgentcare;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class VictimActivity extends AppCompatActivity {
    Toolbar toolbar;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_victim);

        auth = FirebaseAuth.getInstance();
        FirebaseMessaging.getInstance().unsubscribeFromTopic("pushNotifications");

        ViewPager viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        VictimActivity.ViewPagerAdapter adapter = new VictimActivity.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ListFragment(), "Volunteers");
        adapter.addFragment(new MapFragment(), "Map");
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();



        if (auth.getCurrentUser() != null){
            auth.signOut();
        }

        auth.signInAnonymously();

        FirebaseMessaging.getInstance().subscribeToTopic("replyNotifications");
    }

    @Override
    protected void onPause() {
        super.onPause();

        auth.signOut();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (auth.getCurrentUser() != null){
            auth.signOut();
        }

        auth.signInAnonymously();

        FirebaseMessaging.getInstance().subscribeToTopic("replyNotifications");
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.signOut();
    }
}
