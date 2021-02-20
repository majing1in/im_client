package com.xiaoma.im.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonParser;
import com.xiaoma.im.R;
import com.xiaoma.im.enums.ResponseEnum;
import com.xiaoma.im.response.ResultBase;
import com.xiaoma.im.utils.CallBackUtil;
import com.xiaoma.im.utils.OkhttpUtil;
import com.xiaoma.im.utils.UuidUtils;
import com.xiaoma.im.vo.UserInfoVo;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 注册页面 原本来图片选择，暂时关闭这个功能
 */
public class RegistryActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    // private ImageView imageView;
    private Button registry;
    private EditText useruserAccount, password, again, et_token;
    private final Context context = RegistryActivity.this;
    private TextView token;
    private Uri uri = null;
    private String path = null;

    private String auth;

    public static final int LOCAL_CODE = 1;
    public static final int CAMERA_CODE = 2;

    private String uuid = UuidUtils.getUuid();

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);
        if (Build.VERSION.SDK_INT >= 11) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
        }
        initView();
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.sendToServer();
    }

    private void sendToServer() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("uuid", uuid);
        OkhttpUtil.okHttpGet("http://10.0.2.2:8001/user/auth", null, headers, new CallBackUtil<ResultBase>() {
            @Override
            public ResultBase onParseResponse(Call call, Response response) {
                try {
                    auth = (String) JSON.parseObject(response.body().string(), ResultBase.class).getData();
                    RegistryActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            token.setText(auth);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onFailure(Call call, Exception e) {
            }

            @Override
            public void onResponse(ResultBase response) {
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        registry = findViewById(R.id.bt_registry_login);
        useruserAccount = findViewById(R.id.et_registry_username);
        password = findViewById(R.id.et_registry_password);
        again = findViewById(R.id.et_registry_passwordagain);
        toolbar = findViewById(R.id.tb_registry_title);
        token = findViewById(R.id.tv_registry_token);
        et_token = findViewById(R.id.et_registry_token);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        //设置actionbar的返回图标
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setListener() {
        // imageView.setOnClickListener(this);
        registry.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(ResultBase.menu.registry_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            //Registry的返回图标android.ResultBase.id.home
            case android.R.id.home:
                RegistryActivity.this.finish();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case ResultBase.id.iv_registry_head:
//                //dialog的view,通过getLayoutInflater初始化
//                View view = getLayoutInflater().inflate(ResultBase.layout.registry_dialog_item, null);
//                TextView textView_local = view.findViewById(ResultBase.id.tv_registry_item_local);
//                TextView textView_camera = view.findViewById(ResultBase.id.tv_registry_item_camera);
//                //初始化dialog
//                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setView(view).create();
//                final AlertDialog dialog = builder.show();
//                //给dialog的选项建立监听器
//                textView_local.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //获取本地相册
//                        getPermission("Local");
//                        dialog.dismiss();
//                    }
//                });
//                textView_camera.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //获取相机
//                        getPermission("Camera");
//                        dialog.dismiss();
//                    }
//                });
//                break;
            case R.id.bt_registry_login:
                String userAccount = useruserAccount.getText().toString();
                String userpassword = password.getText().toString();
                String userpasswordagain = again.getText().toString();
                String etToken = et_token.getText().toString();
                if (userpassword.isEmpty() || userAccount.isEmpty() || etToken.isEmpty()) {
                    Toast.makeText(context, "用户名或密码为空", Toast.LENGTH_SHORT).show();
                } else if (!userpassword.equals(userpasswordagain)) {
                    Toast.makeText(context, "两次密码不同", Toast.LENGTH_SHORT).show();
                    password.setText("");
                    again.setText("");
                } else if (userAccount.length() != 10) {
                    Toast.makeText(context, "用户名太长或过短", Toast.LENGTH_SHORT).show();
                    useruserAccount.setText("");
                } else {
                    if (!auth.equals(etToken)) {
                        Toast.makeText(context, "验证码错误！", Toast.LENGTH_SHORT).show();
                        et_token.setText("");
                    } else {
                        UserInfoVo userInfoVo = new UserInfoVo();
                        userInfoVo.setUserAccount(userAccount);
                        userInfoVo.setUserPassword(userpassword);
                        userInfoVo.setVerificationCode(auth);
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("uuid", uuid);
                        OkhttpUtil.okHttpPostJson("http://10.0.2.2:8001/user/register", JSON.toJSONString(userInfoVo), headers, new CallBackUtil<ResultBase>() {
                            @Override
                            public ResultBase onParseResponse(Call call, Response response) {
                                try {
                                    Integer code = JSON.parseObject(response.body().string(), ResultBase.class).getCode();
                                    if (ResponseEnum.RESPONSE_SUCCESS.getCode().equals(code)) {
                                        RegistryActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(context, "注册成功！", Toast.LENGTH_SHORT).show();
                                                RegistryActivity.this.finish();
                                            }
                                        });
                                    } else if (ResponseEnum.RESPONSE_TIMEOUT.getCode().equals(code)) {
                                        uuid = UuidUtils.getUuid();
                                        sendToServer();
                                        RegistryActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(context, "注册失败，请重试！", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        RegistryActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(context, "注册失败，请重试！", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            public void onFailure(Call call, Exception e) {

                            }

                            @Override
                            public void onResponse(ResultBase response) {

                            }
                        });
                    }
                }
                break;
        }
    }

    private void getLocalPhoto() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, LOCAL_CODE);
    }

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
    private void getPermission(String userAccount) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RegistryActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 99);
        } else {
            switch (userAccount) {
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
                        // imageView.setImageBitmap(bitmap);
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
        if (DocumentsContract.isDocumentUri(this, uri)) {
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
            // imageView.setImageBitmap(bitmap);
        } else {
            Toast.makeText(context, "无法获取图片", Toast.LENGTH_SHORT).show();
        }
    }
}
