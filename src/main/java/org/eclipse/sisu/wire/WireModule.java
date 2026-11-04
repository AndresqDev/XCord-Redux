package org.eclipse.sisu.wire;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.spi.Element;
import com.google.inject.spi.Elements;
import java.util.Arrays;

public final class WireModule implements Module {
   static final Module[] CONVERTERS = new Module[]{new FileTypeConverter(), new URLTypeConverter()};
   private final Iterable<Module> modules;
   private WireModule.Strategy strategy = WireModule.Strategy.DEFAULT;

   public WireModule(Module... var1) {
      this(Arrays.asList(var1));
   }

   public WireModule(Iterable<Module> var1) {
      this.modules = var1;
   }

   public Module with(WireModule.Strategy var1) {
      this.strategy = var1;
      return this;
   }

   public void configure(Binder var1) {
      ElementAnalyzer var2 = new ElementAnalyzer(var1);

      for (Element var3 : Elements.getElements(this.modules)) {
         var3.acceptVisitor(var2);
      }

      var2.apply(this.strategy);
   }

   public interface Strategy {
      WireModule.Strategy DEFAULT = new WireModule.Strategy() {
         @Override
         public Wiring wiring(Binder var1) {
            for (Module var2 : WireModule.CONVERTERS) {
               var2.configure(var1);
            }

            return new LocatorWiring(var1);
         }
      };

      Wiring wiring(Binder var1);
   }
}
