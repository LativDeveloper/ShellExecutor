package server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private Server server;

    public ServerHandler(Server server) {
        this.server = server;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Подключение: " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("Отключение: " + ctx.channel().remoteAddress());
        User user = server.getUsers().get(ctx.channel().id());
        if (user != null) server.getUsers().remove(ctx.channel().id());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        String received = byteBuf.toString(CharsetUtil.UTF_8);
//        Query query = Query.fromJson(received);
//        System.out.println("Server received: " + received);

        String[] params = received.split(":");

        if (params[0].equals("auth"))
            server.getUsers().put(ctx.channel().id(), new User(params[1], ctx));

        receivedMessageHandler(received);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
//        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private void receivedMessageHandler(String message) {
        System.out.println(message);
    }
}
