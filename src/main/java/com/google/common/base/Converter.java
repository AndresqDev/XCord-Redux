package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.ForOverride;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.google.j2objc.annotations.RetainedWith;
import java.io.Serializable;
import java.util.Iterator;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class Converter<A, B> implements Function<A, B> {
   private final boolean handleNullAutomatically;
   @LazyInit
   @RetainedWith
   private transient Converter<B, A> reverse;

   protected Converter() {
      this(true);
   }

   Converter(boolean var1) {
      this.handleNullAutomatically = var1;
   }

   @ForOverride
   protected abstract B doForward(A var1);

   @ForOverride
   protected abstract A doBackward(B var1);

   @CanIgnoreReturnValue
   public final B convert(A var1) {
      return this.correctedDoForward((A)var1);
   }

   B correctedDoForward(A var1) {
      if (this.handleNullAutomatically) {
         return var1 == null ? null : Preconditions.checkNotNull(this.doForward((A)var1));
      } else {
         return this.unsafeDoForward((A)var1);
      }
   }

   A correctedDoBackward(B var1) {
      if (this.handleNullAutomatically) {
         return var1 == null ? null : Preconditions.checkNotNull(this.doBackward((B)var1));
      } else {
         return this.unsafeDoBackward((B)var1);
      }
   }

   private B unsafeDoForward(A var1) {
      return this.doForward(NullnessCasts.uncheckedCastNullableTToT((A)var1));
   }

   private A unsafeDoBackward(B var1) {
      return this.doBackward(NullnessCasts.uncheckedCastNullableTToT((B)var1));
   }

   @CanIgnoreReturnValue
   public Iterable<B> convertAll(final Iterable<? extends A> var1) {
      Preconditions.checkNotNull(var1, "fromIterable");
      return new Iterable<B>() {
         @Override
         public Iterator<B> iterator() {
            return new Iterator<B>() {
               private final Iterator<? extends A> fromIterator = var1.iterator();

               @Override
               public boolean hasNext() {
                  return this.fromIterator.hasNext();
               }

               @Override
               public B next() {
                  return (B)Converter.this.convert((A)this.fromIterator.next());
               }

               @Override
               public void remove() {
                  this.fromIterator.remove();
               }
            };
         }
      };
   }

   @CanIgnoreReturnValue
   public Converter<B, A> reverse() {
      Converter var1 = this.reverse;
      return var1 == null ? (this.reverse = (Converter<B, A>)(new Converter.ReverseConverter<>(this))) : var1;
   }

   public final <C> Converter<A, C> andThen(Converter<B, C> var1) {
      return this.doAndThen(var1);
   }

   <C> Converter<A, C> doAndThen(Converter<B, C> var1) {
      return new Converter.ConverterComposition<>(this, Preconditions.checkNotNull(var1));
   }

   @Deprecated
   @CanIgnoreReturnValue
   @Override
   public final B apply(A var1) {
      return this.convert((A)var1);
   }

   @Override
   public boolean equals(Object var1) {
      return super.equals(var1);
   }

   public static <A, B> Converter<A, B> from(Function<? super A, ? extends B> var0, Function<? super B, ? extends A> var1) {
      return new Converter.FunctionBasedConverter<>(var0, var1);
   }

   public static <T> Converter<T, T> identity() {
      return Converter.IdentityConverter.INSTANCE;
   }

   private static final class ConverterComposition<A, B, C> extends Converter<A, C> implements Serializable {
      final Converter<A, B> first;
      final Converter<B, C> second;
      private static final long serialVersionUID = 0L;

      ConverterComposition(Converter<A, B> var1, Converter<B, C> var2) {
         this.first = var1;
         this.second = var2;
      }

      @Override
      protected C doForward(A var1) {
         throw new AssertionError();
      }

      @Override
      protected A doBackward(C var1) {
         throw new AssertionError();
      }

      @Override
      C correctedDoForward(A var1) {
         return this.second.correctedDoForward(this.first.correctedDoForward((A)var1));
      }

      @Override
      A correctedDoBackward(C var1) {
         return this.first.correctedDoBackward(this.second.correctedDoBackward((C)var1));
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof Converter.ConverterComposition)) {
            return false;
         } else {
            Converter.ConverterComposition var2 = (Converter.ConverterComposition)var1;
            return this.first.equals(var2.first) && this.second.equals(var2.second);
         }
      }

      @Override
      public int hashCode() {
         return 31 * this.first.hashCode() + this.second.hashCode();
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.first);
         String var2 = String.valueOf(this.second);
         return new StringBuilder(10 + String.valueOf(var1).length() + String.valueOf(var2).length())
            .append(var1)
            .append(".andThen(")
            .append(var2)
            .append(")")
            .toString();
      }
   }

   private static final class FunctionBasedConverter<A, B> extends Converter<A, B> implements Serializable {
      private final Function<? super A, ? extends B> forwardFunction;
      private final Function<? super B, ? extends A> backwardFunction;

      private FunctionBasedConverter(Function<? super A, ? extends B> var1, Function<? super B, ? extends A> var2) {
         this.forwardFunction = Preconditions.checkNotNull(var1);
         this.backwardFunction = Preconditions.checkNotNull(var2);
      }

      @Override
      protected B doForward(A var1) {
         return (B)this.forwardFunction.apply((A)var1);
      }

      @Override
      protected A doBackward(B var1) {
         return (A)this.backwardFunction.apply((B)var1);
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof Converter.FunctionBasedConverter)) {
            return false;
         } else {
            Converter.FunctionBasedConverter var2 = (Converter.FunctionBasedConverter)var1;
            return this.forwardFunction.equals(var2.forwardFunction) && this.backwardFunction.equals(var2.backwardFunction);
         }
      }

      @Override
      public int hashCode() {
         return this.forwardFunction.hashCode() * 31 + this.backwardFunction.hashCode();
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.forwardFunction);
         String var2 = String.valueOf(this.backwardFunction);
         return new StringBuilder(18 + String.valueOf(var1).length() + String.valueOf(var2).length())
            .append("Converter.from(")
            .append(var1)
            .append(", ")
            .append(var2)
            .append(")")
            .toString();
      }
   }

   private static final class IdentityConverter<T> extends Converter<T, T> implements Serializable {
      static final Converter.IdentityConverter<?> INSTANCE = new Converter.IdentityConverter();
      private static final long serialVersionUID = 0L;

      @Override
      protected T doForward(T var1) {
         return (T)var1;
      }

      @Override
      protected T doBackward(T var1) {
         return (T)var1;
      }

      public Converter.IdentityConverter<T> reverse() {
         return this;
      }

      @Override
      <S> Converter<T, S> doAndThen(Converter<T, S> var1) {
         return Preconditions.checkNotNull(var1, "otherConverter");
      }

      @Override
      public String toString() {
         return "Converter.identity()";
      }

      private Object readResolve() {
         return INSTANCE;
      }
   }

   private static final class ReverseConverter<A, B> extends Converter<B, A> implements Serializable {
      final Converter<A, B> original;
      private static final long serialVersionUID = 0L;

      ReverseConverter(Converter<A, B> var1) {
         this.original = var1;
      }

      @Override
      protected A doForward(B var1) {
         throw new AssertionError();
      }

      @Override
      protected B doBackward(A var1) {
         throw new AssertionError();
      }

      @Override
      A correctedDoForward(B var1) {
         return this.original.correctedDoBackward((B)var1);
      }

      @Override
      B correctedDoBackward(A var1) {
         return this.original.correctedDoForward((A)var1);
      }

      @Override
      public Converter<A, B> reverse() {
         return this.original;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 instanceof Converter.ReverseConverter) {
            Converter.ReverseConverter var2 = (Converter.ReverseConverter)var1;
            return this.original.equals(var2.original);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return ~this.original.hashCode();
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.original);
         return new StringBuilder(10 + String.valueOf(var1).length()).append(var1).append(".reverse()").toString();
      }
   }
}
