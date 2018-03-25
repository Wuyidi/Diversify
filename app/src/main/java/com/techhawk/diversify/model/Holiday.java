package com.techhawk.diversify.model;


import com.google.firebase.database.IgnoreExtraProperties;
/**
 * Created by Yidi Wu on 22/3/18.
 */

@IgnoreExtraProperties
public class Holiday {
    // Instance variable
    private String imgUrl;
    private String type;
    private String comments;
    private String history;
    private String name;

    public Holiday() {
    }

    public Holiday(String imgUrl, String type, String comments, String history, String name) {
        this.imgUrl = imgUrl;
        this.type = type;
        this.comments = comments;
        this.history = history;
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
