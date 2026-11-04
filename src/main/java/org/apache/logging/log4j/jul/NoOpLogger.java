package org.apache.logging.log4j.jul;

import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class NoOpLogger extends Logger {
   protected NoOpLogger(String var1) {
      super(var1, null);
   }

   @Override
   public void log(LogRecord var1) {
   }

   @Override
   public void log(Level var1, String var2) {
   }

   @Override
   public void log(Level var1, Supplier<String> var2) {
   }

   @Override
   public void log(Level var1, String var2, Object var3) {
   }

   @Override
   public void log(Level var1, String var2, Object[] var3) {
   }

   @Override
   public void log(Level var1, String var2, Throwable var3) {
   }

   @Override
   public void log(Level var1, Throwable var2, Supplier<String> var3) {
   }

   @Override
   public void logp(Level var1, String var2, String var3, String var4) {
   }

   @Override
   public void logp(Level var1, String var2, String var3, Supplier<String> var4) {
   }

   @Override
   public void logp(Level var1, String var2, String var3, String var4, Object var5) {
   }

   @Override
   public void logp(Level var1, String var2, String var3, String var4, Object[] var5) {
   }

   @Override
   public void logp(Level var1, String var2, String var3, String var4, Throwable var5) {
   }

   @Override
   public void logp(Level var1, String var2, String var3, Throwable var4, Supplier<String> var5) {
   }

   @Override
   public void logrb(Level var1, String var2, String var3, String var4, String var5) {
   }

   @Override
   public void logrb(Level var1, String var2, String var3, String var4, String var5, Object var6) {
   }

   @Override
   public void logrb(Level var1, String var2, String var3, String var4, String var5, Object[] var6) {
   }

   @Override
   public void logrb(Level var1, String var2, String var3, ResourceBundle var4, String var5, Object... var6) {
   }

   @Override
   public void logrb(Level var1, String var2, String var3, String var4, String var5, Throwable var6) {
   }

   @Override
   public void logrb(Level var1, String var2, String var3, ResourceBundle var4, String var5, Throwable var6) {
   }

   @Override
   public void entering(String var1, String var2) {
   }

   @Override
   public void entering(String var1, String var2, Object var3) {
   }

   @Override
   public void entering(String var1, String var2, Object[] var3) {
   }

   @Override
   public void exiting(String var1, String var2) {
   }

   @Override
   public void exiting(String var1, String var2, Object var3) {
   }

   @Override
   public void throwing(String var1, String var2, Throwable var3) {
   }

   @Override
   public void severe(String var1) {
   }

   @Override
   public void warning(String var1) {
   }

   @Override
   public void info(String var1) {
   }

   @Override
   public void config(String var1) {
   }

   @Override
   public void fine(String var1) {
   }

   @Override
   public void finer(String var1) {
   }

   @Override
   public void finest(String var1) {
   }

   @Override
   public void severe(Supplier<String> var1) {
   }

   @Override
   public void warning(Supplier<String> var1) {
   }

   @Override
   public void info(Supplier<String> var1) {
   }

   @Override
   public void config(Supplier<String> var1) {
   }

   @Override
   public void fine(Supplier<String> var1) {
   }

   @Override
   public void finer(Supplier<String> var1) {
   }

   @Override
   public void finest(Supplier<String> var1) {
   }

   @Override
   public void setLevel(Level var1) throws SecurityException {
   }

   @Override
   public Level getLevel() {
      return Level.OFF;
   }

   @Override
   public boolean isLoggable(Level var1) {
      return false;
   }
}
