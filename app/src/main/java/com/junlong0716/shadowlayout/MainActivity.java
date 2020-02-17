package com.junlong0716.shadowlayout;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ImageUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.junlong0716.shadow.EHaiWidgetShadowLayout;
import com.junlong0716.shadow.MyTabView;
import com.junlong0716.shadow.ShadowFrameLayout;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRvList;
    private ArrayList<TestBean> list = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //ShadowFrameLayout shadowFrameLayout = findViewById(R.id.sfl);
//        EHaiWidgetShadowLayout shadowFrameLayout1 = findViewById(R.id.sfl1);
//
//        Observable.interval(1, TimeUnit.SECONDS)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<Long>() {
//                    @Override
//                    public void accept(Long aLong) throws Exception {
//                     //   shadowFrameLayout.requestLayout();
//                        shadowFrameLayout1.requestLayout();
//                    }
//                });
//        Log.d("ON-CREATE","ON-CREATE");
//        MyTabView tv1 = findViewById(R.id.tv);
////        TriangleView tv2 = findViewById(R.id.tv_2);
////        TriangleView tv3 = findViewById(R.id.tv_3);
////        TriangleView tv4 = findViewById(R.id.tv_4);
//
//        ArrayList<MyTabView.MyTabEntity> objects = new ArrayList<>();
//        objects.add(new MyTabView.MyTabEntity("国际租车", true));
//        objects.add(new MyTabView.MyTabEntity("1"));
//        objects.add(new MyTabView.MyTabEntity("国际租车"));
//        objects.add(new MyTabView.MyTabEntity("国际租车"));
//
        ImageView iv = findViewById(R.id.iv);
//        Glide.with(this).asBitmap().load("http://b-ssl.duitang.com/uploads/item/201510/25/201510G25131025_mCjxX.jpeg").into(new SimpleTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//
//            }
//        })


        Disposable no_data = Observable.create(new ObservableOnSubscribe<Drawable>() {
            @Override
            public void subscribe(ObservableEmitter<Drawable> emitter) throws Exception {

                Bitmap bitmap = Glide.with(MainActivity.this).asBitmap().load("http://d.hiphotos.baidu.com/zhidao/pic/item/6a63f6246b600c334c3e91cb1e4c510fd9f9a16a.jpg").submit().get();

                if (bitmap != null) {
                    Bitmap bitmap1 = ImageUtils.fastBlur(bitmap, 0.5f, 20f);
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
                    emitter.onNext(bitmapDrawable);
                } else {
                    emitter.onError(new Exception("no data"));
                }

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Drawable>() {
            @Override
            public void accept(Drawable drawable) throws Exception {
                iv.setBackground(drawable);
            }
        });

//
//        tv1.setData(objects);

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
