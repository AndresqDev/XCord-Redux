package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
public final class Splitter {
   private final CharMatcher trimmer;
   private final boolean omitEmptyStrings;
   private final Splitter.Strategy strategy;
   private final int limit;

   private Splitter(Splitter.Strategy var1) {
      this(var1, false, CharMatcher.none(), Integer.MAX_VALUE);
   }

   private Splitter(Splitter.Strategy var1, boolean var2, CharMatcher var3, int var4) {
      this.strategy = var1;
      this.omitEmptyStrings = var2;
      this.trimmer = var3;
      this.limit = var4;
   }

   public static Splitter on(char var0) {
      return on(CharMatcher.is(var0));
   }

   public static Splitter on(final CharMatcher var0) {
      Preconditions.checkNotNull(var0);
      return new Splitter(new Splitter.Strategy() {
         public Splitter.SplittingIterator iterator(Splitter var1, CharSequence var2) {
            return new Splitter.SplittingIterator(var1, var2) {
               @Override
               int separatorStart(int var1) {
                  return var0.indexIn(this.toSplit, var1);
               }

               @Override
               int separatorEnd(int var1) {
                  return var1 + 1;
               }
            };
         }
      });
   }

   public static Splitter on(final String var0) {
      Preconditions.checkArgument(var0.length() != 0, "The separator may not be the empty string.");
      return var0.length() == 1 ? on(var0.charAt(0)) : new Splitter(new Splitter.Strategy() {
         public Splitter.SplittingIterator iterator(Splitter var1, CharSequence var2) {
            return new Splitter.SplittingIterator(var1, var2) {
               @Override
               public int separatorStart(int var1) {
                  int var2 = var0.length();
                  int var3 = var1;

                  label24:
                  for (int var4 = this.toSplit.length() - var2; var3 <= var4; var3++) {
                     for (int var5 = 0; var5 < var2; var5++) {
                        if (this.toSplit.charAt(var5 + var3) != var0.charAt(var5)) {
                           continue label24;
                        }
                     }

                     return var3;
                  }

                  return -1;
               }

               @Override
               public int separatorEnd(int var1) {
                  return var1 + var0.length();
               }
            };
         }
      });
   }

   @GwtIncompatible
   public static Splitter on(Pattern var0) {
      return on(new JdkPattern(var0));
   }

   private static Splitter on(final CommonPattern var0) {
      Preconditions.checkArgument(!var0.matcher("").matches(), "The pattern may not match the empty string: %s", var0);
      return new Splitter(new Splitter.Strategy() {
         public Splitter.SplittingIterator iterator(Splitter var1, CharSequence var2) {
            final CommonMatcher var3 = var0.matcher(var2);
            return new Splitter.SplittingIterator(this, var1, var2) {
               @Override
               public int separatorStart(int var1) {
                  return var3.find(var1) ? var3.start() : -1;
               }

               @Override
               public int separatorEnd(int var1) {
                  return var3.end();
               }
            };
         }
      });
   }

   @GwtIncompatible
   public static Splitter onPattern(String var0) {
      return on(Platform.compilePattern(var0));
   }

   public static Splitter fixedLength(final int var0) {
      Preconditions.checkArgument(var0 > 0, "The length may not be less than 1");
      return new Splitter(new Splitter.Strategy() {
         public Splitter.SplittingIterator iterator(Splitter var1, CharSequence var2) {
            return new Splitter.SplittingIterator(var1, var2) {
               @Override
               public int separatorStart(int var1) {
                  int var2 = var1 + var0;
                  return var2 < this.toSplit.length() ? var2 : -1;
               }

               @Override
               public int separatorEnd(int var1) {
                  return var1;
               }
            };
         }
      });
   }

   public Splitter omitEmptyStrings() {
      return new Splitter(this.strategy, true, this.trimmer, this.limit);
   }

   public Splitter limit(int var1) {
      Preconditions.checkArgument(var1 > 0, "must be greater than zero: %s", var1);
      return new Splitter(this.strategy, this.omitEmptyStrings, this.trimmer, var1);
   }

   public Splitter trimResults() {
      return this.trimResults(CharMatcher.whitespace());
   }

   public Splitter trimResults(CharMatcher var1) {
      Preconditions.checkNotNull(var1);
      return new Splitter(this.strategy, this.omitEmptyStrings, var1, this.limit);
   }

   public Iterable<String> split(final CharSequence var1) {
      Preconditions.checkNotNull(var1);
      return new Iterable<String>() {
         @Override
         public Iterator<String> iterator() {
            return Splitter.this.splittingIterator(var1);
         }

         @Override
         public String toString() {
            return Joiner.on(", ").appendTo(new StringBuilder().append('['), this).append(']').toString();
         }
      };
   }

   private Iterator<String> splittingIterator(CharSequence var1) {
      return this.strategy.iterator(this, var1);
   }

   public List<String> splitToList(CharSequence var1) {
      Preconditions.checkNotNull(var1);
      Iterator var2 = this.splittingIterator(var1);
      ArrayList var3 = new ArrayList();

      while (var2.hasNext()) {
         var3.add((String)var2.next());
      }

      return Collections.unmodifiableList(var3);
   }

   @Beta
   public Stream<String> splitToStream(CharSequence var1) {
      return StreamSupport.stream(this.split(var1).spliterator(), false);
   }

   @Beta
   public Splitter.MapSplitter withKeyValueSeparator(String var1) {
      return this.withKeyValueSeparator(on(var1));
   }

   @Beta
   public Splitter.MapSplitter withKeyValueSeparator(char var1) {
      return this.withKeyValueSeparator(on(var1));
   }

   @Beta
   public Splitter.MapSplitter withKeyValueSeparator(Splitter var1) {
      return new Splitter.MapSplitter(this, var1);
   }

   @Beta
   public static final class MapSplitter {
      private static final String INVALID_ENTRY_MESSAGE = "Chunk [%s] is not a valid entry";
      private final Splitter outerSplitter;
      private final Splitter entrySplitter;

      private MapSplitter(Splitter var1, Splitter var2) {
         this.outerSplitter = var1;
         this.entrySplitter = Preconditions.checkNotNull(var2);
      }

      public Map<String, String> split(CharSequence var1) {
         LinkedHashMap var2 = new LinkedHashMap();

         for (String var4 : this.outerSplitter.split(var1)) {
            Iterator var5 = this.entrySplitter.splittingIterator(var4);
            Preconditions.checkArgument(var5.hasNext(), "Chunk [%s] is not a valid entry", var4);
            String var6 = (String)var5.next();
            Preconditions.checkArgument(!var2.containsKey(var6), "Duplicate key [%s] found.", var6);
            Preconditions.checkArgument(var5.hasNext(), "Chunk [%s] is not a valid entry", var4);
            String var7 = (String)var5.next();
            var2.put(var6, var7);
            Preconditions.checkArgument(!var5.hasNext(), "Chunk [%s] is not a valid entry", var4);
         }

         return Collections.unmodifiableMap(var2);
      }
   }

   private abstract static class SplittingIterator extends AbstractIterator<String> {
      final CharSequence toSplit;
      final CharMatcher trimmer;
      final boolean omitEmptyStrings;
      int offset = 0;
      int limit;

      abstract int separatorStart(int var1);

      abstract int separatorEnd(int var1);

      protected SplittingIterator(Splitter var1, CharSequence var2) {
         this.trimmer = var1.trimmer;
         this.omitEmptyStrings = var1.omitEmptyStrings;
         this.limit = var1.limit;
         this.toSplit = var2;
      }

      protected String computeNext() {
         int var1 = this.offset;

         while (this.offset != -1) {
            int var2 = var1;
            int var4 = this.separatorStart(this.offset);
            int var3;
            if (var4 == -1) {
               var3 = this.toSplit.length();
               this.offset = -1;
            } else {
               var3 = var4;
               this.offset = this.separatorEnd(var4);
            }

            if (this.offset != var1) {
               while (var2 < var3 && this.trimmer.matches(this.toSplit.charAt(var2))) {
                  var2++;
               }

               while (var3 > var2 && this.trimmer.matches(this.toSplit.charAt(var3 - 1))) {
                  var3--;
               }

               if (!this.omitEmptyStrings || var2 != var3) {
                  if (this.limit == 1) {
                     var3 = this.toSplit.length();
                     this.offset = -1;

                     while (var3 > var2 && this.trimmer.matches(this.toSplit.charAt(var3 - 1))) {
                        var3--;
                     }
                  } else {
                     this.limit--;
                  }

                  return this.toSplit.subSequence(var2, var3).toString();
               }

               var1 = this.offset;
            } else {
               this.offset++;
               if (this.offset > this.toSplit.length()) {
                  this.offset = -1;
               }
            }
         }

         return this.endOfData();
      }
   }

   private interface Strategy {
      Iterator<String> iterator(Splitter var1, CharSequence var2);
   }
}
