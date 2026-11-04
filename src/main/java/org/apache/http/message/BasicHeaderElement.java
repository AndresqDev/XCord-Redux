package org.apache.http.message;

import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.util.Args;
import org.apache.http.util.LangUtils;

public class BasicHeaderElement implements Cloneable, HeaderElement {
   private final String name;
   private final String value;
   private final NameValuePair[] parameters;

   public BasicHeaderElement(String var1, String var2, NameValuePair[] var3) {
      this.name = Args.notNull(var1, "Name");
      this.value = var2;
      if (var3 != null) {
         this.parameters = var3;
      } else {
         this.parameters = new NameValuePair[0];
      }
   }

   public BasicHeaderElement(String var1, String var2) {
      this(var1, var2, null);
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
   public NameValuePair[] getParameters() {
      return (NameValuePair[])this.parameters.clone();
   }

   @Override
   public int getParameterCount() {
      return this.parameters.length;
   }

   @Override
   public NameValuePair getParameter(int var1) {
      return this.parameters[var1];
   }

   @Override
   public NameValuePair getParameterByName(String var1) {
      Args.notNull(var1, "Name");
      NameValuePair var2 = null;

      for (NameValuePair var6 : this.parameters) {
         if (var6.getName().equalsIgnoreCase(var1)) {
            var2 = var6;
            break;
         }
      }

      return var2;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof HeaderElement)) {
         return false;
      } else {
         BasicHeaderElement var2 = (BasicHeaderElement)var1;
         return this.name.equals(var2.name) && LangUtils.equals(this.value, var2.value) && LangUtils.equals(this.parameters, var2.parameters);
      }
   }

   @Override
   public int hashCode() {
      int var1 = 17;
      var1 = LangUtils.hashCode(var1, this.name);
      var1 = LangUtils.hashCode(var1, this.value);

      for (NameValuePair var5 : this.parameters) {
         var1 = LangUtils.hashCode(var1, var5);
      }

      return var1;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(this.name);
      if (this.value != null) {
         var1.append("=");
         var1.append(this.value);
      }

      for (NameValuePair var5 : this.parameters) {
         var1.append("; ");
         var1.append(var5);
      }

      return var1.toString();
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }
}
