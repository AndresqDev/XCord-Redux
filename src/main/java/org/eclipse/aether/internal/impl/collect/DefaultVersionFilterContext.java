package org.eclipse.aether.internal.impl.collect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.collection.VersionFilter;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.ArtifactRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.version.Version;
import org.eclipse.aether.version.VersionConstraint;

final class DefaultVersionFilterContext implements VersionFilter.VersionFilterContext {
   private final RepositorySystemSession session;
   private Dependency dependency;
   VersionRangeResult result;
   int count;
   byte[] deleted = new byte[64];

   DefaultVersionFilterContext(RepositorySystemSession var1) {
      this.session = var1;
   }

   public void set(Dependency var1, VersionRangeResult var2) {
      this.dependency = var1;
      this.result = var2;
      this.count = var2.getVersions().size();
      if (this.deleted.length < this.count) {
         this.deleted = new byte[this.count];
      } else {
         for (int var3 = this.count - 1; var3 >= 0; var3--) {
            this.deleted[var3] = 0;
         }
      }
   }

   public List<Version> get() {
      if (this.count == this.result.getVersions().size()) {
         return this.result.getVersions();
      } else if (this.count <= 1) {
         return this.count <= 0 ? Collections.emptyList() : Collections.singletonList(this.iterator().next());
      } else {
         ArrayList var1 = new ArrayList(this.count);

         for (Version var3 : this) {
            var1.add(var3);
         }

         return var1;
      }
   }

   @Override
   public RepositorySystemSession getSession() {
      return this.session;
   }

   @Override
   public Dependency getDependency() {
      return this.dependency;
   }

   @Override
   public VersionConstraint getVersionConstraint() {
      return this.result.getVersionConstraint();
   }

   @Override
   public int getCount() {
      return this.count;
   }

   @Override
   public ArtifactRepository getRepository(Version var1) {
      return this.result.getRepository(var1);
   }

   @Override
   public List<RemoteRepository> getRepositories() {
      return Collections.unmodifiableList(this.result.getRequest().getRepositories());
   }

   @Override
   public Iterator<Version> iterator() {
      return (Iterator<Version>)(this.count > 0 ? new DefaultVersionFilterContext.VersionIterator() : Collections.<Version>emptySet().iterator());
   }

   @Override
   public String toString() {
      return this.dependency + " " + this.result.getVersions();
   }

   private class VersionIterator implements Iterator<Version> {
      private final List<Version> versions;
      private final int size;
      private int count;
      private int index;
      private int next;

      VersionIterator() {
         this.count = DefaultVersionFilterContext.this.count;
         this.index = -1;
         this.next = 0;
         this.versions = DefaultVersionFilterContext.this.result.getVersions();
         this.size = this.versions.size();
         this.advance();
      }

      private void advance() {
         this.next = this.index + 1;

         while (this.next < this.size && DefaultVersionFilterContext.this.deleted[this.next] != 0) {
            this.next++;
         }
      }

      @Override
      public boolean hasNext() {
         return this.next < this.size;
      }

      public Version next() {
         if (this.count != DefaultVersionFilterContext.this.count) {
            throw new ConcurrentModificationException();
         } else if (this.next >= this.size) {
            throw new NoSuchElementException();
         } else {
            this.index = this.next;
            this.advance();
            return this.versions.get(this.index);
         }
      }

      @Override
      public void remove() {
         if (this.count != DefaultVersionFilterContext.this.count) {
            throw new ConcurrentModificationException();
         } else if (this.index >= 0 && DefaultVersionFilterContext.this.deleted[this.index] != 1) {
            DefaultVersionFilterContext.this.deleted[this.index] = 1;
            this.count = --DefaultVersionFilterContext.this.count;
         } else {
            throw new IllegalStateException();
         }
      }

      @Override
      public String toString() {
         return this.index < 0 ? "null" : String.valueOf(this.versions.get(this.index));
      }
   }
}
