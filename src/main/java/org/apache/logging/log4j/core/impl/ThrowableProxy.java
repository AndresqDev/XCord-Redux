package org.apache.logging.log4j.core.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import org.apache.logging.log4j.core.pattern.PlainTextRenderer;
import org.apache.logging.log4j.core.pattern.TextRenderer;
import org.apache.logging.log4j.util.StackLocatorUtil;

public class ThrowableProxy implements Serializable {
   static final ThrowableProxy[] EMPTY_ARRAY = new ThrowableProxy[0];
   private static final char EOL = '\n';
   private static final String EOL_STR = String.valueOf('\n');
   private static final long serialVersionUID = -2752771578252251910L;
   private final ThrowableProxy causeProxy;
   private int commonElementCount;
   private final ExtendedStackTraceElement[] extendedStackTrace;
   private final String localizedMessage;
   private final String message;
   private final String name;
   private final ThrowableProxy[] suppressedProxies;
   private final transient Throwable throwable;

   ThrowableProxy() {
      this.throwable = null;
      this.name = null;
      this.extendedStackTrace = ExtendedStackTraceElement.EMPTY_ARRAY;
      this.causeProxy = null;
      this.message = null;
      this.localizedMessage = null;
      this.suppressedProxies = EMPTY_ARRAY;
   }

   public ThrowableProxy(Throwable var1) {
      this(var1, null);
   }

   ThrowableProxy(Throwable var1, Set<Throwable> var2) {
      this.throwable = var1;
      this.name = var1.getClass().getName();
      this.message = var1.getMessage();
      this.localizedMessage = var1.getLocalizedMessage();
      HashMap var3 = new HashMap();
      Stack var4 = StackLocatorUtil.getCurrentStackTrace();
      this.extendedStackTrace = ThrowableProxyHelper.toExtendedStackTrace(this, var4, var3, null, var1.getStackTrace());
      Throwable var5 = var1.getCause();
      HashSet var6 = new HashSet(1);
      this.causeProxy = var5 == null ? null : new ThrowableProxy(var1, var4, var3, var5, var2, var6);
      this.suppressedProxies = ThrowableProxyHelper.toSuppressedProxies(var1, var2);
   }

   private ThrowableProxy(
      Throwable var1, Stack<Class<?>> var2, Map<String, ThrowableProxyHelper.CacheEntry> var3, Throwable var4, Set<Throwable> var5, Set<Throwable> var6
   ) {
      var6.add(var4);
      this.throwable = var4;
      this.name = var4.getClass().getName();
      this.message = this.throwable.getMessage();
      this.localizedMessage = this.throwable.getLocalizedMessage();
      this.extendedStackTrace = ThrowableProxyHelper.toExtendedStackTrace(this, var2, var3, var1.getStackTrace(), var4.getStackTrace());
      Throwable var7 = var4.getCause();
      this.causeProxy = var7 != null && !var6.contains(var7) ? new ThrowableProxy(var1, var2, var3, var7, var5, var6) : null;
      this.suppressedProxies = ThrowableProxyHelper.toSuppressedProxies(var4, var5);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         ThrowableProxy var2 = (ThrowableProxy)var1;
         if (!Objects.equals(this.causeProxy, var2.causeProxy)) {
            return false;
         } else if (this.commonElementCount != var2.commonElementCount) {
            return false;
         } else if (!Objects.equals(this.name, var2.name)) {
            return false;
         } else {
            return !Arrays.equals((Object[])this.extendedStackTrace, (Object[])var2.extendedStackTrace)
               ? false
               : Arrays.equals((Object[])this.suppressedProxies, (Object[])var2.suppressedProxies);
         }
      }
   }

   public void formatWrapper(StringBuilder var1, ThrowableProxy var2, String var3) {
      this.formatWrapper(var1, var2, null, PlainTextRenderer.getInstance(), var3);
   }

   public void formatWrapper(StringBuilder var1, ThrowableProxy var2, List<String> var3, String var4) {
      this.formatWrapper(var1, var2, var3, PlainTextRenderer.getInstance(), var4);
   }

   public void formatWrapper(StringBuilder var1, ThrowableProxy var2, List<String> var3, TextRenderer var4, String var5) {
      this.formatWrapper(var1, var2, var3, var4, var5, EOL_STR);
   }

   public void formatWrapper(StringBuilder var1, ThrowableProxy var2, List<String> var3, TextRenderer var4, String var5, String var6) {
      ThrowableProxyRenderer.formatWrapper(var1, var2, var3, var4, var5, var6);
   }

   public ThrowableProxy getCauseProxy() {
      return this.causeProxy;
   }

   public String getCauseStackTraceAsString(String var1) {
      return this.getCauseStackTraceAsString(null, PlainTextRenderer.getInstance(), var1, EOL_STR);
   }

   public String getCauseStackTraceAsString(List<String> var1, String var2) {
      return this.getCauseStackTraceAsString(var1, PlainTextRenderer.getInstance(), var2, EOL_STR);
   }

   public String getCauseStackTraceAsString(List<String> var1, TextRenderer var2, String var3) {
      return this.getCauseStackTraceAsString(var1, var2, var3, EOL_STR);
   }

   public String getCauseStackTraceAsString(List<String> var1, TextRenderer var2, String var3, String var4) {
      StringBuilder var5 = new StringBuilder();
      ThrowableProxyRenderer.formatCauseStackTrace(this, var5, var1, var2, var3, var4);
      return var5.toString();
   }

   public int getCommonElementCount() {
      return this.commonElementCount;
   }

   void setCommonElementCount(int var1) {
      this.commonElementCount = var1;
   }

   public ExtendedStackTraceElement[] getExtendedStackTrace() {
      return this.extendedStackTrace;
   }

   public String getExtendedStackTraceAsString() {
      return this.getExtendedStackTraceAsString(null, PlainTextRenderer.getInstance(), "", EOL_STR);
   }

   public String getExtendedStackTraceAsString(String var1) {
      return this.getExtendedStackTraceAsString(null, PlainTextRenderer.getInstance(), var1, EOL_STR);
   }

   public String getExtendedStackTraceAsString(List<String> var1, String var2) {
      return this.getExtendedStackTraceAsString(var1, PlainTextRenderer.getInstance(), var2, EOL_STR);
   }

   public String getExtendedStackTraceAsString(List<String> var1, TextRenderer var2, String var3) {
      return this.getExtendedStackTraceAsString(var1, var2, var3, EOL_STR);
   }

   public String getExtendedStackTraceAsString(List<String> var1, TextRenderer var2, String var3, String var4) {
      StringBuilder var5 = new StringBuilder(1024);
      this.formatExtendedStackTraceTo(var5, var1, var2, var3, var4);
      return var5.toString();
   }

   public void formatExtendedStackTraceTo(StringBuilder var1, List<String> var2, TextRenderer var3, String var4, String var5) {
      ThrowableProxyRenderer.formatExtendedStackTraceTo(this, var1, var2, var3, var4, var5);
   }

   public String getLocalizedMessage() {
      return this.localizedMessage;
   }

   public String getMessage() {
      return this.message;
   }

   public String getName() {
      return this.name;
   }

   public StackTraceElement[] getStackTrace() {
      return this.throwable == null ? null : this.throwable.getStackTrace();
   }

   public ThrowableProxy[] getSuppressedProxies() {
      return this.suppressedProxies;
   }

   public String getSuppressedStackTrace(String var1) {
      ThrowableProxy[] var2 = this.getSuppressedProxies();
      if (var2 != null && var2.length != 0) {
         StringBuilder var3 = new StringBuilder("Suppressed Stack Trace Elements:").append('\n');

         for (ThrowableProxy var7 : var2) {
            var3.append(var7.getExtendedStackTraceAsString(var1));
         }

         return var3.toString();
      } else {
         return "";
      }
   }

   public Throwable getThrowable() {
      return this.throwable;
   }

   @Override
   public int hashCode() {
      byte var1 = 31;
      int var2 = 1;
      var2 = 31 * var2 + (this.causeProxy == null ? 0 : this.causeProxy.hashCode());
      var2 = 31 * var2 + this.commonElementCount;
      var2 = 31 * var2 + (this.extendedStackTrace == null ? 0 : Arrays.hashCode((Object[])this.extendedStackTrace));
      var2 = 31 * var2 + (this.suppressedProxies == null ? 0 : Arrays.hashCode((Object[])this.suppressedProxies));
      return 31 * var2 + (this.name == null ? 0 : this.name.hashCode());
   }

   @Override
   public String toString() {
      String var1 = this.message;
      return var1 != null ? this.name + ": " + var1 : this.name;
   }
}
