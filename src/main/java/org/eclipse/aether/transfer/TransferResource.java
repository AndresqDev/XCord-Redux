package org.eclipse.aether.transfer;

import java.io.File;
import org.eclipse.aether.RequestTrace;

public final class TransferResource {
   private final String repositoryId;
   private final String repositoryUrl;
   private final String resourceName;
   private final File file;
   private final long startTime;
   private final RequestTrace trace;
   private long contentLength = -1L;
   private long resumeOffset;

   @Deprecated
   public TransferResource(String var1, String var2, File var3, RequestTrace var4) {
      this(null, var1, var2, var3, var4);
   }

   public TransferResource(String var1, String var2, String var3, File var4, RequestTrace var5) {
      if (var1 != null && var1.length() > 0) {
         this.repositoryId = var1;
      } else {
         this.repositoryId = "";
      }

      if (var2 == null || var2.length() <= 0) {
         this.repositoryUrl = "";
      } else if (var2.endsWith("/")) {
         this.repositoryUrl = var2;
      } else {
         this.repositoryUrl = var2 + '/';
      }

      if (var3 == null || var3.length() <= 0) {
         this.resourceName = "";
      } else if (var3.startsWith("/")) {
         this.resourceName = var3.substring(1);
      } else {
         this.resourceName = var3;
      }

      this.file = var4;
      this.trace = var5;
      this.startTime = System.currentTimeMillis();
   }

   public String getRepositoryId() {
      return this.repositoryId;
   }

   public String getRepositoryUrl() {
      return this.repositoryUrl;
   }

   public String getResourceName() {
      return this.resourceName;
   }

   public File getFile() {
      return this.file;
   }

   public long getContentLength() {
      return this.contentLength;
   }

   public TransferResource setContentLength(long var1) {
      this.contentLength = var1;
      return this;
   }

   public long getResumeOffset() {
      return this.resumeOffset;
   }

   public TransferResource setResumeOffset(long var1) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("resume offset cannot be negative");
      } else {
         this.resumeOffset = var1;
         return this;
      }
   }

   public long getTransferStartTime() {
      return this.startTime;
   }

   public RequestTrace getTrace() {
      return this.trace;
   }

   @Override
   public String toString() {
      return this.getRepositoryUrl() + this.getResourceName() + " <> " + this.getFile();
   }
}
