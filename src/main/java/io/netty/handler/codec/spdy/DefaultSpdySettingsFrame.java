package io.netty.handler.codec.spdy;

import io.netty.util.internal.StringUtil;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

public class DefaultSpdySettingsFrame implements SpdySettingsFrame {
   private boolean clear;
   private final Map<Integer, DefaultSpdySettingsFrame.Setting> settingsMap = new TreeMap<>();

   @Override
   public Set<Integer> ids() {
      return this.settingsMap.keySet();
   }

   @Override
   public boolean isSet(int var1) {
      return this.settingsMap.containsKey(var1);
   }

   @Override
   public int getValue(int var1) {
      DefaultSpdySettingsFrame.Setting var2 = this.settingsMap.get(var1);
      return var2 != null ? var2.getValue() : -1;
   }

   @Override
   public SpdySettingsFrame setValue(int var1, int var2) {
      return this.setValue(var1, var2, false, false);
   }

   @Override
   public SpdySettingsFrame setValue(int var1, int var2, boolean var3, boolean var4) {
      if (var1 >= 0 && var1 <= 16777215) {
         Integer var5 = var1;
         DefaultSpdySettingsFrame.Setting var6 = this.settingsMap.get(var5);
         if (var6 != null) {
            var6.setValue(var2);
            var6.setPersist(var3);
            var6.setPersisted(var4);
         } else {
            this.settingsMap.put(var5, new DefaultSpdySettingsFrame.Setting(var2, var3, var4));
         }

         return this;
      } else {
         throw new IllegalArgumentException("Setting ID is not valid: " + var1);
      }
   }

   @Override
   public SpdySettingsFrame removeValue(int var1) {
      this.settingsMap.remove(var1);
      return this;
   }

   @Override
   public boolean isPersistValue(int var1) {
      DefaultSpdySettingsFrame.Setting var2 = this.settingsMap.get(var1);
      return var2 != null && var2.isPersist();
   }

   @Override
   public SpdySettingsFrame setPersistValue(int var1, boolean var2) {
      DefaultSpdySettingsFrame.Setting var3 = this.settingsMap.get(var1);
      if (var3 != null) {
         var3.setPersist(var2);
      }

      return this;
   }

   @Override
   public boolean isPersisted(int var1) {
      DefaultSpdySettingsFrame.Setting var2 = this.settingsMap.get(var1);
      return var2 != null && var2.isPersisted();
   }

   @Override
   public SpdySettingsFrame setPersisted(int var1, boolean var2) {
      DefaultSpdySettingsFrame.Setting var3 = this.settingsMap.get(var1);
      if (var3 != null) {
         var3.setPersisted(var2);
      }

      return this;
   }

   @Override
   public boolean clearPreviouslyPersistedSettings() {
      return this.clear;
   }

   @Override
   public SpdySettingsFrame setClearPreviouslyPersistedSettings(boolean var1) {
      this.clear = var1;
      return this;
   }

   private Set<Entry<Integer, DefaultSpdySettingsFrame.Setting>> getSettings() {
      return this.settingsMap.entrySet();
   }

   private void appendSettings(StringBuilder var1) {
      for (Entry var3 : this.getSettings()) {
         DefaultSpdySettingsFrame.Setting var4 = (DefaultSpdySettingsFrame.Setting)var3.getValue();
         var1.append("--> ");
         var1.append(var3.getKey());
         var1.append(':');
         var1.append(var4.getValue());
         var1.append(" (persist value: ");
         var1.append(var4.isPersist());
         var1.append("; persisted: ");
         var1.append(var4.isPersisted());
         var1.append(')');
         var1.append(StringUtil.NEWLINE);
      }
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder().append(StringUtil.simpleClassName(this)).append(StringUtil.NEWLINE);
      this.appendSettings(var1);
      var1.setLength(var1.length() - StringUtil.NEWLINE.length());
      return var1.toString();
   }

   private static final class Setting {
      private int value;
      private boolean persist;
      private boolean persisted;

      Setting(int var1, boolean var2, boolean var3) {
         this.value = var1;
         this.persist = var2;
         this.persisted = var3;
      }

      int getValue() {
         return this.value;
      }

      void setValue(int var1) {
         this.value = var1;
      }

      boolean isPersist() {
         return this.persist;
      }

      void setPersist(boolean var1) {
         this.persist = var1;
      }

      boolean isPersisted() {
         return this.persisted;
      }

      void setPersisted(boolean var1) {
         this.persisted = var1;
      }
   }
}
