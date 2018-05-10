package com.techhawk.diversify.activity;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.techhawk.diversify.R;
import com.techhawk.diversify.helper.GmailSender;
import com.techhawk.diversify.model.CustomEvent;
import com.techhawk.diversify.model.FavouriteEvent;

public class ViewCustomEventActivity extends BaseActivity {

    // Unique Identifier for receiving activity result
    public static final String EXTRA_EVENT_KEY = "event_key";

    private DatabaseReference eventRef;
    private DatabaseReference publicRef;
    private String eventKey;
    private CustomEvent event;
    private TextView titleField;
    private TextView descField;
    private TextView dateField;
    private TextView locationField;
    private TextView contactField;
    private MenuItem edit;
    private boolean canEdit;
    private BottomNavigationBar bottomNavigationBar;
    private DatabaseReference favouriteRef;
    private ToggleButton favouriteBtn;
    private Button shareButton;


    // Unique Identifier for receiving activity result
    public static final int EDIT_EVENT_REQUEST = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_custom_event);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Get reference from activity
        titleField = findViewById(R.id.event_title);
        descField = findViewById(R.id.event_desc);
        dateField = findViewById(R.id.event_date);
        locationField = findViewById(R.id.event_location);
        contactField = findViewById(R.id.event_contact);
        bottomNavigationBar = findViewById(R.id.bottom_navigation_bar);
        favouriteBtn = findViewById(R.id.btn_favourite);
        shareButton = findViewById(R.id.btn_share);
        // Connect to the database
        eventKey = getIntent().getStringExtra(EXTRA_EVENT_KEY);
        eventRef = FirebaseDatabase.getInstance().getReference().child("users/" + getUid() + "/events/" + eventKey);
        publicRef = FirebaseDatabase.getInstance().getReference().child("custom_events").child(eventKey);
        if (eventRef != null) {
            eventRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    event = dataSnapshot.getValue(CustomEvent.class);

                    if (event != null) {
                        canEdit = true;
                        setText();

                    } else {
                        publicRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                event = dataSnapshot.getValue(CustomEvent.class);
                                canEdit = false;
                                setText();

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        favouriteRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("favourite_events")
                .child(getUid())
                .child("custom_events")
                .child(eventKey);

        favouriteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    favouriteBtn.setChecked(true);
                } else {
                    favouriteBtn.setChecked(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        favouriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (favouriteBtn.isChecked()) {
                    addFavourite();
                } else {
                    removeFavourite();
                }
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildDialog();
            }
        });


        setUpBottomNavigationBar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }

        if (id == R.id.edit_event) {
            Intent intent = new Intent(this,EditCustomEventActivity.class);
            intent.putExtra(ViewCustomEventActivity.EXTRA_EVENT_KEY,eventKey);
            startActivityForResult(intent, EDIT_EVENT_REQUEST);

        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_EVENT_REQUEST) {
            if (resultCode == RESULT_OK) {
                feedback("Edit Event Successfully");
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_view, menu);
        return  true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        edit = menu.findItem(R.id.edit_event);
        edit.setVisible(canEdit);
        return super.onPrepareOptionsMenu(menu);
    }

    private void setText() {
        if (event != null) {
            titleField.setText(event.getName());
            dateField.setText(event.getDate());
            locationField.setText(event.getLocation());
            descField.setText(event.getDescription());
            contactField.setText(event.getContact());
        }

    }



    private void setUpBottomNavigationBar() {
        BottomNavigationItem item1 = new BottomNavigationItem(R.drawable.ic_event, R.string.tab_info);
        BottomNavigationItem item2 = new BottomNavigationItem(R.drawable.ic_comment, R.string.tab_comment);
        BottomNavigationItem item3 = new BottomNavigationItem(R.drawable.ic_map, R.string.tab_map);
        bottomNavigationBar.addItem(item1).addItem(item2).addItem(item3).initialise();
        bottomNavigationBar.selectTab(0, false);
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                if (position == 1) {
                    Intent intent = new Intent(ViewCustomEventActivity.this,CommentActivity.class);
                    intent.putExtra(CommentActivity.EXTRA_COMMENT_EVENT_KEY, eventKey);
                    startActivity(intent);
                    finish();
                } else if (position == 2) {
                    Intent intent = new Intent(ViewCustomEventActivity.this,ViewLocationActivity.class);
                    intent.putExtra(CommentActivity.EXTRA_COMMENT_EVENT_KEY, eventKey);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });
    }

    private void addFavourite() {
        FavouriteEvent favouriteEvent = new FavouriteEvent();
        favouriteEvent.setName(event.getName());
        favouriteEvent.setDate(event.getDate());
        favouriteEvent.setDesc(event.getDescription());
        favouriteEvent.setLocation(event.getLocation());

        favouriteRef.setValue(favouriteEvent);
    }

    private void removeFavourite() {
        favouriteRef.removeValue();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bottomNavigationBar.selectTab(0,false);

    }

    private void buildDialog() {
        final Dialog dialog = new Dialog(ViewCustomEventActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_edit_emial_dialog);
        dialog.setCancelable(false);
        Button sendButton = dialog.findViewById(R.id.btn_send);
        Button cancelButton = dialog.findViewById(R.id.btn_cancel);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText inputEmail = dialog.findViewById(R.id.input_email);
                EditText inputName = dialog.findViewById(R.id.input_name);
                String email = inputEmail.getText().toString().trim();
                String name = inputName.getText().toString().trim();
                sendEmail(event, email,name);
                dialog.cancel();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        dialog.show();

    }


    private void sendEmail(final CustomEvent event, final String toEmail, final String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    GmailSender sender = new GmailSender("techhawks.diversify@gmail.com",
                            "techhawks");

                    String body = "Hello,\n\n" + name
                            + " invite you attend "
                            + event.getName() + " " + event.getDate()
                            +" in "
                            + event.getLocation() + ".";
                    sender.sendMail("Hello from TechHawks.Diversify", body,
                            "techhawks.diversify@gmail.com", toEmail);
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
            }

        }).start();

    }

}
