package org.apache.maven.model.building;

import java.io.File;
import org.apache.maven.model.Model;

public class ModelProblemUtils {
   static String toSourceHint(Model var0) {
      if (var0 == null) {
         return "";
      } else {
         StringBuilder var1 = new StringBuilder(128);
         var1.append(toId(var0));
         File var2 = var0.getPomFile();
         if (var2 != null) {
            var1.append(" (").append(var2).append(')');
         }

         return var1.toString();
      }
   }

   static String toPath(Model var0) {
      String var1 = "";
      if (var0 != null) {
         File var2 = var0.getPomFile();
         if (var2 != null) {
            var1 = var2.getAbsolutePath();
         }
      }

      return var1;
   }

   static String toId(Model var0) {
      if (var0 == null) {
         return "";
      } else {
         String var1 = var0.getGroupId();
         if (var1 == null && var0.getParent() != null) {
            var1 = var0.getParent().getGroupId();
         }

         String var2 = var0.getArtifactId();
         String var3 = var0.getVersion();
         if (var3 == null && var0.getParent() != null) {
            var3 = var0.getParent().getVersion();
         }

         if (var3 == null) {
            var3 = "[unknown-version]";
         }

         return toId(var1, var2, var3);
      }
   }

   static String toId(String var0, String var1, String var2) {
      StringBuilder var3 = new StringBuilder(128);
      var3.append(var0 != null && var0.length() > 0 ? var0 : "[unknown-group-id]");
      var3.append(':');
      var3.append(var1 != null && var1.length() > 0 ? var1 : "[unknown-artifact-id]");
      var3.append(':');
      var3.append(var2 != null && var2.length() > 0 ? var2 : "[unknown-version]");
      return var3.toString();
   }

   public static String formatLocation(ModelProblem var0, String var1) {
      StringBuilder var2 = new StringBuilder(256);
      if (!var0.getModelId().equals(var1)) {
         var2.append(var0.getModelId());
         if (var0.getSource().length() > 0) {
            if (var2.length() > 0) {
               var2.append(", ");
            }

            var2.append(var0.getSource());
         }
      }

      if (var0.getLineNumber() > 0) {
         if (var2.length() > 0) {
            var2.append(", ");
         }

         var2.append("line ").append(var0.getLineNumber());
      }

      if (var0.getColumnNumber() > 0) {
         if (var2.length() > 0) {
            var2.append(", ");
         }

         var2.append("column ").append(var0.getColumnNumber());
      }

      return var2.toString();
   }
}
