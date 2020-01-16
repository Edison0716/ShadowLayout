package com.junlong0716.shadowlayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.junlong0716.shadow.TriangleView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRvList;
    private ArrayList<TestBean> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        TriangleView tv1 = findViewById(R.id.tv_1);
        TriangleView tv2 = findViewById(R.id.tv_2);
        TriangleView tv3 = findViewById(R.id.tv_3);
        TriangleView tv4 = findViewById(R.id.tv_4);


        tv1.setChildViewSelected(true);
        tv4.setBgHasRightRadius(true);



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
