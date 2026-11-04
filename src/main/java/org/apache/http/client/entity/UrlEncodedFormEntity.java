package org.apache.http.client.entity;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

public class UrlEncodedFormEntity extends StringEntity {
   public UrlEncodedFormEntity(List<? extends NameValuePair> var1, String var2) throws UnsupportedEncodingException {
      super(URLEncodedUtils.format(var1, var2 != null ? var2 : HTTP.DEF_CONTENT_CHARSET.name()), ContentType.create("application/x-www-form-urlencoded", var2));
   }

   public UrlEncodedFormEntity(Iterable<? extends NameValuePair> var1, Charset var2) {
      super(URLEncodedUtils.format(var1, var2 != null ? var2 : HTTP.DEF_CONTENT_CHARSET), ContentType.create("application/x-www-form-urlencoded", var2));
   }

   public UrlEncodedFormEntity(List<? extends NameValuePair> var1) throws UnsupportedEncodingException {
      this(var1, (Charset)null);
   }

   public UrlEncodedFormEntity(Iterable<? extends NameValuePair> var1) {
      this(var1, null);
   }
}
