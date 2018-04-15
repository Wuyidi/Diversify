package com.techhawk.diversify.viewholder;

import android.content.Context;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.techhawk.diversify.model.Event;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Yidi Wu on 15/4/18.
 */

public class PublicEventViewHolder extends EventViewHolder {
    public PublicEventViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindToEvent(Event event, Context context) {
        nameView.setText(event.getName());
        monthView.setText(event.getMonth());
        loadImg(event.getImgUrl(),context);
    }

    private void loadImg(String url, Context context) {
        Glide.with(context).load(url)
                .transition(withCrossFade())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(bgView);
    }

}
