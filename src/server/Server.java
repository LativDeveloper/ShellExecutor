package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Server {
    private static final int PORT = 7777;
    private static Server server;

    private HashMap<ChannelId, User> users;
    private User selectedUser;

    public Server() {
        server = this;
        users = new HashMap<>();
    }

    public void launch() {
        System.out.println("Запуск сервера...");
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(group);
            serverBootstrap.channel(NioServerSocketChannel.class);

            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) {
                    socketChannel.pipeline().addLast(new IdleStateHandler(0,0,60));
                    socketChannel.pipeline().addLast(new ServerHandler(server));
                }
            });
            ChannelFuture channelFuture = serverBootstrap.bind(PORT).sync();
            System.out.println("Успешный запуск! Порт: " + PORT);
            System.out.println("select [name] - выбрать пользователя для работы с ним");
            System.out.println("exit - выйти из режима работы с пользователем");
            System.out.println("all - список всех пользователей онлайн");
            initConsoleReader();
            channelFuture.channel().closeFuture().sync();
        } catch(InterruptedException e){
            e.printStackTrace();
        } finally {
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public HashMap<ChannelId, User> getUsers() {
        return users;
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User user) {
        selectedUser = user;
    }

    private void initConsoleReader() {
        Scanner scanner = new Scanner(System.in);
        String input;
        while ((input = scanner.nextLine()) != null) {
            consoleHandler(input);
        }
    }

    private void consoleHandler(String message) {
        String[] params = message.split(" ");
        if (selectedUser == null) {

            switch (params[0]) {
                case "all":
                    if (users.size() == 0) {
                        System.out.println("Пользователи отсутствуют!");
                        return;
                    }
                    if (selectedUser != null) System.out.println("Selected User: " + selectedUser);
                    System.out.println("Пользователи:");
                    for (Map.Entry<ChannelId, User> entry : users.entrySet()) {
                        User user = entry.getValue();
                        System.out.println(user.getName() + " : " + user.getChannelId());
                    }
                    break;
                case "select":
                    if (params.length < 2) {
                        System.out.println("select [name]");
                        return;
                    }
                    User user = getUserByName(params[1]);
                    if (user == null) System.out.println("Пользователь не найден!");
                    else {
                        selectedUser = user;
                        System.out.println("Выбран пользователь: " + user);
                    }
                    break;
                default:
                    System.out.println("Неверная команда!");
                    break;
            }
        } else {
            switch (params[0]) {
                case "exit":
                    selectedUser = null;
                    System.out.println("Вышли из режима работы с пользователем!");
                    break;
                default:
                    selectedUser.sendMessage(message);
                    break;
            }
        }
//        if (selectedUser != null) System.out.print(selectedUser.getName() + ": ");
    }

    private User getUserByName(String name) {
        for (Map.Entry<ChannelId, User> entry : users.entrySet()) {
            if (entry.getValue().getName().equals(name)) return entry.getValue();
        }
        return null;
    }
}
