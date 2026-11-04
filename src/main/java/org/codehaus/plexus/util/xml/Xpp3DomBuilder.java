package org.codehaus.plexus.util.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class Xpp3DomBuilder {
   private static final boolean DEFAULT_TRIM = true;

   public static Xpp3Dom build(Reader var0) throws XmlPullParserException, IOException {
      return build(var0, null);
   }

   public static Xpp3Dom build(Reader var0, Xpp3DomBuilder.InputLocationBuilder var1) throws XmlPullParserException, IOException {
      return build(var0, true, var1);
   }

   public static Xpp3Dom build(InputStream var0, String var1) throws XmlPullParserException, IOException {
      return build(var0, var1, true);
   }

   public static Xpp3Dom build(InputStream var0, String var1, boolean var2) throws XmlPullParserException, IOException {
      Xpp3Dom var5;
      try {
         MXParser var3 = new MXParser();
         var3.setInput(var0, var1);
         Xpp3Dom var4 = build(var3, var2);
         var0.close();
         var0 = null;
         var5 = var4;
      } finally {
         IOUtil.close(var0);
      }

      return var5;
   }

   public static Xpp3Dom build(Reader var0, boolean var1) throws XmlPullParserException, IOException {
      return build(var0, var1, null);
   }

   public static Xpp3Dom build(Reader var0, boolean var1, Xpp3DomBuilder.InputLocationBuilder var2) throws XmlPullParserException, IOException {
      Xpp3Dom var5;
      try {
         MXParser var3 = new MXParser();
         var3.setInput(var0);
         Xpp3Dom var4 = build(var3, var1, var2);
         var0.close();
         var0 = null;
         var5 = var4;
      } finally {
         IOUtil.close(var0);
      }

      return var5;
   }

   public static Xpp3Dom build(XmlPullParser var0) throws XmlPullParserException, IOException {
      return build(var0, true);
   }

   public static Xpp3Dom build(XmlPullParser var0, boolean var1) throws XmlPullParserException, IOException {
      return build(var0, var1, null);
   }

   public static Xpp3Dom build(XmlPullParser var0, boolean var1, Xpp3DomBuilder.InputLocationBuilder var2) throws XmlPullParserException, IOException {
      ArrayList var3 = new ArrayList();
      ArrayList var4 = new ArrayList();
      int var5 = var0.getEventType();

      for (boolean var6 = false; var5 != 1; var5 = var0.next()) {
         if (var5 != 2) {
            if (var5 == 4) {
               int var14 = var4.size() - 1;
               StringBuilder var16 = (StringBuilder)var4.get(var14);
               String var18 = var0.getText();
               if (var1 && !var6) {
                  var18 = var18.trim();
               }

               var16.append(var18);
            } else if (var5 == 3) {
               int var15 = var3.size() - 1;
               Xpp3Dom var17 = (Xpp3Dom)var3.remove(var15);
               Object var19 = var4.remove(var15);
               if (var17.getChildCount() == 0) {
                  if (var19 == null) {
                     var17.setValue(null);
                  } else {
                     var17.setValue(var19.toString());
                  }
               }

               if (var15 == 0) {
                  return var17;
               }
            }
         } else {
            var6 = false;
            String var7 = var0.getName();
            Xpp3Dom var8 = new Xpp3Dom(var7);
            if (var2 != null) {
               var8.setInputLocation(var2.toInputLocation(var0));
            }

            int var9 = var3.size();
            if (var9 > 0) {
               Xpp3Dom var10 = (Xpp3Dom)var3.get(var9 - 1);
               var10.addChild(var8);
            }

            var3.add(var8);
            if (var0.isEmptyElementTag()) {
               var4.add(null);
            } else {
               var4.add(new StringBuilder());
            }

            int var20 = var0.getAttributeCount();

            for (int var11 = 0; var11 < var20; var11++) {
               String var12 = var0.getAttributeName(var11);
               String var13 = var0.getAttributeValue(var11);
               var8.setAttribute(var12, var13);
               var6 = var6 || "xml:space".equals(var12) && "preserve".equals(var13);
            }
         }
      }

      throw new IllegalStateException("End of document found before returning to 0 depth");
   }

   public interface InputLocationBuilder {
      Object toInputLocation(XmlPullParser var1);
   }
}
