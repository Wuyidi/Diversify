package com.techhawk.diversify.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.techhawk.diversify.R;
import com.techhawk.diversify.model.CustomEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EditCustomEventActivity extends BaseActivity {

    private CustomEvent event;
    private DatabaseReference eventRef;
    private DatabaseReference publicRef;
    private EditText inputName;
    private EditText inputLocation;
    private EditText inputDescription;
    private DatePicker inputDate;
    private Button saveButton;
    private Switch switchButton;
    private static final String ERROR = "Required";
    private boolean isPublic;
    private String key;
    private Button uploadButton;
    private Uri imageUri;
    private StorageReference filePath;

    private static final int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_custom_event);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inputName = findViewById(R.id.event_name);
        inputLocation = findViewById(R.id.event_location);
        inputDescription = findViewById(R.id.event_description);
        inputDate = findViewById(R.id.event_date);
        saveButton = findViewById(R.id.btn_save);
        switchButton = findViewById(R.id.btn_switch);
        key = getIntent().getStringExtra("event_key");
        filePath = FirebaseStorage.getInstance().getReference().child("event").child(key);
        eventRef = FirebaseDatabase.getInstance().getReference().child("users/"+getUid()+"/events").child(key);
        publicRef = FirebaseDatabase.getInstance().getReference().child("custom_events").child(key);
        publicRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                event = dataSnapshot.getValue(CustomEvent.class);
                if (event != null) {
                    isPublic = true;
                    setUI();
                } else {
                    eventRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            event = dataSnapshot.getValue(CustomEvent.class);
                            isPublic = false;
                            setUI();
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
        uploadButton = findViewById(R.id.btn_upload);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
        hideKeyboard();

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

    private void setUI() {
        if (event != null) {
            inputName.setText(event.getName(), TextView.BufferType.EDITABLE);
            inputDescription.setText(event.getDescription(), TextView.BufferType.EDITABLE);
            inputLocation.setText(event.getLocation(),TextView.BufferType.EDITABLE);
            switchButton.setChecked(isPublic);
            String date = event.getDate();
            String[] dateParts = date.split("-");
            int day = Integer.parseInt(dateParts[0]);
            int month = (Integer.parseInt(dateParts[1])>0)? Integer.parseInt(dateParts[1])-1:0;
            int year = Integer.parseInt(dateParts[2]);
            inputDate.updateDate(year,month,day);
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

            eventRef.setValue(event, new DatabaseReference.CompletionListener() {
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
            publicRef.setValue(event, new DatabaseReference.CompletionListener() {
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
            eventRef.setValue(event, new DatabaseReference.CompletionListener() {
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

            publicRef.setValue(null);
        }
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        gallery.setType("image/*");
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            // Upload image to the Firebase storage
            // https://firebase.google.com/docs/storage/android/upload-files
            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    feedback("Upload image done.");

                }
            });

        }
    }
}
