package io.netty.handler.codec.http;

import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.Headers;
import io.netty.handler.codec.ValueConverter;
import io.netty.util.AsciiString;
import io.netty.util.HashingStrategy;
import io.netty.util.internal.StringUtil;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class CombinedHttpHeaders extends DefaultHttpHeaders {
   public CombinedHttpHeaders(boolean var1) {
      super(new CombinedHttpHeaders.CombinedHttpHeadersImpl(AsciiString.CASE_INSENSITIVE_HASHER, valueConverter(var1), nameValidator(var1)));
   }

   @Override
   public boolean containsValue(CharSequence var1, CharSequence var2, boolean var3) {
      return super.containsValue(var1, StringUtil.trimOws(var2), var3);
   }

   private static final class CombinedHttpHeadersImpl extends DefaultHeaders<CharSequence, CharSequence, CombinedHttpHeaders.CombinedHttpHeadersImpl> {
      private static final int VALUE_LENGTH_ESTIMATE = 10;
      private CombinedHttpHeaders.CombinedHttpHeadersImpl.CsvValueEscaper<Object> objectEscaper;
      private CombinedHttpHeaders.CombinedHttpHeadersImpl.CsvValueEscaper<CharSequence> charSequenceEscaper;

      private CombinedHttpHeaders.CombinedHttpHeadersImpl.CsvValueEscaper<Object> objectEscaper() {
         if (this.objectEscaper == null) {
            this.objectEscaper = new CombinedHttpHeaders.CombinedHttpHeadersImpl.CsvValueEscaper<Object>() {
               @Override
               public CharSequence escape(CharSequence var1, Object var2) {
                  CharSequence var3;
                  try {
                     var3 = CombinedHttpHeadersImpl.this.valueConverter().convertObject(var2);
                  } catch (IllegalArgumentException var5) {
                     throw new IllegalArgumentException("Failed to convert object value for header '" + var1 + '\'', var5);
                  }

                  return StringUtil.escapeCsv(var3, true);
               }
            };
         }

         return this.objectEscaper;
      }

      private CombinedHttpHeaders.CombinedHttpHeadersImpl.CsvValueEscaper<CharSequence> charSequenceEscaper() {
         if (this.charSequenceEscaper == null) {
            this.charSequenceEscaper = new CombinedHttpHeaders.CombinedHttpHeadersImpl.CsvValueEscaper<CharSequence>() {
               public CharSequence escape(CharSequence var1, CharSequence var2) {
                  return StringUtil.escapeCsv(var2, true);
               }
            };
         }

         return this.charSequenceEscaper;
      }

      CombinedHttpHeadersImpl(HashingStrategy<CharSequence> var1, ValueConverter<CharSequence> var2, DefaultHeaders.NameValidator<CharSequence> var3) {
         super(var1, var2, var3);
      }

      public Iterator<CharSequence> valueIterator(CharSequence var1) {
         Iterator var2 = super.valueIterator(var1);
         if (var2.hasNext() && !cannotBeCombined(var1)) {
            Iterator var3 = StringUtil.unescapeCsvFields((CharSequence)var2.next()).iterator();
            if (var2.hasNext()) {
               throw new IllegalStateException("CombinedHttpHeaders should only have one value");
            } else {
               return var3;
            }
         } else {
            return var2;
         }
      }

      public List<CharSequence> getAll(CharSequence var1) {
         List var2 = super.getAll(var1);
         if (!var2.isEmpty() && !cannotBeCombined(var1)) {
            if (var2.size() != 1) {
               throw new IllegalStateException("CombinedHttpHeaders should only have one value");
            } else {
               return StringUtil.unescapeCsvFields((CharSequence)var2.get(0));
            }
         } else {
            return var2;
         }
      }

      public CombinedHttpHeaders.CombinedHttpHeadersImpl add(Headers<? extends CharSequence, ? extends CharSequence, ?> var1) {
         if (var1 == this) {
            throw new IllegalArgumentException("can't add to itself.");
         } else {
            if (var1 instanceof CombinedHttpHeaders.CombinedHttpHeadersImpl) {
               if (this.isEmpty()) {
                  this.addImpl(var1);
               } else {
                  for (Entry var3 : var1) {
                     this.addEscapedValue((CharSequence)var3.getKey(), (CharSequence)var3.getValue());
                  }
               }
            } else {
               for (Entry var5 : var1) {
                  this.add((CharSequence)var5.getKey(), (CharSequence)var5.getValue());
               }
            }

            return this;
         }
      }

      public CombinedHttpHeaders.CombinedHttpHeadersImpl set(Headers<? extends CharSequence, ? extends CharSequence, ?> var1) {
         if (var1 == this) {
            return this;
         } else {
            this.clear();
            return this.add(var1);
         }
      }

      public CombinedHttpHeaders.CombinedHttpHeadersImpl setAll(Headers<? extends CharSequence, ? extends CharSequence, ?> var1) {
         if (var1 == this) {
            return this;
         } else {
            for (CharSequence var3 : var1.names()) {
               this.remove(var3);
            }

            return this.add(var1);
         }
      }

      public CombinedHttpHeaders.CombinedHttpHeadersImpl add(CharSequence var1, CharSequence var2) {
         return this.addEscapedValue(var1, this.charSequenceEscaper().escape(var1, var2));
      }

      public CombinedHttpHeaders.CombinedHttpHeadersImpl add(CharSequence var1, CharSequence... var2) {
         return this.addEscapedValue(var1, commaSeparate(var1, this.charSequenceEscaper(), var2));
      }

      public CombinedHttpHeaders.CombinedHttpHeadersImpl add(CharSequence var1, Iterable<? extends CharSequence> var2) {
         return this.addEscapedValue(var1, commaSeparate(var1, this.charSequenceEscaper(), var2));
      }

      public CombinedHttpHeaders.CombinedHttpHeadersImpl addObject(CharSequence var1, Object var2) {
         return this.addEscapedValue(var1, commaSeparate(var1, this.objectEscaper(), var2));
      }

      public CombinedHttpHeaders.CombinedHttpHeadersImpl addObject(CharSequence var1, Iterable<?> var2) {
         return this.addEscapedValue(var1, commaSeparate(var1, this.objectEscaper(), var2));
      }

      public CombinedHttpHeaders.CombinedHttpHeadersImpl addObject(CharSequence var1, Object... var2) {
         return this.addEscapedValue(var1, commaSeparate(var1, this.objectEscaper(), var2));
      }

      public CombinedHttpHeaders.CombinedHttpHeadersImpl set(CharSequence var1, CharSequence... var2) {
         this.set(var1, commaSeparate(var1, this.charSequenceEscaper(), var2));
         return this;
      }

      public CombinedHttpHeaders.CombinedHttpHeadersImpl set(CharSequence var1, Iterable<? extends CharSequence> var2) {
         this.set(var1, commaSeparate(var1, this.charSequenceEscaper(), var2));
         return this;
      }

      public CombinedHttpHeaders.CombinedHttpHeadersImpl setObject(CharSequence var1, Object var2) {
         this.set(var1, commaSeparate(var1, this.objectEscaper(), var2));
         return this;
      }

      public CombinedHttpHeaders.CombinedHttpHeadersImpl setObject(CharSequence var1, Object... var2) {
         this.set(var1, commaSeparate(var1, this.objectEscaper(), var2));
         return this;
      }

      public CombinedHttpHeaders.CombinedHttpHeadersImpl setObject(CharSequence var1, Iterable<?> var2) {
         this.set(var1, commaSeparate(var1, this.objectEscaper(), var2));
         return this;
      }

      private static boolean cannotBeCombined(CharSequence var0) {
         return HttpHeaderNames.SET_COOKIE.contentEqualsIgnoreCase(var0);
      }

      private CombinedHttpHeaders.CombinedHttpHeadersImpl addEscapedValue(CharSequence var1, CharSequence var2) {
         CharSequence var3 = this.get(var1);
         if (var3 != null && !cannotBeCombined(var1)) {
            this.set(var1, commaSeparateEscapedValues(var3, var2));
         } else {
            super.add(var1, var2);
         }

         return this;
      }

      private static <T> CharSequence commaSeparate(CharSequence var0, CombinedHttpHeaders.CombinedHttpHeadersImpl.CsvValueEscaper<T> var1, T... var2) {
         StringBuilder var3 = new StringBuilder(var2.length * 10);
         if (var2.length > 0) {
            int var4 = var2.length - 1;

            for (int var5 = 0; var5 < var4; var5++) {
               var3.append(var1.escape(var0, var2[var5])).append(',');
            }

            var3.append(var1.escape(var0, var2[var4]));
         }

         return var3;
      }

      private static <T> CharSequence commaSeparate(
         CharSequence var0, CombinedHttpHeaders.CombinedHttpHeadersImpl.CsvValueEscaper<T> var1, Iterable<? extends T> var2
      ) {
         StringBuilder var3 = var2 instanceof Collection ? new StringBuilder(((Collection)var2).size() * 10) : new StringBuilder();
         Iterator var4 = var2.iterator();
         if (var4.hasNext()) {
            Object var5;
            for (var5 = var4.next(); var4.hasNext(); var5 = var4.next()) {
               var3.append(var1.escape(var0, var5)).append(',');
            }

            var3.append(var1.escape(var0, var5));
         }

         return var3;
      }

      private static CharSequence commaSeparateEscapedValues(CharSequence var0, CharSequence var1) {
         return new StringBuilder(var0.length() + 1 + var1.length()).append(var0).append(',').append(var1);
      }

      private interface CsvValueEscaper<T> {
         CharSequence escape(CharSequence var1, T var2);
      }
   }
}
