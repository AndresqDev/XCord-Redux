package org.yaml.snakeyaml;

public class LoaderOptions {
   private boolean allowDuplicateKeys = true;
   private boolean wrappedToRootException = false;
   private int maxAliasesForCollections = 50;
   private boolean allowRecursiveKeys = false;
   private boolean processComments = false;
   private boolean enumCaseSensitive = true;

   public boolean isAllowDuplicateKeys() {
      return this.allowDuplicateKeys;
   }

   public void setAllowDuplicateKeys(boolean var1) {
      this.allowDuplicateKeys = var1;
   }

   public boolean isWrappedToRootException() {
      return this.wrappedToRootException;
   }

   public void setWrappedToRootException(boolean var1) {
      this.wrappedToRootException = var1;
   }

   public int getMaxAliasesForCollections() {
      return this.maxAliasesForCollections;
   }

   public void setMaxAliasesForCollections(int var1) {
      this.maxAliasesForCollections = var1;
   }

   public void setAllowRecursiveKeys(boolean var1) {
      this.allowRecursiveKeys = var1;
   }

   public boolean getAllowRecursiveKeys() {
      return this.allowRecursiveKeys;
   }

   public void setProcessComments(boolean var1) {
      this.processComments = var1;
   }

   public boolean isProcessComments() {
      return this.processComments;
   }

   public boolean isEnumCaseSensitive() {
      return this.enumCaseSensitive;
   }

   public void setEnumCaseSensitive(boolean var1) {
      this.enumCaseSensitive = var1;
   }
}
