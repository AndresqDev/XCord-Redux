package org.apache.maven.repository.internal;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Writer;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.WriterFactory;
import org.codehaus.plexus.util.xml.XmlStreamReader;
import org.codehaus.plexus.util.xml.XmlStreamWriter;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.metadata.AbstractMetadata;
import org.eclipse.aether.metadata.MergeableMetadata;

abstract class MavenMetadata extends AbstractMetadata implements MergeableMetadata {
   static final String MAVEN_METADATA_XML = "maven-metadata.xml";
   protected Metadata metadata;
   private final File file;
   protected final Date timestamp;
   private boolean merged;

   protected MavenMetadata(Metadata var1, File var2, Date var3) {
      this.metadata = var1;
      this.file = var2;
      this.timestamp = var3;
   }

   @Override
   public String getType() {
      return "maven-metadata.xml";
   }

   @Override
   public File getFile() {
      return this.file;
   }

   @Override
   public void merge(File var1, File var2) throws RepositoryException {
      Metadata var3 = read(var1);
      this.merge(var3);
      this.write(var2, this.metadata);
      this.merged = true;
   }

   @Override
   public boolean isMerged() {
      return this.merged;
   }

   protected abstract void merge(Metadata var1);

   static Metadata read(File var0) throws RepositoryException {
      if (var0.length() <= 0L) {
         return new Metadata();
      } else {
         try (XmlStreamReader var1 = ReaderFactory.newXmlReader(var0)) {
            return new MetadataXpp3Reader().read(var1, false);
         } catch (IOException var16) {
            throw new RepositoryException("Could not read metadata " + var0 + ": " + var16.getMessage(), var16);
         } catch (XmlPullParserException var17) {
            throw new RepositoryException("Could not parse metadata " + var0 + ": " + var17.getMessage(), var17);
         }
      }
   }

   private void write(File var1, Metadata var2) throws RepositoryException {
      var1.getParentFile().mkdirs();

      try (XmlStreamWriter var3 = WriterFactory.newXmlWriter(var1)) {
         new MetadataXpp3Writer().write(var3, var2);
      } catch (IOException var16) {
         throw new RepositoryException("Could not write metadata " + var1 + ": " + var16.getMessage(), var16);
      }
   }

   @Override
   public Map<String, String> getProperties() {
      return Collections.emptyMap();
   }

   @Override
   public org.eclipse.aether.metadata.Metadata setProperties(Map<String, String> var1) {
      return this;
   }
}
