package utils;

import client.ClientHandler;

import java.io.File;
import java.util.concurrent.Executors;

public class ShellExecutor {
    private static final String OS_NAME = System.getProperty("os.name").toLowerCase();
    private static final String USER_HOME = System.getProperty("user.home");

    public void executeCommand(String command, ClientHandler handler) {
        boolean isWindows = OS_NAME.startsWith("windows");
        ProcessBuilder builder = new ProcessBuilder();
        builder.redirectErrorStream(true);

        if (isWindows) builder.command("cmd.exe", "/c", command);
        else builder.command("sh", "-c", command);

        builder.directory(new File(USER_HOME));
        try {
            Process process = builder.start();
            if (handler != null) {
                StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), s -> outputHandler(s, handler));
                Executors.newSingleThreadExecutor().submit(streamGobbler);
            }
            int exitCode = process.waitFor();
//            System.out.println("ExitCode: " + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void outputHandler(String line, ClientHandler handler) {
        handler.sendMessage(line);
    }

}
