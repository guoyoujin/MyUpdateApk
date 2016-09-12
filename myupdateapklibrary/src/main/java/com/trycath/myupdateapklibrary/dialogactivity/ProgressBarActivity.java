package com.trycath.myupdateapklibrary.dialogactivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.trycath.myupdateapklibrary.R;
import com.trycath.myupdateapklibrary.model.DownloadModel;
import com.trycath.myupdateapklibrary.util.StringUtils;

public class ProgressBarActivity extends AppCompatActivity {
    private ProgressBar downloaddialog_progress;
    private TextView downloaddialog_count;
    private ImageView downloaddialog_close;
    public static final String TAG = ProgressBarActivity.class.getSimpleName();
    public static final String MESSAGE_PROGRESS = "message_progress";
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MESSAGE_PROGRESS)) {
                DownloadModel download = intent.getParcelableExtra("download");
                downloaddialog_progress.setProgress(download.getProgress());
                if (download.getProgress() == 100) {
                    downloaddialog_count.setText("File Download Complete");
                } else {
                    downloaddialog_count.setText(
                            StringUtils.getDataSize(download.getCurrentFileSize())+ "/" + StringUtils.getDataSize(download.getTotalFileSize()));
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_bar);
        initView();
        registerReceiver();
    }
    
    private void registerReceiver() {
        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MESSAGE_PROGRESS);
        bManager.registerReceiver(broadcastReceiver, intentFilter);
    }
    
    public static void startActivity(Context context) {
        Intent intent = new Intent(context,ProgressBarActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
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
            
        }
    };
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver!=null){
            try {
                unregisterReceiver(broadcastReceiver);
            } catch (IllegalArgumentException e) {
                if (e.getMessage().contains("Receiver not registered")) {
                } else {
                    throw e;
                }
            }
        }
    }
}
