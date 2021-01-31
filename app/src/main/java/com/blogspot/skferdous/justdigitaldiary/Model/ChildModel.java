package com.blogspot.skferdous.justdigitaldiary.Model;

public class ChildModel {

    private String id, name, designation, phoneHome, phonePer, email, others, pbx;

    public ChildModel() {
    }

    public ChildModel(String id, String name, String designation, String phoneHome, String phonePer, String email, String others, String pbx) {
        this.id = id;
        this.name = name;
        this.designation = designation;
        this.phoneHome = phoneHome;
        this.phonePer = phonePer;
        this.email = email;
        this.others = others;
        this.pbx = pbx;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getPhoneHome() {
        return phoneHome;
    }

    public void setPhoneHome(String phoneHome) {
        this.phoneHome = phoneHome;
    }

    public String getPhonePer() {
        return phonePer;
    }

    public void setPhonePer(String phonePer) {
        this.phonePer = phonePer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOthers() {
        return others;
    }

    public void setOthers(String others) {
        this.others = others;
    }

    public String getPbx() {
        return pbx;
    }

    public void setPbx(String pbx) {
        this.pbx = pbx;
    }
}
