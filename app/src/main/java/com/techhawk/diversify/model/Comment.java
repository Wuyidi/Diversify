package com.techhawk.diversify.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Yidi Wu on 1/5/18.
 */

public class Comment implements Parcelable {

    private String uid;
    private String username;
    private String date;
    private int numOfThumb;
    private String content;

    public Comment() {
    }

    public Comment(String uid, String username, String date, int numOfThumb, String content) {
        this.uid = uid;
        this.username = username;
        this.date = date;
        this.numOfThumb = numOfThumb;
        this.content = content;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getNumOfThumb() {
        return numOfThumb;
    }

    public void setNumOfThumb(int numOfThumb) {
        this.numOfThumb = numOfThumb;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.username);
        dest.writeString(this.date);
        dest.writeInt(this.numOfThumb);
        dest.writeString(this.content);
    }

    protected Comment(Parcel in) {
        this.uid = in.readString();
        this.username = in.readString();
        this.date = in.readString();
        this.numOfThumb = in.readInt();
        this.content = in.readString();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}
