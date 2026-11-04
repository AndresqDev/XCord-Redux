package org.eclipse.sisu.space;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import org.eclipse.sisu.inject.Logs;

public class SisuIndex extends AbstractSisuIndex implements ClassVisitor, SpaceVisitor {
   private final QualifierCache qualifierCache = new QualifierCache();
   private final File targetDirectory;
   private ClassSpace space;
   private String clazzName;

   public SisuIndex(File var1) {
      this.targetDirectory = var1;
   }

   public static void main(String[] var0) {
      ArrayList var1 = new ArrayList(var0.length);

      for (String var2 : var0) {
         try {
            var1.add(new File(var2).toURI().toURL());
         } catch (MalformedURLException var7) {
            Logs.warn("Bad classpath element: {}", var2, var7);
         }
      }

      ClassLoader var8 = SisuIndex.class.getClassLoader();
      URL[] var9 = var1.toArray(new URL[var1.size()]);
      Object var10 = var9.length > 0 ? URLClassLoader.newInstance(var9, var8) : var8;
      new SisuIndex(new File(".")).index(new URLClassSpace((ClassLoader)var10));
   }

   public final void index(ClassSpace var1) {
      try {
         new SpaceScanner(var1).accept(this);
      } finally {
         this.flushIndex();
      }
   }

   @Override
   public final void enterSpace(ClassSpace var1) {
      this.space = var1;
   }

   @Override
   public final ClassVisitor visitClass(URL var1) {
      return this;
   }

   @Override
   public final void enterClass(int var1, String var2, String var3, String[] var4) {
      if ((var1 & 30208) == 0) {
         this.clazzName = var2;
      }
   }

   @Override
   public final AnnotationVisitor visitAnnotation(String var1) {
      if (this.clazzName != null && this.qualifierCache.qualify(this.space, var1)) {
         this.addClassToIndex("b", this.clazzName.replace('/', '.'));
      }

      return null;
   }

   @Override
   public final void leaveClass() {
      this.clazzName = null;
   }

   @Override
   public final void leaveSpace() {
      this.space = null;
   }

   @Override
   protected void info(String var1) {
      System.out.println("[INFO] " + var1);
   }

   @Override
   protected void warn(String var1) {
      System.out.println("[WARN] " + var1);
   }

   @Override
   protected Reader getReader(String var1) throws IOException {
      return new InputStreamReader(new FileInputStream(new File(this.targetDirectory, var1)), "UTF-8");
   }

   @Override
   protected Writer getWriter(String var1) throws IOException {
      File var2 = new File(this.targetDirectory, var1);
      File var3 = var2.getParentFile();
      if (!var3.isDirectory() && !var3.mkdirs()) {
         throw new IOException("Error creating: " + var3);
      } else {
         return new OutputStreamWriter(new FileOutputStream(var2), "UTF-8");
      }
   }
}
