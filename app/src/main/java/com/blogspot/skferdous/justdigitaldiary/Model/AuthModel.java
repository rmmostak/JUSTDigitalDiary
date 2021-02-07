package com.blogspot.skferdous.justdigitaldiary.Model;

public class AuthModel {
    private String id, uid, email, name, dept;

    public AuthModel() {
    }

    public AuthModel(String id, String uid, String email, String name, String dept) {
        this.id = id;
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.dept = dept;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }
}
