package org.apache.logging.log4j.core.appender;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.core.util.FileUtils;

public class FileManager extends OutputStreamManager {
   private static final FileManager.FileManagerFactory FACTORY = new FileManager.FileManagerFactory();
   private final boolean isAppend;
   private final boolean createOnDemand;
   private final boolean isLocking;
   private final String advertiseURI;
   private final int bufferSize;
   private final Set<PosixFilePermission> filePermissions;
   private final String fileOwner;
   private final String fileGroup;
   private final boolean attributeViewEnabled;

   @Deprecated
   protected FileManager(String var1, OutputStream var2, boolean var3, boolean var4, String var5, Layout<? extends Serializable> var6, int var7, boolean var8) {
      this(var1, var2, var3, var4, var5, var6, var8, ByteBuffer.wrap(new byte[var7]));
   }

   @Deprecated
   protected FileManager(
      String var1, OutputStream var2, boolean var3, boolean var4, String var5, Layout<? extends Serializable> var6, boolean var7, ByteBuffer var8
   ) {
      super(var2, var1, var6, var7, var8);
      this.isAppend = var3;
      this.createOnDemand = false;
      this.isLocking = var4;
      this.advertiseURI = var5;
      this.bufferSize = var8.capacity();
      this.filePermissions = null;
      this.fileOwner = null;
      this.fileGroup = null;
      this.attributeViewEnabled = false;
   }

   @Deprecated
   protected FileManager(
      LoggerContext var1,
      String var2,
      OutputStream var3,
      boolean var4,
      boolean var5,
      boolean var6,
      String var7,
      Layout<? extends Serializable> var8,
      boolean var9,
      ByteBuffer var10
   ) {
      super(var1, var3, var2, var6, var8, var9, var10);
      this.isAppend = var4;
      this.createOnDemand = var6;
      this.isLocking = var5;
      this.advertiseURI = var7;
      this.bufferSize = var10.capacity();
      this.filePermissions = null;
      this.fileOwner = null;
      this.fileGroup = null;
      this.attributeViewEnabled = false;
   }

   protected FileManager(
      LoggerContext var1,
      String var2,
      OutputStream var3,
      boolean var4,
      boolean var5,
      boolean var6,
      String var7,
      Layout<? extends Serializable> var8,
      String var9,
      String var10,
      String var11,
      boolean var12,
      ByteBuffer var13
   ) {
      super(var1, var3, var2, var6, var8, var12, var13);
      this.isAppend = var4;
      this.createOnDemand = var6;
      this.isLocking = var5;
      this.advertiseURI = var7;
      this.bufferSize = var13.capacity();
      Set var14 = FileSystems.getDefault().supportedFileAttributeViews();
      if (var14.contains("posix")) {
         this.filePermissions = var9 != null ? PosixFilePermissions.fromString(var9) : null;
         this.fileGroup = var11;
      } else {
         this.filePermissions = null;
         this.fileGroup = null;
         if (var9 != null) {
            LOGGER.warn("Posix file attribute permissions defined but it is not supported by this files system.");
         }

         if (var11 != null) {
            LOGGER.warn("Posix file attribute group defined but it is not supported by this files system.");
         }
      }

      if (var14.contains("owner")) {
         this.fileOwner = var10;
      } else {
         this.fileOwner = null;
         if (var10 != null) {
            LOGGER.warn("Owner file attribute defined but it is not supported by this files system.");
         }
      }

      this.attributeViewEnabled = this.filePermissions != null || this.fileOwner != null || this.fileGroup != null;
   }

   public static FileManager getFileManager(
      String var0,
      boolean var1,
      boolean var2,
      boolean var3,
      boolean var4,
      String var5,
      Layout<? extends Serializable> var6,
      int var7,
      String var8,
      String var9,
      String var10,
      Configuration var11
   ) {
      if (var2 && var3) {
         var2 = false;
      }

      return narrow(
         FileManager.class, getManager(var0, new FileManager.FactoryData(var1, var2, var3, var7, var4, var5, var6, var8, var9, var10, var11), FACTORY)
      );
   }

   @Override
   protected OutputStream createOutputStream() throws IOException {
      String var1 = this.getFileName();
      LOGGER.debug("Now writing to {} at {}", var1, new Date());
      File var2 = new File(var1);
      this.createParentDir(var2);
      FileOutputStream var3 = new FileOutputStream(var2, this.isAppend);
      if (var2.exists() && var2.length() == 0L) {
         try {
            FileTime var4 = FileTime.fromMillis(System.currentTimeMillis());
            Files.setAttribute(var2.toPath(), "creationTime", var4);
         } catch (Exception var5) {
            LOGGER.warn("Unable to set current file time for {}", var1);
         }

         this.writeHeader(var3);
      }

      this.defineAttributeView(Paths.get(var1));
      return var3;
   }

   protected void createParentDir(File var1) {
   }

   protected void defineAttributeView(Path var1) {
      if (this.attributeViewEnabled) {
         try {
            var1.toFile().createNewFile();
            FileUtils.defineFilePosixAttributeView(var1, this.filePermissions, this.fileOwner, this.fileGroup);
         } catch (Exception var3) {
            LOGGER.error("Could not define attribute view on path \"{}\" got {}", var1, var3.getMessage(), var3);
         }
      }
   }

   @Override
   protected synchronized void write(byte[] var1, int var2, int var3, boolean var4) {
      if (this.isLocking) {
         try {
            FileChannel var5 = ((FileOutputStream)this.getOutputStream()).getChannel();

            try (FileLock var6 = var5.lock(0L, Long.MAX_VALUE, false)) {
               super.write(var1, var2, var3, var4);
            }
         } catch (IOException var19) {
            throw new AppenderLoggingException("Unable to obtain lock on " + this.getName(), var19);
         }
      } else {
         super.write(var1, var2, var3, var4);
      }
   }

   @Override
   protected synchronized void writeToDestination(byte[] var1, int var2, int var3) {
      if (this.isLocking) {
         try {
            FileChannel var4 = ((FileOutputStream)this.getOutputStream()).getChannel();

            try (FileLock var5 = var4.lock(0L, Long.MAX_VALUE, false)) {
               super.writeToDestination(var1, var2, var3);
            }
         } catch (IOException var18) {
            throw new AppenderLoggingException("Unable to obtain lock on " + this.getName(), var18);
         }
      } else {
         super.writeToDestination(var1, var2, var3);
      }
   }

   public String getFileName() {
      return this.getName();
   }

   public boolean isAppend() {
      return this.isAppend;
   }

   public boolean isCreateOnDemand() {
      return this.createOnDemand;
   }

   public boolean isLocking() {
      return this.isLocking;
   }

   public int getBufferSize() {
      return this.bufferSize;
   }

   public Set<PosixFilePermission> getFilePermissions() {
      return this.filePermissions;
   }

   public String getFileOwner() {
      return this.fileOwner;
   }

   public String getFileGroup() {
      return this.fileGroup;
   }

   public boolean isAttributeViewEnabled() {
      return this.attributeViewEnabled;
   }

   @Override
   public Map<String, String> getContentFormat() {
      HashMap var1 = new HashMap<>(super.getContentFormat());
      var1.put("fileURI", this.advertiseURI);
      return var1;
   }

   private static class FactoryData extends ConfigurationFactoryData {
      private final boolean append;
      private final boolean locking;
      private final boolean bufferedIo;
      private final int bufferSize;
      private final boolean createOnDemand;
      private final String advertiseURI;
      private final Layout<? extends Serializable> layout;
      private final String filePermissions;
      private final String fileOwner;
      private final String fileGroup;

      public FactoryData(
         boolean var1,
         boolean var2,
         boolean var3,
         int var4,
         boolean var5,
         String var6,
         Layout<? extends Serializable> var7,
         String var8,
         String var9,
         String var10,
         Configuration var11
      ) {
         super(var11);
         this.append = var1;
         this.locking = var2;
         this.bufferedIo = var3;
         this.bufferSize = var4;
         this.createOnDemand = var5;
         this.advertiseURI = var6;
         this.layout = var7;
         this.filePermissions = var8;
         this.fileOwner = var9;
         this.fileGroup = var10;
      }
   }

   private static class FileManagerFactory implements ManagerFactory<FileManager, FileManager.FactoryData> {
      private FileManagerFactory() {
      }

      public FileManager createManager(String var1, FileManager.FactoryData var2) {
         File var3 = new File(var1);

         try {
            FileUtils.makeParentDirs(var3);
            boolean var4 = !var2.append || !var3.exists();
            int var5 = var2.bufferedIo ? var2.bufferSize : Constants.ENCODER_BYTE_BUFFER_SIZE;
            ByteBuffer var6 = ByteBuffer.wrap(new byte[var5]);
            FileOutputStream var7 = var2.createOnDemand ? null : new FileOutputStream(var3, var2.append);
            FileManager var8 = new FileManager(
               var2.getLoggerContext(),
               var1,
               var7,
               var2.append,
               var2.locking,
               var2.createOnDemand,
               var2.advertiseURI,
               var2.layout,
               var2.filePermissions,
               var2.fileOwner,
               var2.fileGroup,
               var4,
               var6
            );
            if (var7 != null && var8.attributeViewEnabled) {
               var8.defineAttributeView(var3.toPath());
            }

            return var8;
         } catch (IOException var9) {
            AbstractManager.LOGGER.error("FileManager (" + var1 + ") " + var9, (Throwable)var9);
            return null;
         }
      }
   }
}
