package org.eclipse.sisu.space;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Enumeration;
import org.eclipse.sisu.inject.Logs;
import org.eclipse.sisu.space.asm.ClassReader;
import org.eclipse.sisu.space.asm.Type;

public final class SpaceScanner {
   private static final int ASM_FLAGS = 7;
   static final ClassFinder DEFAULT_FINDER = new DefaultClassFinder();
   private final ClassSpace space;
   private final ClassFinder finder;

   public SpaceScanner(ClassSpace var1, ClassFinder var2) {
      this.space = var1;
      this.finder = var2;
   }

   public SpaceScanner(ClassSpace var1) {
      this(var1, DEFAULT_FINDER);
   }

   public void accept(SpaceVisitor var1) {
      var1.enterSpace(this.space);
      Enumeration var2 = this.finder.findClasses(this.space);

      while (var2.hasMoreElements()) {
         URL var3 = (URL)var2.nextElement();
         ClassVisitor var4 = var1.visitClass(var3);
         if (var4 != null) {
            accept(var4, var3);
         }
      }

      var1.leaveSpace();
   }

   public static void accept(ClassVisitor var0, URL var1) {
      if (var1 != null) {
         try {
            InputStream var2 = Streams.open(var1);

            try {
               new ClassReader(var2).accept(adapt(var0), 7);
            } finally {
               var2.close();
            }
         } catch (ArrayIndexOutOfBoundsException var8) {
         } catch (Exception var9) {
            Logs.trace("Problem scanning: {}", var1, var9);
         }
      }
   }

   public static String jvmDescriptor(Class<? extends Annotation> var0) {
      return 'L' + var0.getName().replace('.', '/') + ';';
   }

   private static org.eclipse.sisu.space.asm.ClassVisitor adapt(final ClassVisitor var0) {
      return var0 == null ? null : new org.eclipse.sisu.space.asm.ClassVisitor(327680) {
         @Override
         public void visit(int var1, int var2, String var3, String var4, String var5, String[] var6) {
            var0.enterClass(var2, var3, var5, var6);
         }

         @Override
         public org.eclipse.sisu.space.asm.AnnotationVisitor visitAnnotation(String var1, boolean var2) {
            final AnnotationVisitor var3 = var0.visitAnnotation(var1);
            return var3 == null ? null : new org.eclipse.sisu.space.asm.AnnotationVisitor(327680) {
               {
                  var3.enterAnnotation();
               }

               @Override
               public void visit(String var1, Object var2) {
                  var3.visitElement(var1, var2 instanceof Type ? ((Type)var2).getClassName() : var2);
               }

               @Override
               public void visitEnd() {
                  var3.leaveAnnotation();
               }
            };
         }

         @Override
         public void visitEnd() {
            var0.leaveClass();
         }
      };
   }
}
