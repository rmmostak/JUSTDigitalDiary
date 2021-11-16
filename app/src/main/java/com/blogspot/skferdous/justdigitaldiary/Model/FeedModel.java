package com.blogspot.skferdous.justdigitaldiary.Model;

public class FeedModel {
    private String feedBack;
    private String id;

    public FeedModel(String feedBack, String id) {
        this.feedBack = feedBack;
        this.id = id;
    }

    public FeedModel() {
    }

    public String getFeedBack() {
        return feedBack;
    }

    public void setFeedBack(String feedBack) {
        this.feedBack = feedBack;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
