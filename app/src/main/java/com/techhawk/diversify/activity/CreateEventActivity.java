package com.techhawk.diversify.activity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.techhawk.diversify.R;
import com.techhawk.diversify.model.CustomEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateEventActivity extends BaseActivity implements View.OnClickListener{

    private CustomEvent event;
    private DatabaseReference eventRef;
    private EditText inputName;
    private EditText inputLocation;
    private EditText inputDescription;
    private DatePicker inputDate;
    private Button saveButton;
    private Switch switchButton;
    private static final String ERROR = "Required";
    private boolean isPublic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        hideKeyboard();
        eventRef = FirebaseDatabase.getInstance().getReference().child("users/"+getUid()+"/events");
        inputName = findViewById(R.id.event_name);
        inputLocation = findViewById(R.id.event_location);
        inputDescription = findViewById(R.id.event_description);
        inputDate = findViewById(R.id.event_date);
        saveButton = findViewById(R.id.btn_save);
        switchButton = findViewById(R.id.btn_switch);
        saveButton.setOnClickListener(this);


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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == saveButton.getId()) {
            save();
        }

    }

    private boolean validForm() {
        boolean valid = true;
        String name = inputName.getText().toString().trim();
        String desc = inputDescription.getText().toString().trim();
        String location = inputLocation.getText().toString().trim();
        // Validate user input
        if (TextUtils.isEmpty(name)) {
            inputName.setError(ERROR);
            valid = false;
        }else {
            inputName.setError(null);
        }

        if (TextUtils.isEmpty(desc)) {
            inputDescription.setError(ERROR);
            valid = false;
        }else {
            inputDescription.setError(null);
        }

        if (TextUtils.isEmpty(location)) {
            inputLocation.setError(ERROR);
            valid = false;
        }else {
            inputLocation.setError(null);
        }


        return  valid;
    }

    private void createNewEvent(String name, String location, String date, String desc) {
        showProgressDialog();
        event = new CustomEvent();
        event.setDate(date);
        event.setName(name);
        event.setDescription(desc);
        event.setLocation(location);
        event.setContact(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        isPublic = switchButton.isChecked();

        if (isPublic) {
            DatabaseReference publicEventRef = FirebaseDatabase.getInstance().getReference().child("custom_events");
            String key = eventRef.push().getKey();
            eventRef.child(key).setValue(event, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        feedback("Event couldn't be saved " + databaseError.getMessage());
                    } else {
                        feedback("Event saved successfully");
                    }
                    hideProgressDialog();
                }
            });
            publicEventRef.child(key).setValue(event, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        feedback("Event couldn't be saved " + databaseError.getMessage());
                    } else {
                        feedback("Event saved successfully");
                    }
                    hideProgressDialog();
                }
            });

        } else {
            eventRef.push().setValue(event, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        feedback("Event couldn't be saved " + databaseError.getMessage());
                    } else {
                        feedback("Event saved successfully");
                    }
                    hideProgressDialog();
                }
            });
        }
    }

    private void save() {
        if (validForm()) {
            String name = inputName.getText().toString().trim();
            String desc = inputDescription.getText().toString().trim();
            String location = inputLocation.getText().toString().trim();
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            int day = inputDate.getDayOfMonth();
            int month = inputDate.getMonth();
            int year = inputDate.getYear();
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            String formattedDate = dateFormat.format(calendar.getTime());

            createNewEvent(name,location,formattedDate,desc);

            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }

}
