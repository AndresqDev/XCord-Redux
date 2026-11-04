package org.apache.logging.log4j.core.config.plugins.convert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;
import java.util.UnknownFormatConversionException;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.apache.logging.log4j.core.config.plugins.util.PluginType;
import org.apache.logging.log4j.core.util.ReflectionUtil;
import org.apache.logging.log4j.core.util.TypeUtil;
import org.apache.logging.log4j.status.StatusLogger;

public class TypeConverterRegistry {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static volatile TypeConverterRegistry INSTANCE;
   private static final Object INSTANCE_LOCK = new Object();
   private final ConcurrentMap<Type, TypeConverter<?>> registry = new ConcurrentHashMap<>();

   public static TypeConverterRegistry getInstance() {
      TypeConverterRegistry var0 = INSTANCE;
      if (var0 == null) {
         synchronized (INSTANCE_LOCK) {
            var0 = INSTANCE;
            if (var0 == null) {
               INSTANCE = var0 = new TypeConverterRegistry();
            }
         }
      }

      return var0;
   }

   public TypeConverter<?> findCompatibleConverter(Type var1) {
      Objects.requireNonNull(var1, "No type was provided");
      TypeConverter var2 = this.registry.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         if (var1 instanceof Class) {
            Class var3 = (Class)var1;
            if (var3.isEnum()) {
               EnumConverter var12 = new EnumConverter(var3.asSubclass(Enum.class));
               synchronized (INSTANCE_LOCK) {
                  return this.registerConverter(var1, var12);
               }
            }
         }

         for (Entry var4 : this.registry.entrySet()) {
            Type var5 = (Type)var4.getKey();
            if (TypeUtil.isAssignable(var1, var5)) {
               LOGGER.debug("Found compatible TypeConverter<{}> for type [{}].", var5, var1);
               TypeConverter var6 = (TypeConverter)var4.getValue();
               synchronized (INSTANCE_LOCK) {
                  return this.registerConverter(var1, var6);
               }
            }
         }

         throw new UnknownFormatConversionException(var1.toString());
      }
   }

   private TypeConverterRegistry() {
      LOGGER.trace("TypeConverterRegistry initializing.");
      PluginManager var1 = new PluginManager("TypeConverter");
      var1.collectPlugins();
      this.loadKnownTypeConverters(var1.getPlugins().values());
      this.registerPrimitiveTypes();
   }

   private void loadKnownTypeConverters(Collection<PluginType<?>> var1) {
      for (PluginType var3 : var1) {
         Class var4 = var3.getPluginClass();
         if (TypeConverter.class.isAssignableFrom(var4)) {
            Class var5 = var4.asSubclass(TypeConverter.class);
            Type var6 = getTypeConverterSupportedType(var5);
            TypeConverter var7 = ReflectionUtil.instantiate(var5);
            this.registerConverter(var6, var7);
         }
      }
   }

   private TypeConverter<?> registerConverter(Type var1, TypeConverter<?> var2) {
      TypeConverter var3 = this.registry.get(var1);
      if (var3 != null) {
         boolean var4;
         if (var2 instanceof Comparable) {
            Comparable var5 = (Comparable)var2;
            var4 = var5.compareTo(var3) < 0;
         } else if (var3 instanceof Comparable) {
            Comparable var6 = (Comparable)var3;
            var4 = var6.compareTo(var2) > 0;
         } else {
            var4 = false;
         }

         if (var4) {
            LOGGER.debug("Replacing TypeConverter [{}] for type [{}] with [{}] after comparison.", var3, var1, var2);
            this.registry.put(var1, var2);
            return var2;
         } else {
            LOGGER.warn("Ignoring TypeConverter [{}] for type [{}] that conflicts with [{}], since they are not comparable.", var2, var1, var3);
            return var3;
         }
      } else {
         this.registry.put(var1, var2);
         return var2;
      }
   }

   private static Type getTypeConverterSupportedType(Class<? extends TypeConverter> var0) {
      for (Type var4 : var0.getGenericInterfaces()) {
         if (var4 instanceof ParameterizedType) {
            ParameterizedType var5 = (ParameterizedType)var4;
            if (TypeConverter.class.equals(var5.getRawType())) {
               return var5.getActualTypeArguments()[0];
            }
         }
      }

      return void.class;
   }

   private void registerPrimitiveTypes() {
      this.registerTypeAlias(Boolean.class, boolean.class);
      this.registerTypeAlias(Byte.class, byte.class);
      this.registerTypeAlias(Character.class, char.class);
      this.registerTypeAlias(Double.class, double.class);
      this.registerTypeAlias(Float.class, float.class);
      this.registerTypeAlias(Integer.class, int.class);
      this.registerTypeAlias(Long.class, long.class);
      this.registerTypeAlias(Short.class, short.class);
   }

   private void registerTypeAlias(Type var1, Type var2) {
      this.registry.putIfAbsent(var2, this.registry.get(var1));
   }
}
