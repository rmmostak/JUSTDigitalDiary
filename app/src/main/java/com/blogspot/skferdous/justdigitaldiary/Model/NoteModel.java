package com.blogspot.skferdous.justdigitaldiary.Model;

public class NoteModel {
    private String id;
    private String date;
    private String time;
    private String title;
    private String body;
    private String attendees;
    private String noteId;

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    private String senderId;
    private boolean permission;

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

    public String getAttendees() {
        return attendees;
    }

    public void setAttendees(String attendees) {
        this.attendees = attendees;
    }

    public boolean isPermission() {
        return permission;
    }

    public void setPermission(boolean permission) {
        this.permission = permission;
    }

    public NoteModel(String id, String date, String time, String title, String body, String attendees, boolean permission) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.title = title;
        this.body = body;
        this.attendees = attendees;
        this.permission = permission;
    }
}
