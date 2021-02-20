package com.xiaoma.im.activities;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.xiaoma.im.R;
import com.xiaoma.im.constant.BroadcastConstants;
import com.xiaoma.im.constant.Constants;
import com.xiaoma.im.constant.MessageConstants;
import com.xiaoma.im.entity.MessagePackage;
import com.xiaoma.im.enums.ResponseEnum;
import com.xiaoma.im.fragments.FragmentFriend;
import com.xiaoma.im.fragments.FragmentHome;
import com.xiaoma.im.fragments.FragmentInfo;
import com.xiaoma.im.response.FriendResponseDto;
import com.xiaoma.im.response.ResultBase;
import com.xiaoma.im.service.ImClient;
import com.xiaoma.im.utils.CallBackUtil;
import com.xiaoma.im.utils.DataMapUtils;
import com.xiaoma.im.utils.OkhttpUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FragmentManager fragmentManager;
    private FragmentFriend fragment_friend;
    private FragmentHome fragment_home;
    private FragmentInfo fragment_info;
    private TextView tv_title;
    private Context context = MainActivity.this;
    private ImageView iv_red_point, iv_friend, iv_home, iv_info;
    private RelativeLayout relativeLayout;
    private NotificationManager notificationManager;
    private String userAccount;
    private LocalBroadcastManager localBroadcastManager;
    private LocalReceiver localReceiver;

    public String getUserAccount() {
        return userAccount;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        setListener();
        Bundle extras = MainActivity.this.getIntent().getExtras();
        userAccount = extras.getString("userAccount");
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastConstants.FRIENDS_MESSAGE_ACTIVITY);
        intentFilter.addAction(BroadcastConstants.UNREAD_MESSAGE);
        localReceiver = new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);
        new Thread(() -> {
            try {
                MessagePackage meInfo = new MessagePackage();
                meInfo.setType(Constants.ME_INFO);
                meInfo.setLength(userAccount.getBytes().length);
                meInfo.setContent(userAccount.getBytes());
                ImClient.getInstance().writeAndFlush(meInfo);
                TimeUnit.SECONDS.sleep(3);
                MessagePackage friendList = new MessagePackage();
                friendList.setType(Constants.FRIENDS_LIST);
                friendList.setLength(userAccount.getBytes().length);
                friendList.setContent(userAccount.getBytes());
                ImClient.getInstance().writeAndFlush(friendList);
                TimeUnit.SECONDS.sleep(3);
                MessagePackage received = new MessagePackage();
                received.setType(Constants.RECEIVED);
                received.setLength(userAccount.getBytes().length);
                received.setContent(userAccount.getBytes());
                ImClient.getInstance().writeAndFlush(received);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideFrament(1);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int options = intent.getIntExtra(MessageConstants.USER_UNREAD, 0);
            switch (options) {
                case MessageConstants.USER_UNREAD_ADD:
                    FriendResponseDto friendResponseDto = (FriendResponseDto) DataMapUtils.getInstance().getObj(MessageConstants.USER_UNREAD_ADD_MAP);
                    View view = getLayoutInflater().inflate(R.layout.item_add_dialog, null);
                    final TextView friend = view.findViewById(R.id.tv_item_add_friend);
                    final TextView tip = view.findViewById(R.id.tv_item_add_tip);
                    final CircleImageView photo = view.findViewById(R.id.iv_item_add);
                    friend.setText(friendResponseDto.getFriendAccount());
                    if (Objects.isNull(friendResponseDto.getPhoto()) || "".equals(friendResponseDto.getPhoto())) {
                        photo.setBackgroundResource(R.drawable.preview);
                    } else {
                        Glide.with(context).load(friendResponseDto.getPhoto()).into(photo);
                    }
                    final EditText nickname = view.findViewById(R.id.et_item_add);
                    final Button sure = view.findViewById(R.id.bt_item_add_sure);
                    final Button cancel = view.findViewById(R.id.bt_item_add_cancel);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setView(view).create();
                    final AlertDialog dialog = builder.show();
                    sure.setOnClickListener(v1 -> {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("userAccount", userAccount);
                        map.put("usernickname", nickname.getText().toString());
                        map.put("friendAccount", friendResponseDto.getFriendAccount());
                        map.put("nickname", friendResponseDto.getFriendNickName());
                        OkhttpUtil.okHttpGet("http://10.0.2.2:8001/friends/agree", map, new CallBackUtil<ResultBase>() {
                            @Override
                            public ResultBase onParseResponse(Call call, Response response) {
                                try {
                                    ResultBase resultBase = JSON.parseObject(response.body().string(), ResultBase.class);
                                    if (Objects.equals(resultBase.getCode(), ResponseEnum.RESPONSE_SUCCESS.getCode())) {
                                        MainActivity.this.runOnUiThread(() -> {
                                            Toast.makeText(context, "添加成功！", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        });
                                        new Thread(() -> {
                                            MessagePackage friendList = new MessagePackage();
                                            friendList.setType(Constants.FRIENDS_LIST);
                                            friendList.setLength(userAccount.getBytes().length);
                                            friendList.setContent(userAccount.getBytes());
                                            ImClient.getInstance().writeAndFlush(friendList);
                                        }).start();
                                    } else {
                                        MainActivity.this.runOnUiThread(() -> Toast.makeText(context, "请求失败！", Toast.LENGTH_SHORT).show());
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
                    });
                    cancel.setOnClickListener(v12 -> dialog.dismiss());
                    break;
            }
        }
    }

    private void setListener() {
        iv_home.setOnClickListener(this);
        iv_friend.setOnClickListener(this);
        iv_info.setOnClickListener(this);
        relativeLayout.setOnClickListener(this);
    }

    private void initData() {
        fragmentManager = getSupportFragmentManager();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void initView() {
        iv_home = findViewById(R.id.iv_main_messege);
        iv_friend = findViewById(R.id.iv_main_home);
        iv_info = findViewById(R.id.iv_main_state);
        tv_title = findViewById(R.id.tv_main_title);
        iv_red_point = findViewById(R.id.iv_main_red);
        iv_red_point.setVisibility(View.GONE);
        relativeLayout = findViewById(R.id.rl_main_reach);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_main_messege:
                hideFrament(1);
                break;
            case R.id.iv_main_home:
                hideFrament(2);
                break;
            case R.id.iv_main_state:
                hideFrament(3);
                break;
            case R.id.rl_main_reach:
                View view = getLayoutInflater().inflate(R.layout.item_reach_dialog, null);
                final EditText editText = view.findViewById(R.id.et_dialog_reach);
                final EditText etNickname = view.findViewById(R.id.et_dialog_nickname);
                Button sure = view.findViewById(R.id.bt_dialog_sure);
                Button cancel = view.findViewById(R.id.bt_dialog_cnacel);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(view).create();
                final AlertDialog dialog = builder.show();
                sure.setOnClickListener(v1 -> {
                    String friendAccount = editText.getText().toString();
                    String nickname = etNickname.getText().toString();
                    if (!"".equals(friendAccount)) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("userAccount", userAccount);
                        map.put("friendAccount", friendAccount);
                        map.put("nickname",nickname);
                        OkhttpUtil.okHttpGet("http://10.0.2.2:8001/friends/apply", map, new CallBackUtil<ResultBase>() {
                            @Override
                            public ResultBase onParseResponse(Call call, Response response) {
                                try {
                                    ResultBase resultBase = JSON.parseObject(response.body().string(), ResultBase.class);
                                    if(resultBase.getCode().equals(ResponseEnum.RESPONSE_VALID.getCode())) {
                                        MainActivity.this.runOnUiThread(() -> {
                                            Toast.makeText(context, "非法账号！", Toast.LENGTH_SHORT).show();
                                            editText.setText("");
                                        });
                                    } else if (resultBase.getCode().equals(ResponseEnum.RESPONSE_NOT_FIND.getCode())) {
                                        MainActivity.this.runOnUiThread(() -> {
                                            Toast.makeText(context, "找不到该用户！", Toast.LENGTH_SHORT).show();
                                            editText.setText("");
                                        });
                                    } else {
                                        MainActivity.this.runOnUiThread(() -> {
                                            Toast.makeText(context, "消息已发送！", Toast.LENGTH_SHORT).show();
                                            editText.setText("");
                                            dialog.dismiss();
                                        });
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            public void onFailure(Call call, Exception e) {
                                MainActivity.this.runOnUiThread(() -> Toast.makeText(context, "网络错误请重试！", Toast.LENGTH_SHORT).show());
                            }

                            @Override
                            public void onResponse(ResultBase response) {

                            }
                        });
                    } else {
                        Toast.makeText(context, "请输入用户账号！", Toast.LENGTH_SHORT).show();
                    }
                });
                cancel.setOnClickListener(v12 -> dialog.dismiss());
                break;
        }
    }

    private void hideFrament(int index) {
        setBackColor(index);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        fragmentHide(transaction);
        switch (index) {
            case 1:
                if (fragment_home == null) {
                    fragment_home = new FragmentHome();
                    transaction.add(R.id.fragment, fragment_home);
                } else {
                    transaction.show(fragment_home);
                }
                break;
            case 2:
                if (fragment_friend == null) {
                    fragment_friend = new FragmentFriend();
                    transaction.add(R.id.fragment, fragment_friend);
                } else {
                    transaction.show(fragment_friend);
                }
                break;
            case 3:
                if (fragment_info == null) {
                    fragment_info = new FragmentInfo();
                    transaction.add(R.id.fragment, fragment_info);
                } else {
                    transaction.show(fragment_info);
                }
                break;
        }
        transaction.commit();
    }

    private void fragmentHide(FragmentTransaction transaction) {
        if (fragment_friend != null) transaction.hide(fragment_friend);
        if (fragment_info != null) transaction.hide(fragment_info);
        if (fragment_home != null) transaction.hide(fragment_home);
    }

    private void setBackColor(int i) {
        iv_home.setImageResource(R.drawable.default_message);
        iv_friend.setImageResource(R.drawable.default_home);
        iv_info.setImageResource(R.drawable.default_info);
        switch (i) {
            case 1:
                tv_title.setText("消息");
                iv_home.setImageResource(R.drawable.active_message);
                break;
            case 2:
                tv_title.setText("联系人");
                iv_friend.setImageResource(R.drawable.active_home);
                break;
            case 3:
                tv_title.setText("动态");
                iv_info.setImageResource(R.drawable.active_info);
                break;
        }
    }

}
