package org.apache.logging.log4j.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FilteredObjectInputStream extends ObjectInputStream {
   private static final Set<String> REQUIRED_JAVA_CLASSES = new HashSet<>(
      Arrays.asList("java.math.BigDecimal", "java.math.BigInteger", "java.rmi.MarshalledObject", "[B")
   );
   private static final Set<String> REQUIRED_JAVA_PACKAGES = new HashSet<>(
      Arrays.asList("java.lang.", "java.time.", "java.util.", "org.apache.logging.log4j.", "[Lorg.apache.logging.log4j.")
   );
   private final Collection<String> allowedExtraClasses;

   public FilteredObjectInputStream() throws IOException, SecurityException {
      this.allowedExtraClasses = Collections.emptySet();
   }

   public FilteredObjectInputStream(InputStream var1) throws IOException {
      super(var1);
      this.allowedExtraClasses = Collections.emptySet();
   }

   public FilteredObjectInputStream(Collection<String> var1) throws IOException, SecurityException {
      this.allowedExtraClasses = var1;
   }

   public FilteredObjectInputStream(InputStream var1, Collection<String> var2) throws IOException {
      super(var1);
      this.allowedExtraClasses = var2;
   }

   public Collection<String> getAllowedClasses() {
      return this.allowedExtraClasses;
   }

   @Override
   protected Class<?> resolveClass(ObjectStreamClass var1) throws IOException, ClassNotFoundException {
      String var2 = var1.getName();
      if (!isAllowedByDefault(var2) && !this.allowedExtraClasses.contains(var2)) {
         throw new InvalidObjectException("Class is not allowed for deserialization: " + var2);
      } else {
         return super.resolveClass(var1);
      }
   }

   private static boolean isAllowedByDefault(String var0) {
      return isRequiredPackage(var0) || REQUIRED_JAVA_CLASSES.contains(var0);
   }

   private static boolean isRequiredPackage(String var0) {
      for (String var2 : REQUIRED_JAVA_PACKAGES) {
         if (var0.startsWith(var2)) {
            return true;
         }
      }

      return false;
   }
}
