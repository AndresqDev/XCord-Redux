package org.eclipse.sisu.space;

import java.security.AccessController;
import java.security.PrivilegedAction;
import org.eclipse.sisu.inject.DeferredClass;
import org.eclipse.sisu.space.asm.ClassWriter;
import org.eclipse.sisu.space.asm.MethodVisitor;

public final class CloningClassSpace extends URLClassSpace {
   private static final String CLONE_MARKER = "$__sisu";
   private int cloneCount;

   public CloningClassSpace(final ClassSpace var1) {
      super(AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
         public ClassLoader run() {
            return new CloningClassSpace.CloningClassLoader(var1);
         }
      }), null);
   }

   public DeferredClass<?> cloneClass(String var1) {
      StringBuilder var2 = new StringBuilder();
      if (var1.startsWith("java")) {
         var2.append('$');
      }

      return this.deferLoadClass(var2.append(var1).append("$__sisu").append(++this.cloneCount).toString());
   }

   public static String originalName(String var0) {
      int var1 = var0.lastIndexOf("$__sisu");
      if (var1 < 0) {
         return var0;
      } else {
         int var2 = var1 + "$__sisu".length();

         for (int var3 = var0.length(); var2 < var3; var2++) {
            char var4 = var0.charAt(var2);
            if (var4 < '0' || var4 > '9') {
               return var0;
            }
         }

         return var0.substring('$' == var0.charAt(0) ? 1 : 0, var1);
      }
   }

   private static final class CloningClassLoader extends ClassLoader {
      private final ClassSpace parent;

      CloningClassLoader(ClassSpace var1) {
         this.parent = var1;
      }

      @Override
      public String toString() {
         return this.parent.toString();
      }

      @Override
      protected synchronized Class<?> loadClass(String var1, boolean var2) throws ClassNotFoundException {
         if (!var1.contains("$__sisu")) {
            try {
               return this.parent.loadClass(var1);
            } catch (TypeNotPresentException var3) {
               throw new ClassNotFoundException(var1);
            }
         } else {
            return super.loadClass(var1, var2);
         }
      }

      @Override
      protected Class<?> findClass(String var1) throws ClassNotFoundException {
         String var2 = var1.replace('.', '/');
         String var3 = CloningClassSpace.originalName(var2);
         if (var3.equals(var2)) {
            throw new ClassNotFoundException(var1);
         } else {
            ClassWriter var4 = new ClassWriter(0);
            var4.visit(50, 1, var2, null, var3, null);
            MethodVisitor var5 = var4.visitMethod(1, "<init>", "()V", null, null);
            var5.visitCode();
            var5.visitVarInsn(25, 0);
            var5.visitMethodInsn(183, var3, "<init>", "()V", false);
            var5.visitInsn(177);
            var5.visitMaxs(1, 1);
            var5.visitEnd();
            var4.visitEnd();
            byte[] var6 = var4.toByteArray();
            return this.defineClass(var1, var6, 0, var6.length);
         }
      }
   }
}
