package com.techhawk.diversify.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.techhawk.diversify.R;
import com.techhawk.diversify.model.Event;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ViewPublicEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    // Instance variable
    private Event event;
//    private TextView headerText;
    private TextView commentsText;
    private TextView celebrationText;
    private ImageView img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_public_event);

        // Initialization
//        headerText = findViewById(R.id.event_header);
        commentsText = findViewById(R.id.event_comments);
        img = findViewById(R.id.img_event_bg);
        celebrationText = findViewById(R.id.event_celebration);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();

        event = intent.getParcelableExtra("event");
        getSupportActionBar().setTitle(event.getName());
        loadImg(event.getImgUrl());
//        headerText.setText(event.getName());
        commentsText.setText(event.getComments());
        celebrationText.setText(event.getCelebration());

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
            int month = Integer.parseInt(dateParts[1]);
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
}
