package org.apache.logging.log4j.core.appender.rolling;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.appender.rolling.action.Action;
import org.apache.logging.log4j.core.appender.rolling.action.CompositeAction;
import org.apache.logging.log4j.core.appender.rolling.action.FileRenameAction;
import org.apache.logging.log4j.core.appender.rolling.action.PathCondition;
import org.apache.logging.log4j.core.appender.rolling.action.PosixViewAttributeAction;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.core.util.Integers;

@Plugin(
   name = "DirectWriteRolloverStrategy",
   category = "Core",
   printObject = true
)
public class DirectWriteRolloverStrategy extends AbstractRolloverStrategy implements DirectFileRolloverStrategy {
   private static final int DEFAULT_MAX_FILES = 7;
   private final int maxFiles;
   private final int compressionLevel;
   private final List<Action> customActions;
   private final boolean stopCustomActionsOnError;
   private volatile String currentFileName;
   private int nextIndex = -1;
   private final PatternProcessor tempCompressedFilePattern;
   private volatile boolean usePrevTime = false;

   @PluginBuilderFactory
   public static DirectWriteRolloverStrategy.Builder newBuilder() {
      return new DirectWriteRolloverStrategy.Builder();
   }

   @Deprecated
   @PluginFactory
   public static DirectWriteRolloverStrategy createStrategy(
      @PluginAttribute("maxFiles") String var0,
      @PluginAttribute("compressionLevel") String var1,
      @PluginElement("Actions") Action[] var2,
      @PluginAttribute(value = "stopCustomActionsOnError",defaultBoolean = true) boolean var3,
      @PluginConfiguration Configuration var4
   ) {
      return newBuilder().withMaxFiles(var0).withCompressionLevelStr(var1).withCustomActions(var2).withStopCustomActionsOnError(var3).withConfig(var4).build();
   }

   @Deprecated
   protected DirectWriteRolloverStrategy(int var1, int var2, StrSubstitutor var3, Action[] var4, boolean var5) {
      this(var1, var2, var3, var4, var5, null);
   }

   protected DirectWriteRolloverStrategy(int var1, int var2, StrSubstitutor var3, Action[] var4, boolean var5, String var6) {
      super(var3);
      this.maxFiles = var1;
      this.compressionLevel = var2;
      this.stopCustomActionsOnError = var5;
      this.customActions = var4 == null ? Collections.emptyList() : Arrays.asList(var4);
      this.tempCompressedFilePattern = var6 != null ? new PatternProcessor(var6) : null;
   }

   public int getCompressionLevel() {
      return this.compressionLevel;
   }

   public List<Action> getCustomActions() {
      return this.customActions;
   }

   public int getMaxFiles() {
      return this.maxFiles;
   }

   public boolean isStopCustomActionsOnError() {
      return this.stopCustomActionsOnError;
   }

   public PatternProcessor getTempCompressedFilePattern() {
      return this.tempCompressedFilePattern;
   }

   private int purge(RollingFileManager var1) {
      SortedMap var2 = this.getEligibleFiles(var1);
      LOGGER.debug("Found {} eligible files, max is  {}", var2.size(), this.maxFiles);

      while (var2.size() >= this.maxFiles) {
         try {
            Integer var3 = (Integer)var2.firstKey();
            Files.delete((Path)var2.get(var3));
            var2.remove(var3);
         } catch (IOException var4) {
            LOGGER.error("Unable to delete {}", var2.firstKey(), var4);
            break;
         }
      }

      return var2.size() > 0 ? (Integer)var2.lastKey() : 1;
   }

   @Override
   public String getCurrentFileName(RollingFileManager var1) {
      if (this.currentFileName == null) {
         SortedMap var2 = this.getEligibleFiles(var1);
         int var3 = var2.size() > 0 ? (this.nextIndex > 0 ? this.nextIndex : var2.size()) : 1;
         StringBuilder var4 = new StringBuilder(255);
         var1.getPatternProcessor().formatFileName(this.strSubstitutor, var4, true, var3);
         int var5 = this.suffixLength(var4.toString());
         String var6 = var5 > 0 ? var4.substring(0, var4.length() - var5) : var4.toString();
         this.currentFileName = var6;
      }

      return this.currentFileName;
   }

   @Override
   public void clearCurrentFileName() {
      this.currentFileName = null;
   }

   @Override
   public RolloverDescription rollover(RollingFileManager var1) throws SecurityException {
      LOGGER.debug("Rolling " + this.currentFileName);
      if (this.maxFiles < 0) {
         return null;
      } else {
         long var2 = System.nanoTime();
         int var4 = this.purge(var1);
         if (LOGGER.isTraceEnabled()) {
            double var5 = (double)TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - var2);
            LOGGER.trace("DirectWriteRolloverStrategy.purge() took {} milliseconds", var5);
         }

         Object var13 = null;
         String var6 = this.getCurrentFileName(var1);
         String var7 = var6;
         this.currentFileName = null;
         this.nextIndex = var4 + 1;
         FileExtension var8 = var1.getFileExtension();
         if (var8 != null) {
            var7 = var6 + var8.getExtension();
            if (this.tempCompressedFilePattern != null) {
               StringBuilder var9 = new StringBuilder();
               this.tempCompressedFilePattern.formatFileName(this.strSubstitutor, var9, var4);
               String var10 = var9.toString();
               File var11 = new File(var10);
               File var12 = var11.getParentFile();
               if (var12 != null) {
                  var12.mkdirs();
               }

               var13 = new CompositeAction(
                  Arrays.asList(var8.createCompressAction(var6, var10, true, this.compressionLevel), new FileRenameAction(var11, new File(var7), true)), true
               );
            } else {
               var13 = var8.createCompressAction(var6, var7, true, this.compressionLevel);
            }
         }

         if (var13 != null && var1.isAttributeViewEnabled()) {
            PosixViewAttributeAction var14 = PosixViewAttributeAction.newBuilder()
               .withBasePath(var7)
               .withFollowLinks(false)
               .withMaxDepth(1)
               .withPathConditions(PathCondition.EMPTY_ARRAY)
               .withSubst(this.getStrSubstitutor())
               .withFilePermissions(var1.getFilePermissions())
               .withFileOwner(var1.getFileOwner())
               .withFileGroup(var1.getFileGroup())
               .build();
            var13 = new CompositeAction(Arrays.asList((Action)var13, var14), false);
         }

         Action var15 = this.merge((Action)var13, this.customActions, this.stopCustomActionsOnError);
         return new RolloverDescriptionImpl(var6, false, null, var15);
      }
   }

   @Override
   public String toString() {
      return "DirectWriteRolloverStrategy(maxFiles=" + this.maxFiles + ')';
   }

   public static class Builder implements org.apache.logging.log4j.core.util.Builder<DirectWriteRolloverStrategy> {
      @PluginBuilderAttribute("maxFiles")
      private String maxFiles;
      @PluginBuilderAttribute("compressionLevel")
      private String compressionLevelStr;
      @PluginElement("Actions")
      private Action[] customActions;
      @PluginBuilderAttribute("stopCustomActionsOnError")
      private boolean stopCustomActionsOnError = true;
      @PluginBuilderAttribute("tempCompressedFilePattern")
      private String tempCompressedFilePattern;
      @PluginConfiguration
      private Configuration config;

      public DirectWriteRolloverStrategy build() {
         int var1 = Integer.MAX_VALUE;
         if (this.maxFiles != null) {
            var1 = Integer.parseInt(this.maxFiles);
            if (var1 < 0) {
               var1 = Integer.MAX_VALUE;
            } else if (var1 < 2) {
               AbstractRolloverStrategy.LOGGER.error("Maximum files too small. Limited to 7");
               var1 = 7;
            }
         }

         int var2 = Integers.parseInt(this.compressionLevelStr, -1);
         return new DirectWriteRolloverStrategy(
            var1, var2, this.config.getStrSubstitutor(), this.customActions, this.stopCustomActionsOnError, this.tempCompressedFilePattern
         );
      }

      public String getMaxFiles() {
         return this.maxFiles;
      }

      public DirectWriteRolloverStrategy.Builder withMaxFiles(String var1) {
         this.maxFiles = var1;
         return this;
      }

      public String getCompressionLevelStr() {
         return this.compressionLevelStr;
      }

      public DirectWriteRolloverStrategy.Builder withCompressionLevelStr(String var1) {
         this.compressionLevelStr = var1;
         return this;
      }

      public Action[] getCustomActions() {
         return this.customActions;
      }

      public DirectWriteRolloverStrategy.Builder withCustomActions(Action[] var1) {
         this.customActions = var1;
         return this;
      }

      public boolean isStopCustomActionsOnError() {
         return this.stopCustomActionsOnError;
      }

      public DirectWriteRolloverStrategy.Builder withStopCustomActionsOnError(boolean var1) {
         this.stopCustomActionsOnError = var1;
         return this;
      }

      public String getTempCompressedFilePattern() {
         return this.tempCompressedFilePattern;
      }

      public DirectWriteRolloverStrategy.Builder withTempCompressedFilePattern(String var1) {
         this.tempCompressedFilePattern = var1;
         return this;
      }

      public Configuration getConfig() {
         return this.config;
      }

      public DirectWriteRolloverStrategy.Builder withConfig(Configuration var1) {
         this.config = var1;
         return this;
      }
   }
}
