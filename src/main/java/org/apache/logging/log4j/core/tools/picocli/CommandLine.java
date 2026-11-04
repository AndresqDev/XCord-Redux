package org.apache.logging.log4j.core.tools.picocli;

import java.io.File;
import java.io.PrintStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.text.BreakIterator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

public class CommandLine {
   public static final String VERSION = "2.0.3";
   private final CommandLine.Tracer tracer = new CommandLine.Tracer();
   private final CommandLine.Interpreter interpreter;
   private String commandName = "<main class>";
   private boolean overwrittenOptionsAllowed = false;
   private boolean unmatchedArgumentsAllowed = false;
   private final List<String> unmatchedArguments = new ArrayList<>();
   private CommandLine parent;
   private boolean usageHelpRequested;
   private boolean versionHelpRequested;
   private final List<String> versionLines = new ArrayList<>();

   public CommandLine(Object var1) {
      this.interpreter = new CommandLine.Interpreter(var1);
   }

   public CommandLine addSubcommand(String var1, Object var2) {
      CommandLine var3 = toCommandLine(var2);
      var3.parent = this;
      this.interpreter.commands.put(var1, var3);
      return this;
   }

   public Map<String, CommandLine> getSubcommands() {
      return new LinkedHashMap<>(this.interpreter.commands);
   }

   public CommandLine getParent() {
      return this.parent;
   }

   public <T> T getCommand() {
      return (T)this.interpreter.command;
   }

   public boolean isUsageHelpRequested() {
      return this.usageHelpRequested;
   }

   public boolean isVersionHelpRequested() {
      return this.versionHelpRequested;
   }

   public boolean isOverwrittenOptionsAllowed() {
      return this.overwrittenOptionsAllowed;
   }

   public CommandLine setOverwrittenOptionsAllowed(boolean var1) {
      this.overwrittenOptionsAllowed = var1;

      for (CommandLine var3 : this.interpreter.commands.values()) {
         var3.setOverwrittenOptionsAllowed(var1);
      }

      return this;
   }

   public boolean isUnmatchedArgumentsAllowed() {
      return this.unmatchedArgumentsAllowed;
   }

   public CommandLine setUnmatchedArgumentsAllowed(boolean var1) {
      this.unmatchedArgumentsAllowed = var1;

      for (CommandLine var3 : this.interpreter.commands.values()) {
         var3.setUnmatchedArgumentsAllowed(var1);
      }

      return this;
   }

   public List<String> getUnmatchedArguments() {
      return this.unmatchedArguments;
   }

   public static <T> T populateCommand(T var0, String... var1) {
      CommandLine var2 = toCommandLine(var0);
      var2.parse(var1);
      return (T)var0;
   }

   public List<CommandLine> parse(String... var1) {
      return this.interpreter.parse(var1);
   }

   public static boolean printHelpIfRequested(List<CommandLine> var0, PrintStream var1, CommandLine.Help.Ansi var2) {
      for (CommandLine var4 : var0) {
         if (var4.isUsageHelpRequested()) {
            var4.usage(var1, var2);
            return true;
         }

         if (var4.isVersionHelpRequested()) {
            var4.printVersionHelp(var1, var2);
            return true;
         }
      }

      return false;
   }

   private static Object execute(CommandLine var0) {
      Object var1 = var0.getCommand();
      if (var1 instanceof Runnable) {
         try {
            ((Runnable)var1).run();
            return null;
         } catch (Exception var3) {
            throw new CommandLine.ExecutionException(var0, "Error while running command (" + var1 + ")", var3);
         }
      } else if (var1 instanceof Callable) {
         try {
            return ((Callable)var1).call();
         } catch (Exception var4) {
            throw new CommandLine.ExecutionException(var0, "Error while calling command (" + var1 + ")", var4);
         }
      } else {
         throw new CommandLine.ExecutionException(var0, "Parsed command (" + var1 + ") is not Runnable or Callable");
      }
   }

   public List<Object> parseWithHandler(CommandLine.IParseResultHandler var1, PrintStream var2, String... var3) {
      return this.parseWithHandlers(var1, var2, CommandLine.Help.Ansi.AUTO, new CommandLine.DefaultExceptionHandler(), var3);
   }

   public List<Object> parseWithHandlers(
      CommandLine.IParseResultHandler var1, PrintStream var2, CommandLine.Help.Ansi var3, CommandLine.IExceptionHandler var4, String... var5
   ) {
      try {
         List var6 = this.parse(var5);
         return var1.handleParseResult(var6, var2, var3);
      } catch (CommandLine.ParameterException var7) {
         return var4.handleException(var7, var2, var3, var5);
      }
   }

   public static void usage(Object var0, PrintStream var1) {
      toCommandLine(var0).usage(var1);
   }

   public static void usage(Object var0, PrintStream var1, CommandLine.Help.Ansi var2) {
      toCommandLine(var0).usage(var1, var2);
   }

   public static void usage(Object var0, PrintStream var1, CommandLine.Help.ColorScheme var2) {
      toCommandLine(var0).usage(var1, var2);
   }

   public void usage(PrintStream var1) {
      this.usage(var1, CommandLine.Help.Ansi.AUTO);
   }

   public void usage(PrintStream var1, CommandLine.Help.Ansi var2) {
      this.usage(var1, CommandLine.Help.defaultColorScheme(var2));
   }

   public void usage(PrintStream var1, CommandLine.Help.ColorScheme var2) {
      CommandLine.Help var3 = new CommandLine.Help(this.interpreter.command, var2).addAllSubcommands(this.getSubcommands());
      if (!"=".equals(this.getSeparator())) {
         var3.separator = this.getSeparator();
         var3.parameterLabelRenderer = var3.createDefaultParamLabelRenderer();
      }

      if (!"<main class>".equals(this.getCommandName())) {
         var3.commandName = this.getCommandName();
      }

      StringBuilder var4 = new StringBuilder()
         .append(var3.headerHeading())
         .append(var3.header())
         .append(var3.synopsisHeading())
         .append(var3.synopsis(var3.synopsisHeadingLength()))
         .append(var3.descriptionHeading())
         .append(var3.description())
         .append(var3.parameterListHeading())
         .append(var3.parameterList())
         .append(var3.optionListHeading())
         .append(var3.optionList())
         .append(var3.commandListHeading())
         .append(var3.commandList())
         .append(var3.footerHeading())
         .append(var3.footer());
      var1.print(var4);
   }

   public void printVersionHelp(PrintStream var1) {
      this.printVersionHelp(var1, CommandLine.Help.Ansi.AUTO);
   }

   public void printVersionHelp(PrintStream var1, CommandLine.Help.Ansi var2) {
      for (String var4 : this.versionLines) {
         var1.println(var2.new Text(var4));
      }
   }

   public void printVersionHelp(PrintStream var1, CommandLine.Help.Ansi var2, Object... var3) {
      for (String var5 : this.versionLines) {
         var1.println(var2.new Text(String.format(var5, var3)));
      }
   }

   public static <C extends Callable<T>, T> T call(C var0, PrintStream var1, String... var2) {
      return call((C)var0, var1, CommandLine.Help.Ansi.AUTO, var2);
   }

   public static <C extends Callable<T>, T> T call(C var0, PrintStream var1, CommandLine.Help.Ansi var2, String... var3) {
      CommandLine var4 = new CommandLine(var0);
      List var5 = var4.parseWithHandlers(new CommandLine.RunLast(), var1, var2, new CommandLine.DefaultExceptionHandler(), var3);
      return (T)(var5 != null && !var5.isEmpty() ? var5.get(0) : null);
   }

   public static <R extends Runnable> void run(R var0, PrintStream var1, String... var2) {
      run(var0, var1, CommandLine.Help.Ansi.AUTO, var2);
   }

   public static <R extends Runnable> void run(R var0, PrintStream var1, CommandLine.Help.Ansi var2, String... var3) {
      CommandLine var4 = new CommandLine(var0);
      var4.parseWithHandlers(new CommandLine.RunLast(), var1, var2, new CommandLine.DefaultExceptionHandler(), var3);
   }

   public <K> CommandLine registerConverter(Class<K> var1, CommandLine.ITypeConverter<K> var2) {
      this.interpreter.converterRegistry.put(CommandLine.Assert.notNull(var1, "class"), CommandLine.Assert.notNull(var2, "converter"));

      for (CommandLine var4 : this.interpreter.commands.values()) {
         var4.registerConverter(var1, var2);
      }

      return this;
   }

   public String getSeparator() {
      return this.interpreter.separator;
   }

   public CommandLine setSeparator(String var1) {
      this.interpreter.separator = CommandLine.Assert.notNull(var1, "separator");
      return this;
   }

   public String getCommandName() {
      return this.commandName;
   }

   public CommandLine setCommandName(String var1) {
      this.commandName = CommandLine.Assert.notNull(var1, "commandName");
      return this;
   }

   private static boolean empty(String var0) {
      return var0 == null || var0.trim().length() == 0;
   }

   private static boolean empty(Object[] var0) {
      return var0 == null || var0.length == 0;
   }

   private static boolean empty(CommandLine.Help.Ansi.Text var0) {
      return var0 == null || var0.plain.toString().trim().length() == 0;
   }

   private static String str(String[] var0, int var1) {
      return var0 != null && var0.length != 0 ? var0[var1] : "";
   }

   private static boolean isBoolean(Class<?> var0) {
      return var0 == Boolean.class || var0 == boolean.class;
   }

   private static CommandLine toCommandLine(Object var0) {
      return var0 instanceof CommandLine ? (CommandLine)var0 : new CommandLine(var0);
   }

   private static boolean isMultiValue(Field var0) {
      return isMultiValue(var0.getType());
   }

   private static boolean isMultiValue(Class<?> var0) {
      return var0.isArray() || Collection.class.isAssignableFrom(var0) || Map.class.isAssignableFrom(var0);
   }

   private static Class<?>[] getTypeAttribute(Field var0) {
      Class[] var1 = var0.isAnnotationPresent(CommandLine.Parameters.class)
         ? var0.getAnnotation(CommandLine.Parameters.class).type()
         : var0.getAnnotation(CommandLine.Option.class).type();
      if (var1.length > 0) {
         return var1;
      } else if (var0.getType().isArray()) {
         return new Class[]{var0.getType().getComponentType()};
      } else if (!isMultiValue(var0)) {
         return new Class[]{var0.getType()};
      } else {
         Type var2 = var0.getGenericType();
         if (!(var2 instanceof ParameterizedType)) {
            return new Class[]{String.class, String.class};
         } else {
            ParameterizedType var3 = (ParameterizedType)var2;
            Type[] var4 = var3.getActualTypeArguments();
            Class[] var5 = new Class[var4.length];
            int var6 = 0;

            while (true) {
               if (var6 >= var4.length) {
                  return var5;
               }

               if (var4[var6] instanceof Class) {
                  var5[var6] = (Class)var4[var6];
               } else {
                  if (!(var4[var6] instanceof WildcardType)) {
                     break;
                  }

                  WildcardType var7 = (WildcardType)var4[var6];
                  Type[] var8 = var7.getLowerBounds();
                  if (var8.length > 0 && var8[0] instanceof Class) {
                     var5[var6] = (Class)var8[0];
                  } else {
                     Type[] var9 = var7.getUpperBounds();
                     if (var9.length <= 0 || !(var9[0] instanceof Class)) {
                        break;
                     }

                     var5[var6] = (Class)var9[0];
                  }
               }

               var6++;
            }

            Arrays.fill(var5, String.class);
            return var5;
         }
      }
   }

   static void init(Class<?> var0, List<Field> var1, Map<String, Field> var2, Map<Character, Field> var3, List<Field> var4) {
      Field[] var5 = var0.getDeclaredFields();

      for (Field var9 : var5) {
         var9.setAccessible(true);
         if (var9.isAnnotationPresent(CommandLine.Option.class)) {
            CommandLine.Option var10 = var9.getAnnotation(CommandLine.Option.class);
            if (var10.required()) {
               var1.add(var9);
            }

            for (String var14 : var10.names()) {
               Field var15 = var2.put(var14, var9);
               if (var15 != null && var15 != var9) {
                  throw CommandLine.DuplicateOptionAnnotationsException.create(var14, var9, var15);
               }

               if (var14.length() == 2 && var14.startsWith("-")) {
                  char var16 = var14.charAt(1);
                  Field var17 = var3.put(var16, var9);
                  if (var17 != null && var17 != var9) {
                     throw CommandLine.DuplicateOptionAnnotationsException.create(var14, var9, var17);
                  }
               }
            }
         }

         if (var9.isAnnotationPresent(CommandLine.Parameters.class)) {
            if (var9.isAnnotationPresent(CommandLine.Option.class)) {
               throw new CommandLine.DuplicateOptionAnnotationsException("A field can be either @Option or @Parameters, but '" + var9.getName() + "' is both.");
            }

            var4.add(var9);
            CommandLine.Range var18 = CommandLine.Range.parameterArity(var9);
            if (var18.min > 0) {
               var1.add(var9);
            }
         }
      }
   }

   static void validatePositionalParameters(List<Field> var0) {
      int var1 = 0;

      for (Field var3 : var0) {
         CommandLine.Range var4 = CommandLine.Range.parameterIndex(var3);
         if (var4.min > var1) {
            throw new CommandLine.ParameterIndexGapException(
               "Missing field annotated with @Parameter(index=" + var1 + "). Nearest field '" + var3.getName() + "' has index=" + var4.min
            );
         }

         var1 = Math.max(var1, var4.max);
         var1 = var1 == Integer.MAX_VALUE ? var1 : var1 + 1;
      }
   }

   private static <T> Stack<T> reverse(Stack<T> var0) {
      Collections.reverse(var0);
      return var0;
   }

   private static final class Assert {
      static <T> T notNull(T var0, String var1) {
         if (var0 == null) {
            throw new NullPointerException(var1);
         } else {
            return (T)var0;
         }
      }
   }

   private static class BuiltIn {
      static class BigDecimalConverter implements CommandLine.ITypeConverter<BigDecimal> {
         public BigDecimal convert(String var1) {
            return new BigDecimal(var1);
         }
      }

      static class BigIntegerConverter implements CommandLine.ITypeConverter<BigInteger> {
         public BigInteger convert(String var1) {
            return new BigInteger(var1);
         }
      }

      static class BooleanConverter implements CommandLine.ITypeConverter<Boolean> {
         public Boolean convert(String var1) {
            if (!"true".equalsIgnoreCase(var1) && !"false".equalsIgnoreCase(var1)) {
               throw new CommandLine.TypeConversionException("'" + var1 + "' is not a boolean");
            } else {
               return Boolean.parseBoolean(var1);
            }
         }
      }

      static class ByteConverter implements CommandLine.ITypeConverter<Byte> {
         public Byte convert(String var1) {
            return Byte.valueOf(var1);
         }
      }

      static class CharSequenceConverter implements CommandLine.ITypeConverter<CharSequence> {
         public String convert(String var1) {
            return var1;
         }
      }

      static class CharacterConverter implements CommandLine.ITypeConverter<Character> {
         public Character convert(String var1) {
            if (var1.length() > 1) {
               throw new CommandLine.TypeConversionException("'" + var1 + "' is not a single character");
            } else {
               return var1.charAt(0);
            }
         }
      }

      static class CharsetConverter implements CommandLine.ITypeConverter<Charset> {
         public Charset convert(String var1) {
            return Charset.forName(var1);
         }
      }

      static class DoubleConverter implements CommandLine.ITypeConverter<Double> {
         public Double convert(String var1) {
            return Double.valueOf(var1);
         }
      }

      static class FileConverter implements CommandLine.ITypeConverter<File> {
         public File convert(String var1) {
            return new File(var1);
         }
      }

      static class FloatConverter implements CommandLine.ITypeConverter<Float> {
         public Float convert(String var1) {
            return Float.valueOf(var1);
         }
      }

      static class ISO8601DateConverter implements CommandLine.ITypeConverter<Date> {
         public Date convert(String var1) {
            try {
               return new SimpleDateFormat("yyyy-MM-dd").parse(var1);
            } catch (ParseException var3) {
               throw new CommandLine.TypeConversionException("'" + var1 + "' is not a yyyy-MM-dd date");
            }
         }
      }

      static class ISO8601TimeConverter implements CommandLine.ITypeConverter<Time> {
         public Time convert(String var1) {
            try {
               if (var1.length() <= 5) {
                  return new Time(new SimpleDateFormat("HH:mm").parse(var1).getTime());
               }

               if (var1.length() <= 8) {
                  return new Time(new SimpleDateFormat("HH:mm:ss").parse(var1).getTime());
               }

               if (var1.length() <= 12) {
                  try {
                     return new Time(new SimpleDateFormat("HH:mm:ss.SSS").parse(var1).getTime());
                  } catch (ParseException var3) {
                     return new Time(new SimpleDateFormat("HH:mm:ss,SSS").parse(var1).getTime());
                  }
               }
            } catch (ParseException var4) {
            }

            throw new CommandLine.TypeConversionException("'" + var1 + "' is not a HH:mm[:ss[.SSS]] time");
         }
      }

      static class InetAddressConverter implements CommandLine.ITypeConverter<InetAddress> {
         public InetAddress convert(String var1) throws Exception {
            return InetAddress.getByName(var1);
         }
      }

      static class IntegerConverter implements CommandLine.ITypeConverter<Integer> {
         public Integer convert(String var1) {
            return Integer.valueOf(var1);
         }
      }

      static class LongConverter implements CommandLine.ITypeConverter<Long> {
         public Long convert(String var1) {
            return Long.valueOf(var1);
         }
      }

      static class PathConverter implements CommandLine.ITypeConverter<Path> {
         public Path convert(String var1) {
            return Paths.get(var1);
         }
      }

      static class PatternConverter implements CommandLine.ITypeConverter<Pattern> {
         public Pattern convert(String var1) {
            return Pattern.compile(var1);
         }
      }

      static class ShortConverter implements CommandLine.ITypeConverter<Short> {
         public Short convert(String var1) {
            return Short.valueOf(var1);
         }
      }

      static class StringBuilderConverter implements CommandLine.ITypeConverter<StringBuilder> {
         public StringBuilder convert(String var1) {
            return new StringBuilder(var1);
         }
      }

      static class StringConverter implements CommandLine.ITypeConverter<String> {
         public String convert(String var1) {
            return var1;
         }
      }

      static class URIConverter implements CommandLine.ITypeConverter<URI> {
         public URI convert(String var1) throws URISyntaxException {
            return new URI(var1);
         }
      }

      static class URLConverter implements CommandLine.ITypeConverter<URL> {
         public URL convert(String var1) throws MalformedURLException {
            return new URL(var1);
         }
      }

      static class UUIDConverter implements CommandLine.ITypeConverter<UUID> {
         public UUID convert(String var1) throws Exception {
            return UUID.fromString(var1);
         }
      }
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.TYPE, ElementType.LOCAL_VARIABLE, ElementType.PACKAGE})
   public @interface Command {
      String name() default "<main class>";

      Class<?>[] subcommands() default {};

      String separator() default "=";

      String[] version() default {};

      String headerHeading() default "";

      String[] header() default {};

      String synopsisHeading() default "Usage: ";

      boolean abbreviateSynopsis() default false;

      String[] customSynopsis() default {};

      String descriptionHeading() default "";

      String[] description() default {};

      String parameterListHeading() default "";

      String optionListHeading() default "";

      boolean sortOptions() default true;

      char requiredOptionMarker() default ' ';

      boolean showDefaultValues() default false;

      String commandListHeading() default "Commands:%n";

      String footerHeading() default "";

      String[] footer() default {};
   }

   public static class DefaultExceptionHandler implements CommandLine.IExceptionHandler {
      @Override
      public List<Object> handleException(CommandLine.ParameterException var1, PrintStream var2, CommandLine.Help.Ansi var3, String... var4) {
         var2.println(var1.getMessage());
         var1.getCommandLine().usage(var2, var3);
         return Collections.emptyList();
      }
   }

   public static class DuplicateOptionAnnotationsException extends CommandLine.InitializationException {
      private static final long serialVersionUID = -3355128012575075641L;

      public DuplicateOptionAnnotationsException(String var1) {
         super(var1);
      }

      private static CommandLine.DuplicateOptionAnnotationsException create(String var0, Field var1, Field var2) {
         return new CommandLine.DuplicateOptionAnnotationsException(
            "Option name '"
               + var0
               + "' is used by both "
               + var1.getDeclaringClass().getName()
               + "."
               + var1.getName()
               + " and "
               + var2.getDeclaringClass().getName()
               + "."
               + var2.getName()
         );
      }
   }

   public static class ExecutionException extends CommandLine.PicocliException {
      private static final long serialVersionUID = 7764539594267007998L;
      private final CommandLine commandLine;

      public ExecutionException(CommandLine var1, String var2) {
         super(var2);
         this.commandLine = CommandLine.Assert.notNull(var1, "commandLine");
      }

      public ExecutionException(CommandLine var1, String var2, Exception var3) {
         super(var2, var3);
         this.commandLine = CommandLine.Assert.notNull(var1, "commandLine");
      }

      public CommandLine getCommandLine() {
         return this.commandLine;
      }
   }

   public static class Help {
      protected static final String DEFAULT_COMMAND_NAME = "<main class>";
      protected static final String DEFAULT_SEPARATOR = "=";
      private static final int usageHelpWidth = 80;
      private static final int optionsColumnWidth = 29;
      private final Object command;
      private final Map<String, CommandLine.Help> commands = new LinkedHashMap<>();
      final CommandLine.Help.ColorScheme colorScheme;
      public final List<Field> optionFields;
      public final List<Field> positionalParametersFields;
      public String separator;
      public String commandName = "<main class>";
      public String[] description = new String[0];
      public String[] customSynopsis = new String[0];
      public String[] header = new String[0];
      public String[] footer = new String[0];
      public CommandLine.Help.IParamLabelRenderer parameterLabelRenderer;
      public Boolean abbreviateSynopsis;
      public Boolean sortOptions;
      public Boolean showDefaultValues;
      public Character requiredOptionMarker;
      public String headerHeading;
      public String synopsisHeading;
      public String descriptionHeading;
      public String parameterListHeading;
      public String optionListHeading;
      public String commandListHeading;
      public String footerHeading;

      public Help(Object var1) {
         this(var1, CommandLine.Help.Ansi.AUTO);
      }

      public Help(Object var1, CommandLine.Help.Ansi var2) {
         this(var1, defaultColorScheme(var2));
      }

      public Help(Object var1, CommandLine.Help.ColorScheme var2) {
         this.command = CommandLine.Assert.notNull(var1, "command");
         this.colorScheme = CommandLine.Assert.notNull(var2, "colorScheme").applySystemProperties();
         ArrayList var3 = new ArrayList();
         ArrayList var4 = new ArrayList();

         for (Class var5 = var1.getClass(); var5 != null; var5 = var5.getSuperclass()) {
            for (Field var9 : var5.getDeclaredFields()) {
               var9.setAccessible(true);
               if (var9.isAnnotationPresent(CommandLine.Option.class)) {
                  CommandLine.Option var10 = var9.getAnnotation(CommandLine.Option.class);
                  if (!var10.hidden()) {
                     var3.add(var9);
                  }
               }

               if (var9.isAnnotationPresent(CommandLine.Parameters.class)) {
                  var4.add(var9);
               }
            }

            if (var5.isAnnotationPresent(CommandLine.Command.class)) {
               CommandLine.Command var11 = var5.getAnnotation(CommandLine.Command.class);
               if ("<main class>".equals(this.commandName)) {
                  this.commandName = var11.name();
               }

               this.separator = this.separator == null ? var11.separator() : this.separator;
               this.abbreviateSynopsis = this.abbreviateSynopsis == null ? var11.abbreviateSynopsis() : this.abbreviateSynopsis;
               this.sortOptions = this.sortOptions == null ? var11.sortOptions() : this.sortOptions;
               this.requiredOptionMarker = this.requiredOptionMarker == null ? var11.requiredOptionMarker() : this.requiredOptionMarker;
               this.showDefaultValues = this.showDefaultValues == null ? var11.showDefaultValues() : this.showDefaultValues;
               this.customSynopsis = CommandLine.empty(this.customSynopsis) ? var11.customSynopsis() : this.customSynopsis;
               this.description = CommandLine.empty(this.description) ? var11.description() : this.description;
               this.header = CommandLine.empty(this.header) ? var11.header() : this.header;
               this.footer = CommandLine.empty(this.footer) ? var11.footer() : this.footer;
               this.headerHeading = CommandLine.empty(this.headerHeading) ? var11.headerHeading() : this.headerHeading;
               this.synopsisHeading = !CommandLine.empty(this.synopsisHeading) && !"Usage: ".equals(this.synopsisHeading)
                  ? this.synopsisHeading
                  : var11.synopsisHeading();
               this.descriptionHeading = CommandLine.empty(this.descriptionHeading) ? var11.descriptionHeading() : this.descriptionHeading;
               this.parameterListHeading = CommandLine.empty(this.parameterListHeading) ? var11.parameterListHeading() : this.parameterListHeading;
               this.optionListHeading = CommandLine.empty(this.optionListHeading) ? var11.optionListHeading() : this.optionListHeading;
               this.commandListHeading = !CommandLine.empty(this.commandListHeading) && !"Commands:%n".equals(this.commandListHeading)
                  ? this.commandListHeading
                  : var11.commandListHeading();
               this.footerHeading = CommandLine.empty(this.footerHeading) ? var11.footerHeading() : this.footerHeading;
            }
         }

         this.sortOptions = this.sortOptions == null ? true : this.sortOptions;
         this.abbreviateSynopsis = this.abbreviateSynopsis == null ? false : this.abbreviateSynopsis;
         this.requiredOptionMarker = this.requiredOptionMarker == null ? ' ' : this.requiredOptionMarker;
         this.showDefaultValues = this.showDefaultValues == null ? false : this.showDefaultValues;
         this.synopsisHeading = this.synopsisHeading == null ? "Usage: " : this.synopsisHeading;
         this.commandListHeading = this.commandListHeading == null ? "Commands:%n" : this.commandListHeading;
         this.separator = this.separator == null ? "=" : this.separator;
         this.parameterLabelRenderer = this.createDefaultParamLabelRenderer();
         Collections.sort(var4, new CommandLine.PositionalParametersSorter());
         this.positionalParametersFields = Collections.unmodifiableList(var4);
         this.optionFields = Collections.unmodifiableList(var3);
      }

      public CommandLine.Help addAllSubcommands(Map<String, CommandLine> var1) {
         if (var1 != null) {
            for (Entry var3 : var1.entrySet()) {
               this.addSubcommand((String)var3.getKey(), ((CommandLine)var3.getValue()).getCommand());
            }
         }

         return this;
      }

      public CommandLine.Help addSubcommand(String var1, Object var2) {
         this.commands.put(var1, new CommandLine.Help(var2));
         return this;
      }

      @Deprecated
      public String synopsis() {
         return this.synopsis(0);
      }

      public String synopsis(int var1) {
         if (!CommandLine.empty(this.customSynopsis)) {
            return this.customSynopsis();
         } else {
            return this.abbreviateSynopsis ? this.abbreviatedSynopsis() : this.detailedSynopsis(var1, createShortOptionArityAndNameComparator(), true);
         }
      }

      public String abbreviatedSynopsis() {
         StringBuilder var1 = new StringBuilder();
         if (!this.optionFields.isEmpty()) {
            var1.append(" [OPTIONS]");
         }

         for (Field var3 : this.positionalParametersFields) {
            if (!var3.getAnnotation(CommandLine.Parameters.class).hidden()) {
               var1.append(' ').append(this.parameterLabelRenderer.renderParameterLabel(var3, this.ansi(), this.colorScheme.parameterStyles));
            }
         }

         return this.colorScheme.commandText(this.commandName).toString() + var1.toString() + System.getProperty("line.separator");
      }

      @Deprecated
      public String detailedSynopsis(Comparator<Field> var1, boolean var2) {
         return this.detailedSynopsis(0, var1, var2);
      }

      public String detailedSynopsis(int var1, Comparator<Field> var2, boolean var3) {
         CommandLine.Help.Ansi.Text var4 = this.ansi().new Text(0);
         ArrayList var5 = new ArrayList<>(this.optionFields);
         if (var2 != null) {
            Collections.sort(var5, var2);
         }

         if (var3) {
            ArrayList var6 = new ArrayList();
            StringBuilder var7 = new StringBuilder("-");
            StringBuilder var8 = new StringBuilder("-");

            for (Field var10 : var5) {
               if (var10.getType() == boolean.class || var10.getType() == Boolean.class) {
                  CommandLine.Option var11 = var10.getAnnotation(CommandLine.Option.class);
                  String var12 = CommandLine.Help.ShortestFirst.sort(var11.names())[0];
                  if (var12.length() == 2 && var12.startsWith("-")) {
                     var6.add(var10);
                     if (var11.required()) {
                        var7.append(var12.substring(1));
                     } else {
                        var8.append(var12.substring(1));
                     }
                  }
               }
            }

            var5.removeAll(var6);
            if (var7.length() > 1) {
               var4 = var4.append(" ").append(this.colorScheme.optionText(var7.toString()));
            }

            if (var8.length() > 1) {
               var4 = var4.append(" [").append(this.colorScheme.optionText(var8.toString())).append("]");
            }
         }

         for (Field var17 : var5) {
            CommandLine.Option var20 = var17.getAnnotation(CommandLine.Option.class);
            if (!var20.hidden()) {
               if (var20.required()) {
                  var4 = this.appendOptionSynopsis(var4, var17, CommandLine.Help.ShortestFirst.sort(var20.names())[0], " ", "");
                  if (CommandLine.isMultiValue(var17)) {
                     var4 = this.appendOptionSynopsis(var4, var17, CommandLine.Help.ShortestFirst.sort(var20.names())[0], " [", "]...");
                  }
               } else {
                  var4 = this.appendOptionSynopsis(var4, var17, CommandLine.Help.ShortestFirst.sort(var20.names())[0], " [", "]");
                  if (CommandLine.isMultiValue(var17)) {
                     var4 = var4.append("...");
                  }
               }
            }
         }

         for (Field var18 : this.positionalParametersFields) {
            if (!var18.getAnnotation(CommandLine.Parameters.class).hidden()) {
               var4 = var4.append(" ");
               CommandLine.Help.Ansi.Text var21 = this.parameterLabelRenderer
                  .renderParameterLabel(var18, this.colorScheme.ansi(), this.colorScheme.parameterStyles);
               var4 = var4.append(var21);
            }
         }

         int var16 = this.commandName.length() + var1;
         CommandLine.Help.TextTable var19 = new CommandLine.Help.TextTable(this.ansi(), var16, 80 - var16);
         var19.indentWrappedLines = 1;
         CommandLine.Help.Ansi.Text var22 = CommandLine.Help.Ansi.OFF.new Text(stringOf('X', var1));
         var19.addRowValues(var22.append(this.colorScheme.commandText(this.commandName)), var4);
         return var19.toString().substring(var1);
      }

      private CommandLine.Help.Ansi.Text appendOptionSynopsis(CommandLine.Help.Ansi.Text var1, Field var2, String var3, String var4, String var5) {
         CommandLine.Help.Ansi.Text var6 = this.parameterLabelRenderer.renderParameterLabel(var2, this.colorScheme.ansi(), this.colorScheme.optionParamStyles);
         return var1.append(var4).append(this.colorScheme.optionText(var3)).append(var6).append(var5);
      }

      public int synopsisHeadingLength() {
         String[] var1 = CommandLine.Help.Ansi.OFF.new Text(this.synopsisHeading).toString().split("\\r?\\n|\\r|%n", -1);
         return var1[var1.length - 1].length();
      }

      public String optionList() {
         Comparator var1 = this.sortOptions != null && !this.sortOptions ? null : createShortOptionNameComparator();
         return this.optionList(this.createDefaultLayout(), var1, this.parameterLabelRenderer);
      }

      public String optionList(CommandLine.Help.Layout var1, Comparator<Field> var2, CommandLine.Help.IParamLabelRenderer var3) {
         ArrayList var4 = new ArrayList<>(this.optionFields);
         if (var2 != null) {
            Collections.sort(var4, var2);
         }

         var1.addOptions(var4, var3);
         return var1.toString();
      }

      public String parameterList() {
         return this.parameterList(this.createDefaultLayout(), this.parameterLabelRenderer);
      }

      public String parameterList(CommandLine.Help.Layout var1, CommandLine.Help.IParamLabelRenderer var2) {
         var1.addPositionalParameters(this.positionalParametersFields, var2);
         return var1.toString();
      }

      private static String heading(CommandLine.Help.Ansi var0, String var1, Object... var2) {
         StringBuilder var3 = join(var0, new String[]{var1}, new StringBuilder(), var2);
         String var4 = var3.toString();
         var4 = var4.endsWith(System.getProperty("line.separator")) ? var4.substring(0, var4.length() - System.getProperty("line.separator").length()) : var4;
         return var4 + new String(spaces(countTrailingSpaces(var1)));
      }

      private static char[] spaces(int var0) {
         char[] var1 = new char[var0];
         Arrays.fill(var1, ' ');
         return var1;
      }

      private static int countTrailingSpaces(String var0) {
         if (var0 == null) {
            return 0;
         } else {
            int var1 = 0;

            for (int var2 = var0.length() - 1; var2 >= 0 && var0.charAt(var2) == ' '; var2--) {
               var1++;
            }

            return var1;
         }
      }

      public static StringBuilder join(CommandLine.Help.Ansi var0, String[] var1, StringBuilder var2, Object... var3) {
         if (var1 != null) {
            CommandLine.Help.TextTable var4 = new CommandLine.Help.TextTable(var0, 80);
            var4.indentWrappedLines = 0;

            for (String var8 : var1) {
               CommandLine.Help.Ansi.Text[] var9 = var0.new Text(format(var8, var3)).splitLines();

               for (CommandLine.Help.Ansi.Text var13 : var9) {
                  var4.addRowValues(var13);
               }
            }

            var4.toString(var2);
         }

         return var2;
      }

      private static String format(String var0, Object... var1) {
         return var0 == null ? "" : String.format(var0, var1);
      }

      public String customSynopsis(Object... var1) {
         return join(this.ansi(), this.customSynopsis, new StringBuilder(), var1).toString();
      }

      public String description(Object... var1) {
         return join(this.ansi(), this.description, new StringBuilder(), var1).toString();
      }

      public String header(Object... var1) {
         return join(this.ansi(), this.header, new StringBuilder(), var1).toString();
      }

      public String footer(Object... var1) {
         return join(this.ansi(), this.footer, new StringBuilder(), var1).toString();
      }

      public String headerHeading(Object... var1) {
         return heading(this.ansi(), this.headerHeading, var1);
      }

      public String synopsisHeading(Object... var1) {
         return heading(this.ansi(), this.synopsisHeading, var1);
      }

      public String descriptionHeading(Object... var1) {
         return CommandLine.empty(this.descriptionHeading) ? "" : heading(this.ansi(), this.descriptionHeading, var1);
      }

      public String parameterListHeading(Object... var1) {
         return this.positionalParametersFields.isEmpty() ? "" : heading(this.ansi(), this.parameterListHeading, var1);
      }

      public String optionListHeading(Object... var1) {
         return this.optionFields.isEmpty() ? "" : heading(this.ansi(), this.optionListHeading, var1);
      }

      public String commandListHeading(Object... var1) {
         return this.commands.isEmpty() ? "" : heading(this.ansi(), this.commandListHeading, var1);
      }

      public String footerHeading(Object... var1) {
         return heading(this.ansi(), this.footerHeading, var1);
      }

      public String commandList() {
         if (this.commands.isEmpty()) {
            return "";
         } else {
            int var1 = maxLength(this.commands.keySet());
            CommandLine.Help.TextTable var2 = new CommandLine.Help.TextTable(
               this.ansi(),
               new CommandLine.Help.Column(var1 + 2, 2, CommandLine.Help.Column.Overflow.SPAN),
               new CommandLine.Help.Column(80 - (var1 + 2), 2, CommandLine.Help.Column.Overflow.WRAP)
            );

            for (Entry var4 : this.commands.entrySet()) {
               CommandLine.Help var5 = (CommandLine.Help)var4.getValue();
               String var6 = var5.header != null && var5.header.length > 0
                  ? var5.header[0]
                  : (var5.description != null && var5.description.length > 0 ? var5.description[0] : "");
               var2.addRowValues(this.colorScheme.commandText((String)var4.getKey()), this.ansi().new Text(var6));
            }

            return var2.toString();
         }
      }

      private static int maxLength(Collection<String> var0) {
         ArrayList var1 = new ArrayList(var0);
         Collections.sort(var1, Collections.reverseOrder(shortestFirst()));
         return ((String)var1.get(0)).length();
      }

      private static String join(String[] var0, int var1, int var2, String var3) {
         if (var0 == null) {
            return "";
         } else {
            StringBuilder var4 = new StringBuilder();

            for (int var5 = var1; var5 < var1 + var2; var5++) {
               var4.append(var5 > var1 ? var3 : "").append(var0[var5]);
            }

            return var4.toString();
         }
      }

      private static String stringOf(char var0, int var1) {
         char[] var2 = new char[var1];
         Arrays.fill(var2, var0);
         return new String(var2);
      }

      public CommandLine.Help.Layout createDefaultLayout() {
         return new CommandLine.Help.Layout(
            this.colorScheme,
            new CommandLine.Help.TextTable(this.colorScheme.ansi()),
            this.createDefaultOptionRenderer(),
            this.createDefaultParameterRenderer()
         );
      }

      public CommandLine.Help.IOptionRenderer createDefaultOptionRenderer() {
         CommandLine.Help.DefaultOptionRenderer var1 = new CommandLine.Help.DefaultOptionRenderer();
         var1.requiredMarker = String.valueOf(this.requiredOptionMarker);
         if (this.showDefaultValues != null && this.showDefaultValues) {
            var1.command = this.command;
         }

         return var1;
      }

      public static CommandLine.Help.IOptionRenderer createMinimalOptionRenderer() {
         return new CommandLine.Help.MinimalOptionRenderer();
      }

      public CommandLine.Help.IParameterRenderer createDefaultParameterRenderer() {
         CommandLine.Help.DefaultParameterRenderer var1 = new CommandLine.Help.DefaultParameterRenderer();
         var1.requiredMarker = String.valueOf(this.requiredOptionMarker);
         return var1;
      }

      public static CommandLine.Help.IParameterRenderer createMinimalParameterRenderer() {
         return new CommandLine.Help.MinimalParameterRenderer();
      }

      public static CommandLine.Help.IParamLabelRenderer createMinimalParamLabelRenderer() {
         return new CommandLine.Help.IParamLabelRenderer() {
            @Override
            public CommandLine.Help.Ansi.Text renderParameterLabel(Field var1, CommandLine.Help.Ansi var2, List<CommandLine.Help.Ansi.IStyle> var3) {
               String var4 = CommandLine.Help.DefaultParamLabelRenderer.renderParameterName(var1);
               return var2.apply(var4, var3);
            }

            @Override
            public String separator() {
               return "";
            }
         };
      }

      public CommandLine.Help.IParamLabelRenderer createDefaultParamLabelRenderer() {
         return new CommandLine.Help.DefaultParamLabelRenderer(this.separator);
      }

      public static Comparator<Field> createShortOptionNameComparator() {
         return new CommandLine.Help.SortByShortestOptionNameAlphabetically();
      }

      public static Comparator<Field> createShortOptionArityAndNameComparator() {
         return new CommandLine.Help.SortByOptionArityAndNameAlphabetically();
      }

      public static Comparator<String> shortestFirst() {
         return new CommandLine.Help.ShortestFirst();
      }

      public CommandLine.Help.Ansi ansi() {
         return this.colorScheme.ansi;
      }

      public static CommandLine.Help.ColorScheme defaultColorScheme(CommandLine.Help.Ansi var0) {
         return new CommandLine.Help.ColorScheme(var0)
            .commands(CommandLine.Help.Ansi.Style.bold)
            .options(CommandLine.Help.Ansi.Style.fg_yellow)
            .parameters(CommandLine.Help.Ansi.Style.fg_yellow)
            .optionParams(CommandLine.Help.Ansi.Style.italic);
      }

      public static enum Ansi {
         AUTO,
         ON,
         OFF;

         static CommandLine.Help.Ansi.Text EMPTY_TEXT = OFF.new Text(0);
         static final boolean isWindows = System.getProperty("os.name").startsWith("Windows");
         static final boolean isXterm = System.getenv("TERM") != null && System.getenv("TERM").startsWith("xterm");
         static final boolean ISATTY = calcTTY();

         static final boolean calcTTY() {
            if (isWindows && isXterm) {
               return true;
            } else {
               try {
                  return System.class.getDeclaredMethod("console").invoke(null) != null;
               } catch (Throwable var1) {
                  return true;
               }
            }
         }

         private static boolean ansiPossible() {
            return ISATTY && (!isWindows || isXterm);
         }

         public boolean enabled() {
            if (this == ON) {
               return true;
            } else if (this == OFF) {
               return false;
            } else {
               return System.getProperty("picocli.ansi") == null ? ansiPossible() : Boolean.getBoolean("picocli.ansi");
            }
         }

         public CommandLine.Help.Ansi.Text apply(String var1, List<CommandLine.Help.Ansi.IStyle> var2) {
            if (var1.length() == 0) {
               return new CommandLine.Help.Ansi.Text(0);
            } else {
               CommandLine.Help.Ansi.Text var3 = new CommandLine.Help.Ansi.Text(var1.length());
               CommandLine.Help.Ansi.IStyle[] var4 = var2.toArray(new CommandLine.Help.Ansi.IStyle[var2.size()]);
               var3.sections
                  .add(
                     new CommandLine.Help.Ansi.StyledSection(
                        0,
                        var1.length(),
                        CommandLine.Help.Ansi.Style.on(var4),
                        CommandLine.Help.Ansi.Style.off(reverse(var4)) + CommandLine.Help.Ansi.Style.reset.off()
                     )
                  );
               var3.plain.append(var1);
               var3.length = var3.plain.length();
               return var3;
            }
         }

         private static <T> T[] reverse(T[] var0) {
            for (int var1 = 0; var1 < var0.length / 2; var1++) {
               Object var2 = var0[var1];
               var0[var1] = var0[var0.length - var1 - 1];
               var0[var0.length - var1 - 1] = var2;
            }

            return (T[])var0;
         }

         public interface IStyle {
            String CSI = "\u001b[";

            String on();

            String off();
         }

         static class Palette256Color implements CommandLine.Help.Ansi.IStyle {
            private final int fgbg;
            private final int color;

            Palette256Color(boolean var1, String var2) {
               this.fgbg = var1 ? 38 : 48;
               String[] var3 = var2.split(";");
               if (var3.length == 3) {
                  this.color = 16 + 36 * Integer.decode(var3[0]) + 6 * Integer.decode(var3[1]) + Integer.decode(var3[2]);
               } else {
                  this.color = Integer.decode(var2);
               }
            }

            @Override
            public String on() {
               return String.format("\u001b[%d;5;%dm", this.fgbg, this.color);
            }

            @Override
            public String off() {
               return "\u001b[" + (this.fgbg + 1) + "m";
            }
         }

         public static enum Style implements CommandLine.Help.Ansi.IStyle {
            reset(0, 0),
            bold(1, 21),
            faint(2, 22),
            italic(3, 23),
            underline(4, 24),
            blink(5, 25),
            reverse(7, 27),
            fg_black(30, 39),
            fg_red(31, 39),
            fg_green(32, 39),
            fg_yellow(33, 39),
            fg_blue(34, 39),
            fg_magenta(35, 39),
            fg_cyan(36, 39),
            fg_white(37, 39),
            bg_black(40, 49),
            bg_red(41, 49),
            bg_green(42, 49),
            bg_yellow(43, 49),
            bg_blue(44, 49),
            bg_magenta(45, 49),
            bg_cyan(46, 49),
            bg_white(47, 49);

            private final int startCode;
            private final int endCode;

            private Style(int var3, int var4) {
               this.startCode = var3;
               this.endCode = var4;
            }

            @Override
            public String on() {
               return "\u001b[" + this.startCode + "m";
            }

            @Override
            public String off() {
               return "\u001b[" + this.endCode + "m";
            }

            public static String on(CommandLine.Help.Ansi.IStyle... var0) {
               StringBuilder var1 = new StringBuilder();

               for (CommandLine.Help.Ansi.IStyle var5 : var0) {
                  var1.append(var5.on());
               }

               return var1.toString();
            }

            public static String off(CommandLine.Help.Ansi.IStyle... var0) {
               StringBuilder var1 = new StringBuilder();

               for (CommandLine.Help.Ansi.IStyle var5 : var0) {
                  var1.append(var5.off());
               }

               return var1.toString();
            }

            public static CommandLine.Help.Ansi.IStyle fg(String var0) {
               try {
                  return valueOf(var0.toLowerCase(Locale.ENGLISH));
               } catch (Exception var3) {
                  try {
                     return valueOf("fg_" + var0.toLowerCase(Locale.ENGLISH));
                  } catch (Exception var2) {
                     return new CommandLine.Help.Ansi.Palette256Color(true, var0);
                  }
               }
            }

            public static CommandLine.Help.Ansi.IStyle bg(String var0) {
               try {
                  return valueOf(var0.toLowerCase(Locale.ENGLISH));
               } catch (Exception var3) {
                  try {
                     return valueOf("bg_" + var0.toLowerCase(Locale.ENGLISH));
                  } catch (Exception var2) {
                     return new CommandLine.Help.Ansi.Palette256Color(false, var0);
                  }
               }
            }

            public static CommandLine.Help.Ansi.IStyle[] parse(String var0) {
               String[] var1 = var0.split(",");
               CommandLine.Help.Ansi.IStyle[] var2 = new CommandLine.Help.Ansi.IStyle[var1.length];

               for (int var3 = 0; var3 < var1.length; var3++) {
                  if (var1[var3].toLowerCase(Locale.ENGLISH).startsWith("fg(")) {
                     int var4 = var1[var3].indexOf(41);
                     var2[var3] = fg(var1[var3].substring(3, var4 < 0 ? var1[var3].length() : var4));
                  } else if (var1[var3].toLowerCase(Locale.ENGLISH).startsWith("bg(")) {
                     int var5 = var1[var3].indexOf(41);
                     var2[var3] = bg(var1[var3].substring(3, var5 < 0 ? var1[var3].length() : var5));
                  } else {
                     var2[var3] = fg(var1[var3]);
                  }
               }

               return var2;
            }
         }

         private static class StyledSection {
            int startIndex;
            int length;
            String startStyles;
            String endStyles;

            StyledSection(int var1, int var2, String var3, String var4) {
               this.startIndex = var1;
               this.length = var2;
               this.startStyles = var3;
               this.endStyles = var4;
            }

            CommandLine.Help.Ansi.StyledSection withStartIndex(int var1) {
               return new CommandLine.Help.Ansi.StyledSection(var1, this.length, this.startStyles, this.endStyles);
            }
         }

         public class Text implements Cloneable {
            private final int maxLength;
            private int from;
            private int length;
            private StringBuilder plain = new StringBuilder();
            private List<CommandLine.Help.Ansi.StyledSection> sections = new ArrayList<>();

            public Text(int var2) {
               this.maxLength = var2;
            }

            public Text(String var2) {
               this.maxLength = -1;
               this.plain.setLength(0);
               int var3 = 0;

               while (true) {
                  int var4 = var2.indexOf("@|", var3);
                  if (var4 == -1) {
                     if (var3 == 0) {
                        this.plain.append(var2);
                        this.length = this.plain.length();
                        return;
                     }

                     this.plain.append(var2.substring(var3, var2.length()));
                     this.length = this.plain.length();
                     return;
                  }

                  this.plain.append(var2.substring(var3, var4));
                  int var5 = var2.indexOf("|@", var4);
                  if (var5 == -1) {
                     this.plain.append(var2);
                     this.length = this.plain.length();
                     return;
                  }

                  var4 += 2;
                  String var6 = var2.substring(var4, var5);
                  String[] var7 = var6.split(" ", 2);
                  if (var7.length == 1) {
                     this.plain.append(var2);
                     this.length = this.plain.length();
                     return;
                  }

                  CommandLine.Help.Ansi.IStyle[] var8 = CommandLine.Help.Ansi.Style.parse(var7[0]);
                  this.addStyledSection(
                     this.plain.length(),
                     var7[1].length(),
                     CommandLine.Help.Ansi.Style.on(var8),
                     CommandLine.Help.Ansi.Style.off(CommandLine.Help.Ansi.reverse(var8)) + CommandLine.Help.Ansi.Style.reset.off()
                  );
                  this.plain.append(var7[1]);
                  var3 = var5 + 2;
               }
            }

            private void addStyledSection(int var1, int var2, String var3, String var4) {
               this.sections.add(new CommandLine.Help.Ansi.StyledSection(var1, var2, var3, var4));
            }

            @Override
            public Object clone() {
               try {
                  return super.clone();
               } catch (CloneNotSupportedException var2) {
                  throw new IllegalStateException(var2);
               }
            }

            public CommandLine.Help.Ansi.Text[] splitLines() {
               ArrayList var1 = new ArrayList();
               boolean var2 = false;
               int var3 = 0;
               int var4 = 0;

               for (int var5 = 0; var5 < this.plain.length(); var4 = ++var5) {
                  char var6;
                  boolean var7;
                  boolean var10001;
                  label48: {
                     var6 = this.plain.charAt(var5);
                     var7 = var6 == '\n';
                     if (var6 == '\r' && var5 + 1 < this.plain.length() && this.plain.charAt(var5 + 1) == '\n') {
                        if (++var5 > 0) {
                           var10001 = true;
                           break label48;
                        }
                     }

                     var10001 = false;
                  }

                  var7 |= var10001;
                  var7 |= var6 == '\r';
                  if (var7) {
                     var1.add(this.substring(var3, var4));
                     var2 = var5 == this.plain.length() - 1;
                     var3 = var5 + 1;
                  }
               }

               if (var3 < this.plain.length() || var2) {
                  var1.add(this.substring(var3, this.plain.length()));
               }

               return var1.toArray(new CommandLine.Help.Ansi.Text[var1.size()]);
            }

            public CommandLine.Help.Ansi.Text substring(int var1) {
               return this.substring(var1, this.length);
            }

            public CommandLine.Help.Ansi.Text substring(int var1, int var2) {
               CommandLine.Help.Ansi.Text var3 = (CommandLine.Help.Ansi.Text)this.clone();
               var3.from = this.from + var1;
               var3.length = var2 - var1;
               return var3;
            }

            public CommandLine.Help.Ansi.Text append(String var1) {
               return this.append(Ansi.this.new Text(var1));
            }

            public CommandLine.Help.Ansi.Text append(CommandLine.Help.Ansi.Text var1) {
               CommandLine.Help.Ansi.Text var2 = (CommandLine.Help.Ansi.Text)this.clone();
               var2.plain = new StringBuilder(this.plain.toString().substring(this.from, this.from + this.length));
               var2.from = 0;
               var2.sections = new ArrayList<>();

               for (CommandLine.Help.Ansi.StyledSection var4 : this.sections) {
                  var2.sections.add(var4.withStartIndex(var4.startIndex - this.from));
               }

               var2.plain.append(var1.plain.toString().substring(var1.from, var1.from + var1.length));

               for (CommandLine.Help.Ansi.StyledSection var7 : var1.sections) {
                  int var5 = var2.length + var7.startIndex - var1.from;
                  var2.sections.add(var7.withStartIndex(var5));
               }

               var2.length = var2.plain.length();
               return var2;
            }

            public void getStyledChars(int var1, int var2, CommandLine.Help.Ansi.Text var3, int var4) {
               if (var3.length < var4) {
                  for (int var5 = var3.length; var5 < var4; var5++) {
                     var3.plain.append(' ');
                  }

                  var3.length = var4;
               }

               for (CommandLine.Help.Ansi.StyledSection var6 : this.sections) {
                  var3.sections.add(var6.withStartIndex(var6.startIndex - var1 + var3.length));
               }

               var3.plain.append(this.plain.toString().substring(var1, var1 + var2));
               var3.length = var3.plain.length();
            }

            public String plainString() {
               return this.plain.toString().substring(this.from, this.from + this.length);
            }

            @Override
            public boolean equals(Object var1) {
               return this.toString().equals(String.valueOf(var1));
            }

            @Override
            public int hashCode() {
               return this.toString().hashCode();
            }

            @Override
            public String toString() {
               if (!Ansi.this.enabled()) {
                  return this.plain.toString().substring(this.from, this.from + this.length);
               } else if (this.length == 0) {
                  return "";
               } else {
                  StringBuilder var1 = new StringBuilder(this.plain.length() + 20 * this.sections.size());
                  CommandLine.Help.Ansi.StyledSection var2 = null;
                  int var3 = Math.min(this.from + this.length, this.plain.length());

                  for (int var4 = this.from; var4 < var3; var4++) {
                     CommandLine.Help.Ansi.StyledSection var5 = this.findSectionContaining(var4);
                     if (var5 != var2) {
                        if (var2 != null) {
                           var1.append(var2.endStyles);
                        }

                        if (var5 != null) {
                           var1.append(var5.startStyles);
                        }

                        var2 = var5;
                     }

                     var1.append(this.plain.charAt(var4));
                  }

                  if (var2 != null) {
                     var1.append(var2.endStyles);
                  }

                  return var1.toString();
               }
            }

            private CommandLine.Help.Ansi.StyledSection findSectionContaining(int var1) {
               for (CommandLine.Help.Ansi.StyledSection var3 : this.sections) {
                  if (var1 >= var3.startIndex && var1 < var3.startIndex + var3.length) {
                     return var3;
                  }
               }

               return null;
            }
         }
      }

      public static class ColorScheme {
         public final List<CommandLine.Help.Ansi.IStyle> commandStyles = new ArrayList<>();
         public final List<CommandLine.Help.Ansi.IStyle> optionStyles = new ArrayList<>();
         public final List<CommandLine.Help.Ansi.IStyle> parameterStyles = new ArrayList<>();
         public final List<CommandLine.Help.Ansi.IStyle> optionParamStyles = new ArrayList<>();
         private final CommandLine.Help.Ansi ansi;

         public ColorScheme() {
            this(CommandLine.Help.Ansi.AUTO);
         }

         public ColorScheme(CommandLine.Help.Ansi var1) {
            this.ansi = CommandLine.Assert.notNull(var1, "ansi");
         }

         public CommandLine.Help.ColorScheme commands(CommandLine.Help.Ansi.IStyle... var1) {
            return this.addAll(this.commandStyles, var1);
         }

         public CommandLine.Help.ColorScheme options(CommandLine.Help.Ansi.IStyle... var1) {
            return this.addAll(this.optionStyles, var1);
         }

         public CommandLine.Help.ColorScheme parameters(CommandLine.Help.Ansi.IStyle... var1) {
            return this.addAll(this.parameterStyles, var1);
         }

         public CommandLine.Help.ColorScheme optionParams(CommandLine.Help.Ansi.IStyle... var1) {
            return this.addAll(this.optionParamStyles, var1);
         }

         public CommandLine.Help.Ansi.Text commandText(String var1) {
            return this.ansi().apply(var1, this.commandStyles);
         }

         public CommandLine.Help.Ansi.Text optionText(String var1) {
            return this.ansi().apply(var1, this.optionStyles);
         }

         public CommandLine.Help.Ansi.Text parameterText(String var1) {
            return this.ansi().apply(var1, this.parameterStyles);
         }

         public CommandLine.Help.Ansi.Text optionParamText(String var1) {
            return this.ansi().apply(var1, this.optionParamStyles);
         }

         public CommandLine.Help.ColorScheme applySystemProperties() {
            this.replace(this.commandStyles, System.getProperty("picocli.color.commands"));
            this.replace(this.optionStyles, System.getProperty("picocli.color.options"));
            this.replace(this.parameterStyles, System.getProperty("picocli.color.parameters"));
            this.replace(this.optionParamStyles, System.getProperty("picocli.color.optionParams"));
            return this;
         }

         private void replace(List<CommandLine.Help.Ansi.IStyle> var1, String var2) {
            if (var2 != null) {
               var1.clear();
               this.addAll(var1, CommandLine.Help.Ansi.Style.parse(var2));
            }
         }

         private CommandLine.Help.ColorScheme addAll(List<CommandLine.Help.Ansi.IStyle> var1, CommandLine.Help.Ansi.IStyle... var2) {
            var1.addAll(Arrays.asList(var2));
            return this;
         }

         public CommandLine.Help.Ansi ansi() {
            return this.ansi;
         }
      }

      public static class Column {
         public final int width;
         public final int indent;
         public final CommandLine.Help.Column.Overflow overflow;

         public Column(int var1, int var2, CommandLine.Help.Column.Overflow var3) {
            this.width = var1;
            this.indent = var2;
            this.overflow = CommandLine.Assert.notNull(var3, "overflow");
         }

         public static enum Overflow {
            TRUNCATE,
            SPAN,
            WRAP;
         }
      }

      static class DefaultOptionRenderer implements CommandLine.Help.IOptionRenderer {
         public String requiredMarker = " ";
         public Object command;
         private String sep;
         private boolean showDefault;

         @Override
         public CommandLine.Help.Ansi.Text[][] render(
            CommandLine.Option var1, Field var2, CommandLine.Help.IParamLabelRenderer var3, CommandLine.Help.ColorScheme var4
         ) {
            String[] var5 = CommandLine.Help.ShortestFirst.sort(var1.names());
            int var6 = var5[0].length() == 2 ? 1 : 0;
            String var7 = var6 > 0 ? var5[0] : "";
            this.sep = var6 > 0 && var5.length > 1 ? "," : "";
            String var8 = CommandLine.Help.join(var5, var6, var5.length - var6, ", ");
            CommandLine.Help.Ansi.Text var9 = this.createLongOptionText(var2, var3, var4, var8);
            this.showDefault = this.command != null && !var1.help() && !CommandLine.isBoolean(var2.getType());
            Object var10 = this.createDefaultValue(var2);
            String var11 = var1.required() ? this.requiredMarker : "";
            return this.renderDescriptionLines(var1, var4, var11, var7, var9, var10);
         }

         private Object createDefaultValue(Field var1) {
            Object var2 = null;

            try {
               var2 = var1.get(this.command);
               if (var2 == null) {
                  this.showDefault = false;
               } else if (var1.getType().isArray()) {
                  StringBuilder var3 = new StringBuilder();

                  for (int var4 = 0; var4 < Array.getLength(var2); var4++) {
                     var3.append(var4 > 0 ? ", " : "").append(Array.get(var2, var4));
                  }

                  var2 = var3.insert(0, "[").append("]").toString();
               }
            } catch (Exception var5) {
               this.showDefault = false;
            }

            return var2;
         }

         private CommandLine.Help.Ansi.Text createLongOptionText(
            Field var1, CommandLine.Help.IParamLabelRenderer var2, CommandLine.Help.ColorScheme var3, String var4
         ) {
            CommandLine.Help.Ansi.Text var5 = var2.renderParameterLabel(var1, var3.ansi(), var3.optionParamStyles);
            if (var5.length > 0 && var4.length() == 0) {
               this.sep = var2.separator();
               int var6 = var5.plainString().indexOf(this.sep);
               CommandLine.Help.Ansi.Text var7 = var5.substring(0, var6);
               var5 = var7.append(var5.substring(var6 + this.sep.length()));
            }

            CommandLine.Help.Ansi.Text var8 = var3.optionText(var4);
            return var8.append(var5);
         }

         private CommandLine.Help.Ansi.Text[][] renderDescriptionLines(
            CommandLine.Option var1, CommandLine.Help.ColorScheme var2, String var3, String var4, CommandLine.Help.Ansi.Text var5, Object var6
         ) {
            CommandLine.Help.Ansi.Text var7 = CommandLine.Help.Ansi.EMPTY_TEXT;
            ArrayList var8 = new ArrayList();
            CommandLine.Help.Ansi.Text[] var9 = var2.ansi().new Text(CommandLine.str(var1.description(), 0)).splitLines();
            if (var9.length == 0) {
               if (this.showDefault) {
                  var9 = new CommandLine.Help.Ansi.Text[]{var2.ansi().new Text("  Default: " + var6)};
                  this.showDefault = false;
               } else {
                  var9 = new CommandLine.Help.Ansi.Text[]{var7};
               }
            }

            var8.add(new CommandLine.Help.Ansi.Text[]{var2.optionText(var3), var2.optionText(var4), var2.ansi().new Text(this.sep), var5, var9[0]});

            for (int var10 = 1; var10 < var9.length; var10++) {
               var8.add(new CommandLine.Help.Ansi.Text[]{var7, var7, var7, var7, var9[var10]});
            }

            for (int var16 = 1; var16 < var1.description().length; var16++) {
               CommandLine.Help.Ansi.Text[] var11 = var2.ansi().new Text(var1.description()[var16]).splitLines();

               for (CommandLine.Help.Ansi.Text var15 : var11) {
                  var8.add(new CommandLine.Help.Ansi.Text[]{var7, var7, var7, var7, var15});
               }
            }

            if (this.showDefault) {
               var8.add(new CommandLine.Help.Ansi.Text[]{var7, var7, var7, var7, var2.ansi().new Text("  Default: " + var6)});
            }

            return var8.toArray(new CommandLine.Help.Ansi.Text[var8.size()][]);
         }
      }

      static class DefaultParamLabelRenderer implements CommandLine.Help.IParamLabelRenderer {
         public final String separator;

         public DefaultParamLabelRenderer(String var1) {
            this.separator = CommandLine.Assert.notNull(var1, "separator");
         }

         @Override
         public String separator() {
            return this.separator;
         }

         @Override
         public CommandLine.Help.Ansi.Text renderParameterLabel(Field var1, CommandLine.Help.Ansi var2, List<CommandLine.Help.Ansi.IStyle> var3) {
            boolean var4 = var1.isAnnotationPresent(CommandLine.Option.class);
            CommandLine.Range var5 = var4 ? CommandLine.Range.optionArity(var1) : CommandLine.Range.parameterCapacity(var1);
            String var6 = var4 ? var1.getAnnotation(CommandLine.Option.class).split() : var1.getAnnotation(CommandLine.Parameters.class).split();
            CommandLine.Help.Ansi.Text var7 = var2.new Text("");
            String var8 = var4 ? this.separator : "";
            CommandLine.Help.Ansi.Text var9 = var2.apply(renderParameterName(var1), var3);
            if (!CommandLine.empty(var6)) {
               var9 = var9.append("[" + var6).append(var9).append("]...");
            }

            for (int var10 = 0; var10 < var5.min; var10++) {
               var7 = var7.append(var8).append(var9);
               var8 = " ";
            }

            if (var5.isVariable) {
               if (var7.length == 0) {
                  var7 = var7.append(var8 + "[").append(var9).append("]...");
               } else if (!var7.plainString().endsWith("...")) {
                  var7 = var7.append("...");
               }
            } else {
               var8 = var7.length == 0 ? (var4 ? this.separator : "") : " ";

               for (int var12 = var5.min; var12 < var5.max; var12++) {
                  if (var8.trim().length() == 0) {
                     var7 = var7.append(var8 + "[").append(var9);
                  } else {
                     var7 = var7.append("[" + var8).append(var9);
                  }

                  var8 = " ";
               }

               for (int var13 = var5.min; var13 < var5.max; var13++) {
                  var7 = var7.append("]");
               }
            }

            return var7;
         }

         private static String renderParameterName(Field var0) {
            String var1 = null;
            if (var0.isAnnotationPresent(CommandLine.Option.class)) {
               var1 = var0.getAnnotation(CommandLine.Option.class).paramLabel();
            } else if (var0.isAnnotationPresent(CommandLine.Parameters.class)) {
               var1 = var0.getAnnotation(CommandLine.Parameters.class).paramLabel();
            }

            if (var1 != null && var1.trim().length() > 0) {
               return var1.trim();
            } else {
               String var2 = var0.getName();
               if (Map.class.isAssignableFrom(var0.getType())) {
                  Class[] var3 = CommandLine.getTypeAttribute(var0);
                  if (var3.length >= 2 && var3[0] != null && var3[1] != null) {
                     var2 = var3[0].getSimpleName() + "=" + var3[1].getSimpleName();
                  } else {
                     var2 = "String=String";
                  }
               }

               return "<" + var2 + ">";
            }
         }
      }

      static class DefaultParameterRenderer implements CommandLine.Help.IParameterRenderer {
         public String requiredMarker = " ";

         @Override
         public CommandLine.Help.Ansi.Text[][] render(
            CommandLine.Parameters var1, Field var2, CommandLine.Help.IParamLabelRenderer var3, CommandLine.Help.ColorScheme var4
         ) {
            CommandLine.Help.Ansi.Text var5 = var3.renderParameterLabel(var2, var4.ansi(), var4.parameterStyles);
            CommandLine.Help.Ansi.Text var6 = var4.parameterText(CommandLine.Range.parameterArity(var2).min > 0 ? this.requiredMarker : "");
            CommandLine.Help.Ansi.Text var7 = CommandLine.Help.Ansi.EMPTY_TEXT;
            ArrayList var8 = new ArrayList();
            CommandLine.Help.Ansi.Text[] var9 = var4.ansi().new Text(CommandLine.str(var1.description(), 0)).splitLines();
            if (var9.length == 0) {
               var9 = new CommandLine.Help.Ansi.Text[]{var7};
            }

            var8.add(new CommandLine.Help.Ansi.Text[]{var6, var7, var7, var5, var9[0]});

            for (int var10 = 1; var10 < var9.length; var10++) {
               var8.add(new CommandLine.Help.Ansi.Text[]{var7, var7, var7, var7, var9[var10]});
            }

            for (int var16 = 1; var16 < var1.description().length; var16++) {
               CommandLine.Help.Ansi.Text[] var11 = var4.ansi().new Text(var1.description()[var16]).splitLines();

               for (CommandLine.Help.Ansi.Text var15 : var11) {
                  var8.add(new CommandLine.Help.Ansi.Text[]{var7, var7, var7, var7, var15});
               }
            }

            return var8.toArray(new CommandLine.Help.Ansi.Text[var8.size()][]);
         }
      }

      public interface IOptionRenderer {
         CommandLine.Help.Ansi.Text[][] render(
            CommandLine.Option var1, Field var2, CommandLine.Help.IParamLabelRenderer var3, CommandLine.Help.ColorScheme var4
         );
      }

      public interface IParamLabelRenderer {
         CommandLine.Help.Ansi.Text renderParameterLabel(Field var1, CommandLine.Help.Ansi var2, List<CommandLine.Help.Ansi.IStyle> var3);

         String separator();
      }

      public interface IParameterRenderer {
         CommandLine.Help.Ansi.Text[][] render(
            CommandLine.Parameters var1, Field var2, CommandLine.Help.IParamLabelRenderer var3, CommandLine.Help.ColorScheme var4
         );
      }

      public static class Layout {
         protected final CommandLine.Help.ColorScheme colorScheme;
         protected final CommandLine.Help.TextTable table;
         protected CommandLine.Help.IOptionRenderer optionRenderer;
         protected CommandLine.Help.IParameterRenderer parameterRenderer;

         public Layout(CommandLine.Help.ColorScheme var1) {
            this(var1, new CommandLine.Help.TextTable(var1.ansi()));
         }

         public Layout(CommandLine.Help.ColorScheme var1, CommandLine.Help.TextTable var2) {
            this(var1, var2, new CommandLine.Help.DefaultOptionRenderer(), new CommandLine.Help.DefaultParameterRenderer());
         }

         public Layout(
            CommandLine.Help.ColorScheme var1, CommandLine.Help.TextTable var2, CommandLine.Help.IOptionRenderer var3, CommandLine.Help.IParameterRenderer var4
         ) {
            this.colorScheme = CommandLine.Assert.notNull(var1, "colorScheme");
            this.table = CommandLine.Assert.notNull(var2, "textTable");
            this.optionRenderer = CommandLine.Assert.notNull(var3, "optionRenderer");
            this.parameterRenderer = CommandLine.Assert.notNull(var4, "parameterRenderer");
         }

         public void layout(Field var1, CommandLine.Help.Ansi.Text[][] var2) {
            for (CommandLine.Help.Ansi.Text[] var6 : var2) {
               this.table.addRowValues(var6);
            }
         }

         public void addOptions(List<Field> var1, CommandLine.Help.IParamLabelRenderer var2) {
            for (Field var4 : var1) {
               CommandLine.Option var5 = var4.getAnnotation(CommandLine.Option.class);
               if (!var5.hidden()) {
                  this.addOption(var4, var2);
               }
            }
         }

         public void addOption(Field var1, CommandLine.Help.IParamLabelRenderer var2) {
            CommandLine.Option var3 = var1.getAnnotation(CommandLine.Option.class);
            CommandLine.Help.Ansi.Text[][] var4 = this.optionRenderer.render(var3, var1, var2, this.colorScheme);
            this.layout(var1, var4);
         }

         public void addPositionalParameters(List<Field> var1, CommandLine.Help.IParamLabelRenderer var2) {
            for (Field var4 : var1) {
               CommandLine.Parameters var5 = var4.getAnnotation(CommandLine.Parameters.class);
               if (!var5.hidden()) {
                  this.addPositionalParameter(var4, var2);
               }
            }
         }

         public void addPositionalParameter(Field var1, CommandLine.Help.IParamLabelRenderer var2) {
            CommandLine.Parameters var3 = var1.getAnnotation(CommandLine.Parameters.class);
            CommandLine.Help.Ansi.Text[][] var4 = this.parameterRenderer.render(var3, var1, var2, this.colorScheme);
            this.layout(var1, var4);
         }

         @Override
         public String toString() {
            return this.table.toString();
         }
      }

      static class MinimalOptionRenderer implements CommandLine.Help.IOptionRenderer {
         @Override
         public CommandLine.Help.Ansi.Text[][] render(
            CommandLine.Option var1, Field var2, CommandLine.Help.IParamLabelRenderer var3, CommandLine.Help.ColorScheme var4
         ) {
            CommandLine.Help.Ansi.Text var5 = var4.optionText(var1.names()[0]);
            CommandLine.Help.Ansi.Text var6 = var3.renderParameterLabel(var2, var4.ansi(), var4.optionParamStyles);
            var5 = var5.append(var6);
            return new CommandLine.Help.Ansi.Text[][]{{var5, var4.ansi().new Text(var1.description().length == 0 ? "" : var1.description()[0])}};
         }
      }

      static class MinimalParameterRenderer implements CommandLine.Help.IParameterRenderer {
         @Override
         public CommandLine.Help.Ansi.Text[][] render(
            CommandLine.Parameters var1, Field var2, CommandLine.Help.IParamLabelRenderer var3, CommandLine.Help.ColorScheme var4
         ) {
            return new CommandLine.Help.Ansi.Text[][]{
               {
                     var3.renderParameterLabel(var2, var4.ansi(), var4.parameterStyles),
                     var4.ansi().new Text(var1.description().length == 0 ? "" : var1.description()[0])
               }
            };
         }
      }

      static class ShortestFirst implements Comparator<String> {
         public int compare(String var1, String var2) {
            return var1.length() - var2.length();
         }

         public static String[] sort(String[] var0) {
            Arrays.sort(var0, new CommandLine.Help.ShortestFirst());
            return var0;
         }
      }

      static class SortByOptionArityAndNameAlphabetically extends CommandLine.Help.SortByShortestOptionNameAlphabetically {
         @Override
         public int compare(Field var1, Field var2) {
            CommandLine.Option var3 = var1.getAnnotation(CommandLine.Option.class);
            CommandLine.Option var4 = var2.getAnnotation(CommandLine.Option.class);
            CommandLine.Range var5 = CommandLine.Range.optionArity(var1);
            CommandLine.Range var6 = CommandLine.Range.optionArity(var2);
            int var7 = var5.max - var6.max;
            if (var7 == 0) {
               var7 = var5.min - var6.min;
            }

            if (var7 == 0) {
               if (CommandLine.isMultiValue(var1) && !CommandLine.isMultiValue(var2)) {
                  var7 = 1;
               }

               if (!CommandLine.isMultiValue(var1) && CommandLine.isMultiValue(var2)) {
                  var7 = -1;
               }
            }

            return var7 == 0 ? super.compare(var1, var2) : var7;
         }
      }

      static class SortByShortestOptionNameAlphabetically implements Comparator<Field> {
         public int compare(Field var1, Field var2) {
            CommandLine.Option var3 = var1.getAnnotation(CommandLine.Option.class);
            CommandLine.Option var4 = var2.getAnnotation(CommandLine.Option.class);
            if (var3 == null) {
               return 1;
            } else if (var4 == null) {
               return -1;
            } else {
               String[] var5 = CommandLine.Help.ShortestFirst.sort(var3.names());
               String[] var6 = CommandLine.Help.ShortestFirst.sort(var4.names());
               int var7 = var5[0].toUpperCase().compareTo(var6[0].toUpperCase());
               var7 = var7 == 0 ? -var5[0].compareTo(var6[0]) : var7;
               return var3.help() == var4.help() ? var7 : (var4.help() ? -1 : 1);
            }
         }
      }

      public static class TextTable {
         public final CommandLine.Help.Column[] columns;
         protected final List<CommandLine.Help.Ansi.Text> columnValues = new ArrayList<>();
         public int indentWrappedLines = 2;
         private final CommandLine.Help.Ansi ansi;

         public TextTable(CommandLine.Help.Ansi var1) {
            this(
               var1,
               new CommandLine.Help.Column(2, 0, CommandLine.Help.Column.Overflow.TRUNCATE),
               new CommandLine.Help.Column(2, 0, CommandLine.Help.Column.Overflow.TRUNCATE),
               new CommandLine.Help.Column(1, 0, CommandLine.Help.Column.Overflow.TRUNCATE),
               new CommandLine.Help.Column(24, 1, CommandLine.Help.Column.Overflow.SPAN),
               new CommandLine.Help.Column(51, 1, CommandLine.Help.Column.Overflow.WRAP)
            );
         }

         public TextTable(CommandLine.Help.Ansi var1, int... var2) {
            this.ansi = CommandLine.Assert.notNull(var1, "ansi");
            this.columns = new CommandLine.Help.Column[var2.length];

            for (int var3 = 0; var3 < var2.length; var3++) {
               this.columns[var3] = new CommandLine.Help.Column(
                  var2[var3], 0, var3 == var2.length - 1 ? CommandLine.Help.Column.Overflow.SPAN : CommandLine.Help.Column.Overflow.WRAP
               );
            }
         }

         public TextTable(CommandLine.Help.Ansi var1, CommandLine.Help.Column... var2) {
            this.ansi = CommandLine.Assert.notNull(var1, "ansi");
            this.columns = CommandLine.Assert.notNull(var2, "columns");
            if (var2.length == 0) {
               throw new IllegalArgumentException("At least one column is required");
            }
         }

         public CommandLine.Help.Ansi.Text textAt(int var1, int var2) {
            return this.columnValues.get(var2 + var1 * this.columns.length);
         }

         @Deprecated
         public CommandLine.Help.Ansi.Text cellAt(int var1, int var2) {
            return this.textAt(var1, var2);
         }

         public int rowCount() {
            return this.columnValues.size() / this.columns.length;
         }

         public void addEmptyRow() {
            for (int var1 = 0; var1 < this.columns.length; var1++) {
               this.columnValues.add(this.ansi.new Text(this.columns[var1].width));
            }
         }

         public void addRowValues(String... var1) {
            CommandLine.Help.Ansi.Text[] var2 = new CommandLine.Help.Ansi.Text[var1.length];

            for (int var3 = 0; var3 < var2.length; var3++) {
               var2[var3] = var1[var3] == null ? CommandLine.Help.Ansi.EMPTY_TEXT : this.ansi.new Text(var1[var3]);
            }

            this.addRowValues(var2);
         }

         public void addRowValues(CommandLine.Help.Ansi.Text... var1) {
            if (var1.length > this.columns.length) {
               throw new IllegalArgumentException(var1.length + " values don't fit in " + this.columns.length + " columns");
            } else {
               this.addEmptyRow();

               for (int var2 = 0; var2 < var1.length; var2++) {
                  int var3 = this.rowCount() - 1;
                  CommandLine.Help.TextTable.Cell var4 = this.putValue(var3, var2, var1[var2]);
                  if ((var4.row != var3 || var4.column != var2) && var2 != var1.length - 1) {
                     this.addEmptyRow();
                  }
               }
            }
         }

         public CommandLine.Help.TextTable.Cell putValue(int var1, int var2, CommandLine.Help.Ansi.Text var3) {
            if (var1 > this.rowCount() - 1) {
               throw new IllegalArgumentException("Cannot write to row " + var1 + ": rowCount=" + this.rowCount());
            } else if (var3 != null && var3.plain.length() != 0) {
               CommandLine.Help.Column var4 = this.columns[var2];
               int var5 = var4.indent;
               switch (var4.overflow) {
                  case TRUNCATE:
                     copy(var3, this.textAt(var1, var2), var5);
                     return new CommandLine.Help.TextTable.Cell(var2, var1);
                  case SPAN:
                     int var6 = var2;

                     do {
                        boolean var9 = var2 == this.columns.length - 1;
                        int var10 = var9
                           ? this.copy(BreakIterator.getLineInstance(), var3, this.textAt(var1, var2), var5)
                           : copy(var3, this.textAt(var1, var2), var5);
                        var3 = var3.substring(var10);
                        var5 = 0;
                        if (var3.length > 0) {
                           var2++;
                        }

                        if (var3.length > 0 && var2 >= this.columns.length) {
                           this.addEmptyRow();
                           var1++;
                           var2 = var6;
                           var5 = var4.indent + this.indentWrappedLines;
                        }
                     } while (var3.length > 0);

                     return new CommandLine.Help.TextTable.Cell(var2, var1);
                  case WRAP:
                     BreakIterator var7 = BreakIterator.getLineInstance();

                     do {
                        int var8 = this.copy(var7, var3, this.textAt(var1, var2), var5);
                        var3 = var3.substring(var8);
                        var5 = var4.indent + this.indentWrappedLines;
                        if (var3.length > 0) {
                           var1++;
                           this.addEmptyRow();
                        }
                     } while (var3.length > 0);

                     return new CommandLine.Help.TextTable.Cell(var2, var1);
                  default:
                     throw new IllegalStateException(var4.overflow.toString());
               }
            } else {
               return new CommandLine.Help.TextTable.Cell(var2, var1);
            }
         }

         private static int length(CommandLine.Help.Ansi.Text var0) {
            return var0.length;
         }

         private int copy(BreakIterator var1, CommandLine.Help.Ansi.Text var2, CommandLine.Help.Ansi.Text var3, int var4) {
            var1.setText(var2.plainString().replace("-", "ÿ"));
            int var5 = 0;
            int var6 = var1.first();

            for (int var7 = var1.next(); var7 != -1; var7 = var1.next()) {
               CommandLine.Help.Ansi.Text var8 = var2.substring(var6, var7);
               if (var3.maxLength < var4 + var5 + length(var8)) {
                  break;
               }

               var5 += copy(var8, var3, var4 + var5);
               var6 = var7;
            }

            if (var5 == 0 && length(var2) > var3.maxLength) {
               var5 = copy(var2, var3, var4);
            }

            return var5;
         }

         private static int copy(CommandLine.Help.Ansi.Text var0, CommandLine.Help.Ansi.Text var1, int var2) {
            int var3 = Math.min(var0.length, var1.maxLength - var2);
            var0.getStyledChars(var0.from, var3, var1, var2);
            return var3;
         }

         public StringBuilder toString(StringBuilder var1) {
            int var2 = this.columns.length;
            StringBuilder var3 = new StringBuilder(80);

            for (int var4 = 0; var4 < this.columnValues.size(); var4++) {
               CommandLine.Help.Ansi.Text var5 = this.columnValues.get(var4);
               var3.append(var5.toString());
               var3.append(new String(CommandLine.Help.spaces(this.columns[var4 % var2].width - var5.length)));
               if (var4 % var2 == var2 - 1) {
                  int var6 = var3.length() - 1;

                  while (var6 >= 0 && var3.charAt(var6) == ' ') {
                     var6--;
                  }

                  var3.setLength(var6 + 1);
                  var1.append(var3.toString()).append(System.getProperty("line.separator"));
                  var3.setLength(0);
               }
            }

            return var1;
         }

         @Override
         public String toString() {
            return this.toString(new StringBuilder()).toString();
         }

         public static class Cell {
            public final int column;
            public final int row;

            public Cell(int var1, int var2) {
               this.column = var1;
               this.row = var2;
            }
         }
      }
   }

   public interface IExceptionHandler {
      List<Object> handleException(CommandLine.ParameterException var1, PrintStream var2, CommandLine.Help.Ansi var3, String... var4);
   }

   public interface IParseResultHandler {
      List<Object> handleParseResult(List<CommandLine> var1, PrintStream var2, CommandLine.Help.Ansi var3) throws CommandLine.ExecutionException;
   }

   public interface ITypeConverter<K> {
      K convert(String var1) throws Exception;
   }

   public static class InitializationException extends CommandLine.PicocliException {
      private static final long serialVersionUID = 8423014001666638895L;

      public InitializationException(String var1) {
         super(var1);
      }

      public InitializationException(String var1, Exception var2) {
         super(var1, var2);
      }
   }

   private class Interpreter {
      private final Map<String, CommandLine> commands = new LinkedHashMap<>();
      private final Map<Class<?>, CommandLine.ITypeConverter<?>> converterRegistry = new HashMap<>();
      private final Map<String, Field> optionName2Field = new HashMap<>();
      private final Map<Character, Field> singleCharOption2Field = new HashMap<>();
      private final List<Field> requiredFields = new ArrayList<>();
      private final List<Field> positionalParametersFields = new ArrayList<>();
      private final Object command;
      private boolean isHelpRequested;
      private String separator = "=";
      private int position;

      Interpreter(Object var2) {
         this.converterRegistry.put(Path.class, new CommandLine.BuiltIn.PathConverter());
         this.converterRegistry.put(Object.class, new CommandLine.BuiltIn.StringConverter());
         this.converterRegistry.put(String.class, new CommandLine.BuiltIn.StringConverter());
         this.converterRegistry.put(StringBuilder.class, new CommandLine.BuiltIn.StringBuilderConverter());
         this.converterRegistry.put(CharSequence.class, new CommandLine.BuiltIn.CharSequenceConverter());
         this.converterRegistry.put(Byte.class, new CommandLine.BuiltIn.ByteConverter());
         this.converterRegistry.put(byte.class, new CommandLine.BuiltIn.ByteConverter());
         this.converterRegistry.put(Boolean.class, new CommandLine.BuiltIn.BooleanConverter());
         this.converterRegistry.put(boolean.class, new CommandLine.BuiltIn.BooleanConverter());
         this.converterRegistry.put(Character.class, new CommandLine.BuiltIn.CharacterConverter());
         this.converterRegistry.put(char.class, new CommandLine.BuiltIn.CharacterConverter());
         this.converterRegistry.put(Short.class, new CommandLine.BuiltIn.ShortConverter());
         this.converterRegistry.put(short.class, new CommandLine.BuiltIn.ShortConverter());
         this.converterRegistry.put(Integer.class, new CommandLine.BuiltIn.IntegerConverter());
         this.converterRegistry.put(int.class, new CommandLine.BuiltIn.IntegerConverter());
         this.converterRegistry.put(Long.class, new CommandLine.BuiltIn.LongConverter());
         this.converterRegistry.put(long.class, new CommandLine.BuiltIn.LongConverter());
         this.converterRegistry.put(Float.class, new CommandLine.BuiltIn.FloatConverter());
         this.converterRegistry.put(float.class, new CommandLine.BuiltIn.FloatConverter());
         this.converterRegistry.put(Double.class, new CommandLine.BuiltIn.DoubleConverter());
         this.converterRegistry.put(double.class, new CommandLine.BuiltIn.DoubleConverter());
         this.converterRegistry.put(File.class, new CommandLine.BuiltIn.FileConverter());
         this.converterRegistry.put(URI.class, new CommandLine.BuiltIn.URIConverter());
         this.converterRegistry.put(URL.class, new CommandLine.BuiltIn.URLConverter());
         this.converterRegistry.put(Date.class, new CommandLine.BuiltIn.ISO8601DateConverter());
         this.converterRegistry.put(Time.class, new CommandLine.BuiltIn.ISO8601TimeConverter());
         this.converterRegistry.put(BigDecimal.class, new CommandLine.BuiltIn.BigDecimalConverter());
         this.converterRegistry.put(BigInteger.class, new CommandLine.BuiltIn.BigIntegerConverter());
         this.converterRegistry.put(Charset.class, new CommandLine.BuiltIn.CharsetConverter());
         this.converterRegistry.put(InetAddress.class, new CommandLine.BuiltIn.InetAddressConverter());
         this.converterRegistry.put(Pattern.class, new CommandLine.BuiltIn.PatternConverter());
         this.converterRegistry.put(UUID.class, new CommandLine.BuiltIn.UUIDConverter());
         this.command = CommandLine.Assert.notNull(var2, "command");
         Class var3 = var2.getClass();
         String var4 = null;
         String var5 = null;

         boolean var6;
         for (var6 = false; var3 != null; var3 = var3.getSuperclass()) {
            CommandLine.init(var3, this.requiredFields, this.optionName2Field, this.singleCharOption2Field, this.positionalParametersFields);
            if (var3.isAnnotationPresent(CommandLine.Command.class)) {
               var6 = true;
               CommandLine.Command var7 = var3.getAnnotation(CommandLine.Command.class);
               var5 = var5 == null ? var7.separator() : var5;
               var4 = var4 == null ? var7.name() : var4;
               CommandLine.this.versionLines.addAll(Arrays.asList(var7.version()));

               for (Class var11 : var7.subcommands()) {
                  CommandLine.Command var12 = var11.getAnnotation(CommandLine.Command.class);
                  if (var12 == null || "<main class>".equals(var12.name())) {
                     throw new CommandLine.InitializationException(
                        "Subcommand " + var11.getName() + " is missing the mandatory @Command annotation with a 'name' attribute"
                     );
                  }

                  try {
                     Constructor var13 = var11.getDeclaredConstructor();
                     var13.setAccessible(true);
                     CommandLine var14 = CommandLine.toCommandLine(var13.newInstance());
                     var14.parent = CommandLine.this;
                     this.commands.put(var12.name(), var14);
                  } catch (CommandLine.InitializationException var15) {
                     throw var15;
                  } catch (NoSuchMethodException var16) {
                     throw new CommandLine.InitializationException("Cannot instantiate subcommand " + var11.getName() + ": the class has no constructor", var16);
                  } catch (Exception var17) {
                     throw new CommandLine.InitializationException("Could not instantiate and add subcommand " + var11.getName() + ": " + var17, var17);
                  }
               }
            }
         }

         this.separator = var5 != null ? var5 : this.separator;
         CommandLine.this.commandName = var4 != null ? var4 : CommandLine.this.commandName;
         Collections.sort(this.positionalParametersFields, new CommandLine.PositionalParametersSorter());
         CommandLine.validatePositionalParameters(this.positionalParametersFields);
         if (this.positionalParametersFields.isEmpty() && this.optionName2Field.isEmpty() && !var6) {
            throw new CommandLine.InitializationException(
               var2 + " (" + var2.getClass() + ") is not a command: it has no @Command, @Option or @Parameters annotations"
            );
         }
      }

      List<CommandLine> parse(String... var1) {
         CommandLine.Assert.notNull(var1, "argument array");
         if (CommandLine.this.tracer.isInfo()) {
            CommandLine.this.tracer.info("Parsing %d command line args %s%n", var1.length, Arrays.toString((Object[])var1));
         }

         Stack var2 = new Stack();

         for (int var3 = var1.length - 1; var3 >= 0; var3--) {
            var2.push(var1[var3]);
         }

         ArrayList var4 = new ArrayList();
         this.parse(var4, var2, var1);
         return var4;
      }

      private void parse(List<CommandLine> var1, Stack<String> var2, String[] var3) {
         this.isHelpRequested = false;
         CommandLine.this.versionHelpRequested = false;
         CommandLine.this.usageHelpRequested = false;
         Class var4 = this.command.getClass();
         if (CommandLine.this.tracer.isDebug()) {
            CommandLine.this.tracer
               .debug(
                  "Initializing %s: %d options, %d positional parameters, %d required, %d subcommands.%n",
                  var4.getName(),
                  new HashSet<>(this.optionName2Field.values()).size(),
                  this.positionalParametersFields.size(),
                  this.requiredFields.size(),
                  this.commands.size()
               );
         }

         var1.add(CommandLine.this);
         ArrayList var5 = new ArrayList<>(this.requiredFields);
         HashSet var6 = new HashSet();
         Collections.sort(var5, new CommandLine.PositionalParametersSorter());

         try {
            this.processArguments(var1, var2, var5, var6, var3);
         } catch (CommandLine.ParameterException var10) {
            throw var10;
         } catch (Exception var11) {
            int var8 = var3.length - var2.size() - 1;
            String var9 = var8 >= 0 && var8 < var3.length ? var3[var8] : "?";
            throw CommandLine.ParameterException.create(CommandLine.this, var11, var9, var8, var3);
         }

         if (!this.isAnyHelpRequested() && !var5.isEmpty()) {
            for (Field var12 : var5) {
               if (var12.isAnnotationPresent(CommandLine.Option.class)) {
                  throw CommandLine.MissingParameterException.create(CommandLine.this, var5, this.separator);
               }

               this.assertNoMissingParameters(var12, CommandLine.Range.parameterArity(var12).min, var2);
            }
         }

         if (!CommandLine.this.unmatchedArguments.isEmpty()) {
            if (!CommandLine.this.isUnmatchedArgumentsAllowed()) {
               throw new CommandLine.UnmatchedArgumentException(CommandLine.this, CommandLine.this.unmatchedArguments);
            }

            if (CommandLine.this.tracer.isWarn()) {
               CommandLine.this.tracer.warn("Unmatched arguments: %s%n", CommandLine.this.unmatchedArguments);
            }
         }
      }

      private void processArguments(List<CommandLine> var1, Stack<String> var2, Collection<Field> var3, Set<Field> var4, String[] var5) throws Exception {
         while (!var2.isEmpty()) {
            String var6 = (String)var2.pop();
            if (CommandLine.this.tracer.isDebug()) {
               CommandLine.this.tracer.debug("Processing argument '%s'. Remainder=%s%n", var6, CommandLine.reverse((Stack)var2.clone()));
            }

            if ("--".equals(var6)) {
               CommandLine.this.tracer.info("Found end-of-options delimiter '--'. Treating remainder as positional parameters.%n");
               this.processRemainderAsPositionalParameters(var3, var4, var2);
               return;
            }

            if (this.commands.containsKey(var6)) {
               if (!this.isHelpRequested && !var3.isEmpty()) {
                  throw CommandLine.MissingParameterException.create(CommandLine.this, var3, this.separator);
               }

               if (CommandLine.this.tracer.isDebug()) {
                  CommandLine.this.tracer.debug("Found subcommand '%s' (%s)%n", var6, this.commands.get(var6).interpreter.command.getClass().getName());
               }

               this.commands.get(var6).interpreter.parse(var1, var2, var5);
               return;
            }

            boolean var7 = false;
            int var8 = var6.indexOf(this.separator);
            if (var8 > 0) {
               String var9 = var6.substring(0, var8);
               if (this.optionName2Field.containsKey(var9) && !this.optionName2Field.containsKey(var6)) {
                  var7 = true;
                  String var10 = var6.substring(var8 + this.separator.length());
                  var2.push(var10);
                  var6 = var9;
                  if (CommandLine.this.tracer.isDebug()) {
                     CommandLine.this.tracer.debug("Separated '%s' option from '%s' option parameter%n", var9, var10);
                  }
               } else if (CommandLine.this.tracer.isDebug()) {
                  CommandLine.this.tracer.debug("'%s' contains separator '%s' but '%s' is not a known option%n", var6, this.separator, var9);
               }
            } else if (CommandLine.this.tracer.isDebug()) {
               CommandLine.this.tracer.debug("'%s' cannot be separated into <option>%s<option-parameter>%n", var6, this.separator);
            }

            if (this.optionName2Field.containsKey(var6)) {
               this.processStandaloneOption(var3, var4, var6, var2, var7);
            } else if (var6.length() > 2 && var6.startsWith("-")) {
               if (CommandLine.this.tracer.isDebug()) {
                  CommandLine.this.tracer.debug("Trying to process '%s' as clustered short options%n", var6, var2);
               }

               this.processClusteredShortOptions(var3, var4, var6, var2);
            } else {
               var2.push(var6);
               if (CommandLine.this.tracer.isDebug()) {
                  CommandLine.this.tracer.debug("Could not find option '%s', deciding whether to treat as unmatched option or positional parameter...%n", var6);
               }

               if (this.resemblesOption(var6)) {
                  this.handleUnmatchedArguments((String)var2.pop());
               } else {
                  if (CommandLine.this.tracer.isDebug()) {
                     CommandLine.this.tracer.debug("No option named '%s' found. Processing remainder as positional parameters%n", var6);
                  }

                  this.processPositionalParameter(var3, var4, var2);
               }
            }
         }
      }

      private boolean resemblesOption(String var1) {
         int var2 = 0;

         for (String var4 : this.optionName2Field.keySet()) {
            for (int var5 = 0; var5 < var1.length() && var4.length() > var5 && var1.charAt(var5) == var4.charAt(var5); var5++) {
               var2++;
            }
         }

         boolean var6 = var2 > 0 && var2 * 10 >= this.optionName2Field.size() * 9;
         if (CommandLine.this.tracer.isDebug()) {
            CommandLine.this.tracer
               .debug(
                  "%s %s an option: %d matching prefix chars out of %d option names%n",
                  var1,
                  var6 ? "resembles" : "doesn't resemble",
                  var2,
                  this.optionName2Field.size()
               );
         }

         return var6;
      }

      private void handleUnmatchedArguments(String var1) {
         Stack var2 = new Stack();
         var2.add(var1);
         this.handleUnmatchedArguments(var2);
      }

      private void handleUnmatchedArguments(Stack<String> var1) {
         while (!var1.isEmpty()) {
            CommandLine.this.unmatchedArguments.add((String)var1.pop());
         }
      }

      private void processRemainderAsPositionalParameters(Collection<Field> var1, Set<Field> var2, Stack<String> var3) throws Exception {
         while (!var3.empty()) {
            this.processPositionalParameter(var1, var2, var3);
         }
      }

      private void processPositionalParameter(Collection<Field> var1, Set<Field> var2, Stack<String> var3) throws Exception {
         if (CommandLine.this.tracer.isDebug()) {
            CommandLine.this.tracer
               .debug("Processing next arg as a positional parameter at index=%d. Remainder=%s%n", this.position, CommandLine.reverse((Stack)var3.clone()));
         }

         int var4 = 0;

         for (Field var6 : this.positionalParametersFields) {
            CommandLine.Range var7 = CommandLine.Range.parameterIndex(var6);
            if (var7.contains(this.position)) {
               Stack var8 = (Stack)var3.clone();
               CommandLine.Range var9 = CommandLine.Range.parameterArity(var6);
               if (CommandLine.this.tracer.isDebug()) {
                  CommandLine.this.tracer.debug("Position %d is in index range %s. Trying to assign args to %s, arity=%s%n", this.position, var7, var6, var9);
               }

               this.assertNoMissingParameters(var6, var9.min, var8);
               int var10 = var8.size();
               this.applyOption(var6, CommandLine.Parameters.class, var9, false, var8, var2, "args[" + var7 + "] at position " + this.position);
               int var11 = var10 - var8.size();
               if (var11 > 0) {
                  var1.remove(var6);
               }

               var4 = Math.max(var4, var11);
            }
         }

         for (int var12 = 0; var12 < var4; var12++) {
            var3.pop();
         }

         this.position += var4;
         if (CommandLine.this.tracer.isDebug()) {
            CommandLine.this.tracer.debug("Consumed %d arguments, moving position to index %d.%n", var4, this.position);
         }

         if (var4 == 0 && !var3.isEmpty()) {
            this.handleUnmatchedArguments((String)var3.pop());
         }
      }

      private void processStandaloneOption(Collection<Field> var1, Set<Field> var2, String var3, Stack<String> var4, boolean var5) throws Exception {
         Field var6 = this.optionName2Field.get(var3);
         var1.remove(var6);
         CommandLine.Range var7 = CommandLine.Range.optionArity(var6);
         if (var5) {
            var7 = var7.min(Math.max(1, var7.min));
         }

         if (CommandLine.this.tracer.isDebug()) {
            CommandLine.this.tracer.debug("Found option named '%s': field %s, arity=%s%n", var3, var6, var7);
         }

         this.applyOption(var6, CommandLine.Option.class, var7, var5, var4, var2, "option " + var3);
      }

      private void processClusteredShortOptions(Collection<Field> var1, Set<Field> var2, String var3, Stack<String> var4) throws Exception {
         String var5 = var3.substring(0, 1);
         String var6 = var3.substring(1);
         boolean var7 = true;

         while (var6.length() > 0 && this.singleCharOption2Field.containsKey(var6.charAt(0))) {
            Field var8 = this.singleCharOption2Field.get(var6.charAt(0));
            CommandLine.Range var9 = CommandLine.Range.optionArity(var8);
            String var10 = "option " + var5 + var6.charAt(0);
            if (CommandLine.this.tracer.isDebug()) {
               CommandLine.this.tracer.debug("Found option '%s%s' in %s: field %s, arity=%s%n", var5, var6.charAt(0), var3, var8, var9);
            }

            var1.remove(var8);
            var6 = var6.length() > 0 ? var6.substring(1) : "";
            var7 = var6.length() > 0;
            if (var6.startsWith(this.separator)) {
               var6 = var6.substring(this.separator.length());
               var9 = var9.min(Math.max(1, var9.min));
            }

            if (var9.min > 0 && !CommandLine.empty(var6) && CommandLine.this.tracer.isDebug()) {
               CommandLine.this.tracer.debug("Trying to process '%s' as option parameter%n", var6);
            }

            if (!CommandLine.empty(var6)) {
               var4.push(var6);
            }

            int var11 = this.applyOption(var8, CommandLine.Option.class, var9, var7, var4, var2, var10);
            if (CommandLine.empty(var6) || var11 > 0 || var4.isEmpty()) {
               return;
            }

            var6 = (String)var4.pop();
         }

         if (var6.length() != 0) {
            if (var3.endsWith(var6)) {
               var4.push(var7 ? var5 + var6 : var6);
               if (((String)var4.peek()).equals(var3)) {
                  if (CommandLine.this.tracer.isDebug()) {
                     CommandLine.this.tracer
                        .debug("Could not match any short options in %s, deciding whether to treat as unmatched option or positional parameter...%n", var3);
                  }

                  if (this.resemblesOption(var3)) {
                     this.handleUnmatchedArguments((String)var4.pop());
                     return;
                  }

                  this.processPositionalParameter(var1, var2, var4);
                  return;
               }

               if (CommandLine.this.tracer.isDebug()) {
                  CommandLine.this.tracer.debug("No option found for %s in %s%n", var6, var3);
               }

               this.handleUnmatchedArguments((String)var4.pop());
            } else {
               var4.push(var6);
               if (CommandLine.this.tracer.isDebug()) {
                  CommandLine.this.tracer.debug("%s is not an option parameter for %s%n", var6, var3);
               }

               this.processPositionalParameter(var1, var2, var4);
            }
         }
      }

      private int applyOption(Field var1, Class<?> var2, CommandLine.Range var3, boolean var4, Stack<String> var5, Set<Field> var6, String var7) throws Exception {
         this.updateHelpRequested(var1);
         int var8 = var5.size();
         this.assertNoMissingParameters(var1, var3.min, var5);
         Class var9 = var1.getType();
         if (var9.isArray()) {
            return this.applyValuesToArrayField(var1, var2, var3, var5, var9, var7);
         } else if (Collection.class.isAssignableFrom(var9)) {
            return this.applyValuesToCollectionField(var1, var2, var3, var5, var9, var7);
         } else if (Map.class.isAssignableFrom(var9)) {
            return this.applyValuesToMapField(var1, var2, var3, var5, var9, var7);
         } else {
            var9 = CommandLine.getTypeAttribute(var1)[0];
            return this.applyValueToSingleValuedField(var1, var3, var5, var9, var6, var7);
         }
      }

      private int applyValueToSingleValuedField(Field var1, CommandLine.Range var2, Stack<String> var3, Class<?> var4, Set<Field> var5, String var6) throws Exception {
         boolean var7 = var3.isEmpty();
         String var8 = var3.isEmpty() ? null : this.trim((String)var3.pop());
         int var9 = var2.min;
         if ((var4 == Boolean.class || var4 == boolean.class) && var2.min <= 0) {
            if (var2.max <= 0 || !"true".equalsIgnoreCase(var8) && !"false".equalsIgnoreCase(var8)) {
               if (var8 != null) {
                  var3.push(var8);
               }

               Boolean var10 = (Boolean)var1.get(this.command);
               var8 = String.valueOf(var10 == null ? true : !var10);
            } else {
               var9 = 1;
            }
         }

         if (var7 && var8 == null) {
            return 0;
         } else {
            CommandLine.ITypeConverter var15 = this.getTypeConverter(var4, var1);
            Object var11 = this.tryConvert(var1, -1, var15, var8, var4);
            Object var12 = var1.get(this.command);
            CommandLine.TraceLevel var13 = CommandLine.TraceLevel.INFO;
            String var14 = "Setting %s field '%s.%s' to '%5$s' (was '%4$s') for %6$s%n";
            if (var5 != null) {
               if (var5.contains(var1)) {
                  if (!CommandLine.this.isOverwrittenOptionsAllowed()) {
                     throw new CommandLine.OverwrittenOptionException(CommandLine.this, this.optionDescription("", var1, 0) + " should be specified only once");
                  }

                  var13 = CommandLine.TraceLevel.WARN;
                  var14 = "Overwriting %s field '%s.%s' value '%s' with '%s' for %s%n";
               }

               var5.add(var1);
            }

            if (CommandLine.this.tracer.level.isEnabled(var13)) {
               var13.print(
                  CommandLine.this.tracer,
                  var14,
                  var1.getType().getSimpleName(),
                  var1.getDeclaringClass().getSimpleName(),
                  var1.getName(),
                  String.valueOf(var12),
                  String.valueOf(var11),
                  var6
               );
            }

            var1.set(this.command, var11);
            return var9;
         }
      }

      private int applyValuesToMapField(Field var1, Class<?> var2, CommandLine.Range var3, Stack<String> var4, Class<?> var5, String var6) throws Exception {
         Class[] var7 = CommandLine.getTypeAttribute(var1);
         if (var7.length < 2) {
            throw new CommandLine.ParameterException(
               CommandLine.this,
               "Field " + var1 + " needs two types (one for the map key, one for the value) but only has " + var7.length + " types configured."
            );
         } else {
            CommandLine.ITypeConverter var8 = this.getTypeConverter(var7[0], var1);
            CommandLine.ITypeConverter var9 = this.getTypeConverter(var7[1], var1);
            Map var10 = (Map)var1.get(this.command);
            if (var10 == null) {
               var10 = this.createMap(var5);
               var1.set(this.command, var10);
            }

            int var11 = var10.size();
            this.consumeMapArguments(var1, var3, var4, var7, var8, var9, var10, var6);
            return var10.size() - var11;
         }
      }

      private void consumeMapArguments(
         Field var1,
         CommandLine.Range var2,
         Stack<String> var3,
         Class<?>[] var4,
         CommandLine.ITypeConverter<?> var5,
         CommandLine.ITypeConverter<?> var6,
         Map<Object, Object> var7,
         String var8
      ) throws Exception {
         for (int var9 = 0; var9 < var2.min; var9++) {
            this.consumeOneMapArgument(var1, var2, var3, var4, var5, var6, var7, var9, var8);
         }

         for (int var10 = var2.min; var10 < var2.max && !var3.isEmpty(); var10++) {
            if (!var1.isAnnotationPresent(CommandLine.Parameters.class) && (this.commands.containsKey(var3.peek()) || this.isOption((String)var3.peek()))) {
               return;
            }

            this.consumeOneMapArgument(var1, var2, var3, var4, var5, var6, var7, var10, var8);
         }
      }

      private void consumeOneMapArgument(
         Field var1,
         CommandLine.Range var2,
         Stack<String> var3,
         Class<?>[] var4,
         CommandLine.ITypeConverter<?> var5,
         CommandLine.ITypeConverter<?> var6,
         Map<Object, Object> var7,
         int var8,
         String var9
      ) throws Exception {
         String[] var10 = this.split(this.trim((String)var3.pop()), var1);

         for (String var14 : var10) {
            String[] var15 = var14.split("=");
            if (var15.length < 2) {
               String var18 = this.splitRegex(var1);
               if (var18.length() == 0) {
                  throw new CommandLine.ParameterException(
                     CommandLine.this, "Value for option " + this.optionDescription("", var1, 0) + " should be in KEY=VALUE format but was " + var14
                  );
               }

               throw new CommandLine.ParameterException(
                  CommandLine.this,
                  "Value for option " + this.optionDescription("", var1, 0) + " should be in KEY=VALUE[" + var18 + "KEY=VALUE]... format but was " + var14
               );
            }

            Object var16 = this.tryConvert(var1, var8, var5, var15[0], var4[0]);
            Object var17 = this.tryConvert(var1, var8, var6, var15[1], var4[1]);
            var7.put(var16, var17);
            if (CommandLine.this.tracer.isInfo()) {
               CommandLine.this.tracer
                  .info(
                     "Putting [%s : %s] in %s<%s, %s> field '%s.%s' for %s%n",
                     String.valueOf(var16),
                     String.valueOf(var17),
                     var7.getClass().getSimpleName(),
                     var4[0].getSimpleName(),
                     var4[1].getSimpleName(),
                     var1.getDeclaringClass().getSimpleName(),
                     var1.getName(),
                     var9
                  );
            }
         }
      }

      private void checkMaxArityExceeded(CommandLine.Range var1, int var2, Field var3, String[] var4) {
         if (var4.length > var2) {
            if (var1.max == var2) {
               new StringBuilder().append("").append(var2).toString();
            } else {
               new StringBuilder().append(var1).append(", remainder=").append(var2).toString();
            }

            throw new CommandLine.MaxValuesforFieldExceededException(
               CommandLine.this,
               this.optionDescription("", var3, -1)
                  + " max number of values ("
                  + var1.max
                  + ") exceeded: remainder is "
                  + var2
                  + " but "
                  + var4.length
                  + " values were specified: "
                  + Arrays.toString((Object[])var4)
            );
         }
      }

      private int applyValuesToArrayField(Field var1, Class<?> var2, CommandLine.Range var3, Stack<String> var4, Class<?> var5, String var6) throws Exception {
         Object var7 = var1.get(this.command);
         int var8 = var7 == null ? 0 : Array.getLength(var7);
         Class var9 = CommandLine.getTypeAttribute(var1)[0];
         List var10 = this.consumeArguments(var1, var2, var3, var4, var9, var8, var6);
         ArrayList var11 = new ArrayList();

         for (int var12 = 0; var12 < var8; var12++) {
            var11.add(Array.get(var7, var12));
         }

         for (Object var13 : var10) {
            if (var13 instanceof Collection) {
               var11.addAll((Collection)var13);
            } else {
               var11.add(var13);
            }
         }

         Object var15 = Array.newInstance(var9, var11.size());
         var1.set(this.command, var15);

         for (int var16 = 0; var16 < var11.size(); var16++) {
            Array.set(var15, var16, var11.get(var16));
         }

         return var10.size();
      }

      private int applyValuesToCollectionField(Field var1, Class<?> var2, CommandLine.Range var3, Stack<String> var4, Class<?> var5, String var6) throws Exception {
         Collection var7 = (Collection)var1.get(this.command);
         Class var8 = CommandLine.getTypeAttribute(var1)[0];
         int var9 = var7 == null ? 0 : var7.size();
         List var10 = this.consumeArguments(var1, var2, var3, var4, var8, var9, var6);
         if (var7 == null) {
            var7 = this.createCollection(var5);
            var1.set(this.command, var7);
         }

         for (Object var12 : var10) {
            if (var12 instanceof Collection) {
               var7.addAll((Collection)var12);
            } else {
               var7.add(var12);
            }
         }

         return var10.size();
      }

      private List<Object> consumeArguments(Field var1, Class<?> var2, CommandLine.Range var3, Stack<String> var4, Class<?> var5, int var6, String var7) throws Exception {
         ArrayList var8 = new ArrayList();

         for (int var9 = 0; var9 < var3.min; var9++) {
            this.consumeOneArgument(var1, var3, var4, var5, var8, var9, var6, var7);
         }

         for (int var10 = var3.min; var10 < var3.max && !var4.isEmpty(); var10++) {
            if (var2 != CommandLine.Parameters.class && (this.commands.containsKey(var4.peek()) || this.isOption((String)var4.peek()))) {
               return var8;
            }

            this.consumeOneArgument(var1, var3, var4, var5, var8, var10, var6, var7);
         }

         return var8;
      }

      private int consumeOneArgument(Field var1, CommandLine.Range var2, Stack<String> var3, Class<?> var4, List<Object> var5, int var6, int var7, String var8) throws Exception {
         String[] var9 = this.split(this.trim((String)var3.pop()), var1);
         CommandLine.ITypeConverter var10 = this.getTypeConverter(var4, var1);

         for (int var11 = 0; var11 < var9.length; var11++) {
            var5.add(this.tryConvert(var1, var6, var10, var9[var11], var4));
            if (CommandLine.this.tracer.isInfo()) {
               if (var1.getType().isArray()) {
                  CommandLine.this.tracer
                     .info(
                        "Adding [%s] to %s[] field '%s.%s' for %s%n",
                        String.valueOf(var5.get(var5.size() - 1)),
                        var4.getSimpleName(),
                        var1.getDeclaringClass().getSimpleName(),
                        var1.getName(),
                        var8
                     );
               } else {
                  CommandLine.this.tracer
                     .info(
                        "Adding [%s] to %s<%s> field '%s.%s' for %s%n",
                        String.valueOf(var5.get(var5.size() - 1)),
                        var1.getType().getSimpleName(),
                        var4.getSimpleName(),
                        var1.getDeclaringClass().getSimpleName(),
                        var1.getName(),
                        var8
                     );
               }
            }
         }

         return var6 + 1;
      }

      private String splitRegex(Field var1) {
         if (var1.isAnnotationPresent(CommandLine.Option.class)) {
            return var1.getAnnotation(CommandLine.Option.class).split();
         } else {
            return var1.isAnnotationPresent(CommandLine.Parameters.class) ? var1.getAnnotation(CommandLine.Parameters.class).split() : "";
         }
      }

      private String[] split(String var1, Field var2) {
         String var3 = this.splitRegex(var2);
         return var3.length() == 0 ? new String[]{var1} : var1.split(var3);
      }

      private boolean isOption(String var1) {
         if ("--".equals(var1)) {
            return true;
         } else if (this.optionName2Field.containsKey(var1)) {
            return true;
         } else {
            int var2 = var1.indexOf(this.separator);
            return var2 > 0 && this.optionName2Field.containsKey(var1.substring(0, var2))
               ? true
               : var1.length() > 2 && var1.startsWith("-") && this.singleCharOption2Field.containsKey(var1.charAt(1));
         }
      }

      private Object tryConvert(Field var1, int var2, CommandLine.ITypeConverter<?> var3, String var4, Class<?> var5) throws Exception {
         try {
            return var3.convert(var4);
         } catch (CommandLine.TypeConversionException var8) {
            throw new CommandLine.ParameterException(CommandLine.this, var8.getMessage() + this.optionDescription(" for ", var1, var2));
         } catch (Exception var9) {
            String var7 = this.optionDescription(" for ", var1, var2) + ": " + var9;
            throw new CommandLine.ParameterException(CommandLine.this, "Could not convert '" + var4 + "' to " + var5.getSimpleName() + var7, var9);
         }
      }

      private String optionDescription(String var1, Field var2, int var3) {
         CommandLine.Help.IParamLabelRenderer var4 = CommandLine.Help.createMinimalParamLabelRenderer();
         String var5 = "";
         if (var2.isAnnotationPresent(CommandLine.Option.class)) {
            var5 = var1 + "option '" + var2.getAnnotation(CommandLine.Option.class).names()[0] + "'";
            if (var3 >= 0) {
               CommandLine.Range var6 = CommandLine.Range.optionArity(var2);
               if (var6.max > 1) {
                  var5 = var5 + " at index " + var3;
               }

               var5 = var5 + " (" + var4.renderParameterLabel(var2, CommandLine.Help.Ansi.OFF, Collections.emptyList()) + ")";
            }
         } else if (var2.isAnnotationPresent(CommandLine.Parameters.class)) {
            CommandLine.Range var8 = CommandLine.Range.parameterIndex(var2);
            CommandLine.Help.Ansi.Text var7 = var4.renderParameterLabel(var2, CommandLine.Help.Ansi.OFF, Collections.emptyList());
            var5 = var1 + "positional parameter at index " + var8 + " (" + var7 + ")";
         }

         return var5;
      }

      private boolean isAnyHelpRequested() {
         return this.isHelpRequested || CommandLine.this.versionHelpRequested || CommandLine.this.usageHelpRequested;
      }

      private void updateHelpRequested(Field var1) {
         if (var1.isAnnotationPresent(CommandLine.Option.class)) {
            this.isHelpRequested = this.isHelpRequested | this.is(var1, "help", var1.getAnnotation(CommandLine.Option.class).help());
            CommandLine var2 = CommandLine.this;
            var2.versionHelpRequested = var2.versionHelpRequested | this.is(var1, "versionHelp", var1.getAnnotation(CommandLine.Option.class).versionHelp());
            var2 = CommandLine.this;
            var2.usageHelpRequested = var2.usageHelpRequested | this.is(var1, "usageHelp", var1.getAnnotation(CommandLine.Option.class).usageHelp());
         }
      }

      private boolean is(Field var1, String var2, boolean var3) {
         if (var3 && CommandLine.this.tracer.isInfo()) {
            CommandLine.this.tracer
               .info("Field '%s.%s' has '%s' annotation: not validating required fields%n", var1.getDeclaringClass().getSimpleName(), var1.getName(), var2);
         }

         return var3;
      }

      private Collection<Object> createCollection(Class<?> var1) throws Exception {
         if (var1.isInterface()) {
            if (SortedSet.class.isAssignableFrom(var1)) {
               return new TreeSet<>();
            } else if (Set.class.isAssignableFrom(var1)) {
               return new LinkedHashSet<>();
            } else {
               return (Collection<Object>)(Queue.class.isAssignableFrom(var1) ? new LinkedList<>() : new ArrayList<>());
            }
         } else {
            return (Collection<Object>)var1.newInstance();
         }
      }

      private Map<Object, Object> createMap(Class<?> var1) throws Exception {
         try {
            return (Map<Object, Object>)var1.newInstance();
         } catch (Exception var3) {
            return new LinkedHashMap<>();
         }
      }

      private CommandLine.ITypeConverter<?> getTypeConverter(final Class<?> var1, Field var2) {
         CommandLine.ITypeConverter var3 = this.converterRegistry.get(var1);
         if (var3 != null) {
            return var3;
         } else if (var1.isEnum()) {
            return new CommandLine.ITypeConverter<Object>() {
               @Override
               public Object convert(String var1x) throws Exception {
                  return Enum.valueOf(var1, var1x);
               }
            };
         } else {
            throw new CommandLine.MissingTypeConverterException(CommandLine.this, "No TypeConverter registered for " + var1.getName() + " of field " + var2);
         }
      }

      private void assertNoMissingParameters(Field var1, int var2, Stack<String> var3) {
         if (var2 > var3.size()) {
            if (var2 == 1) {
               if (var1.isAnnotationPresent(CommandLine.Option.class)) {
                  throw new CommandLine.MissingParameterException(CommandLine.this, "Missing required parameter for " + this.optionDescription("", var1, 0));
               } else {
                  CommandLine.Range var4 = CommandLine.Range.parameterIndex(var1);
                  CommandLine.Help.IParamLabelRenderer var5 = CommandLine.Help.createMinimalParamLabelRenderer();
                  String var6 = "";
                  String var7 = "";
                  int var8 = 0;

                  for (int var9 = var4.min; var9 < this.positionalParametersFields.size(); var9++) {
                     if (CommandLine.Range.parameterArity(this.positionalParametersFields.get(var9)).min > 0) {
                        var7 = var7
                           + var6
                           + var5.renderParameterLabel(this.positionalParametersFields.get(var9), CommandLine.Help.Ansi.OFF, Collections.emptyList());
                        var6 = ", ";
                        var8++;
                     }
                  }

                  String var11 = "Missing required parameter";
                  CommandLine.Range var10 = CommandLine.Range.parameterArity(var1);
                  if (var10.isVariable) {
                     var11 = var11 + "s at positions " + var4 + ": ";
                  } else {
                     var11 = var11 + (var8 > 1 ? "s: " : ": ");
                  }

                  throw new CommandLine.MissingParameterException(CommandLine.this, var11 + var7);
               }
            } else if (var3.isEmpty()) {
               throw new CommandLine.MissingParameterException(
                  CommandLine.this, this.optionDescription("", var1, 0) + " requires at least " + var2 + " values, but none were specified."
               );
            } else {
               throw new CommandLine.MissingParameterException(
                  CommandLine.this,
                  this.optionDescription("", var1, 0)
                     + " requires at least "
                     + var2
                     + " values, but only "
                     + var3.size()
                     + " were specified: "
                     + CommandLine.reverse(var3)
               );
            }
         }
      }

      private String trim(String var1) {
         return this.unquote(var1);
      }

      private String unquote(String var1) {
         return var1 == null ? null : (var1.length() > 1 && var1.startsWith("\"") && var1.endsWith("\"") ? var1.substring(1, var1.length() - 1) : var1);
      }
   }

   public static class MaxValuesforFieldExceededException extends CommandLine.ParameterException {
      private static final long serialVersionUID = 6536145439570100641L;

      public MaxValuesforFieldExceededException(CommandLine var1, String var2) {
         super(var1, var2);
      }
   }

   public static class MissingParameterException extends CommandLine.ParameterException {
      private static final long serialVersionUID = 5075678535706338753L;

      public MissingParameterException(CommandLine var1, String var2) {
         super(var1, var2);
      }

      private static CommandLine.MissingParameterException create(CommandLine var0, Collection<Field> var1, String var2) {
         if (var1.size() == 1) {
            return new CommandLine.MissingParameterException(var0, "Missing required option '" + describe((Field)var1.iterator().next(), var2) + "'");
         } else {
            ArrayList var3 = new ArrayList(var1.size());

            for (Field var5 : var1) {
               var3.add(describe(var5, var2));
            }

            return new CommandLine.MissingParameterException(var0, "Missing required options " + var3.toString());
         }
      }

      private static String describe(Field var0, String var1) {
         String var2 = var0.isAnnotationPresent(CommandLine.Option.class)
            ? var0.getAnnotation(CommandLine.Option.class).names()[0] + var1
            : "params[" + var0.getAnnotation(CommandLine.Parameters.class).index() + "]" + var1;
         return var2 + CommandLine.Help.DefaultParamLabelRenderer.renderParameterName(var0);
      }
   }

   public static class MissingTypeConverterException extends CommandLine.ParameterException {
      private static final long serialVersionUID = -6050931703233083760L;

      public MissingTypeConverterException(CommandLine var1, String var2) {
         super(var1, var2);
      }
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.FIELD})
   public @interface Option {
      String[] names();

      boolean required() default false;

            boolean help() default false;

      boolean usageHelp() default false;

      boolean versionHelp() default false;

      String[] description() default {};

      String arity() default "";

      String paramLabel() default "";

      Class<?>[] type() default {};

      String split() default "";

      boolean hidden() default false;
   }

   public static class OverwrittenOptionException extends CommandLine.ParameterException {
      private static final long serialVersionUID = 1338029208271055776L;

      public OverwrittenOptionException(CommandLine var1, String var2) {
         super(var1, var2);
      }
   }

   public static class ParameterException extends CommandLine.PicocliException {
      private static final long serialVersionUID = 1477112829129763139L;
      private final CommandLine commandLine;

      public ParameterException(CommandLine var1, String var2) {
         super(var2);
         this.commandLine = CommandLine.Assert.notNull(var1, "commandLine");
      }

      public ParameterException(CommandLine var1, String var2, Exception var3) {
         super(var2, var3);
         this.commandLine = CommandLine.Assert.notNull(var1, "commandLine");
      }

      public CommandLine getCommandLine() {
         return this.commandLine;
      }

      private static CommandLine.ParameterException create(CommandLine var0, Exception var1, String var2, int var3, String[] var4) {
         String var5 = var1.getClass().getSimpleName()
            + ": "
            + var1.getLocalizedMessage()
            + " while processing argument at or before arg["
            + var3
            + "] '"
            + var2
            + "' in "
            + Arrays.toString((Object[])var4)
            + ": "
            + var1.toString();
         return new CommandLine.ParameterException(var0, var5, var1);
      }
   }

   public static class ParameterIndexGapException extends CommandLine.InitializationException {
      private static final long serialVersionUID = -1520981133257618319L;

      public ParameterIndexGapException(String var1) {
         super(var1);
      }
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.FIELD})
   public @interface Parameters {
      String index() default "*";

      String[] description() default {};

      String arity() default "";

      String paramLabel() default "";

      Class<?>[] type() default {};

      String split() default "";

      boolean hidden() default false;
   }

   public static class PicocliException extends RuntimeException {
      private static final long serialVersionUID = -2574128880125050818L;

      public PicocliException(String var1) {
         super(var1);
      }

      public PicocliException(String var1, Exception var2) {
         super(var1, var2);
      }
   }

   private static class PositionalParametersSorter implements Comparator<Field> {
      private PositionalParametersSorter() {
      }

      public int compare(Field var1, Field var2) {
         int var3 = CommandLine.Range.parameterIndex(var1).compareTo(CommandLine.Range.parameterIndex(var2));
         return var3 == 0 ? CommandLine.Range.parameterArity(var1).compareTo(CommandLine.Range.parameterArity(var2)) : var3;
      }
   }

   public static class Range implements Comparable<CommandLine.Range> {
      public final int min;
      public final int max;
      public final boolean isVariable;
      private final boolean isUnspecified;
      private final String originalValue;

      public Range(int var1, int var2, boolean var3, boolean var4, String var5) {
         this.min = var1;
         this.max = var2;
         this.isVariable = var3;
         this.isUnspecified = var4;
         this.originalValue = var5;
      }

      public static CommandLine.Range optionArity(Field var0) {
         return var0.isAnnotationPresent(CommandLine.Option.class)
            ? adjustForType(valueOf(var0.getAnnotation(CommandLine.Option.class).arity()), var0)
            : new CommandLine.Range(0, 0, false, true, "0");
      }

      public static CommandLine.Range parameterArity(Field var0) {
         return var0.isAnnotationPresent(CommandLine.Parameters.class)
            ? adjustForType(valueOf(var0.getAnnotation(CommandLine.Parameters.class).arity()), var0)
            : new CommandLine.Range(0, 0, false, true, "0");
      }

      public static CommandLine.Range parameterIndex(Field var0) {
         return var0.isAnnotationPresent(CommandLine.Parameters.class)
            ? valueOf(var0.getAnnotation(CommandLine.Parameters.class).index())
            : new CommandLine.Range(0, 0, false, true, "0");
      }

      static CommandLine.Range adjustForType(CommandLine.Range var0, Field var1) {
         return var0.isUnspecified ? defaultArity(var1) : var0;
      }

      public static CommandLine.Range defaultArity(Field var0) {
         Class var1 = var0.getType();
         if (var0.isAnnotationPresent(CommandLine.Option.class)) {
            return defaultArity(var1);
         } else {
            return CommandLine.isMultiValue(var1) ? valueOf("0..1") : valueOf("1");
         }
      }

      public static CommandLine.Range defaultArity(Class<?> var0) {
         return CommandLine.isBoolean(var0) ? valueOf("0") : valueOf("1");
      }

      private int size() {
         return 1 + this.max - this.min;
      }

      static CommandLine.Range parameterCapacity(Field var0) {
         CommandLine.Range var1 = parameterArity(var0);
         if (!CommandLine.isMultiValue(var0)) {
            return var1;
         } else {
            CommandLine.Range var2 = parameterIndex(var0);
            if (var1.max == 0) {
               return var1;
            } else if (var2.size() == 1) {
               return var1;
            } else if (var2.isVariable) {
               return valueOf(var1.min + "..*");
            } else if (var1.size() == 1) {
               return valueOf(var1.min * var2.size() + "");
            } else {
               return var1.isVariable ? valueOf(var1.min * var2.size() + "..*") : valueOf(var1.min * var2.size() + ".." + var1.max * var2.size());
            }
         }
      }

      public static CommandLine.Range valueOf(String var0) {
         var0 = var0.trim();
         boolean var1 = var0.length() == 0 || var0.startsWith("..");
         int var2 = -1;
         int var3 = -1;
         boolean var4 = false;
         int var5 = -1;
         if ((var5 = var0.indexOf("..")) >= 0) {
            var2 = parseInt(var0.substring(0, var5), 0);
            var3 = parseInt(var0.substring(var5 + 2), Integer.MAX_VALUE);
            var4 = var3 == Integer.MAX_VALUE;
         } else {
            var3 = parseInt(var0, Integer.MAX_VALUE);
            var4 = var3 == Integer.MAX_VALUE;
            var2 = var4 ? 0 : var3;
         }

         return new CommandLine.Range(var2, var3, var4, var1, var0);
      }

      private static int parseInt(String var0, int var1) {
         try {
            return Integer.parseInt(var0);
         } catch (Exception var3) {
            return var1;
         }
      }

      public CommandLine.Range min(int var1) {
         return new CommandLine.Range(var1, Math.max(var1, this.max), this.isVariable, this.isUnspecified, this.originalValue);
      }

      public CommandLine.Range max(int var1) {
         return new CommandLine.Range(Math.min(this.min, var1), var1, this.isVariable, this.isUnspecified, this.originalValue);
      }

      public boolean contains(int var1) {
         return this.min <= var1 && this.max >= var1;
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof CommandLine.Range)) {
            return false;
         } else {
            CommandLine.Range var2 = (CommandLine.Range)var1;
            return var2.max == this.max && var2.min == this.min && var2.isVariable == this.isVariable;
         }
      }

      @Override
      public int hashCode() {
         return ((629 + this.max) * 37 + this.min) * 37 + (this.isVariable ? 1 : 0);
      }

      @Override
      public String toString() {
         return this.min == this.max ? String.valueOf(this.min) : this.min + ".." + (this.isVariable ? "*" : this.max);
      }

      public int compareTo(CommandLine.Range var1) {
         int var2 = this.min - var1.min;
         return var2 == 0 ? this.max - var1.max : var2;
      }
   }

   public static class RunAll implements CommandLine.IParseResultHandler {
      @Override
      public List<Object> handleParseResult(List<CommandLine> var1, PrintStream var2, CommandLine.Help.Ansi var3) {
         if (CommandLine.printHelpIfRequested(var1, var2, var3)) {
            return null;
         } else {
            ArrayList var4 = new ArrayList();

            for (CommandLine var6 : var1) {
               var4.add(CommandLine.execute(var6));
            }

            return var4;
         }
      }
   }

   public static class RunFirst implements CommandLine.IParseResultHandler {
      @Override
      public List<Object> handleParseResult(List<CommandLine> var1, PrintStream var2, CommandLine.Help.Ansi var3) {
         return CommandLine.printHelpIfRequested(var1, var2, var3) ? Collections.emptyList() : Arrays.asList(CommandLine.execute((CommandLine)var1.get(0)));
      }
   }

   public static class RunLast implements CommandLine.IParseResultHandler {
      @Override
      public List<Object> handleParseResult(List<CommandLine> var1, PrintStream var2, CommandLine.Help.Ansi var3) {
         if (CommandLine.printHelpIfRequested(var1, var2, var3)) {
            return Collections.emptyList();
         } else {
            CommandLine var4 = (CommandLine)var1.get(var1.size() - 1);
            return Arrays.asList(CommandLine.execute(var4));
         }
      }
   }

   private static enum TraceLevel {
      OFF,
      WARN,
      INFO,
      DEBUG;

      public boolean isEnabled(CommandLine.TraceLevel var1) {
         return this.ordinal() >= var1.ordinal();
      }

      private void print(CommandLine.Tracer var1, String var2, Object... var3) {
         if (var1.level.isEnabled(this)) {
            var1.stream.printf(this.prefix(var2), var3);
         }
      }

      private String prefix(String var1) {
         return "[picocli " + this + "] " + var1;
      }

      static CommandLine.TraceLevel lookup(String var0) {
         return var0 == null ? WARN : (!CommandLine.empty(var0) && !"true".equalsIgnoreCase(var0) ? valueOf(var0) : INFO);
      }
   }

   private static class Tracer {
      CommandLine.TraceLevel level = CommandLine.TraceLevel.lookup(System.getProperty("picocli.trace"));
      PrintStream stream = System.err;

      private Tracer() {
      }

      void warn(String var1, Object... var2) {
         CommandLine.TraceLevel.WARN.print(this, var1, var2);
      }

      void info(String var1, Object... var2) {
         CommandLine.TraceLevel.INFO.print(this, var1, var2);
      }

      void debug(String var1, Object... var2) {
         CommandLine.TraceLevel.DEBUG.print(this, var1, var2);
      }

      boolean isWarn() {
         return this.level.isEnabled(CommandLine.TraceLevel.WARN);
      }

      boolean isInfo() {
         return this.level.isEnabled(CommandLine.TraceLevel.INFO);
      }

      boolean isDebug() {
         return this.level.isEnabled(CommandLine.TraceLevel.DEBUG);
      }
   }

   public static class TypeConversionException extends CommandLine.PicocliException {
      private static final long serialVersionUID = 4251973913816346114L;

      public TypeConversionException(String var1) {
         super(var1);
      }
   }

   public static class UnmatchedArgumentException extends CommandLine.ParameterException {
      private static final long serialVersionUID = -8700426380701452440L;

      public UnmatchedArgumentException(CommandLine var1, String var2) {
         super(var1, var2);
      }

      public UnmatchedArgumentException(CommandLine var1, Stack<String> var2) {
         this(var1, new ArrayList<>(CommandLine.reverse(var2)));
      }

      public UnmatchedArgumentException(CommandLine var1, List<String> var2) {
         this(var1, "Unmatched argument" + (var2.size() == 1 ? " " : "s ") + var2);
      }
   }
}
