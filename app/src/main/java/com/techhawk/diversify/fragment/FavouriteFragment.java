package com.techhawk.diversify.fragment;


import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.techhawk.diversify.R;
import com.techhawk.diversify.model.FavouriteEvent;
import com.twiceyuan.dropdownmenu.ArrayDropdownAdapter;
import com.twiceyuan.dropdownmenu.DropdownMenu;
import com.twiceyuan.dropdownmenu.OnDropdownItemClickListener;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteFragment extends BaseFragment {

    private TextView emptyView;
    private TextView noticeView;
    private ListView favouriteView;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private static String[] TYPES = new String[2];
    private static int CURRENT_TYPE = 0;
    private DatabaseReference publicRef;
    private DatabaseReference customRef;
    private FirebaseListAdapter<FavouriteEvent> publicAdapter;
    private FirebaseListAdapter<FavouriteEvent> customAapter;


    public FavouriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_favourtie, container, false);
        TYPES = getResources().getStringArray(R.array.dropdown_menu_category);

        // Get reference from fragment
        emptyView = rootView.findViewById(R.id.empty_favourite);
        noticeView = rootView.findViewById(R.id.login_notice);
        favouriteView = rootView.findViewById(R.id.favourite_view);
        final DropdownMenu typeMenu = rootView.findViewById(R.id.dm_dropdown);
        typeMenu.setAdapter(new ArrayDropdownAdapter(getContext(),R.layout.dropdown_light_item_oneline,TYPES));
        typeMenu.getListView().setDivider(ContextCompat.getDrawable(getContext(),R.drawable.inset_divider));
        typeMenu.getListView().setDividerHeight(1);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (user != null) {
                    noticeView.setVisibility(View.GONE);
                } else {
                    noticeView.setVisibility(View.VISIBLE);
                }
            }
        };
        if (getUid() != null) {
            publicRef = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("favourite_events")
                    .child(getUid())
                    .child("public_events");
            customRef = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("favourite_events")
                    .child(getUid())
                    .child("custom_events");

            publicRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (! dataSnapshot.hasChildren()) {
                        emptyView.setVisibility(View.VISIBLE);
                    } else {
                        emptyView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            customRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        emptyView.setVisibility(View.GONE);
                    } else {
                        emptyView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            setUpPublicAdapter(publicRef);
            favouriteView.setAdapter(publicAdapter);

            typeMenu.setOnItemClickListener(new OnDropdownItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i == 0) {
                        setUpPublicAdapter(publicRef);
                        favouriteView.setAdapter(publicAdapter);
                    } else {
                        setUpCustomAdapter(customRef);
                        favouriteView.setAdapter(customAapter);
                    }
                }
            });
        }


        auth.addAuthStateListener(authStateListener);
        return  rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }
    }

    private void setUpPublicAdapter(Query query) {
        FirebaseListOptions options = new FirebaseListOptions.Builder<FavouriteEvent>()
                .setQuery(query, FavouriteEvent.class)
                .setLayout(R.layout.list_favourite_items)
                .setLifecycleOwner(this).build();
        publicAdapter = new FirebaseListAdapter<FavouriteEvent>(options) {
            @Override
            protected void populateView(View v, FavouriteEvent model, int position) {
                final String key = this.getRef(position).getKey();
                ((TextView) v.findViewById(R.id.event_name)).setText(model.getName());
                ((TextView) v.findViewById(R.id.event_desc)).setText(model.getDesc());
                ((TextView) v.findViewById(R.id.event_date)).setText(model.getDate());
                Switch notification = v.findViewById(R.id.btn_notification);

            }
        };

    }

    private void setUpCustomAdapter(Query query) {
        FirebaseListOptions options = new FirebaseListOptions.Builder<FavouriteEvent>()
                .setQuery(query, FavouriteEvent.class)
                .setLayout(R.layout.list_favourite_items)
                .setLifecycleOwner(this).build();
        customAapter = new FirebaseListAdapter<FavouriteEvent>(options) {
            @Override
            protected void populateView(View v, FavouriteEvent model, int position) {
                final String key = this.getRef(position).getKey();
                ((TextView) v.findViewById(R.id.event_name)).setText(model.getName());
                ((TextView) v.findViewById(R.id.event_desc)).setText(model.getDesc());
                ((TextView) v.findViewById(R.id.event_date)).setText(model.getDate());
                Switch notification = v.findViewById(R.id.btn_notification);



            }
        };
    }


    private void setNotification(String date) {
        String[] dateParts = date.split("-");
        int day = Integer.parseInt(dateParts[0]);
        int month = (Integer.parseInt(dateParts[1])>0)? Integer.parseInt(dateParts[1])-1:0;
        int year = Integer.parseInt(dateParts[2]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,day);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 5);
        calendar.set(Calendar.AM_PM, Calendar.PM);
    }
}
