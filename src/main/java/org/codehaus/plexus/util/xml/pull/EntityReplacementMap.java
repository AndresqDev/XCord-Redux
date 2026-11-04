package org.codehaus.plexus.util.xml.pull;

public class EntityReplacementMap {
   final String[] entityName;
   final char[][] entityNameBuf;
   final String[] entityReplacement;
   final char[][] entityReplacementBuf;
   int entityEnd;
   final int[] entityNameHash;
   public static final EntityReplacementMap defaultEntityReplacementMap = new EntityReplacementMap(
      new String[][]{
         {"nbsp", " "},
         {"iexcl", "¡"},
         {"cent", "¢"},
         {"pound", "£"},
         {"curren", "¤"},
         {"yen", "¥"},
         {"brvbar", "¦"},
         {"sect", "§"},
         {"uml", "¨"},
         {"copy", "©"},
         {"ordf", "ª"},
         {"laquo", "«"},
         {"not", "¬"},
         {"shy", "\u00ad"},
         {"reg", "®"},
         {"macr", "¯"},
         {"deg", "°"},
         {"plusmn", "±"},
         {"sup2", "²"},
         {"sup3", "³"},
         {"acute", "´"},
         {"micro", "µ"},
         {"para", "¶"},
         {"middot", "·"},
         {"cedil", "¸"},
         {"sup1", "¹"},
         {"ordm", "º"},
         {"raquo", "»"},
         {"frac14", "¼"},
         {"frac12", "½"},
         {"frac34", "¾"},
         {"iquest", "¿"},
         {"Agrave", "À"},
         {"Aacute", "Á"},
         {"Acirc", "Â"},
         {"Atilde", "Ã"},
         {"Auml", "Ä"},
         {"Aring", "Å"},
         {"AElig", "Æ"},
         {"Ccedil", "Ç"},
         {"Egrave", "È"},
         {"Eacute", "É"},
         {"Ecirc", "Ê"},
         {"Euml", "Ë"},
         {"Igrave", "Ì"},
         {"Iacute", "Í"},
         {"Icirc", "Î"},
         {"Iuml", "Ï"},
         {"ETH", "Ð"},
         {"Ntilde", "Ñ"},
         {"Ograve", "Ò"},
         {"Oacute", "Ó"},
         {"Ocirc", "Ô"},
         {"Otilde", "Õ"},
         {"Ouml", "Ö"},
         {"times", "×"},
         {"Oslash", "Ø"},
         {"Ugrave", "Ù"},
         {"Uacute", "Ú"},
         {"Ucirc", "Û"},
         {"Uuml", "Ü"},
         {"Yacute", "Ý"},
         {"THORN", "Þ"},
         {"szlig", "ß"},
         {"agrave", "à"},
         {"aacute", "á"},
         {"acirc", "â"},
         {"atilde", "ã"},
         {"auml", "ä"},
         {"aring", "å"},
         {"aelig", "æ"},
         {"ccedil", "ç"},
         {"egrave", "è"},
         {"eacute", "é"},
         {"ecirc", "ê"},
         {"euml", "ë"},
         {"igrave", "ì"},
         {"iacute", "í"},
         {"icirc", "î"},
         {"iuml", "ï"},
         {"eth", "ð"},
         {"ntilde", "ñ"},
         {"ograve", "ò"},
         {"oacute", "ó"},
         {"ocirc", "ô"},
         {"otilde", "õ"},
         {"ouml", "ö"},
         {"divide", "÷"},
         {"oslash", "ø"},
         {"ugrave", "ù"},
         {"uacute", "ú"},
         {"ucirc", "û"},
         {"uuml", "ü"},
         {"yacute", "ý"},
         {"thorn", "þ"},
         {"yuml", "ÿ"},
         {"OElig", "Œ"},
         {"oelig", "œ"},
         {"Scaron", "Š"},
         {"scaron", "š"},
         {"Yuml", "Ÿ"},
         {"circ", "ˆ"},
         {"tilde", "˜"},
         {"ensp", " "},
         {"emsp", " "},
         {"thinsp", " "},
         {"zwnj", "\u200c"},
         {"zwj", "\u200d"},
         {"lrm", "\u200e"},
         {"rlm", "\u200f"},
         {"ndash", "–"},
         {"mdash", "—"},
         {"lsquo", "‘"},
         {"rsquo", "’"},
         {"sbquo", "‚"},
         {"ldquo", "“"},
         {"rdquo", "”"},
         {"bdquo", "„"},
         {"dagger", "†"},
         {"Dagger", "‡"},
         {"permil", "‰"},
         {"lsaquo", "‹"},
         {"rsaquo", "›"},
         {"euro", "€"},
         {"fnof", "ƒ"},
         {"Alpha", "Α"},
         {"Beta", "Β"},
         {"Gamma", "Γ"},
         {"Delta", "Δ"},
         {"Epsilon", "Ε"},
         {"Zeta", "Ζ"},
         {"Eta", "Η"},
         {"Theta", "Θ"},
         {"Iota", "Ι"},
         {"Kappa", "Κ"},
         {"Lambda", "Λ"},
         {"Mu", "Μ"},
         {"Nu", "Ν"},
         {"Xi", "Ξ"},
         {"Omicron", "Ο"},
         {"Pi", "Π"},
         {"Rho", "Ρ"},
         {"Sigma", "Σ"},
         {"Tau", "Τ"},
         {"Upsilon", "Υ"},
         {"Phi", "Φ"},
         {"Chi", "Χ"},
         {"Psi", "Ψ"},
         {"Omega", "Ω"},
         {"alpha", "α"},
         {"beta", "β"},
         {"gamma", "γ"},
         {"delta", "δ"},
         {"epsilon", "ε"},
         {"zeta", "ζ"},
         {"eta", "η"},
         {"theta", "θ"},
         {"iota", "ι"},
         {"kappa", "κ"},
         {"lambda", "λ"},
         {"mu", "μ"},
         {"nu", "ν"},
         {"xi", "ξ"},
         {"omicron", "ο"},
         {"pi", "π"},
         {"rho", "ρ"},
         {"sigmaf", "ς"},
         {"sigma", "σ"},
         {"tau", "τ"},
         {"upsilon", "υ"},
         {"phi", "φ"},
         {"chi", "χ"},
         {"psi", "ψ"},
         {"omega", "ω"},
         {"thetasym", "ϑ"},
         {"upsih", "ϒ"},
         {"piv", "ϖ"},
         {"bull", "•"},
         {"hellip", "…"},
         {"prime", "′"},
         {"Prime", "″"},
         {"oline", "‾"},
         {"frasl", "⁄"},
         {"weierp", "℘"},
         {"image", "ℑ"},
         {"real", "ℜ"},
         {"trade", "™"},
         {"alefsym", "ℵ"},
         {"larr", "←"},
         {"uarr", "↑"},
         {"rarr", "→"},
         {"darr", "↓"},
         {"harr", "↔"},
         {"crarr", "↵"},
         {"lArr", "⇐"},
         {"uArr", "⇑"},
         {"rArr", "⇒"},
         {"dArr", "⇓"},
         {"hArr", "⇔"},
         {"forall", "∀"},
         {"part", "∂"},
         {"exist", "∃"},
         {"empty", "∅"},
         {"nabla", "∇"},
         {"isin", "∈"},
         {"notin", "∉"},
         {"ni", "∋"},
         {"prod", "∏"},
         {"sum", "∑"},
         {"minus", "−"},
         {"lowast", "∗"},
         {"radic", "√"},
         {"prop", "∝"},
         {"infin", "∞"},
         {"ang", "∠"},
         {"and", "∧"},
         {"or", "∨"},
         {"cap", "∩"},
         {"cup", "∪"},
         {"int", "∫"},
         {"there4", "∴"},
         {"sim", "∼"},
         {"cong", "≅"},
         {"asymp", "≈"},
         {"ne", "≠"},
         {"equiv", "≡"},
         {"le", "≤"},
         {"ge", "≥"},
         {"sub", "⊂"},
         {"sup", "⊃"},
         {"nsub", "⊄"},
         {"sube", "⊆"},
         {"supe", "⊇"},
         {"oplus", "⊕"},
         {"otimes", "⊗"},
         {"perp", "⊥"},
         {"sdot", "⋅"},
         {"lceil", "⌈"},
         {"rceil", "⌉"},
         {"lfloor", "⌊"},
         {"rfloor", "⌋"},
         {"lang", "〈"},
         {"rang", "〉"},
         {"loz", "◊"},
         {"spades", "♠"},
         {"clubs", "♣"},
         {"hearts", "♥"},
         {"diams", "♦"}
      }
   );

   public EntityReplacementMap(String[][] var1) {
      int var2 = var1.length;
      this.entityName = new String[var2];
      this.entityNameBuf = new char[var2][];
      this.entityReplacement = new String[var2];
      this.entityReplacementBuf = new char[var2][];
      this.entityNameHash = new int[var2];

      for (String[] var6 : var1) {
         this.defineEntityReplacementText(var6[0], var6[1]);
      }
   }

   private void defineEntityReplacementText(String var1, String var2) {
      if (!var2.startsWith("&#") && this.entityName != null && var2.length() > 1) {
         String var3 = var2.substring(1, var2.length() - 1);

         for (int var4 = 0; var4 < this.entityName.length; var4++) {
            if (this.entityName[var4] != null && this.entityName[var4].equals(var3)) {
               var2 = this.entityReplacement[var4];
            }
         }
      }

      char[] var5 = var1.toCharArray();
      this.entityName[this.entityEnd] = this.newString(var5, 0, var1.length());
      this.entityNameBuf[this.entityEnd] = var5;
      this.entityReplacement[this.entityEnd] = var2;
      this.entityReplacementBuf[this.entityEnd] = var2.toCharArray();
      this.entityNameHash[this.entityEnd] = fastHash(this.entityNameBuf[this.entityEnd], 0, this.entityNameBuf[this.entityEnd].length);
      this.entityEnd++;
   }

   private String newString(char[] var1, int var2, int var3) {
      return new String(var1, var2, var3);
   }

   private static int fastHash(char[] var0, int var1, int var2) {
      if (var2 == 0) {
         return 0;
      } else {
         int var3 = var0[var1];
         var3 = (var3 << 7) + var0[var1 + var2 - 1];
         if (var2 > 16) {
            var3 = (var3 << 7) + var0[var1 + var2 / 4];
         }

         if (var2 > 8) {
            var3 = (var3 << 7) + var0[var1 + var2 / 2];
         }

         return var3;
      }
   }
}
