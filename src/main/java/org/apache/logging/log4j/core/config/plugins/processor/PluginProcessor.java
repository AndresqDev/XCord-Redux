package org.apache.logging.log4j.core.config.plugins.processor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleElementVisitor7;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import javax.tools.Diagnostic.Kind;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAliases;

@SupportedAnnotationTypes({"org.apache.logging.log4j.core.config.plugins.*"})
public class PluginProcessor extends AbstractProcessor {
   public static final String PLUGIN_CACHE_FILE = "META-INF/org/apache/logging/log4j/core/config/plugins/Log4j2Plugins.dat";
   private final PluginCache pluginCache = new PluginCache();

   @Override
   public SourceVersion getSupportedSourceVersion() {
      return SourceVersion.latest();
   }

   @Override
   public boolean process(Set<? extends TypeElement> var1, RoundEnvironment var2) {
      Messager var3 = this.processingEnv.getMessager();
      var3.printMessage(Kind.NOTE, "Processing Log4j annotations");

      try {
         Set var4 = var2.getElementsAnnotatedWith(Plugin.class);
         if (var4.isEmpty()) {
            var3.printMessage(Kind.NOTE, "No elements to process");
            return false;
         } else {
            this.collectPlugins(var4);
            this.writeCacheFile(var4.toArray(new Element[var4.size()]));
            var3.printMessage(Kind.NOTE, "Annotations processed");
            return true;
         }
      } catch (Exception var5) {
         var5.printStackTrace();
         this.error(var5.getMessage());
         return false;
      }
   }

   private void error(CharSequence var1) {
      this.processingEnv.getMessager().printMessage(Kind.ERROR, var1);
   }

   private void collectPlugins(Iterable<? extends Element> var1) {
      Elements var2 = this.processingEnv.getElementUtils();
      PluginProcessor.PluginElementVisitor var3 = new PluginProcessor.PluginElementVisitor(var2);
      PluginProcessor.PluginAliasesElementVisitor var4 = new PluginProcessor.PluginAliasesElementVisitor(var2);

      for (Element var6 : var1) {
         Plugin var7 = var6.getAnnotation(Plugin.class);
         if (var7 != null) {
            PluginEntry var8 = var6.accept(var3, var7);
            Map var9 = this.pluginCache.getCategory(var8.getCategory());
            var9.put(var8.getKey(), var8);

            for (PluginEntry var12 : var6.accept(var4, var7)) {
               var9.put(var12.getKey(), var12);
            }
         }
      }
   }

   private void writeCacheFile(Element... var1) throws IOException {
      FileObject var2 = this.processingEnv
         .getFiler()
         .createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/org/apache/logging/log4j/core/config/plugins/Log4j2Plugins.dat", var1);

      try (OutputStream var3 = var2.openOutputStream()) {
         this.pluginCache.writeCache(var3);
      }
   }

   private static class PluginAliasesElementVisitor extends SimpleElementVisitor7<Collection<PluginEntry>, Plugin> {
      private final Elements elements;

      private PluginAliasesElementVisitor(Elements var1) {
         super(Collections.emptyList());
         this.elements = var1;
      }

      public Collection<PluginEntry> visitType(TypeElement var1, Plugin var2) {
         PluginAliases var3 = var1.getAnnotation(PluginAliases.class);
         if (var3 == null) {
            return this.DEFAULT_VALUE;
         } else {
            ArrayList var4 = new ArrayList(var3.value().length);

            for (String var8 : var3.value()) {
               PluginEntry var9 = new PluginEntry();
               var9.setKey(var8.toLowerCase(Locale.US));
               var9.setClassName(this.elements.getBinaryName(var1).toString());
               var9.setName("".equals(var2.elementType()) ? var8 : var2.elementType());
               var9.setPrintable(var2.printObject());
               var9.setDefer(var2.deferChildren());
               var9.setCategory(var2.category());
               var4.add(var9);
            }

            return var4;
         }
      }
   }

   private static class PluginElementVisitor extends SimpleElementVisitor7<PluginEntry, Plugin> {
      private final Elements elements;

      private PluginElementVisitor(Elements var1) {
         this.elements = var1;
      }

      public PluginEntry visitType(TypeElement var1, Plugin var2) {
         Objects.requireNonNull(var2, "Plugin annotation is null.");
         PluginEntry var3 = new PluginEntry();
         var3.setKey(var2.name().toLowerCase(Locale.US));
         var3.setClassName(this.elements.getBinaryName(var1).toString());
         var3.setName("".equals(var2.elementType()) ? var2.name() : var2.elementType());
         var3.setPrintable(var2.printObject());
         var3.setDefer(var2.deferChildren());
         var3.setCategory(var2.category());
         return var3;
      }
   }
}
