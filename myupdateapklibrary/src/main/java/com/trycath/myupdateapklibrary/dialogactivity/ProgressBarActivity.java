package com.trycath.myupdateapklibrary.dialogactivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.trycath.myupdateapklibrary.R;
import com.trycath.myupdateapklibrary.model.DownloadModel;
import com.trycath.myupdateapklibrary.rxbus.RxBus;
import com.trycath.myupdateapklibrary.rxbus.RxBusResult;
import com.trycath.myupdateapklibrary.util.StringUtils;

public class ProgressBarActivity extends AppCompatActivity {
    private ProgressBar downloaddialog_progress;
    private TextView downloaddialog_count;
    private ImageView downloaddialog_close;
    public static final String TAG = ProgressBarActivity.class.getSimpleName();
    public static final String MESSAGE_PROGRESS = "MESSAGE_PROGRESS";
    public static final String MESSAGE_COLOSE = "MESSAGE_COLOSE";
    private RxBus rxBus = RxBus.getInstance();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_bar);
        initView();
        initRxBusProgressDownload();
        
    }
    
    private void initRxBusProgressDownload(){
        rxBus.toObserverableOnMainThread(ProgressBarActivity.MESSAGE_PROGRESS, new RxBusResult() {
            @Override
            public void onRxBusResult(Object o) {
                if(o instanceof  DownloadModel){
                    DownloadModel download = (DownloadModel) o;
                    downloaddialog_progress.setProgress(download.getProgress());
                    if (download.getProgress() == 100) {
                        downloaddialog_count.setText("File Download Complete");
                    } else {
                        downloaddialog_count.setText(StringUtils.getDataSize(download.getCurrentFileSize())+ "/" + StringUtils.getDataSize(download.getTotalFileSize()));
                    }
                }
            }
        });
    }

    private void initView(){
        downloaddialog_progress = (ProgressBar) findViewById(R.id.downloaddialog_progress);
        downloaddialog_count = (TextView) findViewById(R.id.downloaddialog_count);
        downloaddialog_close = (ImageView) findViewById(R.id.downloaddialog_close);
        downloaddialog_close.setOnClickListener(onClickListenerDownLoadingClose);
    }
    
    View.OnClickListener onClickListenerDownLoadingClose = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            rxBus.post(ProgressBarActivity.MESSAGE_COLOSE,ProgressBarActivity.MESSAGE_COLOSE);
            finish();
        }
    };
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(rxBus!=null){
            rxBus.release();
        }
    }
    public static void startActivity(Context context) {
        Intent intent = new Intent(context,ProgressBarActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
