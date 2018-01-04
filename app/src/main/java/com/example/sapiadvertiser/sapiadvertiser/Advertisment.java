package com.example.sapiadvertiser.sapiadvertiser;

/**
 * Created by Rita on 1/2/2018.
 */

public class Advertisment {

    private String title,desc, image,username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Advertisment(){


    }

    public Advertisment(String title, String desc, String image,String username) {
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
