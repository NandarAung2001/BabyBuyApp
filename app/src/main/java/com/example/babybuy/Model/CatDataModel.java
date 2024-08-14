package com.example.babybuy.Model;

public class CatDataModel {
    public int catid;
    public String catname;
    public byte[] catimage;

    public int getCatid() {
        return catid;
    }

    public void setCatid(int catid) {
        this.catid = catid;
    }

    public String getCatname() {
        return catname;
    }

    public void setCatname(String catname) {
        this.catname = catname;
    }

    public byte[] getCatimage() {
        return catimage;
    }

    public void setCatimage(byte[] catimage) {
        this.catimage = catimage;
    }
}
