package com.techhawk.diversify.activity;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.techhawk.diversify.R;
import com.techhawk.diversify.fragment.EventFragment;
import com.techhawk.diversify.fragment.FestivalFragment;
import com.techhawk.diversify.fragment.HomeFragment;
import com.techhawk.diversify.fragment.FavouriteFragment;
import com.techhawk.diversify.fragment.SettingFragment;
import com.techhawk.diversify.helper.AlarmReceiver;
import com.techhawk.diversify.model.FavouriteEvent;
import com.techhawk.diversify.model.User;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


public class MainActivity extends BaseActivity {

    // instance variable
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View navHeader;
    private ImageView imgNavHeaderBg;
    private TextView name;
    private Toolbar toolbar;
    // urls to load navigation header background image
    // private static final String urlNavHeaderBg = "https://api.androidhive.info/images/nav-menu-header-bg.jpg";
    private static final String urlNavHeaderBg = "https://3esi-enersight.com/wp-content/uploads/2017/06/3esi-Enersight-Energy-Navigator-Reserves-Management-Solution-Header-background-desktop.jpg";
    // index to identify current nav menu item
    public static int navItemIndex = 0;
    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_FESTIVAL = "festival";
    private static final String TAG_SETTINGS = "settings";
    private static final String TAG_FAVOURITE = "favourite";
    private static final String TAG_EVENT = "event";
    public static String CURRENT_TAG = TAG_HOME;
    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;
    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private ImageView profilePicker;
    private StorageReference filePath;
    // Unique Identifier for receiving activity result
    private static final int PICK_IMAGE = 100;
    private Uri imageUri;
    private User currentUser;
    private DatabaseReference userRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get reference from activity
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);

        mHandler = new Handler();

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        name = navHeader.findViewById(R.id.user_name);
        profilePicker = navHeader.findViewById(R.id.profile_pick);
        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        if (getUid() != null) {
            filePath = FirebaseStorage.getInstance().getReference().child("photos").child(getUid());
            userRef = FirebaseDatabase.getInstance().getReference().child("users").child(getUid());
        }


        // Load nav menu header
        loadNavHeader();
        // initializing navigation menu
        setupDrawerContent();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadFragment();
        }

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (user == null) {
                    profilePicker.setVisibility(View.GONE);

                } else {
                    if (getUid() != null) {
                        filePath = FirebaseStorage.getInstance().getReference().child("photos/"+getUid());
                        profilePicker.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                openGallery();
                            }
                        });
                    }
                    name.setText(user.getEmail());

                }

            }
        };
        auth.addAuthStateListener(authStateListener);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        int i = preferences.getInt("numOfLaunch",1);
        if (i<2) {
            validateNotification();
        }
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        gallery.setType("image/*");
        startActivityForResult(gallery, PICK_IMAGE);
    }

    private void loadNavHeader() {
        // loading header background image
        Glide.with(this).load(urlNavHeaderBg)
                .transition(withCrossFade())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(imgNavHeaderBg);
        // showing dot next to favourite label
//        navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
        if (userRef != null) {
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currentUser = dataSnapshot.getValue(User.class);
                    if (currentUser != null && ! currentUser.getImgUrl().isEmpty()) {
                        Glide.with(getBaseContext()).load(currentUser.getImgUrl()).into(profilePicker);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        profilePicker.setImageResource(R.drawable.ic_profile);

    }

    // Switch fragment by navigation drawer
    private void setupDrawerContent() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //Check to see which item was being clicked and perform appropriate action
                switch (item.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_festival:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_FESTIVAL;
                        break;
                    case R.id.nav_event:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_EVENT;
                        break;
                    case R.id.nav_favourite:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_FAVOURITE;
                        break;
                    case R.id.nav_setting:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_SETTINGS;
                        break;
                    case R.id.nav_about:
                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        drawerLayout.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;

                }
                //Checking if the item is in checked state or not, if not make it in checked state
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                item.setChecked(true);

                loadFragment();

                return true;
            }

        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

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
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

    }

    private void loadFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }
        //Closing drawer on item click
        drawerLayout.closeDrawers();
        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                Fragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                // festival
                Fragment festivalFragment = new FestivalFragment();
                return festivalFragment;
            case 2:
                // event
                Fragment eventFragment = new EventFragment();
                return eventFragment;
            case 3:
                // favourites
                Fragment favouriteFragment = new FavouriteFragment();
                return favouriteFragment;
            case 4:
                // settings
                Fragment settingFragment = new SettingFragment();
                return settingFragment;
            default:
                return new HomeFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadFragment();
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // when fragment is favourite, load the menu created for favourite
        if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.favourite, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        // user is in favourite fragment
        // and selected 'Mark all as Read'
        if (id == R.id.action_clear) {
            if (getUid() != null) {
                DatabaseReference reference = FirebaseDatabase.getInstance()
                        .getReference()
                        .child("favourite_events")
                        .child(getUid());
                reference.removeValue();
                feedback("Clear All!");
            } else {
                feedback("please login!");
            }
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null)
            auth.removeAuthStateListener(authStateListener);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            profilePicker.setImageURI(imageUri);
            // Upload image to the Firebase storage
            // https://firebase.google.com/docs/storage/android/upload-files
            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String url = taskSnapshot.getDownloadUrl().toString();
                    userRef.child("imgUrl").setValue(url);
                    feedback("Upload photo done.");

                }
            });

        }
    }


    private void setReminder(boolean b) {
        AlarmManager am= (AlarmManager) getSystemService(ALARM_SERVICE);

        PendingIntent pi= PendingIntent.getBroadcast(MainActivity.this, 0, new Intent(this,AlarmReceiver.class), 0);
        if(b){
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.HOUR,0);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.AM_PM,Calendar.AM);
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pi);
        }
        else{
            // cancel current alarm
            am.cancel(pi);
        }

    }

    private void validateNotification() {

        if (getUid() != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("favourite_events")
                    .child(getUid());

            DatabaseReference pubRef = reference.child("public_events");
            DatabaseReference customRef = reference.child("custom_events");
            pubRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String currentDate = dateFormat.format(Calendar.getInstance().getTime());
                    for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                        FavouriteEvent event = dataSnapshot1.getValue(FavouriteEvent.class);
                        String date = event.getDate();
                        if (date.equals(currentDate)) {
                            setReminder(true);
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            customRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String currentDate = dateFormat.format(Calendar.getInstance().getTime());
                    for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                        FavouriteEvent event = dataSnapshot1.getValue(FavouriteEvent.class);
                        String date = event.getDate();
                        if (date.equals(currentDate)) {
                            setReminder(true);
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }

}
