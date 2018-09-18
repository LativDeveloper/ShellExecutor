package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class Client {
    private static final String ADDRESS = "localhost";
    private static final int PORT = 4444;
    public static final int WAIT_TIME_MILLS = 5000;
    private static Client client;

    public static void main(String[] args) {
        new Client().connect();
    }

    public Client() {
        client = this;
    }

    public void connect() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            System.out.println("Подключение: " + ADDRESS + ":" + PORT);
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.remoteAddress(new InetSocketAddress(ADDRESS, PORT));

            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) {
                    socketChannel.pipeline().addLast(new ClientHandler(client));
                }
            });

            ChannelFuture future = bootstrap.connect().sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            System.out.println("Соединение не установлено! Ожидание: " + WAIT_TIME_MILLS + " ms");
            try {
                Thread.sleep(WAIT_TIME_MILLS);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            connect();
        }
    }

}
