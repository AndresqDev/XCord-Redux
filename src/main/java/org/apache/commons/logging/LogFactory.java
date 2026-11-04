package org.apache.commons.logging;

import java.util.Hashtable;
import org.apache.commons.logging.impl.SLF4JLogFactory;

public abstract class LogFactory {
   static String UNSUPPORTED_OPERATION_IN_JCL_OVER_SLF4J = "http://www.slf4j.org/codes.html#unsupported_operation_in_jcl_over_slf4j";
   static LogFactory logFactory = new SLF4JLogFactory();
   public static final String PRIORITY_KEY = "priority";
   public static final String TCCL_KEY = "use_tccl";
   public static final String FACTORY_PROPERTY = "org.apache.commons.logging.LogFactory";
   public static final String FACTORY_DEFAULT = "org.apache.commons.logging.impl.SLF4JLogFactory";
   public static final String FACTORY_PROPERTIES = "commons-logging.properties";
   protected static final String SERVICE_ID = "META-INF/services/org.apache.commons.logging.LogFactory";
   public static final String DIAGNOSTICS_DEST_PROPERTY = "org.apache.commons.logging.diagnostics.dest";
   public static final String HASHTABLE_IMPLEMENTATION_PROPERTY = "org.apache.commons.logging.LogFactory.HashtableImpl";
   protected static Hashtable factories = null;
   protected static LogFactory nullClassLoaderFactory = null;

   protected LogFactory() {
   }

   public abstract Object getAttribute(String var1);

   public abstract String[] getAttributeNames();

   public abstract Log getInstance(Class var1) throws LogConfigurationException;

   public abstract Log getInstance(String var1) throws LogConfigurationException;

   public abstract void release();

   public abstract void removeAttribute(String var1);

   public abstract void setAttribute(String var1, Object var2);

   public static LogFactory getFactory() throws LogConfigurationException {
      return logFactory;
   }

   public static Log getLog(Class var0) throws LogConfigurationException {
      return getFactory().getInstance(var0);
   }

   public static Log getLog(String var0) throws LogConfigurationException {
      return getFactory().getInstance(var0);
   }

   public static void release(ClassLoader var0) {
   }

   public static void releaseAll() {
   }

   public static String objectId(Object var0) {
      return var0 == null ? "null" : var0.getClass().getName() + "@" + System.identityHashCode(var0);
   }

   protected static Object createFactory(String var0, ClassLoader var1) {
      throw new UnsupportedOperationException(
         "Operation [factoryClass] is not supported in jcl-over-slf4j. See also " + UNSUPPORTED_OPERATION_IN_JCL_OVER_SLF4J
      );
   }

   protected static ClassLoader directGetContextClassLoader() {
      throw new UnsupportedOperationException(
         "Operation [directGetContextClassLoader] is not supported in jcl-over-slf4j. See also " + UNSUPPORTED_OPERATION_IN_JCL_OVER_SLF4J
      );
   }

   protected static ClassLoader getContextClassLoader() throws LogConfigurationException {
      throw new UnsupportedOperationException(
         "Operation [getContextClassLoader] is not supported in jcl-over-slf4j. See also " + UNSUPPORTED_OPERATION_IN_JCL_OVER_SLF4J
      );
   }

   protected static ClassLoader getClassLoader(Class var0) {
      throw new UnsupportedOperationException(
         "Operation [getClassLoader] is not supported in jcl-over-slf4j. See also " + UNSUPPORTED_OPERATION_IN_JCL_OVER_SLF4J
      );
   }

   protected static boolean isDiagnosticsEnabled() {
      throw new UnsupportedOperationException(
         "Operation [isDiagnosticsEnabled] is not supported in jcl-over-slf4j. See also " + UNSUPPORTED_OPERATION_IN_JCL_OVER_SLF4J
      );
   }

   protected static void logRawDiagnostic(String var0) {
      throw new UnsupportedOperationException(
         "Operation [logRawDiagnostic] is not supported in jcl-over-slf4j. See also " + UNSUPPORTED_OPERATION_IN_JCL_OVER_SLF4J
      );
   }

   protected static LogFactory newFactory(String var0, ClassLoader var1, ClassLoader var2) {
      throw new UnsupportedOperationException(
         "Operation [logRawDiagnostic] is not supported in jcl-over-slf4j. See also " + UNSUPPORTED_OPERATION_IN_JCL_OVER_SLF4J
      );
   }

   protected static LogFactory newFactory(String var0, ClassLoader var1) {
      throw new UnsupportedOperationException("Operation [newFactory] is not supported in jcl-over-slf4j. See also " + UNSUPPORTED_OPERATION_IN_JCL_OVER_SLF4J);
   }
}
