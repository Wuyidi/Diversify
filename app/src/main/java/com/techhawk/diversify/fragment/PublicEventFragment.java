package com.techhawk.diversify.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.techhawk.diversify.R;
import com.techhawk.diversify.activity.ViewPublicEventActivity;
import com.techhawk.diversify.model.Event;
import com.techhawk.diversify.viewholder.PublicEventViewHolder;
import com.twiceyuan.dropdownmenu.ArrayDropdownAdapter;
import com.twiceyuan.dropdownmenu.DropdownMenu;
import com.twiceyuan.dropdownmenu.OnDropdownItemClickListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class PublicEventFragment extends BaseFragment {

    // Instance variable
    private DatabaseReference eventRef;
    private RecyclerView eventView;
    private LinearLayoutManager manager;
    private FirebaseRecyclerAdapter<Event, PublicEventViewHolder> adapter;
    private TextView emptyView;
    // tags used to attach the filter
    private static final int TAG_VIC = 6;
    private static int CURRENT_TAG = TAG_VIC;
    // list used to inflate adapter
    private static String[] REGIONS = new String[8];


    public PublicEventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_public_event, container, false);
        // Initialization
        REGIONS = getResources().getStringArray(R.array.dropdown_menu_region);
        final DropdownMenu regionMenu = rootView.findViewById(R.id.dm_dropdown);
        regionMenu.setAdapter(new ArrayDropdownAdapter(getContext(), R.layout.dropdown_light_item_oneline, REGIONS));
        regionMenu.getListView().setDivider(ContextCompat.getDrawable(getContext(), R.drawable.inset_divider));
        emptyView = rootView.findViewById(R.id.empty_event);
        regionMenu.getListView().setDividerHeight(1);
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
        loading(eventView);
        setUpAdapter("VIC");
        eventView.setAdapter(adapter);
        regionMenu.setOnItemClickListener(new OnDropdownItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CURRENT_TAG = i;
                setUpAdapter(REGIONS[CURRENT_TAG]);
                eventView.setAdapter(adapter);
            }
        });

        return rootView;
    }

    private void setUpAdapter(String ref) {
        Query query = eventRef.child(ref).orderByChild("index");
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

            @Override
            public void onDataChanged() {
                emptyView.setVisibility(
                        adapter.getItemCount() == 0 ? View.VISIBLE : View.INVISIBLE
                );
            }
        };
    }


}
