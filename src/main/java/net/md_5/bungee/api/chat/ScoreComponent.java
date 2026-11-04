package net.md_5.bungee.api.chat;

public final class ScoreComponent extends BaseComponent {
   private String name;
   private String objective;
   private String value = "";

   public ScoreComponent(String var1, String var2) {
      this.setName(var1);
      this.setObjective(var2);
   }

   public ScoreComponent(ScoreComponent var1) {
      super(var1);
      this.setName(var1.getName());
      this.setObjective(var1.getObjective());
      this.setValue(var1.getValue());
   }

   public ScoreComponent duplicate() {
      return new ScoreComponent(this);
   }

   @Override
   protected void toPlainText(StringBuilder var1) {
      var1.append(this.value);
      super.toPlainText(var1);
   }

   @Override
   protected void toLegacyText(StringBuilder var1) {
      this.addFormat(var1);
      var1.append(this.value);
      super.toLegacyText(var1);
   }

   public String getName() {
      return this.name;
   }

   public String getObjective() {
      return this.objective;
   }

   public String getValue() {
      return this.value;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public void setObjective(String var1) {
      this.objective = var1;
   }

   public void setValue(String var1) {
      this.value = var1;
   }

   @Override
   public String toString() {
      return "ScoreComponent(name=" + this.getName() + ", objective=" + this.getObjective() + ", value=" + this.getValue() + ")";
   }

   public ScoreComponent(String var1, String var2, String var3) {
      this.name = var1;
      this.objective = var2;
      this.value = var3;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ScoreComponent)) {
         return false;
      } else {
         ScoreComponent var2 = (ScoreComponent)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (!super.equals(var1)) {
            return false;
         } else {
            String var3 = this.getName();
            String var4 = var2.getName();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               String var5 = this.getObjective();
               String var6 = var2.getObjective();
               if (var5 == null ? var6 == null : var5.equals(var6)) {
                  String var7 = this.getValue();
                  String var8 = var2.getValue();
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

   @Override
   protected boolean canEqual(Object var1) {
      return var1 instanceof ScoreComponent;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = super.hashCode();
      String var3 = this.getName();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      String var4 = this.getObjective();
      var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
      String var5 = this.getValue();
      return var2 * 59 + (var5 == null ? 43 : var5.hashCode());
   }
}
