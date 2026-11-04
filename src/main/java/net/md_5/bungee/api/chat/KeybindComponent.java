package net.md_5.bungee.api.chat;

public final class KeybindComponent extends BaseComponent {
   private String keybind;

   public KeybindComponent(KeybindComponent var1) {
      super(var1);
      this.setKeybind(var1.getKeybind());
   }

   public KeybindComponent(String var1) {
      this.setKeybind(var1);
   }

   public KeybindComponent duplicate() {
      return new KeybindComponent(this);
   }

   @Override
   protected void toPlainText(StringBuilder var1) {
      var1.append(this.getKeybind());
      super.toPlainText(var1);
   }

   @Override
   protected void toLegacyText(StringBuilder var1) {
      this.addFormat(var1);
      var1.append(this.getKeybind());
      super.toLegacyText(var1);
   }

   public String getKeybind() {
      return this.keybind;
   }

   public void setKeybind(String var1) {
      this.keybind = var1;
   }

   @Override
   public String toString() {
      return "KeybindComponent(keybind=" + this.getKeybind() + ")";
   }

   public KeybindComponent() {
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof KeybindComponent)) {
         return false;
      } else {
         KeybindComponent var2 = (KeybindComponent)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (!super.equals(var1)) {
            return false;
         } else {
            String var3 = this.getKeybind();
            String var4 = var2.getKeybind();
            return var3 == null ? var4 == null : var3.equals(var4);
         }
      }
   }

   @Override
   protected boolean canEqual(Object var1) {
      return var1 instanceof KeybindComponent;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = super.hashCode();
      String var3 = this.getKeybind();
      return var2 * 59 + (var3 == null ? 43 : var3.hashCode());
   }
}
