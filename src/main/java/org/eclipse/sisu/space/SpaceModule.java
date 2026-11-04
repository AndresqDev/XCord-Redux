package org.eclipse.sisu.space;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.PrivateBinder;
import com.google.inject.spi.Element;
import com.google.inject.spi.Elements;
import com.google.inject.spi.MembersInjectorLookup;
import com.google.inject.spi.PrivateElements;
import com.google.inject.spi.ProviderLookup;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class SpaceModule implements Module {
   private static final String NAMED_INDEX = "META-INF/sisu/javax.inject.Named";
   public static final ClassFinder LOCAL_INDEX = new IndexedClassFinder("META-INF/sisu/javax.inject.Named", false);
   public static final ClassFinder GLOBAL_INDEX = new IndexedClassFinder("META-INF/sisu/javax.inject.Named", true);
   public static final ClassFinder LOCAL_SCAN = SpaceScanner.DEFAULT_FINDER;
   private final boolean caching;
   private final ClassSpace space;
   private final ClassFinder finder;
   private SpaceModule.Strategy strategy = SpaceModule.Strategy.DEFAULT;

   public SpaceModule(ClassSpace var1) {
      this(var1, BeanScanning.ON);
   }

   public SpaceModule(ClassSpace var1, ClassFinder var2) {
      this.caching = false;
      this.space = var1;
      this.finder = var2;
   }

   public SpaceModule(ClassSpace var1, BeanScanning var2) {
      this.caching = BeanScanning.CACHE == var2;
      this.space = var1;
      switch (var2) {
         case OFF:
            this.finder = null;
            break;
         case CACHE:
         default:
            this.finder = LOCAL_SCAN;
            break;
         case INDEX:
            this.finder = LOCAL_INDEX;
            break;
         case GLOBAL_INDEX:
            this.finder = GLOBAL_INDEX;
      }
   }

   public Module with(SpaceModule.Strategy var1) {
      this.strategy = var1;
      return this;
   }

   public void configure(Binder var1) {
      var1.bind(ClassSpace.class).toInstance(this.space);
      if (this.caching) {
         this.recordAndReplayElements(var1);
      } else if (this.finder != null) {
         this.scanForElements(var1);
      }
   }

   void scanForElements(Binder var1) {
      new SpaceScanner(this.space, this.finder).accept(this.strategy.visitor(var1));
   }

   private void recordAndReplayElements(Binder var1) {
      String var2 = this.space.toString();
      List var3 = SpaceModule.RecordedElements.cache.get(var2);
      if (var3 == null) {
         List var4 = Elements.getElements(new Module[]{new Module() {
            public void configure(Binder var1) {
               SpaceModule.this.scanForElements(var1);
            }
         }});
         var3 = SpaceModule.RecordedElements.cache.putIfAbsent(var2, var4);
         if (var3 == null) {
            Elements.getModule(var4).configure(var1);
            return;
         }
      }

      replayRecordedElements(var1, var3);
   }

   private static void replayRecordedElements(Binder var0, List<Element> var1) {
      for (Element var2 : var1) {
         if (var2 instanceof ProviderLookup) {
            var0.getProvider(((ProviderLookup)var2).getKey());
         } else if (var2 instanceof MembersInjectorLookup) {
            var0.getMembersInjector(((MembersInjectorLookup)var2).getType());
         } else if (var2 instanceof PrivateElements) {
            PrivateElements var4 = (PrivateElements)var2;
            PrivateBinder var5 = var0.withSource(var2.getSource()).newPrivateBinder();
            replayRecordedElements(var5, var4.getElements());

            for (Key var6 : var4.getExposedKeys()) {
               var5.withSource(var4.getExposedSource(var6)).expose(var6);
            }
         } else {
            var2.applyTo(var0);
         }
      }
   }

   private static final class RecordedElements {
      static final ConcurrentMap<String, List<Element>> cache = new ConcurrentHashMap<>();
   }

   public interface Strategy {
      SpaceModule.Strategy DEFAULT = new SpaceModule.Strategy() {
         @Override
         public SpaceVisitor visitor(Binder var1) {
            return new QualifiedTypeVisitor(new QualifiedTypeBinder(var1));
         }
      };

      SpaceVisitor visitor(Binder var1);
   }
}
