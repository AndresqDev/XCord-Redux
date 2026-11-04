package io.netty.handler.codec.http;

import io.netty.handler.codec.DateFormatter;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Deprecated
public final class CookieDecoder {
   private final InternalLogger logger = InternalLoggerFactory.getInstance(this.getClass());
   private static final CookieDecoder STRICT = new CookieDecoder(true);
   private static final CookieDecoder LAX = new CookieDecoder(false);
   private static final String COMMENT = "Comment";
   private static final String COMMENTURL = "CommentURL";
   private static final String DISCARD = "Discard";
   private static final String PORT = "Port";
   private static final String VERSION = "Version";
   private final boolean strict;

   public static Set<Cookie> decode(String var0) {
      return decode(var0, true);
   }

   public static Set<Cookie> decode(String var0, boolean var1) {
      return (var1 ? STRICT : LAX).doDecode(var0);
   }

   private Set<Cookie> doDecode(String var1) {
      ArrayList var2 = new ArrayList(8);
      ArrayList var3 = new ArrayList(8);
      extractKeyValuePairs(var1, var2, var3);
      if (var2.isEmpty()) {
         return Collections.emptySet();
      } else {
         int var5 = 0;
         int var4;
         if (((String)var2.get(0)).equalsIgnoreCase("Version")) {
            try {
               var5 = Integer.parseInt((String)var3.get(0));
            } catch (NumberFormatException var28) {
            }

            var4 = 1;
         } else {
            var4 = 0;
         }

         if (var2.size() <= var4) {
            return Collections.emptySet();
         } else {
            TreeSet var6;
            for (var6 = new TreeSet(); var4 < var2.size(); var4++) {
               String var7 = (String)var2.get(var4);
               String var8 = (String)var3.get(var4);
               if (var8 == null) {
                  var8 = "";
               }

               DefaultCookie var9 = this.initCookie(var7, var8);
               if (var9 == null) {
                  break;
               }

               boolean var10 = false;
               boolean var11 = false;
               boolean var12 = false;
               Object var13 = null;
               Object var14 = null;
               Object var15 = null;
               Object var16 = null;
               long var17 = Long.MIN_VALUE;
               ArrayList var19 = new ArrayList(2);

               for (int var20 = var4 + 1; var20 < var2.size(); var6.add(var9)) {
                  var7 = (String)var2.get(var20);
                  var8 = (String)var3.get(var20);
                  if ("Discard".equalsIgnoreCase(var7)) {
                     var10 = true;
                  } else if ("Secure".equalsIgnoreCase(var7)) {
                     var11 = true;
                  } else if ("HTTPOnly".equalsIgnoreCase(var7)) {
                     var12 = true;
                  } else if (!"Comment".equalsIgnoreCase(var7)
                     && !"CommentURL".equalsIgnoreCase(var7)
                     && !"Domain".equalsIgnoreCase(var7)
                     && !"Path".equalsIgnoreCase(var7)) {
                     if ("Expires".equalsIgnoreCase(var7)) {
                        Date var21 = DateFormatter.parseHttpDate(var8);
                        if (var21 != null) {
                           long var22 = var21.getTime() - System.currentTimeMillis();
                           var17 = var22 / 1000L + (long)(var22 % 1000L != 0L ? 1 : 0);
                        }
                     } else if ("Max-Age".equalsIgnoreCase(var7)) {
                        var17 = (long)Integer.parseInt(var8);
                     } else if ("Version".equalsIgnoreCase(var7)) {
                        var5 = Integer.parseInt(var8);
                     } else {
                        if (!"Port".equalsIgnoreCase(var7)) {
                           var9.setVersion(var5);
                           var9.setMaxAge(var17);
                           var9.setPath((String)var16);
                           var9.setDomain((String)var15);
                           var9.setSecure(var11);
                           var9.setHttpOnly(var12);
                           if (var5 > 0) {
                              var9.setComment((String)var13);
                           }

                           if (var5 > 1) {
                              var9.setCommentUrl((String)var14);
                              var9.setPorts(var19);
                              var9.setDiscard(var10);
                           }
                           continue;
                        }

                        String[] var31 = var8.split(",");

                        for (String var25 : var31) {
                           try {
                              var19.add(Integer.valueOf(var25));
                           } catch (NumberFormatException var27) {
                           }
                        }
                     }
                  }

                  var20++;
                  var4++;
               }
               break;
            }

            return var6;
         }
      }
   }

   private static void extractKeyValuePairs(String var0, List<String> var1, List<String> var2) {
      int var3 = var0.length();
      int var4 = 0;

      label77:
      while (var4 != var3) {
         switch (var0.charAt(var4)) {
            case '\t':
            case '\n':
            case '\u000b':
            case '\f':
            case '\r':
            case ' ':
            case ',':
            case ';':
               var4++;
               continue;
         }

         while (var4 != var3) {
            if (var0.charAt(var4) != '$') {
               String var5;
               String var6;
               if (var4 == var3) {
                  var5 = null;
                  var6 = null;
               } else {
                  int var7 = var4;

                  label68:
                  while (true) {
                     switch (var0.charAt(var4)) {
                        case ';':
                           var5 = var0.substring(var7, var4);
                           var6 = null;
                           break label68;
                        case '=':
                           var5 = var0.substring(var7, var4);
                           if (++var4 == var3) {
                              var6 = "";
                           } else {
                              char var9 = var0.charAt(var4);
                              if (var9 == '"' || var9 == '\'') {
                                 StringBuilder var15 = new StringBuilder(var0.length() - var4);
                                 char var11 = var9;
                                 boolean var12 = false;
                                 var4++;

                                 while (var4 != var3) {
                                    if (var12) {
                                       var12 = false;
                                       var9 = var0.charAt(var4++);
                                       switch (var9) {
                                          case '"':
                                          case '\'':
                                          case '\\':
                                             var15.setCharAt(var15.length() - 1, var9);
                                             break;
                                          default:
                                             var15.append(var9);
                                       }
                                    } else {
                                       var9 = var0.charAt(var4++);
                                       if (var9 == var11) {
                                          var6 = var15.toString();
                                          break label68;
                                       }

                                       var15.append(var9);
                                       if (var9 == '\\') {
                                          var12 = true;
                                       }
                                    }
                                 }

                                 var6 = var15.toString();
                                 break label68;
                              }

                              int var10 = var0.indexOf(59, var4);
                              if (var10 > 0) {
                                 var6 = var0.substring(var4, var10);
                                 var4 = var10;
                              } else {
                                 var6 = var0.substring(var4);
                                 var4 = var3;
                              }
                           }
                           break label68;
                        default:
                           if (++var4 == var3) {
                              var5 = var0.substring(var7);
                              var6 = null;
                              break label68;
                           }
                     }
                  }
               }

               var1.add(var5);
               var2.add(var6);
               continue label77;
            }

            var4++;
         }
         break;
      }
   }

   private CookieDecoder(boolean var1) {
      this.strict = var1;
   }

   private DefaultCookie initCookie(String var1, String var2) {
      if (var1 == null || var1.length() == 0) {
         this.logger.debug("Skipping cookie with null name");
         return null;
      } else if (var2 == null) {
         this.logger.debug("Skipping cookie with null value");
         return null;
      } else {
         CharSequence var3 = CookieUtil.unwrapValue(var2);
         if (var3 == null) {
            this.logger.debug("Skipping cookie because starting quotes are not properly balanced in '{}'", var3);
            return null;
         } else {
            int var4;
            if (this.strict && (var4 = CookieUtil.firstInvalidCookieNameOctet(var1)) >= 0) {
               if (this.logger.isDebugEnabled()) {
                  this.logger.debug("Skipping cookie because name '{}' contains invalid char '{}'", var1, var1.charAt(var4));
               }

               return null;
            } else {
               boolean var5 = var3.length() != var2.length();
               if (this.strict && (var4 = CookieUtil.firstInvalidCookieValueOctet(var3)) >= 0) {
                  if (this.logger.isDebugEnabled()) {
                     this.logger.debug("Skipping cookie because value '{}' contains invalid char '{}'", var3, var3.charAt(var4));
                  }

                  return null;
               } else {
                  DefaultCookie var6 = new DefaultCookie(var1, var3.toString());
                  var6.setWrap(var5);
                  return var6;
               }
            }
         }
      }
   }
}
