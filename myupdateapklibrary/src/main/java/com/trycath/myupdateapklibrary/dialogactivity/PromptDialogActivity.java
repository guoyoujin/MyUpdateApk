package com.trycath.myupdateapklibrary.dialogactivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.trycath.myupdateapklibrary.R;
import com.trycath.myupdateapklibrary.model.AppInfoModel;
import com.trycath.myupdateapklibrary.service.DownloadFileService;
import com.trycath.myupdateapklibrary.util.FileUtils;
import com.trycath.myupdateapklibrary.util.InstallApk;
import com.trycath.myupdateapklibrary.util.PreferenceUtils;
import com.trycath.myupdateapklibrary.util.StringUtils;


public class PromptDialogActivity extends AppCompatActivity{
    private Button btnNowUpdate;
    private Button btnAfterUpdate;
    private TextView tvTitle;
    private  TextView tvVersion;
    private TextView tvSize;
    private  TextView tvContent;
    private CheckBox chBox;
    private static  String TAG = PromptDialogActivity.class.getSimpleName();
    private static final int REQUEST_EXTERNAL_STORAGE = 111;
    public static final String INTENT_DOWNLOAD_MODEL= "INTENT_DOWNLOAD_MODEL";
    private AppInfoModel appInfoModel ;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompt_dialog);
        Log.d(TAG,"==onCreate==");
        if(getIntent()!=null&&getIntent().getExtras()!=null&&getIntent().getExtras().getSerializable(PromptDialogActivity.INTENT_DOWNLOAD_MODEL)!=null){
            appInfoModel = (AppInfoModel) getIntent().getExtras().getSerializable(PromptDialogActivity.INTENT_DOWNLOAD_MODEL);
            initView();
            initContent();
        }else{
            finish();
        }
    }
    
    private void initView(){
        btnNowUpdate = (Button) findViewById(R.id.btnNowUpdate);
        btnAfterUpdate = (Button) findViewById(R.id.btnAfterUpdate);
        tvTitle = (TextView)findViewById(R.id.tvTitle);
        tvVersion = (TextView)findViewById(R.id.tvVersion);
        tvSize = (TextView) findViewById(R.id.tvSize);
        tvContent = (TextView) findViewById(R.id.tvContent);
        chBox = (CheckBox) findViewById(R.id.chBox);
        btnNowUpdate.setOnClickListener(nowUpdateListener);
        btnAfterUpdate.setOnClickListener(afterUpdateListener);
        chBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    PreferenceUtils.setPrefBoolean(PromptDialogActivity.this,appInfoModel.getVersion(),isChecked);
                }
            }
        });
        
    }
    
    private void initContent(){
        tvVersion.setText(String.format("%s：%s",getResources().getString(R.string.most_version),appInfoModel.getVersionShort()));
        double size = (double)appInfoModel.getBinary().getFsize();
        tvContent.setText(String.format("%s\n%s",getResources().getString(R.string.update_content),appInfoModel.getChangelog()));
        tvContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        if(FileUtils.getFile(appInfoModel).exists() && FileUtils.getFileSize(FileUtils.getFile(appInfoModel))==appInfoModel.getBinary().getFsize()){
            tvSize.setText(getResources().getString(R.string.most_version_downloaded));
        }else{
            tvSize.setText(String.format("%s：%s",getResources().getString(R.string.new_version_size), StringUtils.getDataSize(appInfoModel.getBinary().getFsize())));
        }
    }
   
    View.OnClickListener nowUpdateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int writePermission = ActivityCompat.checkSelfPermission(PromptDialogActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int readPermission = ActivityCompat.checkSelfPermission(PromptDialogActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PromptDialogActivity.this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }else{
                startService();
            }
        }
    };
    
    View.OnClickListener afterUpdateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG,requestCode+"====");
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: 
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startService();
                } else {
                    Toast.makeText(PromptDialogActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    public static void startActivity(Context context,AppInfoModel appInfoModel) {
        if(appInfoModel!=null){
            Intent intent = new Intent(context,PromptDialogActivity.class);
            intent.putExtra(INTENT_DOWNLOAD_MODEL,appInfoModel);
            context.startActivity(intent);

        }
    }
    
    public void startService(){
        if(FileUtils.getFile(appInfoModel).exists() && FileUtils.getFileSize(FileUtils.getFile(appInfoModel))==appInfoModel.getBinary().getFsize()){
            tvSize.setText(getResources().getString(R.string.most_version_downloaded));
            InstallApk.startInstall(PromptDialogActivity.this,FileUtils.getFile(appInfoModel));
            finish();
        }else{
            DownloadFileService.startDownloadFileService(PromptDialogActivity.this,appInfoModel);
            finish();        
        }
        
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG,"==onRestoreInstanceState==");
        super.onRestoreInstanceState(savedInstanceState);
        appInfoModel = (AppInfoModel) savedInstanceState.getSerializable(PromptDialogActivity.INTENT_DOWNLOAD_MODEL);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG,"==onSaveInstanceState==");
        outState.putSerializable(PromptDialogActivity.INTENT_DOWNLOAD_MODEL,appInfoModel);
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"==onPause==");
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.0f;
        lp.dimAmount = 0.0f;
        getWindow().setAttributes(lp);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"==onStart==");
    }

    @Override
    protected void onResume() {
        super.onResume();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1.0f;
        lp.dimAmount = 0.5f;
        getWindow().setAttributes(lp);
        Log.d(TAG,"==onResume==");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG,"==onRestart==");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"==onStop==");
    }
}
