package org.eclipse.sisu.space;

import com.google.inject.Module;
import java.net.URL;
import org.eclipse.sisu.inject.Logs;
import xcord.Qualifier;

public final class QualifiedTypeVisitor implements ClassVisitor, SpaceVisitor {
   private final QualifierCache qualifierCache = new QualifierCache();
   private final QualifiedTypeListener listener;
   private ClassSpace space;
   private URL location;
   private String source;
   private String clazzName;
   private boolean qualified;

   public QualifiedTypeVisitor(QualifiedTypeListener var1) {
      this.listener = var1;
   }

   public static boolean verify(ClassSpace var0, Class<?>... var1) {
      for (Class var2 : var1) {
         try {
            Class var6 = var0.loadClass(var2.getName());
            if (var6 != var2) {
               Logs.warn("Inconsistent ClassLoader for: {} in: {}", var2, var0);
               Logs.warn("Expected: {} saw: {}", var2.getClassLoader(), var6.getClassLoader());
            }
         } catch (TypeNotPresentException var7) {
            if (var2.isAnnotation()) {
               Logs.trace("Potential problem: {} is not visible from: {}", var2, var0);
            }
         }
      }

      return true;
   }

   @Override
   public void enterSpace(ClassSpace var1) {
      this.space = var1;
      this.source = null;
      if (Logs.TRACE_ENABLED) {
         verify(var1, Qualifier.class, Module.class);
      }
   }

   @Override
   public ClassVisitor visitClass(URL var1) {
      this.location = var1;
      this.clazzName = null;
      this.qualified = false;
      return this;
   }

   @Override
   public void enterClass(int var1, String var2, String var3, String[] var4) {
      if ((var1 & 30208) == 0) {
         this.clazzName = var2;
      }
   }

   @Override
   public AnnotationVisitor visitAnnotation(String var1) {
      if (this.clazzName != null) {
         this.qualified = this.qualified || this.qualifierCache.qualify(this.space, var1);
      }

      return null;
   }

   public void disqualify() {
      this.qualified = false;
   }

   @Override
   public void leaveClass() {
      if (this.qualified) {
         this.listener.hear(this.space.loadClass(this.clazzName.replace('/', '.')), this.findSource());
      }
   }

   @Override
   public void leaveSpace() {
   }

   private String findSource() {
      if (this.location != null) {
         String var1 = this.location.getPath();
         if (this.source == null || !var1.startsWith(this.source)) {
            int var2 = var1.indexOf(this.clazzName);
            this.source = var2 <= 0 ? var1 : var1.substring(0, var2);
         }
      } else if (this.source == null) {
         this.source = this.space.toString();
      }

      return this.source;
   }
}
