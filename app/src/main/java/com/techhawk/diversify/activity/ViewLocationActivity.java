package com.techhawk.diversify.activity;


import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.techhawk.diversify.R;
import com.techhawk.diversify.model.CustomEvent;
import com.techhawk.diversify.model.Event;

import java.io.IOException;
import java.util.List;

import static com.techhawk.diversify.activity.CommentActivity.EXTRA_COMMENT_EVENT_KEY;

public class ViewLocationActivity extends BaseActivity implements OnMapReadyCallback {
    private BottomNavigationBar bottomNavigationBar;
    private String key;
    private String region;
    private LatLng location;
    private String address;
    private CustomEvent customEvent;
    private Event publicEvent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_location);
        bottomNavigationBar = findViewById(R.id.bottom_navigation_bar);
        key = this.getIntent().getStringExtra(EXTRA_COMMENT_EVENT_KEY);
        region = this.getIntent().getStringExtra("region");

        // Get access to our MapFragment
        MapFragment mapFrag = (MapFragment)
                getFragmentManager().findFragmentById(R.id.map_fragment);
        // Set up an asynchronous callback to let us know when the map has loaded
        mapFrag.getMapAsync(this);

        setUpBottomNavigationBar();
    }

    private void setUpBottomNavigationBar() {
        BottomNavigationItem item1 = new BottomNavigationItem(R.drawable.ic_event, R.string.tab_info);
        BottomNavigationItem item2 = new BottomNavigationItem(R.drawable.ic_comment, R.string.tab_comment);
        BottomNavigationItem item3 = new BottomNavigationItem(R.drawable.ic_map, R.string.tab_map);
        bottomNavigationBar.addItem(item1).addItem(item2).addItem(item3).initialise();
        bottomNavigationBar.selectTab(2, false);
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                if (position == 0) {
                    if (region != null) {
                        Intent intent = new Intent(ViewLocationActivity.this, ViewPublicEventActivity.class);
                        intent.putExtra(EXTRA_COMMENT_EVENT_KEY, key);
                        intent.putExtra("region", region);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(ViewLocationActivity.this, ViewCustomEventActivity.class);
                        intent.putExtra(ViewCustomEventActivity.EXTRA_EVENT_KEY, key);
                        startActivity(intent);
                        finish();
                    }

                } else if (position == 1) {
                    if (region != null) {
                        Intent intent = new Intent(ViewLocationActivity.this, CommentActivity.class);
                        intent.putExtra(CommentActivity.EXTRA_COMMENT_EVENT_KEY, key);
                        intent.putExtra("region",region);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(ViewLocationActivity.this, CommentActivity.class);
                        intent.putExtra(CommentActivity.EXTRA_COMMENT_EVENT_KEY, key);
                        startActivity(intent);
                        finish();
                    }

                }
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });
    }

    private LatLng getLocationFromAddress(Context context, String inputAddress) {
        Geocoder coder = new Geocoder(context);
        LatLng resLatLng = null;
        List<Address> address;
        try {
            // May throw an IOException
            address = coder.getFromLocationName(inputAddress, 5);
            if (address == null) {
                return null;
            }

            if (address.size() == 0) {
                return null;
            }

            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            resLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return resLatLng;
    }




    @Override
    public void onMapReady(final GoogleMap googleMap) {
        if (region == null) {
            DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("users/" + getUid() + "/events/" + key);
            final DatabaseReference publicRef = FirebaseDatabase.getInstance().getReference().child("custom_events").child(key);
            if (eventRef != null) {
                eventRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        customEvent = dataSnapshot.getValue(CustomEvent.class);
                        if (customEvent!=null) {
                            address = customEvent.getLocation();
                            location = getLocationFromAddress(getApplicationContext(),address);
                            googleMap.addMarker(new MarkerOptions().position(location)
                                    .title(customEvent.getName()));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
                        } else {
                            publicRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    customEvent = dataSnapshot.getValue(CustomEvent.class);
                                    address = customEvent.getLocation();
                                    location = getLocationFromAddress(getApplicationContext(),address);
                                    googleMap.addMarker(new MarkerOptions().position(location)
                                            .title(customEvent.getName()));
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        } else {
            final DatabaseReference publicEventRef = FirebaseDatabase.getInstance()
                    .getReference().child("events")
                    .child(region)
                    .child(key);
            publicEventRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    publicEvent = dataSnapshot.getValue(Event.class);
                    address = publicEvent.getLocation();
                    location = getLocationFromAddress(getApplicationContext(),address);
                    googleMap.addMarker(new MarkerOptions().position(location)
                            .title(publicEvent.getName()));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }

}
