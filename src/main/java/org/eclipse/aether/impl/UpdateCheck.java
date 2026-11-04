package org.eclipse.aether.impl;

import java.io.File;
import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.repository.RemoteRepository;

public final class UpdateCheck<T, E extends RepositoryException> {
   private long localLastUpdated;
   private T item;
   private File file;
   private boolean fileValid = true;
   private String policy;
   private RemoteRepository repository;
   private RemoteRepository authoritativeRepository;
   private boolean required;
   private E exception;

   public long getLocalLastUpdated() {
      return this.localLastUpdated;
   }

   public UpdateCheck<T, E> setLocalLastUpdated(long var1) {
      this.localLastUpdated = var1;
      return this;
   }

   public T getItem() {
      return this.item;
   }

   public UpdateCheck<T, E> setItem(T var1) {
      this.item = (T)var1;
      return this;
   }

   public File getFile() {
      return this.file;
   }

   public UpdateCheck<T, E> setFile(File var1) {
      this.file = var1;
      return this;
   }

   public boolean isFileValid() {
      return this.fileValid;
   }

   public UpdateCheck<T, E> setFileValid(boolean var1) {
      this.fileValid = var1;
      return this;
   }

   public String getPolicy() {
      return this.policy;
   }

   public UpdateCheck<T, E> setPolicy(String var1) {
      this.policy = var1;
      return this;
   }

   public RemoteRepository getRepository() {
      return this.repository;
   }

   public UpdateCheck<T, E> setRepository(RemoteRepository var1) {
      this.repository = var1;
      return this;
   }

   public RemoteRepository getAuthoritativeRepository() {
      return this.authoritativeRepository != null ? this.authoritativeRepository : this.repository;
   }

   public UpdateCheck<T, E> setAuthoritativeRepository(RemoteRepository var1) {
      this.authoritativeRepository = var1;
      return this;
   }

   public boolean isRequired() {
      return this.required;
   }

   public UpdateCheck<T, E> setRequired(boolean var1) {
      this.required = var1;
      return this;
   }

   public E getException() {
      return this.exception;
   }

   public UpdateCheck<T, E> setException(E var1) {
      this.exception = (E)var1;
      return this;
   }

   @Override
   public String toString() {
      return this.getPolicy() + ": " + this.getFile() + " < " + this.getRepository();
   }
}
