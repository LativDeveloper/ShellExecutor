package client;

import utils.ShellExecutor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Autorun {
    private static final String SERVICE_PATH = "/ShellExeService.service";
    private static final String SCRIPT_PATH = "/ShellExeService.sh";
    private static final String JAR_NAME = "/ShellExecutor.jar";

    private static final String TARGET_SERVICE_PATH = "/etc/systemd/system/ShellExeService.service";
    private static final String TARGET_SCRIPT_PATH = "/usr/local/bin/ShellExeService.sh";
    private static final String TARGET_JAR_PATH = "/usr/local";

    private static final String OS_NAME = System.getProperty("os.name").toLowerCase();

    public void unistall() {
        boolean isWindows = OS_NAME.startsWith("windows");
        if (!isWindows) {
            System.out.println("Удаление... (linux)");
            serviceStop();
            File service = new File(TARGET_SERVICE_PATH);
            File script = new File(TARGET_SCRIPT_PATH);
            File jar = new File(TARGET_JAR_PATH + JAR_NAME);

            if (service.delete()) System.out.println(service.getName() + " удален!");
            else System.out.println("Ошибка удаления " + service.getName());

            if (script.delete()) System.out.println(script.getName() + " удален!");
            else System.out.println("Ошибка удаления " + script.getName());

            if (jar.delete()) System.out.println(jar.getName() + " удален!");
            else System.out.println("Ошибка удаления " + jar.getName());
        } else {
            System.out.println("Удаление для windows в разработке...");
        }
    }

    public void bind() {
        boolean isWindows = OS_NAME.startsWith("windows");
        if (!isWindows) {
            unistall();

            System.out.println("Установка на автозапуск... (linux)");
            writeServiceFile();
            writeScriptFile();
            writeJarFile();
            serviceEnable();
            serviceStart();
            removeJar();
        } else {
            System.out.println("Автозапуск для windows в разработке...");
        }
    }

    private void writeServiceFile() {
        try {
            File service = new File(TARGET_SERVICE_PATH);
            FileWriter fileWriter = new FileWriter(service);
            InputStream in = getClass().getResourceAsStream(SERVICE_PATH);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            reader.lines().forEach(s -> {
                try {
                    fileWriter.write(s + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            fileWriter.close();

            service.setReadable(true);
            service.setWritable(true);
            service.setExecutable(true);

            System.out.println(TARGET_SERVICE_PATH + " перезаписан!");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void writeScriptFile() {
        try {
            File script = new File(TARGET_SCRIPT_PATH);
            FileWriter fileWriter = new FileWriter(script);
            InputStream in = getClass().getResourceAsStream(SCRIPT_PATH);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            reader.lines().forEach(s -> {
                try {
                    fileWriter.write(s + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            fileWriter.close();

            script.setReadable(true);
            script.setWritable(true);
            script.setExecutable(true);

            System.out.println(TARGET_SCRIPT_PATH + " перезаписан!");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void writeJarFile() {
        String jarPath = System.getProperty("user.dir") + JAR_NAME;
        File src = new File(jarPath);
        File target = new File(TARGET_JAR_PATH + JAR_NAME);
        try {
            Files.copy(Paths.get(src.getPath()), new FileOutputStream(target));
            System.out.println(JAR_NAME + " скопирован в " + TARGET_JAR_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }

        target.setReadable(true);
        target.setWritable(true);
        target.setExecutable(true);

    }

    private void serviceEnable() {
        ShellExecutor shellExecutor = new ShellExecutor();
        shellExecutor.executeCommand("systemctl enable ShellExeService", null);
        System.out.println("Сервис добавлен!");
    }

    private void serviceStart() {
        ShellExecutor shellExecutor = new ShellExecutor();
        shellExecutor.executeCommand("systemctl start ShellExeService", null);
        System.out.println("Сервис запущен!");
    }

    private void serviceStop() {
        ShellExecutor shellExecutor = new ShellExecutor();
        shellExecutor.executeCommand("systemctl stop ShellExeService", null);
        System.out.println("Сервис остановлен!");

    }

    private void removeJar() {
        String jarPath = System.getProperty("user.dir") + JAR_NAME;
        File jar = new File(jarPath);
        if (jar.delete()) System.out.println(jar.getName() + " удален!");
        else System.out.println("Ошибка удаления " + jar.getName());
    }


}
