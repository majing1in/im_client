package com.xiaoma.im.handler;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.xiaoma.im.activities.ChatFriendActivity;
import com.xiaoma.im.activities.UserUpdateActivity;
import com.xiaoma.im.constant.BroadcastConstants;
import com.xiaoma.im.constant.Constants;
import com.xiaoma.im.constant.MessageConstants;
import com.xiaoma.im.context.ContextHolder;
import com.xiaoma.im.entity.MessagePackage;
import com.xiaoma.im.entity.MessageSingle;
import com.xiaoma.im.entity.UserInfo;
import com.xiaoma.im.enums.ResponseEnum;
import com.xiaoma.im.fragments.FragmentFriend;
import com.xiaoma.im.fragments.FragmentInfo;
import com.xiaoma.im.response.FriendResponseDto;
import com.xiaoma.im.utils.BroadcastUtils;
import com.xiaoma.im.utils.DataMapUtils;
import com.xiaoma.im.vo.FriendsListVo;
import com.xiaoma.im.vo.MessageSingleVo;
import com.xiaoma.im.vo.ResponseMessageVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.hutool.core.util.ObjectUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @Author Xiaoma
 * @Date 2021/2/8 0008 0:28
 * @Email 1468835254@qq.com
 */
public class MyClientHandler extends SimpleChannelInboundHandler<MessagePackage> {

    /**
     * 业务处理
     * @param channelHandlerContext 上下文
     * @param messagePackage        数据对象
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessagePackage messagePackage) throws Exception {
        int type = messagePackage.getType();
        byte[] content = messagePackage.getContent();
        switch (type) {
            case Constants.ME_INFO:
                UserInfo userInfo = ObjectUtil.deserialize(content);
                DataMapUtils.getInstance().putObj(MessageConstants.USER_INFO_MAP, userInfo);
                BroadcastUtils.initLocalBroadcastManager(BroadcastConstants.USER_INFO_FRAGMENT, FragmentInfo.context, MessageConstants.USER_INFO_BROADCAST, MessageConstants.USER_INFO);
                break;
            case Constants.ME_INFO_UPDATE:
                Integer meInfoResult = ObjectUtil.deserialize(content);
                BroadcastUtils.initLocalBroadcastManager(BroadcastConstants.USER_UPDATE_ACTIVITY, UserUpdateActivity.context, MessageConstants.USER_INFO_UPDATE__BROADCAST, meInfoResult);
                break;
            case Constants.FRIENDS_LIST:
                List<FriendsListVo> friendsListVoList = ObjectUtil.deserialize(content);
                if (Objects.isNull(friendsListVoList) || friendsListVoList.size() == 0) {
                    DataMapUtils.getInstance().putObj(MessageConstants.FRIENDS_INFO_MAP, new ArrayList<FriendsListVo>());
                } else {
                    DataMapUtils.getInstance().putObj(MessageConstants.FRIENDS_INFO_MAP, friendsListVoList);
                }
                BroadcastUtils.initLocalBroadcastManager(BroadcastConstants.FRIENDS_INFO_FRAGMENT, FragmentFriend.context, MessageConstants.FRIENDS_INFO_UPDATE_BROADCAST, MessageConstants.FRIENDS_LIST_INFO);
                break;
            case Constants.FRIEND_LIST_MESSAGE:
                List<MessageSingleVo> singleList = ObjectUtil.deserialize(content);
                if (Objects.isNull(singleList) || singleList.size() == 0) {
                    DataMapUtils.getInstance().putObj(MessageConstants.FRIENDS_MESSAGE_ACTIVITY_MAP, new ArrayList<MessageSingleVo>());
                } else {
                    DataMapUtils.getInstance().putObj(MessageConstants.FRIENDS_MESSAGE_ACTIVITY_MAP, singleList);
                }
                BroadcastUtils.initLocalBroadcastManager(BroadcastConstants.FRIENDS_MESSAGE_ACTIVITY, ChatFriendActivity.context, MessageConstants.FRIENDS_MESSAGE_ACTIVITY_BROADCAST, MessageConstants.FRIENDS_MESSAGE_ACTIVITY_VALUE);
                break;
            case Constants.FRIEND_LIST_APPLY:
                // 添加好友消息
                FriendResponseDto friendResponseDto = ObjectUtil.deserialize(content);
                DataMapUtils.getInstance().putObj(MessageConstants.USER_UNREAD_ADD_MAP, friendResponseDto);
                BroadcastUtils.initLocalBroadcastManager(BroadcastConstants.UNREAD_MESSAGE, ContextHolder.getContext(), MessageConstants.USER_UNREAD, MessageConstants.USER_UNREAD_ADD);
                break;
            case Constants.RECEIVED:
                // 好友消息
                ResponseMessageVo responseMessageVo = ObjectUtil.deserialize(content);
                List<ResponseMessageVo> responseMessageVoList = (List<ResponseMessageVo>) DataMapUtils.getInstance().getObj(MessageConstants.USER_UNREAD_MESSAGE_MAP);
                if (responseMessageVoList == null || responseMessageVoList.size() == 0) {
                    List<ResponseMessageVo> list = new ArrayList<>();
                    list.add(responseMessageVo);
                    DataMapUtils.getInstance().putObj(MessageConstants.USER_UNREAD_MESSAGE_MAP, list);
                } else {
                    responseMessageVoList.add(responseMessageVo);
                }
                BroadcastUtils.initLocalBroadcastManager(BroadcastConstants.UNREAD_MESSAGE, ContextHolder.getContext(), MessageConstants.USER_UNREAD, MessageConstants.USER_UNREAD_MESSAGE);
                break;
            case Constants.SUCCESS_CONSTANT:
                // 成功不做其他的
                break;
            case Constants.FAILED_CONSTANT:
                // 失败不做其他的
                break;
        }
    }

    /**
     * 心跳检测
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
                MessagePackage messagePackage = new MessagePackage();
                messagePackage.setType(Constants.PING);
                ctx.writeAndFlush(messagePackage).addListeners((ChannelFutureListener) future -> {
                    if (!future.isSuccess()) {
                        future.channel().close();
                    }
                });
            }

        }
    }

    /**
     * 取消绑定
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

    }


}
