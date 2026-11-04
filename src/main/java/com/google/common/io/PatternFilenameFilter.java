package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

@ElementTypesAreNonnullByDefault
@Beta
@GwtIncompatible
public final class PatternFilenameFilter implements FilenameFilter {
   private final Pattern pattern;

   public PatternFilenameFilter(String var1) {
      this(Pattern.compile(var1));
   }

   public PatternFilenameFilter(Pattern var1) {
      this.pattern = Preconditions.checkNotNull(var1);
   }

   @Override
   public boolean accept(File var1, String var2) {
      return this.pattern.matcher(var2).matches();
   }
}
