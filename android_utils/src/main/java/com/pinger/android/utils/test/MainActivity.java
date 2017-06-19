package com.pinger.android.utils.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pinger.android.utils.R;
import com.pinger.android.utils.inject.annotation.BindView;
import com.pinger.android.utils.inject.annotation.ContentView;
import com.pinger.android.utils.inject.annotation.OnClick;

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
        mTextView2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }

    @OnClick({R.id.text1, R.id.text2})
    public void onClick(View view) {
        Toast.makeText(this, "文本1点击", Toast.LENGTH_SHORT).show();
    }
}
