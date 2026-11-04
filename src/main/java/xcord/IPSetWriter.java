package xcord;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class IPSetWriter extends Thread {
   private final File tempFile = new File(UUID.randomUUID().toString());
   private final Queue<String> queue = new ConcurrentLinkedQueue<>();
   private final AtomicInteger pending = new AtomicInteger();
   private final String tableName;
   private final int intervalMs;
   private final String addPrefix;
   private final String ipsetName;

   public IPSetWriter(int var1, String var2, String var3) {
      this.intervalMs = var1;
      this.tableName = var3;
      this.ipsetName = var2;
      this.addPrefix = "add " + var2 + " ";
      this.start();
   }

   public void a(String var1) {
      this.queue.add(var1);
      this.pending.incrementAndGet();
   }

   @Override
   public void run() {
      while (true) {
         long var1 = (long)this.intervalMs;

         try {
            if (!this.queue.isEmpty()) {
               long var3 = System.currentTimeMillis();
               this.a();
               long var5 = System.currentTimeMillis() - var3;
               if (var5 > (long)(this.intervalMs - 10)) {
                  var1 = 10L;
               } else {
                  var1 = (long)this.intervalMs - var5;
               }
            }
         } catch (Exception var8) {
            var8.printStackTrace();
         }

         try {
            Thread.sleep(var1);
         } catch (InterruptedException var7) {
            var7.printStackTrace();
            return;
         }
      }
   }

   private void a() throws Exception {
      int var1 = this.pending.getAndSet(0);
      if (var1 < 8) {
         while (!this.queue.isEmpty()) {
            new ProcessBuilder("sudo", "ipset", "add", this.ipsetName, this.queue.poll()).redirectErrorStream(true).start();
         }
      } else {
         BufferedWriter var2 = new BufferedWriter(new FileWriter(this.tempFile), 500000);

         try {
            var2.write(this.tableName);
            var2.newLine();

            while (!this.queue.isEmpty()) {
               var2.write(this.addPrefix + this.queue.poll());
               var2.newLine();
            }
         } catch (Throwable var6) {
            try {
               var2.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }

            throw var6;
         }

         var2.close();
         ProcessBuilder var7 = new ProcessBuilder("sudo", "ipset", "restore", "-!").redirectErrorStream(true);
         var7.redirectInput(this.tempFile);
         var7.start().waitFor();
         this.tempFile.delete();
      }
   }
}
