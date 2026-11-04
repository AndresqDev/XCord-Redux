package com.google.common.net;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Ascii;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.Immutable;
import com.google.thirdparty.publicsuffix.PublicSuffixPatterns;
import com.google.thirdparty.publicsuffix.PublicSuffixType;
import java.util.List;

@Immutable
@ElementTypesAreNonnullByDefault
@Beta
@GwtCompatible(
   emulated = true
)
public final class InternetDomainName {
   private static final CharMatcher DOTS_MATCHER = CharMatcher.anyOf(".。．｡");
   private static final Splitter DOT_SPLITTER = Splitter.on('.');
   private static final Joiner DOT_JOINER = Joiner.on('.');
   private static final int NO_SUFFIX_FOUND = -1;
   private static final int MAX_PARTS = 127;
   private static final int MAX_LENGTH = 253;
   private static final int MAX_DOMAIN_PART_LENGTH = 63;
   private final String name;
   private final ImmutableList<String> parts;
   private final int publicSuffixIndex;
   private final int registrySuffixIndex;
   private static final CharMatcher DASH_MATCHER = CharMatcher.anyOf("-_");
   private static final CharMatcher DIGIT_MATCHER = CharMatcher.inRange('0', '9');
   private static final CharMatcher LETTER_MATCHER = CharMatcher.inRange('a', 'z').or(CharMatcher.inRange('A', 'Z'));
   private static final CharMatcher PART_CHAR_MATCHER = DIGIT_MATCHER.or(LETTER_MATCHER).or(DASH_MATCHER);

   InternetDomainName(String var1) {
      var1 = Ascii.toLowerCase(DOTS_MATCHER.replaceFrom(var1, '.'));
      if (var1.endsWith(".")) {
         var1 = var1.substring(0, var1.length() - 1);
      }

      Preconditions.checkArgument(var1.length() <= 253, "Domain name too long: '%s':", var1);
      this.name = var1;
      this.parts = ImmutableList.copyOf(DOT_SPLITTER.split(var1));
      Preconditions.checkArgument(this.parts.size() <= 127, "Domain has too many parts: '%s'", var1);
      Preconditions.checkArgument(validateSyntax(this.parts), "Not a valid domain name: '%s'", var1);
      this.publicSuffixIndex = this.findSuffixOfType(Optional.absent());
      this.registrySuffixIndex = this.findSuffixOfType(Optional.of(PublicSuffixType.REGISTRY));
   }

   private int findSuffixOfType(Optional<PublicSuffixType> var1) {
      int var2 = this.parts.size();

      for (int var3 = 0; var3 < var2; var3++) {
         String var4 = DOT_JOINER.join(this.parts.subList(var3, var2));
         if (matchesType(var1, Optional.fromNullable(PublicSuffixPatterns.EXACT.get(var4)))) {
            return var3;
         }

         if (PublicSuffixPatterns.EXCLUDED.containsKey(var4)) {
            return var3 + 1;
         }

         if (matchesWildcardSuffixType(var1, var4)) {
            return var3;
         }
      }

      return -1;
   }

   public static InternetDomainName from(String var0) {
      return new InternetDomainName(Preconditions.checkNotNull(var0));
   }

   private static boolean validateSyntax(List<String> var0) {
      int var1 = var0.size() - 1;
      if (!validatePart((String)var0.get(var1), true)) {
         return false;
      } else {
         for (int var2 = 0; var2 < var1; var2++) {
            String var3 = (String)var0.get(var2);
            if (!validatePart(var3, false)) {
               return false;
            }
         }

         return true;
      }
   }

   private static boolean validatePart(String var0, boolean var1) {
      if (var0.length() >= 1 && var0.length() <= 63) {
         String var2 = CharMatcher.ascii().retainFrom(var0);
         if (!PART_CHAR_MATCHER.matchesAllOf(var2)) {
            return false;
         } else {
            return DASH_MATCHER.matches(var0.charAt(0)) || DASH_MATCHER.matches(var0.charAt(var0.length() - 1))
               ? false
               : !var1 || !DIGIT_MATCHER.matches(var0.charAt(0));
         }
      } else {
         return false;
      }
   }

   public ImmutableList<String> parts() {
      return this.parts;
   }

   public boolean isPublicSuffix() {
      return this.publicSuffixIndex == 0;
   }

   public boolean hasPublicSuffix() {
      return this.publicSuffixIndex != -1;
   }

   public InternetDomainName publicSuffix() {
      return this.hasPublicSuffix() ? this.ancestor(this.publicSuffixIndex) : null;
   }

   public boolean isUnderPublicSuffix() {
      return this.publicSuffixIndex > 0;
   }

   public boolean isTopPrivateDomain() {
      return this.publicSuffixIndex == 1;
   }

   public InternetDomainName topPrivateDomain() {
      if (this.isTopPrivateDomain()) {
         return this;
      } else {
         Preconditions.checkState(this.isUnderPublicSuffix(), "Not under a public suffix: %s", this.name);
         return this.ancestor(this.publicSuffixIndex - 1);
      }
   }

   public boolean isRegistrySuffix() {
      return this.registrySuffixIndex == 0;
   }

   public boolean hasRegistrySuffix() {
      return this.registrySuffixIndex != -1;
   }

   public InternetDomainName registrySuffix() {
      return this.hasRegistrySuffix() ? this.ancestor(this.registrySuffixIndex) : null;
   }

   public boolean isUnderRegistrySuffix() {
      return this.registrySuffixIndex > 0;
   }

   public boolean isTopDomainUnderRegistrySuffix() {
      return this.registrySuffixIndex == 1;
   }

   public InternetDomainName topDomainUnderRegistrySuffix() {
      if (this.isTopDomainUnderRegistrySuffix()) {
         return this;
      } else {
         Preconditions.checkState(this.isUnderRegistrySuffix(), "Not under a registry suffix: %s", this.name);
         return this.ancestor(this.registrySuffixIndex - 1);
      }
   }

   public boolean hasParent() {
      return this.parts.size() > 1;
   }

   public InternetDomainName parent() {
      Preconditions.checkState(this.hasParent(), "Domain '%s' has no parent", this.name);
      return this.ancestor(1);
   }

   private InternetDomainName ancestor(int var1) {
      return from(DOT_JOINER.join(this.parts.subList(var1, this.parts.size())));
   }

   public InternetDomainName child(String var1) {
      String var2 = Preconditions.checkNotNull(var1);
      String var3 = this.name;
      return from(new StringBuilder(1 + String.valueOf(var2).length() + String.valueOf(var3).length()).append(var2).append(".").append(var3).toString());
   }

   public static boolean isValid(String var0) {
      try {
         from(var0);
         return true;
      } catch (IllegalArgumentException var2) {
         return false;
      }
   }

   private static boolean matchesWildcardSuffixType(Optional<PublicSuffixType> var0, String var1) {
      List var2 = DOT_SPLITTER.limit(2).splitToList(var1);
      return var2.size() == 2 && matchesType(var0, Optional.fromNullable(PublicSuffixPatterns.UNDER.get(var2.get(1))));
   }

   private static boolean matchesType(Optional<PublicSuffixType> var0, Optional<PublicSuffixType> var1) {
      return var0.isPresent() ? var0.equals(var1) : var1.isPresent();
   }

   @Override
   public String toString() {
      return this.name;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof InternetDomainName) {
         InternetDomainName var2 = (InternetDomainName)var1;
         return this.name.equals(var2.name);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.name.hashCode();
   }
}
