package com.epicture.Adapter;

import android.graphics.Bitmap;
import static java.sql.Types.NULL;

public class BitmapAdapter {

    private int position = NULL;
    private String from;
    private Bitmap bitmap;
    final static BitmapAdapter instance = new BitmapAdapter();

    public static BitmapAdapter getInstance() {
        return instance;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getBitmapPosition() {
        return position;
    }

    public void setBitmapPostion(int position) {
        this.position = position;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
