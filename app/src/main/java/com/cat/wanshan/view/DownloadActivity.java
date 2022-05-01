package com.cat.wanshan.view;


import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;
import com.cat.wanshan.R;
import com.cc.baselibrary.base.BaseActivity;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.OnDownloadListener;

import java.io.File;
import java.util.List;

public class DownloadActivity extends BaseActivity {
    public static final String TAG = "UploadActivity";

    private ProgressBar mProgressBar;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_download;
    }

    @Override
    protected void initView() {
        mProgressBar = findViewById(R.id.progress_bar_view);
        TextView btnClick = findViewById(R.id.download_btn);
        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission(view);
            }
        });
    }


    public void checkPermission(View v) {
        if (!PermissionUtils.isGranted(PermissionConstants.STORAGE)) {
            PermissionUtils.permissionGroup(PermissionConstants.STORAGE).callback(new PermissionUtils.FullCallback() {
                @Override
                public void onGranted(@NonNull List<String> granted) {
                    Toast.makeText(mContext, "已经允许", Toast.LENGTH_SHORT).show();
                    downloadFile();
                }

                @Override
                public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {
                    Toast.makeText(mContext, "拒绝了", Toast.LENGTH_SHORT).show();
                }
            }).request();
        } else {
            downloadFile();
        }
    }

    private void downloadFile() {
        final String url = "http://cdn.to-future.net/apk/tofuture.apk";
        File downloadFile = new File(new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "wanshan"), "to-future.apk");
        HttpRequest.DOWNLOAD(mContext, url, downloadFile,
                new OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess(File file) {
                        Log.d(TAG, "文件已下载完成:" + file.getAbsolutePath());
                    }

                    @Override
                    public void onDownloading(int progress) {
                        mProgressBar.setProgress(progress);
                        Log.d(TAG, "progress:" + progress);
                    }

                    @Override
                    public void onDownloadFailed(Exception e) {
                        Log.d(TAG, "download fail:" + e);
                    }
                }
        );
    }


}
