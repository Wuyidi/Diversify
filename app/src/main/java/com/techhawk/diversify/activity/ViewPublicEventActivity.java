package com.techhawk.diversify.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.techhawk.diversify.R;
import com.techhawk.diversify.helper.GmailSender;
import com.techhawk.diversify.model.CustomEvent;
import com.techhawk.diversify.model.Event;
import com.techhawk.diversify.model.FavouriteEvent;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ViewPublicEventActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {

    // Instance variable
    private Event event;
    private TextView commentsText;
    private TextView celebrationText;
    private ImageView img;
    private String key;
    private DatabaseReference eventRef;
    private BottomNavigationBar bottomNavigationBar;
    private String region;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ToggleButton favouriteBtn;
    private DatabaseReference favouriteRef;
    private Button shareButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_public_event);

        // Initialization
        commentsText = findViewById(R.id.event_comments);
        img = findViewById(R.id.img_event_bg);
        celebrationText = findViewById(R.id.event_celebration);
        bottomNavigationBar = findViewById(R.id.bottom_navigation_bar);
        favouriteBtn = findViewById(R.id.btn_favourite);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        key = intent.getStringExtra(CommentActivity.EXTRA_COMMENT_EVENT_KEY);
        region = intent.getStringExtra("region");
        eventRef = FirebaseDatabase.getInstance().getReference().child("events").child(region).child(key);
//        event = intent.getParcelableExtra("event");
        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                event = dataSnapshot.getValue(Event.class);
                getSupportActionBar().setTitle(event.getName());
                loadImg(event.getImgUrl());
                commentsText.setText(event.getComments());
                celebrationText.setText(event.getCelebration());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Require permission
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (user == null) {
                    favouriteBtn.setVisibility(View.GONE);
                } else {
                    favouriteBtn.setVisibility(View.VISIBLE);
                }
            }
        };

        auth.addAuthStateListener(authStateListener);

        favouriteRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("favourite_events")
                .child(getUid())
                .child("public_events")
                .child(key);

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

       shareButton = findViewById(R.id.btn_share);
       shareButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               buildDialog();
           }
       });

        setUpBottomNavigationBar();

    }

    private void loadImg(String url) {
        Glide.with(this).load(url)
                .transition(withCrossFade())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(img);
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
        } else if (id == R.id.calendar) {
            String date = event.getDate();
            String[] dateParts = date.split("-");
            int day = Integer.parseInt(dateParts[0]);
            int month = (Integer.parseInt(dateParts[1])>0)? Integer.parseInt(dateParts[1])-1:0;
            int year = Integer.parseInt(dateParts[2]);
            DatePickerDialog dialog = new DatePickerDialog(ViewPublicEventActivity.this,this ,year,month,day);
            dialog.show();
            dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setVisibility(View.GONE);
            dialog.setCancelable(true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.calendar_view, menu);
        return  true;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

    }


    private void setUpBottomNavigationBar() {
        BottomNavigationItem item1 = new BottomNavigationItem(R.drawable.ic_event, R.string.tab_info);
        BottomNavigationItem item2 = new BottomNavigationItem(R.drawable.ic_comment, R.string.tab_comment);
        BottomNavigationItem item3 = new BottomNavigationItem(R.drawable.ic_map, R.string.tab_map);
        bottomNavigationBar.addItem(item1).addItem(item2).addItem(item3).initialise();
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                if (position == 1) {
                    Intent intent = new Intent(ViewPublicEventActivity.this,CommentActivity.class);
                    intent.putExtra(CommentActivity.EXTRA_COMMENT_EVENT_KEY,key);
                    intent.putExtra("region", region);
                    startActivity(intent);
                    finish();
                } else if (position == 2) {
                    Intent intent = new Intent(ViewPublicEventActivity.this,ViewLocationActivity.class);
                    intent.putExtra(CommentActivity.EXTRA_COMMENT_EVENT_KEY, key);
                    intent.putExtra("region", region);
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
        favouriteEvent.setDesc(event.getComments());
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

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }
    }

    private void buildDialog() {
        final Dialog dialog = new Dialog(ViewPublicEventActivity.this);
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


    private void sendEmail(final Event event, final String toEmail, final String name) {
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
