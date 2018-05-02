package com.techhawk.diversify.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Yidi Wu on 2/5/18.
 */
@IgnoreExtraProperties
public class CommentCredited {
    private boolean isCredited;

    public CommentCredited() {
    }

    public CommentCredited(boolean isCredited) {
        this.isCredited = isCredited;
    }

    public boolean isCredited() {
        return isCredited;
    }

    public void setCredited(boolean credited) {
        isCredited = credited;
    }

}
