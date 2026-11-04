package net.md_5.bungee.config;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonConfiguration extends ConfigurationProvider {
   private final Gson json = new GsonBuilder()
      .serializeNulls()
      .setPrettyPrinting()
      .registerTypeAdapter(Configuration.class, new JsonSerializer<Configuration>() {
         public JsonElement serialize(Configuration var1, Type var2, JsonSerializationContext var3) {
            return var3.serialize(var1.self);
         }
      })
      .create();

   @Override
   public void save(Configuration var1, File var2) throws IOException {
      OutputStreamWriter var3 = new OutputStreamWriter(new FileOutputStream(var2), Charsets.UTF_8);

      try {
         this.save(var1, var3);
      } catch (Throwable var7) {
         try {
            var3.close();
         } catch (Throwable var6) {
            var7.addSuppressed(var6);
         }

         throw var7;
      }

      var3.close();
   }

   @Override
   public void save(Configuration var1, Writer var2) {
      this.json.toJson(var1.self, var2);
   }

   @Override
   public Configuration load(File var1) throws IOException {
      return this.load(var1, null);
   }

   @Override
   public Configuration load(File var1, Configuration var2) throws IOException {
      FileInputStream var3 = new FileInputStream(var1);

      Configuration var4;
      try {
         var4 = this.load(var3, var2);
      } catch (Throwable var7) {
         try {
            var3.close();
         } catch (Throwable var6) {
            var7.addSuppressed(var6);
         }

         throw var7;
      }

      var3.close();
      return var4;
   }

   @Override
   public Configuration load(Reader var1) {
      return this.load(var1, null);
   }

   @Override
   public Configuration load(Reader var1, Configuration var2) {
      Object var3 = (Map)this.json.fromJson(var1, LinkedHashMap.class);
      if (var3 == null) {
         var3 = new LinkedHashMap();
      }

      return new Configuration((Map<?, ?>)var3, var2);
   }

   @Override
   public Configuration load(InputStream var1) {
      return this.load(var1, null);
   }

   @Override
   public Configuration load(InputStream var1, Configuration var2) {
      return this.load(new InputStreamReader(var1, Charsets.UTF_8), var2);
   }

   @Override
   public Configuration load(String var1) {
      return this.load(var1, null);
   }

   @Override
   public Configuration load(String var1, Configuration var2) {
      Object var3 = (Map)this.json.fromJson(var1, LinkedHashMap.class);
      if (var3 == null) {
         var3 = new LinkedHashMap();
      }

      return new Configuration((Map<?, ?>)var3, var2);
   }

   JsonConfiguration() {
   }
}
