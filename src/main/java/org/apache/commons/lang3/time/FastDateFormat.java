package org.apache.commons.lang3.time;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FastDateFormat extends Format implements DateParser, DatePrinter {
   private static final long serialVersionUID = 2L;
   public static final int FULL = 0;
   public static final int LONG = 1;
   public static final int MEDIUM = 2;
   public static final int SHORT = 3;
   private static final FormatCache<FastDateFormat> cache = new FormatCache<FastDateFormat>() {
      protected FastDateFormat createInstance(String var1, TimeZone var2, Locale var3) {
         return new FastDateFormat(var1, var2, var3);
      }
   };
   private final FastDatePrinter printer;
   private final FastDateParser parser;

   public static FastDateFormat getInstance() {
      return cache.getInstance();
   }

   public static FastDateFormat getInstance(String var0) {
      return cache.getInstance(var0, null, null);
   }

   public static FastDateFormat getInstance(String var0, TimeZone var1) {
      return cache.getInstance(var0, var1, null);
   }

   public static FastDateFormat getInstance(String var0, Locale var1) {
      return cache.getInstance(var0, null, var1);
   }

   public static FastDateFormat getInstance(String var0, TimeZone var1, Locale var2) {
      return cache.getInstance(var0, var1, var2);
   }

   public static FastDateFormat getDateInstance(int var0) {
      return cache.getDateInstance(var0, null, null);
   }

   public static FastDateFormat getDateInstance(int var0, Locale var1) {
      return cache.getDateInstance(var0, null, var1);
   }

   public static FastDateFormat getDateInstance(int var0, TimeZone var1) {
      return cache.getDateInstance(var0, var1, null);
   }

   public static FastDateFormat getDateInstance(int var0, TimeZone var1, Locale var2) {
      return cache.getDateInstance(var0, var1, var2);
   }

   public static FastDateFormat getTimeInstance(int var0) {
      return cache.getTimeInstance(var0, null, null);
   }

   public static FastDateFormat getTimeInstance(int var0, Locale var1) {
      return cache.getTimeInstance(var0, null, var1);
   }

   public static FastDateFormat getTimeInstance(int var0, TimeZone var1) {
      return cache.getTimeInstance(var0, var1, null);
   }

   public static FastDateFormat getTimeInstance(int var0, TimeZone var1, Locale var2) {
      return cache.getTimeInstance(var0, var1, var2);
   }

   public static FastDateFormat getDateTimeInstance(int var0, int var1) {
      return cache.getDateTimeInstance(var0, var1, null, null);
   }

   public static FastDateFormat getDateTimeInstance(int var0, int var1, Locale var2) {
      return cache.getDateTimeInstance(var0, var1, null, var2);
   }

   public static FastDateFormat getDateTimeInstance(int var0, int var1, TimeZone var2) {
      return getDateTimeInstance(var0, var1, var2, null);
   }

   public static FastDateFormat getDateTimeInstance(int var0, int var1, TimeZone var2, Locale var3) {
      return cache.getDateTimeInstance(var0, var1, var2, var3);
   }

   protected FastDateFormat(String var1, TimeZone var2, Locale var3) {
      this(var1, var2, var3, null);
   }

   protected FastDateFormat(String var1, TimeZone var2, Locale var3, Date var4) {
      this.printer = new FastDatePrinter(var1, var2, var3);
      this.parser = new FastDateParser(var1, var2, var3, var4);
   }

   @Override
   public StringBuffer format(Object var1, StringBuffer var2, FieldPosition var3) {
      return var2.append(this.printer.format(var1));
   }

   @Override
   public String format(long var1) {
      return this.printer.format(var1);
   }

   @Override
   public String format(Date var1) {
      return this.printer.format(var1);
   }

   @Override
   public String format(Calendar var1) {
      return this.printer.format(var1);
   }

   @Deprecated
   @Override
   public StringBuffer format(long var1, StringBuffer var3) {
      return this.printer.format(var1, var3);
   }

   @Deprecated
   @Override
   public StringBuffer format(Date var1, StringBuffer var2) {
      return this.printer.format(var1, var2);
   }

   @Deprecated
   @Override
   public StringBuffer format(Calendar var1, StringBuffer var2) {
      return this.printer.format(var1, var2);
   }

   @Override
   public <B extends Appendable> B format(long var1, B var3) {
      return this.printer.format(var1, (B)var3);
   }

   @Override
   public <B extends Appendable> B format(Date var1, B var2) {
      return this.printer.format(var1, (B)var2);
   }

   @Override
   public <B extends Appendable> B format(Calendar var1, B var2) {
      return this.printer.format(var1, (B)var2);
   }

   @Override
   public Date parse(String var1) throws ParseException {
      return this.parser.parse(var1);
   }

   @Override
   public Date parse(String var1, ParsePosition var2) {
      return this.parser.parse(var1, var2);
   }

   @Override
   public boolean parse(String var1, ParsePosition var2, Calendar var3) {
      return this.parser.parse(var1, var2, var3);
   }

   @Override
   public Object parseObject(String var1, ParsePosition var2) {
      return this.parser.parseObject(var1, var2);
   }

   @Override
   public String getPattern() {
      return this.printer.getPattern();
   }

   @Override
   public TimeZone getTimeZone() {
      return this.printer.getTimeZone();
   }

   @Override
   public Locale getLocale() {
      return this.printer.getLocale();
   }

   public int getMaxLengthEstimate() {
      return this.printer.getMaxLengthEstimate();
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof FastDateFormat)) {
         return false;
      } else {
         FastDateFormat var2 = (FastDateFormat)var1;
         return this.printer.equals(var2.printer);
      }
   }

   @Override
   public int hashCode() {
      return this.printer.hashCode();
   }

   @Override
   public String toString() {
      return "FastDateFormat[" + this.printer.getPattern() + "," + this.printer.getLocale() + "," + this.printer.getTimeZone().getID() + "]";
   }

   @Deprecated
   protected StringBuffer applyRules(Calendar var1, StringBuffer var2) {
      return this.printer.applyRules(var1, var2);
   }
}
