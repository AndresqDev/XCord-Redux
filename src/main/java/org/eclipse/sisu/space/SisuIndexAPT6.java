package org.eclipse.sisu.space;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.Set;
import javax.annotation.processing.Completion;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import javax.tools.Diagnostic.Kind;
import xcord.Qualifier;

public final class SisuIndexAPT6 extends AbstractSisuIndex implements Processor {
   private static final String QUALIFIERS = "qualifiers";
   private static final String ALL = "all";
   private static final String NONE = "none";
   private static final boolean HAS_QUALIFIER;
   private ProcessingEnvironment environment;
   private String qualifiers;

   static {
      boolean var0;
      try {
         var0 = Qualifier.class.isAnnotation();
      } catch (LinkageError var1) {
         var0 = false;
      }

      HAS_QUALIFIER = var0;
   }

   @Override
   public void init(ProcessingEnvironment var1) {
      this.environment = var1;
      this.qualifiers = var1.getOptions().get("qualifiers");
   }

   @Override
   public boolean process(Set<? extends TypeElement> var1, RoundEnvironment var2) {
      Elements var3 = this.environment.getElementUtils();

      for (TypeElement var4 : var1) {
         if (!"all".equals(this.qualifiers) || hasQualifier(var4)) {
            for (Element var6 : var2.getElementsAnnotatedWith(var4)) {
               if (var6.getKind().isClass()) {
                  this.addClassToIndex("b", var3.getBinaryName((TypeElement)var6));
               }
            }
         }
      }

      if (var2.processingOver()) {
         this.flushIndex();
      }

      return false;
   }

   @Override
   public Iterable<? extends Completion> getCompletions(Element var1, AnnotationMirror var2, ExecutableElement var3, String var4) {
      return Collections.emptySet();
   }

   @Override
   public Set<String> getSupportedAnnotationTypes() {
      if ("all".equalsIgnoreCase(this.qualifiers)) {
         return Collections.singleton("*");
      } else {
         return "none".equalsIgnoreCase(this.qualifiers) ? Collections.emptySet() : Collections.singleton("b");
      }
   }

   @Override
   public Set<String> getSupportedOptions() {
      return Collections.singleton("qualifiers");
   }

   @Override
   public SourceVersion getSupportedSourceVersion() {
      return SourceVersion.latestSupported();
   }

   @Override
   protected void info(String var1) {
      this.environment.getMessager().printMessage(Kind.NOTE, var1);
   }

   @Override
   protected void warn(String var1) {
      this.environment.getMessager().printMessage(Kind.WARNING, var1);
   }

   @Override
   protected Reader getReader(String var1) throws IOException {
      FileObject var2 = this.environment.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", var1);
      return new InputStreamReader(var2.openInputStream(), "UTF-8");
   }

   @Override
   protected Writer getWriter(String var1) throws IOException {
      return this.environment.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", var1).openWriter();
   }

   private static boolean hasQualifier(TypeElement var0) {
      if (HAS_QUALIFIER) {
         return var0.getAnnotation(Qualifier.class) != null;
      } else {
         for (AnnotationMirror var1 : var0.getAnnotationMirrors()) {
            if ("d".equals(var1.getAnnotationType().toString())) {
               return true;
            }
         }

         return false;
      }
   }
}
