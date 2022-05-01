package com.cc.baselibrary.base;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(getContentLayoutId());
        intData();
        initView();
    }

    protected void intData() {
    }

    protected abstract int getContentLayoutId();

    protected abstract void initView();

}