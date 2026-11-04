package org.apache.commons.lang3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SerializationUtils {
   public static <T extends Serializable> T clone(T var0) {
      if (var0 == null) {
         return null;
      } else {
         byte[] var1 = serialize(var0);
         ByteArrayInputStream var2 = new ByteArrayInputStream(var1);

         try (SerializationUtils.ClassLoaderAwareObjectInputStream var3 = new SerializationUtils.ClassLoaderAwareObjectInputStream(
               var2, var0.getClass().getClassLoader()
            )) {
            return (T)var3.readObject();
         } catch (ClassNotFoundException var19) {
            throw new SerializationException("ClassNotFoundException while reading cloned object data", var19);
         } catch (IOException var20) {
            throw new SerializationException("IOException while reading or closing cloned object data", var20);
         }
      }
   }

   public static <T extends Serializable> T roundtrip(T var0) {
      return deserialize(serialize(var0));
   }

   public static void serialize(Serializable var0, OutputStream var1) {
      Validate.isTrue(var1 != null, "The OutputStream must not be null");

      try (ObjectOutputStream var2 = new ObjectOutputStream(var1)) {
         var2.writeObject(var0);
      } catch (IOException var15) {
         throw new SerializationException(var15);
      }
   }

   public static byte[] serialize(Serializable var0) {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream(512);
      serialize(var0, var1);
      return var1.toByteArray();
   }

   public static <T> T deserialize(InputStream var0) {
      Validate.isTrue(var0 != null, "The InputStream must not be null");

      try (ObjectInputStream var1 = new ObjectInputStream(var0)) {
         return (T)var1.readObject();
      } catch (IOException | ClassNotFoundException var16) {
         throw new SerializationException(var16);
      }
   }

   public static <T> T deserialize(byte[] var0) {
      Validate.isTrue(var0 != null, "The byte[] must not be null");
      return deserialize(new ByteArrayInputStream(var0));
   }

   static class ClassLoaderAwareObjectInputStream extends ObjectInputStream {
      private static final Map<String, Class<?>> primitiveTypes = new HashMap<>();
      private final ClassLoader classLoader;

      ClassLoaderAwareObjectInputStream(InputStream var1, ClassLoader var2) throws IOException {
         super(var1);
         this.classLoader = var2;
      }

      @Override
      protected Class<?> resolveClass(ObjectStreamClass var1) throws IOException, ClassNotFoundException {
         String var2 = var1.getName();

         try {
            return Class.forName(var2, false, this.classLoader);
         } catch (ClassNotFoundException var7) {
            try {
               return Class.forName(var2, false, Thread.currentThread().getContextClassLoader());
            } catch (ClassNotFoundException var6) {
               Class var5 = primitiveTypes.get(var2);
               if (var5 != null) {
                  return var5;
               } else {
                  throw var6;
               }
            }
         }
      }

      static {
         primitiveTypes.put("byte", byte.class);
         primitiveTypes.put("short", short.class);
         primitiveTypes.put("int", int.class);
         primitiveTypes.put("long", long.class);
         primitiveTypes.put("float", float.class);
         primitiveTypes.put("double", double.class);
         primitiveTypes.put("boolean", boolean.class);
         primitiveTypes.put("char", char.class);
         primitiveTypes.put("void", void.class);
      }
   }
}
