package io.netty.util;

import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class ResourceLeakDetector<T> {
   private static final String PROP_LEVEL_OLD = "io.netty.leakDetectionLevel";
   private static final String PROP_LEVEL = "io.netty.leakDetection.level";
   private static final ResourceLeakDetector.Level DEFAULT_LEVEL = ResourceLeakDetector.Level.SIMPLE;
   private static final String PROP_TARGET_RECORDS = "io.netty.leakDetection.targetRecords";
   private static final int DEFAULT_TARGET_RECORDS = 4;
   private static final String PROP_SAMPLING_INTERVAL = "io.netty.leakDetection.samplingInterval";
   private static final int DEFAULT_SAMPLING_INTERVAL = 128;
   private static final int TARGET_RECORDS;
   static final int SAMPLING_INTERVAL;
   private static ResourceLeakDetector.Level level;
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ResourceLeakDetector.class);
   private final Set<ResourceLeakDetector.DefaultResourceLeak<?>> allLeaks = Collections.newSetFromMap(new ConcurrentHashMap<>());
   private final ReferenceQueue<Object> refQueue = new ReferenceQueue<>();
   private final Set<String> reportedLeaks = Collections.newSetFromMap(new ConcurrentHashMap<>());
   private final String resourceType;
   private final int samplingInterval;
   private static final AtomicReference<String[]> excludedMethods;

   @Deprecated
   public static void setEnabled(boolean var0) {
      setLevel(var0 ? ResourceLeakDetector.Level.SIMPLE : ResourceLeakDetector.Level.DISABLED);
   }

   public static boolean isEnabled() {
      return getLevel().ordinal() > ResourceLeakDetector.Level.DISABLED.ordinal();
   }

   public static void setLevel(ResourceLeakDetector.Level var0) {
      level = ObjectUtil.checkNotNull(var0, "level");
   }

   public static ResourceLeakDetector.Level getLevel() {
      return level;
   }

   @Deprecated
   public ResourceLeakDetector(Class<?> var1) {
      this(StringUtil.simpleClassName(var1));
   }

   @Deprecated
   public ResourceLeakDetector(String var1) {
      this(var1, 128, Long.MAX_VALUE);
   }

   @Deprecated
   public ResourceLeakDetector(Class<?> var1, int var2, long var3) {
      this(var1, var2);
   }

   public ResourceLeakDetector(Class<?> var1, int var2) {
      this(StringUtil.simpleClassName(var1), var2, Long.MAX_VALUE);
   }

   @Deprecated
   public ResourceLeakDetector(String var1, int var2, long var3) {
      this.resourceType = ObjectUtil.checkNotNull(var1, "resourceType");
      this.samplingInterval = var2;
   }

   @Deprecated
   public final ResourceLeak open(T var1) {
      return this.track0((T)var1);
   }

   public final ResourceLeakTracker<T> track(T var1) {
      return this.track0((T)var1);
   }

   private ResourceLeakDetector.DefaultResourceLeak track0(T var1) {
      ResourceLeakDetector.Level var2 = level;
      if (var2 == ResourceLeakDetector.Level.DISABLED) {
         return null;
      } else if (var2.ordinal() < ResourceLeakDetector.Level.PARANOID.ordinal()) {
         if (PlatformDependent.threadLocalRandom().nextInt(this.samplingInterval) == 0) {
            this.reportLeak();
            return new ResourceLeakDetector.DefaultResourceLeak(var1, this.refQueue, this.allLeaks, this.getInitialHint(this.resourceType));
         } else {
            return null;
         }
      } else {
         this.reportLeak();
         return new ResourceLeakDetector.DefaultResourceLeak(var1, this.refQueue, this.allLeaks, this.getInitialHint(this.resourceType));
      }
   }

   private void clearRefQueue() {
      while (true) {
         ResourceLeakDetector.DefaultResourceLeak var1 = (ResourceLeakDetector.DefaultResourceLeak)this.refQueue.poll();
         if (var1 == null) {
            return;
         }

         var1.dispose();
      }
   }

   protected boolean needReport() {
      return logger.isErrorEnabled();
   }

   private void reportLeak() {
      if (!this.needReport()) {
         this.clearRefQueue();
      } else {
         while (true) {
            ResourceLeakDetector.DefaultResourceLeak var1 = (ResourceLeakDetector.DefaultResourceLeak)this.refQueue.poll();
            if (var1 == null) {
               return;
            }

            if (var1.dispose()) {
               String var2 = var1.getReportAndClearRecords();
               if (this.reportedLeaks.add(var2)) {
                  if (var2.isEmpty()) {
                     this.reportUntracedLeak(this.resourceType);
                  } else {
                     this.reportTracedLeak(this.resourceType, var2);
                  }
               }
            }
         }
      }
   }

   protected void reportTracedLeak(String var1, String var2) {
      logger.error(
         "LEAK: {}.release() was not called before it's garbage-collected. See https://netty.io/wiki/reference-counted-objects.html for more information.{}",
         var1,
         var2
      );
   }

   protected void reportUntracedLeak(String var1) {
      logger.error(
         "LEAK: {}.release() was not called before it's garbage-collected. Enable advanced leak reporting to find out where the leak occurred. To enable advanced leak reporting, specify the JVM option '-D{}={}' or call {}.setLevel() See https://netty.io/wiki/reference-counted-objects.html for more information.",
         var1,
         "io.netty.leakDetection.level",
         ResourceLeakDetector.Level.ADVANCED.name().toLowerCase(),
         StringUtil.simpleClassName(this)
      );
   }

   @Deprecated
   protected void reportInstancesLeak(String var1) {
   }

   protected Object getInitialHint(String var1) {
      return null;
   }

   public static void addExclusions(Class var0, String... var1) {
      HashSet var2 = new HashSet<>(Arrays.asList(var1));

      for (Method var6 : var0.getDeclaredMethods()) {
         if (var2.remove(var6.getName()) && var2.isEmpty()) {
            break;
         }
      }

      if (!var2.isEmpty()) {
         throw new IllegalArgumentException("Can't find '" + var2 + "' in " + var0.getName());
      } else {
         String[] var7;
         String[] var8;
         do {
            var7 = excludedMethods.get();
            var8 = Arrays.copyOf(var7, var7.length + 2 * var1.length);

            for (int var9 = 0; var9 < var1.length; var9++) {
               var8[var7.length + var9 * 2] = var0.getName();
               var8[var7.length + var9 * 2 + 1] = var1[var9];
            }
         } while (!excludedMethods.compareAndSet(var7, var8));
      }
   }

   static {
      boolean var0;
      if (SystemPropertyUtil.get("io.netty.noResourceLeakDetection") != null) {
         var0 = SystemPropertyUtil.getBoolean("io.netty.noResourceLeakDetection", false);
         logger.debug("-Dio.netty.noResourceLeakDetection: {}", var0);
         logger.warn(
            "-Dio.netty.noResourceLeakDetection is deprecated. Use '-D{}={}' instead.", "io.netty.leakDetection.level", DEFAULT_LEVEL.name().toLowerCase()
         );
      } else {
         var0 = false;
      }

      ResourceLeakDetector.Level var1 = var0 ? ResourceLeakDetector.Level.DISABLED : DEFAULT_LEVEL;
      String var2 = SystemPropertyUtil.get("io.netty.leakDetectionLevel", var1.name());
      var2 = SystemPropertyUtil.get("io.netty.leakDetection.level", var2);
      ResourceLeakDetector.Level var3 = ResourceLeakDetector.Level.parseLevel(var2);
      TARGET_RECORDS = SystemPropertyUtil.getInt("io.netty.leakDetection.targetRecords", 4);
      SAMPLING_INTERVAL = SystemPropertyUtil.getInt("io.netty.leakDetection.samplingInterval", 128);
      level = var3;
      if (logger.isDebugEnabled()) {
         logger.debug("-D{}: {}", "io.netty.leakDetection.level", var3.name().toLowerCase());
         logger.debug("-D{}: {}", "io.netty.leakDetection.targetRecords", TARGET_RECORDS);
      }

      excludedMethods = new AtomicReference<>(EmptyArrays.EMPTY_STRINGS);
   }

   private static final class DefaultResourceLeak<T> extends WeakReference<Object> implements ResourceLeak, ResourceLeakTracker<T> {
      private static final AtomicReferenceFieldUpdater<ResourceLeakDetector.DefaultResourceLeak<?>, ResourceLeakDetector.TraceRecord> headUpdater = AtomicReferenceFieldUpdater.newUpdater(
         ResourceLeakDetector.DefaultResourceLeak.class, ResourceLeakDetector.TraceRecord.class, "head"
      );
      private static final AtomicIntegerFieldUpdater<ResourceLeakDetector.DefaultResourceLeak<?>> droppedRecordsUpdater = AtomicIntegerFieldUpdater.newUpdater(
         ResourceLeakDetector.DefaultResourceLeak.class, "droppedRecords"
      );
      private volatile ResourceLeakDetector.TraceRecord head;
      private volatile int droppedRecords;
      private final Set<ResourceLeakDetector.DefaultResourceLeak<?>> allLeaks;
      private final int trackedHash;

      DefaultResourceLeak(Object var1, ReferenceQueue<Object> var2, Set<ResourceLeakDetector.DefaultResourceLeak<?>> var3, Object var4) {
         super(var1, var2);

         assert var1 != null;

         this.trackedHash = System.identityHashCode(var1);
         var3.add(this);
         headUpdater.set(
            this,
            var4 == null
               ? new ResourceLeakDetector.TraceRecord(ResourceLeakDetector.TraceRecord.BOTTOM)
               : new ResourceLeakDetector.TraceRecord(ResourceLeakDetector.TraceRecord.BOTTOM, var4)
         );
         this.allLeaks = var3;
      }

      @Override
      public void record() {
         this.record0(null);
      }

      @Override
      public void record(Object var1) {
         this.record0(var1);
      }

      private void record0(Object var1) {
         if (ResourceLeakDetector.TARGET_RECORDS > 0) {
            ResourceLeakDetector.TraceRecord var2;
            ResourceLeakDetector.TraceRecord var4;
            boolean var5;
            do {
               ResourceLeakDetector.TraceRecord var3;
               if ((var3 = var2 = headUpdater.get(this)) == null) {
                  return;
               }

               int var6 = var2.pos + 1;
               if (var6 >= ResourceLeakDetector.TARGET_RECORDS) {
                  int var7 = Math.min(var6 - ResourceLeakDetector.TARGET_RECORDS, 30);
                  if (var5 = PlatformDependent.threadLocalRandom().nextInt(1 << var7) != 0) {
                     var3 = var2.next;
                  }
               } else {
                  var5 = false;
               }

               var4 = var1 != null ? new ResourceLeakDetector.TraceRecord(var3, var1) : new ResourceLeakDetector.TraceRecord(var3);
            } while (!headUpdater.compareAndSet(this, var2, var4));

            if (var5) {
               droppedRecordsUpdater.incrementAndGet(this);
            }
         }
      }

      boolean dispose() {
         this.clear();
         return this.allLeaks.remove(this);
      }

      @Override
      public boolean close() {
         if (this.allLeaks.remove(this)) {
            this.clear();
            headUpdater.set(this, null);
            return true;
         } else {
            return false;
         }
      }

      @Override
      public boolean close(T var1) {
         assert this.trackedHash == System.identityHashCode(var1);

         boolean var2;
         try {
            var2 = this.close();
         } finally {
            reachabilityFence0(var1);
         }

         return var2;
      }

      private static void reachabilityFence0(Object var0) {
         if (var0 != null) {
            synchronized (var0) {
               ;
            }
         }
      }

      @Override
      public String toString() {
         ResourceLeakDetector.TraceRecord var1 = headUpdater.get(this);
         return this.generateReport(var1);
      }

      String getReportAndClearRecords() {
         ResourceLeakDetector.TraceRecord var1 = headUpdater.getAndSet(this, null);
         return this.generateReport(var1);
      }

      private String generateReport(ResourceLeakDetector.TraceRecord var1) {
         if (var1 == null) {
            return "";
         } else {
            int var2 = droppedRecordsUpdater.get(this);
            int var3 = 0;
            int var4 = var1.pos + 1;
            StringBuilder var5 = new StringBuilder(var4 * 2048).append(StringUtil.NEWLINE);
            var5.append("Recent access records: ").append(StringUtil.NEWLINE);
            int var6 = 1;

            for (HashSet var7 = new HashSet(var4); var1 != ResourceLeakDetector.TraceRecord.BOTTOM; var1 = var1.next) {
               String var8 = var1.toString();
               if (var7.add(var8)) {
                  if (var1.next == ResourceLeakDetector.TraceRecord.BOTTOM) {
                     var5.append("Created at:").append(StringUtil.NEWLINE).append(var8);
                  } else {
                     var5.append('#').append(var6++).append(':').append(StringUtil.NEWLINE).append(var8);
                  }
               } else {
                  var3++;
               }
            }

            if (var3 > 0) {
               var5.append(": ").append(var3).append(" leak records were discarded because they were duplicates").append(StringUtil.NEWLINE);
            }

            if (var2 > 0) {
               var5.append(": ")
                  .append(var2)
                  .append(" leak records were discarded because the leak record count is targeted to ")
                  .append(ResourceLeakDetector.TARGET_RECORDS)
                  .append(". Use system property ")
                  .append("io.netty.leakDetection.targetRecords")
                  .append(" to increase the limit.")
                  .append(StringUtil.NEWLINE);
            }

            var5.setLength(var5.length() - StringUtil.NEWLINE.length());
            return var5.toString();
         }
      }
   }

   public static enum Level {
      DISABLED,
      SIMPLE,
      ADVANCED,
      PARANOID;

      static ResourceLeakDetector.Level parseLevel(String var0) {
         String var1 = var0.trim();

         for (ResourceLeakDetector.Level var5 : values()) {
            if (var1.equalsIgnoreCase(var5.name()) || var1.equals(String.valueOf(var5.ordinal()))) {
               return var5;
            }
         }

         return ResourceLeakDetector.DEFAULT_LEVEL;
      }
   }

   private static class TraceRecord extends Throwable {
      private static final long serialVersionUID = 6065153674892850720L;
      private static final ResourceLeakDetector.TraceRecord BOTTOM = new ResourceLeakDetector.TraceRecord() {
         private static final long serialVersionUID = 7396077602074694571L;

         @Override
         public Throwable fillInStackTrace() {
            return this;
         }
      };
      private final String hintString;
      private final ResourceLeakDetector.TraceRecord next;
      private final int pos;

      TraceRecord(ResourceLeakDetector.TraceRecord var1, Object var2) {
         this.hintString = var2 instanceof ResourceLeakHint ? ((ResourceLeakHint)var2).toHintString() : var2.toString();
         this.next = var1;
         this.pos = var1.pos + 1;
      }

      TraceRecord(ResourceLeakDetector.TraceRecord var1) {
         this.hintString = null;
         this.next = var1;
         this.pos = var1.pos + 1;
      }

      private TraceRecord() {
         this.hintString = null;
         this.next = null;
         this.pos = -1;
      }

      @Override
      public String toString() {
         StringBuilder var1 = new StringBuilder(2048);
         if (this.hintString != null) {
            var1.append("\tHint: ").append(this.hintString).append(StringUtil.NEWLINE);
         }

         StackTraceElement[] var2 = this.getStackTrace();

         label30:
         for (int var3 = 3; var3 < var2.length; var3++) {
            StackTraceElement var4 = var2[var3];
            String[] var5 = ResourceLeakDetector.excludedMethods.get();

            for (byte var6 = 0; var6 < var5.length; var6 += 2) {
               if (var5[var6].equals(var4.getClassName()) && var5[var6 + 1].equals(var4.getMethodName())) {
                  continue label30;
               }
            }

            var1.append('\t');
            var1.append(var4.toString());
            var1.append(StringUtil.NEWLINE);
         }

         return var1.toString();
      }
   }
}
