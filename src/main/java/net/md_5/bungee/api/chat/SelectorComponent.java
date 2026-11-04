package net.md_5.bungee.api.chat;

public final class SelectorComponent extends BaseComponent {
   private String selector;

   public SelectorComponent(SelectorComponent var1) {
      super(var1);
      this.setSelector(var1.getSelector());
   }

   public SelectorComponent duplicate() {
      return new SelectorComponent(this);
   }

   @Override
   protected void toPlainText(StringBuilder var1) {
      var1.append(this.selector);
      super.toPlainText(var1);
   }

   @Override
   protected void toLegacyText(StringBuilder var1) {
      this.addFormat(var1);
      var1.append(this.selector);
      super.toLegacyText(var1);
   }

   public String getSelector() {
      return this.selector;
   }

   public void setSelector(String var1) {
      this.selector = var1;
   }

   @Override
   public String toString() {
      return "SelectorComponent(selector=" + this.getSelector() + ")";
   }

   public SelectorComponent(String var1) {
      this.selector = var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof SelectorComponent)) {
         return false;
      } else {
         SelectorComponent var2 = (SelectorComponent)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (!super.equals(var1)) {
            return false;
         } else {
            String var3 = this.getSelector();
            String var4 = var2.getSelector();
            return var3 == null ? var4 == null : var3.equals(var4);
         }
      }
   }

   @Override
   protected boolean canEqual(Object var1) {
      return var1 instanceof SelectorComponent;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = super.hashCode();
      String var3 = this.getSelector();
      return var2 * 59 + (var3 == null ? 43 : var3.hashCode());
   }
}
