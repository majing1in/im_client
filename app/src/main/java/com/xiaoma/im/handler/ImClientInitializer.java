package com.xiaoma.im.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @Author Xiaoma
 * @Date 2021/2/8 0008 0:24
 * @Email 1468835254@qq.com
 */
public class ImClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new IdleStateHandler(30, 0,0))
                .addLast(new MyDecoderHandler())
                .addLast(new MyEncoderHandler())
                .addLast(new MyClientHandler());
    }
}
