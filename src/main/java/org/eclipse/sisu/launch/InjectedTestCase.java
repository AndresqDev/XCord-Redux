package org.eclipse.sisu.launch;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.name.Names;
import java.io.File;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Properties;
import java.util.Map.Entry;
import junit.framework.TestCase;
import org.eclipse.sisu.inject.MutableBeanLocator;
import org.eclipse.sisu.space.BeanScanning;
import org.eclipse.sisu.space.ClassSpace;
import org.eclipse.sisu.space.SpaceModule;
import org.eclipse.sisu.space.URLClassSpace;
import org.eclipse.sisu.wire.ParameterKeys;
import org.eclipse.sisu.wire.WireModule;
import xcord.Inject;

public abstract class InjectedTestCase extends TestCase implements Module {
   private String basedir;
   @Inject
   private MutableBeanLocator locator;

   protected void setUp() throws Exception {
      Guice.createInjector(new Module[]{new WireModule(new InjectedTestCase.SetUpModule(), this.spaceModule())});
   }

   protected void tearDown() throws Exception {
      this.locator.clear();
   }

   public SpaceModule spaceModule() {
      return new SpaceModule(this.space(), this.scanning());
   }

   public ClassSpace space() {
      return new URLClassSpace(this.getClass().getClassLoader());
   }

   public BeanScanning scanning() {
      return BeanScanning.CACHE;
   }

   public void configure(Binder var1) {
   }

   public void configure(Properties var1) {
   }

   public final <T> T lookup(Class<T> var1) {
      return this.lookup(Key.get(var1));
   }

   public final <T> T lookup(Class<T> var1, String var2) {
      return this.lookup(var1, Names.named(var2));
   }

   public final <T> T lookup(Class<T> var1, Class<? extends Annotation> var2) {
      return this.lookup(Key.get(var1, var2));
   }

   public final <T> T lookup(Class<T> var1, Annotation var2) {
      return this.lookup(Key.get(var1, var2));
   }

   public final String getBasedir() {
      if (this.basedir == null) {
         this.basedir = System.getProperty("basedir", new File("").getAbsolutePath());
      }

      return this.basedir;
   }

   private <T> T lookup(Key<T> var1) {
      Iterator var2 = this.locator.locate(var1).iterator();
      return (T)(var2.hasNext() ? ((Entry)var2.next()).getValue() : null);
   }

   final class SetUpModule implements Module {
      public void configure(Binder var1) {
         var1.install(InjectedTestCase.this);
         Properties var2 = new Properties();
         var2.put("basedir", InjectedTestCase.this.getBasedir());
         InjectedTestCase.this.configure(var2);
         var1.bind(ParameterKeys.PROPERTIES).toInstance(var2);
         var1.requestInjection(InjectedTestCase.this);
      }
   }
}
