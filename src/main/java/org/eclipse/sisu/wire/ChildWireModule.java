package org.eclipse.sisu.wire;

import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.spi.Element;
import com.google.inject.spi.Elements;
import java.util.Arrays;
import org.eclipse.sisu.inject.DefaultBeanLocator;

public final class ChildWireModule implements Module {
   private final Injector parent;
   private final Iterable<Module> modules;
   private WireModule.Strategy strategy = WireModule.Strategy.DEFAULT;

   public ChildWireModule(Injector var1, Module... var2) {
      this(var1, Arrays.asList(var2));
   }

   public ChildWireModule(Injector var1, Iterable<Module> var2) {
      this.modules = var2;
      this.parent = var1;
   }

   public Module with(WireModule.Strategy var1) {
      this.strategy = var1;
      return this;
   }

   public void configure(Binder var1) {
      var1.requestStaticInjection(new Class[]{DefaultBeanLocator.class});
      ElementAnalyzer var2 = new ElementAnalyzer(var1);

      for (Injector var3 = this.parent; var3 != null; var3 = var3.getParent()) {
         var2.ignoreKeys(var3.getAllBindings().keySet());
      }

      for (Element var5 : Elements.getElements(this.modules)) {
         var5.acceptVisitor(var2);
      }

      var2.apply(this.strategy);
   }
}
