package io.netty.util;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Constructor;

public abstract class ResourceLeakDetectorFactory {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ResourceLeakDetectorFactory.class);
   private static volatile ResourceLeakDetectorFactory factoryInstance = new ResourceLeakDetectorFactory.DefaultResourceLeakDetectorFactory();

   public static ResourceLeakDetectorFactory instance() {
      return factoryInstance;
   }

   public static void setResourceLeakDetectorFactory(ResourceLeakDetectorFactory var0) {
      factoryInstance = ObjectUtil.checkNotNull(var0, "factory");
   }

   public final <T> ResourceLeakDetector<T> newResourceLeakDetector(Class<T> var1) {
      return this.newResourceLeakDetector(var1, ResourceLeakDetector.SAMPLING_INTERVAL);
   }

   @Deprecated
   public abstract <T> ResourceLeakDetector<T> newResourceLeakDetector(Class<T> var1, int var2, long var3);

   public <T> ResourceLeakDetector<T> newResourceLeakDetector(Class<T> var1, int var2) {
      ObjectUtil.checkPositive(var2, "samplingInterval");
      return this.newResourceLeakDetector(var1, var2, Long.MAX_VALUE);
   }

   private static final class DefaultResourceLeakDetectorFactory extends ResourceLeakDetectorFactory {
      private final Constructor<?> obsoleteCustomClassConstructor;
      private final Constructor<?> customClassConstructor;

      DefaultResourceLeakDetectorFactory() {
         String var1;
         try {
            var1 = SystemPropertyUtil.get("io.netty.customResourceLeakDetector");
         } catch (Throwable var3) {
            ResourceLeakDetectorFactory.logger.error("Could not access System property: io.netty.customResourceLeakDetector", var3);
            var1 = null;
         }

         if (var1 == null) {
            this.obsoleteCustomClassConstructor = this.customClassConstructor = null;
         } else {
            this.obsoleteCustomClassConstructor = obsoleteCustomClassConstructor(var1);
            this.customClassConstructor = customClassConstructor(var1);
         }
      }

      private static Constructor<?> obsoleteCustomClassConstructor(String var0) {
         try {
            Class var1 = Class.forName(var0, true, PlatformDependent.getSystemClassLoader());
            if (ResourceLeakDetector.class.isAssignableFrom(var1)) {
               return var1.getConstructor(Class.class, int.class, long.class);
            }

            ResourceLeakDetectorFactory.logger.error("Class {} does not inherit from ResourceLeakDetector.", var0);
         } catch (Throwable var2) {
            ResourceLeakDetectorFactory.logger.error("Could not load custom resource leak detector class provided: {}", var0, var2);
         }

         return null;
      }

      private static Constructor<?> customClassConstructor(String var0) {
         try {
            Class var1 = Class.forName(var0, true, PlatformDependent.getSystemClassLoader());
            if (ResourceLeakDetector.class.isAssignableFrom(var1)) {
               return var1.getConstructor(Class.class, int.class);
            }

            ResourceLeakDetectorFactory.logger.error("Class {} does not inherit from ResourceLeakDetector.", var0);
         } catch (Throwable var2) {
            ResourceLeakDetectorFactory.logger.error("Could not load custom resource leak detector class provided: {}", var0, var2);
         }

         return null;
      }

      @Override
      public <T> ResourceLeakDetector<T> newResourceLeakDetector(Class<T> var1, int var2, long var3) {
         if (this.obsoleteCustomClassConstructor != null) {
            try {
               ResourceLeakDetector var7 = (ResourceLeakDetector)this.obsoleteCustomClassConstructor.newInstance(var1, var2, var3);
               ResourceLeakDetectorFactory.logger
                  .debug("Loaded custom ResourceLeakDetector: {}", this.obsoleteCustomClassConstructor.getDeclaringClass().getName());
               return var7;
            } catch (Throwable var6) {
               ResourceLeakDetectorFactory.logger
                  .error(
                     "Could not load custom resource leak detector provided: {} with the given resource: {}",
                     this.obsoleteCustomClassConstructor.getDeclaringClass().getName(),
                     var1,
                     var6
                  );
            }
         }

         ResourceLeakDetector var5 = new ResourceLeakDetector(var1, var2, var3);
         ResourceLeakDetectorFactory.logger.debug("Loaded default ResourceLeakDetector: {}", var5);
         return var5;
      }

      @Override
      public <T> ResourceLeakDetector<T> newResourceLeakDetector(Class<T> var1, int var2) {
         if (this.customClassConstructor != null) {
            try {
               ResourceLeakDetector var5 = (ResourceLeakDetector)this.customClassConstructor.newInstance(var1, var2);
               ResourceLeakDetectorFactory.logger.debug("Loaded custom ResourceLeakDetector: {}", this.customClassConstructor.getDeclaringClass().getName());
               return var5;
            } catch (Throwable var4) {
               ResourceLeakDetectorFactory.logger
                  .error(
                     "Could not load custom resource leak detector provided: {} with the given resource: {}",
                     this.customClassConstructor.getDeclaringClass().getName(),
                     var1,
                     var4
                  );
            }
         }

         ResourceLeakDetector var3 = new ResourceLeakDetector(var1, var2);
         ResourceLeakDetectorFactory.logger.debug("Loaded default ResourceLeakDetector: {}", var3);
         return var3;
      }
   }
}
