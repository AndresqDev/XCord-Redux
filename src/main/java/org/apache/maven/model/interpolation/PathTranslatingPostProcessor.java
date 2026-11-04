package org.apache.maven.model.interpolation;

import java.io.File;
import java.util.Collection;
import java.util.List;
import org.apache.maven.model.path.PathTranslator;
import org.codehaus.plexus.interpolation.InterpolationPostProcessor;
import org.codehaus.plexus.interpolation.util.ValueSourceUtils;

class PathTranslatingPostProcessor implements InterpolationPostProcessor {
   private final Collection<String> unprefixedPathKeys;
   private final File projectDir;
   private final PathTranslator pathTranslator;
   private final List<String> expressionPrefixes;

   PathTranslatingPostProcessor(List<String> var1, Collection<String> var2, File var3, PathTranslator var4) {
      this.expressionPrefixes = var1;
      this.unprefixedPathKeys = var2;
      this.projectDir = var3;
      this.pathTranslator = var4;
   }

   @Override
   public Object execute(String var1, Object var2) {
      if (var2 != null) {
         var1 = ValueSourceUtils.trimPrefix(var1, this.expressionPrefixes, true);
         if (this.unprefixedPathKeys.contains(var1)) {
            return this.pathTranslator.alignToBaseDirectory(String.valueOf(var2), this.projectDir);
         }
      }

      return null;
   }
}
