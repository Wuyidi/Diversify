package com.techhawk.diversify.fragment;


import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.techhawk.diversify.R;
import com.techhawk.diversify.activity.ViewFestivalActivity;
import com.techhawk.diversify.model.Holiday;
import com.twiceyuan.dropdownmenu.ArrayDropdownAdapter;
import com.twiceyuan.dropdownmenu.DropdownMenu;

/**
 * A simple {@link Fragment} subclass.
 */
public class FestivalFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    // Instance variables
    private SearchView searchView;
    private ListView festivalView;
    private DatabaseReference festivalRef;

    private final String[] COUNTRIES = new String[] {"Australia", "China"};

    public FestivalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_festival, container, false);
        festivalView = rootView.findViewById(R.id.festival_list_view);
        // Set countries filter
        final DropdownMenu countriesMenu = (DropdownMenu) rootView.findViewById(R.id.dm_dropdown);
        countriesMenu.setAdapter(new ArrayDropdownAdapter(getContext(),R.layout.dropdown_light_item_oneline,COUNTRIES));
        countriesMenu.getListView().setDivider(ContextCompat.getDrawable(getContext(),R.drawable.inset_divider));
        countriesMenu.getListView().setDividerHeight(1);

        // Connect to Firebase database
        festivalRef = FirebaseDatabase.getInstance().getReference().child("holidays");
        // Set up FirebaseListAdapter
        FirebaseListAdapter adapter = initialFirebaseAdapter(festivalRef);

        festivalView.setAdapter(adapter);
        festivalView.setEmptyView(rootView.findViewById(R.id.empty_festival));
        festivalView.setOnItemClickListener(this);

        return rootView;
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
        // Create an onQueryText Listener Anonymous class
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Make first query text upper case
                if (newText.length() > 0) {
                    newText = newText.substring(0, 1).toUpperCase() + newText.substring(1);
                }
                Query query = festivalRef.orderByChild("name")
                        .startAt(newText)
                        .endAt(String.valueOf(newText) + "\uf8ff");

                FirebaseListAdapter adapter = initialFirebaseAdapter(query);
                festivalView.setAdapter(adapter);

                return true;
            }
        });
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


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ListView listView = (ListView) adapterView;
        Holiday holiday = (Holiday) listView.getItemAtPosition(i);
        Intent intent = new Intent(getActivity(), ViewFestivalActivity.class);
        intent.putExtra("festival", holiday);
        startActivity(intent);
    }

    private FirebaseListAdapter initialFirebaseAdapter(Query query) {
        FirebaseListOptions<Holiday> options = new FirebaseListOptions.Builder<Holiday>()
                .setQuery(query, Holiday.class).setLayout(R.layout.list_festival_item).setLifecycleOwner(this).build();
        FirebaseListAdapter<Holiday> adapter = new FirebaseListAdapter<Holiday>(options) {
            @Override
            protected void populateView(View v, Holiday holiday, int position) {
                ((TextView) v.findViewById(R.id.list_festival_name)).setText(holiday.getName());
                ((TextView) v.findViewById(R.id.list_festival_comments)).setText(holiday.getComments());
            }
        };
        return adapter;
    }
}
