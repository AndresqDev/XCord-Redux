package org.codehaus.plexus.util.xml;

import org.codehaus.plexus.util.StringUtils;

public class XmlWriterUtil {
   public static final String LS = System.getProperty("line.separator");
   public static final int DEFAULT_INDENTATION_SIZE = 2;
   public static final int DEFAULT_COLUMN_LINE = 80;

   public static void writeLineBreak(XMLWriter var0) {
      writeLineBreak(var0, 1);
   }

   public static void writeLineBreak(XMLWriter var0, int var1) {
      for (int var2 = 0; var2 < var1; var2++) {
         var0.writeMarkup(LS);
      }
   }

   public static void writeLineBreak(XMLWriter var0, int var1, int var2) {
      writeLineBreak(var0, var1, var2, 2);
   }

   public static void writeLineBreak(XMLWriter var0, int var1, int var2, int var3) {
      writeLineBreak(var0, var1);
      if (var2 < 0) {
         var2 = 0;
      }

      if (var3 < 0) {
         var3 = 0;
      }

      var0.writeText(StringUtils.repeat(" ", var2 * var3));
   }

   public static void writeCommentLineBreak(XMLWriter var0) {
      writeCommentLineBreak(var0, 80);
   }

   public static void writeCommentLineBreak(XMLWriter var0, int var1) {
      if (var1 < 10) {
         var1 = 80;
      }

      var0.writeMarkup("<!-- " + StringUtils.repeat("=", var1 - 10) + " -->" + LS);
   }

   public static void writeComment(XMLWriter var0, String var1) {
      writeComment(var0, var1, 0, 2);
   }

   public static void writeComment(XMLWriter var0, String var1, int var2) {
      writeComment(var0, var1, var2, 2);
   }

   public static void writeComment(XMLWriter var0, String var1, int var2, int var3) {
      writeComment(var0, var1, var2, var3, 80);
   }

   public static void writeComment(XMLWriter var0, String var1, int var2, int var3, int var4) {
      if (var1 == null) {
         var1 = "null";
      }

      while (var1.contains("<!--")) {
         var1 = var1.replace("<!--", "");
      }

      while (var1.contains("-->")) {
         var1 = var1.replace("-->", "");
      }

      if (var2 < 0) {
         var2 = 0;
      }

      if (var3 < 0) {
         var3 = 0;
      }

      if (var4 < 0) {
         var4 = 80;
      }

      String var5 = StringUtils.repeat(" ", var2 * var3);
      int var6 = var5.length() + var4 - "-->".length() - 1;
      String[] var7 = StringUtils.split(var1, LS);
      StringBuffer var8 = new StringBuffer(var5 + "<!-- ");

      for (String var12 : var7) {
         String[] var13 = StringUtils.split(var12, " ");

         for (String var17 : var13) {
            StringBuilder var18 = new StringBuilder(var8.toString());
            var18.append(var17).append(' ');
            if (var18.length() > var6) {
               if (var8.length() != var5.length() + "<!-- ".length()) {
                  if (var6 - var8.length() > 0) {
                     var8.append(StringUtils.repeat(" ", var6 - var8.length()));
                  }

                  var8.append("-->").append(LS);
                  var0.writeMarkup(var8.toString());
               }

               var8 = new StringBuffer(var5 + "<!-- ");
               var8.append(var17).append(' ');
            } else {
               var8.append(var17).append(' ');
            }
         }

         if (var6 - var8.length() > 0) {
            var8.append(StringUtils.repeat(" ", var6 - var8.length()));
         }
      }

      if (var8.length() <= var6) {
         var8.append(StringUtils.repeat(" ", var6 - var8.length()));
      }

      var8.append("-->").append(LS);
      var0.writeMarkup(var8.toString());
   }

   public static void writeCommentText(XMLWriter var0, String var1) {
      writeCommentText(var0, var1, 0, 2);
   }

   public static void writeCommentText(XMLWriter var0, String var1, int var2) {
      writeCommentText(var0, var1, var2, 2);
   }

   public static void writeCommentText(XMLWriter var0, String var1, int var2, int var3) {
      writeCommentText(var0, var1, var2, var3, 80);
   }

   public static void writeCommentText(XMLWriter var0, String var1, int var2, int var3, int var4) {
      if (var2 < 0) {
         var2 = 0;
      }

      if (var3 < 0) {
         var3 = 0;
      }

      if (var4 < 0) {
         var4 = 80;
      }

      writeLineBreak(var0, 1);
      var0.writeMarkup(StringUtils.repeat(" ", var2 * var3));
      writeCommentLineBreak(var0, var4);
      writeComment(var0, var1, var2, var3, var4);
      var0.writeMarkup(StringUtils.repeat(" ", var2 * var3));
      writeCommentLineBreak(var0, var4);
      writeLineBreak(var0, 1, var2, var3);
   }
}
