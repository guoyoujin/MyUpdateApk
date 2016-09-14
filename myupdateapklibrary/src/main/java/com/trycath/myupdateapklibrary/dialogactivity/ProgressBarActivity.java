package com.trycath.myupdateapklibrary.dialogactivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.trycath.myupdateapklibrary.R;
import com.trycath.myupdateapklibrary.UpdateKey;
import com.trycath.myupdateapklibrary.model.AppInfoModel;
import com.trycath.myupdateapklibrary.model.DownloadModel;
import com.trycath.myupdateapklibrary.rxbus.RxBus;
import com.trycath.myupdateapklibrary.rxbus.RxBusResult;
import com.trycath.myupdateapklibrary.util.StringUtils;

public class ProgressBarActivity extends AppCompatActivity {
    private ProgressBar downloaddialog_progress;
    private TextView downloaddialog_count;
    private TextView tv_title;
    private ImageView downloaddialog_close;
    public static final String TAG = ProgressBarActivity.class.getSimpleName();
    public static final String MESSAGE_PROGRESS = "MESSAGE_PROGRESS";
    public static final String MESSAGE_COLOSE = "MESSAGE_COLOSE";
    private RxBus rxBus = RxBus.getInstance();
    private AppInfoModel appInfoModel ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_bar);
        Log.d(TAG,"onCreate");
        if(getIntent()!=null&&getIntent().getExtras()!=null&&getIntent().getExtras().getSerializable(PromptDialogActivity.INTENT_DOWNLOAD_MODEL)!=null) {
            appInfoModel = (AppInfoModel) getIntent().getExtras().getSerializable(PromptDialogActivity.INTENT_DOWNLOAD_MODEL);
            initView();
            initRxBusProgressDownload();
        }else{
            finish();
        }
        
    }
    
    private void initRxBusProgressDownload(){
        Log.d(TAG,"initRxBusProgressDownload");
        rxBus.toObserverableOnMainThread(ProgressBarActivity.MESSAGE_PROGRESS, new RxBusResult() {
            @Override
            public void onRxBusResult(Object o) {
                if(o instanceof  DownloadModel){
                    DownloadModel download = (DownloadModel) o;
                    downloaddialog_progress.setProgress(download.getProgress());
                    if (download.getProgress() == 100) {
                        downloaddialog_count.setText(getResources().getString(R.string.download_successful));
                        finish();
                    } else {
                        downloaddialog_count.setText(StringUtils.getDataSize(download.getCurrentFileSize())+ "/" + StringUtils.getDataSize(download.getTotalFileSize()));
                    }
                }
            }
        });
    }

    private void initView(){
        Log.d(TAG,"initView");
        downloaddialog_progress = (ProgressBar) findViewById(R.id.downloaddialog_progress);
        downloaddialog_count = (TextView) findViewById(R.id.downloaddialog_count);
        downloaddialog_close = (ImageView) findViewById(R.id.downloaddialog_close);
        downloaddialog_close.setOnClickListener(onClickListenerDownLoadingClose);
        tv_title = (TextView) findViewById(R.id.tv_title);
        if(UpdateKey.DOWNLOAD_NAME!=null&&!"".equals(UpdateKey.DOWNLOAD_NAME)){
            tv_title.setText(String.format("%s%s",getResources().getString(R.string.is_downloading),UpdateKey.DOWNLOAD_NAME));
        }else{
            tv_title.setText(String.format("%s%s",getResources().getString(R.string.is_downloading),appInfoModel.getName()));

        }
    }
    
    View.OnClickListener onClickListenerDownLoadingClose = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG,"onClickListenerDownLoadingClose");
            rxBus.post(ProgressBarActivity.MESSAGE_COLOSE,ProgressBarActivity.MESSAGE_COLOSE);
            finish();
        }
    };
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        if(rxBus!=null){
            rxBus.removeObserverable(ProgressBarActivity.MESSAGE_COLOSE);
        }
    }
    public static void startActivity(Context context,AppInfoModel appInfoModel) {
        Log.d(TAG,"startActivity");
        Intent intent = new Intent(context,ProgressBarActivity.class);
        intent.putExtra(PromptDialogActivity.INTENT_DOWNLOAD_MODEL,appInfoModel);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG,"onRestoreInstanceState");
        appInfoModel = (AppInfoModel) savedInstanceState.getSerializable(PromptDialogActivity.INTENT_DOWNLOAD_MODEL);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG,"onSaveInstanceState");
        outState.putSerializable(PromptDialogActivity.INTENT_DOWNLOAD_MODEL,appInfoModel);
    }
}
