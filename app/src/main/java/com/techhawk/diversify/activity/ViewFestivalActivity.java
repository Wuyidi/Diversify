package com.techhawk.diversify.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.techhawk.diversify.R;
import com.techhawk.diversify.model.Holiday;
import com.techhawk.diversify.tools.TextJustification;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ViewFestivalActivity extends AppCompatActivity {
    private Holiday festival;
    private TextView header;
    private TextView history;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_festival);
        header = findViewById(R.id.festival_header);
        history = findViewById(R.id.festival_history);
        img = findViewById(R.id.img_festival_bg);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        festival = intent.getParcelableExtra("festival");
        getSupportActionBar().setTitle(festival.getName());
        loadImg(festival.getImgUrl());
        header.setText("History of " + festival.getName());
        //history.setText(new SpannableString(festival.getHistory()));
        history.setText(festival.getHistory());
        //TextJustification.justify(history);


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

    private void loadImg(String url) {
        Glide.with(this).load(url)
                .transition(withCrossFade())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(img);
    }
}
