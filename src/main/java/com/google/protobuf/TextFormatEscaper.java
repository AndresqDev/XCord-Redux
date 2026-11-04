package com.google.protobuf;

final class TextFormatEscaper {
   private TextFormatEscaper() {
   }

   static String escapeBytes(TextFormatEscaper.ByteSequence var0) {
      StringBuilder var1 = new StringBuilder(var0.size());

      for (int var2 = 0; var2 < var0.size(); var2++) {
         byte var3 = var0.byteAt(var2);
         switch (var3) {
            case 7:
               var1.append("\\a");
               break;
            case 8:
               var1.append("\\b");
               break;
            case 9:
               var1.append("\\t");
               break;
            case 10:
               var1.append("\\n");
               break;
            case 11:
               var1.append("\\v");
               break;
            case 12:
               var1.append("\\f");
               break;
            case 13:
               var1.append("\\r");
               break;
            case 34:
               var1.append("\\\"");
               break;
            case 39:
               var1.append("\\'");
               break;
            case 92:
               var1.append("\\\\");
               break;
            default:
               if (var3 >= 32 && var3 <= 126) {
                  var1.append((char)var3);
               } else {
                  var1.append('\\');
                  var1.append((char)(48 + (var3 >>> 6 & 3)));
                  var1.append((char)(48 + (var3 >>> 3 & 7)));
                  var1.append((char)(48 + (var3 & 7)));
               }
         }
      }

      return var1.toString();
   }

   static String escapeBytes(final ByteString var0) {
      return escapeBytes(new TextFormatEscaper.ByteSequence() {
         @Override
         public int size() {
            return var0.size();
         }

         @Override
         public byte byteAt(int var1) {
            return var0.byteAt(var1);
         }
      });
   }

   static String escapeBytes(final byte[] var0) {
      return escapeBytes(new TextFormatEscaper.ByteSequence() {
         @Override
         public int size() {
            return var0.length;
         }

         @Override
         public byte byteAt(int var1) {
            return var0[var1];
         }
      });
   }

   static String escapeText(String var0) {
      return escapeBytes(ByteString.copyFromUtf8(var0));
   }

   static String escapeDoubleQuotesAndBackslashes(String var0) {
      return var0.replace("\\", "\\\\").replace("\"", "\\\"");
   }

   private interface ByteSequence {
      int size();

      byte byteAt(int var1);
   }
}
