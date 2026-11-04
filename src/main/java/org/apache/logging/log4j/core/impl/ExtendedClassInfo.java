package org.apache.logging.log4j.core.impl;

import java.io.Serializable;
import java.util.Objects;
import org.apache.logging.log4j.core.pattern.PlainTextRenderer;
import org.apache.logging.log4j.core.pattern.TextRenderer;

public final class ExtendedClassInfo implements Serializable {
   private static final long serialVersionUID = 1L;
   private final boolean exact;
   private final String location;
   private final String version;

   public ExtendedClassInfo(boolean var1, String var2, String var3) {
      this.exact = var1;
      this.location = var2;
      this.version = var3;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (!(var1 instanceof ExtendedClassInfo)) {
         return false;
      } else {
         ExtendedClassInfo var2 = (ExtendedClassInfo)var1;
         if (this.exact != var2.exact) {
            return false;
         } else {
            return !Objects.equals(this.location, var2.location) ? false : Objects.equals(this.version, var2.version);
         }
      }
   }

   public boolean getExact() {
      return this.exact;
   }

   public String getLocation() {
      return this.location;
   }

   public String getVersion() {
      return this.version;
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.exact, this.location, this.version);
   }

   public void renderOn(StringBuilder var1, TextRenderer var2) {
      if (!this.exact) {
         var2.render("~", var1, "ExtraClassInfo.Inexact");
      }

      var2.render("[", var1, "ExtraClassInfo.Container");
      var2.render(this.location, var1, "ExtraClassInfo.Location");
      var2.render(":", var1, "ExtraClassInfo.ContainerSeparator");
      var2.render(this.version, var1, "ExtraClassInfo.Version");
      var2.render("]", var1, "ExtraClassInfo.Container");
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      this.renderOn(var1, PlainTextRenderer.getInstance());
      return var1.toString();
   }
}
