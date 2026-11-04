package org.apache.logging.log4j.spi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogBuilder;
import org.apache.logging.log4j.LoggingException;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.internal.DefaultLogBuilder;
import org.apache.logging.log4j.message.DefaultFlowMessageFactory;
import org.apache.logging.log4j.message.EntryMessage;
import org.apache.logging.log4j.message.FlowMessageFactory;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.MessageFactory2;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;
import org.apache.logging.log4j.message.ReusableMessageFactory;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Constants;
import org.apache.logging.log4j.util.LambdaUtil;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.MessageSupplier;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.StackLocatorUtil;
import org.apache.logging.log4j.util.Strings;
import org.apache.logging.log4j.util.Supplier;

public abstract class AbstractLogger implements Serializable, ExtendedLogger, LocationAwareLogger {
   public static final Marker FLOW_MARKER = MarkerManager.getMarker("FLOW");
   public static final Marker ENTRY_MARKER = MarkerManager.getMarker("ENTER").setParents(FLOW_MARKER);
   public static final Marker EXIT_MARKER = MarkerManager.getMarker("EXIT").setParents(FLOW_MARKER);
   public static final Marker EXCEPTION_MARKER = MarkerManager.getMarker("EXCEPTION");
   public static final Marker THROWING_MARKER = MarkerManager.getMarker("THROWING").setParents(EXCEPTION_MARKER);
   public static final Marker CATCHING_MARKER = MarkerManager.getMarker("CATCHING").setParents(EXCEPTION_MARKER);
   public static final Class<? extends MessageFactory> DEFAULT_MESSAGE_FACTORY_CLASS = createClassForProperty(
      "log4j2.messageFactory", ReusableMessageFactory.class, ParameterizedMessageFactory.class
   );
   public static final Class<? extends FlowMessageFactory> DEFAULT_FLOW_MESSAGE_FACTORY_CLASS = createFlowClassForProperty(
      "log4j2.flowMessageFactory", DefaultFlowMessageFactory.class
   );
   private static final long serialVersionUID = 2L;
   private static final String FQCN = AbstractLogger.class.getName();
   private static final String THROWING = "Throwing";
   private static final String CATCHING = "Catching";
   protected final String name;
   private final MessageFactory2 messageFactory;
   private final FlowMessageFactory flowMessageFactory;
   private static final ThreadLocal<int[]> recursionDepthHolder = new ThreadLocal<>();
   protected final transient ThreadLocal<DefaultLogBuilder> logBuilder;

   public AbstractLogger() {
      this.name = this.getClass().getName();
      this.messageFactory = createDefaultMessageFactory();
      this.flowMessageFactory = createDefaultFlowMessageFactory();
      this.logBuilder = new AbstractLogger.LocalLogBuilder(this);
   }

   public AbstractLogger(String var1) {
      this(var1, createDefaultMessageFactory());
   }

   public AbstractLogger(String var1, MessageFactory var2) {
      this.name = var1;
      this.messageFactory = var2 == null ? createDefaultMessageFactory() : narrow(var2);
      this.flowMessageFactory = createDefaultFlowMessageFactory();
      this.logBuilder = new AbstractLogger.LocalLogBuilder(this);
   }

   public static void checkMessageFactory(ExtendedLogger var0, MessageFactory var1) {
      String var2 = var0.getName();
      MessageFactory var3 = var0.getMessageFactory();
      if (var1 != null && !var3.equals(var1)) {
         StatusLogger.getLogger()
            .warn(
               "The Logger {} was created with the message factory {} and is now requested with the message factory {}, which may create log events with unexpected formatting.",
               var2,
               var3,
               var1
            );
      } else if (var1 == null && !var3.getClass().equals(DEFAULT_MESSAGE_FACTORY_CLASS)) {
         StatusLogger.getLogger()
            .warn(
               "The Logger {} was created with the message factory {} and is now requested with a null message factory (defaults to {}), which may create log events with unexpected formatting.",
               var2,
               var3,
               DEFAULT_MESSAGE_FACTORY_CLASS.getName()
            );
      }
   }

   @Override
   public void catching(Level var1, Throwable var2) {
      this.catching(FQCN, var1, var2);
   }

   protected void catching(String var1, Level var2, Throwable var3) {
      if (this.isEnabled(var2, CATCHING_MARKER, null, null)) {
         this.logMessageSafely(var1, var2, CATCHING_MARKER, this.catchingMsg(var3), var3);
      }
   }

   @Override
   public void catching(Throwable var1) {
      if (this.isEnabled(Level.ERROR, CATCHING_MARKER, null, null)) {
         this.logMessageSafely(FQCN, Level.ERROR, CATCHING_MARKER, this.catchingMsg(var1), var1);
      }
   }

   protected Message catchingMsg(Throwable var1) {
      return this.messageFactory.newMessage("Catching");
   }

   private static Class<? extends MessageFactory> createClassForProperty(
      String var0, Class<ReusableMessageFactory> var1, Class<ParameterizedMessageFactory> var2
   ) {
      try {
         String var3 = Constants.ENABLE_THREADLOCALS ? var1.getName() : var2.getName();
         String var4 = PropertiesUtil.getProperties().getStringProperty(var0, var3);
         return LoaderUtil.loadClass(var4).asSubclass(MessageFactory.class);
      } catch (Throwable var5) {
         return var2;
      }
   }

   private static Class<? extends FlowMessageFactory> createFlowClassForProperty(String var0, Class<DefaultFlowMessageFactory> var1) {
      try {
         String var2 = PropertiesUtil.getProperties().getStringProperty(var0, var1.getName());
         return LoaderUtil.loadClass(var2).asSubclass(FlowMessageFactory.class);
      } catch (Throwable var3) {
         return var1;
      }
   }

   private static MessageFactory2 createDefaultMessageFactory() {
      try {
         MessageFactory var0 = DEFAULT_MESSAGE_FACTORY_CLASS.newInstance();
         return narrow(var0);
      } catch (IllegalAccessException | InstantiationException var1) {
         throw new IllegalStateException(var1);
      }
   }

   private static MessageFactory2 narrow(MessageFactory var0) {
      return (MessageFactory2)(var0 instanceof MessageFactory2 ? (MessageFactory2)var0 : new MessageFactory2Adapter(var0));
   }

   private static FlowMessageFactory createDefaultFlowMessageFactory() {
      try {
         return DEFAULT_FLOW_MESSAGE_FACTORY_CLASS.newInstance();
      } catch (IllegalAccessException | InstantiationException var1) {
         throw new IllegalStateException(var1);
      }
   }

   @Override
   public void debug(Marker var1, CharSequence var2) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, null);
   }

   @Override
   public void debug(Marker var1, CharSequence var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3);
   }

   @Override
   public void debug(Marker var1, Message var2) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var2 != null ? var2.getThrowable() : null);
   }

   @Override
   public void debug(Marker var1, Message var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3);
   }

   @Override
   public void debug(Marker var1, Object var2) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, null);
   }

   @Override
   public void debug(Marker var1, Object var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3);
   }

   @Override
   public void debug(Marker var1, String var2) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, (Throwable)null);
   }

   @Override
   public void debug(Marker var1, String var2, Object... var3) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3);
   }

   @Override
   public void debug(Marker var1, String var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3);
   }

   @Override
   public void debug(Message var1) {
      this.logIfEnabled(FQCN, Level.DEBUG, null, var1, var1 != null ? var1.getThrowable() : null);
   }

   @Override
   public void debug(Message var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.DEBUG, null, var1, var2);
   }

   @Override
   public void debug(CharSequence var1) {
      this.logIfEnabled(FQCN, Level.DEBUG, null, var1, null);
   }

   @Override
   public void debug(CharSequence var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.DEBUG, null, var1, var2);
   }

   @Override
   public void debug(Object var1) {
      this.logIfEnabled(FQCN, Level.DEBUG, null, var1, null);
   }

   @Override
   public void debug(Object var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.DEBUG, null, var1, var2);
   }

   @Override
   public void debug(String var1) {
      this.logIfEnabled(FQCN, Level.DEBUG, null, var1, (Throwable)null);
   }

   @Override
   public void debug(String var1, Object... var2) {
      this.logIfEnabled(FQCN, Level.DEBUG, null, var1, var2);
   }

   @Override
   public void debug(String var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.DEBUG, null, var1, var2);
   }

   @Override
   public void debug(Supplier<?> var1) {
      this.logIfEnabled(FQCN, Level.DEBUG, null, var1, (Throwable)null);
   }

   @Override
   public void debug(Supplier<?> var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.DEBUG, null, var1, var2);
   }

   @Override
   public void debug(Marker var1, Supplier<?> var2) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, (Throwable)null);
   }

   @Override
   public void debug(Marker var1, String var2, Supplier<?>... var3) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3);
   }

   @Override
   public void debug(Marker var1, Supplier<?> var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3);
   }

   @Override
   public void debug(String var1, Supplier<?>... var2) {
      this.logIfEnabled(FQCN, Level.DEBUG, null, var1, var2);
   }

   @Override
   public void debug(Marker var1, MessageSupplier var2) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, (Throwable)null);
   }

   @Override
   public void debug(Marker var1, MessageSupplier var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3);
   }

   @Override
   public void debug(MessageSupplier var1) {
      this.logIfEnabled(FQCN, Level.DEBUG, null, var1, (Throwable)null);
   }

   @Override
   public void debug(MessageSupplier var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.DEBUG, null, var1, var2);
   }

   @Override
   public void debug(Marker var1, String var2, Object var3) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3);
   }

   @Override
   public void debug(Marker var1, String var2, Object var3, Object var4) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3, var4);
   }

   @Override
   public void debug(Marker var1, String var2, Object var3, Object var4, Object var5) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3, var4, var5);
   }

   @Override
   public void debug(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void debug(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3, var4, var5, var6, var7);
   }

   @Override
   public void debug(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void debug(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   @Override
   public void debug(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   @Override
   public void debug(
      Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11
   ) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   @Override
   public void debug(
      Marker var1,
      String var2,
      Object var3,
      Object var4,
      Object var5,
      Object var6,
      Object var7,
      Object var8,
      Object var9,
      Object var10,
      Object var11,
      Object var12
   ) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
   }

   @Override
   public void debug(String var1, Object var2) {
      this.logIfEnabled(FQCN, Level.DEBUG, null, var1, var2);
   }

   @Override
   public void debug(String var1, Object var2, Object var3) {
      this.logIfEnabled(FQCN, Level.DEBUG, null, var1, var2, var3);
   }

   @Override
   public void debug(String var1, Object var2, Object var3, Object var4) {
      this.logIfEnabled(FQCN, Level.DEBUG, null, var1, var2, var3, var4);
   }

   @Override
   public void debug(String var1, Object var2, Object var3, Object var4, Object var5) {
      this.logIfEnabled(FQCN, Level.DEBUG, null, var1, var2, var3, var4, var5);
   }

   @Override
   public void debug(String var1, Object var2, Object var3, Object var4, Object var5, Object var6) {
      this.logIfEnabled(FQCN, Level.DEBUG, null, var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void debug(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      this.logIfEnabled(FQCN, Level.DEBUG, null, var1, var2, var3, var4, var5, var6, var7);
   }

   @Override
   public void debug(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      this.logIfEnabled(FQCN, Level.DEBUG, null, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void debug(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      this.logIfEnabled(FQCN, Level.DEBUG, null, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   @Override
   public void debug(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      this.logIfEnabled(FQCN, Level.DEBUG, null, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   @Override
   public void debug(
      String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11
   ) {
      this.logIfEnabled(FQCN, Level.DEBUG, null, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   protected EntryMessage enter(String var1, String var2, Supplier<?>... var3) {
      EntryMessage var4 = null;
      if (this.isEnabled(Level.TRACE, ENTRY_MARKER, null, null)) {
         this.logMessageSafely(var1, Level.TRACE, ENTRY_MARKER, var4 = this.entryMsg(var2, var3), null);
      }

      return var4;
   }

   @Deprecated
   protected EntryMessage enter(String var1, String var2, MessageSupplier... var3) {
      EntryMessage var4 = null;
      if (this.isEnabled(Level.TRACE, ENTRY_MARKER, null, null)) {
         this.logMessageSafely(var1, Level.TRACE, ENTRY_MARKER, var4 = this.entryMsg(var2, var3), null);
      }

      return var4;
   }

   protected EntryMessage enter(String var1, String var2, Object... var3) {
      EntryMessage var4 = null;
      if (this.isEnabled(Level.TRACE, ENTRY_MARKER, null, null)) {
         this.logMessageSafely(var1, Level.TRACE, ENTRY_MARKER, var4 = this.entryMsg(var2, var3), null);
      }

      return var4;
   }

   @Deprecated
   protected EntryMessage enter(String var1, MessageSupplier var2) {
      EntryMessage var3 = null;
      if (this.isEnabled(Level.TRACE, ENTRY_MARKER, null, null)) {
         this.logMessageSafely(var1, Level.TRACE, ENTRY_MARKER, var3 = this.flowMessageFactory.newEntryMessage(var2.get()), null);
      }

      return var3;
   }

   protected EntryMessage enter(String var1, Message var2) {
      EntryMessage var3 = null;
      if (this.isEnabled(Level.TRACE, ENTRY_MARKER, null, null)) {
         this.logMessageSafely(var1, Level.TRACE, ENTRY_MARKER, var3 = this.flowMessageFactory.newEntryMessage(var2), null);
      }

      return var3;
   }

   @Deprecated
   @Override
   public void entry() {
      this.entry(FQCN, (Object[])null);
   }

   @Override
   public void entry(Object... var1) {
      this.entry(FQCN, var1);
   }

   protected void entry(String var1, Object... var2) {
      if (this.isEnabled(Level.TRACE, ENTRY_MARKER, null, null)) {
         if (var2 == null) {
            this.logMessageSafely(var1, Level.TRACE, ENTRY_MARKER, this.entryMsg(null, (Supplier<?>[])null), null);
         } else {
            this.logMessageSafely(var1, Level.TRACE, ENTRY_MARKER, this.entryMsg(null, var2), null);
         }
      }
   }

   protected EntryMessage entryMsg(String var1, Object... var2) {
      int var3 = var2 == null ? 0 : var2.length;
      if (var3 == 0) {
         return Strings.isEmpty(var1) ? this.flowMessageFactory.newEntryMessage(null) : this.flowMessageFactory.newEntryMessage(new SimpleMessage(var1));
      } else if (var1 != null) {
         return this.flowMessageFactory.newEntryMessage(new ParameterizedMessage(var1, var2));
      } else {
         StringBuilder var4 = new StringBuilder();
         var4.append("params(");

         for (int var5 = 0; var5 < var3; var5++) {
            if (var5 > 0) {
               var4.append(", ");
            }

            Object var6 = var2[var5];
            var4.append(var6 instanceof Message ? ((Message)var6).getFormattedMessage() : String.valueOf(var6));
         }

         var4.append(')');
         return this.flowMessageFactory.newEntryMessage(new SimpleMessage(var4));
      }
   }

   protected EntryMessage entryMsg(String var1, MessageSupplier... var2) {
      int var3 = var2 == null ? 0 : var2.length;
      Object[] var4 = new Object[var3];

      for (int var5 = 0; var5 < var3; var5++) {
         var4[var5] = var2[var5].get();
         var4[var5] = var4[var5] != null ? ((Message)var4[var5]).getFormattedMessage() : null;
      }

      return this.entryMsg(var1, var4);
   }

   protected EntryMessage entryMsg(String var1, Supplier<?>... var2) {
      int var3 = var2 == null ? 0 : var2.length;
      Object[] var4 = new Object[var3];

      for (int var5 = 0; var5 < var3; var5++) {
         var4[var5] = var2[var5].get();
         if (var4[var5] instanceof Message) {
            var4[var5] = ((Message)var4[var5]).getFormattedMessage();
         }
      }

      return this.entryMsg(var1, var4);
   }

   @Override
   public void error(Marker var1, Message var2) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var2 != null ? var2.getThrowable() : null);
   }

   @Override
   public void error(Marker var1, Message var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3);
   }

   @Override
   public void error(Marker var1, CharSequence var2) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, null);
   }

   @Override
   public void error(Marker var1, CharSequence var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3);
   }

   @Override
   public void error(Marker var1, Object var2) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, null);
   }

   @Override
   public void error(Marker var1, Object var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3);
   }

   @Override
   public void error(Marker var1, String var2) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, (Throwable)null);
   }

   @Override
   public void error(Marker var1, String var2, Object... var3) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3);
   }

   @Override
   public void error(Marker var1, String var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3);
   }

   @Override
   public void error(Message var1) {
      this.logIfEnabled(FQCN, Level.ERROR, null, var1, var1 != null ? var1.getThrowable() : null);
   }

   @Override
   public void error(Message var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.ERROR, null, var1, var2);
   }

   @Override
   public void error(CharSequence var1) {
      this.logIfEnabled(FQCN, Level.ERROR, null, var1, null);
   }

   @Override
   public void error(CharSequence var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.ERROR, null, var1, var2);
   }

   @Override
   public void error(Object var1) {
      this.logIfEnabled(FQCN, Level.ERROR, null, var1, null);
   }

   @Override
   public void error(Object var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.ERROR, null, var1, var2);
   }

   @Override
   public void error(String var1) {
      this.logIfEnabled(FQCN, Level.ERROR, null, var1, (Throwable)null);
   }

   @Override
   public void error(String var1, Object... var2) {
      this.logIfEnabled(FQCN, Level.ERROR, null, var1, var2);
   }

   @Override
   public void error(String var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.ERROR, null, var1, var2);
   }

   @Override
   public void error(Supplier<?> var1) {
      this.logIfEnabled(FQCN, Level.ERROR, null, var1, (Throwable)null);
   }

   @Override
   public void error(Supplier<?> var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.ERROR, null, var1, var2);
   }

   @Override
   public void error(Marker var1, Supplier<?> var2) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, (Throwable)null);
   }

   @Override
   public void error(Marker var1, String var2, Supplier<?>... var3) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3);
   }

   @Override
   public void error(Marker var1, Supplier<?> var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3);
   }

   @Override
   public void error(String var1, Supplier<?>... var2) {
      this.logIfEnabled(FQCN, Level.ERROR, null, var1, var2);
   }

   @Override
   public void error(Marker var1, MessageSupplier var2) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, (Throwable)null);
   }

   @Override
   public void error(Marker var1, MessageSupplier var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3);
   }

   @Override
   public void error(MessageSupplier var1) {
      this.logIfEnabled(FQCN, Level.ERROR, null, var1, (Throwable)null);
   }

   @Override
   public void error(MessageSupplier var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.ERROR, null, var1, var2);
   }

   @Override
   public void error(Marker var1, String var2, Object var3) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3);
   }

   @Override
   public void error(Marker var1, String var2, Object var3, Object var4) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3, var4);
   }

   @Override
   public void error(Marker var1, String var2, Object var3, Object var4, Object var5) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3, var4, var5);
   }

   @Override
   public void error(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void error(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3, var4, var5, var6, var7);
   }

   @Override
   public void error(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void error(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   @Override
   public void error(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   @Override
   public void error(
      Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11
   ) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   @Override
   public void error(
      Marker var1,
      String var2,
      Object var3,
      Object var4,
      Object var5,
      Object var6,
      Object var7,
      Object var8,
      Object var9,
      Object var10,
      Object var11,
      Object var12
   ) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
   }

   @Override
   public void error(String var1, Object var2) {
      this.logIfEnabled(FQCN, Level.ERROR, null, var1, var2);
   }

   @Override
   public void error(String var1, Object var2, Object var3) {
      this.logIfEnabled(FQCN, Level.ERROR, null, var1, var2, var3);
   }

   @Override
   public void error(String var1, Object var2, Object var3, Object var4) {
      this.logIfEnabled(FQCN, Level.ERROR, null, var1, var2, var3, var4);
   }

   @Override
   public void error(String var1, Object var2, Object var3, Object var4, Object var5) {
      this.logIfEnabled(FQCN, Level.ERROR, null, var1, var2, var3, var4, var5);
   }

   @Override
   public void error(String var1, Object var2, Object var3, Object var4, Object var5, Object var6) {
      this.logIfEnabled(FQCN, Level.ERROR, null, var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void error(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      this.logIfEnabled(FQCN, Level.ERROR, null, var1, var2, var3, var4, var5, var6, var7);
   }

   @Override
   public void error(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      this.logIfEnabled(FQCN, Level.ERROR, null, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void error(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      this.logIfEnabled(FQCN, Level.ERROR, null, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   @Override
   public void error(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      this.logIfEnabled(FQCN, Level.ERROR, null, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   @Override
   public void error(
      String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11
   ) {
      this.logIfEnabled(FQCN, Level.ERROR, null, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   @Deprecated
   @Override
   public void exit() {
      this.exit(FQCN, null);
   }

   @Deprecated
   @Override
   public <R> R exit(R var1) {
      return this.exit(FQCN, (R)var1);
   }

   protected <R> R exit(String var1, R var2) {
      if (this.isEnabled(Level.TRACE, EXIT_MARKER, (CharSequence)null, null)) {
         this.logMessageSafely(var1, Level.TRACE, EXIT_MARKER, this.exitMsg(null, var2), null);
      }

      return (R)var2;
   }

   protected <R> R exit(String var1, String var2, R var3) {
      if (this.isEnabled(Level.TRACE, EXIT_MARKER, (CharSequence)null, null)) {
         this.logMessageSafely(var1, Level.TRACE, EXIT_MARKER, this.exitMsg(var2, var3), null);
      }

      return (R)var3;
   }

   protected Message exitMsg(String var1, Object var2) {
      if (var2 == null) {
         return var1 == null ? this.messageFactory.newMessage("Exit") : this.messageFactory.newMessage("Exit: " + var1);
      } else {
         return var1 == null ? this.messageFactory.newMessage("Exit with(" + var2 + ')') : this.messageFactory.newMessage("Exit: " + var1, var2);
      }
   }

   @Override
   public void fatal(Marker var1, Message var2) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var2 != null ? var2.getThrowable() : null);
   }

   @Override
   public void fatal(Marker var1, Message var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3);
   }

   @Override
   public void fatal(Marker var1, CharSequence var2) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, null);
   }

   @Override
   public void fatal(Marker var1, CharSequence var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3);
   }

   @Override
   public void fatal(Marker var1, Object var2) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, null);
   }

   @Override
   public void fatal(Marker var1, Object var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3);
   }

   @Override
   public void fatal(Marker var1, String var2) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, (Throwable)null);
   }

   @Override
   public void fatal(Marker var1, String var2, Object... var3) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3);
   }

   @Override
   public void fatal(Marker var1, String var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3);
   }

   @Override
   public void fatal(Message var1) {
      this.logIfEnabled(FQCN, Level.FATAL, null, var1, var1 != null ? var1.getThrowable() : null);
   }

   @Override
   public void fatal(Message var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.FATAL, null, var1, var2);
   }

   @Override
   public void fatal(CharSequence var1) {
      this.logIfEnabled(FQCN, Level.FATAL, null, var1, null);
   }

   @Override
   public void fatal(CharSequence var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.FATAL, null, var1, var2);
   }

   @Override
   public void fatal(Object var1) {
      this.logIfEnabled(FQCN, Level.FATAL, null, var1, null);
   }

   @Override
   public void fatal(Object var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.FATAL, null, var1, var2);
   }

   @Override
   public void fatal(String var1) {
      this.logIfEnabled(FQCN, Level.FATAL, null, var1, (Throwable)null);
   }

   @Override
   public void fatal(String var1, Object... var2) {
      this.logIfEnabled(FQCN, Level.FATAL, null, var1, var2);
   }

   @Override
   public void fatal(String var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.FATAL, null, var1, var2);
   }

   @Override
   public void fatal(Supplier<?> var1) {
      this.logIfEnabled(FQCN, Level.FATAL, null, var1, (Throwable)null);
   }

   @Override
   public void fatal(Supplier<?> var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.FATAL, null, var1, var2);
   }

   @Override
   public void fatal(Marker var1, Supplier<?> var2) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, (Throwable)null);
   }

   @Override
   public void fatal(Marker var1, String var2, Supplier<?>... var3) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3);
   }

   @Override
   public void fatal(Marker var1, Supplier<?> var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3);
   }

   @Override
   public void fatal(String var1, Supplier<?>... var2) {
      this.logIfEnabled(FQCN, Level.FATAL, null, var1, var2);
   }

   @Override
   public void fatal(Marker var1, MessageSupplier var2) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, (Throwable)null);
   }

   @Override
   public void fatal(Marker var1, MessageSupplier var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3);
   }

   @Override
   public void fatal(MessageSupplier var1) {
      this.logIfEnabled(FQCN, Level.FATAL, null, var1, (Throwable)null);
   }

   @Override
   public void fatal(MessageSupplier var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.FATAL, null, var1, var2);
   }

   @Override
   public void fatal(Marker var1, String var2, Object var3) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3);
   }

   @Override
   public void fatal(Marker var1, String var2, Object var3, Object var4) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3, var4);
   }

   @Override
   public void fatal(Marker var1, String var2, Object var3, Object var4, Object var5) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3, var4, var5);
   }

   @Override
   public void fatal(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void fatal(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3, var4, var5, var6, var7);
   }

   @Override
   public void fatal(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void fatal(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   @Override
   public void fatal(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   @Override
   public void fatal(
      Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11
   ) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   @Override
   public void fatal(
      Marker var1,
      String var2,
      Object var3,
      Object var4,
      Object var5,
      Object var6,
      Object var7,
      Object var8,
      Object var9,
      Object var10,
      Object var11,
      Object var12
   ) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
   }

   @Override
   public void fatal(String var1, Object var2) {
      this.logIfEnabled(FQCN, Level.FATAL, null, var1, var2);
   }

   @Override
   public void fatal(String var1, Object var2, Object var3) {
      this.logIfEnabled(FQCN, Level.FATAL, null, var1, var2, var3);
   }

   @Override
   public void fatal(String var1, Object var2, Object var3, Object var4) {
      this.logIfEnabled(FQCN, Level.FATAL, null, var1, var2, var3, var4);
   }

   @Override
   public void fatal(String var1, Object var2, Object var3, Object var4, Object var5) {
      this.logIfEnabled(FQCN, Level.FATAL, null, var1, var2, var3, var4, var5);
   }

   @Override
   public void fatal(String var1, Object var2, Object var3, Object var4, Object var5, Object var6) {
      this.logIfEnabled(FQCN, Level.FATAL, null, var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void fatal(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      this.logIfEnabled(FQCN, Level.FATAL, null, var1, var2, var3, var4, var5, var6, var7);
   }

   @Override
   public void fatal(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      this.logIfEnabled(FQCN, Level.FATAL, null, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void fatal(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      this.logIfEnabled(FQCN, Level.FATAL, null, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   @Override
   public void fatal(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      this.logIfEnabled(FQCN, Level.FATAL, null, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   @Override
   public void fatal(
      String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11
   ) {
      this.logIfEnabled(FQCN, Level.FATAL, null, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   @Override
   public <MF extends MessageFactory> MF getMessageFactory() {
      return (MF)this.messageFactory;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public void info(Marker var1, Message var2) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var2 != null ? var2.getThrowable() : null);
   }

   @Override
   public void info(Marker var1, Message var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3);
   }

   @Override
   public void info(Marker var1, CharSequence var2) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, null);
   }

   @Override
   public void info(Marker var1, CharSequence var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3);
   }

   @Override
   public void info(Marker var1, Object var2) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, null);
   }

   @Override
   public void info(Marker var1, Object var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3);
   }

   @Override
   public void info(Marker var1, String var2) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, (Throwable)null);
   }

   @Override
   public void info(Marker var1, String var2, Object... var3) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3);
   }

   @Override
   public void info(Marker var1, String var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3);
   }

   @Override
   public void info(Message var1) {
      this.logIfEnabled(FQCN, Level.INFO, null, var1, var1 != null ? var1.getThrowable() : null);
   }

   @Override
   public void info(Message var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.INFO, null, var1, var2);
   }

   @Override
   public void info(CharSequence var1) {
      this.logIfEnabled(FQCN, Level.INFO, null, var1, null);
   }

   @Override
   public void info(CharSequence var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.INFO, null, var1, var2);
   }

   @Override
   public void info(Object var1) {
      this.logIfEnabled(FQCN, Level.INFO, null, var1, null);
   }

   @Override
   public void info(Object var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.INFO, null, var1, var2);
   }

   @Override
   public void info(String var1) {
      this.logIfEnabled(FQCN, Level.INFO, null, var1, (Throwable)null);
   }

   @Override
   public void info(String var1, Object... var2) {
      this.logIfEnabled(FQCN, Level.INFO, null, var1, var2);
   }

   @Override
   public void info(String var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.INFO, null, var1, var2);
   }

   @Override
   public void info(Supplier<?> var1) {
      this.logIfEnabled(FQCN, Level.INFO, null, var1, (Throwable)null);
   }

   @Override
   public void info(Supplier<?> var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.INFO, null, var1, var2);
   }

   @Override
   public void info(Marker var1, Supplier<?> var2) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, (Throwable)null);
   }

   @Override
   public void info(Marker var1, String var2, Supplier<?>... var3) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3);
   }

   @Override
   public void info(Marker var1, Supplier<?> var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3);
   }

   @Override
   public void info(String var1, Supplier<?>... var2) {
      this.logIfEnabled(FQCN, Level.INFO, null, var1, var2);
   }

   @Override
   public void info(Marker var1, MessageSupplier var2) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, (Throwable)null);
   }

   @Override
   public void info(Marker var1, MessageSupplier var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3);
   }

   @Override
   public void info(MessageSupplier var1) {
      this.logIfEnabled(FQCN, Level.INFO, null, var1, (Throwable)null);
   }

   @Override
   public void info(MessageSupplier var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.INFO, null, var1, var2);
   }

   @Override
   public void info(Marker var1, String var2, Object var3) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3);
   }

   @Override
   public void info(Marker var1, String var2, Object var3, Object var4) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3, var4);
   }

   @Override
   public void info(Marker var1, String var2, Object var3, Object var4, Object var5) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3, var4, var5);
   }

   @Override
   public void info(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void info(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3, var4, var5, var6, var7);
   }

   @Override
   public void info(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void info(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   @Override
   public void info(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   @Override
   public void info(
      Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11
   ) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   @Override
   public void info(
      Marker var1,
      String var2,
      Object var3,
      Object var4,
      Object var5,
      Object var6,
      Object var7,
      Object var8,
      Object var9,
      Object var10,
      Object var11,
      Object var12
   ) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
   }

   @Override
   public void info(String var1, Object var2) {
      this.logIfEnabled(FQCN, Level.INFO, null, var1, var2);
   }

   @Override
   public void info(String var1, Object var2, Object var3) {
      this.logIfEnabled(FQCN, Level.INFO, null, var1, var2, var3);
   }

   @Override
   public void info(String var1, Object var2, Object var3, Object var4) {
      this.logIfEnabled(FQCN, Level.INFO, null, var1, var2, var3, var4);
   }

   @Override
   public void info(String var1, Object var2, Object var3, Object var4, Object var5) {
      this.logIfEnabled(FQCN, Level.INFO, null, var1, var2, var3, var4, var5);
   }

   @Override
   public void info(String var1, Object var2, Object var3, Object var4, Object var5, Object var6) {
      this.logIfEnabled(FQCN, Level.INFO, null, var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void info(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      this.logIfEnabled(FQCN, Level.INFO, null, var1, var2, var3, var4, var5, var6, var7);
   }

   @Override
   public void info(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      this.logIfEnabled(FQCN, Level.INFO, null, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void info(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      this.logIfEnabled(FQCN, Level.INFO, null, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   @Override
   public void info(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      this.logIfEnabled(FQCN, Level.INFO, null, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   @Override
   public void info(
      String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11
   ) {
      this.logIfEnabled(FQCN, Level.INFO, null, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   @Override
   public boolean isDebugEnabled() {
      return this.isEnabled(Level.DEBUG, null, null);
   }

   @Override
   public boolean isDebugEnabled(Marker var1) {
      return this.isEnabled(Level.DEBUG, var1, null, null);
   }

   @Override
   public boolean isEnabled(Level var1) {
      return this.isEnabled(var1, null, null, null);
   }

   @Override
   public boolean isEnabled(Level var1, Marker var2) {
      return this.isEnabled(var1, var2, null, null);
   }

   @Override
   public boolean isErrorEnabled() {
      return this.isEnabled(Level.ERROR, null, null, null);
   }

   @Override
   public boolean isErrorEnabled(Marker var1) {
      return this.isEnabled(Level.ERROR, var1, null, null);
   }

   @Override
   public boolean isFatalEnabled() {
      return this.isEnabled(Level.FATAL, null, null, null);
   }

   @Override
   public boolean isFatalEnabled(Marker var1) {
      return this.isEnabled(Level.FATAL, var1, null, null);
   }

   @Override
   public boolean isInfoEnabled() {
      return this.isEnabled(Level.INFO, null, null, null);
   }

   @Override
   public boolean isInfoEnabled(Marker var1) {
      return this.isEnabled(Level.INFO, var1, null, null);
   }

   @Override
   public boolean isTraceEnabled() {
      return this.isEnabled(Level.TRACE, null, null, null);
   }

   @Override
   public boolean isTraceEnabled(Marker var1) {
      return this.isEnabled(Level.TRACE, var1, null, null);
   }

   @Override
   public boolean isWarnEnabled() {
      return this.isEnabled(Level.WARN, null, null, null);
   }

   @Override
   public boolean isWarnEnabled(Marker var1) {
      return this.isEnabled(Level.WARN, var1, null, null);
   }

   @Override
   public void log(Level var1, Marker var2, Message var3) {
      this.logIfEnabled(FQCN, var1, var2, var3, var3 != null ? var3.getThrowable() : null);
   }

   @Override
   public void log(Level var1, Marker var2, Message var3, Throwable var4) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4);
   }

   @Override
   public void log(Level var1, Marker var2, CharSequence var3) {
      this.logIfEnabled(FQCN, var1, var2, var3, (Throwable)null);
   }

   @Override
   public void log(Level var1, Marker var2, CharSequence var3, Throwable var4) {
      if (this.isEnabled(var1, var2, var3, var4)) {
         this.logMessage(FQCN, var1, var2, var3, var4);
      }
   }

   @Override
   public void log(Level var1, Marker var2, Object var3) {
      this.logIfEnabled(FQCN, var1, var2, var3, (Throwable)null);
   }

   @Override
   public void log(Level var1, Marker var2, Object var3, Throwable var4) {
      if (this.isEnabled(var1, var2, var3, var4)) {
         this.logMessage(FQCN, var1, var2, var3, var4);
      }
   }

   @Override
   public void log(Level var1, Marker var2, String var3) {
      this.logIfEnabled(FQCN, var1, var2, var3, (Throwable)null);
   }

   @Override
   public void log(Level var1, Marker var2, String var3, Object... var4) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4);
   }

   @Override
   public void log(Level var1, Marker var2, String var3, Throwable var4) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4);
   }

   @Override
   public void log(Level var1, Message var2) {
      this.logIfEnabled(FQCN, var1, null, var2, var2 != null ? var2.getThrowable() : null);
   }

   @Override
   public void log(Level var1, Message var2, Throwable var3) {
      this.logIfEnabled(FQCN, var1, null, var2, var3);
   }

   @Override
   public void log(Level var1, CharSequence var2) {
      this.logIfEnabled(FQCN, var1, null, var2, null);
   }

   @Override
   public void log(Level var1, CharSequence var2, Throwable var3) {
      this.logIfEnabled(FQCN, var1, null, var2, var3);
   }

   @Override
   public void log(Level var1, Object var2) {
      this.logIfEnabled(FQCN, var1, null, var2, null);
   }

   @Override
   public void log(Level var1, Object var2, Throwable var3) {
      this.logIfEnabled(FQCN, var1, null, var2, var3);
   }

   @Override
   public void log(Level var1, String var2) {
      this.logIfEnabled(FQCN, var1, null, var2, (Throwable)null);
   }

   @Override
   public void log(Level var1, String var2, Object... var3) {
      this.logIfEnabled(FQCN, var1, null, var2, var3);
   }

   @Override
   public void log(Level var1, String var2, Throwable var3) {
      this.logIfEnabled(FQCN, var1, null, var2, var3);
   }

   @Override
   public void log(Level var1, Supplier<?> var2) {
      this.logIfEnabled(FQCN, var1, null, var2, (Throwable)null);
   }

   @Override
   public void log(Level var1, Supplier<?> var2, Throwable var3) {
      this.logIfEnabled(FQCN, var1, null, var2, var3);
   }

   @Override
   public void log(Level var1, Marker var2, Supplier<?> var3) {
      this.logIfEnabled(FQCN, var1, var2, var3, (Throwable)null);
   }

   @Override
   public void log(Level var1, Marker var2, String var3, Supplier<?>... var4) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4);
   }

   @Override
   public void log(Level var1, Marker var2, Supplier<?> var3, Throwable var4) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4);
   }

   @Override
   public void log(Level var1, String var2, Supplier<?>... var3) {
      this.logIfEnabled(FQCN, var1, null, var2, var3);
   }

   @Override
   public void log(Level var1, Marker var2, MessageSupplier var3) {
      this.logIfEnabled(FQCN, var1, var2, var3, (Throwable)null);
   }

   @Override
   public void log(Level var1, Marker var2, MessageSupplier var3, Throwable var4) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4);
   }

   @Override
   public void log(Level var1, MessageSupplier var2) {
      this.logIfEnabled(FQCN, var1, null, var2, (Throwable)null);
   }

   @Override
   public void log(Level var1, MessageSupplier var2, Throwable var3) {
      this.logIfEnabled(FQCN, var1, null, var2, var3);
   }

   @Override
   public void log(Level var1, Marker var2, String var3, Object var4) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4);
   }

   @Override
   public void log(Level var1, Marker var2, String var3, Object var4, Object var5) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4, var5);
   }

   @Override
   public void log(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void log(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4, var5, var6, var7);
   }

   @Override
   public void log(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void log(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   @Override
   public void log(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   @Override
   public void log(
      Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11
   ) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   @Override
   public void log(
      Level var1,
      Marker var2,
      String var3,
      Object var4,
      Object var5,
      Object var6,
      Object var7,
      Object var8,
      Object var9,
      Object var10,
      Object var11,
      Object var12
   ) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
   }

   @Override
   public void log(
      Level var1,
      Marker var2,
      String var3,
      Object var4,
      Object var5,
      Object var6,
      Object var7,
      Object var8,
      Object var9,
      Object var10,
      Object var11,
      Object var12,
      Object var13
   ) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
   }

   @Override
   public void log(Level var1, String var2, Object var3) {
      this.logIfEnabled(FQCN, var1, null, var2, var3);
   }

   @Override
   public void log(Level var1, String var2, Object var3, Object var4) {
      this.logIfEnabled(FQCN, var1, null, var2, var3, var4);
   }

   @Override
   public void log(Level var1, String var2, Object var3, Object var4, Object var5) {
      this.logIfEnabled(FQCN, var1, null, var2, var3, var4, var5);
   }

   @Override
   public void log(Level var1, String var2, Object var3, Object var4, Object var5, Object var6) {
      this.logIfEnabled(FQCN, var1, null, var2, var3, var4, var5, var6);
   }

   @Override
   public void log(Level var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      this.logIfEnabled(FQCN, var1, null, var2, var3, var4, var5, var6, var7);
   }

   @Override
   public void log(Level var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      this.logIfEnabled(FQCN, var1, null, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void log(Level var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      this.logIfEnabled(FQCN, var1, null, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   @Override
   public void log(Level var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      this.logIfEnabled(FQCN, var1, null, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   @Override
   public void log(
      Level var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11
   ) {
      this.logIfEnabled(FQCN, var1, null, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   @Override
   public void log(
      Level var1,
      String var2,
      Object var3,
      Object var4,
      Object var5,
      Object var6,
      Object var7,
      Object var8,
      Object var9,
      Object var10,
      Object var11,
      Object var12
   ) {
      this.logIfEnabled(FQCN, var1, null, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
   }

   @Override
   public void logIfEnabled(String var1, Level var2, Marker var3, Message var4, Throwable var5) {
      if (this.isEnabled(var2, var3, var4, var5)) {
         this.logMessageSafely(var1, var2, var3, var4, var5);
      }
   }

   @Override
   public void logIfEnabled(String var1, Level var2, Marker var3, MessageSupplier var4, Throwable var5) {
      if (this.isEnabled(var2, var3, var4, var5)) {
         this.logMessage(var1, var2, var3, var4, var5);
      }
   }

   @Override
   public void logIfEnabled(String var1, Level var2, Marker var3, Object var4, Throwable var5) {
      if (this.isEnabled(var2, var3, var4, var5)) {
         this.logMessage(var1, var2, var3, var4, var5);
      }
   }

   @Override
   public void logIfEnabled(String var1, Level var2, Marker var3, CharSequence var4, Throwable var5) {
      if (this.isEnabled(var2, var3, var4, var5)) {
         this.logMessage(var1, var2, var3, var4, var5);
      }
   }

   @Override
   public void logIfEnabled(String var1, Level var2, Marker var3, Supplier<?> var4, Throwable var5) {
      if (this.isEnabled(var2, var3, var4, var5)) {
         this.logMessage(var1, var2, var3, var4, var5);
      }
   }

   @Override
   public void logIfEnabled(String var1, Level var2, Marker var3, String var4) {
      if (this.isEnabled(var2, var3, var4)) {
         this.logMessage(var1, var2, var3, var4);
      }
   }

   @Override
   public void logIfEnabled(String var1, Level var2, Marker var3, String var4, Supplier<?>... var5) {
      if (this.isEnabled(var2, var3, var4)) {
         this.logMessage(var1, var2, var3, var4, var5);
      }
   }

   @Override
   public void logIfEnabled(String var1, Level var2, Marker var3, String var4, Object... var5) {
      if (this.isEnabled(var2, var3, var4, var5)) {
         this.logMessage(var1, var2, var3, var4, var5);
      }
   }

   @Override
   public void logIfEnabled(String var1, Level var2, Marker var3, String var4, Object var5) {
      if (this.isEnabled(var2, var3, var4, var5)) {
         this.logMessage(var1, var2, var3, var4, var5);
      }
   }

   @Override
   public void logIfEnabled(String var1, Level var2, Marker var3, String var4, Object var5, Object var6) {
      if (this.isEnabled(var2, var3, var4, var5, var6)) {
         this.logMessage(var1, var2, var3, var4, var5, var6);
      }
   }

   @Override
   public void logIfEnabled(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7) {
      if (this.isEnabled(var2, var3, var4, var5, var6, var7)) {
         this.logMessage(var1, var2, var3, var4, var5, var6, var7);
      }
   }

   @Override
   public void logIfEnabled(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8) {
      if (this.isEnabled(var2, var3, var4, var5, var6, var7, var8)) {
         this.logMessage(var1, var2, var3, var4, var5, var6, var7, var8);
      }
   }

   @Override
   public void logIfEnabled(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      if (this.isEnabled(var2, var3, var4, var5, var6, var7, var8, var9)) {
         this.logMessage(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      }
   }

   @Override
   public void logIfEnabled(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      if (this.isEnabled(var2, var3, var4, var5, var6, var7, var8, var9, var10)) {
         this.logMessage(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      }
   }

   @Override
   public void logIfEnabled(
      String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11
   ) {
      if (this.isEnabled(var2, var3, var4, var5, var6, var7, var8, var9, var10, var11)) {
         this.logMessage(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
      }
   }

   @Override
   public void logIfEnabled(
      String var1,
      Level var2,
      Marker var3,
      String var4,
      Object var5,
      Object var6,
      Object var7,
      Object var8,
      Object var9,
      Object var10,
      Object var11,
      Object var12
   ) {
      if (this.isEnabled(var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12)) {
         this.logMessage(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
      }
   }

   @Override
   public void logIfEnabled(
      String var1,
      Level var2,
      Marker var3,
      String var4,
      Object var5,
      Object var6,
      Object var7,
      Object var8,
      Object var9,
      Object var10,
      Object var11,
      Object var12,
      Object var13
   ) {
      if (this.isEnabled(var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13)) {
         this.logMessage(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
      }
   }

   @Override
   public void logIfEnabled(
      String var1,
      Level var2,
      Marker var3,
      String var4,
      Object var5,
      Object var6,
      Object var7,
      Object var8,
      Object var9,
      Object var10,
      Object var11,
      Object var12,
      Object var13,
      Object var14
   ) {
      if (this.isEnabled(var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14)) {
         this.logMessage(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14);
      }
   }

   @Override
   public void logIfEnabled(String var1, Level var2, Marker var3, String var4, Throwable var5) {
      if (this.isEnabled(var2, var3, var4, var5)) {
         this.logMessage(var1, var2, var3, var4, var5);
      }
   }

   protected void logMessage(String var1, Level var2, Marker var3, CharSequence var4, Throwable var5) {
      this.logMessageSafely(var1, var2, var3, this.messageFactory.newMessage(var4), var5);
   }

   protected void logMessage(String var1, Level var2, Marker var3, Object var4, Throwable var5) {
      this.logMessageSafely(var1, var2, var3, this.messageFactory.newMessage(var4), var5);
   }

   protected void logMessage(String var1, Level var2, Marker var3, MessageSupplier var4, Throwable var5) {
      Message var6 = LambdaUtil.get(var4);
      Throwable var7 = var5 == null && var6 != null ? var6.getThrowable() : var5;
      this.logMessageSafely(var1, var2, var3, var6, var7);
   }

   protected void logMessage(String var1, Level var2, Marker var3, Supplier<?> var4, Throwable var5) {
      Message var6 = LambdaUtil.getMessage(var4, this.messageFactory);
      Throwable var7 = var5 == null && var6 != null ? var6.getThrowable() : var5;
      this.logMessageSafely(var1, var2, var3, var6, var7);
   }

   protected void logMessage(String var1, Level var2, Marker var3, String var4, Throwable var5) {
      this.logMessageSafely(var1, var2, var3, this.messageFactory.newMessage(var4), var5);
   }

   protected void logMessage(String var1, Level var2, Marker var3, String var4) {
      Message var5 = this.messageFactory.newMessage(var4);
      this.logMessageSafely(var1, var2, var3, var5, var5.getThrowable());
   }

   protected void logMessage(String var1, Level var2, Marker var3, String var4, Object... var5) {
      Message var6 = this.messageFactory.newMessage(var4, var5);
      this.logMessageSafely(var1, var2, var3, var6, var6.getThrowable());
   }

   protected void logMessage(String var1, Level var2, Marker var3, String var4, Object var5) {
      Message var6 = this.messageFactory.newMessage(var4, var5);
      this.logMessageSafely(var1, var2, var3, var6, var6.getThrowable());
   }

   protected void logMessage(String var1, Level var2, Marker var3, String var4, Object var5, Object var6) {
      Message var7 = this.messageFactory.newMessage(var4, var5, var6);
      this.logMessageSafely(var1, var2, var3, var7, var7.getThrowable());
   }

   protected void logMessage(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7) {
      Message var8 = this.messageFactory.newMessage(var4, var5, var6, var7);
      this.logMessageSafely(var1, var2, var3, var8, var8.getThrowable());
   }

   protected void logMessage(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8) {
      Message var9 = this.messageFactory.newMessage(var4, var5, var6, var7, var8);
      this.logMessageSafely(var1, var2, var3, var9, var9.getThrowable());
   }

   protected void logMessage(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      Message var10 = this.messageFactory.newMessage(var4, var5, var6, var7, var8, var9);
      this.logMessageSafely(var1, var2, var3, var10, var10.getThrowable());
   }

   protected void logMessage(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      Message var11 = this.messageFactory.newMessage(var4, var5, var6, var7, var8, var9, var10);
      this.logMessageSafely(var1, var2, var3, var11, var11.getThrowable());
   }

   protected void logMessage(
      String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11
   ) {
      Message var12 = this.messageFactory.newMessage(var4, var5, var6, var7, var8, var9, var10, var11);
      this.logMessageSafely(var1, var2, var3, var12, var12.getThrowable());
   }

   protected void logMessage(
      String var1,
      Level var2,
      Marker var3,
      String var4,
      Object var5,
      Object var6,
      Object var7,
      Object var8,
      Object var9,
      Object var10,
      Object var11,
      Object var12
   ) {
      Message var13 = this.messageFactory.newMessage(var4, var5, var6, var7, var8, var9, var10, var11, var12);
      this.logMessageSafely(var1, var2, var3, var13, var13.getThrowable());
   }

   protected void logMessage(
      String var1,
      Level var2,
      Marker var3,
      String var4,
      Object var5,
      Object var6,
      Object var7,
      Object var8,
      Object var9,
      Object var10,
      Object var11,
      Object var12,
      Object var13
   ) {
      Message var14 = this.messageFactory.newMessage(var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
      this.logMessageSafely(var1, var2, var3, var14, var14.getThrowable());
   }

   protected void logMessage(
      String var1,
      Level var2,
      Marker var3,
      String var4,
      Object var5,
      Object var6,
      Object var7,
      Object var8,
      Object var9,
      Object var10,
      Object var11,
      Object var12,
      Object var13,
      Object var14
   ) {
      Message var15 = this.messageFactory.newMessage(var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14);
      this.logMessageSafely(var1, var2, var3, var15, var15.getThrowable());
   }

   protected void logMessage(String var1, Level var2, Marker var3, String var4, Supplier<?>... var5) {
      Message var6 = this.messageFactory.newMessage(var4, LambdaUtil.getAll(var5));
      this.logMessageSafely(var1, var2, var3, var6, var6.getThrowable());
   }

   @Override
   public void logMessage(Level var1, Marker var2, String var3, StackTraceElement var4, Message var5, Throwable var6) {
      try {
         incrementRecursionDepth();
         this.log(var1, var2, var3, var4, var5, var6);
      } catch (Throwable var11) {
         this.handleLogMessageException(var11, var3, var5);
      } finally {
         decrementRecursionDepth();
         ReusableMessageFactory.release(var5);
      }
   }

   protected void log(Level var1, Marker var2, String var3, StackTraceElement var4, Message var5, Throwable var6) {
      this.logMessage(var3, var1, var2, var5, var6);
   }

   @Override
   public void printf(Level var1, Marker var2, String var3, Object... var4) {
      if (this.isEnabled(var1, var2, var3, var4)) {
         StringFormattedMessage var5 = new StringFormattedMessage(var3, var4);
         this.logMessageSafely(FQCN, var1, var2, var5, var5.getThrowable());
      }
   }

   @Override
   public void printf(Level var1, String var2, Object... var3) {
      if (this.isEnabled(var1, null, var2, var3)) {
         StringFormattedMessage var4 = new StringFormattedMessage(var2, var3);
         this.logMessageSafely(FQCN, var1, null, var4, var4.getThrowable());
      }
   }

   @PerformanceSensitive
   private void logMessageSafely(String var1, Level var2, Marker var3, Message var4, Throwable var5) {
      try {
         this.logMessageTrackRecursion(var1, var2, var3, var4, var5);
      } finally {
         ReusableMessageFactory.release(var4);
      }
   }

   @PerformanceSensitive
   private void logMessageTrackRecursion(String var1, Level var2, Marker var3, Message var4, Throwable var5) {
      try {
         incrementRecursionDepth();
         this.tryLogMessage(var1, this.getLocation(var1), var2, var3, var4, var5);
      } finally {
         decrementRecursionDepth();
      }
   }

   private static int[] getRecursionDepthHolder() {
      int[] var0 = recursionDepthHolder.get();
      if (var0 == null) {
         var0 = new int[1];
         recursionDepthHolder.set(var0);
      }

      return var0;
   }

   private static void incrementRecursionDepth() {
      getRecursionDepthHolder()[0]++;
   }

   private static void decrementRecursionDepth() {
      int var0 = --getRecursionDepthHolder()[0];
      if (var0 < 0) {
         throw new IllegalStateException("Recursion depth became negative: " + var0);
      }
   }

   public static int getRecursionDepth() {
      return getRecursionDepthHolder()[0];
   }

   @PerformanceSensitive
   private void tryLogMessage(String var1, StackTraceElement var2, Level var3, Marker var4, Message var5, Throwable var6) {
      try {
         this.log(var3, var4, var1, var2, var5, var6);
      } catch (Throwable var8) {
         this.handleLogMessageException(var8, var1, var5);
      }
   }

   @PerformanceSensitive
   private StackTraceElement getLocation(String var1) {
      return this.requiresLocation() ? StackLocatorUtil.calcLocation(var1) : null;
   }

   private void handleLogMessageException(Throwable var1, String var2, Message var3) {
      if (var1 instanceof LoggingException) {
         throw (LoggingException)var1;
      } else {
         StatusLogger.getLogger().warn("{} caught {} logging {}: {}", var2, var1.getClass().getName(), var3.getClass().getSimpleName(), var3.getFormat(), var1);
      }
   }

   @Override
   public <T extends Throwable> T throwing(T var1) {
      return this.throwing(FQCN, Level.ERROR, (T)var1);
   }

   @Override
   public <T extends Throwable> T throwing(Level var1, T var2) {
      return this.throwing(FQCN, var1, (T)var2);
   }

   protected <T extends Throwable> T throwing(String var1, Level var2, T var3) {
      if (this.isEnabled(var2, THROWING_MARKER, null, null)) {
         this.logMessageSafely(var1, var2, THROWING_MARKER, this.throwingMsg(var3), var3);
      }

      return (T)var3;
   }

   protected Message throwingMsg(Throwable var1) {
      return this.messageFactory.newMessage("Throwing");
   }

   @Override
   public void trace(Marker var1, Message var2) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var2 != null ? var2.getThrowable() : null);
   }

   @Override
   public void trace(Marker var1, Message var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3);
   }

   @Override
   public void trace(Marker var1, CharSequence var2) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, null);
   }

   @Override
   public void trace(Marker var1, CharSequence var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3);
   }

   @Override
   public void trace(Marker var1, Object var2) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, null);
   }

   @Override
   public void trace(Marker var1, Object var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3);
   }

   @Override
   public void trace(Marker var1, String var2) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, (Throwable)null);
   }

   @Override
   public void trace(Marker var1, String var2, Object... var3) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3);
   }

   @Override
   public void trace(Marker var1, String var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3);
   }

   @Override
   public void trace(Message var1) {
      this.logIfEnabled(FQCN, Level.TRACE, null, var1, var1 != null ? var1.getThrowable() : null);
   }

   @Override
   public void trace(Message var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.TRACE, null, var1, var2);
   }

   @Override
   public void trace(CharSequence var1) {
      this.logIfEnabled(FQCN, Level.TRACE, null, var1, null);
   }

   @Override
   public void trace(CharSequence var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.TRACE, null, var1, var2);
   }

   @Override
   public void trace(Object var1) {
      this.logIfEnabled(FQCN, Level.TRACE, null, var1, null);
   }

   @Override
   public void trace(Object var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.TRACE, null, var1, var2);
   }

   @Override
   public void trace(String var1) {
      this.logIfEnabled(FQCN, Level.TRACE, null, var1, (Throwable)null);
   }

   @Override
   public void trace(String var1, Object... var2) {
      this.logIfEnabled(FQCN, Level.TRACE, null, var1, var2);
   }

   @Override
   public void trace(String var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.TRACE, null, var1, var2);
   }

   @Override
   public void trace(Supplier<?> var1) {
      this.logIfEnabled(FQCN, Level.TRACE, null, var1, (Throwable)null);
   }

   @Override
   public void trace(Supplier<?> var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.TRACE, null, var1, var2);
   }

   @Override
   public void trace(Marker var1, Supplier<?> var2) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, (Throwable)null);
   }

   @Override
   public void trace(Marker var1, String var2, Supplier<?>... var3) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3);
   }

   @Override
   public void trace(Marker var1, Supplier<?> var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3);
   }

   @Override
   public void trace(String var1, Supplier<?>... var2) {
      this.logIfEnabled(FQCN, Level.TRACE, null, var1, var2);
   }

   @Override
   public void trace(Marker var1, MessageSupplier var2) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, (Throwable)null);
   }

   @Override
   public void trace(Marker var1, MessageSupplier var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3);
   }

   @Override
   public void trace(MessageSupplier var1) {
      this.logIfEnabled(FQCN, Level.TRACE, null, var1, (Throwable)null);
   }

   @Override
   public void trace(MessageSupplier var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.TRACE, null, var1, var2);
   }

   @Override
   public void trace(Marker var1, String var2, Object var3) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3);
   }

   @Override
   public void trace(Marker var1, String var2, Object var3, Object var4) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3, var4);
   }

   @Override
   public void trace(Marker var1, String var2, Object var3, Object var4, Object var5) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3, var4, var5);
   }

   @Override
   public void trace(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void trace(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3, var4, var5, var6, var7);
   }

   @Override
   public void trace(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void trace(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   @Override
   public void trace(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   @Override
   public void trace(
      Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11
   ) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   @Override
   public void trace(
      Marker var1,
      String var2,
      Object var3,
      Object var4,
      Object var5,
      Object var6,
      Object var7,
      Object var8,
      Object var9,
      Object var10,
      Object var11,
      Object var12
   ) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
   }

   @Override
   public void trace(String var1, Object var2) {
      this.logIfEnabled(FQCN, Level.TRACE, null, var1, var2);
   }

   @Override
   public void trace(String var1, Object var2, Object var3) {
      this.logIfEnabled(FQCN, Level.TRACE, null, var1, var2, var3);
   }

   @Override
   public void trace(String var1, Object var2, Object var3, Object var4) {
      this.logIfEnabled(FQCN, Level.TRACE, null, var1, var2, var3, var4);
   }

   @Override
   public void trace(String var1, Object var2, Object var3, Object var4, Object var5) {
      this.logIfEnabled(FQCN, Level.TRACE, null, var1, var2, var3, var4, var5);
   }

   @Override
   public void trace(String var1, Object var2, Object var3, Object var4, Object var5, Object var6) {
      this.logIfEnabled(FQCN, Level.TRACE, null, var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void trace(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      this.logIfEnabled(FQCN, Level.TRACE, null, var1, var2, var3, var4, var5, var6, var7);
   }

   @Override
   public void trace(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      this.logIfEnabled(FQCN, Level.TRACE, null, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void trace(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      this.logIfEnabled(FQCN, Level.TRACE, null, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   @Override
   public void trace(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      this.logIfEnabled(FQCN, Level.TRACE, null, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   @Override
   public void trace(
      String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11
   ) {
      this.logIfEnabled(FQCN, Level.TRACE, null, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   @Override
   public EntryMessage traceEntry() {
      return this.enter(FQCN, null, (Object[])null);
   }

   @Override
   public EntryMessage traceEntry(String var1, Object... var2) {
      return this.enter(FQCN, var1, var2);
   }

   @Override
   public EntryMessage traceEntry(Supplier<?>... var1) {
      return this.enter(FQCN, null, var1);
   }

   @Override
   public EntryMessage traceEntry(String var1, Supplier<?>... var2) {
      return this.enter(FQCN, var1, var2);
   }

   @Override
   public EntryMessage traceEntry(Message var1) {
      return this.enter(FQCN, var1);
   }

   @Override
   public void traceExit() {
      this.exit(FQCN, null, null);
   }

   @Override
   public <R> R traceExit(R var1) {
      return this.exit(FQCN, null, (R)var1);
   }

   @Override
   public <R> R traceExit(String var1, R var2) {
      return this.exit(FQCN, var1, (R)var2);
   }

   @Override
   public void traceExit(EntryMessage var1) {
      if (var1 != null && this.isEnabled(Level.TRACE, EXIT_MARKER, var1, null)) {
         this.logMessageSafely(FQCN, Level.TRACE, EXIT_MARKER, this.flowMessageFactory.newExitMessage(var1), null);
      }
   }

   @Override
   public <R> R traceExit(EntryMessage var1, R var2) {
      if (var1 != null && this.isEnabled(Level.TRACE, EXIT_MARKER, var1, null)) {
         this.logMessageSafely(FQCN, Level.TRACE, EXIT_MARKER, this.flowMessageFactory.newExitMessage(var2, var1), null);
      }

      return (R)var2;
   }

   @Override
   public <R> R traceExit(Message var1, R var2) {
      if (var1 != null && this.isEnabled(Level.TRACE, EXIT_MARKER, var1, null)) {
         this.logMessageSafely(FQCN, Level.TRACE, EXIT_MARKER, this.flowMessageFactory.newExitMessage(var2, var1), null);
      }

      return (R)var2;
   }

   @Override
   public void warn(Marker var1, Message var2) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var2 != null ? var2.getThrowable() : null);
   }

   @Override
   public void warn(Marker var1, Message var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3);
   }

   @Override
   public void warn(Marker var1, CharSequence var2) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, null);
   }

   @Override
   public void warn(Marker var1, CharSequence var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3);
   }

   @Override
   public void warn(Marker var1, Object var2) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, null);
   }

   @Override
   public void warn(Marker var1, Object var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3);
   }

   @Override
   public void warn(Marker var1, String var2) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, (Throwable)null);
   }

   @Override
   public void warn(Marker var1, String var2, Object... var3) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3);
   }

   @Override
   public void warn(Marker var1, String var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3);
   }

   @Override
   public void warn(Message var1) {
      this.logIfEnabled(FQCN, Level.WARN, null, var1, var1 != null ? var1.getThrowable() : null);
   }

   @Override
   public void warn(Message var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.WARN, null, var1, var2);
   }

   @Override
   public void warn(CharSequence var1) {
      this.logIfEnabled(FQCN, Level.WARN, null, var1, null);
   }

   @Override
   public void warn(CharSequence var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.WARN, null, var1, var2);
   }

   @Override
   public void warn(Object var1) {
      this.logIfEnabled(FQCN, Level.WARN, null, var1, null);
   }

   @Override
   public void warn(Object var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.WARN, null, var1, var2);
   }

   @Override
   public void warn(String var1) {
      this.logIfEnabled(FQCN, Level.WARN, null, var1, (Throwable)null);
   }

   @Override
   public void warn(String var1, Object... var2) {
      this.logIfEnabled(FQCN, Level.WARN, null, var1, var2);
   }

   @Override
   public void warn(String var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.WARN, null, var1, var2);
   }

   @Override
   public void warn(Supplier<?> var1) {
      this.logIfEnabled(FQCN, Level.WARN, null, var1, (Throwable)null);
   }

   @Override
   public void warn(Supplier<?> var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.WARN, null, var1, var2);
   }

   @Override
   public void warn(Marker var1, Supplier<?> var2) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, (Throwable)null);
   }

   @Override
   public void warn(Marker var1, String var2, Supplier<?>... var3) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3);
   }

   @Override
   public void warn(Marker var1, Supplier<?> var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3);
   }

   @Override
   public void warn(String var1, Supplier<?>... var2) {
      this.logIfEnabled(FQCN, Level.WARN, null, var1, var2);
   }

   @Override
   public void warn(Marker var1, MessageSupplier var2) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, (Throwable)null);
   }

   @Override
   public void warn(Marker var1, MessageSupplier var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3);
   }

   @Override
   public void warn(MessageSupplier var1) {
      this.logIfEnabled(FQCN, Level.WARN, null, var1, (Throwable)null);
   }

   @Override
   public void warn(MessageSupplier var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.WARN, null, var1, var2);
   }

   @Override
   public void warn(Marker var1, String var2, Object var3) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3);
   }

   @Override
   public void warn(Marker var1, String var2, Object var3, Object var4) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3, var4);
   }

   @Override
   public void warn(Marker var1, String var2, Object var3, Object var4, Object var5) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3, var4, var5);
   }

   @Override
   public void warn(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void warn(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3, var4, var5, var6, var7);
   }

   @Override
   public void warn(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void warn(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   @Override
   public void warn(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   @Override
   public void warn(
      Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11
   ) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   @Override
   public void warn(
      Marker var1,
      String var2,
      Object var3,
      Object var4,
      Object var5,
      Object var6,
      Object var7,
      Object var8,
      Object var9,
      Object var10,
      Object var11,
      Object var12
   ) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
   }

   @Override
   public void warn(String var1, Object var2) {
      this.logIfEnabled(FQCN, Level.WARN, null, var1, var2);
   }

   @Override
   public void warn(String var1, Object var2, Object var3) {
      this.logIfEnabled(FQCN, Level.WARN, null, var1, var2, var3);
   }

   @Override
   public void warn(String var1, Object var2, Object var3, Object var4) {
      this.logIfEnabled(FQCN, Level.WARN, null, var1, var2, var3, var4);
   }

   @Override
   public void warn(String var1, Object var2, Object var3, Object var4, Object var5) {
      this.logIfEnabled(FQCN, Level.WARN, null, var1, var2, var3, var4, var5);
   }

   @Override
   public void warn(String var1, Object var2, Object var3, Object var4, Object var5, Object var6) {
      this.logIfEnabled(FQCN, Level.WARN, null, var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void warn(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      this.logIfEnabled(FQCN, Level.WARN, null, var1, var2, var3, var4, var5, var6, var7);
   }

   @Override
   public void warn(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      this.logIfEnabled(FQCN, Level.WARN, null, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void warn(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      this.logIfEnabled(FQCN, Level.WARN, null, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   @Override
   public void warn(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      this.logIfEnabled(FQCN, Level.WARN, null, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   @Override
   public void warn(
      String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11
   ) {
      this.logIfEnabled(FQCN, Level.WARN, null, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   protected boolean requiresLocation() {
      return false;
   }

   @Override
   public LogBuilder atTrace() {
      return this.atLevel(Level.TRACE);
   }

   @Override
   public LogBuilder atDebug() {
      return this.atLevel(Level.DEBUG);
   }

   @Override
   public LogBuilder atInfo() {
      return this.atLevel(Level.INFO);
   }

   @Override
   public LogBuilder atWarn() {
      return this.atLevel(Level.WARN);
   }

   @Override
   public LogBuilder atError() {
      return this.atLevel(Level.ERROR);
   }

   @Override
   public LogBuilder atFatal() {
      return this.atLevel(Level.FATAL);
   }

   @Override
   public LogBuilder always() {
      DefaultLogBuilder var1 = this.logBuilder.get();
      return (LogBuilder)(var1.isInUse() ? new DefaultLogBuilder(this) : var1.reset(Level.OFF));
   }

   @Override
   public LogBuilder atLevel(Level var1) {
      return this.isEnabled(var1) ? this.getLogBuilder(var1).reset(var1) : LogBuilder.NOOP;
   }

   private DefaultLogBuilder getLogBuilder(Level var1) {
      DefaultLogBuilder var2 = this.logBuilder.get();
      return Constants.ENABLE_THREADLOCALS && !var2.isInUse() ? var2 : new DefaultLogBuilder(this, var1);
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      var1.defaultReadObject();

      try {
         Field var2 = this.getClass().getDeclaredField("logBuilder");
         var2.setAccessible(true);
         var2.set(this, new AbstractLogger.LocalLogBuilder(this));
      } catch (IllegalAccessException | NoSuchFieldException var3) {
         StatusLogger.getLogger().warn("Unable to initialize LogBuilder");
      }
   }

   private class LocalLogBuilder extends ThreadLocal<DefaultLogBuilder> {
      private AbstractLogger logger;

      LocalLogBuilder(AbstractLogger var2) {
         this.logger = var2;
      }

      protected DefaultLogBuilder initialValue() {
         return new DefaultLogBuilder(this.logger);
      }
   }
}
