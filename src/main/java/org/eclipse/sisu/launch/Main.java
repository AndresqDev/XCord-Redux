package org.eclipse.sisu.launch;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;
import java.util.Collections;
import java.util.Map;
import org.eclipse.sisu.Parameters;
import org.eclipse.sisu.inject.MutableBeanLocator;
import org.eclipse.sisu.space.BeanScanning;
import org.eclipse.sisu.space.SpaceModule;
import org.eclipse.sisu.space.URLClassSpace;
import org.eclipse.sisu.wire.ParameterKeys;
import org.eclipse.sisu.wire.WireModule;
import xcord.Inject;

public final class Main implements Module {
   private final Map<?, ?> properties;
   private final String[] args;

   private Main(Map<?, ?> var1, String... var2) {
      this.properties = Collections.unmodifiableMap(var1);
      this.args = var2;
   }

   public static void main(String... var0) {
      boot(System.getProperties(), var0);
   }

   public static <T> T boot(Class<T> var0, String... var1) {
      return (T)boot(System.getProperties(), var1).getInstance(var0);
   }

   public static Injector boot(Map<?, ?> var0, String... var1) {
      BeanScanning var2 = BeanScanning.select(var0);
      Module var3 = wire(var2, new Main(var0, var1));
      return Guice.createInjector(new Module[]{var3});
   }

   public static Module wire(BeanScanning var0, Module... var1) {
      Module[] var2 = new Module[var1.length + 1];
      System.arraycopy(var1, 0, var2, 0, var1.length);
      ClassLoader var3 = Thread.currentThread().getContextClassLoader();
      var2[var1.length] = new SpaceModule(new URLClassSpace(var3), var0);
      return new WireModule(var2);
   }

   public void configure(Binder var1) {
      var1.bind(ParameterKeys.PROPERTIES).toInstance(this.properties);
      var1.bind(Main.ShutdownThread.class).asEagerSingleton();
   }

   @Provides
   @Parameters
   String[] parameters() {
      return (String[])this.args.clone();
   }

   static final class ShutdownThread extends Thread {
      private final MutableBeanLocator locator;

      @Inject
      ShutdownThread(MutableBeanLocator var1) {
         this.locator = var1;
         Runtime.getRuntime().addShutdownHook(this);
      }

      @Override
      public void run() {
         this.locator.clear();
      }
   }
}
