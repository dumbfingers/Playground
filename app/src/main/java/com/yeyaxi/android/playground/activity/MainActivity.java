package com.yeyaxi.android.playground.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yeyaxi.android.playground.R;
import com.yeyaxi.android.playground.fragment.MainFragment;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commit();
    }
}
