package net.md_5.bungee.protocol;

public class Location {
   private final String dimension;
   private final long pos;

   public Location(String var1, long var2) {
      this.dimension = var1;
      this.pos = var2;
   }

   public String getDimension() {
      return this.dimension;
   }

   public long getPos() {
      return this.pos;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Location)) {
         return false;
      } else {
         Location var2 = (Location)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.getPos() != var2.getPos()) {
            return false;
         } else {
            String var3 = this.getDimension();
            String var4 = var2.getDimension();
            return var3 == null ? var4 == null : var3.equals(var4);
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof Location;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      long var3 = this.getPos();
      var2 = var2 * 59 + (int)(var3 >>> 32 ^ var3);
      String var5 = this.getDimension();
      return var2 * 59 + (var5 == null ? 43 : var5.hashCode());
   }

   @Override
   public String toString() {
      return "Location(dimension=" + this.getDimension() + ", pos=" + this.getPos() + ")";
   }
}
