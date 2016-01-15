package com.wll.main.activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.wll.main.R;

public class HomeActivity extends AppCompatActivity {

    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        frameLayout = (FrameLayout) findViewById(R.id.id_fragment);
//
//        FragmentManager manager = getSupportFragmentManager();
//        FragmentTransaction transaction = manager.beginTransaction();
//        TestFragment contentFragment = new TestFragment();
//        transaction.replace(R.id.id_fragment, contentFragment);
//        transaction.commit();
    }
}
