package com.xiaoma.im.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xiaoma.im.R;
import com.xiaoma.im.activities.ChatFriendActivity;
import com.xiaoma.im.constant.Constants;
import com.xiaoma.im.constant.MessageConstants;
import com.xiaoma.im.entity.MessagePackage;
import com.xiaoma.im.entity.UserInfo;
import com.xiaoma.im.response.FriendResponseDto;
import com.xiaoma.im.service.ImClient;
import com.xiaoma.im.utils.DataMapUtils;
import com.xiaoma.im.vo.FriendsListVo;
import com.xiaoma.im.vo.ResponseMessageVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import cn.hutool.core.util.ObjectUtil;
import de.hdodenhof.circleimageview.CircleImageView;
import io.netty.util.CharsetUtil;

public class RecyclerAdapterHome extends RecyclerView.Adapter<RecyclerAdapterHome.ViewHolder> {

    private List<ResponseMessageVo> list = new ArrayList<>();

    private Context context;

    private LocalBroadcastManager manager;

    private String userAccount;

    public RecyclerAdapterHome(List<ResponseMessageVo> list, String userAccount) {
        this.list = list;
        this.userAccount = userAccount;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView tv_username, tv_message;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.iv_activity_home);
            tv_username = itemView.findViewById(R.id.tv_activity_username);
            tv_message = itemView.findViewById(R.id.tv_activity_message);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_recyclerview_home, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final ResponseMessageVo responseMessageVo = list.get(position);
        List<FriendsListVo> friendsListVos = (List<FriendsListVo>) DataMapUtils.getInstance().getObj(MessageConstants.FRIENDS_INFO_MAP);
        holder.tv_message.setText(responseMessageVo.getMessage());
        final FriendsListVo[] friendsListVo = {new FriendsListVo()};
        friendsListVos.forEach(item -> {
            if (Objects.equals(item.getUserAccount(), responseMessageVo.getSender())) {
                friendsListVo[0] = ObjectUtil.clone(item);
            }
        });
        if (!Objects.isNull(friendsListVo[0].getNickName()) || !"".equals(friendsListVo[0].getNickName())) {
            holder.tv_username.setText(friendsListVo[0].getNickName());
        } else {
            holder.tv_username.setText(friendsListVo[0].getUserAccount());
        }
        if (!Objects.isNull(friendsListVo[0].getUserHeadPhoto()) && !"".equals(friendsListVo[0].getUserHeadPhoto())) {
            Glide.with(context).load(friendsListVo[0].getUserHeadPhoto()).into(holder.circleImageView);
        } else {
            holder.circleImageView.setBackgroundResource(R.drawable.smile);
        }
        holder.itemView.setOnClickListener(v -> {
            MessagePackage messagePackage = new MessagePackage();
            String s = userAccount + "," + friendsListVo[0].getId();
            messagePackage.setType(Constants.FRIEND_LIST_MESSAGE);
            messagePackage.setLength(s.getBytes(CharsetUtil.UTF_8).length);
            messagePackage.setContent(s.getBytes(CharsetUtil.UTF_8));
            ImClient.getInstance().writeAndFlush(messagePackage);
            Intent intent = new Intent(context, ChatFriendActivity.class);
            intent.putExtra("friendId", friendsListVo[0].getId());
            if (friendsListVo[0].getNickName() == null || "".equals(friendsListVo[0].getNickName())) {
                intent.putExtra("friendName", friendsListVo[0].getUserAccount());
            } else {
                intent.putExtra("friendName", friendsListVo[0].getNickName());
            }
            intent.putExtra("userId", userAccount);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
