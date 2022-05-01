package com.cat.wanshan.view;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.FileUtils;
import com.cat.wanshan.R;
import com.cat.wanshan.utils.ContentUriUtil;
import com.cc.baselibrary.base.BaseActivity;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.ResponseListener;
import com.kongzue.baseokhttp.listener.UploadProgressListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadFilesActivity extends BaseActivity implements View.OnClickListener, UploadProgressListener {
    public static final int REQUEST_CODE = 2204;
    public static final String URL = "http://my.zol.com.cn/index.php?c=Ajax_User&a=uploadImg";

    public static final String TAG = "UploadFiles_Activity";
    private ProgressBar mProgressBar;
    private TextView mTextView;

    @Override
    protected int getContentLayoutId() {
        return R.layout.upload_files_activity;
    }

    @Override
    protected void initView() {
        mProgressBar = findViewById(R.id.progress_bar_view);
        mTextView = findViewById(R.id.message_text_view);
        View uploadButton = findViewById(R.id.upload_btn);
        uploadButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.upload_btn:
                selectFile();
                break;
        }

    }

    /**
     * 打开系统的文件选择器
     */
    public void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); //打开多个文件
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            List<File> fileList = new ArrayList<>();
            //Get the Uri of the selected file
            if (data.getClipData() != null) {//有选择多个文件
                int count = data.getClipData().getItemCount();
                Log.i(TAG, "url count ：  " + count);
                int currentItem = 0;
                while (currentItem < count) {
                    Uri imageUri = data.getClipData().getItemAt(currentItem).getUri();
                    String imgpath = ContentUriUtil.getPath(this, imageUri);
                    if (imgpath == null) {
                        return;
                    }
                    fileList.add(new File(imgpath));
                    uploadFile(fileList);
                    Log.i(TAG, "url " + imgpath);
                    //do something with the image (save it to some directory or whatever you need to do with it here)
                    currentItem = currentItem + 1;
                }

            } else if (data.getData() != null) {//只有一个文件咯
                String imagePath = ContentUriUtil.getPath(this, data.getData());
                Log.i(TAG, "Single image path ---- " + imagePath);
                if (imagePath == null) {
                    return;
                }
                File fileByPath = FileUtils.getFileByPath(imagePath);
                fileList.add(fileByPath);
                uploadFile(fileList);
                //do something with the image (save it to some directory or whatever you need to do with it here)
            }
        }
    }


    // 使用OkHttp上传文件
    public void uploadFile(List<File> fileList) {
        HttpRequest.build(mContext, URL)
                .addParameter("myPhoto", fileList)
                .setUploadProgressListener(this::onUpload)
                .setResponseListener(new ResponseListener() {
                    @Override
                    public void onResponse(String response, Exception error) {
                        if (error == null) {
                            Log.d(TAG, response);
                            mTextView.setText(response);
                        } else {
                            Log.d(TAG, "请求失败");
                            mTextView.setText("请求失败");
                        }
                    }
                }).doPost();
    }


    @Override
    public void onUpload(float percentage, long current, long total, boolean done) {
        Log.e(TAG, "上传进度: 百分比（float）=" + percentage + "\t" + "已上传字节数=" + current + "\t" +
                "总字节数=" + total + "\t" +
                "是否已完成=" + done);
        int progress = (int) (percentage * 100);
        Log.d(TAG, "progress:" + progress);
        mProgressBar.setProgress(progress);
        mTextView.setText("上传进度: 百分比（float）" + percentage + "\n" + "已上传字节数:" + current + "\n" +
                "总字节数:" + total + "\n" +
                "是否已完成:" + done);
    }

}
