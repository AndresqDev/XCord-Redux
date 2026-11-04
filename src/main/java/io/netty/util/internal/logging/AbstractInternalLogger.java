package io.netty.util.internal.logging;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.io.ObjectStreamException;
import java.io.Serializable;

public abstract class AbstractInternalLogger implements InternalLogger, Serializable {
   private static final long serialVersionUID = -6382972526573193470L;
   static final String EXCEPTION_MESSAGE = "Unexpected exception:";
   private final String name;

   protected AbstractInternalLogger(String var1) {
      this.name = ObjectUtil.checkNotNull(var1, "name");
   }

   @Override
   public String name() {
      return this.name;
   }

   @Override
   public boolean isEnabled(InternalLogLevel var1) {
      switch (var1) {
         case TRACE:
            return this.isTraceEnabled();
         case DEBUG:
            return this.isDebugEnabled();
         case INFO:
            return this.isInfoEnabled();
         case WARN:
            return this.isWarnEnabled();
         case ERROR:
            return this.isErrorEnabled();
         default:
            throw new Error();
      }
   }

   @Override
   public void trace(Throwable var1) {
      this.trace("Unexpected exception:", var1);
   }

   @Override
   public void debug(Throwable var1) {
      this.debug("Unexpected exception:", var1);
   }

   @Override
   public void info(Throwable var1) {
      this.info("Unexpected exception:", var1);
   }

   @Override
   public void warn(Throwable var1) {
      this.warn("Unexpected exception:", var1);
   }

   @Override
   public void error(Throwable var1) {
      this.error("Unexpected exception:", var1);
   }

   @Override
   public void log(InternalLogLevel var1, String var2, Throwable var3) {
      switch (var1) {
         case TRACE:
            this.trace(var2, var3);
            break;
         case DEBUG:
            this.debug(var2, var3);
            break;
         case INFO:
            this.info(var2, var3);
            break;
         case WARN:
            this.warn(var2, var3);
            break;
         case ERROR:
            this.error(var2, var3);
            break;
         default:
            throw new Error();
      }
   }

   @Override
   public void log(InternalLogLevel var1, Throwable var2) {
      switch (var1) {
         case TRACE:
            this.trace(var2);
            break;
         case DEBUG:
            this.debug(var2);
            break;
         case INFO:
            this.info(var2);
            break;
         case WARN:
            this.warn(var2);
            break;
         case ERROR:
            this.error(var2);
            break;
         default:
            throw new Error();
      }
   }

   @Override
   public void log(InternalLogLevel var1, String var2) {
      switch (var1) {
         case TRACE:
            this.trace(var2);
            break;
         case DEBUG:
            this.debug(var2);
            break;
         case INFO:
            this.info(var2);
            break;
         case WARN:
            this.warn(var2);
            break;
         case ERROR:
            this.error(var2);
            break;
         default:
            throw new Error();
      }
   }

   @Override
   public void log(InternalLogLevel var1, String var2, Object var3) {
      switch (var1) {
         case TRACE:
            this.trace(var2, var3);
            break;
         case DEBUG:
            this.debug(var2, var3);
            break;
         case INFO:
            this.info(var2, var3);
            break;
         case WARN:
            this.warn(var2, var3);
            break;
         case ERROR:
            this.error(var2, var3);
            break;
         default:
            throw new Error();
      }
   }

   @Override
   public void log(InternalLogLevel var1, String var2, Object var3, Object var4) {
      switch (var1) {
         case TRACE:
            this.trace(var2, var3, var4);
            break;
         case DEBUG:
            this.debug(var2, var3, var4);
            break;
         case INFO:
            this.info(var2, var3, var4);
            break;
         case WARN:
            this.warn(var2, var3, var4);
            break;
         case ERROR:
            this.error(var2, var3, var4);
            break;
         default:
            throw new Error();
      }
   }

   @Override
   public void log(InternalLogLevel var1, String var2, Object... var3) {
      switch (var1) {
         case TRACE:
            this.trace(var2, var3);
            break;
         case DEBUG:
            this.debug(var2, var3);
            break;
         case INFO:
            this.info(var2, var3);
            break;
         case WARN:
            this.warn(var2, var3);
            break;
         case ERROR:
            this.error(var2, var3);
            break;
         default:
            throw new Error();
      }
   }

   protected Object readResolve() throws ObjectStreamException {
      return InternalLoggerFactory.getInstance(this.name());
   }

   @Override
   public String toString() {
      return StringUtil.simpleClassName(this) + '(' + this.name() + ')';
   }
}
