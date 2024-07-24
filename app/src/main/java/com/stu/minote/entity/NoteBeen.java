package com.stu.minote.entity;

import java.io.Serializable;

public class NoteBeen implements Serializable {


    /**
     * id : 19
     * title : 你好
     * created : 2024-06-14T04:52:36.468Z
     * updated : 2024-06-14T04:52:36.478Z
     */

    private String id;
    private String title;
    private String created;
    private String textContent;
    private String updated;
    private String category;
    private String picture;
    private String audio;

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getCategory() {
        return category;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
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

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }
}
