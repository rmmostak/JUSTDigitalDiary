package com.blogspot.skferdous.justdigitaldiary.Model;

public class PortalModel {
    String id, title, desc, link, others;

    public PortalModel() {
    }

    public PortalModel(String id, String title, String desc, String link, String others) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.link = link;
        this.others = others;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getOthers() {
        return others;
    }

    public void setOthers(String others) {
        this.others = others;
    }
}
