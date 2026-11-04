package net.md_5.bungee.config;

import com.google.common.base.Charsets;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

public class YamlConfiguration extends ConfigurationProvider {
   private final ThreadLocal<Yaml> yaml = new ThreadLocal<Yaml>() {
      protected Yaml initialValue() {
         Representer var1 = new Representer() {
            {
               this.representers.put(Configuration.class, new Represent() {
                  public Node representData(Object var1) {
                     return represent(((Configuration)var1).self);
                  }
               });
            }
         };
         DumperOptions var2 = new DumperOptions();
         var2.setDefaultFlowStyle(FlowStyle.BLOCK);
         return new Yaml(new Constructor(), var1, var2);
      }
   };

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
      this.yaml.get().dump(var1.self, var2);
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
      Object var3 = (Map)this.yaml.get().loadAs(var1, LinkedHashMap.class);
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
      Object var3 = (Map)this.yaml.get().loadAs(var1, LinkedHashMap.class);
      if (var3 == null) {
         var3 = new LinkedHashMap();
      }

      return new Configuration((Map<?, ?>)var3, var2);
   }

   @Override
   public Configuration load(String var1) {
      return this.load(var1, null);
   }

   @Override
   public Configuration load(String var1, Configuration var2) {
      Object var3 = (Map)this.yaml.get().loadAs(var1, LinkedHashMap.class);
      if (var3 == null) {
         var3 = new LinkedHashMap();
      }

      return new Configuration((Map<?, ?>)var3, var2);
   }

   YamlConfiguration() {
   }
}
