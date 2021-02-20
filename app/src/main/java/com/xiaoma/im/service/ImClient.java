package com.xiaoma.im.service;

import com.xiaoma.im.handler.ImClientInitializer;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 22/05/2018 14:19
 * @since JDK 1.8
 */
public class ImClient {

    private static NioEventLoopGroup group = new NioEventLoopGroup();
    private static final String IPADDRESS = "10.0.2.2";
    private static final Integer PORT = 8003;
    public static final Integer MAXCOUNT = 7;
    public static Integer COUNT = 0;
    private static volatile SocketChannel channel = null;
    public static boolean SUCCESS = false;

    private ImClient() {}

    public static SocketChannel getInstance() {
        if (channel == null) {
            synchronized (ImClient.class) {
                SUCCESS = startClient();
            }
        }
        return channel;
    }

    /**
     * 启动客户端
     */
    private static boolean startClient() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ImClientInitializer());
        ChannelFuture future = null;
        try {
            future = bootstrap.connect(new InetSocketAddress(IPADDRESS, PORT)).sync();
        } catch (InterruptedException e) {
            if (++COUNT >= MAXCOUNT ) {
                future.channel().close();
            }
            return false;
        }
        channel = (SocketChannel) future.channel();
        return true;
    }


}
