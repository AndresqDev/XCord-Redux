package net.md_5.bungee.api.score;

public class Score {
   private final String itemName;
   private final String scoreName;
   private final int value;

   public Score(String var1, String var2, int var3) {
      this.itemName = var1;
      this.scoreName = var2;
      this.value = var3;
   }

   public String getItemName() {
      return this.itemName;
   }

   public String getScoreName() {
      return this.scoreName;
   }

   public int getValue() {
      return this.value;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Score)) {
         return false;
      } else {
         Score var2 = (Score)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.getValue() != var2.getValue()) {
            return false;
         } else {
            String var3 = this.getItemName();
            String var4 = var2.getItemName();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               String var5 = this.getScoreName();
               String var6 = var2.getScoreName();
               return var5 == null ? var6 == null : var5.equals(var6);
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof Score;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + this.getValue();
      String var3 = this.getItemName();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      String var4 = this.getScoreName();
      return var2 * 59 + (var4 == null ? 43 : var4.hashCode());
   }

   @Override
   public String toString() {
      return "Score(itemName=" + this.getItemName() + ", scoreName=" + this.getScoreName() + ", value=" + this.getValue() + ")";
   }
}
