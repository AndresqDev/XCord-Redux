package org.eclipse.sisu.wire;

import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.spi.DefaultElementVisitor;
import com.google.inject.spi.Element;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.sisu.inject.Logs;

final class ElementMerger extends DefaultElementVisitor<Void> {
   private final DependencyVerifier verifier = new DependencyVerifier();
   private final Set<Key<?>> localKeys = new HashSet<>();
   private final Binder binder;

   ElementMerger(Binder var1) {
      this.binder = var1;
   }

   public <T> Void visit(Binding<T> var1) {
      Key var2 = var1.getKey();
      if (!this.localKeys.contains(var2)) {
         if (Boolean.TRUE.equals(var1.acceptTargetVisitor(this.verifier))) {
            this.localKeys.add(var2);
            var1.applyTo(this.binder);
         } else {
            Logs.trace("Discard binding: {}", var1, null);
         }
      }

      return null;
   }

   public Void visitOther(Element var1) {
      var1.applyTo(this.binder);
      return null;
   }
}
