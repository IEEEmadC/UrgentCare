package com.example.peterbrayo.urgentcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import static com.example.peterbrayo.urgentcare.UtilityFunctions.decodeFromFirebaseBase64;
import static com.example.peterbrayo.urgentcare.UtilityFunctions.getResizedBitmap;

public class VolunteerActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int clickedNavItemIndex;
    private int[] tabIcons = {R.drawable.ic_person_black_24dp,R.drawable.ic_chat_icon};
    private DrawerLayout mDrawerLayout;
    private String currentItemTitle;
    SharedPreferences sharedPreferences;
    String LOCATION = "location";
    TextView emailText;
    TextView nameText;
    ImageView imgView;
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer);

        FirebaseMessaging.getInstance().unsubscribeFromTopic("replyNotifications");
        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");

        sharedPreferences = getSharedPreferences(LOCATION, Context.MODE_PRIVATE);

        auth = FirebaseAuth.getInstance();

        //create an auth state listener
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = auth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(VolunteerActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        // add listener to auth
        auth.addAuthStateListener(authStateListener);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View navigationHeader;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);


        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        //setUpTabIcons();

        //these details saved from the ProfileFragment
        String nav_header_img = sharedPreferences.getString("nav_header_img", "");
        String nav_header_email = sharedPreferences.getString("nav_header_email", "");
        String nav_header_name = sharedPreferences.getString("nav_header_name", "");

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                setClickedItemsIndex(item.getItemId());
                getFragmentToBeLoaded();
                loadAppropriateFragment(item);
                return true;
            }
        });

        navigationHeader = navigationView.getHeaderView(0);
        emailText = navigationHeader.findViewById(R.id.nav_header_email_text);
        nameText = navigationHeader.findViewById(R.id.nav_header_name_text);
        imgView = navigationHeader.findViewById(R.id.nav_header_image);

        emailText.setText(nav_header_email);
        nameText.setText(nav_header_name);

        byte[] imageByteArray = Base64.decode(nav_header_img, Base64.DEFAULT);
        Glide.with(VolunteerActivity.this)
                .asBitmap()
                .load(imageByteArray)
                .into(imgView);

        //create hamburger menu icon and set listener to it
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);


        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ProfileFragment(), "Profile");
        adapter.addFragment(new ChatFragment(), "Chat Messages");
        viewPager.setAdapter(adapter);
    }

    private void setUpTabIcons(){
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.drawable.ic_menu_black_24dp){
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setClickedItemsIndex(int menuItemId){
        switch(menuItemId){
            case R.id.c_email:
                clickedNavItemIndex = 0;
                currentItemTitle = "Change Email";
                break;

            case R.id.c_password:
                clickedNavItemIndex = 1;
                currentItemTitle = "Change Password";
                break;

            case R.id.c_sign_out:
                clickedNavItemIndex = 2;
                currentItemTitle = "Sign Out";
                break;

            case R.id.delete_account:
                clickedNavItemIndex = 3;
                currentItemTitle = "Delete Account";
                break;

            case R.id.about_us:
                clickedNavItemIndex = 4;
                currentItemTitle = "About Us";
                break;

            case R.id.f_twitter:
                clickedNavItemIndex = 5;
                currentItemTitle = "Follow on twitter";
                break;

            case R.id.f_facebook:
                clickedNavItemIndex = 6;
                currentItemTitle = "Follow on facebook";
                break;

            default:
                clickedNavItemIndex = 0;
                currentItemTitle = "Change Email";
        }

    }

    public Fragment getFragmentToBeLoaded(){
        switch (clickedNavItemIndex){
            case 0:
                return  new ChangeEmailFragment();

            case 1:
                return new ResetPasswordFragment();

            default:
                return new ChangeEmailFragment();
        }
    }

    public void loadAppropriateFragment(MenuItem menuItem){

        if(clickedNavItemIndex == 0 || clickedNavItemIndex == 1) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.content_frame, getFragmentToBeLoaded());
            fragmentTransaction.commit();
        }
        else if (clickedNavItemIndex == 2){
            signOut();
        }

        menuItem.setChecked(true);
        mDrawerLayout.closeDrawers();
        tabLayout.setVisibility(View.GONE);
        toolbar.setTitle(currentItemTitle);
        viewPager.setVisibility(View.GONE);
    }

    public void signOut(){
        auth.signOut();
    }


}
