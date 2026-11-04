package org.apache.logging.log4j.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Objects;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LoggingException;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.util.StackLocatorUtil;

public class IoBuilder {
   private final ExtendedLogger logger;
   private Level level;
   private Marker marker;
   private String fqcn;
   private boolean autoFlush;
   private boolean buffered;
   private int bufferSize;
   private Charset charset;
   private Reader reader;
   private Writer writer;
   private InputStream inputStream;
   private OutputStream outputStream;

   public static IoBuilder forLogger(Logger var0) {
      return new IoBuilder(var0);
   }

   public static IoBuilder forLogger(String var0) {
      return new IoBuilder(LogManager.getLogger(var0));
   }

   public static IoBuilder forLogger(Class<?> var0) {
      return new IoBuilder(LogManager.getLogger(var0));
   }

   public static IoBuilder forLogger() {
      return new IoBuilder(LogManager.getLogger(StackLocatorUtil.getCallerClass(2)));
   }

   protected IoBuilder(Logger var1) {
      if (!(var1 instanceof ExtendedLogger)) {
         throw new UnsupportedOperationException("The provided Logger [" + String.valueOf(var1) + "] does not implement " + ExtendedLogger.class.getName());
      } else {
         this.logger = (ExtendedLogger)var1;
      }
   }

   public IoBuilder setLevel(Level var1) {
      this.level = var1;
      return this;
   }

   public IoBuilder setMarker(Marker var1) {
      this.marker = var1;
      return this;
   }

   public IoBuilder setWrapperClassName(String var1) {
      this.fqcn = var1;
      return this;
   }

   public IoBuilder setAutoFlush(boolean var1) {
      this.autoFlush = var1;
      return this;
   }

   public IoBuilder setBuffered(boolean var1) {
      this.buffered = var1;
      return this;
   }

   public IoBuilder setBufferSize(int var1) {
      this.bufferSize = var1;
      return this;
   }

   public IoBuilder setCharset(Charset var1) {
      this.charset = var1;
      return this;
   }

   public IoBuilder filter(Reader var1) {
      this.reader = var1;
      return this;
   }

   public IoBuilder filter(Writer var1) {
      this.writer = var1;
      return this;
   }

   public IoBuilder filter(InputStream var1) {
      this.inputStream = var1;
      return this;
   }

   public IoBuilder filter(OutputStream var1) {
      this.outputStream = var1;
      return this;
   }

   public Reader buildReader() {
      Reader var1 = Objects.requireNonNull(this.reader, "reader");
      if (this.buffered) {
         return this.bufferSize > 0
            ? new LoggerBufferedReader(var1, this.bufferSize, this.logger, this.fqcn, this.level, this.marker)
            : new LoggerBufferedReader(var1, this.logger, this.fqcn, this.level, this.marker);
      } else {
         return new LoggerReader(var1, this.logger, this.fqcn, this.level, this.marker);
      }
   }

   public Writer buildWriter() {
      return (Writer)(this.writer == null
         ? new LoggerWriter(this.logger, this.fqcn, this.level, this.marker)
         : new LoggerFilterWriter(this.writer, this.logger, this.fqcn, this.level, this.marker));
   }

   public PrintWriter buildPrintWriter() {
      return this.writer == null
         ? new LoggerPrintWriter(this.logger, this.autoFlush, this.fqcn, this.level, this.marker)
         : new LoggerPrintWriter(this.writer, this.autoFlush, this.logger, this.fqcn, this.level, this.marker);
   }

   public InputStream buildInputStream() {
      InputStream var1 = Objects.requireNonNull(this.inputStream, "inputStream");
      if (this.buffered) {
         return this.bufferSize > 0
            ? new LoggerBufferedInputStream(var1, this.charset, this.bufferSize, this.logger, this.fqcn, this.level, this.marker)
            : new LoggerBufferedInputStream(var1, this.charset, this.logger, this.fqcn, this.level, this.marker);
      } else {
         return new LoggerInputStream(var1, this.charset, this.logger, this.fqcn, this.level, this.marker);
      }
   }

   public OutputStream buildOutputStream() {
      return (OutputStream)(this.outputStream == null
         ? new LoggerOutputStream(this.logger, this.level, this.marker, this.charset, this.fqcn)
         : new LoggerFilterOutputStream(this.outputStream, this.charset, this.logger, this.fqcn, this.level, this.marker));
   }

   public PrintStream buildPrintStream() {
      try {
         return this.outputStream == null
            ? new LoggerPrintStream(this.logger, this.autoFlush, this.charset, this.fqcn, this.level, this.marker)
            : new LoggerPrintStream(this.outputStream, this.autoFlush, this.charset, this.logger, this.fqcn, this.level, this.marker);
      } catch (UnsupportedEncodingException var2) {
         throw new LoggingException(var2);
      }
   }
}
