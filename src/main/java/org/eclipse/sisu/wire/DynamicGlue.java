package org.eclipse.sisu.wire;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.sisu.space.asm.ClassWriter;
import org.eclipse.sisu.space.asm.Label;
import org.eclipse.sisu.space.asm.MethodVisitor;
import org.eclipse.sisu.space.asm.Type;
import xcord.Provider;

final class DynamicGlue {
   private static final String PROVIDER_NAME = Type.getInternalName(Provider.class);
   private static final String PROVIDER_DESC = Type.getDescriptor(Provider.class);
   private static final String PROVIDER_HANDLE = "__sisu__";
   private static final String OBJECT_NAME = Type.getInternalName(Object.class);
   private static final String OBJECT_DESC = Type.getDescriptor(Object.class);
   private static final String ILLEGAL_STATE_NAME = Type.getInternalName(IllegalStateException.class);
   private static final Map<String, Method> OBJECT_METHOD_MAP = new HashMap<>();

   static {
      Method[] var3;
      for (Method var0 : var3 = Object.class.getMethods()) {
         if (isWrappable(var0)) {
            OBJECT_METHOD_MAP.put(signatureKey(var0), var0);
         }
      }
   }

   private DynamicGlue() {
   }

   public static byte[] generateProxyClass(String var0, Class<?> var1) {
      String var2 = Type.getInternalName(var1);
      String var3;
      String[] var4;
      if (var1.isInterface()) {
         var3 = OBJECT_NAME;
         var4 = new String[]{var2};
      } else {
         var3 = var2;
         var4 = getInternalNames(var1.getInterfaces());
      }

      ClassWriter var5 = new ClassWriter(1);
      var5.visit(50, 17, var0, null, var3, var4);
      init(var5, var3, var0);

      for (Method var6 : getWrappableMethods(var1)) {
         wrap(var5, var0, var6);
      }

      var5.visitEnd();
      return var5.toByteArray();
   }

   private static void init(ClassWriter var0, String var1, String var2) {
      var0.visitField(18, "__sisu__", PROVIDER_DESC, null, null).visitEnd();
      MethodVisitor var3 = var0.visitMethod(1, "<init>", '(' + PROVIDER_DESC + ")V", null, null);
      var3.visitCode();
      var3.visitVarInsn(25, 0);
      var3.visitInsn(89);
      var3.visitVarInsn(25, 1);
      var3.visitFieldInsn(181, var2, "__sisu__", PROVIDER_DESC);
      var3.visitMethodInsn(183, var1, "<init>", "()V", false);
      var3.visitInsn(177);
      var3.visitMaxs(0, 0);
      var3.visitEnd();
   }

   private static void wrap(ClassWriter var0, String var1, Method var2) {
      String var3 = var2.getName();
      String var4 = Type.getMethodDescriptor(var2);
      String[] var5 = getInternalNames(var2.getExceptionTypes());
      Label var6 = new Label();
      int var7 = var2.getModifiers() & -1313;
      MethodVisitor var8 = var0.visitMethod(var7, var3, var4, null, var5);
      var8.visitCode();
      Class var9 = var2.getDeclaringClass();
      String var10 = Type.getInternalName(var9);
      boolean var11 = OBJECT_METHOD_MAP.containsKey(signatureKey(var2));
      if (!var11 || "toString".equals(var3)) {
         var8.visitVarInsn(25, 0);
         var8.visitFieldInsn(180, var1, "__sisu__", PROVIDER_DESC);
         var8.visitMethodInsn(185, PROVIDER_NAME, "get", "()" + OBJECT_DESC, true);
         var8.visitInsn(89);
         var8.visitJumpInsn(198, var6);
         boolean var12 = var9.isInterface();
         if (!var12 && Object.class != var9) {
            var8.visitTypeInsn(192, var10);
         }

         int var13 = 1;

         Type[] var17;
         for (Type var14 : var17 = Type.getArgumentTypes(var2)) {
            var8.visitVarInsn(var14.getOpcode(21), var13);
            var13 += var14.getSize();
         }

         int var20 = var12 ? 185 : 182;
         var8.visitMethodInsn(var20, var10, var3, var4, var12);
         var8.visitInsn(Type.getReturnType(var2).getOpcode(172));
         var8.visitLabel(var6);
         var8.visitInsn(87);
         if (!var11) {
            var8.visitTypeInsn(187, ILLEGAL_STATE_NAME);
            var8.visitInsn(89);
            var8.visitMethodInsn(183, ILLEGAL_STATE_NAME, "<init>", "()V", false);
            var8.visitInsn(191);
         }
      }

      if (var11) {
         var8.visitVarInsn(25, 0);
         int var18 = 1;

         Type[] var23;
         for (Type var19 : var23 = Type.getArgumentTypes(var2)) {
            var8.visitVarInsn(var19.getOpcode(21), var18);
            var18 += var19.getSize();
         }

         var8.visitMethodInsn(183, var10, var3, var4, false);
         var8.visitInsn(Type.getReturnType(var2).getOpcode(172));
      }

      var8.visitMaxs(0, 0);
      var8.visitEnd();
   }

   private static String[] getInternalNames(Class<?>... var0) {
      String[] var1 = new String[var0.length];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = Type.getInternalName(var0[var2]);
      }

      return var1;
   }

   private static Collection<Method> getWrappableMethods(Class<?> var0) {
      HashMap var1 = new HashMap<>(OBJECT_METHOD_MAP);

      Method[] var5;
      for (Method var2 : var5 = var0.getMethods()) {
         if (isWrappable(var2)) {
            var1.put(signatureKey(var2), var2);
         }
      }

      return var1.values();
   }

   private static boolean isWrappable(Method var0) {
      return (var0.getModifiers() & 24) == 0;
   }

   private static String signatureKey(Method var0) {
      StringBuilder var1 = new StringBuilder(var0.getName());

      Class[] var5;
      for (Class var2 : var5 = var0.getParameterTypes()) {
         var1.append(':').append(var2);
      }

      return var1.toString();
   }
}
