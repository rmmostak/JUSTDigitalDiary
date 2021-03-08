package com.blogspot.skferdous.justdigitaldiary.Model;

public class FeedBack {
    String id, feedBack;

    public FeedBack(String id, String feedBack) {
        this.id = id;
        this.feedBack = feedBack;
    }

    public FeedBack() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFeedBack() {
        return feedBack;
    }

    public void setFeedBack(String feedBack) {
        this.feedBack = feedBack;
    }
}
