package com.techhawk.diversify.fragment;


import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.techhawk.diversify.R;
import com.techhawk.diversify.model.Holiday;

/**
 * A simple {@link Fragment} subclass.
 */
public class FestivalFragment extends BaseFragment {

    // Instance variables
    private SearchView searchView;
    private ListView festivalView;
    private DatabaseReference festivalReference;

    public FestivalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View currentView = inflater.inflate(R.layout.fragment_festival, container, false);
        festivalView = currentView.findViewById(R.id.festival_list_view);
        // Connect to Firebase database
        festivalReference = FirebaseDatabase.getInstance().getReference().child("holidays");
        // Set up FirebaseListAdapter
        FirebaseListOptions<Holiday> options = new FirebaseListOptions.Builder<Holiday>()
                .setQuery(festivalReference, Holiday.class).setLayout(R.layout.list_festival_item).setLifecycleOwner(this).build();
        FirebaseListAdapter<Holiday> adapter = new FirebaseListAdapter<Holiday>(options) {
            @Override
            protected void populateView(View v, Holiday holiday, int position) {
                ((TextView) v.findViewById(R.id.list_festival_name)).setText(holiday.getName());
                ((TextView) v.findViewById(R.id.list_festival_comments)).setText(holiday.getComments());
            }
        };
        festivalView.setAdapter(adapter);

        festivalView.setEmptyView(currentView.findViewById(R.id.empty_festival));


        return currentView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_view, menu);
        setSearchView(menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // Set up search view
    private void setSearchView(Menu menu) {
        MenuItem item = menu.getItem(0);
        searchView = new SearchView(getContext());
        searchView.setIconifiedByDefault(true);
        searchView.setQueryHint("Festival Name");
        searchView.setSubmitButtonEnabled(false);
        item.setActionView(searchView);

    }

}
