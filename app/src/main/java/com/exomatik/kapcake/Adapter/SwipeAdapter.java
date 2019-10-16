package com.exomatik.kapcake.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.exomatik.kapcake.R;

import androidx.viewpager.widget.PagerAdapter;

/**
 * Created by IrfanRZ on 6/1/2018.
 */

public class SwipeAdapter extends PagerAdapter {
    private Context ctx;
    private LayoutInflater layoutInflater;
    private String[] title, isi;

    public SwipeAdapter(Context ctx, String[] title, String[] isi){
        this.ctx = ctx;
        this.title = title;
        this.isi = isi;
    }


    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view==(RelativeLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view = layoutInflater.inflate(R.layout.swipe_layout, container, false);

        TextView textTitle = (TextView) item_view.findViewById(R.id.text_title);
        TextView textIsis = (TextView) item_view.findViewById(R.id.text_isi);

        textTitle.setText(title[position]);
        textIsis.setText(isi[position]);
        container.addView(item_view);

        return item_view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }
}
