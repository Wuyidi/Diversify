package com.techhawk.diversify.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.techhawk.diversify.R;
import com.techhawk.diversify.model.Holiday;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Yidi Wu on 20/4/18.
 */

public class FestivalViewHolder extends RecyclerView.ViewHolder {

    // Instance variable
    private TextView nameView;
    private TextView commentsView;
    private ImageView img;


    public FestivalViewHolder(View itemView) {
        super(itemView);
        // Get reference from activity
        nameView = itemView.findViewById(R.id.festival_name);
        commentsView = itemView.findViewById(R.id.festival_comments);
        img = itemView.findViewById(R.id.cardView_background);
    }

    private void loadImg(String url, Context context) {
        Glide.with(context).load(url)
                .transition(withCrossFade())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(img);
    }

    public void bindToFestival(Holiday holiday, Context context){
        nameView.setText(holiday.getName());
        commentsView.setText(holiday.getComments());
        loadImg(holiday.getImgUrl(), context);

    }

}
