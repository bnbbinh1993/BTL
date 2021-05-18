package com.example.btl.model;

public class Profile {
    private String personEmail;
    private String personFB;
    private String personFamilyName;
    private String personGivenName;
    private String personId;
    private String personName;
    private String personPhone;
    private String personPhoto;

    public Profile() {
    }

    public Profile(String personEmail, String personFB, String personFamilyName, String personGivenName, String personId, String personName, String personPhone, String personPhoto) {
        this.personEmail = personEmail;
        this.personFB = personFB;
        this.personFamilyName = personFamilyName;
        this.personGivenName = personGivenName;
        this.personId = personId;
        this.personName = personName;
        this.personPhone = personPhone;
        this.personPhoto = personPhoto;
    }

    public String getPersonEmail() {
        return personEmail;
    }

    public void setPersonEmail(String personEmail) {
        this.personEmail = personEmail;
    }

    public String getPersonFB() {
        return personFB;
    }

    public void setPersonFB(String personFB) {
        this.personFB = personFB;
    }

    public String getPersonFamilyName() {
        return personFamilyName;
    }

    public void setPersonFamilyName(String personFamilyName) {
        this.personFamilyName = personFamilyName;
    }

    public String getPersonGivenName() {
        return personGivenName;
    }

    public void setPersonGivenName(String personGivenName) {
        this.personGivenName = personGivenName;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonPhone() {
        return personPhone;
    }

    public void setPersonPhone(String personPhone) {
        this.personPhone = personPhone;
    }

    public String getPersonPhoto() {
        return personPhoto;
    }

    public void setPersonPhoto(String personPhoto) {
        this.personPhoto = personPhoto;
    }
}
