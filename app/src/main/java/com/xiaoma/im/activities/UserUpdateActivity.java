package com.xiaoma.im.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoma.im.R;
import com.xiaoma.im.constant.Constants;
import com.xiaoma.im.constant.MessageConstants;
import com.xiaoma.im.entity.MessagePackage;
import com.xiaoma.im.entity.UserInfo;
import com.xiaoma.im.enums.ConstellationEnum;
import com.xiaoma.im.enums.GenderEnum;
import com.xiaoma.im.service.ImClient;
import com.xiaoma.im.utils.DataMapUtils;
import com.xiaoma.im.utils.OssManager;
import com.xiaoma.im.utils.UuidUtils;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import io.netty.channel.socket.SocketChannel;


public class UserUpdateActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText tvInfoNickName, tvInfoSign, tvInfoGender, tvInfoBirth, tvInfoConstellation, tvInfoEmail;
    private Button btUpdate;
    private ImageView circleImageViewv;
    public static Context context;
    private LocalBroadcastManager localBroadcastManager;
    private LocalReceiver localReceiver;
    private String userAccount;
    private Uri uri;
    private String path = null;
    public static final int LOCAL_CODE = 1;
    public static final int CAMERA_CODE = 2;
    public String uuid = UuidUtils.getUuid();
    private String today = DateUtil.format(new Date(), "yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_update);
        Intent intent = this.getIntent();
        userAccount = intent.getStringExtra("userAccount");
        initView();
        initData();
        setListener();
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.xiaoma.im.fragment.UserUpdateActivity");
        localReceiver = new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);
    }

    private void setListener() {
        btUpdate.setOnClickListener(v -> {
            OssManager builder = new OssManager
                    .Builder(context)
                    .accessKeyId(Constants.ACCESS_KEYID)
                    .accessKeySecret(Constants.ACCESS_KEYID_SECRET)
                    .bucketName(Constants.BUCKET_NAME)
                    .endPoint(Constants.END_POINT)
                    .objectKey(today + "/" + uuid + ".jpg")
                    .localFilePath(path)
                    .build();
            builder.push();
        });
        circleImageViewv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_state_activity_smile:
                //dialog的view,通过getLayoutInflater初始化
                View view = getLayoutInflater().inflate(R.layout.item_photo_info, null);
                TextView textView_local = view.findViewById(R.id.tv_info_item_local);
                TextView textView_camera = view.findViewById(R.id.tv_info_item_camera);
                //初始化dialog
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(view).create();
                final AlertDialog dialog = builder.show();
                //给dialog的选项建立监听器
                textView_local.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //获取本地相册
                        getPermission("Local");
                        dialog.dismiss();
                    }
                });
                textView_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //获取相机
                        getPermission("Camera");
                        dialog.dismiss();
                    }
                });
                break;
        }
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int options = intent.getIntExtra(MessageConstants.USER_INFO_UPDATE__BROADCAST, 0);
            switch (options) {
                case Constants.SUCCESS:
                    Toast.makeText(context, "信息修改成功!", Toast.LENGTH_SHORT).show();
                    UserUpdateActivity.this.finish();
                    break;
                case Constants.FAILED:
                    Toast.makeText(context, "信息修改失败!", Toast.LENGTH_SHORT).show();
                    break;
                case MessageConstants.USER_INFO_UPDATE_SUCCESS:
                    Toast.makeText(context, "图片上传成功!", Toast.LENGTH_SHORT).show();
                    int gender = GenderEnum.getGender(tvInfoGender.getText().toString());
                    int constellation = ConstellationEnum.getMessage(tvInfoConstellation.getText().toString());
                    MessagePackage messagePackage = new MessagePackage();
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUserAccount(userAccount);
                    userInfo.setUserHeadPhoto(Constants.fixPhotoUrl(today, uuid));
                    userInfo.setUserNickName(tvInfoNickName.getText().toString());
                    userInfo.setUserEmail(tvInfoEmail.getText().toString());
                    userInfo.setUserSign(tvInfoSign.getText().toString());
                    userInfo.setUserConstellation(constellation);
                    userInfo.setUserGender(gender);
                    messagePackage.setType(Constants.ME_INFO_UPDATE);
                    messagePackage.setLength(ObjectUtil.serialize(userInfo).length);
                    messagePackage.setContent(ObjectUtil.serialize(userInfo));
                    SocketChannel instance = ImClient.getInstance();
                    instance.writeAndFlush(messagePackage);
                    DataMapUtils.getInstance().putObj(MessageConstants.USER_INFO_MAP, userInfo);
                    break;
                case MessageConstants.USER_INFO_UPDATE_FAILED:
                    Toast.makeText(context, "图片上传失败!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private void initData() {
        context = UserUpdateActivity.this;
        this.updateDisplay();
    }

    private void updateDisplay() {
        UserInfo user = (UserInfo) DataMapUtils.getInstance().getObj(MessageConstants.USER_INFO_MAP);
        String gender = GenderEnum.GENDER_MAN.getCode().equals(user.getUserGender()) ? GenderEnum.GENDER_MAN.getGender() : GenderEnum.GENDER_WOMAM.getGender();
        String constellation = ConstellationEnum.getMessage(user.getUserConstellation());
        String birth = DateUtil.format(user.getUserBirthday(), "yyyy-MM-dd");
        UserUpdateActivity.this.runOnUiThread(() -> {
            tvInfoConstellation.setText(MessageFormat.format("{0}", constellation));
            tvInfoEmail.setText(String.format("%s", user.getUserEmail()));
            tvInfoGender.setText(MessageFormat.format("{0}", gender));
            tvInfoNickName.setText(MessageFormat.format("{0}", user.getUserNickName()));
            tvInfoSign.setText(MessageFormat.format("{0}", user.getUserSign()));
            tvInfoBirth.setText(MessageFormat.format("{0}", birth));
        });
    }

    private void initView() {
        tvInfoBirth = findViewById(R.id.tv_info_activity_birth);
        tvInfoNickName = findViewById(R.id.tv_info_activity_nick_name);
        tvInfoConstellation = findViewById(R.id.tv_info_activity_constellation);
        tvInfoEmail = findViewById(R.id.tv_info_activity_email);
        tvInfoSign = findViewById(R.id.tv_info_activity_sign);
        tvInfoGender = findViewById(R.id.tv_info_activity_gender);
        btUpdate = findViewById(R.id.bt_info_activity_update);
        circleImageViewv = findViewById(R.id.iv_state_activity_smile);
    }

    private void getLocalPhoto() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, LOCAL_CODE);
    }

    @SuppressLint("ObsoleteSdkInt")
    private void getCameraPhoto() {
        //保存图片的位置
        File outputImage = new File(getExternalCacheDir(), "output_image");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //API 24以后URI不再是真实路径
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(context, "com.example.wechat.provider", outputImage);
        } else {
            uri = Uri.fromFile(outputImage);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, CAMERA_CODE);
    }

    //获取权限
    private void getPermission(String name) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UserUpdateActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 99);
        } else {
            switch (name) {
                case "Local":
                    getLocalPhoto();
                    break;
                case "Camera":
                    getCameraPhoto();
                    break;
                default:
                    break;
            }
        }
    }

    //获取权限回调onRequestPermissionsResult方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 99:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "获取权限成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "无法获取权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //调用相册或相机后返回的结果集
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LOCAL_CODE:
                //判断手机的版本号，Uri在API19以后不再是真实路径，需要得到真实的Uri
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageKikat(data);
                    } else {
                        handleBeforeImageKikat(data);
                    }
                }
                break;
            case CAMERA_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        circleImageViewv.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    //API 19
    private void handleImageKikat(Intent data) {
        String path = null;
        Uri uri = data.getData();
        //如果图片路径是一个文件，通过文件id处理
        if (DocumentsContract.isDocumentUri(context, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                //解析出数字id
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                path = getImagePaht(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                path = getImagePaht(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的URI，则使用普通方式
            path = getImagePaht(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file，直接获取路径
            path = uri.getPath();
        }
        displayImage(path);
    }

    //API before19
    private void handleBeforeImageKikat(Intent data) {
        Uri uri = data.getData();
        String path = getImagePaht(uri, null);
        displayImage(path);
    }

    //得到URI的真实路径
    private String getImagePaht(Uri uri, String selection) {
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    //将图片显示在页面上
    private void displayImage(String path) {
        if (path != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            circleImageViewv.setImageBitmap(bitmap);
        } else {
            Toast.makeText(context, "无法获取图片", Toast.LENGTH_SHORT).show();
        }
    }

}
