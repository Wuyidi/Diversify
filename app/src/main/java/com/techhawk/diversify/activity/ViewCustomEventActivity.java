package com.techhawk.diversify.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.techhawk.diversify.R;
import com.techhawk.diversify.model.CustomEvent;

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
    boolean canEdit;
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
        titleField.setText(event.getName());
        dateField.setText(event.getDate());
        locationField.setText(event.getLocation());
        descField.setText(event.getDescription());
        contactField.setText(event.getContact());
    }
}
