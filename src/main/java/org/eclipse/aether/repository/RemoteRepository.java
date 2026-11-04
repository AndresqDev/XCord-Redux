package org.eclipse.aether.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RemoteRepository implements ArtifactRepository {
   private static final Pattern URL_PATTERN = Pattern.compile("([^:/]+(:[^:/]{2,}+(?=://))?):(//([^@/]*@)?([^/:]+))?.*");
   private final String id;
   private final String type;
   private final String url;
   private final String host;
   private final String protocol;
   private final RepositoryPolicy releasePolicy;
   private final RepositoryPolicy snapshotPolicy;
   private final Proxy proxy;
   private final Authentication authentication;
   private final List<RemoteRepository> mirroredRepositories;
   private final boolean repositoryManager;
   private final boolean blocked;

   RemoteRepository(RemoteRepository.Builder var1) {
      if (var1.prototype != null) {
         this.id = (var1.delta & 1) != 0 ? var1.id : var1.prototype.id;
         this.type = (var1.delta & 2) != 0 ? var1.type : var1.prototype.type;
         this.url = (var1.delta & 4) != 0 ? var1.url : var1.prototype.url;
         this.releasePolicy = (var1.delta & 8) != 0 ? var1.releasePolicy : var1.prototype.releasePolicy;
         this.snapshotPolicy = (var1.delta & 16) != 0 ? var1.snapshotPolicy : var1.prototype.snapshotPolicy;
         this.proxy = (var1.delta & 32) != 0 ? var1.proxy : var1.prototype.proxy;
         this.authentication = (var1.delta & 64) != 0 ? var1.authentication : var1.prototype.authentication;
         this.repositoryManager = (var1.delta & 256) != 0 ? var1.repositoryManager : var1.prototype.repositoryManager;
         this.blocked = (var1.delta & 512) != 0 ? var1.blocked : var1.prototype.blocked;
         this.mirroredRepositories = (var1.delta & 128) != 0 ? copy(var1.mirroredRepositories) : var1.prototype.mirroredRepositories;
      } else {
         this.id = var1.id;
         this.type = var1.type;
         this.url = var1.url;
         this.releasePolicy = var1.releasePolicy;
         this.snapshotPolicy = var1.snapshotPolicy;
         this.proxy = var1.proxy;
         this.authentication = var1.authentication;
         this.repositoryManager = var1.repositoryManager;
         this.blocked = var1.blocked;
         this.mirroredRepositories = copy(var1.mirroredRepositories);
      }

      Matcher var2 = URL_PATTERN.matcher(this.url);
      if (var2.matches()) {
         this.protocol = var2.group(1);
         String var3 = var2.group(5);
         this.host = var3 != null ? var3 : "";
      } else {
         this.protocol = "";
         this.host = "";
      }
   }

   private static List<RemoteRepository> copy(List<RemoteRepository> var0) {
      return var0 != null && !var0.isEmpty()
         ? Collections.unmodifiableList(Arrays.asList(var0.toArray(new RemoteRepository[var0.size()])))
         : Collections.emptyList();
   }

   @Override
   public String getId() {
      return this.id;
   }

   @Override
   public String getContentType() {
      return this.type;
   }

   public String getUrl() {
      return this.url;
   }

   public String getProtocol() {
      return this.protocol;
   }

   public String getHost() {
      return this.host;
   }

   public RepositoryPolicy getPolicy(boolean var1) {
      return var1 ? this.snapshotPolicy : this.releasePolicy;
   }

   public Proxy getProxy() {
      return this.proxy;
   }

   public Authentication getAuthentication() {
      return this.authentication;
   }

   public List<RemoteRepository> getMirroredRepositories() {
      return this.mirroredRepositories;
   }

   public boolean isRepositoryManager() {
      return this.repositoryManager;
   }

   public boolean isBlocked() {
      return this.blocked;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder(256);
      var1.append(this.getId());
      var1.append(" (").append(this.getUrl());
      var1.append(", ").append(this.getContentType());
      boolean var2 = this.getPolicy(false).isEnabled();
      boolean var3 = this.getPolicy(true).isEnabled();
      if (var2 && var3) {
         var1.append(", releases+snapshots");
      } else if (var2) {
         var1.append(", releases");
      } else if (var3) {
         var1.append(", snapshots");
      } else {
         var1.append(", disabled");
      }

      if (this.isRepositoryManager()) {
         var1.append(", managed");
      }

      if (this.isBlocked()) {
         var1.append(", blocked");
      }

      var1.append(")");
      return var1.toString();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass().equals(var1.getClass())) {
         RemoteRepository var2 = (RemoteRepository)var1;
         return Objects.equals(this.url, var2.url)
            && Objects.equals(this.type, var2.type)
            && Objects.equals(this.id, var2.id)
            && Objects.equals(this.releasePolicy, var2.releasePolicy)
            && Objects.equals(this.snapshotPolicy, var2.snapshotPolicy)
            && Objects.equals(this.proxy, var2.proxy)
            && Objects.equals(this.authentication, var2.authentication)
            && Objects.equals(this.mirroredRepositories, var2.mirroredRepositories)
            && this.repositoryManager == var2.repositoryManager;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = 17;
      var1 = var1 * 31 + hash(this.url);
      var1 = var1 * 31 + hash(this.type);
      var1 = var1 * 31 + hash(this.id);
      var1 = var1 * 31 + hash(this.releasePolicy);
      var1 = var1 * 31 + hash(this.snapshotPolicy);
      var1 = var1 * 31 + hash(this.proxy);
      var1 = var1 * 31 + hash(this.authentication);
      var1 = var1 * 31 + hash(this.mirroredRepositories);
      return var1 * 31 + (this.repositoryManager ? 1 : 0);
   }

   private static int hash(Object var0) {
      return var0 != null ? var0.hashCode() : 0;
   }

   public static final class Builder {
      private static final RepositoryPolicy DEFAULT_POLICY = new RepositoryPolicy();
      static final int ID = 1;
      static final int TYPE = 2;
      static final int URL = 4;
      static final int RELEASES = 8;
      static final int SNAPSHOTS = 16;
      static final int PROXY = 32;
      static final int AUTH = 64;
      static final int MIRRORED = 128;
      static final int REPOMAN = 256;
      static final int BLOCKED = 512;
      int delta;
      RemoteRepository prototype;
      String id;
      String type;
      String url;
      RepositoryPolicy releasePolicy = DEFAULT_POLICY;
      RepositoryPolicy snapshotPolicy = DEFAULT_POLICY;
      Proxy proxy;
      Authentication authentication;
      List<RemoteRepository> mirroredRepositories;
      boolean repositoryManager;
      boolean blocked;

      public Builder(String var1, String var2, String var3) {
         this.id = var1 != null ? var1 : "";
         this.type = var2 != null ? var2 : "";
         this.url = var3 != null ? var3 : "";
      }

      public Builder(RemoteRepository var1) {
         this.prototype = Objects.requireNonNull(var1, "remote repository prototype cannot be null");
      }

      public RemoteRepository build() {
         return this.prototype != null && this.delta == 0 ? this.prototype : new RemoteRepository(this);
      }

      private <T> void delta(int var1, T var2, T var3) {
         boolean var4 = Objects.equals(var2, var3);
         if (var4) {
            this.delta &= ~var1;
         } else {
            this.delta |= var1;
         }
      }

      public RemoteRepository.Builder setId(String var1) {
         this.id = var1 != null ? var1 : "";
         if (this.prototype != null) {
            this.delta(1, this.id, this.prototype.getId());
         }

         return this;
      }

      public RemoteRepository.Builder setContentType(String var1) {
         this.type = var1 != null ? var1 : "";
         if (this.prototype != null) {
            this.delta(2, this.type, this.prototype.getContentType());
         }

         return this;
      }

      public RemoteRepository.Builder setUrl(String var1) {
         this.url = var1 != null ? var1 : "";
         if (this.prototype != null) {
            this.delta(4, this.url, this.prototype.getUrl());
         }

         return this;
      }

      public RemoteRepository.Builder setPolicy(RepositoryPolicy var1) {
         this.releasePolicy = var1 != null ? var1 : DEFAULT_POLICY;
         this.snapshotPolicy = var1 != null ? var1 : DEFAULT_POLICY;
         if (this.prototype != null) {
            this.delta(8, this.releasePolicy, this.prototype.getPolicy(false));
            this.delta(16, this.snapshotPolicy, this.prototype.getPolicy(true));
         }

         return this;
      }

      public RemoteRepository.Builder setReleasePolicy(RepositoryPolicy var1) {
         this.releasePolicy = var1 != null ? var1 : DEFAULT_POLICY;
         if (this.prototype != null) {
            this.delta(8, this.releasePolicy, this.prototype.getPolicy(false));
         }

         return this;
      }

      public RemoteRepository.Builder setSnapshotPolicy(RepositoryPolicy var1) {
         this.snapshotPolicy = var1 != null ? var1 : DEFAULT_POLICY;
         if (this.prototype != null) {
            this.delta(16, this.snapshotPolicy, this.prototype.getPolicy(true));
         }

         return this;
      }

      public RemoteRepository.Builder setProxy(Proxy var1) {
         this.proxy = var1;
         if (this.prototype != null) {
            this.delta(32, this.proxy, this.prototype.getProxy());
         }

         return this;
      }

      public RemoteRepository.Builder setAuthentication(Authentication var1) {
         this.authentication = var1;
         if (this.prototype != null) {
            this.delta(64, this.authentication, this.prototype.getAuthentication());
         }

         return this;
      }

      public RemoteRepository.Builder setMirroredRepositories(List<RemoteRepository> var1) {
         if (this.mirroredRepositories == null) {
            this.mirroredRepositories = new ArrayList<>();
         } else {
            this.mirroredRepositories.clear();
         }

         if (var1 != null) {
            this.mirroredRepositories.addAll(var1);
         }

         if (this.prototype != null) {
            this.delta(128, this.mirroredRepositories, this.prototype.getMirroredRepositories());
         }

         return this;
      }

      public RemoteRepository.Builder addMirroredRepository(RemoteRepository var1) {
         if (var1 != null) {
            if (this.mirroredRepositories == null) {
               this.mirroredRepositories = new ArrayList<>();
               if (this.prototype != null) {
                  this.mirroredRepositories.addAll(this.prototype.getMirroredRepositories());
               }
            }

            this.mirroredRepositories.add(var1);
            if (this.prototype != null) {
               this.delta |= 128;
            }
         }

         return this;
      }

      public RemoteRepository.Builder setRepositoryManager(boolean var1) {
         this.repositoryManager = var1;
         if (this.prototype != null) {
            this.delta(256, this.repositoryManager, this.prototype.isRepositoryManager());
         }

         return this;
      }

      public RemoteRepository.Builder setBlocked(boolean var1) {
         this.blocked = var1;
         if (this.prototype != null) {
            this.delta(512, this.blocked, this.prototype.isBlocked());
         }

         return this;
      }
   }
}
