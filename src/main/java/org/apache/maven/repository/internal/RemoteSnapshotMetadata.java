package org.apache.maven.repository.internal;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.Snapshot;
import org.apache.maven.artifact.repository.metadata.SnapshotVersion;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.eclipse.aether.artifact.Artifact;

final class RemoteSnapshotMetadata extends MavenSnapshotMetadata {
   public static final String DEFAULT_SNAPSHOT_TIMESTAMP_FORMAT = "yyyyMMdd.HHmmss";
   public static final TimeZone DEFAULT_SNAPSHOT_TIME_ZONE = TimeZone.getTimeZone("Etc/UTC");
   private final Map<String, SnapshotVersion> versions = new LinkedHashMap<>();

   RemoteSnapshotMetadata(Artifact var1, boolean var2, Date var3) {
      super(createRepositoryMetadata(var1, var2), null, var2, var3);
   }

   private RemoteSnapshotMetadata(Metadata var1, File var2, boolean var3, Date var4) {
      super(var1, var2, var3, var4);
   }

   public MavenMetadata setFile(File var1) {
      return new RemoteSnapshotMetadata(this.metadata, var1, this.legacyFormat, this.timestamp);
   }

   public String getExpandedVersion(Artifact var1) {
      String var2 = this.getKey(var1.getClassifier(), var1.getExtension());
      return this.versions.get(var2).getVersion();
   }

   @Override
   protected void merge(Metadata var1) {
      Snapshot var2;
      String var3;
      if (this.metadata.getVersioning() == null) {
         SimpleDateFormat var4 = new SimpleDateFormat("yyyyMMdd.HHmmss");
         var4.setCalendar(new GregorianCalendar());
         var4.setTimeZone(DEFAULT_SNAPSHOT_TIME_ZONE);
         var2 = new Snapshot();
         var2.setBuildNumber(getBuildNumber(var1) + 1);
         var2.setTimestamp(var4.format(this.timestamp));
         Versioning var5 = new Versioning();
         var5.setSnapshot(var2);
         var5.setLastUpdatedTimestamp(this.timestamp);
         var3 = var5.getLastUpdated();
         this.metadata.setVersioning(var5);
      } else {
         var2 = this.metadata.getVersioning().getSnapshot();
         var3 = this.metadata.getVersioning().getLastUpdated();
      }

      for (Artifact var10 : this.artifacts) {
         String var6 = var10.getVersion();
         if (var6.endsWith("SNAPSHOT")) {
            String var7 = var2.getTimestamp() + '-' + var2.getBuildNumber();
            var6 = var6.substring(0, var6.length() - "SNAPSHOT".length()) + var7;
         }

         SnapshotVersion var13 = new SnapshotVersion();
         var13.setClassifier(var10.getClassifier());
         var13.setExtension(var10.getExtension());
         var13.setVersion(var6);
         var13.setUpdated(var3);
         this.versions.put(this.getKey(var13.getClassifier(), var13.getExtension()), var13);
      }

      this.artifacts.clear();
      Versioning var9 = var1.getVersioning();
      if (var9 != null) {
         for (SnapshotVersion var12 : var9.getSnapshotVersions()) {
            String var14 = this.getKey(var12.getClassifier(), var12.getExtension());
            if (!this.versions.containsKey(var14)) {
               this.versions.put(var14, var12);
            }
         }
      }

      if (!this.legacyFormat) {
         this.metadata.getVersioning().setSnapshotVersions(new ArrayList<>(this.versions.values()));
      }
   }

   private static int getBuildNumber(Metadata var0) {
      int var1 = 0;
      Versioning var2 = var0.getVersioning();
      if (var2 != null) {
         Snapshot var3 = var2.getSnapshot();
         if (var3 != null && var3.getBuildNumber() > 0) {
            var1 = var3.getBuildNumber();
         }
      }

      return var1;
   }
}
