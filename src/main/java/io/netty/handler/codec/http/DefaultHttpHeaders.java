package io.netty.handler.codec.http;

import io.netty.handler.codec.CharSequenceValueConverter;
import io.netty.handler.codec.DateFormatter;
import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.DefaultHeadersImpl;
import io.netty.handler.codec.HeadersUtils;
import io.netty.handler.codec.ValueConverter;
import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.PlatformDependent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

public class DefaultHttpHeaders extends HttpHeaders {
   private static final int HIGHEST_INVALID_VALUE_CHAR_MASK = -16;
   private static final ByteProcessor HEADER_NAME_VALIDATOR = new ByteProcessor() {
      @Override
      public boolean process(byte var1) throws Exception {
         DefaultHttpHeaders.validateHeaderNameElement(var1);
         return true;
      }
   };
   static final DefaultHeaders.NameValidator<CharSequence> HttpNameValidator = new DefaultHeaders.NameValidator<CharSequence>() {
      public void validateName(CharSequence var1) {
         if (var1 != null && var1.length() != 0) {
            if (var1 instanceof AsciiString) {
               try {
                  ((AsciiString)var1).forEachByte(DefaultHttpHeaders.HEADER_NAME_VALIDATOR);
               } catch (Exception var3) {
                  PlatformDependent.throwException(var3);
               }
            } else {
               for (int var2 = 0; var2 < var1.length(); var2++) {
                  DefaultHttpHeaders.validateHeaderNameElement(var1.charAt(var2));
               }
            }
         } else {
            throw new IllegalArgumentException("empty headers are not allowed [" + var1 + "]");
         }
      }
   };
   private final DefaultHeaders<CharSequence, CharSequence, ?> headers;

   public DefaultHttpHeaders() {
      this(true);
   }

   public DefaultHttpHeaders(boolean var1) {
      this(var1, nameValidator(var1));
   }

   protected DefaultHttpHeaders(boolean var1, DefaultHeaders.NameValidator<CharSequence> var2) {
      this(new DefaultHeadersImpl<>(AsciiString.CASE_INSENSITIVE_HASHER, valueConverter(var1), var2));
   }

   protected DefaultHttpHeaders(DefaultHeaders<CharSequence, CharSequence, ?> var1) {
      this.headers = var1;
   }

   @Override
   public HttpHeaders add(HttpHeaders var1) {
      if (var1 instanceof DefaultHttpHeaders) {
         this.headers.add(((DefaultHttpHeaders)var1).headers);
         return this;
      } else {
         return super.add(var1);
      }
   }

   @Override
   public HttpHeaders set(HttpHeaders var1) {
      if (var1 instanceof DefaultHttpHeaders) {
         this.headers.set(((DefaultHttpHeaders)var1).headers);
         return this;
      } else {
         return super.set(var1);
      }
   }

   @Override
   public HttpHeaders add(String var1, Object var2) {
      this.headers.addObject(var1, var2);
      return this;
   }

   @Override
   public HttpHeaders add(CharSequence var1, Object var2) {
      this.headers.addObject(var1, var2);
      return this;
   }

   @Override
   public HttpHeaders add(String var1, Iterable<?> var2) {
      this.headers.addObject(var1, var2);
      return this;
   }

   @Override
   public HttpHeaders add(CharSequence var1, Iterable<?> var2) {
      this.headers.addObject(var1, var2);
      return this;
   }

   @Override
   public HttpHeaders addInt(CharSequence var1, int var2) {
      this.headers.addInt(var1, var2);
      return this;
   }

   @Override
   public HttpHeaders addShort(CharSequence var1, short var2) {
      this.headers.addShort(var1, var2);
      return this;
   }

   @Override
   public HttpHeaders remove(String var1) {
      this.headers.remove(var1);
      return this;
   }

   @Override
   public HttpHeaders remove(CharSequence var1) {
      this.headers.remove(var1);
      return this;
   }

   @Override
   public HttpHeaders set(String var1, Object var2) {
      this.headers.setObject(var1, var2);
      return this;
   }

   @Override
   public HttpHeaders set(CharSequence var1, Object var2) {
      this.headers.setObject(var1, var2);
      return this;
   }

   @Override
   public HttpHeaders set(String var1, Iterable<?> var2) {
      this.headers.setObject(var1, var2);
      return this;
   }

   @Override
   public HttpHeaders set(CharSequence var1, Iterable<?> var2) {
      this.headers.setObject(var1, var2);
      return this;
   }

   @Override
   public HttpHeaders setInt(CharSequence var1, int var2) {
      this.headers.setInt(var1, var2);
      return this;
   }

   @Override
   public HttpHeaders setShort(CharSequence var1, short var2) {
      this.headers.setShort(var1, var2);
      return this;
   }

   @Override
   public HttpHeaders clear() {
      this.headers.clear();
      return this;
   }

   @Override
   public String get(String var1) {
      return this.get((CharSequence)var1);
   }

   @Override
   public String get(CharSequence var1) {
      return HeadersUtils.getAsString(this.headers, var1);
   }

   @Override
   public Integer getInt(CharSequence var1) {
      return this.headers.getInt(var1);
   }

   @Override
   public int getInt(CharSequence var1, int var2) {
      return this.headers.getInt(var1, var2);
   }

   @Override
   public Short getShort(CharSequence var1) {
      return this.headers.getShort(var1);
   }

   @Override
   public short getShort(CharSequence var1, short var2) {
      return this.headers.getShort(var1, var2);
   }

   @Override
   public Long getTimeMillis(CharSequence var1) {
      return this.headers.getTimeMillis(var1);
   }

   @Override
   public long getTimeMillis(CharSequence var1, long var2) {
      return this.headers.getTimeMillis(var1, var2);
   }

   @Override
   public List<String> getAll(String var1) {
      return this.getAll((CharSequence)var1);
   }

   @Override
   public List<String> getAll(CharSequence var1) {
      return HeadersUtils.getAllAsString(this.headers, var1);
   }

   @Override
   public List<Entry<String, String>> entries() {
      if (this.isEmpty()) {
         return Collections.emptyList();
      } else {
         ArrayList var1 = new ArrayList(this.headers.size());

         for (Entry var3 : this) {
            var1.add(var3);
         }

         return var1;
      }
   }

   @Deprecated
   @Override
   public Iterator<Entry<String, String>> iterator() {
      return HeadersUtils.iteratorAsString(this.headers);
   }

   @Override
   public Iterator<Entry<CharSequence, CharSequence>> iteratorCharSequence() {
      return this.headers.iterator();
   }

   @Override
   public Iterator<String> valueStringIterator(CharSequence var1) {
      final Iterator var2 = this.valueCharSequenceIterator(var1);
      return new Iterator<String>() {
         @Override
         public boolean hasNext() {
            return var2.hasNext();
         }

         public String next() {
            return ((CharSequence)var2.next()).toString();
         }

         @Override
         public void remove() {
            var2.remove();
         }
      };
   }

   @Override
   public Iterator<CharSequence> valueCharSequenceIterator(CharSequence var1) {
      return this.headers.valueIterator(var1);
   }

   @Override
   public boolean contains(String var1) {
      return this.contains((CharSequence)var1);
   }

   @Override
   public boolean contains(CharSequence var1) {
      return this.headers.contains(var1);
   }

   @Override
   public boolean isEmpty() {
      return this.headers.isEmpty();
   }

   @Override
   public int size() {
      return this.headers.size();
   }

   @Override
   public boolean contains(String var1, String var2, boolean var3) {
      return this.contains((CharSequence)var1, (CharSequence)var2, var3);
   }

   @Override
   public boolean contains(CharSequence var1, CharSequence var2, boolean var3) {
      return this.headers.contains(var1, var2, var3 ? AsciiString.CASE_INSENSITIVE_HASHER : AsciiString.CASE_SENSITIVE_HASHER);
   }

   @Override
   public Set<String> names() {
      return HeadersUtils.namesAsString(this.headers);
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof DefaultHttpHeaders && this.headers.equals(((DefaultHttpHeaders)var1).headers, AsciiString.CASE_SENSITIVE_HASHER);
   }

   @Override
   public int hashCode() {
      return this.headers.hashCode(AsciiString.CASE_SENSITIVE_HASHER);
   }

   @Override
   public HttpHeaders copy() {
      return new DefaultHttpHeaders(this.headers.copy());
   }

   private static void validateHeaderNameElement(byte var0) {
      switch (var0) {
         case 0:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 44:
         case 58:
         case 59:
         case 61:
            throw new IllegalArgumentException("a header name cannot contain the following prohibited characters: =,;: \\t\\r\\n\\v\\f: " + var0);
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 33:
         case 34:
         case 35:
         case 36:
         case 37:
         case 38:
         case 39:
         case 40:
         case 41:
         case 42:
         case 43:
         case 45:
         case 46:
         case 47:
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
         case 56:
         case 57:
         case 60:
         default:
            if (var0 < 0) {
               throw new IllegalArgumentException("a header name cannot contain non-ASCII character: " + var0);
            }
      }
   }

   private static void validateHeaderNameElement(char var0) {
      switch (var0) {
         case '\u0000':
         case '\t':
         case '\n':
         case '\u000b':
         case '\f':
         case '\r':
         case '\u001c':
         case '\u001d':
         case '\u001e':
         case '\u001f':
         case ' ':
         case ',':
         case ':':
         case ';':
         case '=':
            throw new IllegalArgumentException("a header name cannot contain the following prohibited characters: =,;: \\t\\r\\n\\v\\f: " + var0);
         case '\u0001':
         case '\u0002':
         case '\u0003':
         case '\u0004':
         case '\u0005':
         case '\u0006':
         case '\u0007':
         case '\b':
         case '\u000e':
         case '\u000f':
         case '\u0010':
         case '\u0011':
         case '\u0012':
         case '\u0013':
         case '\u0014':
         case '\u0015':
         case '\u0016':
         case '\u0017':
         case '\u0018':
         case '\u0019':
         case '\u001a':
         case '\u001b':
         case '!':
         case '"':
         case '#':
         case '$':
         case '%':
         case '&':
         case '\'':
         case '(':
         case ')':
         case '*':
         case '+':
         case '-':
         case '.':
         case '/':
         case '0':
         case '1':
         case '2':
         case '3':
         case '4':
         case '5':
         case '6':
         case '7':
         case '8':
         case '9':
         case '<':
         default:
            if (var0 > 127) {
               throw new IllegalArgumentException("a header name cannot contain non-ASCII character: " + var0);
            }
      }
   }

   static ValueConverter<CharSequence> valueConverter(boolean var0) {
      return (ValueConverter<CharSequence>)(var0
         ? DefaultHttpHeaders.HeaderValueConverterAndValidator.INSTANCE
         : DefaultHttpHeaders.HeaderValueConverter.INSTANCE);
   }

   static DefaultHeaders.NameValidator<CharSequence> nameValidator(boolean var0) {
      return var0 ? HttpNameValidator : DefaultHeaders.NameValidator.NOT_NULL;
   }

   private static class HeaderValueConverter extends CharSequenceValueConverter {
      static final DefaultHttpHeaders.HeaderValueConverter INSTANCE = new DefaultHttpHeaders.HeaderValueConverter();

      private HeaderValueConverter() {
      }

      @Override
      public CharSequence convertObject(Object var1) {
         if (var1 instanceof CharSequence) {
            return (CharSequence)var1;
         } else if (var1 instanceof Date) {
            return DateFormatter.format((Date)var1);
         } else {
            return var1 instanceof Calendar ? DateFormatter.format(((Calendar)var1).getTime()) : var1.toString();
         }
      }
   }

   private static final class HeaderValueConverterAndValidator extends DefaultHttpHeaders.HeaderValueConverter {
      static final DefaultHttpHeaders.HeaderValueConverterAndValidator INSTANCE = new DefaultHttpHeaders.HeaderValueConverterAndValidator();

      @Override
      public CharSequence convertObject(Object var1) {
         CharSequence var2 = super.convertObject(var1);
         int var3 = 0;

         for (int var4 = 0; var4 < var2.length(); var4++) {
            var3 = validateValueChar(var2, var3, var2.charAt(var4));
         }

         if (var3 != 0) {
            throw new IllegalArgumentException("a header value must not end with '\\r' or '\\n'");
         } else {
            return var2;
         }
      }

      private static int validateValueChar(CharSequence var0, int var1, char var2) {
         if ((var2 & -16) == 0) {
            switch (var2) {
               case '\u0000':
                  throw new IllegalArgumentException("a header value contains a prohibited character '\u0000'");
               case '\u000b':
                  throw new IllegalArgumentException("a header value contains a prohibited character '\\v'");
               case '\f':
                  throw new IllegalArgumentException("a header value contains a prohibited character '\\f'");
            }
         }

         switch (var1) {
            case 0:
               switch (var2) {
                  case '\n':
                     return 2;
                  case '\r':
                     return 1;
               }
            default:
               return var1;
            case 1:
               if (var2 == '\n') {
                  return 2;
               }

               throw new IllegalArgumentException("only '\\n' is allowed after '\\r'");
            case 2:
               switch (var2) {
                  case '\t':
                  case ' ':
                     return 0;
                  default:
                     throw new IllegalArgumentException("only ' ' and '\\t' are allowed after '\\n'");
               }
         }
      }
   }
}
