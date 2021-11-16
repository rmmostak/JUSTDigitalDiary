package com.blogspot.skferdous.justdigitaldiary.Model;

public class AdminModel {
    private String id;
    private String identifier;
    private String key;

    public AdminModel() {
    }

    public AdminModel(String id, String identifier, String key) {
        this.id = id;
        this.identifier = identifier;
        this.key=key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
