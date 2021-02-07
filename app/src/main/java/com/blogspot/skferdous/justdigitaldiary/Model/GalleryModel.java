package com.blogspot.skferdous.justdigitaldiary.Model;

public class GalleryModel {
    private String id, topic, url, detail;

    public GalleryModel() {
    }

    public GalleryModel(String id, String topic, String url, String detail) {
        this.id = id;
        this.topic = topic;
        this.url = url;
        this.detail = detail;
    }

    public String getId() {
        return id;
    }

    public GalleryModel(String topic, String url, String detail) {
        this.topic = topic;
        this.url = url;
        this.detail = detail;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
