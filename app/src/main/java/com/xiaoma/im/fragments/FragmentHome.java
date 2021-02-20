package com.xiaoma.im.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.xiaoma.im.R;
import com.xiaoma.im.activities.MainActivity;
import com.xiaoma.im.adapter.RecyclerAdapterFriend;
import com.xiaoma.im.adapter.RecyclerAdapterHome;
import com.xiaoma.im.constant.BroadcastConstants;
import com.xiaoma.im.constant.MessageConstants;
import com.xiaoma.im.utils.DataMapUtils;
import com.xiaoma.im.vo.FriendsListVo;
import com.xiaoma.im.vo.ResponseMessageVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FragmentHome extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    public RecyclerAdapterHome recyclerAdapterHome;
    private String userAccount;
    private LocalReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;
    private List<ResponseMessageVo> responseMessageVos = new ArrayList<>();
    public static Context context;

    public FragmentHome() {
    }

    public void initData() {
        context = this.getActivity();
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        Bundle extras = this.getActivity().getIntent().getExtras();
        userAccount = (String) extras.get("userAccount");
        initView(view);
        initData();
        return view;
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.rv_home_list);
    }

    @Override
    public void onResume() {
        super.onResume();
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastConstants.UNREAD_MESSAGE);
        localReceiver = new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);
        recyclerAdapterHome = new RecyclerAdapterHome(responseMessageVos, userAccount);
        recyclerView.setAdapter(recyclerAdapterHome);
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int options = intent.getIntExtra(MessageConstants.USER_UNREAD, 0);
            switch (options) {
                case MessageConstants.USER_UNREAD_MESSAGE:
                    List<ResponseMessageVo> responseMessageVoList = (List<ResponseMessageVo>) DataMapUtils.getInstance().getObj(MessageConstants.USER_UNREAD_ADD_MAP);
                    if (!Objects.isNull(responseMessageVoList) && responseMessageVoList.size() > 0) {
                        getActivity().runOnUiThread(() -> {
                            if (responseMessageVos.size() > 0) {
                                responseMessageVos.clear();
                            }
                            responseMessageVos.addAll(responseMessageVoList);
                            recyclerAdapterHome.notifyDataSetChanged();
                            AppCompatActivity activity = (AppCompatActivity) getActivity();
                            ImageView redImageView = activity.findViewById(R.id.iv_main_red);
                            redImageView.setVisibility(View.VISIBLE);
                        });
                    }
                    break;
            }
        }
    }


}
