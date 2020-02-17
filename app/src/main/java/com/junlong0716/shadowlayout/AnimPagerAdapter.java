package com.junlong0716.shadowlayout;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

/**
 * @ClassName: AnimPagerAdapter
 * @Description:
 * @Author: LiJunlong
 * @CreateDate: 2020-02-17 15:47
 */
public class AnimPagerAdapter extends PagerAdapter {
    private ArrayList<String> pageData;
    private Context mContext;

    public AnimPagerAdapter(ArrayList<String> pageData,Context context) {
        this.pageData = pageData;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return pageData.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        if (position % 2 == 0) {
            View v = View.inflate(mContext,R.layout.anim_adapter_item1,null);
            container.addView(v);
            return v;
        } else {
            View v = View.inflate(mContext,R.layout.anim_adapter_item2,null);
            container.addView(v);
            return v;
        }
    }
}
