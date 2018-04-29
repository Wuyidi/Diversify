package com.techhawk.diversify.fragment;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.techhawk.diversify.R;
import com.techhawk.diversify.activity.ViewPublicEventActivity;
import com.techhawk.diversify.helper.GPSTracker;
import com.techhawk.diversify.model.Event;
import com.techhawk.diversify.viewholder.PublicEventViewHolder;

import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment {


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private GPSTracker gps;
    private Button button;

    private DatabaseReference eventRef;
    private RecyclerView eventView;
    private LinearLayoutManager manager;
    private FirebaseRecyclerAdapter<Event, PublicEventViewHolder> adapter;
    private static String[] REGIONS = new String[8];
    private int tag = 7;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        REGIONS = getResources().getStringArray(R.array.dropdown_menu_region);
        button = rootView.findViewById(R.id.btn_refresh);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        // Connect to database
        eventRef = FirebaseDatabase.getInstance().getReference().child("events");
        // Get reference from database
        eventView = rootView.findViewById(R.id.event_list);
        eventView.setHasFixedSize(true);
        // Set up Layout Manager, reverse layout
        manager = new LinearLayoutManager(getActivity());
        manager.setReverseLayout(false);
        manager.setStackFromEnd(false);
        eventView.setLayoutManager(manager);
        setUpAdapter(REGIONS[tag]);
        eventView.setAdapter(adapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading(eventView);
                gps = new GPSTracker(getActivity());
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();
                String address = getState(latitude, longitude);
                if (gps.isCanGetLocation()) {
                    swapTag(address);
                    setUpAdapter(REGIONS[tag]);
                    eventView.setAdapter(adapter);
                    feedback("Your location is " + address + "\n" + "Latitude: " + latitude + "   " + "Longitude: " + longitude);
                } else {
                    gps.showSettingAlert();
                }
            }
        });


        return rootView;
    }


    // Check the permission
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                    }
                } else {
                    // Permission denied, Disable the functionality that depends on this permission.
                    feedback("Permission denied");
                }
                return;
        }
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;
    }

    private String getState(double LATITUDE, double LONGITUDE) {
        String state = "";
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                state = addresses.get(0).getAdminArea();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return state;
    }

    private void setUpAdapter(String ref) {
        Query query = eventRef.child(ref).orderByChild("index").limitToLast(6);
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(query, Event.class)
                .setLifecycleOwner(this)
                .build();

        adapter = new FirebaseRecyclerAdapter<Event, PublicEventViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PublicEventViewHolder holder, int position, @NonNull final Event model) {
                holder.bindToEvent(model, getContext());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), ViewPublicEventActivity.class);
                        intent.putExtra("event", model);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public PublicEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_public_event_item, parent, false);
                return new PublicEventViewHolder(view);
            }

        };
    }

    private void swapTag(String state) {
        switch (state) {
            case "Australian Capital Territory":
                tag = 0;
                break;
            case "New South Wales":
                tag = 1;
                break;
            case "Northern Territory":
                tag = 2;
                break;
            case "Queensland":
                tag = 3;
                break;
            case  "South Australia":
                tag = 4;
                break;
            case "Tasmania":
                tag = 5;
                break;
            case "Victoria":
                tag = 6;
                break;
            case "Western Australia":
                tag = 7;
                break;
            default:
                tag = 0;
                return;
        }
    }
}
