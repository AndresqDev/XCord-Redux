package io.netty.handler.ssl.util;

import io.netty.util.internal.PlatformDependent;
import java.security.SecureRandom;
import java.util.Random;

final class ThreadLocalInsecureRandom extends SecureRandom {
   private static final long serialVersionUID = -8209473337192526191L;
   private static final SecureRandom INSTANCE = new ThreadLocalInsecureRandom();

   static SecureRandom current() {
      return INSTANCE;
   }

   private ThreadLocalInsecureRandom() {
   }

   @Override
   public String getAlgorithm() {
      return "insecure";
   }

   @Override
   public void setSeed(byte[] var1) {
   }

   @Override
   public void setSeed(long var1) {
   }

   @Override
   public void nextBytes(byte[] var1) {
      random().nextBytes(var1);
   }

   @Override
   public byte[] generateSeed(int var1) {
      byte[] var2 = new byte[var1];
      random().nextBytes(var2);
      return var2;
   }

   @Override
   public int nextInt() {
      return random().nextInt();
   }

   @Override
   public int nextInt(int var1) {
      return random().nextInt(var1);
   }

   @Override
   public boolean nextBoolean() {
      return random().nextBoolean();
   }

   @Override
   public long nextLong() {
      return random().nextLong();
   }

   @Override
   public float nextFloat() {
      return random().nextFloat();
   }

   @Override
   public double nextDouble() {
      return random().nextDouble();
   }

   @Override
   public double nextGaussian() {
      return random().nextGaussian();
   }

   private static Random random() {
      return PlatformDependent.threadLocalRandom();
   }
}
