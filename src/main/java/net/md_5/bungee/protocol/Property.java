package net.md_5.bungee.protocol;

public class Property {
   private String name;
   private String value;
   private String signature;

   public Property(String var1, String var2) {
      this(var1, var2, null);
   }

   public String getName() {
      return this.name;
   }

   public String getValue() {
      return this.value;
   }

   public String getSignature() {
      return this.signature;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public void setValue(String var1) {
      this.value = var1;
   }

   public void setSignature(String var1) {
      this.signature = var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Property)) {
         return false;
      } else {
         Property var2 = (Property)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            String var3 = this.getName();
            String var4 = var2.getName();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               String var5 = this.getValue();
               String var6 = var2.getValue();
               if (var5 == null ? var6 == null : var5.equals(var6)) {
                  String var7 = this.getSignature();
                  String var8 = var2.getSignature();
                  return var7 == null ? var8 == null : var7.equals(var8);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof Property;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      String var3 = this.getName();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      String var4 = this.getValue();
      var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
      String var5 = this.getSignature();
      return var2 * 59 + (var5 == null ? 43 : var5.hashCode());
   }

   @Override
   public String toString() {
      return "Property(name=" + this.getName() + ", value=" + this.getValue() + ", signature=" + this.getSignature() + ")";
   }

   public Property(String var1, String var2, String var3) {
      this.name = var1;
      this.value = var2;
      this.signature = var3;
   }
}
