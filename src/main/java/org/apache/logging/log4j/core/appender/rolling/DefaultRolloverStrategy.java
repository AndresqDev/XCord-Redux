package org.apache.logging.log4j.core.appender.rolling;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.Map.Entry;
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
   name = "DefaultRolloverStrategy",
   category = "Core",
   printObject = true
)
public class DefaultRolloverStrategy extends AbstractRolloverStrategy {
   private static final int MIN_WINDOW_SIZE = 1;
   private static final int DEFAULT_WINDOW_SIZE = 7;
   private final int maxIndex;
   private final int minIndex;
   private final boolean useMax;
   private final int compressionLevel;
   private final List<Action> customActions;
   private final boolean stopCustomActionsOnError;
   private final PatternProcessor tempCompressedFilePattern;

   @PluginBuilderFactory
   public static DefaultRolloverStrategy.Builder newBuilder() {
      return new DefaultRolloverStrategy.Builder();
   }

   @PluginFactory
   @Deprecated
   public static DefaultRolloverStrategy createStrategy(
      @PluginAttribute("max") String var0,
      @PluginAttribute("min") String var1,
      @PluginAttribute("fileIndex") String var2,
      @PluginAttribute("compressionLevel") String var3,
      @PluginElement("Actions") Action[] var4,
      @PluginAttribute(value = "stopCustomActionsOnError",defaultBoolean = true) boolean var5,
      @PluginConfiguration Configuration var6
   ) {
      return newBuilder()
         .withMin(var1)
         .withMax(var0)
         .withFileIndex(var2)
         .withCompressionLevelStr(var3)
         .withCustomActions(var4)
         .withStopCustomActionsOnError(var5)
         .withConfig(var6)
         .build();
   }

   @Deprecated
   protected DefaultRolloverStrategy(int var1, int var2, boolean var3, int var4, StrSubstitutor var5, Action[] var6, boolean var7) {
      this(var1, var2, var3, var4, var5, var6, var7, null);
   }

   protected DefaultRolloverStrategy(int var1, int var2, boolean var3, int var4, StrSubstitutor var5, Action[] var6, boolean var7, String var8) {
      super(var5);
      this.minIndex = var1;
      this.maxIndex = var2;
      this.useMax = var3;
      this.compressionLevel = var4;
      this.stopCustomActionsOnError = var7;
      this.customActions = var6 == null ? Collections.emptyList() : Arrays.asList(var6);
      this.tempCompressedFilePattern = var8 != null ? new PatternProcessor(var8) : null;
   }

   public int getCompressionLevel() {
      return this.compressionLevel;
   }

   public List<Action> getCustomActions() {
      return this.customActions;
   }

   public int getMaxIndex() {
      return this.maxIndex;
   }

   public int getMinIndex() {
      return this.minIndex;
   }

   public boolean isStopCustomActionsOnError() {
      return this.stopCustomActionsOnError;
   }

   public boolean isUseMax() {
      return this.useMax;
   }

   public PatternProcessor getTempCompressedFilePattern() {
      return this.tempCompressedFilePattern;
   }

   private int purge(int var1, int var2, RollingFileManager var3) {
      return this.useMax ? this.purgeAscending(var1, var2, var3) : this.purgeDescending(var1, var2, var3);
   }

   private int purgeAscending(int var1, int var2, RollingFileManager var3) {
      SortedMap var4 = this.getEligibleFiles(var3);
      int var5 = var2 - var1 + 1;
      boolean var6 = !var4.isEmpty() && (Integer)var4.lastKey() >= this.maxIndex;

      while (var4.size() >= var5) {
         try {
            LOGGER.debug("Eligible files: {}", var4);
            Integer var7 = (Integer)var4.firstKey();
            LOGGER.debug("Deleting {}", ((Path)var4.get(var7)).toFile().getAbsolutePath());
            Files.delete((Path)var4.get(var7));
            var4.remove(var7);
            var6 = true;
         } catch (IOException var16) {
            LOGGER.error("Unable to delete {}, {}", var4.firstKey(), var16.getMessage(), var16);
            break;
         }
      }

      StringBuilder var17 = new StringBuilder();
      if (var6) {
         for (Entry var9 : var4.entrySet()) {
            var17.setLength(0);
            var3.getPatternProcessor().formatFileName(this.strSubstitutor, var17, (Integer)var9.getKey() - 1);
            String var10 = ((Path)var9.getValue()).toFile().getName();
            String var11 = var17.toString();
            int var12 = this.suffixLength(var11);
            if (var12 > 0 && this.suffixLength(var10) == 0) {
               var11 = var11.substring(0, var11.length() - var12);
            }

            FileRenameAction var13 = new FileRenameAction(((Path)var9.getValue()).toFile(), new File(var11), true);

            try {
               LOGGER.debug("DefaultRolloverStrategy.purgeAscending executing {}", var13);
               if (!var13.execute()) {
                  return -1;
               }
            } catch (Exception var15) {
               LOGGER.warn("Exception during purge in RollingFileAppender", (Throwable)var15);
               return -1;
            }
         }
      }

      return var4.size() > 0 ? (var4.lastKey() < var2 ? (Integer)var4.lastKey() + 1 : var2) : var1;
   }

   private int purgeDescending(int var1, int var2, RollingFileManager var3) {
      SortedMap var4 = this.getEligibleFiles(var3, false);
      int var5 = var2 - var1 + 1;
      LOGGER.debug("Eligible files: {}", var4);

      while (var4.size() >= var5) {
         try {
            Integer var6 = (Integer)var4.firstKey();
            Files.delete((Path)var4.get(var6));
            var4.remove(var6);
         } catch (IOException var15) {
            LOGGER.error("Unable to delete {}, {}", var4.firstKey(), var15.getMessage(), var15);
            break;
         }
      }

      StringBuilder var16 = new StringBuilder();

      for (Entry var8 : var4.entrySet()) {
         var16.setLength(0);
         var3.getPatternProcessor().formatFileName(this.strSubstitutor, var16, (Integer)var8.getKey() + 1);
         String var9 = ((Path)var8.getValue()).toFile().getName();
         String var10 = var16.toString();
         int var11 = this.suffixLength(var10);
         if (var11 > 0 && this.suffixLength(var9) == 0) {
            var10 = var10.substring(0, var10.length() - var11);
         }

         FileRenameAction var12 = new FileRenameAction(((Path)var8.getValue()).toFile(), new File(var10), true);

         try {
            LOGGER.debug("DefaultRolloverStrategy.purgeDescending executing {}", var12);
            if (!var12.execute()) {
               return -1;
            }
         } catch (Exception var14) {
            LOGGER.warn("Exception during purge in RollingFileAppender", (Throwable)var14);
            return -1;
         }
      }

      return var1;
   }

   @Override
   public RolloverDescription rollover(RollingFileManager var1) throws SecurityException {
      StringBuilder var3 = new StringBuilder(255);
      int var2;
      if (this.minIndex == Integer.MIN_VALUE) {
         SortedMap var4 = this.getEligibleFiles(var1);
         var2 = var4.size() > 0 ? (Integer)var4.lastKey() + 1 : 1;
         var1.getPatternProcessor().formatFileName(this.strSubstitutor, var3, var2);
      } else {
         if (this.maxIndex < 0) {
            return null;
         }

         long var13 = System.nanoTime();
         var2 = this.purge(this.minIndex, this.maxIndex, var1);
         if (var2 < 0) {
            return null;
         }

         var1.getPatternProcessor().formatFileName(this.strSubstitutor, var3, var2);
         if (LOGGER.isTraceEnabled()) {
            double var6 = (double)TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - var13);
            LOGGER.trace("DefaultRolloverStrategy.purge() took {} milliseconds", var6);
         }
      }

      String var14 = var1.getFileName();
      String var5 = var3.toString();
      String var15 = var5;
      Object var7 = null;
      FileExtension var8 = var1.getFileExtension();
      if (var8 != null) {
         File var9 = new File(var5);
         var5 = var5.substring(0, var5.length() - var8.length());
         if (this.tempCompressedFilePattern != null) {
            var3.delete(0, var3.length());
            this.tempCompressedFilePattern.formatFileName(this.strSubstitutor, var3, var2);
            String var10 = var3.toString();
            File var11 = new File(var10);
            File var12 = var11.getParentFile();
            if (var12 != null) {
               var12.mkdirs();
            }

            var7 = new CompositeAction(
               Arrays.asList(var8.createCompressAction(var5, var10, true, this.compressionLevel), new FileRenameAction(var11, var9, true)), true
            );
         } else {
            var7 = var8.createCompressAction(var5, var15, true, this.compressionLevel);
         }
      }

      if (var14.equals(var5)) {
         LOGGER.warn("Attempt to rename file {} to itself will be ignored", var14);
         return new RolloverDescriptionImpl(var14, false, null, null);
      } else {
         if (var7 != null && var1.isAttributeViewEnabled()) {
            PosixViewAttributeAction var16 = PosixViewAttributeAction.newBuilder()
               .withBasePath(var15)
               .withFollowLinks(false)
               .withMaxDepth(1)
               .withPathConditions(PathCondition.EMPTY_ARRAY)
               .withSubst(this.getStrSubstitutor())
               .withFilePermissions(var1.getFilePermissions())
               .withFileOwner(var1.getFileOwner())
               .withFileGroup(var1.getFileGroup())
               .build();
            var7 = new CompositeAction(Arrays.asList((Action)var7, var16), false);
         }

         FileRenameAction var17 = new FileRenameAction(new File(var14), new File(var5), var1.isRenameEmptyFiles());
         Action var18 = this.merge((Action)var7, this.customActions, this.stopCustomActionsOnError);
         return new RolloverDescriptionImpl(var14, false, var17, var18);
      }
   }

   @Override
   public String toString() {
      return "DefaultRolloverStrategy(min=" + this.minIndex + ", max=" + this.maxIndex + ", useMax=" + this.useMax + ")";
   }

   public static class Builder implements org.apache.logging.log4j.core.util.Builder<DefaultRolloverStrategy> {
      @PluginBuilderAttribute("max")
      private String max;
      @PluginBuilderAttribute("min")
      private String min;
      @PluginBuilderAttribute("fileIndex")
      private String fileIndex;
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

      public DefaultRolloverStrategy build() {
         int var1;
         int var2;
         boolean var3;
         if (this.fileIndex != null && this.fileIndex.equalsIgnoreCase("nomax")) {
            var1 = Integer.MIN_VALUE;
            var2 = Integer.MAX_VALUE;
            var3 = false;
         } else {
            var3 = this.fileIndex == null ? true : this.fileIndex.equalsIgnoreCase("max");
            var1 = 1;
            if (this.min != null) {
               var1 = Integer.parseInt(this.min);
               if (var1 < 1) {
                  AbstractRolloverStrategy.LOGGER.error("Minimum window size too small. Limited to 1");
                  var1 = 1;
               }
            }

            var2 = 7;
            if (this.max != null) {
               var2 = Integer.parseInt(this.max);
               if (var2 < var1) {
                  var2 = var1 < 7 ? 7 : var1;
                  AbstractRolloverStrategy.LOGGER.error("Maximum window size must be greater than the minimum windows size. Set to " + var2);
               }
            }
         }

         int var4 = Integers.parseInt(this.compressionLevelStr, -1);
         StrSubstitutor var5 = this.config != null ? this.config.getStrSubstitutor() : new StrSubstitutor();
         return new DefaultRolloverStrategy(var1, var2, var3, var4, var5, this.customActions, this.stopCustomActionsOnError, this.tempCompressedFilePattern);
      }

      public String getMax() {
         return this.max;
      }

      public DefaultRolloverStrategy.Builder withMax(String var1) {
         this.max = var1;
         return this;
      }

      public String getMin() {
         return this.min;
      }

      public DefaultRolloverStrategy.Builder withMin(String var1) {
         this.min = var1;
         return this;
      }

      public String getFileIndex() {
         return this.fileIndex;
      }

      public DefaultRolloverStrategy.Builder withFileIndex(String var1) {
         this.fileIndex = var1;
         return this;
      }

      public String getCompressionLevelStr() {
         return this.compressionLevelStr;
      }

      public DefaultRolloverStrategy.Builder withCompressionLevelStr(String var1) {
         this.compressionLevelStr = var1;
         return this;
      }

      public Action[] getCustomActions() {
         return this.customActions;
      }

      public DefaultRolloverStrategy.Builder withCustomActions(Action[] var1) {
         this.customActions = var1;
         return this;
      }

      public boolean isStopCustomActionsOnError() {
         return this.stopCustomActionsOnError;
      }

      public DefaultRolloverStrategy.Builder withStopCustomActionsOnError(boolean var1) {
         this.stopCustomActionsOnError = var1;
         return this;
      }

      public String getTempCompressedFilePattern() {
         return this.tempCompressedFilePattern;
      }

      public DefaultRolloverStrategy.Builder withTempCompressedFilePattern(String var1) {
         this.tempCompressedFilePattern = var1;
         return this;
      }

      public Configuration getConfig() {
         return this.config;
      }

      public DefaultRolloverStrategy.Builder withConfig(Configuration var1) {
         this.config = var1;
         return this;
      }
   }
}
