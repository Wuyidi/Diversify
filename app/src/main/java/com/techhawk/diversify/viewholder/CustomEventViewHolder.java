package com.techhawk.diversify.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.StorageReference;
import com.techhawk.diversify.R;
import com.techhawk.diversify.helper.GlideApp;
import com.techhawk.diversify.model.CustomEvent;

/**
 * Created by Yidi Wu on 22/4/18.
 */

public class CustomEventViewHolder extends RecyclerView.ViewHolder {
    // Instance variable
    protected TextView title;
    protected TextView desc;
    protected Button deleteButton;
    protected ImageView bgView;

    public CustomEventViewHolder(View itemView) {
        super(itemView);
        // Get reference from activity
        title = itemView.findViewById(R.id.event_name);
        desc = itemView.findViewById(R.id.event_desc);
        deleteButton = itemView.findViewById(R.id.btn_delete);
        bgView = itemView.findViewById(R.id.cardView_background);
        deleteButton.setVisibility(View.GONE);
    }

    // bind button and text view to CustomEvent
    public void bindToCustomEvent(CustomEvent event,Context context, StorageReference ref, View.OnClickListener clickListener) {
        title.setText(event.getName());
        desc.setText(event.getDate());
        deleteButton.setOnClickListener(clickListener);
        loadImage(ref,context);
    }

    private void loadImage(StorageReference ref, Context context) {
        RequestOptions options = new RequestOptions().error(R.drawable.card_view_bg);
        GlideApp.with(context).load(ref).apply(options).into(bgView);
    }



}
