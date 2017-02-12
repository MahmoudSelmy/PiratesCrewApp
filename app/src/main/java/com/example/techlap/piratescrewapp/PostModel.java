package com.example.techlap.piratescrewapp;

/**
 * Created by tech lap on 04/10/2016.
 */
public class PostModel {
    private  String title,desc,image;
    private long time;
    public PostModel(){

    }
    //using Generate
    public PostModel(String image, String desc, String title,Long time) {
        this.image = image;
        this.desc = desc;
        this.title = title;
        this.time=time;
    }

    //use genrate : getter & setter


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
