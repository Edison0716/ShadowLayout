package com.junlong0716.shadowlayout;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.junlong0716.shadow.MyTabView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRvList;
    private ArrayList<TestBean> list = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Log.d("ON-CREATE","ON-CREATE");
        MyTabView tv1 = findViewById(R.id.tv);
//        TriangleView tv2 = findViewById(R.id.tv_2);
//        TriangleView tv3 = findViewById(R.id.tv_3);
//        TriangleView tv4 = findViewById(R.id.tv_4);

        ArrayList<MyTabView.MyTabEntity> objects = new ArrayList<>();
        objects.add(new MyTabView.MyTabEntity("国际租车", true));
        objects.add(new MyTabView.MyTabEntity("1"));
        objects.add(new MyTabView.MyTabEntity("国际租车"));
        objects.add(new MyTabView.MyTabEntity("国际租车"));


        tv1.setData(objects);

        //tv1.setOnTabClickListener(pressedIndex -> objects.stream().filter(s -> s.getTabTitle().equals("1")).forEach(myTabEntity -> Toast.makeText(MainActivity.this, myTabEntity.getTabTyp() +"", Toast.LENGTH_SHORT).show()));

//        tv1.setChildViewSelected(true);
//        tv4.setBgHasRightRadius(true);


//        mRvList = findViewById(R.id.rvList);
//
//        for (int i = 0; i < 100; i++) {
//            list.add(new TestBean());
//        }
//
//        mRvList.setLayoutManager(new LinearLayoutManager(this));
//        mRvList.setAdapter(new BaseQuickAdapter<TestBean, BaseViewHolder>(R.layout.activity_main, list) {
//            @Override
//            protected void convert(@NonNull BaseViewHolder helper, TestBean item) {
//
//            }
//        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("ON-CONFIGURATION","ON-CONFIGURATION");
    }

    class TestBean {

        private String title;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
