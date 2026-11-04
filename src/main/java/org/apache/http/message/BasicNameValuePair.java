package org.apache.http.message;

import java.io.Serializable;
import org.apache.http.NameValuePair;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;
import org.apache.http.util.LangUtils;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class BasicNameValuePair implements Serializable, Cloneable, NameValuePair {
   private static final long serialVersionUID = -6437800749411518984L;
   private final String name;
   private final String value;

   public BasicNameValuePair(String var1, String var2) {
      this.name = Args.notNull(var1, "Name");
      this.value = var2;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public String getValue() {
      return this.value;
   }

   @Override
   public String toString() {
      if (this.value == null) {
         return this.name;
      } else {
         int var1 = this.name.length() + 1 + this.value.length();
         StringBuilder var2 = new StringBuilder(var1);
         var2.append(this.name);
         var2.append("=");
         var2.append(this.value);
         return var2.toString();
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof NameValuePair)) {
         return false;
      } else {
         BasicNameValuePair var2 = (BasicNameValuePair)var1;
         return this.name.equals(var2.name) && LangUtils.equals(this.value, var2.value);
      }
   }

   @Override
   public int hashCode() {
      int var1 = 17;
      var1 = LangUtils.hashCode(var1, this.name);
      return LangUtils.hashCode(var1, this.value);
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }
}
