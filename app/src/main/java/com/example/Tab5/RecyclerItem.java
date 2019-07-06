package com.example.Tab5;

import android.graphics.drawable.Drawable;

public class RecyclerItem {
    private String nameStr ;
    private String numberStr ;
    private Drawable iconDrawable ;
    public void setIcon(Drawable icon) {
        iconDrawable = icon ;
    }
    public void setName(String name) {
        nameStr = name ;
    }
    public void setNum(String number) {
        numberStr = number ;
    }
    public Drawable getIcon() {
        return this.iconDrawable ;
    }
    public String getName() {
        return this.nameStr ;
    }
    public String getNum() {
        return this.numberStr ;
    }
}
