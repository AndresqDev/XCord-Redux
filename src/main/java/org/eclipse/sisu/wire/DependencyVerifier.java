package org.eclipse.sisu.wire;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.DefaultBindingTargetVisitor;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.LinkedKeyBinding;
import com.google.inject.spi.UntargettedBinding;
import org.eclipse.sisu.inject.Logs;
import org.eclipse.sisu.inject.TypeArguments;

final class DependencyVerifier extends DefaultBindingTargetVisitor<Object, Boolean> {
   public Boolean visit(UntargettedBinding<?> var1) {
      return verifyImplementation(var1.getKey().getTypeLiteral());
   }

   public Boolean visit(LinkedKeyBinding<?> var1) {
      Key var2 = var1.getLinkedKey();
      return var2.getAnnotationType() == null ? verifyImplementation(var2.getTypeLiteral()) : Boolean.TRUE;
   }

   public Boolean visitOther(Binding<?> var1) {
      return Boolean.TRUE;
   }

   private static Boolean verifyImplementation(TypeLiteral<?> var0) {
      if (TypeArguments.isConcrete(var0) && !var0.toString().startsWith("java")) {
         try {
            InjectionPoint.forInstanceMethodsAndFields(var0);
            InjectionPoint.forConstructorOf(var0);
         } catch (RuntimeException var2) {
            Logs.trace("Potential problem: {}", var0, var2);
            return Boolean.FALSE;
         } catch (LinkageError var3) {
            Logs.trace("Potential problem: {}", var0, var3);
            return Boolean.FALSE;
         }
      }

      return Boolean.TRUE;
   }
}
