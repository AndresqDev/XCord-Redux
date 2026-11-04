package org.apache.maven.model.interpolation;

import java.util.HashSet;
import java.util.Set;
import org.apache.maven.model.path.UrlNormalizer;
import org.codehaus.plexus.interpolation.InterpolationPostProcessor;

class UrlNormalizingPostProcessor implements InterpolationPostProcessor {
   private static final Set<String> URL_EXPRESSIONS;
   private UrlNormalizer normalizer;

   UrlNormalizingPostProcessor(UrlNormalizer var1) {
      this.normalizer = var1;
   }

   @Override
   public Object execute(String var1, Object var2) {
      return var2 != null && URL_EXPRESSIONS.contains(var1) ? this.normalizer.normalize(var2.toString()) : null;
   }

   static {
      HashSet var0 = new HashSet();
      var0.add("project.url");
      var0.add("project.scm.url");
      var0.add("project.scm.connection");
      var0.add("project.scm.developerConnection");
      var0.add("project.distributionManagement.site.url");
      URL_EXPRESSIONS = var0;
   }
}
