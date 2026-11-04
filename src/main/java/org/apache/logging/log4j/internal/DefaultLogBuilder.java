package org.apache.logging.log4j.internal;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogBuilder;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LambdaUtil;
import org.apache.logging.log4j.util.StackLocatorUtil;
import org.apache.logging.log4j.util.Supplier;

public class DefaultLogBuilder implements LogBuilder {
   private static Message EMPTY_MESSAGE = new SimpleMessage("");
   private static final String FQCN = DefaultLogBuilder.class.getName();
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final Logger logger;
   private Level level;
   private Marker marker;
   private Throwable throwable;
   private StackTraceElement location;
   private volatile boolean inUse;
   private long threadId;

   public DefaultLogBuilder(Logger var1, Level var2) {
      this.logger = var1;
      this.level = var2;
      this.threadId = Thread.currentThread().getId();
      this.inUse = true;
   }

   public DefaultLogBuilder(Logger var1) {
      this.logger = var1;
      this.inUse = false;
      this.threadId = Thread.currentThread().getId();
   }

   public LogBuilder reset(Level var1) {
      this.inUse = true;
      this.level = var1;
      this.marker = null;
      this.throwable = null;
      this.location = null;
      return this;
   }

   @Override
   public LogBuilder withMarker(Marker var1) {
      this.marker = var1;
      return this;
   }

   @Override
   public LogBuilder withThrowable(Throwable var1) {
      this.throwable = var1;
      return this;
   }

   @Override
   public LogBuilder withLocation() {
      this.location = StackLocatorUtil.getStackTraceElement(2);
      return this;
   }

   @Override
   public LogBuilder withLocation(StackTraceElement var1) {
      this.location = var1;
      return this;
   }

   public boolean isInUse() {
      return this.inUse;
   }

   @Override
   public void log(Message var1) {
      if (this.isValid()) {
         this.logMessage(var1);
      }
   }

   @Override
   public void log(CharSequence var1) {
      if (this.isValid()) {
         this.logMessage(this.logger.<MessageFactory>getMessageFactory().newMessage(var1));
      }
   }

   @Override
   public void log(String var1) {
      if (this.isValid()) {
         this.logMessage(this.logger.<MessageFactory>getMessageFactory().newMessage(var1));
      }
   }

   @Override
   public void log(String var1, Object... var2) {
      if (this.isValid()) {
         this.logMessage(this.logger.<MessageFactory>getMessageFactory().newMessage(var1, var2));
      }
   }

   @Override
   public void log(String var1, Supplier<?>... var2) {
      if (this.isValid()) {
         this.logMessage(this.logger.<MessageFactory>getMessageFactory().newMessage(var1, LambdaUtil.getAll(var2)));
      }
   }

   @Override
   public void log(Supplier<Message> var1) {
      if (this.isValid()) {
         this.logMessage((Message)var1.get());
      }
   }

   @Override
   public void log(Object var1) {
      if (this.isValid()) {
         this.logMessage(this.logger.<MessageFactory>getMessageFactory().newMessage(var1));
      }
   }

   @Override
   public void log(String var1, Object var2) {
      if (this.isValid()) {
         this.logMessage(this.logger.<MessageFactory>getMessageFactory().newMessage(var1, var2));
      }
   }

   @Override
   public void log(String var1, Object var2, Object var3) {
      if (this.isValid()) {
         this.logMessage(this.logger.<MessageFactory>getMessageFactory().newMessage(var1, var2, var3));
      }
   }

   @Override
   public void log(String var1, Object var2, Object var3, Object var4) {
      if (this.isValid()) {
         this.logMessage(this.logger.<MessageFactory>getMessageFactory().newMessage(var1, var2, var3, var4));
      }
   }

   @Override
   public void log(String var1, Object var2, Object var3, Object var4, Object var5) {
      if (this.isValid()) {
         this.logMessage(this.logger.<MessageFactory>getMessageFactory().newMessage(var1, var2, var3, var4, var5));
      }
   }

   @Override
   public void log(String var1, Object var2, Object var3, Object var4, Object var5, Object var6) {
      if (this.isValid()) {
         this.logMessage(this.logger.<MessageFactory>getMessageFactory().newMessage(var1, var2, var3, var4, var5, var6));
      }
   }

   @Override
   public void log(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      if (this.isValid()) {
         this.logMessage(this.logger.<MessageFactory>getMessageFactory().newMessage(var1, var2, var3, var4, var5, var6, var7));
      }
   }

   @Override
   public void log(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      if (this.isValid()) {
         this.logMessage(this.logger.<MessageFactory>getMessageFactory().newMessage(var1, var2, var3, var4, var5, var6, var7, var8));
      }
   }

   @Override
   public void log(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      if (this.isValid()) {
         this.logMessage(this.logger.<MessageFactory>getMessageFactory().newMessage(var1, var2, var3, var4, var5, var6, var7, var8, var9));
      }
   }

   @Override
   public void log(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      if (this.isValid()) {
         this.logMessage(this.logger.<MessageFactory>getMessageFactory().newMessage(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10));
      }
   }

   @Override
   public void log(
      String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11
   ) {
      if (this.isValid()) {
         this.logMessage(this.logger.<MessageFactory>getMessageFactory().newMessage(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11));
      }
   }

   @Override
   public void log() {
      if (this.isValid()) {
         this.logMessage(EMPTY_MESSAGE);
      }
   }

   private void logMessage(Message var1) {
      try {
         this.logger.logMessage(this.level, this.marker, FQCN, this.location, var1, this.throwable);
      } finally {
         this.inUse = false;
      }
   }

   private boolean isValid() {
      if (!this.inUse) {
         LOGGER.warn("Attempt to reuse LogBuilder was ignored. {}", StackLocatorUtil.getCallerClass(2));
         return false;
      } else if (this.threadId != Thread.currentThread().getId()) {
         LOGGER.warn("LogBuilder can only be used on the owning thread. {}", StackLocatorUtil.getCallerClass(2));
         return false;
      } else {
         return true;
      }
   }
}
