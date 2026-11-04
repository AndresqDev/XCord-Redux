package net.md_5.bungee.api.score;

public class Objective {
   private final String name;
   private String value;
   private String type;

   public String getName() {
      return this.name;
   }

   public String getValue() {
      return this.value;
   }

   public String getType() {
      return this.type;
   }

   public void setValue(String var1) {
      this.value = var1;
   }

   public void setType(String var1) {
      this.type = var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Objective)) {
         return false;
      } else {
         Objective var2 = (Objective)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            String var3 = this.getName();
            String var4 = var2.getName();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               String var5 = this.getValue();
               String var6 = var2.getValue();
               if (var5 == null ? var6 == null : var5.equals(var6)) {
                  String var7 = this.getType();
                  String var8 = var2.getType();
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
      return var1 instanceof Objective;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      String var3 = this.getName();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      String var4 = this.getValue();
      var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
      String var5 = this.getType();
      return var2 * 59 + (var5 == null ? 43 : var5.hashCode());
   }

   @Override
   public String toString() {
      return "Objective(name=" + this.getName() + ", value=" + this.getValue() + ", type=" + this.getType() + ")";
   }

   public Objective(String var1, String var2, String var3) {
      this.name = var1;
      this.value = var2;
      this.type = var3;
   }
}
