package com.example.app4;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class CustomAdapter extends BaseAdapter {
    private Context mContext;

    private Integer[] mThumbIds = new Integer[100];


    CustomAdapter(Context c) {
        mContext = c;

        for(int i = 0; i < 100; i++){
            mThumbIds[i] = R.drawable.empty;
        }
    }

    public int getCount(){
        return mThumbIds.length;
    }

    public Object getItem(int position) {return position;}

    public long getItemId(int position) {return  position;}

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        }
        else
        {
            imageView = (ImageView) convertView;
        }
        imageView.setImageResource(mThumbIds[position]);
        return imageView;

    }

    void updateImage(int position, int resourceId)
    {
        if(resourceId == 1) mThumbIds[position] = R.drawable.thread1;
        if(resourceId == 2) mThumbIds[position] = R.drawable.thread2;
        if(resourceId == 3) mThumbIds[position] = R.drawable.gopher;

    }
}
