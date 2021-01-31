package com.blogspot.skferdous.justdigitaldiary.Model;

public class NoteModel {
    private String id, date, time, title, body;

    public NoteModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public NoteModel(String id, String date, String time, String title, String body) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.title = title;
        this.body = body;
    }
}
