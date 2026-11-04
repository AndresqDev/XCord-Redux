package net.md_5.bungee.entitymap;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import java.io.DataInputStream;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import se.llbit.nbt.NamedTag;
import se.llbit.nbt.Tag;

public abstract class EntityMap {
   private final boolean[] clientboundInts = new boolean[256];
   private final boolean[] clientboundVarInts = new boolean[256];
   private final boolean[] serverboundInts = new boolean[256];
   private final boolean[] serverboundVarInts = new boolean[256];

   public static EntityMap getEntityMap(int var0) {
      return getEntityMap(var0, false);
   }

   public static EntityMap getEntityMap(int var0, boolean var1) {
      if (ProxyServer.getInstance().getConfig().isDisableEntityMetadataRewrite() && !var1) {
         return EntityMap_Dummy.INSTANCE;
      } else {
         switch (var0) {
            case 4:
               return EntityMap_1_7_2.INSTANCE;
            case 5:
               return EntityMap_1_7_6.INSTANCE;
            case 47:
               return EntityMap_1_8.INSTANCE;
            case 107:
            case 108:
            case 109:
               return EntityMap_1_9.INSTANCE;
            case 110:
               return EntityMap_1_9_4.INSTANCE;
            case 210:
               return EntityMap_1_10.INSTANCE;
            case 315:
            case 316:
               return EntityMap_1_11.INSTANCE;
            case 335:
               return EntityMap_1_12.INSTANCE;
            case 338:
            case 340:
               return EntityMap_1_12_1.INSTANCE;
            case 393:
            case 401:
            case 404:
               return EntityMap_1_13.INSTANCE;
            case 477:
            case 480:
            case 485:
            case 490:
            case 498:
               return EntityMap_1_14.INSTANCE;
            case 573:
            case 575:
            case 578:
               return EntityMap_1_15.INSTANCE;
            case 735:
            case 736:
               return EntityMap_1_16.INSTANCE;
            case 751:
            case 753:
            case 754:
               return EntityMap_1_16_2.INSTANCE_1_16_2;
            case 755:
            case 756:
               return EntityMap_1_16_2.INSTANCE_1_17;
            case 757:
            case 758:
               return EntityMap_1_16_2.INSTANCE_1_18;
            case 759:
               return EntityMap_1_16_2.INSTANCE_1_19;
            case 760:
               return EntityMap_1_16_2.INSTANCE_1_19_1;
            default:
               throw new RuntimeException("Version " + var0 + " has no entity map");
         }
      }
   }

   protected void addRewrite(int var1, ProtocolConstants.Direction var2, boolean var3) {
      if (var2 == ProtocolConstants.Direction.TO_CLIENT) {
         if (var3) {
            this.clientboundVarInts[var1] = true;
         } else {
            this.clientboundInts[var1] = true;
         }
      } else if (var3) {
         this.serverboundVarInts[var1] = true;
      } else {
         this.serverboundInts[var1] = true;
      }
   }

   public void rewriteServerbound(ByteBuf var1, int var2, int var3) {
      rewrite(var1, var2, var3, this.serverboundInts, this.serverboundVarInts);
   }

   public void rewriteServerbound(ByteBuf var1, int var2, int var3, int var4) {
      this.rewriteServerbound(var1, var2, var3);
   }

   public void rewriteClientbound(ByteBuf var1, int var2, int var3) {
      rewrite(var1, var2, var3, this.clientboundInts, this.clientboundVarInts);
   }

   public void rewriteClientbound(ByteBuf var1, int var2, int var3, int var4) {
      this.rewriteClientbound(var1, var2, var3);
   }

   protected static void rewriteInt(ByteBuf var0, int var1, int var2, int var3) {
      int var4 = var0.getInt(var3);
      if (var4 == var1) {
         var0.setInt(var3, var2);
      } else if (var4 == var2) {
         var0.setInt(var3, var1);
      }
   }

   @SuppressFBWarnings({"DLS_DEAD_LOCAL_STORE"})
   protected static void rewriteVarInt(ByteBuf var0, int var1, int var2, int var3) {
      int var4 = DefinedPacket.readVarInt(var0);
      int var5 = var0.readerIndex() - var3;
      if (var4 == var1 || var4 == var2) {
         ByteBuf var6 = var0.copy();
         var0.readerIndex(var3);
         var0.writerIndex(var3);
         DefinedPacket.writeVarInt(var4 == var1 ? var2 : var1, var0);
         var0.writeBytes(var6);
         var6.release();
      }
   }

   protected static void rewriteMetaVarInt(ByteBuf var0, int var1, int var2, int var3) {
      rewriteMetaVarInt(var0, var1, var2, var3, -1);
   }

   protected static void rewriteMetaVarInt(ByteBuf var0, int var1, int var2, int var3, int var4) {
      int var5 = var0.readerIndex();

      short var6;
      while ((var6 = var0.readUnsignedByte()) != 255) {
         int var7 = DefinedPacket.readVarInt(var0);
         if (var4 >= 393) {
            switch (var7) {
               case 5:
                  if (var0.readBoolean()) {
                     DefinedPacket.readString(var0);
                  }
                  continue;
               case 15:
                  int var11 = DefinedPacket.readVarInt(var0);
                  if (var4 >= 477) {
                     switch (var11) {
                        case 3:
                        case 23:
                           DefinedPacket.readVarInt(var0);
                           continue;
                        case 14:
                           var0.skipBytes(16);
                           continue;
                        case 32:
                           readSkipSlot(var0, var4);
                     }
                  } else {
                     switch (var11) {
                        case 3:
                        case 20:
                           DefinedPacket.readVarInt(var0);
                           continue;
                        case 11:
                           var0.skipBytes(16);
                           continue;
                        case 27:
                           readSkipSlot(var0, var4);
                     }
                  }
                  continue;
               default:
                  if (var7 >= 6) {
                     var7--;
                  }
            }
         }

         switch (var7) {
            case 0:
               var0.skipBytes(1);
               break;
            case 1:
               if (var6 == var3) {
                  int var10 = var0.readerIndex();
                  rewriteVarInt(var0, var1, var2, var10);
                  var0.readerIndex(var10);
               }

               DefinedPacket.readVarInt(var0);
               break;
            case 2:
               var0.skipBytes(4);
               break;
            case 3:
            case 4:
               DefinedPacket.readString(var0);
               break;
            case 5:
               readSkipSlot(var0, var4);
               break;
            case 6:
               var0.skipBytes(1);
               break;
            case 7:
               var0.skipBytes(12);
               break;
            case 8:
               var0.readLong();
               break;
            case 9:
               if (var0.readBoolean()) {
                  var0.skipBytes(8);
               }
               break;
            case 10:
               DefinedPacket.readVarInt(var0);
               break;
            case 11:
               if (var0.readBoolean()) {
                  var0.skipBytes(16);
               }
               break;
            case 12:
               DefinedPacket.readVarInt(var0);
               break;
            case 13:
               Tag var8 = NamedTag.read(new DataInputStream(new ByteBufInputStream(var0)));
               if (var8.isError()) {
                  throw new RuntimeException(var8.error());
               }
               break;
            case 14:
            default:
               if (var4 >= 393) {
                  var7++;
               }

               throw new IllegalArgumentException("Unknown meta type " + var7 + ": Using mods? refer to disable_entity_metadata_rewrite in waterfall.yml");
            case 15:
               DefinedPacket.readVarInt(var0);
               DefinedPacket.readVarInt(var0);
               DefinedPacket.readVarInt(var0);
               break;
            case 16:
               if (var6 == var3) {
                  int var9 = var0.readerIndex();
                  rewriteVarInt(var0, var1 + 1, var2 + 1, var9);
                  var0.readerIndex(var9);
               }

               DefinedPacket.readVarInt(var0);
               break;
            case 17:
               DefinedPacket.readVarInt(var0);
         }
      }

      var0.readerIndex(var5);
   }

   private static void readSkipSlot(ByteBuf var0, int var1) {
      if (var1 >= 404 ? var0.readBoolean() : var0.readShort() != -1) {
         if (var1 >= 404) {
            DefinedPacket.readVarInt(var0);
         }

         var0.skipBytes(var1 >= 393 ? 1 : 3);
         int var2 = var0.readerIndex();
         if (var0.readByte() != 0) {
            var0.readerIndex(var2);
            Tag var3 = NamedTag.read(new DataInputStream(new ByteBufInputStream(var0)));
            if (var3.isError()) {
               throw new RuntimeException(var3.error());
            }
         }
      }
   }

   private static void rewrite(ByteBuf var0, int var1, int var2, boolean[] var3, boolean[] var4) {
      int var5 = var0.readerIndex();
      int var6 = DefinedPacket.readVarInt(var0);
      int var7 = var0.readerIndex() - var5;
      if (var6 >= 0 && var6 <= var3.length && var6 <= var4.length) {
         if (var3[var6]) {
            rewriteInt(var0, var1, var2, var5 + var7);
         } else if (var4[var6]) {
            rewriteVarInt(var0, var1, var2, var5 + var7);
         }

         var0.readerIndex(var5);
      } else {
         var0.readerIndex(var5);
      }
   }

   EntityMap() {
   }
}
