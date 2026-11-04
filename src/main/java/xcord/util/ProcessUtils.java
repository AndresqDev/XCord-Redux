package xcord.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import net.md_5.bungee.BungeeCord;
import xcord.XCord;

public class ProcessUtils {
    public static int ipToInt(String address) {
        int result = 0;
        StringBuilder octet = new StringBuilder(3);
        for (int i = 0; i < address.length(); i++) {
            char character = address.charAt(i);
            if (character == '.') {
                result <<= 8;
                result |= Integer.parseInt(octet.toString());
                octet = new StringBuilder(3);
                continue;
            }
            octet.append(character);
        }
        result <<= 8;
        result |= Integer.parseInt(octet.toString());
        return result;
    }

    public static boolean isValidIp(String address) {
        try {
            if (address == null || address.isEmpty()) {
                return false;
            }
            String[] parts = address.split("\\.");
            if (parts.length != 4) {
                return false;
            }
            for (String part : parts) {
                int value = Integer.parseInt(part);
                if (value >= 0 && value <= 255) {
                    continue;
                }
                return false;
            }
            return !address.endsWith(".");
        }
        catch (NumberFormatException exception) {
            return false;
        }
    }

    public static String execute(ProcessBuilder processBuilder, int timeoutMillis) {
        Process process = null;
        String output = "";
        try {
            process = processBuilder.start();
            if (timeoutMillis > 0) {
                Process startedProcess = process;
                new Thread(() -> ProcessUtils.scheduleKill(timeoutMillis, startedProcess, processBuilder)).start();
            }
            StringBuilder builder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append(System.lineSeparator());
                }
            }
            output = builder.toString();
        }
        catch (Exception exception) {
            if (exception instanceof IOException) {
                output = exception.getMessage();
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
            System.out.println("-> Executed: " + processBuilder.command().toString() + " -> Response: " + output);
        }
        return output;
    }

    private static void scheduleKill(int timeoutMillis, Process process, ProcessBuilder processBuilder) {
        try {
            Thread.sleep(timeoutMillis);
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
                BungeeCord.getInstance().getLogger().warning("XCord Failed to execute: " + processBuilder.command().toString() + ", timed out.");
            }
        }
        catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }
}
