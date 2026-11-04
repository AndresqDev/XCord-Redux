package xcord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import net.md_5.bungee.BungeeCord;
import xcord.XCord;

public class ProcessUtils {
    public static int ipToInt(String string) {
        int n = 0;
        StringBuilder stringBuilder = new StringBuilder(3);
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            if (c == '.') {
                n <<= 8;
                n |= Integer.parseInt(stringBuilder.toString());
                stringBuilder = new StringBuilder(3);
                continue;
            }
            stringBuilder.append(string.charAt(i));
        }
        n <<= 8;
        return n |= Integer.parseInt(stringBuilder.toString());
    }

    public static boolean isValidIp(String string) {
        try {
            if (string == null || string.isEmpty()) {
                return false;
            }
            String[] stringArray = string.split("\\.");
            if (stringArray.length != 4) {
                return false;
            }
            for (String string2 : stringArray) {
                int n = Integer.parseInt(string2);
                if (n >= 0 && n <= 255) continue;
                return false;
            }
            return !string.endsWith(".");
        }
        catch (NumberFormatException numberFormatException) {
            return false;
        }
    }

    public static String execute(ProcessBuilder processBuilder, int n) {
        Process process = null;
        String string = "";
        try {
            Object object;
            process = processBuilder.start();
            if (n > 0) {
                Process killerProcess = process;
                new Thread(() -> ProcessUtils.scheduleKill(n, killerProcess, processBuilder)).start();
            }
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));){
                while ((object = bufferedReader.readLine()) != null) {
                    stringBuilder.append((String)object).append(System.lineSeparator());
                }
            }
            string = stringBuilder.toString();
        }
        catch (Exception exception) {
            if (exception instanceof IOException) {
                string = exception.getMessage();
            }
            exception.printStackTrace();
        }
        if (process != null) {
            try {
                process.destroyForcibly();
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        if (XCord.getConfig() != null && XCord.getConfig().getDebugDebugMode()) {
            System.out.println("-> Executed: " + processBuilder.command().toString() + " -> Response: " + string);
        }
        return string;
    }

    private static void scheduleKill(int n, Process process, ProcessBuilder processBuilder) {
        try {
            Thread.sleep(n);
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
                BungeeCord.getInstance().getLogger().warning("XCord Failed to execute: " + processBuilder.command().toString() + ", timed out.");
            }
        }
        catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }
}

