package server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.util.CharsetUtil;

public class User {
    private String name;
    private ChannelHandlerContext ctx;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChannelId getChannelId() {
        return ctx.channel().id();
    }

    public User(String name, ChannelHandlerContext ctx) {
        this.name = name;
        this.ctx = ctx;
    }

    public void sendMessage(String message) {
        ctx.writeAndFlush(Unpooled.copiedBuffer(message, CharsetUtil.UTF_8));
    }

    @Override
    public String toString() {
        return name + " : " + ctx.channel().id();
    }
}
