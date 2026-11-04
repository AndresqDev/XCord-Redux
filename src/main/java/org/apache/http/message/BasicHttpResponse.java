package org.apache.http.message;

import java.util.Locale;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.ReasonPhraseCatalog;
import org.apache.http.StatusLine;
import org.apache.http.util.Args;
import org.apache.http.util.TextUtils;

public class BasicHttpResponse extends AbstractHttpMessage implements HttpResponse {
   private StatusLine statusline;
   private ProtocolVersion ver;
   private int code;
   private String reasonPhrase;
   private HttpEntity entity;
   private final ReasonPhraseCatalog reasonCatalog;
   private Locale locale;

   public BasicHttpResponse(StatusLine var1, ReasonPhraseCatalog var2, Locale var3) {
      this.statusline = Args.notNull(var1, "Status line");
      this.ver = var1.getProtocolVersion();
      this.code = var1.getStatusCode();
      this.reasonPhrase = var1.getReasonPhrase();
      this.reasonCatalog = var2;
      this.locale = var3;
   }

   public BasicHttpResponse(StatusLine var1) {
      this.statusline = Args.notNull(var1, "Status line");
      this.ver = var1.getProtocolVersion();
      this.code = var1.getStatusCode();
      this.reasonPhrase = var1.getReasonPhrase();
      this.reasonCatalog = null;
      this.locale = null;
   }

   public BasicHttpResponse(ProtocolVersion var1, int var2, String var3) {
      Args.notNegative(var2, "Status code");
      this.statusline = null;
      this.ver = var1;
      this.code = var2;
      this.reasonPhrase = var3;
      this.reasonCatalog = null;
      this.locale = null;
   }

   @Override
   public ProtocolVersion getProtocolVersion() {
      return this.ver;
   }

   @Override
   public StatusLine getStatusLine() {
      if (this.statusline == null) {
         this.statusline = new BasicStatusLine(
            (ProtocolVersion)(this.ver != null ? this.ver : HttpVersion.HTTP_1_1),
            this.code,
            this.reasonPhrase != null ? this.reasonPhrase : this.getReason(this.code)
         );
      }

      return this.statusline;
   }

   @Override
   public HttpEntity getEntity() {
      return this.entity;
   }

   @Override
   public Locale getLocale() {
      return this.locale;
   }

   @Override
   public void setStatusLine(StatusLine var1) {
      this.statusline = Args.notNull(var1, "Status line");
      this.ver = var1.getProtocolVersion();
      this.code = var1.getStatusCode();
      this.reasonPhrase = var1.getReasonPhrase();
   }

   @Override
   public void setStatusLine(ProtocolVersion var1, int var2) {
      Args.notNegative(var2, "Status code");
      this.statusline = null;
      this.ver = var1;
      this.code = var2;
      this.reasonPhrase = null;
   }

   @Override
   public void setStatusLine(ProtocolVersion var1, int var2, String var3) {
      Args.notNegative(var2, "Status code");
      this.statusline = null;
      this.ver = var1;
      this.code = var2;
      this.reasonPhrase = var3;
   }

   @Override
   public void setStatusCode(int var1) {
      Args.notNegative(var1, "Status code");
      this.statusline = null;
      this.code = var1;
      this.reasonPhrase = null;
   }

   @Override
   public void setReasonPhrase(String var1) {
      this.statusline = null;
      this.reasonPhrase = TextUtils.isBlank(var1) ? null : var1;
   }

   @Override
   public void setEntity(HttpEntity var1) {
      this.entity = var1;
   }

   @Override
   public void setLocale(Locale var1) {
      this.locale = Args.notNull(var1, "Locale");
      this.statusline = null;
   }

   protected String getReason(int var1) {
      return this.reasonCatalog != null ? this.reasonCatalog.getReason(var1, this.locale != null ? this.locale : Locale.getDefault()) : null;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(this.getStatusLine());
      var1.append(' ');
      var1.append(this.headergroup);
      if (this.entity != null) {
         var1.append(' ');
         var1.append(this.entity);
      }

      return var1.toString();
   }
}
