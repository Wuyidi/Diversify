package com.techhawk.diversify.fragment;


import android.content.Intent;
import android.graphics.Color;
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
import android.widget.EditText;
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
import com.twiceyuan.dropdownmenu.MenuManager;
import com.twiceyuan.dropdownmenu.OnDropdownItemClickListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class FestivalFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    // Instance variables
    private SearchView searchView;
    private ListView festivalView;
    private DatabaseReference festivalRef;
    // list used to inflate adapter
    private final String[] TYPES = new String[] {"All","National","Regional", "Not Public"};
    // tags used to attach the filter
    private static final int TAG_ALL = 0;
    private static final int TAG_NATIONAL = 1;
    private static final int TAG_REGIONAL = 2;
    private static final int TAG_PUBLIC = 3;
    private static int CURRENT_TAG = TAG_ALL;

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
        final DropdownMenu category = (DropdownMenu) rootView.findViewById(R.id.dm_dropdown);
        category.setAdapter(new ArrayDropdownAdapter(getContext(),R.layout.dropdown_light_item_oneline,TYPES));
        category.getListView().setDivider(ContextCompat.getDrawable(getContext(),R.drawable.inset_divider));
        category.getListView().setDividerHeight(1);

        initListView();

        category.setOnItemClickListener(new OnDropdownItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CURRENT_TAG = i;
                if (CURRENT_TAG == 0) {
                    initListView();
                } else {
                    setUpListView();
                }
            }
        });
        festivalView.setEmptyView(rootView.findViewById(R.id.empty_festival));
        festivalView.setOnItemClickListener(this);
//        MenuManager.group(category,typeMenu);
        return rootView;
    }

    private void initListView() {
        // Connect to Firebase database
        festivalRef = FirebaseDatabase.getInstance().getReference().child("holidays");
        // Set up firebase list adapter
        Query query = festivalRef;
        FirebaseListAdapter<Holiday> adapter = initialFirebaseAdapter(query);
        festivalView.setAdapter(adapter);
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
                Query query = festivalRef
                        .orderByChild("name")
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
        EditText editText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        editText.setTextColor(Color.WHITE);
        item.setActionView(searchView);

    }

    // Set up list view
    private void setUpListView() {
        // Connect to Firebase database
        festivalRef = FirebaseDatabase.getInstance().getReference().child("holidays");
        // Set up FirebaseListAdapter
        Query query = festivalRef.orderByChild("type").equalTo(TYPES[CURRENT_TAG]);
        FirebaseListOptions<Holiday> options = new FirebaseListOptions.Builder<Holiday>()
                .setQuery(query, Holiday.class).setLayout(R.layout.list_festival_item).setLifecycleOwner(this).build();
        FirebaseListAdapter<Holiday> adapter = new FirebaseListAdapter<Holiday>(options) {
            @Override
            protected void populateView(View v, Holiday holiday, int position) {
                ((TextView) v.findViewById(R.id.list_festival_name)).setText(holiday.getName());
                ((TextView) v.findViewById(R.id.list_festival_comments)).setText(holiday.getComments());
            }
        };

        festivalView.setAdapter(adapter);
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
