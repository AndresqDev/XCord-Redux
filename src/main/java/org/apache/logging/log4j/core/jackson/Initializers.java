package org.apache.logging.log4j.core.jackson;

import com.fasterxml.jackson.databind.Module.SetupContext;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.ExtendedStackTraceElement;
import org.apache.logging.log4j.core.impl.ThrowableProxy;
import org.apache.logging.log4j.core.time.Instant;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ObjectMessage;

class Initializers {
   static class SetupContextInitializer {
      void setupModule(SetupContext var1, boolean var2, boolean var3) {
         var1.setMixInAnnotations(StackTraceElement.class, StackTraceElementMixIn.class);
         var1.setMixInAnnotations(Marker.class, MarkerMixIn.class);
         var1.setMixInAnnotations(Level.class, LevelMixIn.class);
         var1.setMixInAnnotations(Instant.class, InstantMixIn.class);
         var1.setMixInAnnotations(LogEvent.class, LogEventWithContextListMixIn.class);
         var1.setMixInAnnotations(ExtendedStackTraceElement.class, ExtendedStackTraceElementMixIn.class);
         var1.setMixInAnnotations(
            ThrowableProxy.class,
            var2 ? (var3 ? ThrowableProxyWithStacktraceAsStringMixIn.class : ThrowableProxyMixIn.class) : ThrowableProxyWithoutStacktraceMixIn.class
         );
      }
   }

   static class SetupContextJsonInitializer {
      void setupModule(SetupContext var1, boolean var2, boolean var3) {
         var1.setMixInAnnotations(StackTraceElement.class, StackTraceElementMixIn.class);
         var1.setMixInAnnotations(Marker.class, MarkerMixIn.class);
         var1.setMixInAnnotations(Level.class, LevelMixIn.class);
         var1.setMixInAnnotations(Instant.class, InstantMixIn.class);
         var1.setMixInAnnotations(LogEvent.class, LogEventJsonMixIn.class);
         var1.setMixInAnnotations(ExtendedStackTraceElement.class, ExtendedStackTraceElementMixIn.class);
         var1.setMixInAnnotations(
            ThrowableProxy.class,
            var2 ? (var3 ? ThrowableProxyWithStacktraceAsStringMixIn.class : ThrowableProxyMixIn.class) : ThrowableProxyWithoutStacktraceMixIn.class
         );
      }
   }

   static class SimpleModuleInitializer {
      void initialize(SimpleModule var1, boolean var2) {
         var1.addDeserializer(StackTraceElement.class, new Log4jStackTraceElementDeserializer());
         var1.addDeserializer(ThreadContext.ContextStack.class, new MutableThreadContextStackDeserializer());
         if (var2) {
            var1.addSerializer(ObjectMessage.class, new ObjectMessageSerializer());
         }

         var1.addSerializer(Message.class, new MessageSerializer());
      }
   }
}
