package org.apache.logging.log4j.core.lookup;

import java.util.Locale;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.util.Strings;

@Plugin(
   name = "java",
   category = "Lookup"
)
public class JavaLookup extends AbstractLookup {
   private final SystemPropertiesLookup spLookup = new SystemPropertiesLookup();

   public String getHardware() {
      return "processors: "
         + Runtime.getRuntime().availableProcessors()
         + ", architecture: "
         + this.getSystemProperty("os.arch")
         + this.getSystemProperty("-", "sun.arch.data.model")
         + this.getSystemProperty(", instruction sets: ", "sun.cpu.isalist");
   }

   public String getLocale() {
      return "default locale: " + Locale.getDefault() + ", platform encoding: " + this.getSystemProperty("file.encoding");
   }

   public String getOperatingSystem() {
      return this.getSystemProperty("os.name")
         + " "
         + this.getSystemProperty("os.version")
         + this.getSystemProperty(" ", "sun.os.patch.level")
         + ", architecture: "
         + this.getSystemProperty("os.arch")
         + this.getSystemProperty("-", "sun.arch.data.model");
   }

   public String getRuntime() {
      return this.getSystemProperty("java.runtime.name")
         + " (build "
         + this.getSystemProperty("java.runtime.version")
         + ") from "
         + this.getSystemProperty("java.vendor");
   }

   private String getSystemProperty(String var1) {
      return this.spLookup.lookup(var1);
   }

   private String getSystemProperty(String var1, String var2) {
      String var3 = this.getSystemProperty(var2);
      return Strings.isEmpty(var3) ? "" : var1 + var3;
   }

   public String getVirtualMachine() {
      return this.getSystemProperty("java.vm.name")
         + " (build "
         + this.getSystemProperty("java.vm.version")
         + ", "
         + this.getSystemProperty("java.vm.info")
         + ")";
   }

   @Override
   public String lookup(LogEvent var1, String var2) {
      switch (var2) {
         case "version":
            return "Java version " + this.getSystemProperty("java.version");
         case "runtime":
            return this.getRuntime();
         case "vm":
            return this.getVirtualMachine();
         case "os":
            return this.getOperatingSystem();
         case "hw":
            return this.getHardware();
         case "locale":
            return this.getLocale();
         default:
            throw new IllegalArgumentException(var2);
      }
   }
}
