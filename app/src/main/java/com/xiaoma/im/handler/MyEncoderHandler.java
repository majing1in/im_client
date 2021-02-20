package com.xiaoma.im.handler;

import com.xiaoma.im.entity.MessagePackage;

import cn.hutool.core.util.ObjectUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Author Xiaoma
 * @Date 2021/2/8 0008 0:28
 * @Email 1468835254@qq.com
 */
public class MyEncoderHandler extends MessageToByteEncoder<MessagePackage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessagePackage msg, ByteBuf out) throws Exception {
        if (ObjectUtil.isNotEmpty(msg.getType()))
            out.writeInt(msg.getType());
        if (ObjectUtil.isNotEmpty(msg.getLength()))
            out.writeInt(msg.getLength());
        if (ObjectUtil.isNotEmpty(msg.getContent()))
            out.writeBytes(msg.getContent());
    }
}
