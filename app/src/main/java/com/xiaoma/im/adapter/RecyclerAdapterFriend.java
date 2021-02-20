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
import com.xiaoma.im.entity.MessagePackage;
import com.xiaoma.im.entity.UserInfo;
import com.xiaoma.im.service.ImClient;
import com.xiaoma.im.vo.FriendsListVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public class RecyclerAdapterFriend extends RecyclerView.Adapter<RecyclerAdapterFriend.ViewHolder> {

    private List<FriendsListVo> list;

    private String userAccount;

    private Context context;

    public RecyclerAdapterFriend(List<FriendsListVo> list, String userAccount) {
        this.list = list;
        this.userAccount = userAccount;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView circleImageView;
        TextView tv_username, tv_sign;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.iv_friend_recyclerviewItem);
            tv_username = itemView.findViewById(R.id.tv_friend_user);
            tv_sign = itemView.findViewById(R.id.tv_friend_sign);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_recyclerview_friend, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final FriendsListVo friendsListVo = list.get(position);
        if (Objects.isNull(friendsListVo.getNickName()) || !"".equals(friendsListVo.getNickName())) {
            holder.tv_username.setText(friendsListVo.getNickName());
        } else {
            holder.tv_username.setText(friendsListVo.getUserAccount());
        }
        if (Objects.isNull(friendsListVo.getUserSign()) || !"".equals(friendsListVo.getUserSign())) {
            holder.tv_sign.setText(friendsListVo.getUserSign());
        } else {
            holder.tv_sign.setText("");
        }
        if (!Objects.isNull(friendsListVo.getUserHeadPhoto()) && !"".equals(friendsListVo.getUserHeadPhoto())) {
            Glide.with(context).load(friendsListVo.getUserHeadPhoto()).into(holder.circleImageView);
        } else {
            holder.circleImageView.setBackgroundResource(R.drawable.preview);
        }
        holder.itemView.setOnClickListener(v -> {
            MessagePackage messagePackage = new MessagePackage();
            String s = userAccount + "," + friendsListVo.getId();
            messagePackage.setType(Constants.FRIEND_LIST_MESSAGE);
            messagePackage.setLength(s.getBytes(CharsetUtil.UTF_8).length);
            messagePackage.setContent(s.getBytes(CharsetUtil.UTF_8));
            ImClient.getInstance().writeAndFlush(messagePackage);
            Intent intent = new Intent(context, ChatFriendActivity.class);
            intent.putExtra("friendId", friendsListVo.getId());
            if (friendsListVo.getNickName() == null || "".equals(friendsListVo.getNickName())) {
                intent.putExtra("friendName", friendsListVo.getUserAccount());
            } else {
                intent.putExtra("friendName", friendsListVo.getNickName());
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
