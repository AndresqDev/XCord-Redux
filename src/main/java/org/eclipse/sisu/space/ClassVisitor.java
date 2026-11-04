package org.eclipse.sisu.space;

public interface ClassVisitor {
   int NON_INSTANTIABLE = 30208;

   void enterClass(int var1, String var2, String var3, String[] var4);

   AnnotationVisitor visitAnnotation(String var1);

   void leaveClass();
}
