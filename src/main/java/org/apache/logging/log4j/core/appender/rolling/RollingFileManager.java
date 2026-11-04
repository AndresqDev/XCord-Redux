package org.apache.logging.log4j.core.appender.rolling;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.LifeCycle2;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConfigurationFactoryData;
import org.apache.logging.log4j.core.appender.FileManager;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.appender.rolling.action.AbstractAction;
import org.apache.logging.log4j.core.appender.rolling.action.Action;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.core.util.FileUtils;
import org.apache.logging.log4j.core.util.Log4jThreadFactory;

public class RollingFileManager extends FileManager {
   private static RollingFileManager.RollingFileManagerFactory factory = new RollingFileManager.RollingFileManagerFactory();
   private static final int MAX_TRIES = 3;
   private static final int MIN_DURATION = 100;
   private static final FileTime EPOCH = FileTime.fromMillis(0L);
   protected long size;
   private long initialTime;
   private volatile PatternProcessor patternProcessor;
   private final Semaphore semaphore = new Semaphore(1);
   private final Log4jThreadFactory threadFactory = Log4jThreadFactory.createThreadFactory("RollingFileManager");
   private volatile TriggeringPolicy triggeringPolicy;
   private volatile RolloverStrategy rolloverStrategy;
   private volatile boolean renameEmptyFiles;
   private volatile boolean initialized;
   private volatile String fileName;
   private final boolean directWrite;
   private final CopyOnWriteArrayList<RolloverListener> rolloverListeners = new CopyOnWriteArrayList<>();
   private final ExecutorService asyncExecutor = new ThreadPoolExecutor(
      0, Integer.MAX_VALUE, 0L, TimeUnit.MILLISECONDS, new RollingFileManager.EmptyQueue(), this.threadFactory
   );
   private static final AtomicReferenceFieldUpdater<RollingFileManager, TriggeringPolicy> triggeringPolicyUpdater = AtomicReferenceFieldUpdater.newUpdater(
      RollingFileManager.class, TriggeringPolicy.class, "triggeringPolicy"
   );
   private static final AtomicReferenceFieldUpdater<RollingFileManager, RolloverStrategy> rolloverStrategyUpdater = AtomicReferenceFieldUpdater.newUpdater(
      RollingFileManager.class, RolloverStrategy.class, "rolloverStrategy"
   );
   private static final AtomicReferenceFieldUpdater<RollingFileManager, PatternProcessor> patternProcessorUpdater = AtomicReferenceFieldUpdater.newUpdater(
      RollingFileManager.class, PatternProcessor.class, "patternProcessor"
   );

   @Deprecated
   protected RollingFileManager(
      String var1,
      String var2,
      OutputStream var3,
      boolean var4,
      long var5,
      long var7,
      TriggeringPolicy var9,
      RolloverStrategy var10,
      String var11,
      Layout<? extends Serializable> var12,
      int var13,
      boolean var14
   ) {
      this(var1, var2, var3, var4, var5, var7, var9, var10, var11, var12, var14, ByteBuffer.wrap(new byte[Constants.ENCODER_BYTE_BUFFER_SIZE]));
   }

   @Deprecated
   protected RollingFileManager(
      String var1,
      String var2,
      OutputStream var3,
      boolean var4,
      long var5,
      long var7,
      TriggeringPolicy var9,
      RolloverStrategy var10,
      String var11,
      Layout<? extends Serializable> var12,
      boolean var13,
      ByteBuffer var14
   ) {
      super(var1 != null ? var1 : var2, var3, var4, false, var11, var12, var13, var14);
      this.size = var5;
      this.initialTime = var7;
      this.triggeringPolicy = var9;
      this.rolloverStrategy = var10;
      this.patternProcessor = new PatternProcessor(var2);
      this.patternProcessor.setPrevFileTime(var7);
      this.fileName = var1;
      this.directWrite = var10 instanceof DirectWriteRolloverStrategy;
   }

   @Deprecated
   protected RollingFileManager(
      LoggerContext var1,
      String var2,
      String var3,
      OutputStream var4,
      boolean var5,
      boolean var6,
      long var7,
      long var9,
      TriggeringPolicy var11,
      RolloverStrategy var12,
      String var13,
      Layout<? extends Serializable> var14,
      boolean var15,
      ByteBuffer var16
   ) {
      super(var1, var2 != null ? var2 : var3, var4, var5, false, var6, var13, var14, var15, var16);
      this.size = var7;
      this.initialTime = var9;
      this.triggeringPolicy = var11;
      this.rolloverStrategy = var12;
      this.patternProcessor = new PatternProcessor(var3);
      this.patternProcessor.setPrevFileTime(var9);
      this.fileName = var2;
      this.directWrite = var12 instanceof DirectWriteRolloverStrategy;
   }

   protected RollingFileManager(
      LoggerContext var1,
      String var2,
      String var3,
      OutputStream var4,
      boolean var5,
      boolean var6,
      long var7,
      long var9,
      TriggeringPolicy var11,
      RolloverStrategy var12,
      String var13,
      Layout<? extends Serializable> var14,
      String var15,
      String var16,
      String var17,
      boolean var18,
      ByteBuffer var19
   ) {
      super(var1, var2 != null ? var2 : var3, var4, var5, false, var6, var13, var14, var15, var16, var17, var18, var19);
      this.size = var7;
      this.initialTime = var9;
      this.patternProcessor = new PatternProcessor(var3);
      this.patternProcessor.setPrevFileTime(var9);
      this.triggeringPolicy = var11;
      this.rolloverStrategy = var12;
      this.fileName = var2;
      this.directWrite = var12 instanceof DirectFileRolloverStrategy;
   }

   public void initialize() {
      if (!this.initialized) {
         LOGGER.debug("Initializing triggering policy {}", this.triggeringPolicy);
         this.initialized = true;
         if (this.directWrite) {
            File var1 = new File(this.getFileName());
            if (var1.exists()) {
               this.size = var1.length();
            } else {
               ((DirectFileRolloverStrategy)this.rolloverStrategy).clearCurrentFileName();
            }
         }

         this.triggeringPolicy.initialize(this);
         if (this.triggeringPolicy instanceof LifeCycle) {
            ((LifeCycle)this.triggeringPolicy).start();
         }

         if (this.directWrite) {
            File var2 = new File(this.getFileName());
            if (var2.exists()) {
               this.size = var2.length();
            } else {
               ((DirectFileRolloverStrategy)this.rolloverStrategy).clearCurrentFileName();
            }
         }
      }
   }

   public static RollingFileManager getFileManager(
      String var0,
      String var1,
      boolean var2,
      boolean var3,
      TriggeringPolicy var4,
      RolloverStrategy var5,
      String var6,
      Layout<? extends Serializable> var7,
      int var8,
      boolean var9,
      boolean var10,
      String var11,
      String var12,
      String var13,
      Configuration var14
   ) {
      if (var5 instanceof DirectWriteRolloverStrategy && var0 != null) {
         LOGGER.error("The fileName attribute must not be specified with the DirectWriteRolloverStrategy");
         return null;
      } else {
         String var15 = var0 == null ? var1 : var0;
         return narrow(
            RollingFileManager.class,
            getManager(
               var15,
               new RollingFileManager.FactoryData(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14),
               factory
            )
         );
      }
   }

   public void addRolloverListener(RolloverListener var1) {
      this.rolloverListeners.add(var1);
   }

   public void removeRolloverListener(RolloverListener var1) {
      this.rolloverListeners.remove(var1);
   }

   @Override
   public String getFileName() {
      if (this.directWrite) {
         this.fileName = ((DirectFileRolloverStrategy)this.rolloverStrategy).getCurrentFileName(this);
      }

      return this.fileName;
   }

   @Override
   protected void createParentDir(File var1) {
      if (this.directWrite) {
         var1.getParentFile().mkdirs();
      }
   }

   public boolean isDirectWrite() {
      return this.directWrite;
   }

   public FileExtension getFileExtension() {
      return this.patternProcessor.getFileExtension();
   }

   @Override
   protected synchronized void write(byte[] var1, int var2, int var3, boolean var4) {
      super.write(var1, var2, var3, var4);
   }

   @Override
   protected synchronized void writeToDestination(byte[] var1, int var2, int var3) {
      this.size += (long)var3;
      super.writeToDestination(var1, var2, var3);
   }

   public boolean isRenameEmptyFiles() {
      return this.renameEmptyFiles;
   }

   public void setRenameEmptyFiles(boolean var1) {
      this.renameEmptyFiles = var1;
   }

   public long getFileSize() {
      return this.size + (long)this.byteBuffer.position();
   }

   public long getFileTime() {
      return this.initialTime;
   }

   public synchronized void checkRollover(LogEvent var1) {
      if (this.triggeringPolicy.isTriggeringEvent(var1)) {
         this.rollover();
      }
   }

   @Override
   public boolean releaseSub(long var1, TimeUnit var3) {
      LOGGER.debug("Shutting down RollingFileManager {}", this.getName());
      boolean var4 = true;
      if (this.triggeringPolicy instanceof LifeCycle2) {
         var4 &= ((LifeCycle2)this.triggeringPolicy).stop(var1, var3);
      } else if (this.triggeringPolicy instanceof LifeCycle) {
         ((LifeCycle)this.triggeringPolicy).stop();
         var4 &= true;
      }

      boolean var5 = super.releaseSub(var1, var3) && var4;
      this.asyncExecutor.shutdown();

      try {
         long var6 = var3.toMillis(var1);
         long var8 = 100L < var6 ? var6 : 100L;

         for (int var10 = 1; var10 <= 3 && !this.asyncExecutor.isTerminated(); var10++) {
            this.asyncExecutor.awaitTermination(var8 * (long)var10, TimeUnit.MILLISECONDS);
         }

         if (this.asyncExecutor.isTerminated()) {
            LOGGER.debug("All asynchronous threads have terminated");
         } else {
            this.asyncExecutor.shutdownNow();

            try {
               this.asyncExecutor.awaitTermination(var1, var3);
               if (this.asyncExecutor.isTerminated()) {
                  LOGGER.debug("All asynchronous threads have terminated");
               } else {
                  LOGGER.debug("RollingFileManager shutting down but some asynchronous services may not have completed");
               }
            } catch (InterruptedException var12) {
               LOGGER.warn("RollingFileManager stopped but some asynchronous services may not have completed.");
            }
         }
      } catch (InterruptedException var13) {
         this.asyncExecutor.shutdownNow();

         try {
            this.asyncExecutor.awaitTermination(var1, var3);
            if (this.asyncExecutor.isTerminated()) {
               LOGGER.debug("All asynchronous threads have terminated");
            }
         } catch (InterruptedException var11) {
            LOGGER.warn("RollingFileManager stopped but some asynchronous services may not have completed.");
         }

         Thread.currentThread().interrupt();
      }

      LOGGER.debug("RollingFileManager shutdown completed with status {}", var5);
      return var5;
   }

   public synchronized void rollover(Date var1, Date var2) {
      this.getPatternProcessor().setPrevFileTime(var1.getTime());
      this.getPatternProcessor().setCurrentFileTime(var2.getTime());
      this.rollover();
   }

   public synchronized void rollover() {
      if (this.hasOutputStream() || this.isCreateOnDemand() || this.isDirectWrite()) {
         String var1 = this.fileName;
         if (this.rolloverListeners.size() > 0) {
            for (RolloverListener var3 : this.rolloverListeners) {
               try {
                  var3.rolloverTriggered(var1);
               } catch (Exception var12) {
                  LOGGER.warn("Rollover Listener {} failed with {}: {}", var3.getClass().getSimpleName(), var12.getClass().getName(), var12.getMessage());
               }
            }
         }

         boolean var14 = Thread.interrupted();

         try {
            if (var14) {
               LOGGER.warn("RollingFileManager cleared thread interrupted state, continue to rollover");
            }

            if (this.rollover(this.rolloverStrategy)) {
               try {
                  this.size = 0L;
                  this.initialTime = System.currentTimeMillis();
                  this.createFileAfterRollover();
               } catch (IOException var11) {
                  this.logError("Failed to create file after rollover", var11);
               }
            }
         } finally {
            if (var14) {
               Thread.currentThread().interrupt();
            }
         }

         if (this.rolloverListeners.size() > 0) {
            for (RolloverListener var4 : this.rolloverListeners) {
               try {
                  var4.rolloverComplete(var1);
               } catch (Exception var10) {
                  LOGGER.warn("Rollover Listener {} failed with {}: {}", var4.getClass().getSimpleName(), var10.getClass().getName(), var10.getMessage());
               }
            }
         }
      }
   }

   protected void createFileAfterRollover() throws IOException {
      this.setOutputStream(this.createOutputStream());
   }

   public PatternProcessor getPatternProcessor() {
      return this.patternProcessor;
   }

   public void setTriggeringPolicy(TriggeringPolicy var1) {
      var1.initialize(this);
      TriggeringPolicy var2 = this.triggeringPolicy;
      int var3 = 0;
      boolean var4 = false;

      while (!(var4 = triggeringPolicyUpdater.compareAndSet(this, this.triggeringPolicy, var1)) && ++var3 < 3) {
      }

      if (var4) {
         if (var1 instanceof LifeCycle) {
            ((LifeCycle)var1).start();
         }

         if (var2 instanceof LifeCycle) {
            ((LifeCycle)var2).stop();
         }
      } else if (var1 instanceof LifeCycle) {
         ((LifeCycle)var1).stop();
      }
   }

   public void setRolloverStrategy(RolloverStrategy var1) {
      rolloverStrategyUpdater.compareAndSet(this, this.rolloverStrategy, var1);
   }

   public void setPatternProcessor(PatternProcessor var1) {
      patternProcessorUpdater.compareAndSet(this, this.patternProcessor, var1);
   }

   public <T extends TriggeringPolicy> T getTriggeringPolicy() {
      return (T)this.triggeringPolicy;
   }

   Semaphore getSemaphore() {
      return this.semaphore;
   }

   public RolloverStrategy getRolloverStrategy() {
      return this.rolloverStrategy;
   }

   private boolean rollover(RolloverStrategy var1) {
      boolean var2 = false;

      try {
         this.semaphore.acquire();
         var2 = true;
      } catch (InterruptedException var11) {
         this.logError("Thread interrupted while attempting to check rollover", var11);
         return false;
      }

      boolean var3 = true;

      boolean var5;
      try {
         RolloverDescription var4 = var1.rollover(this);
         if (var4 == null) {
            return false;
         }

         this.writeFooter();
         this.closeOutputStream();
         if (var4.getSynchronous() != null) {
            LOGGER.debug("RollingFileManager executing synchronous {}", var4.getSynchronous());

            try {
               var3 = var4.getSynchronous().execute();
            } catch (Exception var10) {
               var3 = false;
               this.logError("Caught error in synchronous task", var10);
            }
         }

         if (var3 && var4.getAsynchronous() != null) {
            LOGGER.debug("RollingFileManager executing async {}", var4.getAsynchronous());
            this.asyncExecutor.execute(new RollingFileManager.AsyncAction(var4.getAsynchronous(), this));
            var2 = false;
         }

         var5 = true;
      } finally {
         if (var2) {
            this.semaphore.release();
         }
      }

      return var5;
   }

   @Override
   public void updateData(Object var1) {
      RollingFileManager.FactoryData var2 = (RollingFileManager.FactoryData)var1;
      this.setRolloverStrategy(var2.getRolloverStrategy());
      this.setPatternProcessor(new PatternProcessor(var2.getPattern(), this.getPatternProcessor()));
      this.setTriggeringPolicy(var2.getTriggeringPolicy());
   }

   private static long initialFileTime(File var0) {
      Path var1 = var0.toPath();
      if (Files.exists(var1)) {
         try {
            BasicFileAttributes var2 = Files.readAttributes(var1, BasicFileAttributes.class);
            FileTime var3 = var2.creationTime();
            if (var3.compareTo(EPOCH) > 0) {
               LOGGER.debug("Returning file creation time for {}", var0.getAbsolutePath());
               return var3.toMillis();
            }

            LOGGER.info("Unable to obtain file creation time for " + var0.getAbsolutePath());
         } catch (Exception var4) {
            LOGGER.info("Unable to calculate file creation time for " + var0.getAbsolutePath() + ": " + var4.getMessage());
         }
      }

      return var0.lastModified();
   }

   private static class AsyncAction extends AbstractAction {
      private final Action action;
      private final RollingFileManager manager;

      public AsyncAction(Action var1, RollingFileManager var2) {
         this.action = var1;
         this.manager = var2;
      }

      @Override
      public boolean execute() throws IOException {
         boolean var1;
         try {
            var1 = this.action.execute();
         } finally {
            this.manager.semaphore.release();
         }

         return var1;
      }

      @Override
      public void close() {
         this.action.close();
      }

      @Override
      public boolean isComplete() {
         return this.action.isComplete();
      }

      @Override
      public String toString() {
         StringBuilder var1 = new StringBuilder();
         var1.append(super.toString());
         var1.append("[action=");
         var1.append(this.action);
         var1.append(", manager=");
         var1.append(this.manager);
         var1.append(", isComplete()=");
         var1.append(this.isComplete());
         var1.append(", isInterrupted()=");
         var1.append(this.isInterrupted());
         var1.append("]");
         return var1.toString();
      }
   }

   private static class EmptyQueue extends ArrayBlockingQueue<Runnable> {
      private static final long serialVersionUID = 1L;

      EmptyQueue() {
         super(1);
      }

      @Override
      public int remainingCapacity() {
         return 0;
      }

      public boolean add(Runnable var1) {
         throw new IllegalStateException("Queue is full");
      }

      public void put(Runnable var1) throws InterruptedException {
         throw new InterruptedException("Unable to insert into queue");
      }

      public boolean offer(Runnable var1, long var2, TimeUnit var4) throws InterruptedException {
         Thread.sleep(var4.toMillis(var2));
         return false;
      }

      @Override
      public boolean addAll(Collection<? extends Runnable> var1) {
         if (var1.size() > 0) {
            throw new IllegalArgumentException("Too many items in collection");
         } else {
            return false;
         }
      }
   }

   private static class FactoryData extends ConfigurationFactoryData {
      private final String fileName;
      private final String pattern;
      private final boolean append;
      private final boolean bufferedIO;
      private final int bufferSize;
      private final boolean immediateFlush;
      private final boolean createOnDemand;
      private final TriggeringPolicy policy;
      private final RolloverStrategy strategy;
      private final String advertiseURI;
      private final Layout<? extends Serializable> layout;
      private final String filePermissions;
      private final String fileOwner;
      private final String fileGroup;

      public FactoryData(
         String var1,
         String var2,
         boolean var3,
         boolean var4,
         TriggeringPolicy var5,
         RolloverStrategy var6,
         String var7,
         Layout<? extends Serializable> var8,
         int var9,
         boolean var10,
         boolean var11,
         String var12,
         String var13,
         String var14,
         Configuration var15
      ) {
         super(var15);
         this.fileName = var1;
         this.pattern = var2;
         this.append = var3;
         this.bufferedIO = var4;
         this.bufferSize = var9;
         this.policy = var5;
         this.strategy = var6;
         this.advertiseURI = var7;
         this.layout = var8;
         this.immediateFlush = var10;
         this.createOnDemand = var11;
         this.filePermissions = var12;
         this.fileOwner = var13;
         this.fileGroup = var14;
      }

      public TriggeringPolicy getTriggeringPolicy() {
         return this.policy;
      }

      public RolloverStrategy getRolloverStrategy() {
         return this.strategy;
      }

      public String getPattern() {
         return this.pattern;
      }

      @Override
      public String toString() {
         StringBuilder var1 = new StringBuilder();
         var1.append(super.toString());
         var1.append("[pattern=");
         var1.append(this.pattern);
         var1.append(", append=");
         var1.append(this.append);
         var1.append(", bufferedIO=");
         var1.append(this.bufferedIO);
         var1.append(", bufferSize=");
         var1.append(this.bufferSize);
         var1.append(", policy=");
         var1.append(this.policy);
         var1.append(", strategy=");
         var1.append(this.strategy);
         var1.append(", advertiseURI=");
         var1.append(this.advertiseURI);
         var1.append(", layout=");
         var1.append(this.layout);
         var1.append(", filePermissions=");
         var1.append(this.filePermissions);
         var1.append(", fileOwner=");
         var1.append(this.fileOwner);
         var1.append("]");
         return var1.toString();
      }
   }

   private static class RollingFileManagerFactory implements ManagerFactory<RollingFileManager, RollingFileManager.FactoryData> {
      private RollingFileManagerFactory() {
      }

      public RollingFileManager createManager(String var1, RollingFileManager.FactoryData var2) {
         long var3 = 0L;
         File var5 = null;
         if (var2.fileName != null) {
            var5 = new File(var2.fileName);

            try {
               FileUtils.makeParentDirs(var5);
               boolean var6 = var2.createOnDemand ? false : var5.createNewFile();
               RollingFileManager.LOGGER.trace("New file '{}' created = {}", var1, var6);
            } catch (IOException var13) {
               RollingFileManager.LOGGER.error("Unable to create file " + var1, (Throwable)var13);
               return null;
            }

            var3 = var2.append ? var5.length() : 0L;
         }

         try {
            int var15 = var2.bufferedIO ? var2.bufferSize : Constants.ENCODER_BYTE_BUFFER_SIZE;
            ByteBuffer var7 = ByteBuffer.wrap(new byte[var15]);
            FileOutputStream var8 = !var2.createOnDemand && var2.fileName != null ? new FileOutputStream(var2.fileName, var2.append) : null;
            long var9 = var5 != null && var5.exists() ? RollingFileManager.initialFileTime(var5) : 0L;
            boolean var11 = var5 != null && var5.exists() && var5.length() == 0L;
            RollingFileManager var12 = new RollingFileManager(
               var2.getLoggerContext(),
               var2.fileName,
               var2.pattern,
               var8,
               var2.append,
               var2.createOnDemand,
               var3,
               var9,
               var2.policy,
               var2.strategy,
               var2.advertiseURI,
               var2.layout,
               var2.filePermissions,
               var2.fileOwner,
               var2.fileGroup,
               var11,
               var7
            );
            if (var8 != null && var12.isAttributeViewEnabled()) {
               var12.defineAttributeView(var5.toPath());
            }

            return var12;
         } catch (IOException var14) {
            RollingFileManager.LOGGER.error("RollingFileManager (" + var1 + ") " + var14, (Throwable)var14);
            return null;
         }
      }
   }
}
