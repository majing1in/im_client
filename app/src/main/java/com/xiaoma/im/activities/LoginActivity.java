package com.xiaoma.im.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.xiaoma.im.R;
import com.xiaoma.im.entity.UserInfo;
import com.xiaoma.im.enums.ResponseEnum;
import com.xiaoma.im.response.ResultBase;
import com.xiaoma.im.service.ImClient;
import com.xiaoma.im.utils.CallBackUtil;
import com.xiaoma.im.utils.GlobalMap;
import com.xiaoma.im.utils.OkhttpUtil;

import java.io.IOException;

import cn.hutool.core.util.ObjectUtil;
import io.netty.channel.socket.SocketChannel;
import okhttp3.Call;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etUserusername, etPassword;
    private Context context = null;
    private TextView tvRegistry;
    private Button btLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        tvRegistry = findViewById(R.id.tv_login_registry);
        btLogin = findViewById(R.id.bt_login_login);
        etUserusername = findViewById(R.id.et_login_username);
        etPassword = findViewById(R.id.et_login_password);
    }

    private void initData() {
        context = LoginActivity.this;
    }

    private void setListener() {
        tvRegistry.setOnClickListener(this);
        btLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_login_registry:
                startActivity(new Intent(context, RegistryActivity.class));
                break;
            case R.id.bt_login_login:
                String username = etUserusername.getText().toString();
                String password = etPassword.getText().toString();
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "用户名或者密码为空", Toast.LENGTH_SHORT).show();
                } else {
                    final UserInfo userInfo = new UserInfo();
                    userInfo.setUserAccount(username);
                    userInfo.setUserPassword(password);
                    OkhttpUtil.okHttpPostJson("http://10.0.2.2:8001/user/login", JSON.toJSONString(userInfo), new CallBackUtil<ResultBase>() {
                        @Override
                        public ResultBase onParseResponse(Call call, Response response) {
                            try {
                                ResultBase resultBase = JSON.parseObject(response.body().string(), ResultBase.class);
                                if (ObjectUtil.equals(resultBase.getCode(),ResponseEnum.RESPONSE_SUCCESS.getCode())) {
                                    SocketChannel instance = null;
                                    while (ImClient.COUNT < ImClient.MAXCOUNT) {
                                        if (instance == null || !ImClient.SUCCESS) {
                                            instance = ImClient.getInstance();
                                            continue;
                                        }
                                        break;
                                    }
                                    if (instance == null || !ImClient.SUCCESS) {
                                        boolean serverOnline = LoginActivity.this.sendToServerOnline(userInfo);
                                        if (serverOnline) {
                                            LoginActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(context, "无法连接服务器，请检查网络是否开启！", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    } else {
                                        GlobalMap.put("global_session", resultBase.getData().toString());
                                        LoginActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(context, "登录成功！", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent();
                                                intent.putExtra("userAccount", username);
                                                intent.setClass(LoginActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                LoginActivity.this.finish();
                                            }
                                        });
                                    }
                                } else {
                                    LoginActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(context, "登录失败,用户名或密码错误！", Toast.LENGTH_SHORT).show();
                                            etUserusername.setText("");
                                            etPassword.setText("");
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
                            System.out.println(JSON.toJSONString(e));
                        }

                        @Override
                        public void onResponse(ResultBase response) {
                            System.out.println(JSON.toJSONString(response));
                        }
                    });
                }
                break;
        }
    }

    private boolean sendToServerOnline(UserInfo userInfo) {
        OkhttpUtil.okHttpPostJson("http://10.0.2.2:8001/user/online", JSON.toJSONString(userInfo), new CallBackUtil<ResultBase>() {
            @Override
            public ResultBase onParseResponse(Call call, Response response) {
                return null;
            }

            @Override
            public void onFailure(Call call, Exception e) {

            }

            @Override
            public void onResponse(ResultBase response) {

            }
        });
        return true;
    }

}
