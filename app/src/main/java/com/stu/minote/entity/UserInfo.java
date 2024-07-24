package com.stu.minote.entity;

import java.io.Serializable;

public class UserInfo implements Serializable {
    /**
     * id : 1
     * username : john_doe
     * nickname : John
     * mobile : 1234567890
     * bio : Hello World
     * head_image : url_to_image
     * created : 2023-06-15 12:00:00
     */

    private int id;
    private String username;
    private String nickname;
    private String mobile;
    private String bio;
    private String head_image;
    private String created;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getHead_image() {
        return head_image;
    }

    public void setHead_image(String head_image) {
        this.head_image = head_image;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
