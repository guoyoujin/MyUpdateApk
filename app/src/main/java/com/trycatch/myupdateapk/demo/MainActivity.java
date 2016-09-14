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
        UpdateKey.API_TOKEN = "160105e0223dec1896a6648b8265cc95";
        UpdateKey.APP_ID = "57d75987ca87a87c89000b91";
        UpdateKey.WITH_DIALOG=true;
        UpdateKey.WITH_NOTIFITION=true;
        UpdateApk.init(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UpdateApk.destory();
    }
}
