package org.apache.logging.log4j.core.appender.rolling;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LoggingException;
import org.apache.logging.log4j.core.appender.rolling.action.Action;
import org.apache.logging.log4j.core.appender.rolling.action.CompositeAction;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.core.pattern.NotANumber;
import org.apache.logging.log4j.status.StatusLogger;

public abstract class AbstractRolloverStrategy implements RolloverStrategy {
   protected static final Logger LOGGER = StatusLogger.getLogger();
   public static final Pattern PATTERN_COUNTER = Pattern.compile(".*%((?<ZEROPAD>0)?(?<PADDING>\\d+))?i.*");
   protected final StrSubstitutor strSubstitutor;

   protected AbstractRolloverStrategy(StrSubstitutor var1) {
      this.strSubstitutor = var1;
   }

   public StrSubstitutor getStrSubstitutor() {
      return this.strSubstitutor;
   }

   protected Action merge(Action var1, List<Action> var2, boolean var3) {
      if (var2.isEmpty()) {
         return var1;
      } else if (var1 == null) {
         return new CompositeAction(var2, var3);
      } else {
         ArrayList var4 = new ArrayList();
         var4.add(var1);
         var4.addAll(var2);
         return new CompositeAction(var4, var3);
      }
   }

   protected int suffixLength(String var1) {
      for (FileExtension var5 : FileExtension.values()) {
         if (var5.isExtensionFor(var1)) {
            return var5.length();
         }
      }

      return 0;
   }

   protected SortedMap<Integer, Path> getEligibleFiles(RollingFileManager var1) {
      return this.getEligibleFiles(var1, true);
   }

   protected SortedMap<Integer, Path> getEligibleFiles(RollingFileManager var1, boolean var2) {
      StringBuilder var3 = new StringBuilder();
      String var4 = var1.getPatternProcessor().getPattern();
      var1.getPatternProcessor().formatFileName(this.strSubstitutor, var3, NotANumber.NAN);
      String var5 = var1.isDirectWrite() ? "" : var1.getFileName();
      return this.getEligibleFiles(var5, var3.toString(), var4, var2);
   }

   protected SortedMap<Integer, Path> getEligibleFiles(String var1, String var2) {
      return this.getEligibleFiles("", var1, var2, true);
   }

   @Deprecated
   protected SortedMap<Integer, Path> getEligibleFiles(String var1, String var2, boolean var3) {
      return this.getEligibleFiles("", var1, var2, var3);
   }

   protected SortedMap<Integer, Path> getEligibleFiles(String var1, String var2, String var3, boolean var4) {
      TreeMap var5 = new TreeMap();
      File var6 = new File(var2);
      File var7 = var6.getParentFile();
      if (var7 == null) {
         var7 = new File(".");
      } else {
         var7.mkdirs();
      }

      if (!PATTERN_COUNTER.matcher(var3).matches()) {
         return var5;
      } else {
         Path var8 = var7.toPath();
         String var9 = var6.getName();
         int var10 = this.suffixLength(var9);
         if (var10 > 0) {
            var9 = Pattern.quote(var9.substring(0, var9.length() - var10)) + ".*";
         } else {
            var9 = Pattern.quote(var9);
         }

         String var11 = var9.replaceFirst("0?\\u0000", "\\\\E(0?\\\\d+)\\\\Q");
         Pattern var12 = Pattern.compile(var11);
         Path var13 = var1.length() > 0 ? new File(var1).toPath() : null;
         LOGGER.debug("Current file: {}", var1);

         try (DirectoryStream var14 = Files.newDirectoryStream(var8)) {
            for (Path var17 : var14) {
               Matcher var18 = var12.matcher(var17.toFile().getName());
               if (var18.matches() && !var17.equals(var13)) {
                  try {
                     Integer var19 = Integer.parseInt(var18.group(1));
                     var5.put(var19, var17);
                  } catch (NumberFormatException var29) {
                     LOGGER.debug("Ignoring file {} which matches pattern but the index is invalid.", var17.toFile().getName());
                  }
               }
            }
         } catch (IOException var32) {
            throw new LoggingException("Error reading folder " + var8 + " " + var32.getMessage(), var32);
         }

         return (SortedMap<Integer, Path>)(var4 ? var5 : var5.descendingMap());
      }
   }
}
