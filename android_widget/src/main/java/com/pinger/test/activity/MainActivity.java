package com.pinger.test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.pinger.widget.R;

/**
 * @author Pinger
 * @since 2017/3/27 0027 下午 2:45
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        String gifUrl = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1493782876&di=d47acc012cb81467eb7ee9e239fb50e5&imgtype=jpg&er=1&src=http%3A%2F%2Fwww.zlcool.com%2Fd%2Ffile%2F2013%2F12%2F15%2F54d4df8b42e5c0b56b1224bfac906248.gif";
        Glide.with(this).load(gifUrl).transform(new GifIconTransformation(this, true)).into(imageView);
    }

    public void imagePreview(View view) {
        startActivity(new Intent(this, ImageActivity.class));
    }


    public void banner(View view) {
        startActivity(new Intent(this, BannerActivity.class));
    }

}
