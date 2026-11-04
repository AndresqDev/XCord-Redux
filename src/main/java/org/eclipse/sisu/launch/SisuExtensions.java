package org.eclipse.sisu.launch;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.sisu.inject.Logs;
import org.eclipse.sisu.space.ClassSpace;
import org.eclipse.sisu.space.ClassVisitor;
import org.eclipse.sisu.space.IndexedClassFinder;
import org.eclipse.sisu.space.SpaceModule;
import org.eclipse.sisu.space.SpaceVisitor;
import org.eclipse.sisu.wire.WireModule;
import org.eclipse.sisu.wire.Wiring;

public final class SisuExtensions implements SpaceModule.Strategy, WireModule.Strategy {
   private final ClassSpace space;
   private final boolean global;

   private SisuExtensions(ClassSpace var1, boolean var2) {
      this.space = var1;
      this.global = var2;
   }

   public static SisuExtensions local(ClassSpace var0) {
      return new SisuExtensions(var0, false);
   }

   public static SisuExtensions global(ClassSpace var0) {
      return new SisuExtensions(var0, true);
   }

   public void install(Binder var1) {
      this.install(var1, null, null);
   }

   public <C> void install(Binder var1, Class<C> var2, C var3) {
      for (Module var4 : this.create(Module.class, var2, var3)) {
         var1.install(var4);
      }
   }

   @Override
   public Wiring wiring(Binder var1) {
      final Wiring var2 = WireModule.Strategy.DEFAULT.wiring(var1);
      final List var3 = this.create(Wiring.class, Binder.class, var1);
      return var3.isEmpty() ? var2 : new Wiring() {
         @Override
         public boolean wire(Key<?> var1) {
            for (Wiring var2x : var3) {
               if (var2x.wire(var1)) {
                  return true;
               }
            }

            return var2.wire(var1);
         }
      };
   }

   @Override
   public SpaceVisitor visitor(Binder var1) {
      final SpaceVisitor var2 = SpaceModule.Strategy.DEFAULT.visitor(var1);
      final List var3 = this.create(SpaceVisitor.class, Binder.class, var1);
      return var3.isEmpty() ? var2 : new SpaceVisitor() {
         @Override
         public void enterSpace(ClassSpace var1) {
            for (SpaceVisitor var2x : var3) {
               var2x.enterSpace(var1);
            }

            var2.enterSpace(var1);
         }

         @Override
         public ClassVisitor visitClass(URL var1) {
            for (SpaceVisitor var2x : var3) {
               ClassVisitor var4 = var2x.visitClass(var1);
               if (var4 != null) {
                  return var4;
               }
            }

            return var2.visitClass(var1);
         }

         @Override
         public void leaveSpace() {
            for (SpaceVisitor var1 : var3) {
               var1.leaveSpace();
            }

            var2.leaveSpace();
         }
      };
   }

   public <T> List<T> create(Class<T> var1) {
      return this.create(var1, null, null);
   }

   public <T, C> List<T> create(Class<T> var1, Class<C> var2, C var3) {
      ArrayList var4 = new ArrayList();

      for (Class var5 : this.load(var1)) {
         try {
            Object var7 = null;
            if (var2 != null) {
               try {
                  var7 = var5.getConstructor(var2).newInstance(var3);
               } catch (NoSuchMethodException var9) {
               }
            }

            var4.add(var7 != null ? var7 : var5.newInstance());
         } catch (Exception var10) {
            Object var8 = var10 instanceof InvocationTargetException ? var10.getCause() : var10;
            Logs.trace("Problem creating: {}", var5, var8);
         } catch (LinkageError var11) {
            Logs.trace("Problem creating: {}", var5, var11);
         }
      }

      return var4;
   }

   public <T> List<Class<? extends T>> load(Class<T> var1) {
      String var2 = "META-INF/services/" + var1.getName();
      ArrayList var3 = new ArrayList();

      for (String var4 : new IndexedClassFinder(var2, this.global).indexedNames(this.space)) {
         try {
            var3.add(this.space.loadClass(var4).asSubclass(var1));
         } catch (Exception var7) {
            Logs.trace("Problem loading: {}", var4, var7);
         } catch (LinkageError var8) {
            Logs.trace("Problem loading: {}", var4, var8);
         }
      }

      return var3;
   }
}
