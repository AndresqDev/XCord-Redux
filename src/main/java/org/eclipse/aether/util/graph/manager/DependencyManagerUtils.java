package org.eclipse.aether.util.graph.manager;

import java.util.Collection;
import java.util.Map;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.Exclusion;

public final class DependencyManagerUtils {
   public static final String CONFIG_PROP_VERBOSE = "aether.dependencyManager.verbose";
   public static final String NODE_DATA_PREMANAGED_VERSION = "premanaged.version";
   public static final String NODE_DATA_PREMANAGED_SCOPE = "premanaged.scope";
   public static final String NODE_DATA_PREMANAGED_OPTIONAL = "premanaged.optional";
   public static final String NODE_DATA_PREMANAGED_EXCLUSIONS = "premanaged.exclusions";
   public static final String NODE_DATA_PREMANAGED_PROPERTIES = "premanaged.properties";

   public static String getPremanagedVersion(DependencyNode var0) {
      return (var0.getManagedBits() & 1) == 0 ? null : cast(var0.getData().get("premanaged.version"), String.class);
   }

   public static String getPremanagedScope(DependencyNode var0) {
      return (var0.getManagedBits() & 2) == 0 ? null : cast(var0.getData().get("premanaged.scope"), String.class);
   }

   public static Boolean getPremanagedOptional(DependencyNode var0) {
      return (var0.getManagedBits() & 4) == 0 ? null : cast(var0.getData().get("premanaged.optional"), Boolean.class);
   }

   public static Collection<Exclusion> getPremanagedExclusions(DependencyNode var0) {
      return (var0.getManagedBits() & 16) == 0 ? null : cast(var0.getData().get("premanaged.exclusions"), Collection.class);
   }

   public static Map<String, String> getPremanagedProperties(DependencyNode var0) {
      return (var0.getManagedBits() & 8) == 0 ? null : cast(var0.getData().get("premanaged.properties"), Map.class);
   }

   private static <T> T cast(Object var0, Class<T> var1) {
      return (T)(var1.isInstance(var0) ? var1.cast(var0) : null);
   }
}
