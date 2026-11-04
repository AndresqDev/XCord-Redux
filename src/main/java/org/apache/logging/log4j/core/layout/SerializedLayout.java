package org.apache.logging.log4j.core.layout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Deprecated
@Plugin(
   name = "SerializedLayout",
   category = "Core",
   elementType = "layout",
   printObject = true
)
public final class SerializedLayout extends AbstractLayout<LogEvent> {
   private static byte[] serializedHeader;

   private SerializedLayout() {
      super(null, null, null);
      LOGGER.warn(
         "SerializedLayout is deprecated due to the inherent security weakness in Java Serialization, see https://www.owasp.org/index.php/Deserialization_of_untrusted_data Consider using another layout, e.g. JsonLayout"
      );
   }

   @Override
   public byte[] toByteArray(LogEvent var1) {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream();

      try (SerializedLayout.PrivateObjectOutputStream var3 = new SerializedLayout.PrivateObjectOutputStream(var2)) {
         var3.writeObject(var1);
         var3.reset();
      } catch (IOException var16) {
         LOGGER.error("Serialization of LogEvent failed.", (Throwable)var16);
      }

      return var2.toByteArray();
   }

   public LogEvent toSerializable(LogEvent var1) {
      return var1;
   }

   @Deprecated
   @PluginFactory
   public static SerializedLayout createLayout() {
      return new SerializedLayout();
   }

   @Override
   public byte[] getHeader() {
      return serializedHeader;
   }

   @Override
   public String getContentType() {
      return "application/octet-stream";
   }

   static {
      ByteArrayOutputStream var0 = new ByteArrayOutputStream();

      try {
         new ObjectOutputStream(var0).close();
         serializedHeader = var0.toByteArray();
      } catch (Exception var2) {
         LOGGER.error("Unable to generate Object stream header", (Throwable)var2);
      }
   }

   private class PrivateObjectOutputStream extends ObjectOutputStream {
      public PrivateObjectOutputStream(OutputStream var2) throws IOException {
         super(var2);
      }

      @Override
      protected void writeStreamHeader() {
      }
   }
}
