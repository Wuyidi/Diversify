package com.techhawk.diversify.fragment;


import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techhawk.diversify.R;
import com.techhawk.diversify.adapter.EventPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment {

    // Instance variable
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public EventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Set a scroll flag for toolbar
        // https://stackoverflow.com/questions/30771156/how-to-set-applayout-scrollflags-for-toolbar-programmatically
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        AppBarLayout.LayoutParams params =
                (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_event, container, false);
        viewPager = rootView.findViewById(R.id.view_pager);
        tabLayout = getActivity().findViewById(R.id.tab_layout);
        setUpViewPager(viewPager);
        setupTabLayout(tabLayout);
        return rootView;
    }

    private void setUpViewPager(ViewPager viewPager) {
        EventPagerAdapter adapter = new EventPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new PublicEventFragment(), "Public Events");
        adapter.addFragment(new CustomEventFragment(), "Custom Events");
        viewPager.setAdapter(adapter);
    }

    // Set up a tab
    // https://stackoverflow.com/questions/32238343/using-navigationview-with-tablayout
    private void setupTabLayout(TabLayout tabLayout) {
        tabLayout.setVisibility(View.VISIBLE);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onResume() {
        super.onResume();
        tabLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        tabLayout.setVisibility(View.GONE);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        AppBarLayout.LayoutParams params =
                (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
    }

}
