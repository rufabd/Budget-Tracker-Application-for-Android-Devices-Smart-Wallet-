package com.example.app_dev_money_tracking;

import android.graphics.Bitmap;

public class Receipt {
    private int id;
    private byte[] image;

    public Receipt() {
    }

    public Receipt(int id, byte[] image) {
        this.id = id;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getCategoryImg() {
        return image;
    }

    public void setCategoryImg(byte[] image) {
        this.image = image;
    }
}
