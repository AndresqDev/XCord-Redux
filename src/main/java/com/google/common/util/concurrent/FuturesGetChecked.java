package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Ordering;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
final class FuturesGetChecked {
   private static final Ordering<Constructor<?>> WITH_STRING_PARAM_FIRST = Ordering.<Boolean>natural().onResultOf(new Function<Constructor<?>, Boolean>() {
      public Boolean apply(Constructor<?> var1) {
         return Arrays.asList(var1.getParameterTypes()).contains(String.class);
      }
   }).reverse();

   @ParametricNullness
   @CanIgnoreReturnValue
   static <V, X extends Exception> V getChecked(Future<V> var0, Class<X> var1) throws X {
      return getChecked(bestGetCheckedTypeValidator(), var0, var1);
   }

   @ParametricNullness
   @CanIgnoreReturnValue
   @VisibleForTesting
   static <V, X extends Exception> V getChecked(FuturesGetChecked.GetCheckedTypeValidator var0, Future<V> var1, Class<X> var2) throws X {
      var0.validateClass(var2);

      try {
         return (V)var1.get();
      } catch (InterruptedException var4) {
         Thread.currentThread().interrupt();
         throw newWithCause(var2, var4);
      } catch (ExecutionException var5) {
         wrapAndThrowExceptionOrError(var5.getCause(), var2);
         throw new AssertionError();
      }
   }

   @ParametricNullness
   @CanIgnoreReturnValue
   static <V, X extends Exception> V getChecked(Future<V> var0, Class<X> var1, long var2, TimeUnit var4) throws X {
      bestGetCheckedTypeValidator().validateClass(var1);

      try {
         return (V)var0.get(var2, var4);
      } catch (InterruptedException var6) {
         Thread.currentThread().interrupt();
         throw newWithCause(var1, var6);
      } catch (TimeoutException var7) {
         throw newWithCause(var1, var7);
      } catch (ExecutionException var8) {
         wrapAndThrowExceptionOrError(var8.getCause(), var1);
         throw new AssertionError();
      }
   }

   private static FuturesGetChecked.GetCheckedTypeValidator bestGetCheckedTypeValidator() {
      return FuturesGetChecked.GetCheckedTypeValidatorHolder.BEST_VALIDATOR;
   }

   @VisibleForTesting
   static FuturesGetChecked.GetCheckedTypeValidator weakSetValidator() {
      return FuturesGetChecked.GetCheckedTypeValidatorHolder.WeakSetValidator.INSTANCE;
   }

   @VisibleForTesting
   static FuturesGetChecked.GetCheckedTypeValidator classValueValidator() {
      return FuturesGetChecked.GetCheckedTypeValidatorHolder.ClassValueValidator.INSTANCE;
   }

   private static <X extends Exception> void wrapAndThrowExceptionOrError(Throwable var0, Class<X> var1) throws X {
      if (var0 instanceof Error) {
         throw new ExecutionError((Error)var0);
      } else if (var0 instanceof RuntimeException) {
         throw new UncheckedExecutionException(var0);
      } else {
         throw newWithCause(var1, var0);
      }
   }

   private static boolean hasConstructorUsableByGetChecked(Class<? extends Exception> var0) {
      try {
         Exception var1 = newWithCause(var0, new Exception());
         return true;
      } catch (Exception var2) {
         return false;
      }
   }

   private static <X extends Exception> X newWithCause(Class<X> var0, Throwable var1) {
      List var2 = Arrays.asList(var0.getConstructors());

      for (Constructor var4 : preferringStrings(var2)) {
         Exception var5 = newFromConstructor(var4, var1);
         if (var5 != null) {
            if (var5.getCause() == null) {
               var5.initCause(var1);
            }

            return (X)var5;
         }
      }

      String var6 = String.valueOf(var0);
      throw new IllegalArgumentException(
         new StringBuilder(82 + String.valueOf(var6).length())
            .append("No appropriate constructor for exception of type ")
            .append(var6)
            .append(" in response to chained exception")
            .toString(),
         var1
      );
   }

   private static <X extends Exception> List<Constructor<X>> preferringStrings(List<Constructor<X>> var0) {
      return WITH_STRING_PARAM_FIRST.sortedCopy(var0);
   }

   private static <X> X newFromConstructor(Constructor<X> var0, Throwable var1) {
      Class[] var2 = var0.getParameterTypes();
      Object[] var3 = new Object[var2.length];

      for (int var4 = 0; var4 < var2.length; var4++) {
         Class var5 = var2[var4];
         if (var5.equals(String.class)) {
            var3[var4] = var1.toString();
         } else {
            if (!var5.equals(Throwable.class)) {
               return null;
            }

            var3[var4] = var1;
         }
      }

      try {
         return (X)var0.newInstance(var3);
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException | IllegalArgumentException var6) {
         return null;
      }
   }

   @VisibleForTesting
   static boolean isCheckedException(Class<? extends Exception> var0) {
      return !RuntimeException.class.isAssignableFrom(var0);
   }

   @VisibleForTesting
   static void checkExceptionClassValidity(Class<? extends Exception> var0) {
      Preconditions.checkArgument(isCheckedException(var0), "Futures.getChecked exception type (%s) must not be a RuntimeException", var0);
      Preconditions.checkArgument(
         hasConstructorUsableByGetChecked(var0),
         "Futures.getChecked exception type (%s) must be an accessible class with an accessible constructor whose parameters (if any) must be of type String and/or Throwable",
         var0
      );
   }

   private FuturesGetChecked() {
   }

   @VisibleForTesting
   interface GetCheckedTypeValidator {
      void validateClass(Class<? extends Exception> var1);
   }

   @VisibleForTesting
   static class GetCheckedTypeValidatorHolder {
      static final String CLASS_VALUE_VALIDATOR_NAME = String.valueOf(FuturesGetChecked.GetCheckedTypeValidatorHolder.class.getName())
         .concat("$ClassValueValidator");
      static final FuturesGetChecked.GetCheckedTypeValidator BEST_VALIDATOR = getBestValidator();

      static FuturesGetChecked.GetCheckedTypeValidator getBestValidator() {
         try {
            Class var0 = Class.forName(CLASS_VALUE_VALIDATOR_NAME).asSubclass(Enum.class);
            return (FuturesGetChecked.GetCheckedTypeValidator)((Enum[])var0.getEnumConstants())[0];
         } catch (Throwable var1) {
            return FuturesGetChecked.weakSetValidator();
         }
      }

      static enum ClassValueValidator implements FuturesGetChecked.GetCheckedTypeValidator {
         INSTANCE;

         private static final ClassValue<Boolean> isValidClass = new ClassValue<Boolean>() {
            protected Boolean computeValue(Class<?> var1) {
               FuturesGetChecked.checkExceptionClassValidity(var1.asSubclass(Exception.class));
               return true;
            }
         };

         @Override
         public void validateClass(Class<? extends Exception> var1) {
            isValidClass.get(var1);
         }
      }

      static enum WeakSetValidator implements FuturesGetChecked.GetCheckedTypeValidator {
         INSTANCE;

         private static final Set<WeakReference<Class<? extends Exception>>> validClasses = new CopyOnWriteArraySet<>();

         @Override
         public void validateClass(Class<? extends Exception> var1) {
            for (WeakReference var3 : validClasses) {
               if (var1.equals(var3.get())) {
                  return;
               }
            }

            FuturesGetChecked.checkExceptionClassValidity(var1);
            if (validClasses.size() > 1000) {
               validClasses.clear();
            }

            validClasses.add(new WeakReference<>(var1));
         }
      }
   }
}
