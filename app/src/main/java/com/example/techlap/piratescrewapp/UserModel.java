package com.example.techlap.piratescrewapp;

/**
 * Created by tech lap on 06/10/2016.
 */
public class UserModel {
    //name,id,photo_profile
    private String name,email,commitee, personalInfo,position,profileimage, phone;



    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public UserModel() {


    }

    public UserModel(String profileimage, String name, String email, String commitee, String personalInfo, String position, String phone) {
        this.profileimage = profileimage;
        this.name = name;
        this.email = email;
        this.commitee = commitee;
        this.personalInfo = personalInfo;
        this.position = position;
        this.phone = phone;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPersonalInfo() {
        return personalInfo;
    }

    public void setPersonalInfo(String personalInfo) {
        this.personalInfo = personalInfo;
    }

    public String getCommitee() {
        return commitee;
    }

    public void setCommitee(String commitee) {
        this.commitee = commitee;
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
}
