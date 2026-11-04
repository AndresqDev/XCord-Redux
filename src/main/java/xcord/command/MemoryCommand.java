package xcord.command;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Timer;
import java.util.TimerTask;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class MemoryCommand extends Command {
    private static final long BYTES_PER_MEGABYTE = 1024L * 1024L;
    private Timer timer;

    public MemoryCommand() {
        super("testmemory", "bungeecord.command.reload");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (this.timer == null) {
            this.timer = new Timer("bobproxy-memory-timer", true);
            this.timer.scheduleAtFixedRate(new MemoryStatsTask(), 2000L, 2000L);
            sender.sendMessage(ChatColor.BOLD.toString() + ChatColor.GREEN.toString() + "XCord has started the memory profiler. Check your logs/console for results (Prints every 2 seconds)");
        } else {
            this.timer.cancel();
            this.timer = null;
            sender.sendMessage(ChatColor.BOLD.toString() + ChatColor.GREEN.toString() + "Memory profiling has been disabled. Check your logs for results");
        }
    }

    private static class MemoryStatsTask extends TimerTask {
        private long lastGcCount = this.gcCount();
        private long lastGcTime = this.gcTime();

        @Override
        public void run() {
            System.out.println("Memory Stats> Used=" + this.usedMemory() + " Free=" + Runtime.getRuntime().freeMemory() / BYTES_PER_MEGABYTE + " Max=" + this.maxMemory());
            long gcCount = this.gcCount();
            if (gcCount != this.lastGcCount) {
                long gcTime = this.gcTime();
                long pauseTime = gcTime - this.lastGcTime;
                this.lastGcCount = gcCount;
                this.lastGcTime = gcTime;
                System.out.println("Memory Stats> Garbage collection event> Pause-Time=" + pauseTime + " ");
            }
        }

        private long usedMemory() {
            return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / BYTES_PER_MEGABYTE;
        }

        private long maxMemory() {
            return Runtime.getRuntime().maxMemory() / BYTES_PER_MEGABYTE;
        }

        private long gcCount() {
            long total = 0L;
            for (GarbageCollectorMXBean garbageCollectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
                long collectionCount = garbageCollectorMXBean.getCollectionCount();
                if (collectionCount < 0L) {
                    continue;
                }
                total += collectionCount;
            }
            return total;
        }

        private long gcTime() {
            long total = 0L;
            for (GarbageCollectorMXBean garbageCollectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
                long collectionTime = garbageCollectorMXBean.getCollectionTime();
                if (collectionTime < 0L) {
                    continue;
                }
                total += collectionTime;
            }
            return total;
        }
    }
}
