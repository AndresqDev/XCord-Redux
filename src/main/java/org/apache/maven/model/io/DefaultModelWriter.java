package org.apache.maven.model.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Objects;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.WriterFactory;
import xcord.Named;
import xcord.Singleton;

@Named
@Singleton
public class DefaultModelWriter implements ModelWriter {
   @Override
   public void write(File var1, Map<String, Object> var2, Model var3) throws IOException {
      Objects.requireNonNull(var1, "output cannot be null");
      Objects.requireNonNull(var3, "model cannot be null");
      var1.getParentFile().mkdirs();
      this.write(WriterFactory.newXmlWriter(var1), var2, var3);
   }

   @Override
   public void write(Writer var1, Map<String, Object> var2, Model var3) throws IOException {
      Objects.requireNonNull(var1, "output cannot be null");
      Objects.requireNonNull(var3, "model cannot be null");
      Writer var4 = var1;
      Throwable var5 = null;

      try {
         new MavenXpp3Writer().write(var4, var3);
      } catch (Throwable var14) {
         var5 = var14;
         throw var14;
      } finally {
         if (var1 != null) {
            if (var5 != null) {
               try {
                  var4.close();
               } catch (Throwable var13) {
                  var5.addSuppressed(var13);
               }
            } else {
               var1.close();
            }
         }
      }
   }

   @Override
   public void write(OutputStream var1, Map<String, Object> var2, Model var3) throws IOException {
      Objects.requireNonNull(var1, "output cannot be null");
      Objects.requireNonNull(var3, "model cannot be null");
      String var4 = var3.getModelEncoding();
      if (var4 == null || var4.length() <= 0) {
         var4 = "UTF-8";
      }

      try (OutputStreamWriter var5 = new OutputStreamWriter(var1, var4)) {
         this.write(var5, var2, var3);
      }
   }
}
