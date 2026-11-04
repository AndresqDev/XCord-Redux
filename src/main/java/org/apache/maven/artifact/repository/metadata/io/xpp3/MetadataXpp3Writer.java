package org.apache.maven.artifact.repository.metadata.io.xpp3;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.Plugin;
import org.apache.maven.artifact.repository.metadata.Snapshot;
import org.apache.maven.artifact.repository.metadata.SnapshotVersion;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.codehaus.plexus.util.xml.pull.MXSerializer;
import org.codehaus.plexus.util.xml.pull.XmlSerializer;

public class MetadataXpp3Writer {
   private static final String NAMESPACE = null;
   private String fileComment = null;

   public void setFileComment(String var1) {
      this.fileComment = var1;
   }

   public void write(Writer var1, Metadata var2) throws IOException {
      MXSerializer var3 = new MXSerializer();
      var3.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-indentation", "  ");
      var3.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator", "\n");
      var3.setOutput(var1);
      var3.startDocument(var2.getModelEncoding(), null);
      this.writeMetadata(var2, "metadata", var3);
      var3.endDocument();
   }

   public void write(OutputStream var1, Metadata var2) throws IOException {
      MXSerializer var3 = new MXSerializer();
      var3.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-indentation", "  ");
      var3.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator", "\n");
      var3.setOutput(var1, var2.getModelEncoding());
      var3.startDocument(var2.getModelEncoding(), null);
      this.writeMetadata(var2, "metadata", var3);
      var3.endDocument();
   }

   private void writeMetadata(Metadata var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getModelVersion() != null) {
         var3.attribute(NAMESPACE, "modelVersion", var1.getModelVersion());
      }

      if (var1.getGroupId() != null) {
         var3.startTag(NAMESPACE, "groupId").text(var1.getGroupId()).endTag(NAMESPACE, "groupId");
      }

      if (var1.getArtifactId() != null) {
         var3.startTag(NAMESPACE, "artifactId").text(var1.getArtifactId()).endTag(NAMESPACE, "artifactId");
      }

      if (var1.getVersioning() != null) {
         this.writeVersioning(var1.getVersioning(), "versioning", var3);
      }

      if (var1.getVersion() != null) {
         var3.startTag(NAMESPACE, "version").text(var1.getVersion()).endTag(NAMESPACE, "version");
      }

      if (var1.getPlugins() != null && var1.getPlugins().size() > 0) {
         var3.startTag(NAMESPACE, "plugins");

         for (Plugin var5 : var1.getPlugins()) {
            this.writePlugin(var5, "plugin", var3);
         }

         var3.endTag(NAMESPACE, "plugins");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writePlugin(Plugin var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getName() != null) {
         var3.startTag(NAMESPACE, "name").text(var1.getName()).endTag(NAMESPACE, "name");
      }

      if (var1.getPrefix() != null) {
         var3.startTag(NAMESPACE, "prefix").text(var1.getPrefix()).endTag(NAMESPACE, "prefix");
      }

      if (var1.getArtifactId() != null) {
         var3.startTag(NAMESPACE, "artifactId").text(var1.getArtifactId()).endTag(NAMESPACE, "artifactId");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeSnapshot(Snapshot var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getTimestamp() != null) {
         var3.startTag(NAMESPACE, "timestamp").text(var1.getTimestamp()).endTag(NAMESPACE, "timestamp");
      }

      if (var1.getBuildNumber() != 0) {
         var3.startTag(NAMESPACE, "buildNumber").text(String.valueOf(var1.getBuildNumber())).endTag(NAMESPACE, "buildNumber");
      }

      if (var1.isLocalCopy()) {
         var3.startTag(NAMESPACE, "localCopy").text(String.valueOf(var1.isLocalCopy())).endTag(NAMESPACE, "localCopy");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeSnapshotVersion(SnapshotVersion var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getClassifier() != null && !var1.getClassifier().equals("")) {
         var3.startTag(NAMESPACE, "classifier").text(var1.getClassifier()).endTag(NAMESPACE, "classifier");
      }

      if (var1.getExtension() != null) {
         var3.startTag(NAMESPACE, "extension").text(var1.getExtension()).endTag(NAMESPACE, "extension");
      }

      if (var1.getVersion() != null) {
         var3.startTag(NAMESPACE, "value").text(var1.getVersion()).endTag(NAMESPACE, "value");
      }

      if (var1.getUpdated() != null) {
         var3.startTag(NAMESPACE, "updated").text(var1.getUpdated()).endTag(NAMESPACE, "updated");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeVersioning(Versioning var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getLatest() != null) {
         var3.startTag(NAMESPACE, "latest").text(var1.getLatest()).endTag(NAMESPACE, "latest");
      }

      if (var1.getRelease() != null) {
         var3.startTag(NAMESPACE, "release").text(var1.getRelease()).endTag(NAMESPACE, "release");
      }

      if (var1.getVersions() != null && var1.getVersions().size() > 0) {
         var3.startTag(NAMESPACE, "versions");

         for (String var5 : var1.getVersions()) {
            var3.startTag(NAMESPACE, "version").text(var5).endTag(NAMESPACE, "version");
         }

         var3.endTag(NAMESPACE, "versions");
      }

      if (var1.getLastUpdated() != null) {
         var3.startTag(NAMESPACE, "lastUpdated").text(var1.getLastUpdated()).endTag(NAMESPACE, "lastUpdated");
      }

      if (var1.getSnapshot() != null) {
         this.writeSnapshot(var1.getSnapshot(), "snapshot", var3);
      }

      if (var1.getSnapshotVersions() != null && var1.getSnapshotVersions().size() > 0) {
         var3.startTag(NAMESPACE, "snapshotVersions");

         for (SnapshotVersion var7 : var1.getSnapshotVersions()) {
            this.writeSnapshotVersion(var7, "snapshotVersion", var3);
         }

         var3.endTag(NAMESPACE, "snapshotVersions");
      }

      var3.endTag(NAMESPACE, var2);
   }
}
