package com.techhawk.diversify.viewholder;

import android.view.View;
import android.widget.Button;

import com.techhawk.diversify.R;
import com.techhawk.diversify.model.CustomEvent;

/**
 * Created by Yidi Wu on 26/4/18.
 */

public class PrivateEventViewHolder extends CustomEventViewHolder {


    public PrivateEventViewHolder(View itemView) {
        super(itemView);
        deleteButton.setVisibility(View.VISIBLE);

    }


}
