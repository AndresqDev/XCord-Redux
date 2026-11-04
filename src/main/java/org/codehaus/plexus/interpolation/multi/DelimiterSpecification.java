package org.codehaus.plexus.interpolation.multi;

public final class DelimiterSpecification {
   public static final DelimiterSpecification DEFAULT_SPEC = parse("${*}");
   private String begin;
   private String end;
   private int nextStart;

   public DelimiterSpecification(String var1, String var2) {
      this.begin = var1;
      this.end = var2;
   }

   public int getNextStartIndex() {
      return this.nextStart;
   }

   public void setNextStartIndex(int var1) {
      this.nextStart = var1;
   }

   public void clearNextStart() {
      this.nextStart = -1;
   }

   public static DelimiterSpecification parse(String var0) {
      String[] var1 = new String[2];
      int var2 = var0.indexOf(42);
      if (var2 < 0) {
         var1[0] = var0;
         var1[1] = var1[0];
      } else if (var2 == var0.length() - 1) {
         var1[0] = var0.substring(0, var0.length() - 1);
         var1[1] = var1[0];
      } else {
         var1[0] = var0.substring(0, var2);
         var1[1] = var0.substring(var2 + 1);
      }

      return new DelimiterSpecification(var1[0], var1[1]);
   }

   public String getBegin() {
      return this.begin;
   }

   public String getEnd() {
      return this.end;
   }

   @Override
   public int hashCode() {
      byte var1 = 31;
      int var2 = 1;
      var2 = 31 * var2 + (this.begin == null ? 0 : this.begin.hashCode());
      return 31 * var2 + (this.end == null ? 0 : this.end.hashCode());
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         DelimiterSpecification var2 = (DelimiterSpecification)var1;
         if (this.begin == null) {
            if (var2.begin != null) {
               return false;
            }
         } else if (!this.begin.equals(var2.begin)) {
            return false;
         }

         if (this.end == null) {
            if (var2.end != null) {
               return false;
            }
         } else if (!this.end.equals(var2.end)) {
            return false;
         }

         return true;
      }
   }

   @Override
   public String toString() {
      return "Interpolation delimiter [begin: '" + this.begin + "', end: '" + this.end + "']";
   }
}
