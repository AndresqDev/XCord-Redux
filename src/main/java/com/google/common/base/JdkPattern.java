package com.google.common.base;

import com.google.common.annotations.GwtIncompatible;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
final class JdkPattern extends CommonPattern implements Serializable {
   private final Pattern pattern;
   private static final long serialVersionUID = 0L;

   JdkPattern(Pattern var1) {
      this.pattern = Preconditions.checkNotNull(var1);
   }

   @Override
   public CommonMatcher matcher(CharSequence var1) {
      return new JdkPattern.JdkMatcher(this.pattern.matcher(var1));
   }

   @Override
   public String pattern() {
      return this.pattern.pattern();
   }

   @Override
   public int flags() {
      return this.pattern.flags();
   }

   @Override
   public String toString() {
      return this.pattern.toString();
   }

   private static final class JdkMatcher extends CommonMatcher {
      final Matcher matcher;

      JdkMatcher(Matcher var1) {
         this.matcher = Preconditions.checkNotNull(var1);
      }

      @Override
      public boolean matches() {
         return this.matcher.matches();
      }

      @Override
      public boolean find() {
         return this.matcher.find();
      }

      @Override
      public boolean find(int var1) {
         return this.matcher.find(var1);
      }

      @Override
      public String replaceAll(String var1) {
         return this.matcher.replaceAll(var1);
      }

      @Override
      public int end() {
         return this.matcher.end();
      }

      @Override
      public int start() {
         return this.matcher.start();
      }
   }
}
