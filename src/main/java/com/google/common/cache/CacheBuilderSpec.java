package com.google.common.cache;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.qual.Nullable;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public final class CacheBuilderSpec {
   private static final Splitter KEYS_SPLITTER = Splitter.on(',').trimResults();
   private static final Splitter KEY_VALUE_SPLITTER = Splitter.on('=').trimResults();
   private static final ImmutableMap<String, CacheBuilderSpec.ValueParser> VALUE_PARSERS = ImmutableMap.<String, CacheBuilderSpec.ValueParser>builder()
      .put("initialCapacity", new CacheBuilderSpec.InitialCapacityParser())
      .put("maximumSize", new CacheBuilderSpec.MaximumSizeParser())
      .put("maximumWeight", new CacheBuilderSpec.MaximumWeightParser())
      .put("concurrencyLevel", new CacheBuilderSpec.ConcurrencyLevelParser())
      .put("weakKeys", new CacheBuilderSpec.KeyStrengthParser(LocalCache.Strength.WEAK))
      .put("softValues", new CacheBuilderSpec.ValueStrengthParser(LocalCache.Strength.SOFT))
      .put("weakValues", new CacheBuilderSpec.ValueStrengthParser(LocalCache.Strength.WEAK))
      .put("recordStats", new CacheBuilderSpec.RecordStatsParser())
      .put("expireAfterAccess", new CacheBuilderSpec.AccessDurationParser())
      .put("expireAfterWrite", new CacheBuilderSpec.WriteDurationParser())
      .put("refreshAfterWrite", new CacheBuilderSpec.RefreshDurationParser())
      .put("refreshInterval", new CacheBuilderSpec.RefreshDurationParser())
      .build();
   @VisibleForTesting
   Integer initialCapacity;
   @VisibleForTesting
   Long maximumSize;
   @VisibleForTesting
   Long maximumWeight;
   @VisibleForTesting
   Integer concurrencyLevel;
   @VisibleForTesting
   LocalCache.Strength keyStrength;
   @VisibleForTesting
   LocalCache.Strength valueStrength;
   @VisibleForTesting
   Boolean recordStats;
   @VisibleForTesting
   long writeExpirationDuration;
   @VisibleForTesting
   TimeUnit writeExpirationTimeUnit;
   @VisibleForTesting
   long accessExpirationDuration;
   @VisibleForTesting
   TimeUnit accessExpirationTimeUnit;
   @VisibleForTesting
   long refreshDuration;
   @VisibleForTesting
   TimeUnit refreshTimeUnit;
   private final String specification;

   private CacheBuilderSpec(String var1) {
      this.specification = var1;
   }

   public static CacheBuilderSpec parse(String var0) {
      CacheBuilderSpec var1 = new CacheBuilderSpec(var0);
      if (!var0.isEmpty()) {
         for (String var3 : KEYS_SPLITTER.split(var0)) {
            ImmutableList var4 = ImmutableList.copyOf(KEY_VALUE_SPLITTER.split(var3));
            Preconditions.checkArgument(!var4.isEmpty(), "blank key-value pair");
            Preconditions.checkArgument(var4.size() <= 2, "key-value pair %s with more than one equals sign", var3);
            String var5 = (String)var4.get(0);
            CacheBuilderSpec.ValueParser var6 = VALUE_PARSERS.get(var5);
            Preconditions.checkArgument(var6 != null, "unknown key %s", var5);
            String var7 = var4.size() == 1 ? null : (String)var4.get(1);
            var6.parse(var1, var5, var7);
         }
      }

      return var1;
   }

   public static CacheBuilderSpec disableCaching() {
      return parse("maximumSize=0");
   }

   CacheBuilder<Object, Object> toCacheBuilder() {
      CacheBuilder var1 = CacheBuilder.newBuilder();
      if (this.initialCapacity != null) {
         var1.initialCapacity(this.initialCapacity);
      }

      if (this.maximumSize != null) {
         var1.maximumSize(this.maximumSize);
      }

      if (this.maximumWeight != null) {
         var1.maximumWeight(this.maximumWeight);
      }

      if (this.concurrencyLevel != null) {
         var1.concurrencyLevel(this.concurrencyLevel);
      }

      if (this.keyStrength != null) {
         switch (this.keyStrength) {
            case WEAK:
               var1.weakKeys();
               break;
            default:
               throw new AssertionError();
         }
      }

      if (this.valueStrength != null) {
         switch (this.valueStrength) {
            case WEAK:
               var1.weakValues();
               break;
            case SOFT:
               var1.softValues();
               break;
            default:
               throw new AssertionError();
         }
      }

      if (this.recordStats != null && this.recordStats) {
         var1.recordStats();
      }

      if (this.writeExpirationTimeUnit != null) {
         var1.expireAfterWrite(this.writeExpirationDuration, this.writeExpirationTimeUnit);
      }

      if (this.accessExpirationTimeUnit != null) {
         var1.expireAfterAccess(this.accessExpirationDuration, this.accessExpirationTimeUnit);
      }

      if (this.refreshTimeUnit != null) {
         var1.refreshAfterWrite(this.refreshDuration, this.refreshTimeUnit);
      }

      return var1;
   }

   public String toParsableString() {
      return this.specification;
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this).addValue(this.toParsableString()).toString();
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(
         this.initialCapacity,
         this.maximumSize,
         this.maximumWeight,
         this.concurrencyLevel,
         this.keyStrength,
         this.valueStrength,
         this.recordStats,
         durationInNanos(this.writeExpirationDuration, this.writeExpirationTimeUnit),
         durationInNanos(this.accessExpirationDuration, this.accessExpirationTimeUnit),
         durationInNanos(this.refreshDuration, this.refreshTimeUnit)
      );
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof CacheBuilderSpec)) {
         return false;
      } else {
         CacheBuilderSpec var2 = (CacheBuilderSpec)var1;
         return Objects.equal(this.initialCapacity, var2.initialCapacity)
            && Objects.equal(this.maximumSize, var2.maximumSize)
            && Objects.equal(this.maximumWeight, var2.maximumWeight)
            && Objects.equal(this.concurrencyLevel, var2.concurrencyLevel)
            && Objects.equal(this.keyStrength, var2.keyStrength)
            && Objects.equal(this.valueStrength, var2.valueStrength)
            && Objects.equal(this.recordStats, var2.recordStats)
            && Objects.equal(
               durationInNanos(this.writeExpirationDuration, this.writeExpirationTimeUnit),
               durationInNanos(var2.writeExpirationDuration, var2.writeExpirationTimeUnit)
            )
            && Objects.equal(
               durationInNanos(this.accessExpirationDuration, this.accessExpirationTimeUnit),
               durationInNanos(var2.accessExpirationDuration, var2.accessExpirationTimeUnit)
            )
            && Objects.equal(durationInNanos(this.refreshDuration, this.refreshTimeUnit), durationInNanos(var2.refreshDuration, var2.refreshTimeUnit));
      }
   }

   private static Long durationInNanos(long var0, TimeUnit var2) {
      return var2 == null ? null : var2.toNanos(var0);
   }

   private static String format(String var0, Object... var1) {
      return String.format(Locale.ROOT, var0, var1);
   }

   static class AccessDurationParser extends CacheBuilderSpec.DurationParser {
      @Override
      protected void parseDuration(CacheBuilderSpec var1, long var2, TimeUnit var4) {
         Preconditions.checkArgument(var1.accessExpirationTimeUnit == null, "expireAfterAccess already set");
         var1.accessExpirationDuration = var2;
         var1.accessExpirationTimeUnit = var4;
      }
   }

   static class ConcurrencyLevelParser extends CacheBuilderSpec.IntegerParser {
      @Override
      protected void parseInteger(CacheBuilderSpec var1, int var2) {
         Preconditions.checkArgument(var1.concurrencyLevel == null, "concurrency level was already set to ", var1.concurrencyLevel);
         var1.concurrencyLevel = var2;
      }
   }

   abstract static class DurationParser implements CacheBuilderSpec.ValueParser {
      protected abstract void parseDuration(CacheBuilderSpec var1, long var2, TimeUnit var4);

      @Override
      public void parse(CacheBuilderSpec var1, String var2, String var3) {
         if (Strings.isNullOrEmpty(var3)) {
            throw new IllegalArgumentException(
               new StringBuilder(21 + String.valueOf(var2).length()).append("value of key ").append(var2).append(" omitted").toString()
            );
         } else {
            try {
               char var4 = var3.charAt(var3.length() - 1);
               TimeUnit var5;
               switch (var4) {
                  case 'd':
                     var5 = TimeUnit.DAYS;
                     break;
                  case 'h':
                     var5 = TimeUnit.HOURS;
                     break;
                  case 'm':
                     var5 = TimeUnit.MINUTES;
                     break;
                  case 's':
                     var5 = TimeUnit.SECONDS;
                     break;
                  default:
                     throw new IllegalArgumentException(CacheBuilderSpec.format("key %s invalid unit: was %s, must end with one of [dhms]", var2, var3));
               }

               long var6 = Long.parseLong(var3.substring(0, var3.length() - 1));
               this.parseDuration(var1, var6, var5);
            } catch (NumberFormatException var8) {
               throw new IllegalArgumentException(CacheBuilderSpec.format("key %s value set to %s, must be integer", var2, var3));
            }
         }
      }
   }

   static class InitialCapacityParser extends CacheBuilderSpec.IntegerParser {
      @Override
      protected void parseInteger(CacheBuilderSpec var1, int var2) {
         Preconditions.checkArgument(var1.initialCapacity == null, "initial capacity was already set to ", var1.initialCapacity);
         var1.initialCapacity = var2;
      }
   }

   abstract static class IntegerParser implements CacheBuilderSpec.ValueParser {
      protected abstract void parseInteger(CacheBuilderSpec var1, int var2);

      @Override
      public void parse(CacheBuilderSpec var1, String var2, @Nullable String var3) {
         if (Strings.isNullOrEmpty(var3)) {
            throw new IllegalArgumentException(
               new StringBuilder(21 + String.valueOf(var2).length()).append("value of key ").append(var2).append(" omitted").toString()
            );
         } else {
            try {
               this.parseInteger(var1, Integer.parseInt(var3));
            } catch (NumberFormatException var5) {
               throw new IllegalArgumentException(CacheBuilderSpec.format("key %s value set to %s, must be integer", var2, var3), var5);
            }
         }
      }
   }

   static class KeyStrengthParser implements CacheBuilderSpec.ValueParser {
      private final LocalCache.Strength strength;

      public KeyStrengthParser(LocalCache.Strength var1) {
         this.strength = var1;
      }

      @Override
      public void parse(CacheBuilderSpec var1, String var2, String var3) {
         Preconditions.checkArgument(var3 == null, "key %s does not take values", var2);
         Preconditions.checkArgument(var1.keyStrength == null, "%s was already set to %s", var2, var1.keyStrength);
         var1.keyStrength = this.strength;
      }
   }

   abstract static class LongParser implements CacheBuilderSpec.ValueParser {
      protected abstract void parseLong(CacheBuilderSpec var1, long var2);

      @Override
      public void parse(CacheBuilderSpec var1, String var2, @Nullable String var3) {
         if (Strings.isNullOrEmpty(var3)) {
            throw new IllegalArgumentException(
               new StringBuilder(21 + String.valueOf(var2).length()).append("value of key ").append(var2).append(" omitted").toString()
            );
         } else {
            try {
               this.parseLong(var1, Long.parseLong(var3));
            } catch (NumberFormatException var5) {
               throw new IllegalArgumentException(CacheBuilderSpec.format("key %s value set to %s, must be integer", var2, var3), var5);
            }
         }
      }
   }

   static class MaximumSizeParser extends CacheBuilderSpec.LongParser {
      @Override
      protected void parseLong(CacheBuilderSpec var1, long var2) {
         Preconditions.checkArgument(var1.maximumSize == null, "maximum size was already set to ", var1.maximumSize);
         Preconditions.checkArgument(var1.maximumWeight == null, "maximum weight was already set to ", var1.maximumWeight);
         var1.maximumSize = var2;
      }
   }

   static class MaximumWeightParser extends CacheBuilderSpec.LongParser {
      @Override
      protected void parseLong(CacheBuilderSpec var1, long var2) {
         Preconditions.checkArgument(var1.maximumWeight == null, "maximum weight was already set to ", var1.maximumWeight);
         Preconditions.checkArgument(var1.maximumSize == null, "maximum size was already set to ", var1.maximumSize);
         var1.maximumWeight = var2;
      }
   }

   static class RecordStatsParser implements CacheBuilderSpec.ValueParser {
      @Override
      public void parse(CacheBuilderSpec var1, String var2, String var3) {
         Preconditions.checkArgument(var3 == null, "recordStats does not take values");
         Preconditions.checkArgument(var1.recordStats == null, "recordStats already set");
         var1.recordStats = true;
      }
   }

   static class RefreshDurationParser extends CacheBuilderSpec.DurationParser {
      @Override
      protected void parseDuration(CacheBuilderSpec var1, long var2, TimeUnit var4) {
         Preconditions.checkArgument(var1.refreshTimeUnit == null, "refreshAfterWrite already set");
         var1.refreshDuration = var2;
         var1.refreshTimeUnit = var4;
      }
   }

   private interface ValueParser {
      void parse(CacheBuilderSpec var1, String var2, String var3);
   }

   static class ValueStrengthParser implements CacheBuilderSpec.ValueParser {
      private final LocalCache.Strength strength;

      public ValueStrengthParser(LocalCache.Strength var1) {
         this.strength = var1;
      }

      @Override
      public void parse(CacheBuilderSpec var1, String var2, String var3) {
         Preconditions.checkArgument(var3 == null, "key %s does not take values", var2);
         Preconditions.checkArgument(var1.valueStrength == null, "%s was already set to %s", var2, var1.valueStrength);
         var1.valueStrength = this.strength;
      }
   }

   static class WriteDurationParser extends CacheBuilderSpec.DurationParser {
      @Override
      protected void parseDuration(CacheBuilderSpec var1, long var2, TimeUnit var4) {
         Preconditions.checkArgument(var1.writeExpirationTimeUnit == null, "expireAfterWrite already set");
         var1.writeExpirationDuration = var2;
         var1.writeExpirationTimeUnit = var4;
      }
   }
}
