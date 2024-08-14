package com.example.babybuy.Model;

public class AuthDatamodel {

    public int auth_id;
    public String firstname;
    public String lastname;
    public String dbmemail;
    String password;
    byte[] profileimage;

    public byte[] getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(byte[] profileimage) {
        this.profileimage = profileimage;
    }

    public int getAuth_id() {
        return auth_id;
    }

    public void setAuth_id(int auth_id) {
        this.auth_id = auth_id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getDbmemail() {
        return dbmemail;
    }

    public void setDbmemail(String dbmemail) {
        this.dbmemail = dbmemail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
