package com.xiaoma.im.fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.xiaoma.im.R;
import com.xiaoma.im.activities.UserUpdateActivity;
import com.xiaoma.im.constant.BroadcastConstants;
import com.xiaoma.im.constant.MessageConstants;
import com.xiaoma.im.entity.UserInfo;
import com.xiaoma.im.enums.ConstellationEnum;
import com.xiaoma.im.enums.GenderEnum;
import com.xiaoma.im.utils.DataMapUtils;
import com.xiaoma.im.utils.LogUtils;

import java.text.MessageFormat;
import java.util.Objects;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.xiaoma.im.utils.LogUtils.LOG_LEVEL_INFO;

public class FragmentInfo extends Fragment implements View.OnClickListener {

    private View view;
    private TextView tvInfoNickName, tvInfoSign, tvInfoGender, tvInfoBirth, tvInfoConstellation, tvInfoEmail;
    private Button btExit, btUpdate;
    private CircleImageView imageView;
    private LocalBroadcastManager localBroadcastManager;
    private LocalReceiver localReceiver;
    public static Context context;
    private String userAccount;

    public FragmentInfo() {
        LogUtils.setLogLevel(LOG_LEVEL_INFO);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_info, container, false);
        context = this.getActivity();
        Bundle extras = this.getActivity().getIntent().getExtras();
        userAccount = (String) extras.get("userAccount");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initView(view);
        initData();
        setListener();
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastConstants.USER_INFO_FRAGMENT);
        intentFilter.addAction(BroadcastConstants.UNREAD_MESSAGE);
        localReceiver = new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);
    }

    private void initData() {
        if(ObjectUtil.isNotEmpty(DataMapUtils.getInstance().getObj(MessageConstants.USER_INFO_MAP))) {
            UserInfo user = (UserInfo) DataMapUtils.getInstance().getObj(MessageConstants.USER_INFO_MAP);
            String birth = DateUtil.format(user.getUserBirthday(), "yyyy-MM-dd");
            String gender = GenderEnum.GENDER_MAN.getCode().equals(user.getUserGender()) ? GenderEnum.GENDER_MAN.getGender() : GenderEnum.GENDER_WOMAM.getGender();
            String constellation = ConstellationEnum.getMessage(user.getUserConstellation());
            Glide.with(context).load(user.getUserHeadPhoto()).into(imageView);
            tvInfoConstellation.setText(MessageFormat.format("星座：{0}", constellation));
            tvInfoEmail.setText(String.format("邮箱：%s", user.getUserEmail()));
            tvInfoGender.setText(MessageFormat.format("性别：{0}", gender));
            tvInfoNickName.setText(MessageFormat.format("昵称：{0}", user.getUserNickName()));
            tvInfoSign.setText(MessageFormat.format("个性签名：{0}", user.getUserSign()));
            tvInfoBirth.setText(MessageFormat.format("生日：{0}", birth));
        }
    }

    @Override
    public void onClick(View v) {

    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int options = intent.getIntExtra(MessageConstants.USER_INFO_BROADCAST, 0);
            if (options == MessageConstants.USER_INFO) {
                UserInfo user = (UserInfo) DataMapUtils.getInstance().getObj(MessageConstants.USER_INFO_MAP);
                String birth = DateUtil.format(user.getUserBirthday(), "yyyy-MM-dd");
                String gender = GenderEnum.GENDER_MAN.getCode().equals(user.getUserGender()) ? GenderEnum.GENDER_MAN.getGender() : GenderEnum.GENDER_WOMAM.getGender();
                String constellation = ConstellationEnum.getMessage(user.getUserConstellation());
                if (ObjectUtil.isNotEmpty(user)) {
                    Objects.requireNonNull(FragmentInfo.this.getActivity()).runOnUiThread(() -> {
                        Glide.with(context).load(user.getUserHeadPhoto()).into(imageView);
                        tvInfoConstellation.setText(MessageFormat.format("星座：{0}", constellation));
                        tvInfoEmail.setText(String.format("邮箱：%s", user.getUserEmail()));
                        tvInfoGender.setText(MessageFormat.format("性别：{0}", gender));
                        tvInfoNickName.setText(MessageFormat.format("昵称：{0}", user.getUserNickName()));
                        tvInfoSign.setText(MessageFormat.format("个性签名：{0}", user.getUserSign()));
                        tvInfoBirth.setText(MessageFormat.format("生日：{0}", birth));
                    });
                }
            }
        }
    }

    private void setListener() {
        btExit.setOnClickListener(v -> Objects.requireNonNull(FragmentInfo.this.getActivity()).finish());
        btUpdate.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("userAccount", userAccount);
            intent.setClass(context, UserUpdateActivity.class);
            startActivity(intent);
        });
    }

    private void initView(View view) {
        tvInfoBirth = view.findViewById(R.id.tv_info_birth);
        tvInfoNickName = view.findViewById(R.id.tv_info_nick_name);
        tvInfoConstellation = view.findViewById(R.id.tv_info_constellation);
        tvInfoEmail = view.findViewById(R.id.tv_info_email);
        tvInfoSign = view.findViewById(R.id.tv_info_sign);
        tvInfoGender = view.findViewById(R.id.tv_info_gender);
        btExit = view.findViewById(R.id.bt_info_exit);
        btUpdate = view.findViewById(R.id.bt_info_update);
        imageView = view.findViewById(R.id.iv_state_smile);
    }

}