package org.eclipse.aether.resolution;

import org.eclipse.aether.repository.RemoteRepository;

public final class ResolutionErrorPolicyRequest<T> {
   private T item;
   private RemoteRepository repository;

   public ResolutionErrorPolicyRequest() {
   }

   public ResolutionErrorPolicyRequest(T var1, RemoteRepository var2) {
      this.setItem((T)var1);
      this.setRepository(var2);
   }

   public T getItem() {
      return this.item;
   }

   public ResolutionErrorPolicyRequest<T> setItem(T var1) {
      this.item = (T)var1;
      return this;
   }

   public RemoteRepository getRepository() {
      return this.repository;
   }

   public ResolutionErrorPolicyRequest<T> setRepository(RemoteRepository var1) {
      this.repository = var1;
      return this;
   }

   @Override
   public String toString() {
      return this.getItem() + " < " + this.getRepository();
   }
}
