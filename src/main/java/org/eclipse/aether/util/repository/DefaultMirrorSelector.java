package org.eclipse.aether.util.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.aether.repository.MirrorSelector;
import org.eclipse.aether.repository.RemoteRepository;

public final class DefaultMirrorSelector implements MirrorSelector {
   private static final String WILDCARD = "*";
   private static final String EXTERNAL_WILDCARD = "external:*";
   private static final String EXTERNAL_HTTP_WILDCARD = "external:http:*";
   private final List<DefaultMirrorSelector.MirrorDef> mirrors = new ArrayList<>();

   @Deprecated
   public DefaultMirrorSelector add(String var1, String var2, String var3, boolean var4, String var5, String var6) {
      return this.add(var1, var2, var3, var4, false, var5, var6);
   }

   public DefaultMirrorSelector add(String var1, String var2, String var3, boolean var4, boolean var5, String var6, String var7) {
      this.mirrors.add(new DefaultMirrorSelector.MirrorDef(var1, var2, var3, var4, var5, var6, var7));
      return this;
   }

   @Override
   public RemoteRepository getMirror(RemoteRepository var1) {
      DefaultMirrorSelector.MirrorDef var2 = this.findMirror(var1);
      if (var2 == null) {
         return null;
      } else {
         RemoteRepository.Builder var3 = new RemoteRepository.Builder(var2.id, var1.getContentType(), var2.url);
         var3.setRepositoryManager(var2.repositoryManager);
         var3.setBlocked(var2.blocked);
         if (var2.type != null && var2.type.length() > 0) {
            var3.setContentType(var2.type);
         }

         var3.setSnapshotPolicy(var1.getPolicy(true));
         var3.setReleasePolicy(var1.getPolicy(false));
         var3.setMirroredRepositories(Collections.singletonList(var1));
         return var3.build();
      }
   }

   private DefaultMirrorSelector.MirrorDef findMirror(RemoteRepository var1) {
      String var2 = var1.getId();
      if (var2 != null && !this.mirrors.isEmpty()) {
         for (DefaultMirrorSelector.MirrorDef var4 : this.mirrors) {
            if (var2.equals(var4.mirrorOfIds) && matchesType(var1.getContentType(), var4.mirrorOfTypes)) {
               return var4;
            }
         }

         for (DefaultMirrorSelector.MirrorDef var6 : this.mirrors) {
            if (matchPattern(var1, var6.mirrorOfIds) && matchesType(var1.getContentType(), var6.mirrorOfTypes)) {
               return var6;
            }
         }
      }

      return null;
   }

   static boolean matchPattern(RemoteRepository var0, String var1) {
      boolean var2 = false;
      String var3 = var0.getId();
      if (!"*".equals(var1) && !var1.equals(var3)) {
         String[] var4 = var1.split(",");

         for (String var8 : var4) {
            if (var8.length() > 1 && var8.startsWith("!")) {
               if (var8.substring(1).equals(var3)) {
                  var2 = false;
                  break;
               }
            } else {
               if (var8.equals(var3)) {
                  var2 = true;
                  break;
               }

               if ("external:*".equals(var8) && isExternalRepo(var0)) {
                  var2 = true;
               } else if ("external:http:*".equals(var8) && isExternalHttpRepo(var0)) {
                  var2 = true;
               } else if ("*".equals(var8)) {
                  var2 = true;
               }
            }
         }
      } else {
         var2 = true;
      }

      return var2;
   }

   static boolean isExternalRepo(RemoteRepository var0) {
      boolean var1 = isLocal(var0.getHost()) || "file".equalsIgnoreCase(var0.getProtocol());
      return !var1;
   }

   private static boolean isLocal(String var0) {
      return "localhost".equals(var0) || "127.0.0.1".equals(var0);
   }

   static boolean isExternalHttpRepo(RemoteRepository var0) {
      return (
            "http".equalsIgnoreCase(var0.getProtocol())
               || "dav".equalsIgnoreCase(var0.getProtocol())
               || "dav:http".equalsIgnoreCase(var0.getProtocol())
               || "dav+http".equalsIgnoreCase(var0.getProtocol())
         )
         && !isLocal(var0.getHost());
   }

   static boolean matchesType(String var0, String var1) {
      boolean var2 = false;
      if (var1 == null || var1.length() <= 0 || "*".equals(var1)) {
         var2 = true;
      } else if (var1.equals(var0)) {
         var2 = true;
      } else {
         String[] var3 = var1.split(",");

         for (String var7 : var3) {
            if (var7.length() > 1 && var7.startsWith("!")) {
               if (var7.substring(1).equals(var0)) {
                  var2 = false;
                  break;
               }
            } else {
               if (var7.equals(var0)) {
                  var2 = true;
                  break;
               }

               if ("*".equals(var7)) {
                  var2 = true;
               }
            }
         }
      }

      return var2;
   }

   static class MirrorDef {
      final String id;
      final String url;
      final String type;
      final boolean repositoryManager;
      final boolean blocked;
      final String mirrorOfIds;
      final String mirrorOfTypes;

      MirrorDef(String var1, String var2, String var3, boolean var4, boolean var5, String var6, String var7) {
         this.id = var1;
         this.url = var2;
         this.type = var3;
         this.repositoryManager = var4;
         this.blocked = var5;
         this.mirrorOfIds = var6;
         this.mirrorOfTypes = var7;
      }
   }
}
