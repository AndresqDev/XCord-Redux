package io.netty.handler.codec.http.cookie;

import io.netty.util.internal.ObjectUtil;

public class DefaultCookie implements Cookie {
   private final String name;
   private String value;
   private boolean wrap;
   private String domain;
   private String path;
   private long maxAge = Long.MIN_VALUE;
   private boolean secure;
   private boolean httpOnly;
   private CookieHeaderNames.SameSite sameSite;

   public DefaultCookie(String var1, String var2) {
      this.name = ObjectUtil.checkNonEmptyAfterTrim(var1, "name");
      this.setValue(var2);
   }

   @Override
   public String name() {
      return this.name;
   }

   @Override
   public String value() {
      return this.value;
   }

   @Override
   public void setValue(String var1) {
      this.value = ObjectUtil.checkNotNull(var1, "value");
   }

   @Override
   public boolean wrap() {
      return this.wrap;
   }

   @Override
   public void setWrap(boolean var1) {
      this.wrap = var1;
   }

   @Override
   public String domain() {
      return this.domain;
   }

   @Override
   public void setDomain(String var1) {
      this.domain = CookieUtil.validateAttributeValue("domain", var1);
   }

   @Override
   public String path() {
      return this.path;
   }

   @Override
   public void setPath(String var1) {
      this.path = CookieUtil.validateAttributeValue("path", var1);
   }

   @Override
   public long maxAge() {
      return this.maxAge;
   }

   @Override
   public void setMaxAge(long var1) {
      this.maxAge = var1;
   }

   @Override
   public boolean isSecure() {
      return this.secure;
   }

   @Override
   public void setSecure(boolean var1) {
      this.secure = var1;
   }

   @Override
   public boolean isHttpOnly() {
      return this.httpOnly;
   }

   @Override
   public void setHttpOnly(boolean var1) {
      this.httpOnly = var1;
   }

   public CookieHeaderNames.SameSite sameSite() {
      return this.sameSite;
   }

   public void setSameSite(CookieHeaderNames.SameSite var1) {
      this.sameSite = var1;
   }

   @Override
   public int hashCode() {
      return this.name().hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Cookie)) {
         return false;
      } else {
         Cookie var2 = (Cookie)var1;
         if (!this.name().equals(var2.name())) {
            return false;
         } else {
            if (this.path() == null) {
               if (var2.path() != null) {
                  return false;
               }
            } else {
               if (var2.path() == null) {
                  return false;
               }

               if (!this.path().equals(var2.path())) {
                  return false;
               }
            }

            return this.domain() == null ? var2.domain() == null : this.domain().equalsIgnoreCase(var2.domain());
         }
      }
   }

   public int compareTo(Cookie var1) {
      int var2 = this.name().compareTo(var1.name());
      if (var2 != 0) {
         return var2;
      } else {
         if (this.path() == null) {
            if (var1.path() != null) {
               return -1;
            }
         } else {
            if (var1.path() == null) {
               return 1;
            }

            var2 = this.path().compareTo(var1.path());
            if (var2 != 0) {
               return var2;
            }
         }

         if (this.domain() == null) {
            return var1.domain() != null ? -1 : 0;
         } else {
            return var1.domain() == null ? 1 : this.domain().compareToIgnoreCase(var1.domain());
         }
      }
   }

   @Deprecated
   protected String validateValue(String var1, String var2) {
      return CookieUtil.validateAttributeValue(var1, var2);
   }

   @Override
   public String toString() {
      StringBuilder var1 = CookieUtil.stringBuilder().append(this.name()).append('=').append(this.value());
      if (this.domain() != null) {
         var1.append(", domain=").append(this.domain());
      }

      if (this.path() != null) {
         var1.append(", path=").append(this.path());
      }

      if (this.maxAge() >= 0L) {
         var1.append(", maxAge=").append(this.maxAge()).append('s');
      }

      if (this.isSecure()) {
         var1.append(", secure");
      }

      if (this.isHttpOnly()) {
         var1.append(", HTTPOnly");
      }

      if (this.sameSite() != null) {
         var1.append(", SameSite=").append(this.sameSite());
      }

      return var1.toString();
   }
}
