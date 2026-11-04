package xcord;

import java.lang.reflect.InvocationTargetException;
import java.util.SortedSet;
import java.util.Map.Entry;
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
   public void execute(CommandSender var1, String[] var2) {
      PluginManager var3 = BungeeCord.getInstance().getPluginManager();
      if (var3.getEventBus() instanceof TimedEventsCommand.TimingEventBus) {
         var3.setEventBus(((TimedEventsCommand.TimingEventBus)var3.getEventBus()).delegate);
         var1.sendMessage(ChatColor.BOLD.toString() + ChatColor.GREEN.toString() + "XCord has disabled delayed-event timing. Check your logs for results.");
      } else {
         var3.setEventBus(new TimedEventsCommand.TimingEventBus(var3.getEventBus()));
         var1.sendMessage(
            ChatColor.BOLD.toString()
               + ChatColor.GREEN.toString()
               + "XCord has injected into the event bus. You will see timings/output inside of your logs or console"
         );
      }
   }

   public static class TimingEventBus extends EventBus {
      private final EventBus delegate;

      public TimingEventBus(EventBus var1) {
         this.delegate = var1;
      }

      @Override
      public <T> void post(T var1, EventExceptionHandler<T> var2) {
         if (var1 instanceof AsyncEvent) {
            AsyncEvent var3 = (AsyncEvent)var1;
            if (var3.getDoneCallback() != null) {
               Stopwatch var4 = new Stopwatch();
               var3.setStopwatch(var4);
               Callback var5 = var3.getDoneCallback();
               long var6 = System.nanoTime();
               var3.setDoneCallback(
                  (var5x, var6x) -> {
                     SortedSet var7 = var4.getDeltas();
                     if (!var7.isEmpty()) {
                        double var8 = (double)(System.nanoTime() - var6) / 1000000.0;
                        BungeeCord.getInstance().getLogger().info("[Async-Timings] (" + var3.getClass().getSimpleName() + ") Total-Time=" + var8 + " MS");
                        if (!var7.isEmpty()) {
                           BungeeCord.getInstance().getLogger().info("> Plugin timings:");

                           for (Entry var11 : (SortedSet<Entry>)var7) {
                              BungeeCord.getInstance()
                                 .getLogger()
                                 .info("  > " + ((Plugin)var11.getKey()).getDescription().getName() + ": " + var11.getValue() + " MS");
                           }
                        }
                     }

                     var5.done(var5x, var6x);
                  }
               );
            }

            this.delegate.post(var1, var2);
         } else {
            this.postSync(var1, var2);
         }
      }

      private <T> void postSync(T var1, EventExceptionHandler<T> var2) {
         EventHandlerMethod[] var3 = this.delegate.getByEventBaked().get(var1.getClass());
         if (var3 != null && var3.length > 0) {
            Stopwatch var4 = new Stopwatch();
            long var5 = System.nanoTime();

            for (EventHandlerMethod var10 : var3) {
               try {
                  var4.start(var10);
                  var10.invoke(var1);
                  var4.stop(var10);
               } catch (IllegalAccessException var12) {
                  throw new Error("Method became inaccessible: " + var1, var12);
               } catch (IllegalArgumentException var13) {
                  throw new Error("Method rejected target/argument: " + var1, var13);
               } catch (InvocationTargetException var14) {
                  throw new Error("Error: " + var1, var14);
               }
            }

            double var15 = (double)(System.nanoTime() - var5) / 1000000.0;
            SortedSet var16 = var4.getDeltas();
            if (!var16.isEmpty()) {
               BungeeCord.getInstance().getLogger().info("[Sync-Timings] (" + var1.getClass().getSimpleName() + ") Total-Time=" + var15 + " MS");
               if (!var16.isEmpty()) {
                  BungeeCord.getInstance().getLogger().info("> Plugin listener timings:");

                  for (Entry var11 : (SortedSet<Entry>)var16) {
                     BungeeCord.getInstance()
                        .getLogger()
                        .info("  > " + ((EventHandlerMethod)var11.getKey()).getMethod().getDeclaringClass().getName() + ": " + var11.getValue() + " MS");
                  }
               }
            }
         }
      }

      @Override
      public void register(Object var1) {
         this.delegate.register(var1);
      }

      @Override
      public void unregister(Object var1) {
         this.delegate.unregister(var1);
      }
   }
}
