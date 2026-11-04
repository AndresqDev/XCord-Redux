package org.apache.logging.log4j.message;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Map.Entry;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.StringBuilderFormattable;

@AsynchronouslyFormattable
public class ThreadDumpMessage implements Message, StringBuilderFormattable {
   private static final long serialVersionUID = -1103400781608841088L;
   private static ThreadDumpMessage.ThreadInfoFactory FACTORY;
   private volatile Map<ThreadInformation, StackTraceElement[]> threads;
   private final String title;
   private String formattedMessage;

   public ThreadDumpMessage(String var1) {
      this.title = var1 == null ? "" : var1;
      this.threads = getFactory().createThreadInfo();
   }

   private ThreadDumpMessage(String var1, String var2) {
      this.formattedMessage = var1;
      this.title = var2 == null ? "" : var2;
   }

   private static ThreadDumpMessage.ThreadInfoFactory getFactory() {
      if (FACTORY == null) {
         FACTORY = initFactory(ThreadDumpMessage.class.getClassLoader());
      }

      return FACTORY;
   }

   private static ThreadDumpMessage.ThreadInfoFactory initFactory(ClassLoader var0) {
      ServiceLoader var1 = ServiceLoader.load(ThreadDumpMessage.ThreadInfoFactory.class, var0);
      ThreadDumpMessage.ThreadInfoFactory var2 = null;

      try {
         Iterator var3 = var1.iterator();

         while (var2 == null && var3.hasNext()) {
            var2 = (ThreadDumpMessage.ThreadInfoFactory)var3.next();
         }
      } catch (LinkageError | Exception | ServiceConfigurationError var4) {
         StatusLogger.getLogger().info("ThreadDumpMessage uses BasicThreadInfoFactory: could not load extended ThreadInfoFactory: {}", var4.toString());
         var2 = null;
      }

      return (ThreadDumpMessage.ThreadInfoFactory)(var2 == null ? new ThreadDumpMessage.BasicThreadInfoFactory() : var2);
   }

   @Override
   public String toString() {
      return this.getFormattedMessage();
   }

   @Override
   public String getFormattedMessage() {
      if (this.formattedMessage != null) {
         return this.formattedMessage;
      } else {
         StringBuilder var1 = new StringBuilder(255);
         this.formatTo(var1);
         return var1.toString();
      }
   }

   @Override
   public void formatTo(StringBuilder var1) {
      var1.append(this.title);
      if (this.title.length() > 0) {
         var1.append('\n');
      }

      for (Entry var3 : this.threads.entrySet()) {
         ThreadInformation var4 = (ThreadInformation)var3.getKey();
         var4.printThreadInfo(var1);
         var4.printStack(var1, (StackTraceElement[])var3.getValue());
         var1.append('\n');
      }
   }

   @Override
   public String getFormat() {
      return this.title == null ? "" : this.title;
   }

   @Override
   public Object[] getParameters() {
      return null;
   }

   protected Object writeReplace() {
      return new ThreadDumpMessage.ThreadDumpMessageProxy(this);
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Proxy required");
   }

   @Override
   public Throwable getThrowable() {
      return null;
   }

   private static class BasicThreadInfoFactory implements ThreadDumpMessage.ThreadInfoFactory {
      private BasicThreadInfoFactory() {
      }

      @Override
      public Map<ThreadInformation, StackTraceElement[]> createThreadInfo() {
         Map var1 = Thread.getAllStackTraces();
         HashMap var2 = new HashMap(var1.size());

         for (Entry var4 : var1.entrySet()) {
            var2.put(new BasicThreadInformation((Thread)var4.getKey()), var4.getValue());
         }

         return var2;
      }
   }

   private static class ThreadDumpMessageProxy implements Serializable {
      private static final long serialVersionUID = -3476620450287648269L;
      private final String formattedMsg;
      private final String title;

      ThreadDumpMessageProxy(ThreadDumpMessage var1) {
         this.formattedMsg = var1.getFormattedMessage();
         this.title = var1.title;
      }

      protected Object readResolve() {
         return new ThreadDumpMessage(this.formattedMsg, this.title);
      }
   }

   public interface ThreadInfoFactory {
      Map<ThreadInformation, StackTraceElement[]> createThreadInfo();
   }
}
