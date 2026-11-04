package io.netty.handler.codec.http;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

public class EmptyHttpHeaders extends HttpHeaders {
   static final Iterator<Entry<CharSequence, CharSequence>> EMPTY_CHARS_ITERATOR = Collections.<Entry<CharSequence, CharSequence>>emptyList().iterator();
   public static final EmptyHttpHeaders INSTANCE = instance();

   @Deprecated
   static EmptyHttpHeaders instance() {
      return EmptyHttpHeaders.InstanceInitializer.EMPTY_HEADERS;
   }

   protected EmptyHttpHeaders() {
   }

   @Override
   public String get(String var1) {
      return null;
   }

   @Override
   public Integer getInt(CharSequence var1) {
      return null;
   }

   @Override
   public int getInt(CharSequence var1, int var2) {
      return var2;
   }

   @Override
   public Short getShort(CharSequence var1) {
      return null;
   }

   @Override
   public short getShort(CharSequence var1, short var2) {
      return var2;
   }

   @Override
   public Long getTimeMillis(CharSequence var1) {
      return null;
   }

   @Override
   public long getTimeMillis(CharSequence var1, long var2) {
      return var2;
   }

   @Override
   public List<String> getAll(String var1) {
      return Collections.emptyList();
   }

   @Override
   public List<Entry<String, String>> entries() {
      return Collections.emptyList();
   }

   @Override
   public boolean contains(String var1) {
      return false;
   }

   @Override
   public boolean isEmpty() {
      return true;
   }

   @Override
   public int size() {
      return 0;
   }

   @Override
   public Set<String> names() {
      return Collections.emptySet();
   }

   @Override
   public HttpHeaders add(String var1, Object var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public HttpHeaders add(String var1, Iterable<?> var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public HttpHeaders addInt(CharSequence var1, int var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public HttpHeaders addShort(CharSequence var1, short var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public HttpHeaders set(String var1, Object var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public HttpHeaders set(String var1, Iterable<?> var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public HttpHeaders setInt(CharSequence var1, int var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public HttpHeaders setShort(CharSequence var1, short var2) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public HttpHeaders remove(String var1) {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public HttpHeaders clear() {
      throw new UnsupportedOperationException("read only");
   }

   @Override
   public Iterator<Entry<String, String>> iterator() {
      return this.entries().iterator();
   }

   @Override
   public Iterator<Entry<CharSequence, CharSequence>> iteratorCharSequence() {
      return EMPTY_CHARS_ITERATOR;
   }

   @Deprecated
   private static final class InstanceInitializer {
      @Deprecated
      private static final EmptyHttpHeaders EMPTY_HEADERS = new EmptyHttpHeaders();
   }
}
