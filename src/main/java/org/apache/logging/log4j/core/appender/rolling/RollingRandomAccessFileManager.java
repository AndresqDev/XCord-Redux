package org.apache.logging.log4j.core.appender.rolling;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.appender.ConfigurationFactoryData;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.util.FileUtils;
import org.apache.logging.log4j.core.util.NullOutputStream;

public class RollingRandomAccessFileManager extends RollingFileManager {
   public static final int DEFAULT_BUFFER_SIZE = 262144;
   private static final RollingRandomAccessFileManager.RollingRandomAccessFileManagerFactory FACTORY = new RollingRandomAccessFileManager.RollingRandomAccessFileManagerFactory(
      
   );
   private RandomAccessFile randomAccessFile;

   @Deprecated
   public RollingRandomAccessFileManager(
      LoggerContext var1,
      RandomAccessFile var2,
      String var3,
      String var4,
      OutputStream var5,
      boolean var6,
      boolean var7,
      int var8,
      long var9,
      long var11,
      TriggeringPolicy var13,
      RolloverStrategy var14,
      String var15,
      Layout<? extends Serializable> var16,
      boolean var17
   ) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var11, var13, var14, var15, var16, null, null, null, var17);
   }

   public RollingRandomAccessFileManager(
      LoggerContext var1,
      RandomAccessFile var2,
      String var3,
      String var4,
      OutputStream var5,
      boolean var6,
      boolean var7,
      int var8,
      long var9,
      long var11,
      TriggeringPolicy var13,
      RolloverStrategy var14,
      String var15,
      Layout<? extends Serializable> var16,
      String var17,
      String var18,
      String var19,
      boolean var20
   ) {
      super(var1, var3, var4, var5, var6, false, var9, var11, var13, var14, var15, var16, var17, var18, var19, var20, ByteBuffer.wrap(new byte[var8]));
      this.randomAccessFile = var2;
      this.writeHeader();
   }

   private void writeHeader() {
      if (this.layout != null) {
         byte[] var1 = this.layout.getHeader();
         if (var1 != null) {
            try {
               if (this.randomAccessFile != null && this.randomAccessFile.length() == 0L) {
                  this.randomAccessFile.write(var1, 0, var1.length);
               }
            } catch (IOException var3) {
               this.logError("Unable to write header", var3);
            }
         }
      }
   }

   public static RollingRandomAccessFileManager getRollingRandomAccessFileManager(
      String var0,
      String var1,
      boolean var2,
      boolean var3,
      int var4,
      TriggeringPolicy var5,
      RolloverStrategy var6,
      String var7,
      Layout<? extends Serializable> var8,
      String var9,
      String var10,
      String var11,
      Configuration var12
   ) {
      if (var6 instanceof DirectWriteRolloverStrategy && var0 != null) {
         LOGGER.error("The fileName attribute must not be specified with the DirectWriteRolloverStrategy");
         return null;
      } else {
         String var13 = var0 == null ? var1 : var0;
         return narrow(
            RollingRandomAccessFileManager.class,
            getManager(
               var13, new RollingRandomAccessFileManager.FactoryData(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12), FACTORY
            )
         );
      }
   }

   @Deprecated
   public Boolean isEndOfBatch() {
      return Boolean.FALSE;
   }

   @Deprecated
   public void setEndOfBatch(boolean var1) {
   }

   @Override
   protected synchronized void write(byte[] var1, int var2, int var3, boolean var4) {
      super.write(var1, var2, var3, var4);
   }

   @Override
   protected synchronized void writeToDestination(byte[] var1, int var2, int var3) {
      try {
         if (this.randomAccessFile == null) {
            String var4 = this.getFileName();
            File var7 = new File(var4);
            FileUtils.makeParentDirs(var7);
            this.createFileAfterRollover(var4);
         }

         this.randomAccessFile.write(var1, var2, var3);
         this.size += (long)var3;
      } catch (IOException var6) {
         String var5 = "Error writing to RandomAccessFile " + this.getName();
         throw new AppenderLoggingException(var5, var6);
      }
   }

   @Override
   protected void createFileAfterRollover() throws IOException {
      this.createFileAfterRollover(this.getFileName());
   }

   private void createFileAfterRollover(String var1) throws IOException {
      this.randomAccessFile = new RandomAccessFile(var1, "rw");
      if (this.isAttributeViewEnabled()) {
         this.defineAttributeView(Paths.get(var1));
      }

      if (this.isAppend()) {
         this.randomAccessFile.seek(this.randomAccessFile.length());
      }

      this.writeHeader();
   }

   @Override
   public synchronized void flush() {
      this.flushBuffer(this.byteBuffer);
   }

   @Override
   public synchronized boolean closeOutputStream() {
      this.flush();
      if (this.randomAccessFile != null) {
         try {
            this.randomAccessFile.close();
            return true;
         } catch (IOException var2) {
            this.logError("Unable to close RandomAccessFile", var2);
            return false;
         }
      } else {
         return true;
      }
   }

   @Override
   public int getBufferSize() {
      return this.byteBuffer.capacity();
   }

   @Override
   public void updateData(Object var1) {
      RollingRandomAccessFileManager.FactoryData var2 = (RollingRandomAccessFileManager.FactoryData)var1;
      this.setRolloverStrategy(var2.getRolloverStrategy());
      this.setPatternProcessor(new PatternProcessor(var2.getPattern(), this.getPatternProcessor()));
      this.setTriggeringPolicy(var2.getTriggeringPolicy());
   }

   private static class FactoryData extends ConfigurationFactoryData {
      private final String fileName;
      private final String pattern;
      private final boolean append;
      private final boolean immediateFlush;
      private final int bufferSize;
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
         int var5,
         TriggeringPolicy var6,
         RolloverStrategy var7,
         String var8,
         Layout<? extends Serializable> var9,
         String var10,
         String var11,
         String var12,
         Configuration var13
      ) {
         super(var13);
         this.fileName = var1;
         this.pattern = var2;
         this.append = var3;
         this.immediateFlush = var4;
         this.bufferSize = var5;
         this.policy = var6;
         this.strategy = var7;
         this.advertiseURI = var8;
         this.layout = var9;
         this.filePermissions = var10;
         this.fileOwner = var11;
         this.fileGroup = var12;
      }

      public String getPattern() {
         return this.pattern;
      }

      public TriggeringPolicy getTriggeringPolicy() {
         return this.policy;
      }

      public RolloverStrategy getRolloverStrategy() {
         return this.strategy;
      }
   }

   private static class RollingRandomAccessFileManagerFactory
      implements ManagerFactory<RollingRandomAccessFileManager, RollingRandomAccessFileManager.FactoryData> {
      private RollingRandomAccessFileManagerFactory() {
      }

      public RollingRandomAccessFileManager createManager(String var1, RollingRandomAccessFileManager.FactoryData var2) {
         File var3 = null;
         long var4 = 0L;
         long var6 = System.currentTimeMillis();
         RandomAccessFile var8 = null;
         if (var2.fileName != null) {
            var3 = new File(var1);
            if (!var2.append) {
               var3.delete();
            }

            var4 = var2.append ? var3.length() : 0L;
            if (var3.exists()) {
               var6 = var3.lastModified();
            }

            try {
               FileUtils.makeParentDirs(var3);
               var8 = new RandomAccessFile(var1, "rw");
               if (var2.append) {
                  long var9 = var8.length();
                  RollingRandomAccessFileManager.LOGGER.trace("RandomAccessFile {} seek to {}", var1, var9);
                  var8.seek(var9);
               } else {
                  RollingRandomAccessFileManager.LOGGER.trace("RandomAccessFile {} set length to 0", var1);
                  var8.setLength(0L);
               }
            } catch (IOException var12) {
               RollingRandomAccessFileManager.LOGGER.error("Cannot access RandomAccessFile " + var12, (Throwable)var12);
               if (var8 != null) {
                  try {
                     var8.close();
                  } catch (IOException var11) {
                     RollingRandomAccessFileManager.LOGGER.error("Cannot close RandomAccessFile {}", var1, var11);
                  }
               }

               return null;
            }
         }

         boolean var13 = !var2.append || var3 == null || !var3.exists();
         RollingRandomAccessFileManager var10 = new RollingRandomAccessFileManager(
            var2.getLoggerContext(),
            var8,
            var1,
            var2.pattern,
            NullOutputStream.getInstance(),
            var2.append,
            var2.immediateFlush,
            var2.bufferSize,
            var4,
            var6,
            var2.policy,
            var2.strategy,
            var2.advertiseURI,
            var2.layout,
            var2.filePermissions,
            var2.fileOwner,
            var2.fileGroup,
            var13
         );
         if (var10.isAttributeViewEnabled()) {
            var10.defineAttributeView(var3.toPath());
         }

         return var10;
      }
   }
}
