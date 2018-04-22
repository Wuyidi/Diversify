package com.techhawk.diversify.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.techhawk.diversify.R;
import com.techhawk.diversify.model.CustomEvent;

/**
 * Created by Yidi Wu on 22/4/18.
 */

public class CustomEventViewHolder extends RecyclerView.ViewHolder {
    // Instance variable
    private TextView title;
    private TextView desc;
    private Button deleteButton;

    public CustomEventViewHolder(View itemView) {
        super(itemView);
        // Get reference from activity
        title = itemView.findViewById(R.id.event_name);
        desc = itemView.findViewById(R.id.event_desc);
        deleteButton = itemView.findViewById(R.id.btn_delete);
    }

    // bind button and text view to CustomEvent
    public void bindToCustomEvent(CustomEvent event, View.OnClickListener clickListener) {
        title.setText(event.getName());
        desc.setText(event.getDescription());
        deleteButton.setOnClickListener(clickListener);
    }



}
