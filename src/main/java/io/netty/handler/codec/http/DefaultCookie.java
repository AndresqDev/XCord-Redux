package io.netty.handler.codec.http;

import io.netty.util.internal.ObjectUtil;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

@Deprecated
public class DefaultCookie extends io.netty.handler.codec.http.cookie.DefaultCookie implements Cookie {
   private String comment;
   private String commentUrl;
   private boolean discard;
   private Set<Integer> ports = Collections.emptySet();
   private Set<Integer> unmodifiablePorts = this.ports;
   private int version;

   public DefaultCookie(String var1, String var2) {
      super(var1, var2);
   }

   @Deprecated
   @Override
   public String getName() {
      return this.name();
   }

   @Deprecated
   @Override
   public String getValue() {
      return this.value();
   }

   @Deprecated
   @Override
   public String getDomain() {
      return this.domain();
   }

   @Deprecated
   @Override
   public String getPath() {
      return this.path();
   }

   @Deprecated
   @Override
   public String getComment() {
      return this.comment();
   }

   @Deprecated
   @Override
   public String comment() {
      return this.comment;
   }

   @Deprecated
   @Override
   public void setComment(String var1) {
      this.comment = this.validateValue("comment", var1);
   }

   @Deprecated
   @Override
   public String getCommentUrl() {
      return this.commentUrl();
   }

   @Deprecated
   @Override
   public String commentUrl() {
      return this.commentUrl;
   }

   @Deprecated
   @Override
   public void setCommentUrl(String var1) {
      this.commentUrl = this.validateValue("commentUrl", var1);
   }

   @Deprecated
   @Override
   public boolean isDiscard() {
      return this.discard;
   }

   @Deprecated
   @Override
   public void setDiscard(boolean var1) {
      this.discard = var1;
   }

   @Deprecated
   @Override
   public Set<Integer> getPorts() {
      return this.ports();
   }

   @Deprecated
   @Override
   public Set<Integer> ports() {
      if (this.unmodifiablePorts == null) {
         this.unmodifiablePorts = Collections.unmodifiableSet(this.ports);
      }

      return this.unmodifiablePorts;
   }

   @Deprecated
   @Override
   public void setPorts(int... var1) {
      ObjectUtil.checkNotNull(var1, "ports");
      int[] var2 = (int[])var1.clone();
      if (var2.length == 0) {
         this.unmodifiablePorts = this.ports = Collections.emptySet();
      } else {
         TreeSet var3 = new TreeSet();

         for (int var7 : var2) {
            if (var7 <= 0 || var7 > 65535) {
               throw new IllegalArgumentException("port out of range: " + var7);
            }

            var3.add(var7);
         }

         this.ports = var3;
         this.unmodifiablePorts = null;
      }
   }

   @Deprecated
   @Override
   public void setPorts(Iterable<Integer> var1) {
      TreeSet var2 = new TreeSet();

      for (int var4 : var1) {
         if (var4 <= 0 || var4 > 65535) {
            throw new IllegalArgumentException("port out of range: " + var4);
         }

         var2.add(var4);
      }

      if (var2.isEmpty()) {
         this.unmodifiablePorts = this.ports = Collections.emptySet();
      } else {
         this.ports = var2;
         this.unmodifiablePorts = null;
      }
   }

   @Deprecated
   @Override
   public long getMaxAge() {
      return this.maxAge();
   }

   @Deprecated
   @Override
   public int getVersion() {
      return this.version();
   }

   @Deprecated
   @Override
   public int version() {
      return this.version;
   }

   @Deprecated
   @Override
   public void setVersion(int var1) {
      this.version = var1;
   }
}
