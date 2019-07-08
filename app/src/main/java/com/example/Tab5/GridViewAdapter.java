package com.example.Tab5;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

public class GridViewAdapter extends BaseAdapter {
    Context context = null;
    ArrayList<Bitmap> listdata = new ArrayList<Bitmap>();

    public GridViewAdapter(Context c, ArrayList<Bitmap> listdata) {
        this.context = c;
        this.listdata = Godbgallery.listdata;
    }

    public int getCount() {
        return listdata.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new GridView.LayoutParams(700, 700));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setPadding(1, 1, 1, 1);
        imageView.setImageBitmap(rotateBitmap(listdata.get(position), 90));
        imageView.bringToFront();

        return imageView;
    }

    public Bitmap rotateBitmap(Bitmap original, float degrees) {
        int width = original.getWidth();
        int height = original.getHeight();
        Matrix matrix = new Matrix();
        matrix.preRotate(degrees);
        Bitmap rotatedBitmap = Bitmap.createBitmap(original, 0, 0, width, height, matrix, true);
        return rotatedBitmap;
    }
}
