package org.eclipse.sisu.wire;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.spi.Element;
import com.google.inject.spi.Elements;
import java.util.Arrays;

public final class MergedModule implements Module {
   private final Iterable<Module> modules;

   public MergedModule(Module... var1) {
      this.modules = Arrays.asList(var1);
   }

   public MergedModule(Iterable<Module> var1) {
      this.modules = var1;
   }

   public void configure(Binder var1) {
      ElementMerger var2 = new ElementMerger(var1);

      for (Element var3 : Elements.getElements(this.modules)) {
         var3.acceptVisitor(var2);
      }
   }
}
