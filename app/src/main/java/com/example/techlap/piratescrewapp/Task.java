package com.example.techlap.piratescrewapp;

/**
 * Created by tech lap on 18/11/2016.
 */
public class Task {
    private String title,desc,from,file;
    private Long time;
    private Boolean seen;

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }


    public Task() {
    }

    public Task(String title, String desc, String from, String file, Long time, Boolean seen) {
        this.title = title;
        this.desc = desc;
        this.from = from;
        this.file = file;
        this.time=time;
        this.seen=seen;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
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

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

}
