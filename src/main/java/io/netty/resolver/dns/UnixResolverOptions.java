package io.netty.resolver.dns;

final class UnixResolverOptions {
   private final int ndots;
   private final int timeout;
   private final int attempts;

   UnixResolverOptions(int var1, int var2, int var3) {
      this.ndots = var1;
      this.timeout = var2;
      this.attempts = var3;
   }

   static UnixResolverOptions.Builder newBuilder() {
      return new UnixResolverOptions.Builder();
   }

   int ndots() {
      return this.ndots;
   }

   int timeout() {
      return this.timeout;
   }

   int attempts() {
      return this.attempts;
   }

   static final class Builder {
      private int ndots = 1;
      private int timeout = 5;
      private int attempts = 16;

      private Builder() {
      }

      void setNdots(int var1) {
         this.ndots = var1;
      }

      void setTimeout(int var1) {
         this.timeout = var1;
      }

      void setAttempts(int var1) {
         this.attempts = var1;
      }

      UnixResolverOptions build() {
         return new UnixResolverOptions(this.ndots, this.timeout, this.attempts);
      }
   }
}
