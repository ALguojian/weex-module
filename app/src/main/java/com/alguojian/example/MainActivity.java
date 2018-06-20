package com.alguojian.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.shuwei.weex.ui.WeexPagerActivity;
import com.shuwei.weex.ui.WeexPagerFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WeexPagerActivity.start(this,"home/home.js");

        WeexPagerFragment.newInstance("home/home.js");
    }
}
