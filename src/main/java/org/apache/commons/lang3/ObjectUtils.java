package org.apache.commons.lang3;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import org.apache.commons.lang3.exception.CloneFailedException;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.text.StrBuilder;

public class ObjectUtils {
   private static final char AT_SIGN = '@';
   public static final ObjectUtils.Null NULL = new ObjectUtils.Null();

   public static <T> T defaultIfNull(T var0, T var1) {
      return (T)(var0 != null ? var0 : var1);
   }

   @SafeVarargs
   public static <T> T firstNonNull(T... var0) {
      if (var0 != null) {
         for (Object var4 : var0) {
            if (var4 != null) {
               return (T)var4;
            }
         }
      }

      return null;
   }

   public static boolean anyNotNull(Object... var0) {
      return firstNonNull(var0) != null;
   }

   public static boolean allNotNull(Object... var0) {
      if (var0 == null) {
         return false;
      } else {
         for (Object var4 : var0) {
            if (var4 == null) {
               return false;
            }
         }

         return true;
      }
   }

   @Deprecated
   public static boolean equals(Object var0, Object var1) {
      if (var0 == var1) {
         return true;
      } else {
         return var0 != null && var1 != null ? var0.equals(var1) : false;
      }
   }

   public static boolean notEqual(Object var0, Object var1) {
      return !equals(var0, var1);
   }

   @Deprecated
   public static int hashCode(Object var0) {
      return var0 == null ? 0 : var0.hashCode();
   }

   @Deprecated
   public static int hashCodeMulti(Object... var0) {
      int var1 = 1;
      if (var0 != null) {
         for (Object var5 : var0) {
            int var6 = hashCode(var5);
            var1 = var1 * 31 + var6;
         }
      }

      return var1;
   }

   public static String identityToString(Object var0) {
      if (var0 == null) {
         return null;
      } else {
         String var1 = var0.getClass().getName();
         String var2 = Integer.toHexString(System.identityHashCode(var0));
         StringBuilder var3 = new StringBuilder(var1.length() + 1 + var2.length());
         var3.append(var1).append('@').append(var2);
         return var3.toString();
      }
   }

   public static void identityToString(Appendable var0, Object var1) throws IOException {
      Validate.notNull(var1, "Cannot get the toString of a null object");
      var0.append(var1.getClass().getName()).append('@').append(Integer.toHexString(System.identityHashCode(var1)));
   }

   @Deprecated
   public static void identityToString(StrBuilder var0, Object var1) {
      Validate.notNull(var1, "Cannot get the toString of a null object");
      String var2 = var1.getClass().getName();
      String var3 = Integer.toHexString(System.identityHashCode(var1));
      var0.ensureCapacity(var0.length() + var2.length() + 1 + var3.length());
      var0.append(var2).append('@').append(var3);
   }

   public static void identityToString(StringBuffer var0, Object var1) {
      Validate.notNull(var1, "Cannot get the toString of a null object");
      String var2 = var1.getClass().getName();
      String var3 = Integer.toHexString(System.identityHashCode(var1));
      var0.ensureCapacity(var0.length() + var2.length() + 1 + var3.length());
      var0.append(var2).append('@').append(var3);
   }

   public static void identityToString(StringBuilder var0, Object var1) {
      Validate.notNull(var1, "Cannot get the toString of a null object");
      String var2 = var1.getClass().getName();
      String var3 = Integer.toHexString(System.identityHashCode(var1));
      var0.ensureCapacity(var0.length() + var2.length() + 1 + var3.length());
      var0.append(var2).append('@').append(var3);
   }

   @Deprecated
   public static String toString(Object var0) {
      return var0 == null ? "" : var0.toString();
   }

   @Deprecated
   public static String toString(Object var0, String var1) {
      return var0 == null ? var1 : var0.toString();
   }

   @SafeVarargs
   public static <T extends Comparable<? super T>> T min(T... var0) {
      Comparable var1 = null;
      if (var0 != null) {
         for (Comparable var5 : var0) {
            if (compare((T)var5, (T)var1, true) < 0) {
               var1 = var5;
            }
         }
      }

      return (T)var1;
   }

   @SafeVarargs
   public static <T extends Comparable<? super T>> T max(T... var0) {
      Comparable var1 = null;
      if (var0 != null) {
         for (Comparable var5 : var0) {
            if (compare((T)var5, (T)var1, false) > 0) {
               var1 = var5;
            }
         }
      }

      return (T)var1;
   }

   public static <T extends Comparable<? super T>> int compare(T var0, T var1) {
      return compare((T)var0, (T)var1, false);
   }

   public static <T extends Comparable<? super T>> int compare(T var0, T var1, boolean var2) {
      if (var0 == var1) {
         return 0;
      } else if (var0 == null) {
         return var2 ? 1 : -1;
      } else if (var1 == null) {
         return var2 ? -1 : 1;
      } else {
         return var0.compareTo(var1);
      }
   }

   @SafeVarargs
   public static <T extends Comparable<? super T>> T median(T... var0) {
      Validate.notEmpty(var0);
      Validate.noNullElements(var0);
      TreeSet var1 = new TreeSet();
      Collections.addAll(var1, var0);
      return (T)var1.toArray()[(var1.size() - 1) / 2];
   }

   @SafeVarargs
   public static <T> T median(Comparator<T> var0, T... var1) {
      Validate.notEmpty(var1, "null/empty items");
      Validate.noNullElements(var1);
      Validate.notNull(var0, "null comparator");
      TreeSet var2 = new TreeSet(var0);
      Collections.addAll(var2, var1);
      return (T)var2.toArray()[(var2.size() - 1) / 2];
   }

   @SafeVarargs
   public static <T> T mode(T... var0) {
      if (ArrayUtils.isNotEmpty(var0)) {
         HashMap var1 = new HashMap(var0.length);

         for (Object var5 : var0) {
            MutableInt var6 = (MutableInt)var1.get(var5);
            if (var6 == null) {
               var1.put(var5, new MutableInt(1));
            } else {
               var6.increment();
            }
         }

         Object var7 = null;
         int var8 = 0;

         for (Entry var10 : var1.entrySet()) {
            int var11 = ((MutableInt)var10.getValue()).intValue();
            if (var11 == var8) {
               var7 = null;
            } else if (var11 > var8) {
               var8 = var11;
               var7 = var10.getKey();
            }
         }

         return (T)var7;
      } else {
         return null;
      }
   }

   public static <T> T clone(T var0) {
      if (!(var0 instanceof Cloneable)) {
         return null;
      } else {
         Object var1;
         if (var0.getClass().isArray()) {
            Class var2 = var0.getClass().getComponentType();
            if (var2.isPrimitive()) {
               int var3 = Array.getLength(var0);
               var1 = Array.newInstance(var2, var3);

               while (var3-- > 0) {
                  Array.set(var1, var3, Array.get(var0, var3));
               }
            } else {
               var1 = ((Object[])var0).clone();
            }
         } else {
            try {
               Method var7 = var0.getClass().getMethod("clone");
               var1 = var7.invoke(var0);
            } catch (NoSuchMethodException var4) {
               throw new CloneFailedException("Cloneable type " + var0.getClass().getName() + " has no clone method", var4);
            } catch (IllegalAccessException var5) {
               throw new CloneFailedException("Cannot clone Cloneable type " + var0.getClass().getName(), var5);
            } catch (InvocationTargetException var6) {
               throw new CloneFailedException("Exception cloning Cloneable type " + var0.getClass().getName(), var6.getCause());
            }
         }

         return (T)var1;
      }
   }

   public static <T> T cloneIfPossible(T var0) {
      Object var1 = clone(var0);
      return (T)(var1 == null ? var0 : var1);
   }

   public static boolean CONST(boolean var0) {
      return var0;
   }

   public static byte CONST(byte var0) {
      return var0;
   }

   public static byte CONST_BYTE(int var0) {
      if (var0 >= -128 && var0 <= 127) {
         return (byte)var0;
      } else {
         throw new IllegalArgumentException("Supplied value must be a valid byte literal between -128 and 127: [" + var0 + "]");
      }
   }

   public static char CONST(char var0) {
      return var0;
   }

   public static short CONST(short var0) {
      return var0;
   }

   public static short CONST_SHORT(int var0) {
      if (var0 >= -32768 && var0 <= 32767) {
         return (short)var0;
      } else {
         throw new IllegalArgumentException("Supplied value must be a valid byte literal between -32768 and 32767: [" + var0 + "]");
      }
   }

   public static int CONST(int var0) {
      return var0;
   }

   public static long CONST(long var0) {
      return var0;
   }

   public static float CONST(float var0) {
      return var0;
   }

   public static double CONST(double var0) {
      return var0;
   }

   public static <T> T CONST(T var0) {
      return (T)var0;
   }

   public static class Null implements Serializable {
      private static final long serialVersionUID = 7092611880189329093L;

      Null() {
      }

      private Object readResolve() {
         return ObjectUtils.NULL;
      }
   }
}
