package org.jline.utils;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Colors {
   public static final int[] DEFAULT_COLORS_256 = new int[]{
      0,
      8388608,
      32768,
      8421376,
      128,
      8388736,
      32896,
      12632256,
      8421504,
      16711680,
      65280,
      16776960,
      255,
      16711935,
      65535,
      16777215,
      0,
      95,
      135,
      175,
      215,
      255,
      24320,
      24415,
      24455,
      24495,
      24535,
      24575,
      34560,
      34655,
      34695,
      34735,
      34775,
      34815,
      44800,
      44895,
      44935,
      44975,
      45015,
      45055,
      55040,
      55135,
      55175,
      55215,
      55255,
      55295,
      65280,
      65375,
      65415,
      65455,
      65495,
      65535,
      6225920,
      6226015,
      6226055,
      6226095,
      6226135,
      6226175,
      6250240,
      6250335,
      6250375,
      6250415,
      6250455,
      6250495,
      6260480,
      6260575,
      6260615,
      6260655,
      6260695,
      6260735,
      6270720,
      6270815,
      6270855,
      6270895,
      6270935,
      6270975,
      6280960,
      6281055,
      6281095,
      6281135,
      6281175,
      6281215,
      6291200,
      6291295,
      6291335,
      6291375,
      6291415,
      6291455,
      8847360,
      8847455,
      8847495,
      8847535,
      8847575,
      8847615,
      8871680,
      8871775,
      8871815,
      8871855,
      8871895,
      8871935,
      8881920,
      8882015,
      8882055,
      8882095,
      8882135,
      8882175,
      8892160,
      8892255,
      8892295,
      8892335,
      8892375,
      8892415,
      8902400,
      8902495,
      8902535,
      8902575,
      8902615,
      8902655,
      8912640,
      8912735,
      8912775,
      8912815,
      8912855,
      8912895,
      11468800,
      11468895,
      11468935,
      11468975,
      11469015,
      11469055,
      11493120,
      11493215,
      11493255,
      11493295,
      11493335,
      11493375,
      11503360,
      11503455,
      11503495,
      11503535,
      11503575,
      11503615,
      11513600,
      11513695,
      11513735,
      11513775,
      11513815,
      11513855,
      11523840,
      11523935,
      11523975,
      11524015,
      11524055,
      11524095,
      11534080,
      11534175,
      11534215,
      11534255,
      11534295,
      11534335,
      14090240,
      14090335,
      14090375,
      14090415,
      14090455,
      14090495,
      14114560,
      14114655,
      14114695,
      14114735,
      14114775,
      14114815,
      14124800,
      14124895,
      14124935,
      14124975,
      14125015,
      14125055,
      14135040,
      14135135,
      14135175,
      14135215,
      14135255,
      14135295,
      14145280,
      14145375,
      14145415,
      14145455,
      14145495,
      14145535,
      14155520,
      14155615,
      14155655,
      14155695,
      14155735,
      14155775,
      16711680,
      16711775,
      16711815,
      16711855,
      16711895,
      16711935,
      16736000,
      16736095,
      16736135,
      16736175,
      16736215,
      16736255,
      16746240,
      16746335,
      16746375,
      16746415,
      16746455,
      16746495,
      16756480,
      16756575,
      16756615,
      16756655,
      16756695,
      16756735,
      16766720,
      16766815,
      16766855,
      16766895,
      16766935,
      16766975,
      16776960,
      16777055,
      16777095,
      16777135,
      16777175,
      16777215,
      526344,
      1184274,
      1842204,
      2500134,
      3158064,
      3815994,
      4473924,
      5131854,
      5789784,
      6447714,
      7105644,
      7763574,
      8421504,
      9079434,
      9737364,
      10395294,
      11053224,
      11711154,
      12369084,
      13027014,
      13684944,
      14342874,
      15000804,
      15658734
   };
   public static final double[] D50 = new double[]{96.422F, 100.0, 82.521F};
   public static final double[] D65 = new double[]{95.047, 100.0, 108.883};
   public static final double[] averageSurrounding = new double[]{1.0, 0.69, 1.0};
   public static final double[] dimSurrounding = new double[]{0.9, 0.59, 0.9};
   public static final double[] darkSurrounding = new double[]{0.8, 0.525, 0.8};
   public static final double[] sRGB_encoding_environment = vc(D50, 64.0, 12.0, dimSurrounding);
   public static final double[] sRGB_typical_environment = vc(D50, 200.0, 40.0, averageSurrounding);
   public static final double[] AdobeRGB_environment = vc(D65, 160.0, 32.0, averageSurrounding);
   private static int[] COLORS_256 = DEFAULT_COLORS_256;
   private static Map<String, Integer> COLOR_NAMES;
   private static final int L = 0;
   private static final int A = 1;
   private static final int B = 2;
   private static final int X = 0;
   private static final int Y = 1;
   private static final int Z = 2;
   private static final double kl = 2.0;
   private static final double kc = 1.0;
   private static final double kh = 1.0;
   private static final double k1 = 0.045;
   private static final double k2 = 0.015;
   public static final int J = 0;
   public static final int Q = 1;
   public static final int C = 2;
   public static final int M = 3;
   public static final int s = 4;
   public static final int H = 5;
   public static final int h = 6;
   static final int SUR_F = 0;
   static final int SUR_C = 1;
   static final int SUR_N_C = 2;
   static final int VC_X_W = 0;
   static final int VC_Y_W = 1;
   static final int VC_Z_W = 2;
   static final int VC_L_A = 3;
   static final int VC_Y_B = 4;
   static final int VC_F = 5;
   static final int VC_C = 6;
   static final int VC_N_C = 7;
   static final int VC_Z = 8;
   static final int VC_N = 9;
   static final int VC_N_BB = 10;
   static final int VC_N_CB = 11;
   static final int VC_A_W = 12;
   static final int VC_F_L = 13;
   static final int VC_D_RGB_R = 14;
   static final int VC_D_RGB_G = 15;
   static final int VC_D_RGB_B = 16;
   private static final double epsilon = 0.008856451679035631;
   private static final double kappa = 903.2962962962963;

   public static void setRgbColors(int[] var0) {
      if (var0 != null && var0.length == 256) {
         COLORS_256 = var0;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static int rgbColor(int var0) {
      return COLORS_256[var0];
   }

   public static Integer rgbColor(String var0) {
      if (COLOR_NAMES == null) {
         LinkedHashMap var1 = new LinkedHashMap();

         try (
            InputStream var2 = InfoCmp.class.getResourceAsStream("colors.txt");
            BufferedReader var4 = new BufferedReader(new InputStreamReader(var2, StandardCharsets.UTF_8));
         ) {
            var4.lines()
               .map(String::trim)
               .filter(var0x -> !var0x.startsWith("#"))
               .filter(var0x -> !var0x.isEmpty())
               .forEachOrdered(var1x -> var1.put(var1x, var1.size()));
            COLOR_NAMES = var1;
         } catch (IOException var34) {
            throw new IOError(var34);
         }
      }

      return COLOR_NAMES.get(var0);
   }

   public static int roundColor(int var0, int var1) {
      return roundColor(var0, var1, null);
   }

   public static int roundColor(int var0, int var1, String var2) {
      if (var0 >= var1) {
         int var3 = COLORS_256[var0];
         var0 = roundColor(var3, COLORS_256, var1, var2);
      }

      return var0;
   }

   public static int roundRgbColor(int var0, int var1, int var2, int var3) {
      return roundColor((var0 << 16) + (var1 << 8) + var2, COLORS_256, var3, (String)null);
   }

   private static int roundColor(int var0, int[] var1, int var2, String var3) {
      return roundColor(var0, var1, var2, getDistance(var3));
   }

   private static int roundColor(int var0, int[] var1, int var2, Colors.Distance var3) {
      double var4 = 2.147483647E9;
      int var6 = Integer.MAX_VALUE;

      for (int var7 = 0; var7 < var2; var7++) {
         double var8 = var3.compute(var0, var1[var7]);
         if (var8 <= var4) {
            var6 = var7;
            var4 = var8;
         }
      }

      return var6;
   }

   private static Colors.Distance getDistance(String var0) {
      if (var0 == null) {
         var0 = System.getProperty("org.jline.utils.colorDistance", "cie76");
      }

      return doGetDistance(var0);
   }

   private static Colors.Distance doGetDistance(String var0) {
      if (var0.equals("rgb")) {
         return (var0x, var1x) -> {
            double[] var2 = rgb(var0x);
            double[] var3 = rgb(var1x);
            double var4 = (var2[0] + var3[0]) / 2.0;
            double[] var6 = new double[]{2.0 + var4, 4.0, 3.0 - var4};
            return scalar(var2, var3, var6);
         };
      } else if (var0.matches("rgb\\(([0-9]+(\\.[0-9]+)?),([0-9]+(\\.[0-9]+)?),([0-9]+(\\.[0-9]+)?)\\)")) {
         return (var1x, var2) -> scalar(rgb(var1x), rgb(var2), getWeights(var0));
      } else if (var0.equals("lab") || var0.equals("cie76")) {
         return (var0x, var1x) -> scalar(rgb2cielab(var0x), rgb2cielab(var1x));
      } else if (var0.matches("lab\\(([0-9]+(\\.[0-9]+)?),([0-9]+(\\.[0-9]+)?)\\)")) {
         double[] var1 = getWeights(var0);
         return (var1x, var2) -> scalar(rgb2cielab(var1x), rgb2cielab(var2), new double[]{var1[0], var1[1], var1[1]});
      } else if (var0.equals("cie94")) {
         return (var0x, var1x) -> cie94(rgb2cielab(var0x), rgb2cielab(var1x));
      } else if (var0.equals("cie00") || var0.equals("cie2000")) {
         return (var0x, var1x) -> cie00(rgb2cielab(var0x), rgb2cielab(var1x));
      } else if (var0.equals("cam02")) {
         return (var0x, var1x) -> cam02(var0x, var1x, sRGB_typical_environment);
      } else if (var0.equals("camlab")) {
         return (var0x, var1x) -> {
            double[] var2 = camlab(var0x, sRGB_typical_environment);
            double[] var3 = camlab(var1x, sRGB_typical_environment);
            return scalar(var2, var3);
         };
      } else if (var0.matches("camlab\\(([0-9]+(\\.[0-9]+)?),([0-9]+(\\.[0-9]+)?)\\)")) {
         return (var1x, var2) -> {
            double[] var3 = camlab(var1x, sRGB_typical_environment);
            double[] var4 = camlab(var2, sRGB_typical_environment);
            double[] var5 = getWeights(var0);
            return scalar(var3, var4, new double[]{var5[0], var5[1], var5[1]});
         };
      } else if (var0.matches("camlch")) {
         return (var0x, var1x) -> {
            double[] var2 = camlch(var0x, sRGB_typical_environment);
            double[] var3 = camlch(var1x, sRGB_typical_environment);
            return camlch(var2, var3);
         };
      } else if (var0.matches("camlch\\(([0-9]+(\\.[0-9]+)?),([0-9]+(\\.[0-9]+)?),([0-9]+(\\.[0-9]+)?)\\)")) {
         return (var1x, var2) -> {
            double[] var3 = camlch(var1x, sRGB_typical_environment);
            double[] var4 = camlch(var2, sRGB_typical_environment);
            double[] var5 = getWeights(var0);
            return camlch(var3, var4, var5);
         };
      } else {
         throw new IllegalArgumentException("Unsupported distance function: " + var0);
      }
   }

   private static double[] getWeights(String var0) {
      String[] var1 = var0.substring(var0.indexOf(40) + 1, var0.length() - 1).split(",");
      return Stream.of(var1).mapToDouble(Double::parseDouble).toArray();
   }

   private static double scalar(double[] var0, double[] var1, double[] var2) {
      return sqr((var0[0] - var1[0]) * var2[0]) + sqr((var0[1] - var1[1]) * var2[1]) + sqr((var0[2] - var1[2]) * var2[2]);
   }

   private static double scalar(double[] var0, double[] var1) {
      return sqr(var0[0] - var1[0]) + sqr(var0[1] - var1[1]) + sqr(var0[2] - var1[2]);
   }

   private static double cie94(double[] var0, double[] var1) {
      double var2 = var0[0] - var1[0];
      double var4 = var0[1] - var1[1];
      double var6 = var0[2] - var1[2];
      double var8 = Math.sqrt(var0[1] * var0[1] + var0[2] * var0[2]);
      double var10 = Math.sqrt(var1[1] * var1[1] + var1[2] * var1[2]);
      double var12 = var8 - var10;
      double var14 = var4 * var4 + var6 * var6 - var12 * var12;
      var14 = var14 < 0.0 ? 0.0 : Math.sqrt(var14);
      double var16 = 1.0;
      double var18 = 1.0 + 0.045 * var8;
      double var20 = 1.0 + 0.015 * var8;
      double var22 = var2 / (2.0 * var16);
      double var24 = var12 / (1.0 * var18);
      double var26 = var14 / (1.0 * var20);
      return var22 * var22 + var24 * var24 + var26 * var26;
   }

   private static double cie00(double[] var0, double[] var1) {
      double var2 = Math.sqrt(var0[1] * var0[1] + var0[2] * var0[2]);
      double var4 = Math.sqrt(var1[1] * var1[1] + var1[2] * var1[2]);
      double var6 = (var2 + var4) / 2.0;
      double var8 = var6 * var6 * var6;
      double var10 = var8 * var8 * var6;
      double var12 = 0.5 * (1.0 - Math.sqrt(var10 / (var10 + 6.103515625E9)));
      double var14 = (1.0 + var12) * var0[1];
      double var16 = (1.0 + var12) * var1[1];
      double var18 = Math.sqrt(var14 * var14 + var0[2] * var0[2]);
      double var20 = Math.sqrt(var16 * var16 + var1[2] * var1[2]);
      double var22 = (Math.toDegrees(Math.atan2(var0[2], var14)) + 360.0) % 360.0;
      double var24 = (Math.toDegrees(Math.atan2(var1[2], var16)) + 360.0) % 360.0;
      double var26 = var1[0] - var0[0];
      double var28 = var20 - var18;
      double var30 = Math.abs(var22 - var24);
      double var32;
      if (var18 * var20 == 0.0) {
         var32 = 0.0;
      } else if (var30 <= 180.0) {
         var32 = var24 - var22;
      } else if (var24 <= var22) {
         var32 = var24 - var22 + 360.0;
      } else {
         var32 = var24 - var22 - 360.0;
      }

      double var34 = 2.0 * Math.sqrt(var18 * var20) * Math.sin(Math.toRadians(var32 / 2.0));
      double var36 = (var0[0] + var1[0]) / 2.0;
      double var38 = (var18 + var20) / 2.0;
      double var40;
      if (var18 * var20 == 0.0) {
         var40 = 0.0;
      } else if (var30 <= 180.0) {
         var40 = (var22 + var24) / 2.0;
      } else if (var22 + var24 < 360.0) {
         var40 = (var22 + var24 + 360.0) / 2.0;
      } else {
         var40 = (var22 + var24 - 360.0) / 2.0;
      }

      double var42 = var36 - 50.0;
      double var44 = var42 * var42;
      double var46 = 1.0
         - 0.17 * Math.cos(Math.toRadians(var40 - 30.0))
         + 0.24 * Math.cos(Math.toRadians(var40 * 2.0))
         + 0.32 * Math.cos(Math.toRadians(var40 * 3.0 + 6.0))
         - 0.2 * Math.cos(Math.toRadians(var40 * 4.0 - 63.0));
      double var48 = 1.0 + 0.015 * var44 / Math.sqrt(20.0 + var44);
      double var50 = 1.0 + 0.045 * var38;
      double var52 = 1.0 + 0.015 * var46 * var38;
      double var54 = (var40 - 275.0) / 25.0;
      double var56 = var54 * var54;
      double var58 = 30.0 * Math.exp(-var56);
      double var60 = var38 * var38 * var38;
      double var62 = var60 * var60 * var38;
      double var64 = 2.0 * Math.sqrt(var62 / (var62 + 6.103515625E9));
      double var66 = -Math.sin(Math.toRadians(2.0 * var58)) * var64;
      double var68 = var26 / (2.0 * var48);
      double var70 = var28 / (1.0 * var50);
      double var72 = var34 / (1.0 * var52);
      return var68 * var68 + var70 * var70 + var72 * var72 + var66 * var70 * var72;
   }

   private static double cam02(int var0, int var1, double[] var2) {
      double[] var3 = jmh2ucs(camlch(var0, var2));
      double[] var4 = jmh2ucs(camlch(var1, var2));
      return scalar(var3, var4);
   }

   private static double[] jmh2ucs(double[] var0) {
      double var1 = 1.7000000000000002 * var0[0] / (1.0 + 0.007 * var0[0]);
      double var3 = 43.859649122807014 * Math.log(1.0 + 0.0228 * var0[1]);
      double var5 = var3 * Math.cos(Math.toRadians(var0[2]));
      double var7 = var3 * Math.sin(Math.toRadians(var0[2]));
      return new double[]{var1, var5, var7};
   }

   static double camlch(double[] var0, double[] var1) {
      return camlch(var0, var1, new double[]{1.0, 1.0, 1.0});
   }

   static double camlch(double[] var0, double[] var1, double[] var2) {
      double var3 = var2[0] / 100.0;
      double var5 = var2[1] / 120.0;
      double var7 = var2[2] / 360.0;
      double var9 = (var0[0] - var1[0]) * var3;
      double var11 = (var0[1] - var1[1]) * var5;
      double var13 = hueDifference(var0[2], var1[2], 360.0) * var7;
      return var9 * var9 + var11 * var11 + var13 * var13;
   }

   private static double hueDifference(double var0, double var2, double var4) {
      double var6 = (var2 - var0) % var4;
      double var8 = var4 / 2.0;
      if (var6 > var8) {
         var6 -= var4;
      }

      if (var6 < -var8) {
         var6 += var4;
      }

      return var6;
   }

   private static double[] rgb(int var0) {
      int var1 = var0 >> 16 & 0xFF;
      int var2 = var0 >> 8 & 0xFF;
      int var3 = var0 >> 0 & 0xFF;
      return new double[]{(double)var1 / 255.0, (double)var2 / 255.0, (double)var3 / 255.0};
   }

   static double[] rgb2xyz(int var0) {
      return rgb2xyz(rgb(var0));
   }

   static double[] rgb2cielab(int var0) {
      return rgb2cielab(rgb(var0));
   }

   static double[] camlch(int var0) {
      return camlch(var0, sRGB_typical_environment);
   }

   static double[] camlch(int var0, double[] var1) {
      return xyz2camlch(rgb2xyz(var0), var1);
   }

   static double[] camlab(int var0) {
      return camlab(var0, sRGB_typical_environment);
   }

   static double[] camlab(int var0, double[] var1) {
      return lch2lab(camlch(var0, var1));
   }

   static double[] lch2lab(double[] var0) {
      double var1 = Math.PI / 180.0;
      return new double[]{var0[0], var0[1] * Math.cos(var0[2] * var1), var0[1] * Math.sin(var0[2] * var1)};
   }

   private static double[] xyz2camlch(double[] var0, double[] var1) {
      double[] var2 = new double[]{var0[0] * 100.0, var0[1] * 100.0, var0[2] * 100.0};
      double[] var3 = forwardTransform(var2, var1);
      return new double[]{var3[0], var3[3], var3[6]};
   }

   private static double[] forwardTransform(double[] var0, double[] var1) {
      double[] var2 = forwardPreAdaptationConeResponse(var0);
      double[] var3 = forwardPostAdaptationConeResponse(var2, var1);
      double[] var4 = CAT02toHPE(var3);
      double[] var5 = forwardResponseCompression(var4, var1);
      double var6 = (2.0 * var5[0] + var5[1] + var5[2] / 20.0 - 0.305) * var1[10];
      double var8 = 100.0 * Math.pow(var6 / var1[12], var1[8] * var1[6]);
      double var10 = var5[0] + (-12.0 * var5[1] + var5[2]) / 11.0;
      double var12 = (var5[0] + var5[1] - 2.0 * var5[2]) / 9.0;
      double var14 = (Math.toDegrees(Math.atan2(var12, var10)) + 360.0) % 360.0;
      double var16 = 961.5384615384615 * var1[7] * var1[11] * (Math.cos(Math.toRadians(var14) + 2.0) + 3.8);
      double var18 = var16 * Math.sqrt(Math.pow(var10, 2.0) + Math.pow(var12, 2.0)) / (var5[0] + var5[1] + 1.05 * var5[2]);
      double var20 = 4.0 / var1[6] * Math.sqrt(var8 / 100.0) * (var1[12] + 4.0) * Math.pow(var1[13], 0.25);
      double var22 = Math.signum(var18) * Math.pow(Math.abs(var18), 0.9) * Math.sqrt(var8 / 100.0) * Math.pow(1.64 - Math.pow(0.29, var1[9]), 0.73);
      double var24 = var22 * Math.pow(var1[13], 0.25);
      double var26 = 100.0 * Math.sqrt(var24 / var20);
      double var28 = calculateH(var14);
      return new double[]{var8, var20, var22, var24, var26, var28, var14};
   }

   private static double calculateH(double var0) {
      if (var0 < 20.14) {
         var0 += 360.0;
      }

      if (var0 >= 20.14 && var0 < 90.0) {
         double var8 = (var0 - 20.14) / 0.8;
         return 100.0 * var8 / (var8 + (90.0 - var0) / 0.7);
      } else if (var0 < 164.25) {
         double var7 = (var0 - 90.0) / 0.7;
         return 100.0 + 100.0 * var7 / (var7 + (164.25 - var0) / 1.0);
      } else if (var0 < 237.53) {
         double var6 = (var0 - 164.25) / 1.0;
         return 200.0 + 100.0 * var6 / (var6 + (237.53 - var0) / 1.2);
      } else if (var0 <= 380.14) {
         double var2 = (var0 - 237.53) / 1.2;
         double var4 = 300.0 + 100.0 * var2 / (var2 + (380.14 - var0) / 0.8);
         if (var4 <= 400.0 && var4 >= 399.999) {
            var4 = 0.0;
         }

         return var4;
      } else {
         throw new IllegalArgumentException("h outside assumed range 0..360: " + Double.toString(var0));
      }
   }

   private static double[] forwardResponseCompression(double[] var0, double[] var1) {
      double[] var2 = new double[3];

      for (int var3 = 0; var3 < var0.length; var3++) {
         if (var0[var3] >= 0.0) {
            double var4 = Math.pow(var1[13] * var0[var3] / 100.0, 0.42);
            var2[var3] = 400.0 * var4 / (var4 + 27.13) + 0.1;
         } else {
            double var6 = Math.pow(-1.0 * var1[13] * var0[var3] / 100.0, 0.42);
            var2[var3] = -400.0 * var6 / (var6 + 27.13) + 0.1;
         }
      }

      return var2;
   }

   private static double[] forwardPostAdaptationConeResponse(double[] var0, double[] var1) {
      return new double[]{var1[14] * var0[0], var1[15] * var0[1], var1[16] * var0[2]};
   }

   public static double[] CAT02toHPE(double[] var0) {
      return new double[]{
         0.7409792 * var0[0] + 0.218025 * var0[1] + 0.0410058 * var0[2],
         0.2853532 * var0[0] + 0.6242014 * var0[1] + 0.0904454 * var0[2],
         -0.009628 * var0[0] - 0.005698 * var0[1] + 1.015326 * var0[2]
      };
   }

   private static double[] forwardPreAdaptationConeResponse(double[] var0) {
      return new double[]{
         0.7328 * var0[0] + 0.4296 * var0[1] - 0.1624 * var0[2],
         -0.7036 * var0[0] + 1.6975 * var0[1] + 0.0061 * var0[2],
         0.003 * var0[0] + 0.0136 * var0[1] + 0.9834 * var0[2]
      };
   }

   static double[] vc(double[] var0, double var1, double var3, double[] var5) {
      double[] var6 = new double[17];
      var6[0] = var0[0];
      var6[1] = var0[1];
      var6[2] = var0[2];
      var6[3] = var1;
      var6[4] = var3;
      var6[5] = var5[0];
      var6[6] = var5[1];
      var6[7] = var5[2];
      double[] var7 = forwardPreAdaptationConeResponse(var0);
      double var8 = Math.max(0.0, Math.min(1.0, var6[5] * (1.0 - 0.2777777777777778 * Math.pow(Math.E, (-var1 - 42.0) / 92.0))));
      double var10 = var0[1];
      double[] var12 = new double[]{var8 * var10 / var7[0] + (1.0 - var8), var8 * var10 / var7[1] + (1.0 - var8), var8 * var10 / var7[2] + (1.0 - var8)};
      double var13 = 5.0 * var1;
      double var15 = 1.0 / (var13 + 1.0);
      double var17 = Math.pow(var15, 4.0);
      var6[13] = 0.2 * var17 * var13 + 0.1 * Math.pow(1.0 - var17, 2.0) * Math.pow(var13, 0.3333333333333333);
      var6[9] = var3 / var10;
      var6[8] = 1.48 + Math.sqrt(var6[9]);
      var6[10] = 0.725 * Math.pow(1.0 / var6[9], 0.2);
      var6[11] = var6[10];
      double[] var19 = new double[]{var12[0] * var7[0], var12[1] * var7[1], var12[2] * var7[2]};
      double[] var20 = CAT02toHPE(var19);
      double[] var21 = new double[3];

      for (int var22 = 0; var22 < var20.length; var22++) {
         if (var20[var22] >= 0.0) {
            double var23 = Math.pow(var6[13] * var20[var22] / 100.0, 0.42);
            var21[var22] = 400.0 * var23 / (var23 + 27.13) + 0.1;
         } else {
            double var25 = Math.pow(-1.0 * var6[13] * var20[var22] / 100.0, 0.42);
            var21[var22] = -400.0 * var25 / (var25 + 27.13) + 0.1;
         }
      }

      var6[12] = (2.0 * var21[0] + var21[1] + var21[2] / 20.0 - 0.305) * var6[10];
      var6[14] = var12[0];
      var6[15] = var12[1];
      var6[16] = var12[2];
      return var6;
   }

   public static double[] rgb2cielab(double[] var0) {
      return xyz2lab(rgb2xyz(var0));
   }

   private static double[] rgb2xyz(double[] var0) {
      double var1 = pivotRgb(var0[0]);
      double var3 = pivotRgb(var0[1]);
      double var5 = pivotRgb(var0[2]);
      double var7 = var1 * 0.4124564 + var3 * 0.3575761 + var5 * 0.1804375;
      double var9 = var1 * 0.2126729 + var3 * 0.7151522 + var5 * 0.072175;
      double var11 = var1 * 0.0193339 + var3 * 0.119192 + var5 * 0.9503041;
      return new double[]{var7, var9, var11};
   }

   private static double pivotRgb(double var0) {
      return var0 > 0.04045 ? Math.pow((var0 + 0.055) / 1.055, 2.4) : var0 / 12.92;
   }

   private static double[] xyz2lab(double[] var0) {
      double var1 = pivotXyz(var0[0]);
      double var3 = pivotXyz(var0[1]);
      double var5 = pivotXyz(var0[2]);
      double var7 = 116.0 * var3 - 16.0;
      double var9 = 500.0 * (var1 - var3);
      double var11 = 200.0 * (var3 - var5);
      return new double[]{var7, var9, var11};
   }

   private static double pivotXyz(double var0) {
      return var0 > 0.008856451679035631 ? Math.cbrt(var0) : (903.2962962962963 * var0 + 16.0) / 116.0;
   }

   private static double sqr(double var0) {
      return var0 * var0;
   }

   private interface Distance {
      double compute(int var1, int var2);
   }
}
