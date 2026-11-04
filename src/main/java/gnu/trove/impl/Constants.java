package gnu.trove.impl;

public class Constants {
   private static final boolean VERBOSE;
   public static final int DEFAULT_CAPACITY = 10;
   public static final float DEFAULT_LOAD_FACTOR = 0.5F;
   public static final byte DEFAULT_BYTE_NO_ENTRY_VALUE;
   public static final short DEFAULT_SHORT_NO_ENTRY_VALUE;
   public static final char DEFAULT_CHAR_NO_ENTRY_VALUE;
   public static final int DEFAULT_INT_NO_ENTRY_VALUE;
   public static final long DEFAULT_LONG_NO_ENTRY_VALUE;
   public static final float DEFAULT_FLOAT_NO_ENTRY_VALUE;
   public static final double DEFAULT_DOUBLE_NO_ENTRY_VALUE;

   static {
      int var0 = 0;

      try {
         var0 = System.getProperty("gnu.trove.verbose", null) != null;
      } catch (SecurityException var11) {
      }

      VERBOSE = (boolean)var0;
      String var1 = "0";

      try {
         var1 = System.getProperty("gnu.trove.no_entry.byte", var1);
      } catch (SecurityException var10) {
      }

      if ("MAX_VALUE".equalsIgnoreCase(var1)) {
         var0 = (byte)127;
      } else if ("MIN_VALUE".equalsIgnoreCase(var1)) {
         var0 = (byte)-128;
      } else {
         var0 = Byte.valueOf(var1);
      }

      if (var0 > 127) {
         var0 = (byte)127;
      } else if (var0 < -128) {
         var0 = (byte)-128;
      }

      DEFAULT_BYTE_NO_ENTRY_VALUE = var0;
      if (VERBOSE) {
         System.out.println("DEFAULT_BYTE_NO_ENTRY_VALUE: " + DEFAULT_BYTE_NO_ENTRY_VALUE);
      }

      var1 = "0";

      try {
         var1 = System.getProperty("gnu.trove.no_entry.short", var1);
      } catch (SecurityException var9) {
      }

      if ("MAX_VALUE".equalsIgnoreCase(var1)) {
         var0 = (short)32767;
      } else if ("MIN_VALUE".equalsIgnoreCase(var1)) {
         var0 = (short)-32768;
      } else {
         var0 = Short.valueOf(var1);
      }

      if (var0 > 32767) {
         var0 = (short)32767;
      } else if (var0 < -32768) {
         var0 = (short)-32768;
      }

      DEFAULT_SHORT_NO_ENTRY_VALUE = var0;
      if (VERBOSE) {
         System.out.println("DEFAULT_SHORT_NO_ENTRY_VALUE: " + DEFAULT_SHORT_NO_ENTRY_VALUE);
      }

      var1 = "\u0000";

      try {
         var1 = System.getProperty("gnu.trove.no_entry.char", var1);
      } catch (SecurityException var8) {
      }

      char var14;
      if ("MAX_VALUE".equalsIgnoreCase(var1)) {
         var14 = '\uffff';
      } else if ("MIN_VALUE".equalsIgnoreCase(var1)) {
         var14 = 0;
      } else {
         var14 = var1.toCharArray()[0];
      }

      if (var14 > '\uffff') {
         var14 = '\uffff';
      } else if (var14 < 0) {
         var14 = 0;
      }

      DEFAULT_CHAR_NO_ENTRY_VALUE = var14;
      if (VERBOSE) {
         System.out.println("DEFAULT_CHAR_NO_ENTRY_VALUE: " + Integer.valueOf(var14));
      }

      var1 = "0";

      try {
         var1 = System.getProperty("gnu.trove.no_entry.int", var1);
      } catch (SecurityException var7) {
      }

      if ("MAX_VALUE".equalsIgnoreCase(var1)) {
         var0 = Integer.MAX_VALUE;
      } else if ("MIN_VALUE".equalsIgnoreCase(var1)) {
         var0 = Integer.MIN_VALUE;
      } else {
         var0 = Integer.valueOf(var1);
      }

      DEFAULT_INT_NO_ENTRY_VALUE = var0;
      if (VERBOSE) {
         System.out.println("DEFAULT_INT_NO_ENTRY_VALUE: " + DEFAULT_INT_NO_ENTRY_VALUE);
      }

      String var2 = "0";

      try {
         var2 = System.getProperty("gnu.trove.no_entry.long", var2);
      } catch (SecurityException var6) {
      }

      long var16;
      if ("MAX_VALUE".equalsIgnoreCase(var2)) {
         var16 = Long.MAX_VALUE;
      } else if ("MIN_VALUE".equalsIgnoreCase(var2)) {
         var16 = Long.MIN_VALUE;
      } else {
         var16 = Long.valueOf(var2);
      }

      DEFAULT_LONG_NO_ENTRY_VALUE = var16;
      if (VERBOSE) {
         System.out.println("DEFAULT_LONG_NO_ENTRY_VALUE: " + DEFAULT_LONG_NO_ENTRY_VALUE);
      }

      var1 = "0";

      try {
         var1 = System.getProperty("gnu.trove.no_entry.float", var1);
      } catch (SecurityException var5) {
      }

      float var17;
      if ("MAX_VALUE".equalsIgnoreCase(var1)) {
         var17 = Float.MAX_VALUE;
      } else if ("MIN_VALUE".equalsIgnoreCase(var1)) {
         var17 = Float.MIN_VALUE;
      } else if ("MIN_NORMAL".equalsIgnoreCase(var1)) {
         var17 = Float.MIN_NORMAL;
      } else if ("NEGATIVE_INFINITY".equalsIgnoreCase(var1)) {
         var17 = Float.NEGATIVE_INFINITY;
      } else if ("POSITIVE_INFINITY".equalsIgnoreCase(var1)) {
         var17 = Float.POSITIVE_INFINITY;
      } else {
         var17 = Float.valueOf(var1);
      }

      DEFAULT_FLOAT_NO_ENTRY_VALUE = var17;
      if (VERBOSE) {
         System.out.println("DEFAULT_FLOAT_NO_ENTRY_VALUE: " + DEFAULT_FLOAT_NO_ENTRY_VALUE);
      }

      var2 = "0";

      try {
         var2 = System.getProperty("gnu.trove.no_entry.double", var2);
      } catch (SecurityException var4) {
      }

      double var18;
      if ("MAX_VALUE".equalsIgnoreCase(var2)) {
         var18 = Double.MAX_VALUE;
      } else if ("MIN_VALUE".equalsIgnoreCase(var2)) {
         var18 = Double.MIN_VALUE;
      } else if ("MIN_NORMAL".equalsIgnoreCase(var2)) {
         var18 = Double.MIN_NORMAL;
      } else if ("NEGATIVE_INFINITY".equalsIgnoreCase(var2)) {
         var18 = Double.NEGATIVE_INFINITY;
      } else if ("POSITIVE_INFINITY".equalsIgnoreCase(var2)) {
         var18 = Double.POSITIVE_INFINITY;
      } else {
         var18 = Double.valueOf(var2);
      }

      DEFAULT_DOUBLE_NO_ENTRY_VALUE = var18;
      if (VERBOSE) {
         System.out.println("DEFAULT_DOUBLE_NO_ENTRY_VALUE: " + DEFAULT_DOUBLE_NO_ENTRY_VALUE);
      }
   }
}
