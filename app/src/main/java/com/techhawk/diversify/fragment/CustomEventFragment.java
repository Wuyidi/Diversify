package com.techhawk.diversify.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techhawk.diversify.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomEventFragment extends Fragment {


    public CustomEventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_custom_event, container, false);
    }

}
