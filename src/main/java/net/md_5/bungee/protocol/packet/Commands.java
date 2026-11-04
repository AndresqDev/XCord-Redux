package net.md_5.bungee.protocol.packet;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import io.netty.buffer.ByteBuf;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class Commands extends DefinedPacket {
   private static final int FLAG_TYPE = 3;
   private static final int FLAG_EXECUTABLE = 4;
   private static final int FLAG_REDIRECT = 8;
   private static final int FLAG_SUGGESTIONS = 16;
   private static final int NODE_ROOT = 0;
   private static final int NODE_LITERAL = 1;
   private static final int NODE_ARGUMENT = 2;
   private RootCommandNode root;

   @Override
   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      int var4 = readVarInt(var1);
      Commands.NetworkNode[] var5 = new Commands.NetworkNode[var4];
      ArrayDeque var6 = new ArrayDeque(var5.length);

      for (int var7 = 0; var7 < var4; var7++) {
         int var8 = var1.readByte();
         int[] var9 = readVarIntArray(var1);
         int var10 = (var8 & 8) != 0 ? readVarInt(var1) : 0;
         Object var11;
         switch (var8 & 3) {
            case 0:
               var11 = null;
               break;
            case 1:
               var11 = LiteralArgumentBuilder.literal(readString(var1));
               break;
            case 2:
               String var12 = readString(var1);
               var11 = RequiredArgumentBuilder.argument(var12, Commands.ArgumentRegistry.read(var1, var3));
               if ((var8 & 16) != 0) {
                  String var13 = readString(var1);
                  ((RequiredArgumentBuilder)var11).suggests(Commands.SuggestionRegistry.getProvider(var13));
               }
               break;
            default:
               throw new IllegalArgumentException("Unhandled node type " + var8);
         }

         Commands.NetworkNode var18 = new Commands.NetworkNode((ArgumentBuilder)var11, (byte)var8, var10, var9);
         var5[var7] = var18;
         var6.add(var18);
      }

      while (!var6.isEmpty()) {
         boolean var14 = false;
         Iterator var15 = var6.iterator();

         while (var15.hasNext()) {
            Commands.NetworkNode var17 = (Commands.NetworkNode)var15.next();
            if (var17.buildSelf(var5)) {
               var15.remove();
               var14 = true;
            }
         }

         if (!var14) {
            throw new IllegalStateException("Did not finish building root node");
         }
      }

      int var16 = readVarInt(var1);
      this.root = (RootCommandNode)var5[var16].command;
   }

   @Override
   public void write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      LinkedHashMap var4 = new LinkedHashMap();
      ArrayDeque var5 = new ArrayDeque();
      var5.add(this.root);

      while (!var5.isEmpty()) {
         CommandNode var6 = (CommandNode)var5.pollFirst();
         if (!var4.containsKey(var6)) {
            int var7 = var4.size();
            var4.put(var6, var7);
            var5.addAll(var6.getChildren());
            if (var6.getRedirect() != null) {
               var5.add(var6.getRedirect());
            }
         }
      }

      writeVarInt(var4.size(), var1);
      int var13 = 0;

      for (Entry var8 : var4.entrySet()) {
         Preconditions.checkState((Integer)var8.getValue() == var13++, "Iteration out of order!");
         CommandNode var9 = (CommandNode)var8.getKey();
         byte var10 = 0;
         if (var9.getRedirect() != null) {
            var10 = (byte)(var10 | 8);
         }

         if (var9.getCommand() != null) {
            var10 = (byte)(var10 | 4);
         }

         if (var9 instanceof RootCommandNode) {
            var10 = (byte)(var10 | 0);
         } else if (var9 instanceof LiteralCommandNode) {
            var10 = (byte)(var10 | 1);
         } else {
            if (!(var9 instanceof ArgumentCommandNode)) {
               throw new IllegalArgumentException("Unhandled node type " + var9);
            }

            var10 = (byte)(var10 | 2);
            if (((ArgumentCommandNode)var9).getCustomSuggestions() != null) {
               var10 = (byte)(var10 | 16);
            }
         }

         var1.writeByte(var10);
         writeVarInt(var9.getChildren().size(), var1);

         for (CommandNode var12 : var9.getChildren()) {
            writeVarInt((Integer)var4.get(var12), var1);
         }

         if (var9.getRedirect() != null) {
            writeVarInt((Integer)var4.get(var9.getRedirect()), var1);
         }

         if (var9 instanceof LiteralCommandNode) {
            writeString(((LiteralCommandNode)var9).getLiteral(), var1);
         } else if (var9 instanceof ArgumentCommandNode) {
            ArgumentCommandNode var17 = (ArgumentCommandNode)var9;
            writeString(var17.getName(), var1);
            Commands.ArgumentRegistry.write(var17.getType(), var1, var3);
            if (var17.getCustomSuggestions() != null) {
               writeString(Commands.SuggestionRegistry.getKey(var17.getCustomSuggestions()), var1);
            }
         }
      }

      int var15 = (Integer)var4.get(this.root);
      Preconditions.checkState(var15 == 0, "How did root not land up at index 0?!?");
      writeVarInt(var15, var1);
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   private static byte binaryFlag(boolean var0, boolean var1) {
      byte var2 = 0;
      if (var0) {
         var2 = (byte)(var2 | 1);
      }

      if (var1) {
         var2 = (byte)(var2 | 2);
      }

      return var2;
   }

   public RootCommandNode getRoot() {
      return this.root;
   }

   public void setRoot(RootCommandNode var1) {
      this.root = var1;
   }

   @Override
   public String toString() {
      return "Commands(root=" + this.getRoot() + ")";
   }

   public Commands() {
   }

   public Commands(RootCommandNode var1) {
      this.root = var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Commands)) {
         return false;
      } else {
         Commands var2 = (Commands)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            RootCommandNode var3 = this.getRoot();
            RootCommandNode var4 = var2.getRoot();
            return var3 == null ? var4 == null : var3.equals(var4);
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof Commands;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      byte var2 = 1;
      RootCommandNode var3 = this.getRoot();
      return var2 * 59 + (var3 == null ? 43 : var3.hashCode());
   }

   private static class ArgumentRegistry {
      private static final Map<String, Commands.ArgumentRegistry.ArgumentSerializer> PROVIDERS = new HashMap<>();
      private static final List<Commands.ArgumentRegistry.ArgumentSerializer> PROVIDER_LIST = new ArrayList<>();
      private static final Map<Class<?>, Commands.ArgumentRegistry.ProperArgumentSerializer<?>> PROPER_PROVIDERS = new HashMap<>();
      private static final Commands.ArgumentRegistry.ArgumentSerializer<Void> VOID = new Commands.ArgumentRegistry.ArgumentSerializer<Void>() {
         protected Void read(ByteBuf var1) {
            return null;
         }

         protected void write(ByteBuf var1, Void var2) {
         }
      };
      private static final Commands.ArgumentRegistry.ArgumentSerializer<Boolean> BOOLEAN = new Commands.ArgumentRegistry.ArgumentSerializer<Boolean>() {
         protected Boolean read(ByteBuf var1) {
            return var1.readBoolean();
         }

         protected void write(ByteBuf var1, Boolean var2) {
            var1.writeBoolean(var2);
         }
      };
      private static final Commands.ArgumentRegistry.ArgumentSerializer<Byte> BYTE = new Commands.ArgumentRegistry.ArgumentSerializer<Byte>() {
         protected Byte read(ByteBuf var1) {
            return var1.readByte();
         }

         protected void write(ByteBuf var1, Byte var2) {
            var1.writeByte(var2);
         }
      };
      private static final Commands.ArgumentRegistry.ArgumentSerializer<FloatArgumentType> FLOAT = new Commands.ArgumentRegistry.ArgumentSerializer<FloatArgumentType>() {
         protected FloatArgumentType read(ByteBuf var1) {
            byte var2 = var1.readByte();
            float var3 = (var2 & 1) != 0 ? var1.readFloat() : -Float.MAX_VALUE;
            float var4 = (var2 & 2) != 0 ? var1.readFloat() : Float.MAX_VALUE;
            return FloatArgumentType.floatArg(var3, var4);
         }

         protected void write(ByteBuf var1, FloatArgumentType var2) {
            boolean var3 = var2.getMinimum() != -Float.MAX_VALUE;
            boolean var4 = var2.getMaximum() != Float.MAX_VALUE;
            var1.writeByte(Commands.binaryFlag(var3, var4));
            if (var3) {
               var1.writeFloat(var2.getMinimum());
            }

            if (var4) {
               var1.writeFloat(var2.getMaximum());
            }
         }
      };
      private static final Commands.ArgumentRegistry.ArgumentSerializer<DoubleArgumentType> DOUBLE = new Commands.ArgumentRegistry.ArgumentSerializer<DoubleArgumentType>() {
         protected DoubleArgumentType read(ByteBuf var1) {
            byte var2 = var1.readByte();
            double var3 = (var2 & 1) != 0 ? var1.readDouble() : -Double.MAX_VALUE;
            double var5 = (var2 & 2) != 0 ? var1.readDouble() : Double.MAX_VALUE;
            return DoubleArgumentType.doubleArg(var3, var5);
         }

         protected void write(ByteBuf var1, DoubleArgumentType var2) {
            boolean var3 = var2.getMinimum() != -Double.MAX_VALUE;
            boolean var4 = var2.getMaximum() != Double.MAX_VALUE;
            var1.writeByte(Commands.binaryFlag(var3, var4));
            if (var3) {
               var1.writeDouble(var2.getMinimum());
            }

            if (var4) {
               var1.writeDouble(var2.getMaximum());
            }
         }
      };
      private static final Commands.ArgumentRegistry.ArgumentSerializer<IntegerArgumentType> INTEGER = new Commands.ArgumentRegistry.ArgumentSerializer<IntegerArgumentType>() {
         protected IntegerArgumentType read(ByteBuf var1) {
            byte var2 = var1.readByte();
            int var3 = (var2 & 1) != 0 ? var1.readInt() : Integer.MIN_VALUE;
            int var4 = (var2 & 2) != 0 ? var1.readInt() : Integer.MAX_VALUE;
            return IntegerArgumentType.integer(var3, var4);
         }

         protected void write(ByteBuf var1, IntegerArgumentType var2) {
            boolean var3 = var2.getMinimum() != Integer.MIN_VALUE;
            boolean var4 = var2.getMaximum() != Integer.MAX_VALUE;
            var1.writeByte(Commands.binaryFlag(var3, var4));
            if (var3) {
               var1.writeInt(var2.getMinimum());
            }

            if (var4) {
               var1.writeInt(var2.getMaximum());
            }
         }
      };
      private static final Commands.ArgumentRegistry.ArgumentSerializer<LongArgumentType> LONG = new Commands.ArgumentRegistry.ArgumentSerializer<LongArgumentType>() {
         protected LongArgumentType read(ByteBuf var1) {
            byte var2 = var1.readByte();
            long var3 = (var2 & 1) != 0 ? var1.readLong() : Long.MIN_VALUE;
            long var5 = (var2 & 2) != 0 ? var1.readLong() : Long.MAX_VALUE;
            return LongArgumentType.longArg(var3, var5);
         }

         protected void write(ByteBuf var1, LongArgumentType var2) {
            boolean var3 = var2.getMinimum() != Long.MIN_VALUE;
            boolean var4 = var2.getMaximum() != Long.MAX_VALUE;
            var1.writeByte(Commands.binaryFlag(var3, var4));
            if (var3) {
               var1.writeLong(var2.getMinimum());
            }

            if (var4) {
               var1.writeLong(var2.getMaximum());
            }
         }
      };
      private static final Commands.ArgumentRegistry.ProperArgumentSerializer<StringArgumentType> STRING = new Commands.ArgumentRegistry.ProperArgumentSerializer<StringArgumentType>() {
         protected StringArgumentType read(ByteBuf var1) {
            int var2 = DefinedPacket.readVarInt(var1);
            switch (var2) {
               case 0:
                  return StringArgumentType.word();
               case 1:
                  return StringArgumentType.string();
               case 2:
                  return StringArgumentType.greedyString();
               default:
                  throw new IllegalArgumentException("Unknown string type " + var2);
            }
         }

         protected void write(ByteBuf var1, StringArgumentType var2) {
            DefinedPacket.writeVarInt(var2.getType().ordinal(), var1);
         }

         @Override
         protected int getIntKey() {
            return 5;
         }

         @Override
         protected String getKey() {
            return "brigadier:string";
         }
      };
      private static final Commands.ArgumentRegistry.ArgumentSerializer<String> RAW_STRING = new Commands.ArgumentRegistry.ArgumentSerializer<String>() {
         protected String read(ByteBuf var1) {
            return DefinedPacket.readString(var1);
         }

         protected void write(ByteBuf var1, String var2) {
            DefinedPacket.writeString(var2, var1);
         }
      };

      private static void register(String var0, Commands.ArgumentRegistry.ArgumentSerializer var1) {
         PROVIDERS.put(var0, var1);
         PROVIDER_LIST.add(var1);
      }

      private static ArgumentType<?> read(ByteBuf var0, int var1) {
         Object var2;
         Commands.ArgumentRegistry.ArgumentSerializer var3;
         if (var1 >= 759) {
            var2 = DefinedPacket.readVarInt(var0);
            var3 = PROVIDER_LIST.get((Integer)var2);
         } else {
            var2 = DefinedPacket.readString(var0);
            var3 = PROVIDERS.get((String)var2);
         }

         Preconditions.checkArgument(var3 != null, "No provider for argument " + var2);
         Object var4 = var3.read(var0);
         return (ArgumentType<?>)(var4 != null && PROPER_PROVIDERS.containsKey(var4.getClass())
            ? (ArgumentType)var4
            : new Commands.ArgumentRegistry.DummyType<>(var2, var3, var4));
      }

      private static void write(ArgumentType<?> var0, ByteBuf var1, int var2) {
         Commands.ArgumentRegistry.ProperArgumentSerializer var3 = PROPER_PROVIDERS.get(var0.getClass());
         if (var3 != null) {
            if (var2 >= 759) {
               DefinedPacket.writeVarInt(var3.getIntKey(), var1);
            } else {
               DefinedPacket.writeString(var3.getKey(), var1);
            }

            var3.write(var1, var0);
         } else {
            Preconditions.checkArgument(var0 instanceof Commands.ArgumentRegistry.DummyType, "Non dummy arg " + var0.getClass());
            Commands.ArgumentRegistry.DummyType var4 = (Commands.ArgumentRegistry.DummyType)var0;
            if (var4.key instanceof Integer) {
               DefinedPacket.writeVarInt((Integer)var4.key, var1);
            } else {
               DefinedPacket.writeString((String)var4.key, var1);
            }

            var4.serializer.write(var1, var4.value);
         }
      }

      public ArgumentRegistry() {
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof Commands.ArgumentRegistry)) {
            return false;
         } else {
            Commands.ArgumentRegistry var2 = (Commands.ArgumentRegistry)var1;
            return var2.canEqual(this);
         }
      }

      protected boolean canEqual(Object var1) {
         return var1 instanceof Commands.ArgumentRegistry;
      }

      @Override
      public int hashCode() {
         boolean var1 = true;
         return 1;
      }

      @Override
      public String toString() {
         return "Commands.ArgumentRegistry()";
      }

      static {
         register("brigadier:bool", VOID);
         register("brigadier:float", FLOAT);
         register("brigadier:double", DOUBLE);
         register("brigadier:integer", INTEGER);
         register("brigadier:long", LONG);
         register("brigadier:string", STRING);
         PROPER_PROVIDERS.put(StringArgumentType.class, STRING);
         register("minecraft:entity", BYTE);
         register("minecraft:game_profile", VOID);
         register("minecraft:block_pos", VOID);
         register("minecraft:column_pos", VOID);
         register("minecraft:vec3", VOID);
         register("minecraft:vec2", VOID);
         register("minecraft:block_state", VOID);
         register("minecraft:block_predicate", VOID);
         register("minecraft:item_stack", VOID);
         register("minecraft:item_predicate", VOID);
         register("minecraft:color", VOID);
         register("minecraft:component", VOID);
         register("minecraft:message", VOID);
         register("minecraft:nbt_compound_tag", VOID);
         register("minecraft:nbt_tag", VOID);
         register("minecraft:nbt_path", VOID);
         register("minecraft:objective", VOID);
         register("minecraft:objective_criteria", VOID);
         register("minecraft:operation", VOID);
         register("minecraft:particle", VOID);
         register("minecraft:angle", VOID);
         register("minecraft:rotation", VOID);
         register("minecraft:scoreboard_slot", VOID);
         register("minecraft:score_holder", BYTE);
         register("minecraft:swizzle", VOID);
         register("minecraft:team", VOID);
         register("minecraft:item_slot", VOID);
         register("minecraft:resource_location", VOID);
         register("minecraft:mob_effect", VOID);
         register("minecraft:function", VOID);
         register("minecraft:entity_anchor", VOID);
         register("minecraft:int_range", VOID);
         register("minecraft:float_range", VOID);
         register("minecraft:item_enchantment", VOID);
         register("minecraft:entity_summon", VOID);
         register("minecraft:dimension", VOID);
         register("minecraft:time", VOID);
         register("minecraft:resource_or_tag", RAW_STRING);
         register("minecraft:resource", RAW_STRING);
         register("minecraft:template_mirror", VOID);
         register("minecraft:template_rotation", VOID);
         register("minecraft:uuid", VOID);
         register("minecraft:nbt", VOID);
      }

      private abstract static class ArgumentSerializer<T> {
         private ArgumentSerializer() {
         }

         protected abstract T read(ByteBuf var1);

         protected abstract void write(ByteBuf var1, T var2);
      }

      private static class DummyType<T> implements ArgumentType<T> {
         private final Object key;
         private final Commands.ArgumentRegistry.ArgumentSerializer<T> serializer;
         private final T value;

         public T parse(StringReader var1) throws CommandSyntaxException {
            throw new UnsupportedOperationException("Not supported.");
         }

         public DummyType(Object var1, Commands.ArgumentRegistry.ArgumentSerializer<T> var2, T var3) {
            this.key = var1;
            this.serializer = var2;
            this.value = (T)var3;
         }

         public Object getKey() {
            return this.key;
         }

         public Commands.ArgumentRegistry.ArgumentSerializer<T> getSerializer() {
            return this.serializer;
         }

         public T getValue() {
            return this.value;
         }

         @Override
         public boolean equals(Object var1) {
            if (var1 == this) {
               return true;
            } else if (!(var1 instanceof Commands.ArgumentRegistry.DummyType)) {
               return false;
            } else {
               Commands.ArgumentRegistry.DummyType var2 = (Commands.ArgumentRegistry.DummyType)var1;
               if (!var2.canEqual(this)) {
                  return false;
               } else {
                  Object var3 = this.getKey();
                  Object var4 = var2.getKey();
                  if (var3 == null ? var4 == null : var3.equals(var4)) {
                     Commands.ArgumentRegistry.ArgumentSerializer var5 = this.getSerializer();
                     Commands.ArgumentRegistry.ArgumentSerializer var6 = var2.getSerializer();
                     if (var5 == null ? var6 == null : var5.equals(var6)) {
                        Object var7 = this.getValue();
                        Object var8 = var2.getValue();
                        return var7 == null ? var8 == null : var7.equals(var8);
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               }
            }
         }

         protected boolean canEqual(Object var1) {
            return var1 instanceof Commands.ArgumentRegistry.DummyType;
         }

         @Override
         public int hashCode() {
            byte var1 = 59;
            int var2 = 1;
            Object var3 = this.getKey();
            var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
            Commands.ArgumentRegistry.ArgumentSerializer var4 = this.getSerializer();
            var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
            Object var5 = this.getValue();
            return var2 * 59 + (var5 == null ? 43 : var5.hashCode());
         }

         @Override
         public String toString() {
            return "Commands.ArgumentRegistry.DummyType(key=" + this.getKey() + ", serializer=" + this.getSerializer() + ", value=" + this.getValue() + ")";
         }
      }

      private abstract static class ProperArgumentSerializer<T> extends Commands.ArgumentRegistry.ArgumentSerializer<T> {
         private ProperArgumentSerializer() {
         }

         protected abstract int getIntKey();

         protected abstract String getKey();
      }
   }

   private static class NetworkNode {
      private final ArgumentBuilder argumentBuilder;
      private final byte flags;
      private final int redirectNode;
      private final int[] children;
      private CommandNode command;

      private boolean buildSelf(Commands.NetworkNode[] var1) {
         if (this.command == null) {
            if (this.argumentBuilder == null) {
               this.command = new RootCommandNode();
            } else {
               if ((this.flags & 8) != 0) {
                  if (var1[this.redirectNode].command == null) {
                     return false;
                  }

                  this.argumentBuilder.redirect(var1[this.redirectNode].command);
               }

               if ((this.flags & 4) != 0) {
                  this.argumentBuilder.executes(new Command() {
                     public int run(CommandContext var1) throws CommandSyntaxException {
                        return 0;
                     }
                  });
               }

               this.command = this.argumentBuilder.build();
            }
         }

         for (int var5 : this.children) {
            if (var1[var5].command == null) {
               return false;
            }
         }

         for (int var10 : this.children) {
            CommandNode var6 = var1[var10].command;
            Preconditions.checkArgument(!(var6 instanceof RootCommandNode), "Cannot have RootCommandNode as child");
            this.command.addChild(var6);
         }

         return true;
      }

      public NetworkNode(ArgumentBuilder var1, byte var2, int var3, int[] var4) {
         this.argumentBuilder = var1;
         this.flags = var2;
         this.redirectNode = var3;
         this.children = var4;
      }

      public ArgumentBuilder getArgumentBuilder() {
         return this.argumentBuilder;
      }

      public byte getFlags() {
         return this.flags;
      }

      public int getRedirectNode() {
         return this.redirectNode;
      }

      public int[] getChildren() {
         return this.children;
      }

      public CommandNode getCommand() {
         return this.command;
      }

      public void setCommand(CommandNode var1) {
         this.command = var1;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof Commands.NetworkNode)) {
            return false;
         } else {
            Commands.NetworkNode var2 = (Commands.NetworkNode)var1;
            if (!var2.canEqual(this)) {
               return false;
            } else if (this.getFlags() != var2.getFlags()) {
               return false;
            } else if (this.getRedirectNode() != var2.getRedirectNode()) {
               return false;
            } else {
               ArgumentBuilder var3 = this.getArgumentBuilder();
               ArgumentBuilder var4 = var2.getArgumentBuilder();
               if (var3 == null ? var4 == null : var3.equals(var4)) {
                  if (!Arrays.equals(this.getChildren(), var2.getChildren())) {
                     return false;
                  } else {
                     CommandNode var5 = this.getCommand();
                     CommandNode var6 = var2.getCommand();
                     return var5 == null ? var6 == null : var5.equals(var6);
                  }
               } else {
                  return false;
               }
            }
         }
      }

      protected boolean canEqual(Object var1) {
         return var1 instanceof Commands.NetworkNode;
      }

      @Override
      public int hashCode() {
         byte var1 = 59;
         int var2 = 1;
         var2 = var2 * 59 + this.getFlags();
         var2 = var2 * 59 + this.getRedirectNode();
         ArgumentBuilder var3 = this.getArgumentBuilder();
         var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
         var2 = var2 * 59 + Arrays.hashCode(this.getChildren());
         CommandNode var4 = this.getCommand();
         return var2 * 59 + (var4 == null ? 43 : var4.hashCode());
      }

      @Override
      public String toString() {
         return "Commands.NetworkNode(argumentBuilder="
            + this.getArgumentBuilder()
            + ", flags="
            + this.getFlags()
            + ", redirectNode="
            + this.getRedirectNode()
            + ", children="
            + Arrays.toString(this.getChildren())
            + ", command="
            + this.getCommand()
            + ")";
      }
   }

   public static class SuggestionRegistry {
      public static final SuggestionProvider ASK_SERVER = new Commands.SuggestionRegistry.DummyProvider("minecraft:ask_server");
      private static final Map<String, SuggestionProvider<Commands.SuggestionRegistry.DummyProvider>> PROVIDERS = new HashMap<>();

      private static void registerDummy(String var0) {
         PROVIDERS.put(var0, new Commands.SuggestionRegistry.DummyProvider(var0));
      }

      private static SuggestionProvider<Commands.SuggestionRegistry.DummyProvider> getProvider(String var0) {
         SuggestionProvider var1 = PROVIDERS.get(var0);
         Preconditions.checkArgument(var1 != null, "Unknown completion provider " + var0);
         return var1;
      }

      private static String getKey(SuggestionProvider<Commands.SuggestionRegistry.DummyProvider> var0) {
         Preconditions.checkArgument(var0 instanceof Commands.SuggestionRegistry.DummyProvider, "Non dummy provider " + var0);
         return ((Commands.SuggestionRegistry.DummyProvider)var0).key;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof Commands.SuggestionRegistry)) {
            return false;
         } else {
            Commands.SuggestionRegistry var2 = (Commands.SuggestionRegistry)var1;
            return var2.canEqual(this);
         }
      }

      protected boolean canEqual(Object var1) {
         return var1 instanceof Commands.SuggestionRegistry;
      }

      @Override
      public int hashCode() {
         boolean var1 = true;
         return 1;
      }

      @Override
      public String toString() {
         return "Commands.SuggestionRegistry()";
      }

      static {
         PROVIDERS.put("minecraft:ask_server", ASK_SERVER);
         registerDummy("minecraft:all_recipes");
         registerDummy("minecraft:available_sounds");
         registerDummy("minecraft:available_biomes");
         registerDummy("minecraft:summonable_entities");
      }

      private static final class DummyProvider implements SuggestionProvider<Commands.SuggestionRegistry.DummyProvider> {
         private final String key;

         public CompletableFuture<Suggestions> getSuggestions(CommandContext<Commands.SuggestionRegistry.DummyProvider> var1, SuggestionsBuilder var2) throws CommandSyntaxException {
            return var2.buildFuture();
         }

         public DummyProvider(String var1) {
            this.key = var1;
         }

         public String getKey() {
            return this.key;
         }

         @Override
         public boolean equals(Object var1) {
            if (var1 == this) {
               return true;
            } else if (!(var1 instanceof Commands.SuggestionRegistry.DummyProvider)) {
               return false;
            } else {
               Commands.SuggestionRegistry.DummyProvider var2 = (Commands.SuggestionRegistry.DummyProvider)var1;
               String var3 = this.getKey();
               String var4 = var2.getKey();
               return var3 == null ? var4 == null : var3.equals(var4);
            }
         }

         @Override
         public int hashCode() {
            byte var1 = 59;
            byte var2 = 1;
            String var3 = this.getKey();
            return var2 * 59 + (var3 == null ? 43 : var3.hashCode());
         }

         @Override
         public String toString() {
            return "Commands.SuggestionRegistry.DummyProvider(key=" + this.getKey() + ")";
         }
      }
   }
}
