package org.apache.logging.log4j.core;

public class Version {
   public static void main(String[] var0) {
      System.out.println(getProductString());
   }

   public static String getProductString() {
      Package var0 = Version.class.getPackage();
      return var0 == null ? "Apache Log4j" : String.format("%s %s", var0.getSpecificationTitle(), var0.getSpecificationVersion());
   }
}
