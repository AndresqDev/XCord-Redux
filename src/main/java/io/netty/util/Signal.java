package io.netty.util;

public final class Signal extends Error implements Constant<Signal> {
   private static final long serialVersionUID = -221145131122459977L;
   private static final ConstantPool<Signal> pool = new ConstantPool<Signal>() {
      protected Signal newConstant(int var1, String var2) {
         return new Signal(var1, var2);
      }
   };
   private final Signal.SignalConstant constant;

   public static Signal valueOf(String var0) {
      return pool.valueOf(var0);
   }

   public static Signal valueOf(Class<?> var0, String var1) {
      return pool.valueOf(var0, var1);
   }

   private Signal(int var1, String var2) {
      this.constant = new Signal.SignalConstant(var1, var2);
   }

   public void expect(Signal var1) {
      if (this != var1) {
         throw new IllegalStateException("unexpected signal: " + var1);
      }
   }

   @Override
   public Throwable initCause(Throwable var1) {
      return this;
   }

   @Override
   public Throwable fillInStackTrace() {
      return this;
   }

   @Override
   public int id() {
      return this.constant.id();
   }

   @Override
   public String name() {
      return this.constant.name();
   }

   @Override
   public boolean equals(Object var1) {
      return this == var1;
   }

   @Override
   public int hashCode() {
      return System.identityHashCode(this);
   }

   public int compareTo(Signal var1) {
      return this == var1 ? 0 : this.constant.compareTo(var1.constant);
   }

   @Override
   public String toString() {
      return this.name();
   }

   private static final class SignalConstant extends AbstractConstant<Signal.SignalConstant> {
      SignalConstant(int var1, String var2) {
         super(var1, var2);
      }
   }
}
