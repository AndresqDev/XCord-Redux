package net.md_5.bungee.api;

import com.google.common.io.BaseEncoding;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import lombok.NonNull;

public class Favicon {
   private static final TypeAdapter<Favicon> FAVICON_TYPE_ADAPTER = new TypeAdapter<Favicon>() {
      public void write(JsonWriter var1, Favicon var2) throws IOException {
         TypeAdapters.STRING.write(var1, var2 == null ? null : var2.getEncoded());
      }

      public Favicon read(JsonReader var1) throws IOException {
         String var2 = (String)TypeAdapters.STRING.read(var1);
         return var2 == null ? null : Favicon.create(var2);
      }
   };
   @NonNull
   private final String encoded;

   public static TypeAdapter<Favicon> getFaviconTypeAdapter() {
      return FAVICON_TYPE_ADAPTER;
   }

   public static Favicon create(BufferedImage var0) {
      if (var0.getWidth() == 64 && var0.getHeight() == 64) {
         byte[] var1;
         try {
            ByteArrayOutputStream var2 = new ByteArrayOutputStream();
            ImageIO.write(var0, "PNG", var2);
            var1 = var2.toByteArray();
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }

         String var4 = "data:image/png;base64," + BaseEncoding.base64().encode(var1);
         if (var4.length() > 32767) {
            throw new IllegalArgumentException("Favicon file too large for server to process");
         } else {
            return new Favicon(var4);
         }
      } else {
         throw new IllegalArgumentException("Server icon must be exactly 64x64 pixels");
      }
   }

   @Deprecated
   public static Favicon create(String var0) {
      return new Favicon(var0);
   }

   private Favicon(@NonNull String var1) {
      if (var1 == null) {
         throw new NullPointerException("encoded is marked non-null but is null");
      } else {
         this.encoded = var1;
      }
   }

   @NonNull
   public String getEncoded() {
      return this.encoded;
   }
}
