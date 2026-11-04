package org.apache.logging.log4j.core.layout;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.dataformat.xml.util.DefaultXmlPrettyPrinter;
import java.util.HashSet;
import javax.xml.stream.XMLStreamException;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.jackson.Log4jJsonObjectMapper;
import org.apache.logging.log4j.core.jackson.Log4jXmlObjectMapper;
import org.apache.logging.log4j.core.jackson.Log4jYamlObjectMapper;
import org.codehaus.stax2.XMLStreamWriter2;

abstract class JacksonFactory {
   protected abstract String getPropertyNameForTimeMillis();

   protected abstract String getPropertyNameForInstant();

   protected abstract String getPropertNameForContextMap();

   protected abstract String getPropertNameForSource();

   protected abstract String getPropertNameForNanoTime();

   protected abstract PrettyPrinter newCompactPrinter();

   protected abstract ObjectMapper newObjectMapper();

   protected abstract PrettyPrinter newPrettyPrinter();

   ObjectWriter newWriter(boolean var1, boolean var2, boolean var3) {
      return this.newWriter(var1, var2, var3, false);
   }

   ObjectWriter newWriter(boolean var1, boolean var2, boolean var3, boolean var4) {
      SimpleFilterProvider var5 = new SimpleFilterProvider();
      HashSet var6 = new HashSet(3);
      if (!var1) {
         var6.add(this.getPropertNameForSource());
      }

      if (!var2) {
         var6.add(this.getPropertNameForContextMap());
      }

      if (var4) {
         var6.add(this.getPropertyNameForInstant());
      } else {
         var6.add(this.getPropertyNameForTimeMillis());
      }

      var6.add(this.getPropertNameForNanoTime());
      var5.addFilter(Log4jLogEvent.class.getName(), SimpleBeanPropertyFilter.serializeAllExcept(var6));
      ObjectWriter var7 = this.newObjectMapper().writer(var3 ? this.newCompactPrinter() : this.newPrettyPrinter());
      return var7.with(var5);
   }

   static class JSON extends JacksonFactory {
      private final boolean encodeThreadContextAsList;
      private final boolean includeStacktrace;
      private final boolean stacktraceAsString;
      private final boolean objectMessageAsJsonObject;

      public JSON(boolean var1, boolean var2, boolean var3, boolean var4) {
         this.encodeThreadContextAsList = var1;
         this.includeStacktrace = var2;
         this.stacktraceAsString = var3;
         this.objectMessageAsJsonObject = var4;
      }

      @Override
      protected String getPropertNameForContextMap() {
         return "contextMap";
      }

      @Override
      protected String getPropertyNameForTimeMillis() {
         return "timeMillis";
      }

      @Override
      protected String getPropertyNameForInstant() {
         return "instant";
      }

      @Override
      protected String getPropertNameForSource() {
         return "source";
      }

      @Override
      protected String getPropertNameForNanoTime() {
         return "nanoTime";
      }

      @Override
      protected PrettyPrinter newCompactPrinter() {
         return new MinimalPrettyPrinter();
      }

      @Override
      protected ObjectMapper newObjectMapper() {
         return new Log4jJsonObjectMapper(this.encodeThreadContextAsList, this.includeStacktrace, this.stacktraceAsString, this.objectMessageAsJsonObject);
      }

      @Override
      protected PrettyPrinter newPrettyPrinter() {
         return new DefaultPrettyPrinter();
      }
   }

   static class Log4jXmlPrettyPrinter extends DefaultXmlPrettyPrinter {
      private static final long serialVersionUID = 1L;

      Log4jXmlPrettyPrinter(int var1) {
         this._nesting = var1;
      }

      public void writePrologLinefeed(XMLStreamWriter2 var1) throws XMLStreamException {
      }

      public DefaultXmlPrettyPrinter createInstance() {
         return new JacksonFactory.Log4jXmlPrettyPrinter(1);
      }
   }

   static class XML extends JacksonFactory {
      static final int DEFAULT_INDENT = 1;
      private final boolean includeStacktrace;
      private final boolean stacktraceAsString;

      public XML(boolean var1, boolean var2) {
         this.includeStacktrace = var1;
         this.stacktraceAsString = var2;
      }

      @Override
      protected String getPropertyNameForTimeMillis() {
         return "TimeMillis";
      }

      @Override
      protected String getPropertyNameForInstant() {
         return "Instant";
      }

      @Override
      protected String getPropertNameForContextMap() {
         return "ContextMap";
      }

      @Override
      protected String getPropertNameForSource() {
         return "Source";
      }

      @Override
      protected String getPropertNameForNanoTime() {
         return "nanoTime";
      }

      @Override
      protected PrettyPrinter newCompactPrinter() {
         return null;
      }

      @Override
      protected ObjectMapper newObjectMapper() {
         return new Log4jXmlObjectMapper(this.includeStacktrace, this.stacktraceAsString);
      }

      @Override
      protected PrettyPrinter newPrettyPrinter() {
         return new JacksonFactory.Log4jXmlPrettyPrinter(1);
      }
   }

   static class YAML extends JacksonFactory {
      private final boolean includeStacktrace;
      private final boolean stacktraceAsString;

      public YAML(boolean var1, boolean var2) {
         this.includeStacktrace = var1;
         this.stacktraceAsString = var2;
      }

      @Override
      protected String getPropertyNameForTimeMillis() {
         return "timeMillis";
      }

      @Override
      protected String getPropertyNameForInstant() {
         return "instant";
      }

      @Override
      protected String getPropertNameForContextMap() {
         return "contextMap";
      }

      @Override
      protected String getPropertNameForSource() {
         return "source";
      }

      @Override
      protected String getPropertNameForNanoTime() {
         return "nanoTime";
      }

      @Override
      protected PrettyPrinter newCompactPrinter() {
         return new MinimalPrettyPrinter();
      }

      @Override
      protected ObjectMapper newObjectMapper() {
         return new Log4jYamlObjectMapper(false, this.includeStacktrace, this.stacktraceAsString);
      }

      @Override
      protected PrettyPrinter newPrettyPrinter() {
         return new DefaultPrettyPrinter();
      }
   }
}
