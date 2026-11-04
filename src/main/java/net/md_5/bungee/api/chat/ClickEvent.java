package net.md_5.bungee.api.chat;

public final class ClickEvent {
   private final ClickEvent.Action action;
   private final String value;

   public ClickEvent.Action getAction() {
      return this.action;
   }

   public String getValue() {
      return this.value;
   }

   @Override
   public String toString() {
      return "ClickEvent(action=" + this.getAction() + ", value=" + this.getValue() + ")";
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ClickEvent)) {
         return false;
      } else {
         ClickEvent var2 = (ClickEvent)var1;
         ClickEvent.Action var3 = this.getAction();
         ClickEvent.Action var4 = var2.getAction();
         if (var3 == null ? var4 == null : var3.equals(var4)) {
            String var5 = this.getValue();
            String var6 = var2.getValue();
            return var5 == null ? var6 == null : var5.equals(var6);
         } else {
            return false;
         }
      }
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      ClickEvent.Action var3 = this.getAction();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      String var4 = this.getValue();
      return var2 * 59 + (var4 == null ? 43 : var4.hashCode());
   }

   public ClickEvent(ClickEvent.Action var1, String var2) {
      this.action = var1;
      this.value = var2;
   }

   public static enum Action {
      OPEN_URL,
      OPEN_FILE,
      RUN_COMMAND,
      SUGGEST_COMMAND,
      CHANGE_PAGE,
      COPY_TO_CLIPBOARD;
   }
}
