package org.apache.maven.model.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;
import java.util.Objects;
import org.apache.maven.model.InputSource;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3ReaderEx;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.xml.XmlStreamReader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import xcord.Named;
import xcord.Singleton;

@Named
@Singleton
public class DefaultModelReader implements ModelReader {
   @Override
   public Model read(File var1, Map<String, ?> var2) throws IOException {
      Objects.requireNonNull(var1, "input cannot be null");
      Model var3 = this.read(new FileInputStream(var1), var2);
      var3.setPomFile(var1);
      return var3;
   }

   @Override
   public Model read(Reader var1, Map<String, ?> var2) throws IOException {
      Objects.requireNonNull(var1, "input cannot be null");
      Reader var3 = var1;
      Throwable var4 = null;

      Model var5;
      try {
         var5 = this.read(var3, this.isStrict(var2), this.getSource(var2));
      } catch (Throwable var14) {
         var4 = var14;
         throw var14;
      } finally {
         if (var1 != null) {
            if (var4 != null) {
               try {
                  var3.close();
               } catch (Throwable var13) {
                  var4.addSuppressed(var13);
               }
            } else {
               var1.close();
            }
         }
      }

      return var5;
   }

   @Override
   public Model read(InputStream var1, Map<String, ?> var2) throws IOException {
      Objects.requireNonNull(var1, "input cannot be null");

      Model var5;
      try (XmlStreamReader var3 = ReaderFactory.newXmlReader(var1)) {
         var5 = this.read(var3, this.isStrict(var2), this.getSource(var2));
      }

      return var5;
   }

   private boolean isStrict(Map<String, ?> var1) {
      Object var2 = var1 != null ? var1.get("org.apache.maven.model.io.isStrict") : null;
      return var2 == null || Boolean.parseBoolean(var2.toString());
   }

   private InputSource getSource(Map<String, ?> var1) {
      Object var2 = var1 != null ? var1.get("org.apache.maven.model.io.inputSource") : null;
      return (InputSource)var2;
   }

   private Model read(Reader var1, boolean var2, InputSource var3) throws IOException {
      try {
         return var3 != null ? new MavenXpp3ReaderEx().read(var1, var2, var3) : new MavenXpp3Reader().read(var1, var2);
      } catch (XmlPullParserException var5) {
         throw new ModelParseException(var5.getMessage(), var5.getLineNumber(), var5.getColumnNumber(), var5);
      }
   }
}
