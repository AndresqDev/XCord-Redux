package xcord.command;

import java.lang.reflect.InvocationTargetException;
import java.util.Map.Entry;
import java.util.SortedSet;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.Stopwatch;
import net.md_5.bungee.api.event.AsyncEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.event.EventBus;
import net.md_5.bungee.event.EventExceptionHandler;
import net.md_5.bungee.event.EventHandlerMethod;

public class TimedEventsCommand extends Command {
    public TimedEventsCommand() {
        super("timedelayedevents", "bungeecord.command.reload");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        PluginManager pluginManager = BungeeCord.getInstance().getPluginManager();
        if (pluginManager.getEventBus() instanceof TimingEventBus) {
            pluginManager.setEventBus(((TimingEventBus) pluginManager.getEventBus()).delegate);
            sender.sendMessage(ChatColor.BOLD.toString() + ChatColor.GREEN.toString() + "XCord has disabled delayed-event timing. Check your logs for results.");
        } else {
            pluginManager.setEventBus(new TimingEventBus(pluginManager.getEventBus()));
            sender.sendMessage(
                ChatColor.BOLD.toString()
                    + ChatColor.GREEN.toString()
                    + "XCord has injected into the event bus. You will see timings/output inside of your logs or console"
            );
        }
    }

    public static class TimingEventBus extends EventBus {
        private final EventBus delegate;

        public TimingEventBus(EventBus delegate) {
            this.delegate = delegate;
        }

        @Override
        public <T> void post(T event, EventExceptionHandler<T> exceptionHandler) {
            if (event instanceof AsyncEvent) {
                timeAsyncEvent((AsyncEvent<?>) event);
                this.delegate.post(event, exceptionHandler);
            } else {
                this.postSync(event, exceptionHandler);
            }
        }

        private static <T> void timeAsyncEvent(AsyncEvent<T> asyncEvent) {
            if (asyncEvent.getDoneCallback() == null) {
                return;
            }
            Stopwatch<Plugin> stopwatch = new Stopwatch<>();
            asyncEvent.setStopwatch(stopwatch);
            Callback<T> callback = asyncEvent.getDoneCallback();
            long startTime = System.nanoTime();
            asyncEvent.setDoneCallback((result, error) -> {
                SortedSet<Entry<Plugin, Double>> deltas = stopwatch.getDeltas();
                if (!deltas.isEmpty()) {
                    double totalTime = (double) (System.nanoTime() - startTime) / 1000000.0;
                    BungeeCord.getInstance().getLogger().info("[Async-Timings] (" + asyncEvent.getClass().getSimpleName() + ") Total-Time=" + totalTime + " MS");
                    BungeeCord.getInstance().getLogger().info("> Plugin timings:");
                    for (Entry<Plugin, Double> entry : deltas) {
                        BungeeCord.getInstance().getLogger().info("  > " + entry.getKey().getDescription().getName() + ": " + entry.getValue() + " MS");
                    }
                }
                callback.done(result, error);
            });
        }

        private <T> void postSync(T event, EventExceptionHandler<T> exceptionHandler) {
            EventHandlerMethod[] handlers = this.delegate.getByEventBaked().get(event.getClass());
            if (handlers != null && handlers.length > 0) {
                Stopwatch<EventHandlerMethod> stopwatch = new Stopwatch<>();
                long startTime = System.nanoTime();
                for (EventHandlerMethod handler : handlers) {
                    try {
                        stopwatch.start(handler);
                        handler.invoke(event);
                        stopwatch.stop(handler);
                    }
                    catch (IllegalAccessException exception) {
                        throw new Error("Method became inaccessible: " + event, exception);
                    }
                    catch (IllegalArgumentException exception) {
                        throw new Error("Method rejected target/argument: " + event, exception);
                    }
                    catch (InvocationTargetException exception) {
                        throw new Error("Error: " + event, exception);
                    }
                }
                double totalTime = (double) (System.nanoTime() - startTime) / 1000000.0;
                SortedSet<Entry<EventHandlerMethod, Double>> deltas = stopwatch.getDeltas();
                if (!deltas.isEmpty()) {
                    BungeeCord.getInstance().getLogger().info("[Sync-Timings] (" + event.getClass().getSimpleName() + ") Total-Time=" + totalTime + " MS");
                    BungeeCord.getInstance().getLogger().info("> Plugin listener timings:");
                    for (Entry<EventHandlerMethod, Double> entry : deltas) {
                        BungeeCord.getInstance().getLogger().info("  > " + entry.getKey().getMethod().getDeclaringClass().getName() + ": " + entry.getValue() + " MS");
                    }
                }
            }
        }

        @Override
        public void register(Object listener) {
            this.delegate.register(listener);
        }

        @Override
        public void unregister(Object listener) {
            this.delegate.unregister(listener);
        }
    }
}
