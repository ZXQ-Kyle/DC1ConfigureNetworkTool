package info.ponyo.dc1configurenetworktool;

import android.support.annotation.NonNull;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

/**
 * UDPClient
 *
 * @author ：wy.
 * @date ：Created at 10:55 2019/4/11
 * @modifyBy ：
 */
public class UDPClient {

    private final EventLoopGroup group = new NioEventLoopGroup();

    private final Bootstrap bs = new Bootstrap();

    private UdpListener mListener;

    public UDPClient(@NonNull UdpListener listener) {
        mListener = listener;
    }

    public void send(String msg) {
        try {
            bs.group(group)
                    .handler(new UdpClientHandler())
                    //UDP的channel
                    .channel(NioDatagramChannel.class);

            //不需要建立连接，绑定0端口是表示系统为我们设置端口监听
            Channel channel = bs.bind(0).sync().channel();

            //UDP使用DatagramPacket发送数据
            channel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8),
                    new InetSocketAddress("192.168.4.1", 7550)));

            //15秒后未获取响应就打印超时
            if (!channel.closeFuture().await(15000)) {
                mListener.onFail("连接超时");
            }

        } catch (Exception e) {
            mListener.onFail("数据发送异常");
        } finally {
            group.shutdownGracefully();
        }
    }


    private class UdpClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {
        //{"header":"phi-plug-0001","uuid":"00010","status":200,"msg":"set wifi success","result":{"mac":"88:88:88:56:38:21"}}
        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) {
            //获取内容
            String content = datagramPacket.content().toString(CharsetUtil.UTF_8);
            mListener.onSuccess(content);
            channelHandlerContext.close();
        }
    }
}
