package org.apache.logging.log4j;

import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.Supplier;

public interface LogBuilder {
   LogBuilder NOOP = new LogBuilder() {
   };

   default LogBuilder withMarker(Marker var1) {
      return this;
   }

   default LogBuilder withThrowable(Throwable var1) {
      return this;
   }

   default LogBuilder withLocation() {
      return this;
   }

   default LogBuilder withLocation(StackTraceElement var1) {
      return this;
   }

   default void log(CharSequence var1) {
   }

   default void log(String var1) {
   }

   default void log(String var1, Object... var2) {
   }

   default void log(String var1, Supplier<?>... var2) {
   }

   default void log(Message var1) {
   }

   default void log(Supplier<Message> var1) {
   }

   default void log(Object var1) {
   }

   default void log(String var1, Object var2) {
   }

   default void log(String var1, Object var2, Object var3) {
   }

   default void log(String var1, Object var2, Object var3, Object var4) {
   }

   default void log(String var1, Object var2, Object var3, Object var4, Object var5) {
   }

   default void log(String var1, Object var2, Object var3, Object var4, Object var5, Object var6) {
   }

   default void log(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
   }

   default void log(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
   }

   default void log(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
   }

   default void log(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
   }

   default void log(
      String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11
   ) {
   }

   default void log() {
   }
}
