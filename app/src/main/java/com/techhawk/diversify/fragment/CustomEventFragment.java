package com.techhawk.diversify.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.techhawk.diversify.R;
import com.techhawk.diversify.activity.AuthMethodPickerActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomEventFragment extends BaseFragment implements View.OnClickListener {

    private FloatingActionButton createButton;
    private FloatingActionButton signInButton;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener authStateListener;
    private TextView emptyView;
    private TextView noticeView;



    public CustomEventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_custom_event, container, false);
        createButton = rootView.findViewById(R.id.btn_create);
        signInButton = rootView.findViewById(R.id.btn_sign_in);
        emptyView = rootView.findViewById(R.id.empty_event);
        noticeView = rootView.findViewById(R.id.login_notice);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (user == null) {
                    signInButton.hide(false);
                    createButton.hide(true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            signInButton.show(true);
                            signInButton.setShowAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.show_from_bottom));
                        }
                    }, 300);
                    emptyView.setVisibility(View.GONE);
                    noticeView.setVisibility(View.VISIBLE);

                } else {
                    signInButton.hide(true);
                    createButton.hide(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            createButton.show(true);
                            createButton.setShowAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.show_from_bottom));
                        }
                    }, 300);
                    noticeView.setVisibility(View.GONE);
                }
            }
        };

        signInButton.setOnClickListener(this);
        createButton.setOnClickListener(this);
        return  rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null)
            auth.removeAuthStateListener(authStateListener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_create:
                break;
            case R.id.btn_sign_in:
                startActivity(new Intent(this.getActivity(), AuthMethodPickerActivity.class));
                break;
        }
    }
}
