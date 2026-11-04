package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
public final class Throwables {
   @GwtIncompatible
   private static final String JAVA_LANG_ACCESS_CLASSNAME = "sun.misc.JavaLangAccess";
   @GwtIncompatible
   @VisibleForTesting
   static final String SHARED_SECRETS_CLASSNAME = "sun.misc.SharedSecrets";
   @GwtIncompatible
   private static final Object jla = getJLA();
   @GwtIncompatible
   private static final Method getStackTraceElementMethod = jla == null ? null : getGetMethod();
   @GwtIncompatible
   private static final Method getStackTraceDepthMethod = jla == null ? null : getSizeMethod(jla);

   private Throwables() {
   }

   @GwtIncompatible
   public static <X extends Throwable> void throwIfInstanceOf(Throwable var0, Class<X> var1) throws X {
      Preconditions.checkNotNull(var0);
      if (var1.isInstance(var0)) {
         throw (Throwable)var1.cast(var0);
      }
   }

   @Deprecated
   @GwtIncompatible
   public static <X extends Throwable> void propagateIfInstanceOf(Throwable var0, Class<X> var1) throws X {
      if (var0 != null) {
         throwIfInstanceOf(var0, var1);
      }
   }

   public static void throwIfUnchecked(Throwable var0) {
      Preconditions.checkNotNull(var0);
      if (var0 instanceof RuntimeException) {
         throw (RuntimeException)var0;
      } else if (var0 instanceof Error) {
         throw (Error)var0;
      }
   }

   @Deprecated
   @GwtIncompatible
   public static void propagateIfPossible(Throwable var0) {
      if (var0 != null) {
         throwIfUnchecked(var0);
      }
   }

   @GwtIncompatible
   public static <X extends Throwable> void propagateIfPossible(Throwable var0, Class<X> var1) throws X {
      propagateIfInstanceOf(var0, var1);
      propagateIfPossible(var0);
   }

   @GwtIncompatible
   public static <X1 extends Throwable, X2 extends Throwable> void propagateIfPossible(Throwable var0, Class<X1> var1, Class<X2> var2) throws X1, X2 {
      Preconditions.checkNotNull(var2);
      propagateIfInstanceOf(var0, var1);
      propagateIfPossible(var0, var2);
   }

   @Deprecated
   @CanIgnoreReturnValue
   @GwtIncompatible
   public static RuntimeException propagate(Throwable var0) {
      throwIfUnchecked(var0);
      throw new RuntimeException(var0);
   }

   public static Throwable getRootCause(Throwable var0) {
      Throwable var1 = var0;

      Throwable var3;
      for (boolean var2 = false; (var3 = var0.getCause()) != null; var2 = !var2) {
         var0 = var3;
         if (var3 == var1) {
            throw new IllegalArgumentException("Loop in causal chain detected.", var3);
         }

         if (var2) {
            var1 = var1.getCause();
         }
      }

      return var0;
   }

   @Beta
   public static List<Throwable> getCausalChain(Throwable var0) {
      Preconditions.checkNotNull(var0);
      ArrayList var1 = new ArrayList(4);
      var1.add(var0);
      Throwable var2 = var0;

      Throwable var4;
      for (boolean var3 = false; (var4 = var0.getCause()) != null; var3 = !var3) {
         var0 = var4;
         var1.add(var4);
         if (var4 == var2) {
            throw new IllegalArgumentException("Loop in causal chain detected.", var4);
         }

         if (var3) {
            var2 = var2.getCause();
         }
      }

      return Collections.unmodifiableList(var1);
   }

   @Beta
   @GwtIncompatible
   public static <X extends Throwable> X getCauseAs(Throwable var0, Class<X> var1) {
      try {
         return (X)var1.cast(var0.getCause());
      } catch (ClassCastException var3) {
         var3.initCause(var0);
         throw var3;
      }
   }

   @GwtIncompatible
   public static String getStackTraceAsString(Throwable var0) {
      StringWriter var1 = new StringWriter();
      var0.printStackTrace(new PrintWriter(var1));
      return var1.toString();
   }

   @Beta
   @GwtIncompatible
   public static List<StackTraceElement> lazyStackTrace(Throwable var0) {
      return lazyStackTraceIsLazy() ? jlaStackTrace(var0) : Collections.unmodifiableList(Arrays.asList(var0.getStackTrace()));
   }

   @Beta
   @GwtIncompatible
   public static boolean lazyStackTraceIsLazy() {
      return getStackTraceElementMethod != null && getStackTraceDepthMethod != null;
   }

   @GwtIncompatible
   private static List<StackTraceElement> jlaStackTrace(final Throwable var0) {
      Preconditions.checkNotNull(var0);
      return new AbstractList<StackTraceElement>() {
         public StackTraceElement get(int var1) {
            return (StackTraceElement)Throwables.invokeAccessibleNonThrowingMethod(
               java.util.Objects.requireNonNull(Throwables.getStackTraceElementMethod), java.util.Objects.requireNonNull(Throwables.jla), var0, var1
            );
         }

         @Override
         public int size() {
            return (Integer)Throwables.invokeAccessibleNonThrowingMethod(
               java.util.Objects.requireNonNull(Throwables.getStackTraceDepthMethod), java.util.Objects.requireNonNull(Throwables.jla), var0
            );
         }
      };
   }

   @GwtIncompatible
   private static Object invokeAccessibleNonThrowingMethod(Method var0, Object var1, Object... var2) {
      try {
         return var0.invoke(var1, var2);
      } catch (IllegalAccessException var4) {
         throw new RuntimeException(var4);
      } catch (InvocationTargetException var5) {
         throw propagate(var5.getCause());
      }
   }

   @GwtIncompatible
   private static Object getJLA() {
      try {
         Class var0 = Class.forName("sun.misc.SharedSecrets", false, null);
         Method var1 = var0.getMethod("getJavaLangAccess");
         return var1.invoke(null);
      } catch (ThreadDeath var2) {
         throw var2;
      } catch (Throwable var3) {
         return null;
      }
   }

   @GwtIncompatible
   private static Method getGetMethod() {
      return getJlaMethod("getStackTraceElement", Throwable.class, int.class);
   }

   @GwtIncompatible
   private static Method getSizeMethod(Object var0) {
      try {
         Method var1 = getJlaMethod("getStackTraceDepth", Throwable.class);
         if (var1 == null) {
            return null;
         } else {
            var1.invoke(var0, new Throwable());
            return var1;
         }
      } catch (IllegalAccessException | InvocationTargetException | UnsupportedOperationException var2) {
         return null;
      }
   }

   @GwtIncompatible
   private static Method getJlaMethod(String var0, Class<?>... var1) throws ThreadDeath {
      try {
         return Class.forName("sun.misc.JavaLangAccess", false, null).getMethod(var0, var1);
      } catch (ThreadDeath var3) {
         throw var3;
      } catch (Throwable var4) {
         return null;
      }
   }
}
