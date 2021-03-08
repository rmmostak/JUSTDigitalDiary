package com.blogspot.skferdous.justdigitaldiary.Model;

public class InvitedModel {
    String noteId, senderId, attendees, senderName, key;
    boolean permission;

    public InvitedModel() {
    }

    public InvitedModel(String key, String noteId, String senderId, String attendees, String senderName, boolean permission) {
        this.key = key;
        this.noteId = noteId;
        this.senderId = senderId;
        this.attendees = attendees;
        this.senderName = senderName;
        this.permission = permission;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

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

    public String getAttendees() {
        return attendees;
    }

    public void setAttendees(String attendees) {
        this.attendees = attendees;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public boolean getPermission() {
        return permission;
    }

    public void setPermission(boolean permission) {
        this.permission = permission;
    }
}
