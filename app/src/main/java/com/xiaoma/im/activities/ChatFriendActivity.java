package com.xiaoma.im.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xiaoma.im.R;
import com.xiaoma.im.adapter.RecyclerviewChatAdapter;
import com.xiaoma.im.constant.BroadcastConstants;
import com.xiaoma.im.constant.Constants;
import com.xiaoma.im.constant.MessageConstants;
import com.xiaoma.im.entity.MessagePackage;
import com.xiaoma.im.entity.MessageSingle;
import com.xiaoma.im.entity.UserInfo;
import com.xiaoma.im.enums.MessageTypeEnum;
import com.xiaoma.im.response.FriendResponseDto;
import com.xiaoma.im.service.ImClient;
import com.xiaoma.im.utils.DataMapUtils;
import com.xiaoma.im.vo.FriendsListVo;
import com.xiaoma.im.vo.MessageSingleVo;
import com.xiaoma.im.vo.ResponseMessageVo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.hutool.core.util.ObjectUtil;
import io.netty.util.CharsetUtil;

public class ChatFriendActivity extends AppCompatActivity implements View.OnClickListener {

    private int friendId;
    private String friendName;
    private String userAccount;
    public RecyclerviewChatAdapter recyclerviewChatAdapter;
    private RecyclerView recycler;
    public static Context context;
    private LocalBroadcastManager localBroadcastManager;
    private LocalReceiver localReceiver;
    private EditText editText;
    private TextView textView;
    private Button send;
    private List<MessageSingle> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_chat_friend);
        Intent intent = this.getIntent();
        friendId = intent.getIntExtra("friendId", -1);
        friendName = intent.getStringExtra("friendName");
        userAccount = intent.getStringExtra("userId");
        initView();
        initData();
        setListener();
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastConstants.FRIENDS_MESSAGE_ACTIVITY);
        intentFilter.addAction(BroadcastConstants.UNREAD_MESSAGE);
        localReceiver = new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        recycler.setAdapter(recyclerviewChatAdapter);
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int options = intent.getIntExtra(MessageConstants.FRIENDS_MESSAGE_ACTIVITY_BROADCAST, 0);
            switch (options) {
                case MessageConstants.FRIENDS_MESSAGE_ACTIVITY_VALUE:
                    ChatFriendActivity.this.runOnUiThread(() -> {
                        if (list.size() > 0) {
                            list.clear();
                        }
                        list.addAll((List<MessageSingle>) DataMapUtils.getInstance().getObj(MessageConstants.FRIENDS_MESSAGE_ACTIVITY_MAP));
                        recyclerviewChatAdapter.notifyDataSetChanged();
                        recycler.scrollToPosition(recyclerviewChatAdapter.getItemCount() - 1);
                    });
                    break;
            }
        }
    }


    private void initView() {
        recycler = findViewById(R.id.avtivity_recyclerview_chat);
        editText = findViewById(R.id.activity_et_chat_content);
        send = findViewById(R.id.activity_bt_char_send);
        textView = findViewById(R.id.activity_tv_username);
    }

    private void initData() {
        context = this.getApplicationContext();
        LinearLayoutManager manager = new LinearLayoutManager(context);
        recycler.setLayoutManager(manager);
        recyclerviewChatAdapter = new RecyclerviewChatAdapter(list);
        textView.setText(friendName);
    }


    private void setListener() {
        send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_bt_char_send:
                if (editText.getText() == null || "".equals(editText.getText().toString())) {
                    Toast.makeText(context, "发送消息为空！", Toast.LENGTH_SHORT).show();
                } else {
                    UserInfo user = (UserInfo) DataMapUtils.getInstance().getObj(MessageConstants.USER_INFO_MAP);
                    MessageSingleVo messageSingleVo = new MessageSingleVo();
                    messageSingleVo.setCommandType(MessageTypeEnum.MessageType_STRING.getCode());
                    messageSingleVo.setSenderid(user.getId());
                    messageSingleVo.setCreateTime(new Date());
                    List<FriendsListVo> listVos = (List<FriendsListVo>) DataMapUtils.getInstance().getObj(MessageConstants.FRIENDS_INFO_MAP);
                    listVos.forEach(item -> {
                        if (item.getId() == friendId) {
                            messageSingleVo.setFriendAccount(item.getUserAccount());
                        }
                    });
                    messageSingleVo.setReceiverid(friendId);
                    messageSingleVo.setMessage(editText.getText().toString());
                    messageSingleVo.setUserAccount(userAccount);
                    MessagePackage aPackage = new MessagePackage();
                    aPackage.setType(Constants.SEND);
                    aPackage.setLength(ObjectUtil.serialize(messageSingleVo).length);
                    aPackage.setContent(ObjectUtil.serialize(messageSingleVo));
                    ImClient.getInstance().writeAndFlush(aPackage);
                    // 刷新数据
                    MessagePackage messagePackage = new MessagePackage();
                    String s = user.getUserAccount() + "," + friendId;
                    messagePackage.setType(Constants.FRIEND_LIST_MESSAGE);
                    messagePackage.setLength(s.getBytes(CharsetUtil.UTF_8).length);
                    messagePackage.setContent(s.getBytes(CharsetUtil.UTF_8));
                    ImClient.getInstance().writeAndFlush(messagePackage);
                    editText.setText("");
                }
                break;
        }
    }

}


