package server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
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
        if (user == server.getSelectedUser()) server.setSelectedUser(null);
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
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.ALL_IDLE) { // idle for no read and write
                User user = server.getUsers().get(ctx.channel().id());
                if (user == null) return;
                user.sendMessage("ping");
            }
        }
    }

    private void receivedMessageHandler(String message) {
        if (message.equals("pong")) return;
        System.out.println(message);
    }
}
