package com.trycatch.myupdateapk.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.trycath.myupdateapklibrary.UpdateApk;
import com.trycath.myupdateapklibrary.UpdateKey;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UpdateKey.API_TOKEN = "";
        UpdateKey.APP_ID = "";
        UpdateApk.init(this);
    }
}
