import client.Autorun;
import client.Client;
import server.Server;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) args = new String[]{"-server"};
        switch (args[0]) {
            case "-server":
                new Server().launch();
                break;
            case "-client":
                if (!isRoot()) {
                    System.out.println("Используйте 'sudo'!");
                    return;
                }
                new Client().connect();
                break;
            case "-autorun":
                if (!isRoot()) {
                    System.out.println("Используйте 'sudo'!");
                    return;
                }
                new Autorun().bind();
                break;
            case "-remove":
                if (!isRoot()) {
                    System.out.println("Используйте 'sudo'!");
                    return;
                }
                new Autorun().unistall();
                break;
            case "-help":
                System.out.println("Доступные параметры:");
                System.out.println("-server - для запуска сервера");
                System.out.println("-client - для запуска клиента");
                System.out.println("-autorun - для установки клиента на автозапуск");
                System.out.println("-remove - полное удаление");
                break;
            default:
                System.out.println("Укажите параметр -help для для получения справки");
                break;
        }
    }

    private static boolean isRoot() {
        return System.getProperty("user.name").equals("root");
    }

}
