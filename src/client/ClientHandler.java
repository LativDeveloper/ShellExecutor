package client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import utils.ShellExecutor;

import java.net.InetAddress;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private ChannelHandlerContext ctx;
    private Client client;

    public ClientHandler(Client client) {
        this.client = client;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Успешное подключение!");
        this.ctx = ctx;
        String computerName = InetAddress.getLocalHost().getHostName();
        sendMessage("auth:"+computerName);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Соединение потеряно! Ожидание: " + Client.WAIT_TIME_MILLS + " ms");
        try {
            Thread.sleep(Client.WAIT_TIME_MILLS);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        client.connect();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        String received = byteBuf.toString(CharsetUtil.UTF_8);
//        Query query = Query.fromJson(received);

        receivedMessageHandler(received);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();

        System.out.println("Ошибка на клиенте! Ожидание: " + Client.WAIT_TIME_MILLS + " ms");
        try {
            Thread.sleep(Client.WAIT_TIME_MILLS);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        client.connect();
    }

    private void receivedMessageHandler(String message) {
        if (message.equals("ping")) {
            sendMessage("pong");
            return;
        }
        System.out.println("Client received: " + message);
        ShellExecutor executor = new ShellExecutor();
        executor.executeCommand(message, this);
    }

    public void sendMessage(String message) {
        ChannelFuture future = ctx.writeAndFlush(Unpooled.copiedBuffer(message, CharsetUtil.UTF_8));
        future.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }
}
