package org.apache.http.conn.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Consts;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.SAFE
)
public final class PublicSuffixMatcherLoader {
   private static volatile PublicSuffixMatcher DEFAULT_INSTANCE;

   private static PublicSuffixMatcher load(InputStream var0) throws IOException {
      List var1 = new PublicSuffixListParser().parseByType(new InputStreamReader(var0, Consts.UTF_8));
      return new PublicSuffixMatcher(var1);
   }

   public static PublicSuffixMatcher load(URL var0) throws IOException {
      Args.notNull(var0, "URL");
      InputStream var1 = var0.openStream();

      PublicSuffixMatcher var2;
      try {
         var2 = load(var1);
      } finally {
         var1.close();
      }

      return var2;
   }

   public static PublicSuffixMatcher load(File var0) throws IOException {
      Args.notNull(var0, "File");
      FileInputStream var1 = new FileInputStream(var0);

      PublicSuffixMatcher var2;
      try {
         var2 = load(var1);
      } finally {
         var1.close();
      }

      return var2;
   }

   public static PublicSuffixMatcher getDefault() {
      if (DEFAULT_INSTANCE == null) {
         synchronized (PublicSuffixMatcherLoader.class) {
            if (DEFAULT_INSTANCE == null) {
               URL var1 = PublicSuffixMatcherLoader.class.getResource("/mozilla/public-suffix-list.txt");
               if (var1 != null) {
                  try {
                     DEFAULT_INSTANCE = load(var1);
                  } catch (IOException var5) {
                     Log var3 = LogFactory.getLog(PublicSuffixMatcherLoader.class);
                     if (var3.isWarnEnabled()) {
                        var3.warn("Failure loading public suffix list from default resource", var5);
                     }
                  }
               } else {
                  DEFAULT_INSTANCE = new PublicSuffixMatcher(DomainType.ICANN, Arrays.asList("com"), null);
               }
            }
         }
      }

      return DEFAULT_INSTANCE;
   }
}
