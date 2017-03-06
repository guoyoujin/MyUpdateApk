package com.trycatch.myupdateapk.demo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.trycath.myupdateapklibrary.UpdateApk;
import com.trycath.myupdateapklibrary.UpdateKey;
import com.trycath.myupdateapklibrary.UpdateState;
import com.trycath.myupdateapklibrary.listener.AppUpdateListener;
import com.trycath.myupdateapklibrary.model.AppInfoModel;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button updateApp;
    public static final String TAG = MainActivity.class.getSimpleName();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UpdateKey.API_TOKEN = "160105e0223dec1896a6648b8265cc95";
        UpdateKey.APP_ID = "57d75987ca87a87c89000b91";
        UpdateKey.WITH_DIALOG=true; //设置是否显示弹出带进度条框
        UpdateKey.WITH_NOTIFITION=true;//设置是否在NOTIFITION上显示进度
        UpdateApk.init(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UpdateApk.destory();
    }
    
    
    public void initView(){
        updateApp = (Button) findViewById(R.id.updateApp);
        updateApp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        dialog.setIndeterminate(true);
        dialog.setMessage("正在检查更新");
        dialog.setCancelable(false);
        switch (v.getId()){
            case R.id.updateApp:
                Log.d(TAG,"switch updateApp");
                UpdateApk.setmManualUpdateFlg(true);
                UpdateApk.setAppUpdateListener(new AppUpdateListener() {
                    @Override
                    public void onStart() {
                        if(dialog!=null&&!dialog.isShowing()){
                            dialog.show();
                        }
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG,"onCompleted()===");
                        if(dialog!=null&&dialog.isShowing()){
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG,e.toString());
                        if(dialog!=null&&dialog.isShowing()){
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onNext(AppInfoModel appInfoModel) {
                        Log.d(TAG,appInfoModel.toString());
                        if(dialog!=null&&dialog.isShowing()){
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onNext(AppInfoModel appInfoModel, int state) {
                        Log.d(TAG,appInfoModel.toString());
                        Log.d(TAG,"=state=="+state);
                        switch (state){
                            case UpdateState.BEST_NEW_VSERSION:
                                Toast.makeText(MainActivity.this,"this is best new version",Toast.LENGTH_SHORT).show();
                                break;
                            case UpdateState.BEST_HEIGHT_VERSION:
                                Toast.makeText(MainActivity.this,"this is highest version",Toast.LENGTH_SHORT).show();
                                break;
                            case UpdateState.NEED_UPDATE_VERSION:
                                Toast.makeText(MainActivity.this,"need update new version",Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                
                        }
                        if(dialog!=null&&dialog.isShowing()){
                            dialog.dismiss();
                        }
                    }
                });
                UpdateApk.init(MainActivity.this);
                break;
            default:
                Log.d(TAG,"switch default");    
        }
    }
    
}
