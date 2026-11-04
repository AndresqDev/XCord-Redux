package org.eclipse.sisu.wire;

import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.PrivateBinder;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.DefaultElementVisitor;
import com.google.inject.spi.Element;
import com.google.inject.spi.Elements;
import com.google.inject.spi.InjectionRequest;
import com.google.inject.spi.InstanceBinding;
import com.google.inject.spi.PrivateElements;
import com.google.inject.spi.ProviderInstanceBinding;
import com.google.inject.spi.ProviderLookup;
import com.google.inject.spi.RequireExplicitBindingsOption;
import com.google.inject.spi.StaticInjectionRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.sisu.Parameters;
import org.eclipse.sisu.inject.BeanLocator;
import org.eclipse.sisu.inject.DefaultBeanLocator;
import org.eclipse.sisu.inject.DefaultRankingFunction;
import org.eclipse.sisu.inject.Guice4;
import org.eclipse.sisu.inject.Logs;
import org.eclipse.sisu.inject.MutableBeanLocator;
import org.eclipse.sisu.inject.RankingFunction;
import org.eclipse.sisu.inject.TypeArguments;

final class ElementAnalyzer extends DefaultElementVisitor<Void> {
   private static final Map<Key<?>, Key<?>> LEGACY_KEY_ALIASES;
   private static final List<Element> JIT_BINDINGS;
   private final Set<Key<?>> localKeys = new HashSet<>();
   private final DependencyAnalyzer analyzer = new DependencyAnalyzer();
   private final List<ElementAnalyzer> privateAnalyzers = new ArrayList<>();
   private final List<Map<?, ?>> properties = new ArrayList<>();
   private final List<String> arguments = new ArrayList<>();
   private final Binder binder;
   private boolean requireExplicitBindings;

   static {
      HashMap var0 = new HashMap();

      try {
         addLegacyKeyAlias(var0, BeanLocator.class);
         addLegacyKeyAlias(var0, MutableBeanLocator.class);
         addLegacyKeyAlias(var0, RankingFunction.class);
      } catch (Exception var1) {
      } catch (LinkageError var2) {
      }

      LEGACY_KEY_ALIASES = var0.isEmpty() ? null : var0;
      JIT_BINDINGS = Elements.getElements(new Module[]{new Module() {
         public void configure(Binder var1) {
            var1.bind(BeanLocator.class).to(MutableBeanLocator.class);
            var1.bind(MutableBeanLocator.class).to(DefaultBeanLocator.class);
            var1.bind(RankingFunction.class).to(DefaultRankingFunction.class);
            var1.bind(TypeConverterCache.class);
         }
      }});
   }

   ElementAnalyzer(Binder var1) {
      this.binder = var1;
   }

   public void ignoreKeys(Set<Key<?>> var1) {
      this.localKeys.addAll(var1);
   }

   public void apply(WireModule.Strategy var1) {
      if (this.requireExplicitBindings) {
         this.makeJitBindingsExplicit();
      }

      Set var2 = this.analyzer.findMissingKeys(this.localKeys);
      MergedProperties var3 = new MergedProperties(this.properties);
      Wiring var4 = var1.wiring(this.binder);

      for (Key var5 : var2) {
         if (isParameters(var5)) {
            this.wireParameters(var5, var3);
         } else if (!isRestricted(var5)) {
            var4.wire(var5);
         }
      }

      for (ElementAnalyzer var7 : this.privateAnalyzers) {
         var7.ignoreKeys(this.localKeys);
         var7.ignoreKeys(var2);
         var7.apply(var1);
      }
   }

   public <T> Void visit(Binding<T> var1) {
      Key var2 = var1.getKey();
      if (!this.localKeys.contains(var2)) {
         if (isParameters(var2)) {
            this.mergeParameters(var1);
         } else if (Boolean.TRUE.equals(var1.acceptTargetVisitor(this.analyzer))) {
            this.localKeys.add(var2);
            var1.applyTo(this.binder);
            if (LEGACY_KEY_ALIASES != null) {
               Key var3 = LEGACY_KEY_ALIASES.get(var2);
               if (var3 != null && this.localKeys.add(var3)) {
                  this.binder.bind(var3).to(var2);
               }
            }
         } else {
            Logs.trace("Discard binding: {}", var1, null);
         }
      }

      return null;
   }

   public Void visit(PrivateElements var1) {
      PrivateBinder var2 = this.binder.withSource(var1.getSource()).newPrivateBinder();
      ElementAnalyzer var3 = new ElementAnalyzer(var2);
      this.privateAnalyzers.add(var3);
      var3.ignoreKeys(this.localKeys);

      for (Element var4 : var1.getElements()) {
         var4.acceptVisitor(var3);
      }

      for (Key var6 : var1.getExposedKeys()) {
         if (var3.localKeys.contains(var6) && this.localKeys.add(var6)) {
            var2.withSource(var1.getExposedSource(var6)).expose(var6);
         }
      }

      return null;
   }

   public <T> Void visit(ProviderLookup<T> var1) {
      this.analyzer.visit(var1);
      var1.applyTo(this.binder);
      return null;
   }

   public Void visit(StaticInjectionRequest var1) {
      this.analyzer.visit(var1);
      var1.applyTo(this.binder);
      return null;
   }

   public Void visit(InjectionRequest<?> var1) {
      this.analyzer.visit(var1);
      var1.applyTo(this.binder);
      return null;
   }

   public Void visit(RequireExplicitBindingsOption var1) {
      this.requireExplicitBindings = true;
      var1.applyTo(this.binder);
      return null;
   }

   public Void visitOther(Element var1) {
      var1.applyTo(this.binder);
      return null;
   }

   private void makeJitBindingsExplicit() {
      for (Element var1 : JIT_BINDINGS) {
         if (var1 instanceof Binding && this.localKeys.add(((Binding)var1).getKey())) {
            var1.applyTo(this.binder);
         }
      }
   }

   private void mergeParameters(Binding<?> var1) {
      Object var2 = null;
      if (var1 instanceof InstanceBinding) {
         var2 = ((InstanceBinding)var1).getInstance();
      } else if (var1 instanceof ProviderInstanceBinding) {
         var2 = Guice4.getProviderInstance((ProviderInstanceBinding<?>)var1).get();
      }

      if (var2 instanceof Map) {
         this.properties.add((Map<?, ?>)var2);
      } else if (var2 instanceof String[]) {
         Collections.addAll(this.arguments, (String[])var2);
      } else {
         Logs.warn("Ignoring incompatible @Parameters binding: {}", var1, null);
      }
   }

   private void wireParameters(Key var1, Map var2) {
      if (ParameterKeys.PROPERTIES.equals(var1)) {
         this.binder.bind(var1).toInstance(var2);
      } else {
         TypeLiteral var3 = var1.getTypeLiteral();
         Class var4 = var3.getRawType();
         if (Map.class == var4) {
            TypeLiteral[] var5 = TypeArguments.get(var3);
            if (var5.length == 2 && String.class == var5[1].getRawType()) {
               this.binder.bind(var1).to(StringProperties.class);
            } else {
               this.binder.bind(var1).to(ParameterKeys.PROPERTIES);
            }
         } else if (String[].class == var4) {
            this.binder.bind(var1).toInstance(this.arguments.toArray(new String[this.arguments.size()]));
         }
      }
   }

   private static boolean isParameters(Key<?> var0) {
      Class var1 = var0.getAnnotationType();
      return Parameters.class == var1 || org.sonatype.inject.Parameters.class == var1;
   }

   private static boolean isRestricted(Key<?> var0) {
      String var1 = var0.getTypeLiteral().getRawType().getName();
      return !var1.startsWith("org.eclipse.sisu.inject") && !var1.startsWith("org.sonatype.guice.bean.locators")
         ? "org.slf4j.Logger".equals(var1)
         : var1.endsWith("BeanLocator") || var1.endsWith("RankingFunction");
   }

   private static void addLegacyKeyAlias(Map<Key<?>, Key<?>> var0, Class<?> var1) throws ClassNotFoundException {
      String var2 = "org.sonatype.guice.bean.locators." + var1.getSimpleName();
      Class var3 = ElementAnalyzer.class.getClassLoader().loadClass(var2);
      if (var1.isAssignableFrom(var3)) {
         var0.put(Key.get(var3), Key.get(var1));
      }
   }
}
