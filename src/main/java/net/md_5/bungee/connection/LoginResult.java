package net.md_5.bungee.connection;

import java.util.Arrays;
import net.md_5.bungee.protocol.Property;

public class LoginResult {
   private String id;
   private String name;
   private Property[] properties;

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public Property[] getProperties() {
      return this.properties;
   }

   public void setId(String var1) {
      this.id = var1;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public void setProperties(Property[] var1) {
      this.properties = var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof LoginResult)) {
         return false;
      } else {
         LoginResult var2 = (LoginResult)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            String var3 = this.getId();
            String var4 = var2.getId();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               String var5 = this.getName();
               String var6 = var2.getName();
               return (var5 == null ? var6 == null : var5.equals(var6)) ? Arrays.deepEquals(this.getProperties(), var2.getProperties()) : false;
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof LoginResult;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      String var3 = this.getId();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      String var4 = this.getName();
      var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
      return var2 * 59 + Arrays.deepHashCode(this.getProperties());
   }

   @Override
   public String toString() {
      return "LoginResult(id=" + this.getId() + ", name=" + this.getName() + ", properties=" + Arrays.deepToString(this.getProperties()) + ")";
   }

   public LoginResult(String var1, String var2, Property[] var3) {
      this.id = var1;
      this.name = var2;
      this.properties = var3;
   }
}
