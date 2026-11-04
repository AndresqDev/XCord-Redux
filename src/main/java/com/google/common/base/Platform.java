package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.ServiceConfigurationError;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
final class Platform {
   private static final Logger logger = Logger.getLogger(Platform.class.getName());
   private static final PatternCompiler patternCompiler = loadPatternCompiler();

   private Platform() {
   }

   static long systemNanoTime() {
      return System.nanoTime();
   }

   static CharMatcher precomputeCharMatcher(CharMatcher var0) {
      return var0.precomputedInternal();
   }

   static <T extends Enum<T>> Optional<T> getEnumIfPresent(Class<T> var0, String var1) {
      WeakReference var2 = Enums.getEnumConstants(var0).get(var1);
      return var2 == null ? Optional.absent() : Optional.of((T)var0.cast(var2.get()));
   }

   static String formatCompact4Digits(double var0) {
      return String.format(Locale.ROOT, "%.4g", var0);
   }

   static boolean stringIsNullOrEmpty(String var0) {
      return var0 == null || var0.isEmpty();
   }

   static String nullToEmpty(String var0) {
      return var0 == null ? "" : var0;
   }

   static String emptyToNull(String var0) {
      return stringIsNullOrEmpty(var0) ? null : var0;
   }

   static CommonPattern compilePattern(String var0) {
      Preconditions.checkNotNull(var0);
      return patternCompiler.compile(var0);
   }

   static boolean patternCompilerIsPcreLike() {
      return patternCompiler.isPcreLike();
   }

   private static PatternCompiler loadPatternCompiler() {
      return new Platform.JdkPatternCompiler();
   }

   private static void logPatternCompilerError(ServiceConfigurationError var0) {
      logger.log(Level.WARNING, "Error loading regex compiler, falling back to next option", (Throwable)var0);
   }

   static void checkGwtRpcEnabled() {
      String var0 = "guava.gwt.emergency_reenable_rpc";
      if (!Boolean.parseBoolean(System.getProperty(var0, "false"))) {
         throw new UnsupportedOperationException(
            Strings.lenientFormat(
               "We are removing GWT-RPC support for Guava types. You can temporarily reenable support by setting the system property %s to true. For more about system properties, see %s. For more about Guava's GWT-RPC support, see %s.",
               var0,
               "https://stackoverflow.com/q/5189914/28465",
               "https://groups.google.com/d/msg/guava-announce/zHZTFg7YF3o/rQNnwdHeEwAJ"
            )
         );
      } else {
         logger.log(
            Level.WARNING,
            "Later in 2020, we will remove GWT-RPC support for Guava types. You are seeing this warning because you are sending a Guava type over GWT-RPC, which will break. You can identify which type by looking at the class name in the attached stack trace.",
            new Throwable()
         );
      }
   }

   private static final class JdkPatternCompiler implements PatternCompiler {
      private JdkPatternCompiler() {
      }

      @Override
      public CommonPattern compile(String var1) {
         return new JdkPattern(Pattern.compile(var1));
      }

      @Override
      public boolean isPcreLike() {
         return true;
      }
   }
}
