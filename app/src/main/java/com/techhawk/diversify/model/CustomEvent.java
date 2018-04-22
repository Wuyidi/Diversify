package com.techhawk.diversify.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Yidi Wu on 21/4/18.
 */

@IgnoreExtraProperties
public class CustomEvent implements Parcelable {

    private String name;
    private String location;
    private String date;
    private String description;

    public CustomEvent() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.location);
        dest.writeString(this.date);
        dest.writeString(this.description);
    }

    protected CustomEvent(Parcel in) {
        this.name = in.readString();
        this.location = in.readString();
        this.date = in.readString();
        this.description = in.readString();
    }

    public static final Parcelable.Creator<CustomEvent> CREATOR = new Parcelable.Creator<CustomEvent>() {
        @Override
        public CustomEvent createFromParcel(Parcel source) {
            return new CustomEvent(source);
        }

        @Override
        public CustomEvent[] newArray(int size) {
            return new CustomEvent[size];
        }
    };
}
