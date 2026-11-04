package org.eclipse.sisu.space;

public interface AnnotationVisitor {
   void enterAnnotation();

   void visitElement(String var1, Object var2);

   void leaveAnnotation();
}
