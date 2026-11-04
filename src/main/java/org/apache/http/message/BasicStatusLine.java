package org.apache.http.message;

import java.io.Serializable;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class BasicStatusLine implements Serializable, Cloneable, StatusLine {
   private static final long serialVersionUID = -2443303766890459269L;
   private final ProtocolVersion protoVersion;
   private final int statusCode;
   private final String reasonPhrase;

   public BasicStatusLine(ProtocolVersion var1, int var2, String var3) {
      this.protoVersion = Args.notNull(var1, "Version");
      this.statusCode = Args.notNegative(var2, "Status code");
      this.reasonPhrase = var3;
   }

   @Override
   public int getStatusCode() {
      return this.statusCode;
   }

   @Override
   public ProtocolVersion getProtocolVersion() {
      return this.protoVersion;
   }

   @Override
   public String getReasonPhrase() {
      return this.reasonPhrase;
   }

   @Override
   public String toString() {
      return BasicLineFormatter.INSTANCE.formatStatusLine(null, this).toString();
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }
}
