package com.google.common.net;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Ascii;
import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

@Immutable
@ElementTypesAreNonnullByDefault
@Beta
@GwtCompatible
public final class MediaType {
   private static final String CHARSET_ATTRIBUTE = "charset";
   private static final ImmutableListMultimap<String, String> UTF_8_CONSTANT_PARAMETERS = ImmutableListMultimap.of(
      "charset", Ascii.toLowerCase(Charsets.UTF_8.name())
   );
   private static final CharMatcher TOKEN_MATCHER = CharMatcher.ascii()
      .and(CharMatcher.javaIsoControl().negate())
      .and(CharMatcher.isNot(' '))
      .and(CharMatcher.noneOf("()<>@,;:\\\"/[]?="));
   private static final CharMatcher QUOTED_TEXT_MATCHER = CharMatcher.ascii().and(CharMatcher.noneOf("\"\\\r"));
   private static final CharMatcher LINEAR_WHITE_SPACE = CharMatcher.anyOf(" \t\r\n");
   private static final String APPLICATION_TYPE = "application";
   private static final String AUDIO_TYPE = "audio";
   private static final String IMAGE_TYPE = "image";
   private static final String TEXT_TYPE = "text";
   private static final String VIDEO_TYPE = "video";
   private static final String FONT_TYPE = "font";
   private static final String WILDCARD = "*";
   private static final Map<MediaType, MediaType> KNOWN_TYPES = Maps.newHashMap();
   public static final MediaType ANY_TYPE = createConstant("*", "*");
   public static final MediaType ANY_TEXT_TYPE = createConstant("text", "*");
   public static final MediaType ANY_IMAGE_TYPE = createConstant("image", "*");
   public static final MediaType ANY_AUDIO_TYPE = createConstant("audio", "*");
   public static final MediaType ANY_VIDEO_TYPE = createConstant("video", "*");
   public static final MediaType ANY_APPLICATION_TYPE = createConstant("application", "*");
   public static final MediaType ANY_FONT_TYPE = createConstant("font", "*");
   public static final MediaType CACHE_MANIFEST_UTF_8 = createConstantUtf8("text", "cache-manifest");
   public static final MediaType CSS_UTF_8 = createConstantUtf8("text", "css");
   public static final MediaType CSV_UTF_8 = createConstantUtf8("text", "csv");
   public static final MediaType HTML_UTF_8 = createConstantUtf8("text", "html");
   public static final MediaType I_CALENDAR_UTF_8 = createConstantUtf8("text", "calendar");
   public static final MediaType PLAIN_TEXT_UTF_8 = createConstantUtf8("text", "plain");
   public static final MediaType TEXT_JAVASCRIPT_UTF_8 = createConstantUtf8("text", "javascript");
   public static final MediaType TSV_UTF_8 = createConstantUtf8("text", "tab-separated-values");
   public static final MediaType VCARD_UTF_8 = createConstantUtf8("text", "vcard");
   public static final MediaType WML_UTF_8 = createConstantUtf8("text", "vnd.wap.wml");
   public static final MediaType XML_UTF_8 = createConstantUtf8("text", "xml");
   public static final MediaType VTT_UTF_8 = createConstantUtf8("text", "vtt");
   public static final MediaType BMP = createConstant("image", "bmp");
   public static final MediaType CRW = createConstant("image", "x-canon-crw");
   public static final MediaType GIF = createConstant("image", "gif");
   public static final MediaType ICO = createConstant("image", "vnd.microsoft.icon");
   public static final MediaType JPEG = createConstant("image", "jpeg");
   public static final MediaType PNG = createConstant("image", "png");
   public static final MediaType PSD = createConstant("image", "vnd.adobe.photoshop");
   public static final MediaType SVG_UTF_8 = createConstantUtf8("image", "svg+xml");
   public static final MediaType TIFF = createConstant("image", "tiff");
   public static final MediaType WEBP = createConstant("image", "webp");
   public static final MediaType HEIF = createConstant("image", "heif");
   public static final MediaType JP2K = createConstant("image", "jp2");
   public static final MediaType MP4_AUDIO = createConstant("audio", "mp4");
   public static final MediaType MPEG_AUDIO = createConstant("audio", "mpeg");
   public static final MediaType OGG_AUDIO = createConstant("audio", "ogg");
   public static final MediaType WEBM_AUDIO = createConstant("audio", "webm");
   public static final MediaType L16_AUDIO = createConstant("audio", "l16");
   public static final MediaType L24_AUDIO = createConstant("audio", "l24");
   public static final MediaType BASIC_AUDIO = createConstant("audio", "basic");
   public static final MediaType AAC_AUDIO = createConstant("audio", "aac");
   public static final MediaType VORBIS_AUDIO = createConstant("audio", "vorbis");
   public static final MediaType WMA_AUDIO = createConstant("audio", "x-ms-wma");
   public static final MediaType WAX_AUDIO = createConstant("audio", "x-ms-wax");
   public static final MediaType VND_REAL_AUDIO = createConstant("audio", "vnd.rn-realaudio");
   public static final MediaType VND_WAVE_AUDIO = createConstant("audio", "vnd.wave");
   public static final MediaType MP4_VIDEO = createConstant("video", "mp4");
   public static final MediaType MPEG_VIDEO = createConstant("video", "mpeg");
   public static final MediaType OGG_VIDEO = createConstant("video", "ogg");
   public static final MediaType QUICKTIME = createConstant("video", "quicktime");
   public static final MediaType WEBM_VIDEO = createConstant("video", "webm");
   public static final MediaType WMV = createConstant("video", "x-ms-wmv");
   public static final MediaType FLV_VIDEO = createConstant("video", "x-flv");
   public static final MediaType THREE_GPP_VIDEO = createConstant("video", "3gpp");
   public static final MediaType THREE_GPP2_VIDEO = createConstant("video", "3gpp2");
   public static final MediaType APPLICATION_XML_UTF_8 = createConstantUtf8("application", "xml");
   public static final MediaType ATOM_UTF_8 = createConstantUtf8("application", "atom+xml");
   public static final MediaType BZIP2 = createConstant("application", "x-bzip2");
   public static final MediaType DART_UTF_8 = createConstantUtf8("application", "dart");
   public static final MediaType APPLE_PASSBOOK = createConstant("application", "vnd.apple.pkpass");
   public static final MediaType EOT = createConstant("application", "vnd.ms-fontobject");
   public static final MediaType EPUB = createConstant("application", "epub+zip");
   public static final MediaType FORM_DATA = createConstant("application", "x-www-form-urlencoded");
   public static final MediaType KEY_ARCHIVE = createConstant("application", "pkcs12");
   public static final MediaType APPLICATION_BINARY = createConstant("application", "binary");
   public static final MediaType GEO_JSON = createConstant("application", "geo+json");
   public static final MediaType GZIP = createConstant("application", "x-gzip");
   public static final MediaType HAL_JSON = createConstant("application", "hal+json");
   public static final MediaType JAVASCRIPT_UTF_8 = createConstantUtf8("application", "javascript");
   public static final MediaType JOSE = createConstant("application", "jose");
   public static final MediaType JOSE_JSON = createConstant("application", "jose+json");
   public static final MediaType JSON_UTF_8 = createConstantUtf8("application", "json");
   public static final MediaType MANIFEST_JSON_UTF_8 = createConstantUtf8("application", "manifest+json");
   public static final MediaType KML = createConstant("application", "vnd.google-earth.kml+xml");
   public static final MediaType KMZ = createConstant("application", "vnd.google-earth.kmz");
   public static final MediaType MBOX = createConstant("application", "mbox");
   public static final MediaType APPLE_MOBILE_CONFIG = createConstant("application", "x-apple-aspen-config");
   public static final MediaType MICROSOFT_EXCEL = createConstant("application", "vnd.ms-excel");
   public static final MediaType MICROSOFT_OUTLOOK = createConstant("application", "vnd.ms-outlook");
   public static final MediaType MICROSOFT_POWERPOINT = createConstant("application", "vnd.ms-powerpoint");
   public static final MediaType MICROSOFT_WORD = createConstant("application", "msword");
   public static final MediaType MEDIA_PRESENTATION_DESCRIPTION = createConstant("application", "dash+xml");
   public static final MediaType WASM_APPLICATION = createConstant("application", "wasm");
   public static final MediaType NACL_APPLICATION = createConstant("application", "x-nacl");
   public static final MediaType NACL_PORTABLE_APPLICATION = createConstant("application", "x-pnacl");
   public static final MediaType OCTET_STREAM = createConstant("application", "octet-stream");
   public static final MediaType OGG_CONTAINER = createConstant("application", "ogg");
   public static final MediaType OOXML_DOCUMENT = createConstant("application", "vnd.openxmlformats-officedocument.wordprocessingml.document");
   public static final MediaType OOXML_PRESENTATION = createConstant("application", "vnd.openxmlformats-officedocument.presentationml.presentation");
   public static final MediaType OOXML_SHEET = createConstant("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet");
   public static final MediaType OPENDOCUMENT_GRAPHICS = createConstant("application", "vnd.oasis.opendocument.graphics");
   public static final MediaType OPENDOCUMENT_PRESENTATION = createConstant("application", "vnd.oasis.opendocument.presentation");
   public static final MediaType OPENDOCUMENT_SPREADSHEET = createConstant("application", "vnd.oasis.opendocument.spreadsheet");
   public static final MediaType OPENDOCUMENT_TEXT = createConstant("application", "vnd.oasis.opendocument.text");
   public static final MediaType OPENSEARCH_DESCRIPTION_UTF_8 = createConstantUtf8("application", "opensearchdescription+xml");
   public static final MediaType PDF = createConstant("application", "pdf");
   public static final MediaType POSTSCRIPT = createConstant("application", "postscript");
   public static final MediaType PROTOBUF = createConstant("application", "protobuf");
   public static final MediaType RDF_XML_UTF_8 = createConstantUtf8("application", "rdf+xml");
   public static final MediaType RTF_UTF_8 = createConstantUtf8("application", "rtf");
   public static final MediaType SFNT = createConstant("application", "font-sfnt");
   public static final MediaType SHOCKWAVE_FLASH = createConstant("application", "x-shockwave-flash");
   public static final MediaType SKETCHUP = createConstant("application", "vnd.sketchup.skp");
   public static final MediaType SOAP_XML_UTF_8 = createConstantUtf8("application", "soap+xml");
   public static final MediaType TAR = createConstant("application", "x-tar");
   public static final MediaType WOFF = createConstant("application", "font-woff");
   public static final MediaType WOFF2 = createConstant("application", "font-woff2");
   public static final MediaType XHTML_UTF_8 = createConstantUtf8("application", "xhtml+xml");
   public static final MediaType XRD_UTF_8 = createConstantUtf8("application", "xrd+xml");
   public static final MediaType ZIP = createConstant("application", "zip");
   public static final MediaType FONT_COLLECTION = createConstant("font", "collection");
   public static final MediaType FONT_OTF = createConstant("font", "otf");
   public static final MediaType FONT_SFNT = createConstant("font", "sfnt");
   public static final MediaType FONT_TTF = createConstant("font", "ttf");
   public static final MediaType FONT_WOFF = createConstant("font", "woff");
   public static final MediaType FONT_WOFF2 = createConstant("font", "woff2");
   private final String type;
   private final String subtype;
   private final ImmutableListMultimap<String, String> parameters;
   @LazyInit
   private String toString;
   @LazyInit
   private int hashCode;
   @LazyInit
   private Optional<Charset> parsedCharset;
   private static final Joiner.MapJoiner PARAMETER_JOINER = Joiner.on("; ").withKeyValueSeparator("=");

   private static MediaType createConstant(String var0, String var1) {
      MediaType var2 = addKnownType(new MediaType(var0, var1, ImmutableListMultimap.of()));
      var2.parsedCharset = Optional.absent();
      return var2;
   }

   private static MediaType createConstantUtf8(String var0, String var1) {
      MediaType var2 = addKnownType(new MediaType(var0, var1, UTF_8_CONSTANT_PARAMETERS));
      var2.parsedCharset = Optional.of(Charsets.UTF_8);
      return var2;
   }

   private static MediaType addKnownType(MediaType var0) {
      KNOWN_TYPES.put(var0, var0);
      return var0;
   }

   private MediaType(String var1, String var2, ImmutableListMultimap<String, String> var3) {
      this.type = var1;
      this.subtype = var2;
      this.parameters = var3;
   }

   public String type() {
      return this.type;
   }

   public String subtype() {
      return this.subtype;
   }

   public ImmutableListMultimap<String, String> parameters() {
      return this.parameters;
   }

   private Map<String, ImmutableMultiset<String>> parametersAsMap() {
      return Maps.transformValues(this.parameters.asMap(), new Function<Collection<String>, ImmutableMultiset<String>>(this) {
         public ImmutableMultiset<String> apply(Collection<String> var1) {
            return ImmutableMultiset.copyOf(var1);
         }
      });
   }

   public Optional<Charset> charset() {
      Optional var1 = this.parsedCharset;
      if (var1 == null) {
         String var2 = null;
         var1 = Optional.absent();

         for (String var4 : this.parameters.get("charset")) {
            if (var2 == null) {
               var2 = var4;
               var1 = Optional.of(Charset.forName(var4));
            } else if (!var2.equals(var4)) {
               throw new IllegalStateException(
                  new StringBuilder(35 + String.valueOf(var2).length() + String.valueOf(var4).length())
                     .append("Multiple charset values defined: ")
                     .append(var2)
                     .append(", ")
                     .append(var4)
                     .toString()
               );
            }
         }

         this.parsedCharset = var1;
      }

      return var1;
   }

   public MediaType withoutParameters() {
      return this.parameters.isEmpty() ? this : create(this.type, this.subtype);
   }

   public MediaType withParameters(Multimap<String, String> var1) {
      return create(this.type, this.subtype, var1);
   }

   public MediaType withParameters(String var1, Iterable<String> var2) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      String var3 = normalizeToken(var1);
      ImmutableListMultimap.Builder var4 = ImmutableListMultimap.builder();

      for (Entry var6 : this.parameters.entries()) {
         String var7 = (String)var6.getKey();
         if (!var3.equals(var7)) {
            var4.put(var7, (String)var6.getValue());
         }
      }

      for (String var10 : var2) {
         var4.put(var3, normalizeParameterValue(var3, var10));
      }

      MediaType var9 = new MediaType(this.type, this.subtype, var4.build());
      if (!var3.equals("charset")) {
         var9.parsedCharset = this.parsedCharset;
      }

      return MoreObjects.firstNonNull(KNOWN_TYPES.get(var9), var9);
   }

   public MediaType withParameter(String var1, String var2) {
      return this.withParameters(var1, ImmutableSet.of(var2));
   }

   public MediaType withCharset(Charset var1) {
      Preconditions.checkNotNull(var1);
      MediaType var2 = this.withParameter("charset", var1.name());
      var2.parsedCharset = Optional.of(var1);
      return var2;
   }

   public boolean hasWildcard() {
      return "*".equals(this.type) || "*".equals(this.subtype);
   }

   public boolean is(MediaType var1) {
      return (var1.type.equals("*") || var1.type.equals(this.type))
         && (var1.subtype.equals("*") || var1.subtype.equals(this.subtype))
         && this.parameters.entries().containsAll(var1.parameters.entries());
   }

   public static MediaType create(String var0, String var1) {
      MediaType var2 = create(var0, var1, ImmutableListMultimap.of());
      var2.parsedCharset = Optional.absent();
      return var2;
   }

   private static MediaType create(String var0, String var1, Multimap<String, String> var2) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      String var3 = normalizeToken(var0);
      String var4 = normalizeToken(var1);
      Preconditions.checkArgument(!"*".equals(var3) || "*".equals(var4), "A wildcard type cannot be used with a non-wildcard subtype");
      ImmutableListMultimap.Builder var5 = ImmutableListMultimap.builder();

      for (Entry var7 : var2.entries()) {
         String var8 = normalizeToken((String)var7.getKey());
         var5.put(var8, normalizeParameterValue(var8, (String)var7.getValue()));
      }

      MediaType var9 = new MediaType(var3, var4, var5.build());
      return MoreObjects.firstNonNull(KNOWN_TYPES.get(var9), var9);
   }

   static MediaType createApplicationType(String var0) {
      return create("application", var0);
   }

   static MediaType createAudioType(String var0) {
      return create("audio", var0);
   }

   static MediaType createFontType(String var0) {
      return create("font", var0);
   }

   static MediaType createImageType(String var0) {
      return create("image", var0);
   }

   static MediaType createTextType(String var0) {
      return create("text", var0);
   }

   static MediaType createVideoType(String var0) {
      return create("video", var0);
   }

   private static String normalizeToken(String var0) {
      Preconditions.checkArgument(TOKEN_MATCHER.matchesAllOf(var0));
      Preconditions.checkArgument(!var0.isEmpty());
      return Ascii.toLowerCase(var0);
   }

   private static String normalizeParameterValue(String var0, String var1) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkArgument(CharMatcher.ascii().matchesAllOf(var1), "parameter values must be ASCII: %s", var1);
      return "charset".equals(var0) ? Ascii.toLowerCase(var1) : var1;
   }

   public static MediaType parse(String var0) {
      Preconditions.checkNotNull(var0);
      MediaType.Tokenizer var1 = new MediaType.Tokenizer(var0);

      try {
         String var2 = var1.consumeToken(TOKEN_MATCHER);
         var1.consumeCharacter('/');
         String var3 = var1.consumeToken(TOKEN_MATCHER);
         ImmutableListMultimap.Builder var4 = ImmutableListMultimap.builder();

         while (var1.hasMore()) {
            var1.consumeTokenIfPresent(LINEAR_WHITE_SPACE);
            var1.consumeCharacter(';');
            var1.consumeTokenIfPresent(LINEAR_WHITE_SPACE);
            String var5 = var1.consumeToken(TOKEN_MATCHER);
            var1.consumeCharacter('=');
            String var6;
            if ('"' != var1.previewChar()) {
               var6 = var1.consumeToken(TOKEN_MATCHER);
            } else {
               var1.consumeCharacter('"');
               StringBuilder var7 = new StringBuilder();

               while ('"' != var1.previewChar()) {
                  if ('\\' == var1.previewChar()) {
                     var1.consumeCharacter('\\');
                     var7.append(var1.consumeCharacter(CharMatcher.ascii()));
                  } else {
                     var7.append(var1.consumeToken(QUOTED_TEXT_MATCHER));
                  }
               }

               var6 = var7.toString();
               var1.consumeCharacter('"');
            }

            var4.put(var5, var6);
         }

         return create(var2, var3, var4.build());
      } catch (IllegalStateException var8) {
         throw new IllegalArgumentException(
            new StringBuilder(18 + String.valueOf(var0).length()).append("Could not parse '").append(var0).append("'").toString(), var8
         );
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof MediaType)) {
         return false;
      } else {
         MediaType var2 = (MediaType)var1;
         return this.type.equals(var2.type) && this.subtype.equals(var2.subtype) && this.parametersAsMap().equals(var2.parametersAsMap());
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.hashCode;
      if (var1 == 0) {
         var1 = Objects.hashCode(this.type, this.subtype, this.parametersAsMap());
         this.hashCode = var1;
      }

      return var1;
   }

   @Override
   public String toString() {
      String var1 = this.toString;
      if (var1 == null) {
         var1 = this.computeToString();
         this.toString = var1;
      }

      return var1;
   }

   private String computeToString() {
      StringBuilder var1 = new StringBuilder().append(this.type).append('/').append(this.subtype);
      if (!this.parameters.isEmpty()) {
         var1.append("; ");
         ListMultimap var2 = Multimaps.transformValues(this.parameters, new Function<String, String>(this) {
            public String apply(String var1) {
               return MediaType.TOKEN_MATCHER.matchesAllOf(var1) && !var1.isEmpty() ? var1 : MediaType.escapeAndQuote(var1);
            }
         });
         PARAMETER_JOINER.appendTo(var1, var2.entries());
      }

      return var1.toString();
   }

   private static String escapeAndQuote(String var0) {
      StringBuilder var1 = new StringBuilder(var0.length() + 16).append('"');

      for (int var2 = 0; var2 < var0.length(); var2++) {
         char var3 = var0.charAt(var2);
         if (var3 == '\r' || var3 == '\\' || var3 == '"') {
            var1.append('\\');
         }

         var1.append(var3);
      }

      return var1.append('"').toString();
   }

   private static final class Tokenizer {
      final String input;
      int position = 0;

      Tokenizer(String var1) {
         this.input = var1;
      }

      String consumeTokenIfPresent(CharMatcher var1) {
         Preconditions.checkState(this.hasMore());
         int var2 = this.position;
         this.position = var1.negate().indexIn(this.input, var2);
         return this.hasMore() ? this.input.substring(var2, this.position) : this.input.substring(var2);
      }

      String consumeToken(CharMatcher var1) {
         int var2 = this.position;
         String var3 = this.consumeTokenIfPresent(var1);
         Preconditions.checkState(this.position != var2);
         return var3;
      }

      char consumeCharacter(CharMatcher var1) {
         Preconditions.checkState(this.hasMore());
         char var2 = this.previewChar();
         Preconditions.checkState(var1.matches(var2));
         this.position++;
         return var2;
      }

      char consumeCharacter(char var1) {
         Preconditions.checkState(this.hasMore());
         Preconditions.checkState(this.previewChar() == var1);
         this.position++;
         return var1;
      }

      char previewChar() {
         Preconditions.checkState(this.hasMore());
         return this.input.charAt(this.position);
      }

      boolean hasMore() {
         return this.position >= 0 && this.position < this.input.length();
      }
   }
}
