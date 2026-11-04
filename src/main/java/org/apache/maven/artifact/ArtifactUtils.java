package org.apache.maven.artifact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import org.apache.commons.lang3.Validate;
import org.apache.maven.artifact.versioning.VersionRange;

public final class ArtifactUtils {
   public static boolean isSnapshot(String var0) {
      if (var0 != null) {
         if (var0.regionMatches(true, var0.length() - "SNAPSHOT".length(), "SNAPSHOT", 0, "SNAPSHOT".length())) {
            return true;
         }

         if (Artifact.VERSION_FILE_PATTERN.matcher(var0).matches()) {
            return true;
         }
      }

      return false;
   }

   public static String toSnapshotVersion(String var0) {
      notBlank(var0, "version can neither be null, empty nor blank");
      int var1 = var0.lastIndexOf(45);
      if (var1 > 0) {
         int var2 = var0.lastIndexOf(45, var1 - 1);
         if (var2 > 0) {
            Matcher var3 = Artifact.VERSION_FILE_PATTERN.matcher(var0);
            if (var3.matches()) {
               return var3.group(1) + "-" + "SNAPSHOT";
            }
         }
      }

      return var0;
   }

   public static String versionlessKey(Artifact var0) {
      return versionlessKey(var0.getGroupId(), var0.getArtifactId());
   }

   public static String versionlessKey(String var0, String var1) {
      notBlank(var0, "groupId can neither be null, empty nor blank");
      notBlank(var1, "artifactId can neither be null, empty nor blank");
      return var0 + ":" + var1;
   }

   public static String key(Artifact var0) {
      return key(var0.getGroupId(), var0.getArtifactId(), var0.getVersion());
   }

   public static String key(String var0, String var1, String var2) {
      notBlank(var0, "groupId can neither be null, empty nor blank");
      notBlank(var1, "artifactId can neither be null, empty nor blank");
      notBlank(var2, "version can neither be null, empty nor blank");
      return var0 + ":" + var1 + ":" + var2;
   }

   private static void notBlank(String var0, String var1) {
      char var2 = var0 != null && var0.length() > 0 ? var0.charAt(0) : 0;
      if ((var2 < '0' || var2 > '9') && (var2 < 'a' || var2 > 'z')) {
         Validate.notBlank(var0, var1);
      }
   }

   public static Map<String, Artifact> artifactMapByVersionlessId(Collection<Artifact> var0) {
      LinkedHashMap var1 = new LinkedHashMap();
      if (var0 != null) {
         for (Artifact var3 : var0) {
            var1.put(versionlessKey(var3), var3);
         }
      }

      return var1;
   }

   public static Artifact copyArtifactSafe(Artifact var0) {
      return var0 != null ? copyArtifact(var0) : null;
   }

   public static Artifact copyArtifact(Artifact var0) {
      VersionRange var1 = var0.getVersionRange();
      if (var1 == null) {
         var1 = VersionRange.createFromVersion(var0.getVersion());
      }

      DefaultArtifact var2 = new DefaultArtifact(
         var0.getGroupId(), var0.getArtifactId(), var1, var0.getScope(), var0.getType(), var0.getClassifier(), var0.getArtifactHandler(), var0.isOptional()
      );
      var2.setRelease(var0.isRelease());
      var2.setResolvedVersion(var0.getVersion());
      var2.setResolved(var0.isResolved());
      var2.setFile(var0.getFile());
      var2.setAvailableVersions(copyList(var0.getAvailableVersions()));
      if (var0.getVersion() != null) {
         var2.setBaseVersion(var0.getBaseVersion());
      }

      var2.setDependencyFilter(var0.getDependencyFilter());
      var2.setDependencyTrail(copyList(var0.getDependencyTrail()));
      var2.setDownloadUrl(var0.getDownloadUrl());
      var2.setRepository(var0.getRepository());
      return var2;
   }

   public static <T extends Collection<Artifact>> T copyArtifacts(Collection<Artifact> var0, T var1) {
      for (Artifact var3 : var0) {
         var1.add(copyArtifact(var3));
      }

      return (T)var1;
   }

   public static <K, T extends Map<K, Artifact>> T copyArtifacts(Map<K, ? extends Artifact> var0, T var1) {
      if (var0 != null) {
         for (Entry var3 : var0.entrySet()) {
            var1.put(var3.getKey(), copyArtifact((Artifact)var3.getValue()));
         }
      }

      return (T)var1;
   }

   private static <T> List<T> copyList(List<T> var0) {
      ArrayList var1 = null;
      if (var0 != null) {
         var1 = new ArrayList();
         if (!var0.isEmpty()) {
            var1.addAll(var0);
         }
      }

      return var1;
   }
}
