package com.xiaoma.im.handler;

import com.xiaoma.im.constant.Constants;
import com.xiaoma.im.entity.MessagePackage;

import cn.hutool.core.util.ObjectUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * @Author Xiaoma
 * @Date 2021/2/8 0008 0:27
 * @Email 1468835254@qq.com
 */
public class MyDecoderHandler extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        MessagePackage packageBuilder = new MessagePackage();
        int type = byteBuf.readInt();
        if (ObjectUtil.equals(type, Constants.PING)) {
            packageBuilder.setType(type);
        } else {
            int length = byteBuf.readInt();
            byte[] bytes = new byte[length];
            byteBuf.readBytes(bytes);
            packageBuilder.setLength(length);
            packageBuilder.setType(type);
            packageBuilder.setContent(bytes);
        }
        list.add(packageBuilder);
    }
}
