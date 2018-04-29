package com.techhawk.diversify.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.TextView;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.techhawk.diversify.R;
import com.techhawk.diversify.activity.AuthMethodPickerActivity;
import com.techhawk.diversify.activity.CreateEventActivity;
import com.techhawk.diversify.activity.ViewCustomEventActivity;
import com.techhawk.diversify.model.CustomEvent;
import com.techhawk.diversify.model.Event;
import com.techhawk.diversify.viewholder.CustomEventViewHolder;
import com.techhawk.diversify.viewholder.PrivateEventViewHolder;
import com.techhawk.diversify.viewholder.PublicEventViewHolder;
import com.twiceyuan.dropdownmenu.ArrayDropdownAdapter;
import com.twiceyuan.dropdownmenu.DropdownMenu;
import com.twiceyuan.dropdownmenu.OnDropdownItemClickListener;

import static android.app.Activity.RESULT_OK;

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
    // Unique Identifier for receiving activity result
    public static final int ADD_EVENT_REQUEST = 1;
    private DatabaseReference eventRef;
    private RecyclerView eventView;
    private LinearLayoutManager manager;
    private FirebaseRecyclerAdapter<CustomEvent, CustomEventViewHolder> adapter;
    private FirebaseRecyclerAdapter<CustomEvent, PrivateEventViewHolder> adapter2;
    private static String[] TYPES = new String[2];
    private static int CURRENT_TYPE = 0;

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
        eventView = rootView.findViewById(R.id.event_list_view);
        eventView.setHasFixedSize(true);
        TYPES = getResources().getStringArray(R.array.dropdown_munu_type);
        final DropdownMenu typeMenu = rootView.findViewById(R.id.dm_dropdown);
        typeMenu.setAdapter(new ArrayDropdownAdapter(getContext(),R.layout.dropdown_light_item_oneline,TYPES));
        typeMenu.getListView().setDivider(ContextCompat.getDrawable(getContext(),R.drawable.inset_divider));
        typeMenu.getListView().setDividerHeight(1);

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
                    // Get reference from database
                    eventRef = FirebaseDatabase.getInstance().getReference().child("users/"+getUid()+"/events");
                    // Set up Layout Manager, reverse layout
                    manager = new LinearLayoutManager(getActivity());
                    manager.setReverseLayout(false);
                    manager.setStackFromEnd(false);
                    eventView.setLayoutManager(manager);
                    if (CURRENT_TYPE == 0) {
                        setUpAdapter(eventRef);
                        eventView.setAdapter(adapter2);
                    } else {
                        Query query = FirebaseDatabase.getInstance().getReference().child("custom_events");
                        setUpAdapter(query);
                        eventView.setAdapter(adapter);
                    }

                    typeMenu.setOnItemClickListener(new OnDropdownItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            CURRENT_TYPE = i;
                            if (i == 0) {
                                setUpAdapter(eventRef);
                                eventView.setAdapter(adapter2);
                            } else {
                                Query query = FirebaseDatabase.getInstance().getReference().child("custom_events");
                                setUpAdapter(query);
                                eventView.setAdapter(adapter);

                            }

                        }
                    });
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
                Intent intent = new Intent(this.getActivity(), CreateEventActivity.class);
                startActivityForResult(intent, ADD_EVENT_REQUEST);
                break;
            case R.id.btn_sign_in:
                startActivity(new Intent(this.getActivity(), AuthMethodPickerActivity.class));
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_EVENT_REQUEST) {
            if (resultCode == RESULT_OK) {
                feedback("Create Event Successfully");
            }
        }

    }

    private void setUpAdapter(Query query) {
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<CustomEvent>()
                .setQuery(query, CustomEvent.class)
                .setLifecycleOwner(this)
                .build();

        adapter = new FirebaseRecyclerAdapter<CustomEvent, CustomEventViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CustomEventViewHolder holder, final int position, @NonNull final CustomEvent model) {
                final String key = this.getRef(position).getKey();
                holder.bindToCustomEvent(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        buildDialog(model,holder.getAdapterPosition());
                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), ViewCustomEventActivity.class);
                        intent.putExtra(ViewCustomEventActivity.EXTRA_EVENT_KEY,key);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public CustomEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_custom_event_item,parent,false);
                return new CustomEventViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                emptyView.setVisibility(
                        adapter.getItemCount() == 0 ? View.VISIBLE : View.INVISIBLE
                );
            }
        };

        adapter2 = new FirebaseRecyclerAdapter<CustomEvent, PrivateEventViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final PrivateEventViewHolder holder, final int position, @NonNull final CustomEvent model) {
                final String key = this.getRef(position).getKey();
                holder.bindToCustomEvent(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        buildDialog(model,holder.getAdapterPosition());
                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), ViewCustomEventActivity.class);
                        intent.putExtra(ViewCustomEventActivity.EXTRA_EVENT_KEY,key);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public PrivateEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_custom_event_item,parent,false);
                return new PrivateEventViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                emptyView.setVisibility(
                        adapter.getItemCount() == 0 ? View.VISIBLE : View.INVISIBLE
                );
            }

            @Override
            public void onChildChanged(@NonNull ChangeEventType type, @NonNull DataSnapshot snapshot, int newIndex, int oldIndex) {
                super.onChildChanged(type, snapshot, newIndex, oldIndex);
            }
        };

    }

    // Delete a event from Firebase database
    private void deleteData(int position) {
        String key = adapter2.getRef(position).getKey();
        adapter2.getRef(position).removeValue();
        DatabaseReference publicRef = FirebaseDatabase.getInstance().getReference().child("custom_events/"+key);
        if (publicRef != null) {
            publicRef.setValue(null);
        }

    }

    // Build a dialog for deleting a event
    private void buildDialog(CustomEvent event, final int position) {
        // Build a dialog to delete item
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Event?");
        builder.setMessage("Are you sure you wish to delete " + event.getName() + "?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteData(position);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }
}
