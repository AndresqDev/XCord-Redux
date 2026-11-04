package io.netty.handler.codec.http.multipart;

import java.io.File;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

final class DeleteFileOnExitHook {
   private static final Set<String> FILES = Collections.newSetFromMap(new ConcurrentHashMap<>());

   private DeleteFileOnExitHook() {
   }

   public static void remove(String var0) {
      FILES.remove(var0);
   }

   public static void add(String var0) {
      FILES.add(var0);
   }

   public static boolean checkFileExist(String var0) {
      return FILES.contains(var0);
   }

   static void runHook() {
      for (String var1 : FILES) {
         new File(var1).delete();
      }
   }

   static {
      Runtime.getRuntime().addShutdownHook(new Thread() {
         @Override
         public void run() {
            DeleteFileOnExitHook.runHook();
         }
      });
   }
}
