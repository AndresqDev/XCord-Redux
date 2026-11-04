package xcord;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Timer;
import java.util.TimerTask;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class MemoryCommand
extends Command {
    private Timer timer = null;

    public MemoryCommand() {
        super("testmemory", "bungeecord.command.reload", new String[0]);
    }

    @Override
    public void execute(CommandSender commandSender, String[] stringArray) {
        if (this.timer == null) {
            this.timer = new Timer("bobproxy-memory-timer", true);
            this.timer.scheduleAtFixedRate((TimerTask)new MemoryStatsTask(), 2000L, 2000L);
            commandSender.sendMessage(ChatColor.BOLD.toString() + ChatColor.GREEN.toString() + "XCord has started the memory profiler. Check your logs/console for results (Prints every 2 seconds)");
        } else {
            this.timer.cancel();
            this.timer = null;
            commandSender.sendMessage(ChatColor.BOLD.toString() + ChatColor.GREEN.toString() + "Memory profiling has been disabled. Check your logs for results");
        }
    }

    private static class MemoryStatsTask
    extends TimerTask {
        private long lastGcCount = this.c();
        private long lastGcTime = this.d();

        private MemoryStatsTask() {
        }

        @Override
        public void run() {
            System.out.println("Memory Stats> Used=" + this.a() + " Free=" + Runtime.getRuntime().freeMemory() / 0x100000L + " Max=" + this.b());
            long l = this.c();
            if (l != this.lastGcCount) {
                long l2 = this.d();
                long l3 = l2 - this.lastGcTime;
                this.lastGcCount = l;
                this.lastGcTime = l2;
                System.out.println("Memory Stats> Garbage collection event> Pause-Time=" + l3 + " ");
            }
        }

        private long a() {
            return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 0x100000L;
        }

        private long b() {
            return Runtime.getRuntime().maxMemory() / 0x100000L;
        }

        private long c() {
            long l = 0L;
            for (GarbageCollectorMXBean garbageCollectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
                long l2 = garbageCollectorMXBean.getCollectionCount();
                if (l2 < 0L) continue;
                l += l2;
            }
            return l;
        }

        private long d() {
            long l = 0L;
            for (GarbageCollectorMXBean garbageCollectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
                long l2 = garbageCollectorMXBean.getCollectionTime();
                if (l2 < 0L) continue;
                l += l2;
            }
            return l;
        }
    }
}

