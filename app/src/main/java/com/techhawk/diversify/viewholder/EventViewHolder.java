package com.techhawk.diversify.viewholder;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.techhawk.diversify.R;
import com.techhawk.diversify.model.Event;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Yidi Wu on 15/4/18.
 */

public abstract class EventViewHolder extends RecyclerView.ViewHolder {

    // Instance variable
    protected TextView nameView;
    protected TextView monthView;
    protected ImageView bgView;

    public EventViewHolder(View itemView) {
        super(itemView);
        // Get reference from activity
        nameView = itemView.findViewById(R.id.event_name);
        monthView = itemView.findViewById(R.id.event_month);
        bgView = itemView.findViewById(R.id.cardView_background);
    }

    // Bind text and image to event
    public abstract void bindToEvent(Event event, Context context);


}
