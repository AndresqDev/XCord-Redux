package org.apache.logging.log4j.core.appender.rolling.action;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;

public interface PathCondition {
   PathCondition[] EMPTY_ARRAY = new PathCondition[0];

   static PathCondition[] copy(PathCondition... var0) {
      return var0 != null && var0.length != 0 ? Arrays.copyOf(var0, var0.length) : EMPTY_ARRAY;
   }

   void beforeFileTreeWalk();

   boolean accept(Path var1, Path var2, BasicFileAttributes var3);
}
