package com.xiaoma.im.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xiaoma.im.R;
import com.xiaoma.im.adapter.RecyclerAdapterFriend;
import com.xiaoma.im.constant.BroadcastConstants;
import com.xiaoma.im.constant.Constants;
import com.xiaoma.im.constant.MessageConstants;
import com.xiaoma.im.entity.MessagePackage;
import com.xiaoma.im.service.ImClient;
import com.xiaoma.im.utils.DataMapUtils;
import com.xiaoma.im.vo.FriendsListVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FragmentFriend extends Fragment {

    private RecyclerView recycler;
    public static Context context;
    private LocalBroadcastManager localBroadcastManager;
    private LocalReceiver localReceiver;
    public RecyclerAdapterFriend recyclerAdapterFriend;
    private String userAccount;
    private List<FriendsListVo> friendsListVos = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend, container, false);
        Bundle extras = this.getActivity().getIntent().getExtras();
        userAccount = (String) extras.get("userAccount");
        initView(view);
        initData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastConstants.FRIENDS_INFO_FRAGMENT);
        localReceiver = new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);
        recyclerAdapterFriend = new RecyclerAdapterFriend(friendsListVos, userAccount);
        recycler.setAdapter(recyclerAdapterFriend);
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int options = intent.getIntExtra(MessageConstants.FRIENDS_INFO_UPDATE_BROADCAST, 0);
            switch (options) {
                case MessageConstants.FRIENDS_LIST_INFO:
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            friendsListVos.addAll((List<FriendsListVo>) DataMapUtils.getInstance().getObj(MessageConstants.FRIENDS_INFO_MAP));
                            recyclerAdapterFriend.notifyDataSetChanged();
                        }
                    });
                    break;
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initView(View view) {
        recycler = view.findViewById(R.id.rv_chat_list);
    }

    private void initData() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(manager);
        context = this.getActivity();
        List<FriendsListVo> listVos = (List<FriendsListVo>) DataMapUtils.getInstance().getObj(MessageConstants.FRIENDS_INFO_MAP);
        if (!Objects.isNull(listVos)) {
            friendsListVos.clear();
            friendsListVos.addAll(listVos);
        }
    }
}
