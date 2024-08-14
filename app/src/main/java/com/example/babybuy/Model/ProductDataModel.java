package com.example.babybuy.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ProductDataModel {


    Integer productid;
    String productname;
    Integer productquantity;
    Double productprice, productlat, productlong;
    String productdescription;
    Integer productstatus;
    byte[] productimage;
    Integer productcategoryid;
    String productaddress;

    public String getProductaddress() {
        return productaddress;
    }

    public void setProductaddress(String productaddress) {
        this.productaddress = productaddress;
    }

    public Integer getProductid() {
        return productid;
    }

    public void setProductid(Integer productid) {
        this.productid = productid;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public Integer getProductquantity() {
        return productquantity;
    }

    public void setProductquantity(Integer productquantity) {
        this.productquantity = productquantity;
    }

    public Double getProductprice() {
        return productprice;
    }

    public void setProductprice(Double productprice) {
        this.productprice = productprice;
    }

    public Double getProductlat() {
        return productlat;
    }

    public void setProductlat(Double productlat) {
        this.productlat = productlat;
    }

    public Double getProductlong() {
        return productlong;
    }

    public void setProductlong(Double productlong) {
        this.productlong = productlong;
    }

    public String getProductdescription() {
        return productdescription;
    }

    public void setProductdescription(String productdescription) {
        this.productdescription = productdescription;
    }

    public Integer getProductstatus() {
        return productstatus;
    }

    public void setProductstatus(Integer productstatus) {
        this.productstatus = productstatus;
    }

    public byte[] getProductimage() {
        return productimage;
    }

    public void setProductimage(byte[] productimage) {
        this.productimage = productimage;
    }

    public Integer getProductcategoryid() {
        return productcategoryid;
    }

    public void setProductcategoryid(Integer productcategoryid) {
        this.productcategoryid = productcategoryid;
    }
}