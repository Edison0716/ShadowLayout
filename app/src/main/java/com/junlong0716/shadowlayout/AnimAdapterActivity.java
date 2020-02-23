package com.junlong0716.shadowlayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AnimAdapterActivity extends AppCompatActivity {
    private final ArrayList<String> mData = new ArrayList<>(20);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anim_adapter);


        for (int i = 0; i < 50; ++i) {
            mData.add("");
        }

        ViewPager vp = findViewById(R.id.viewpager);
        AnimPagerAdapter animPagerAdapter = new AnimPagerAdapter(mData, this);
        vp.setAdapter(animPagerAdapter);
        vp.setOffscreenPageLimit(mData.size() / 2);

    }
}
