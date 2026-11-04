package org.apache.logging.log4j.core.parser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import java.io.IOException;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;

class AbstractJacksonLogEventParser implements TextLogEventParser {
   private final ObjectReader objectReader;

   AbstractJacksonLogEventParser(ObjectMapper var1) {
      var1.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      this.objectReader = var1.readerFor(Log4jLogEvent.class);
   }

   @Override
   public LogEvent parseFrom(String var1) throws ParseException {
      try {
         return (LogEvent)this.objectReader.readValue(var1);
      } catch (IOException var3) {
         throw new ParseException(var3);
      }
   }

   @Override
   public LogEvent parseFrom(byte[] var1) throws ParseException {
      try {
         return (LogEvent)this.objectReader.readValue(var1);
      } catch (IOException var3) {
         throw new ParseException(var3);
      }
   }

   @Override
   public LogEvent parseFrom(byte[] var1, int var2, int var3) throws ParseException {
      try {
         return (LogEvent)this.objectReader.readValue(var1, var2, var3);
      } catch (IOException var5) {
         throw new ParseException(var5);
      }
   }
}
