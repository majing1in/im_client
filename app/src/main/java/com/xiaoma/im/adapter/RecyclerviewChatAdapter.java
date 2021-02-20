package com.xiaoma.im.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xiaoma.im.R;
import com.xiaoma.im.constant.MessageConstants;
import com.xiaoma.im.entity.MessageSingle;
import com.xiaoma.im.entity.UserInfo;
import com.xiaoma.im.utils.DataMapUtils;
import com.xiaoma.im.vo.FriendsListVo;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerviewChatAdapter extends RecyclerView.Adapter<RecyclerviewChatAdapter.ViewHolder> {

    private Context context;

    private List<MessageSingle> list;

    public RecyclerviewChatAdapter(List<MessageSingle> list) {
        this.list = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout sendMessageLayout, receivedMessageLayout;
        TextView senderMessage, receiverMessage;
        CircleImageView senderPhoto, receiverPhoto;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            sendMessageLayout = itemView.findViewById(R.id.ly_received_message);
            receivedMessageLayout = itemView.findViewById(R.id.ly_send_message);
            receiverMessage = itemView.findViewById(R.id.item_tv_send_message);
            senderMessage = itemView.findViewById(R.id.item_tv_received_message);
            receiverPhoto = itemView.findViewById(R.id.item_iv_send);
            senderPhoto = itemView.findViewById(R.id.item_iv_received);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_activity_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MessageSingle messageSingle = list.get(position);
        List<FriendsListVo> listVos = (List<FriendsListVo>) DataMapUtils.getInstance().getObj(MessageConstants.FRIENDS_INFO_MAP);
        UserInfo user = (UserInfo) DataMapUtils.getInstance().getObj(MessageConstants.USER_INFO_MAP);
        if (messageSingle.getReceiverid() != null && Objects.equals(messageSingle.getSenderid(), user.getId())) {
            holder.receivedMessageLayout.setVisibility(View.GONE);
            holder.sendMessageLayout.setVisibility(View.VISIBLE);
            holder.senderMessage.setText(messageSingle.getMessage());
            if (!Objects.isNull(user.getUserHeadPhoto()) && !"".equals(user.getUserHeadPhoto())) {
                Glide.with(context).load(user.getUserHeadPhoto()).into(holder.senderPhoto);
            } else {
                holder.senderPhoto.setBackgroundResource(R.drawable.smile);
            }
        } else {
            listVos.forEach(item -> {
                if (Objects.equals(messageSingle.getSenderid(), item.getId())) {
                    holder.sendMessageLayout.setVisibility(View.GONE);
                    holder.receivedMessageLayout.setVisibility(View.VISIBLE);
                    holder.receiverMessage.setText(messageSingle.getMessage());
                    if (!Objects.isNull(item.getUserHeadPhoto()) && !"".equals(item.getUserHeadPhoto())) {
                        Glide.with(context).load(item.getUserHeadPhoto()).into(holder.receiverPhoto);
                    } else {
                        holder.receiverPhoto.setBackgroundResource(R.drawable.smile);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
