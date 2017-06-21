package com.pinger.android.utils.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pinger.android.utils.R;
import com.pinger.android.utils.inject.annotation.BindView;
import com.pinger.android.utils.inject.annotation.ContentView;
import com.pinger.android.utils.inject.annotation.OnClick;
import com.pinger.android.utils.inject.annotation.OnLongClick;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    @BindView(R.id.text1)
    TextView mTextView1;

    @BindView(R.id.text2)
    TextView mTextView2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTextView2.setText("注解成功");
    }

    @OnClick({R.id.text1,R.id.text2})
    public void onClick(View view) {
        Toast.makeText(this, "文本点击", Toast.LENGTH_SHORT).show();
    }

    @OnLongClick({R.id.text1,R.id.text2})
    public boolean onLongClick(View view) {
        Toast.makeText(this, "文本2长按", Toast.LENGTH_SHORT).show();
        return false;
    }

}
