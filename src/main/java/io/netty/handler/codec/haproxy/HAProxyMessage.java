package io.netty.handler.codec.haproxy;

import io.netty.buffer.ByteBuf;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ByteProcessor;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetectorFactory;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class HAProxyMessage extends AbstractReferenceCounted {
   private static final ResourceLeakDetector<HAProxyMessage> leakDetector = ResourceLeakDetectorFactory.instance()
      .newResourceLeakDetector(HAProxyMessage.class);
   private final ResourceLeakTracker<HAProxyMessage> leak;
   private final HAProxyProtocolVersion protocolVersion;
   private final HAProxyCommand command;
   private final HAProxyProxiedProtocol proxiedProtocol;
   private final String sourceAddress;
   private final String destinationAddress;
   private final int sourcePort;
   private final int destinationPort;
   private final List<HAProxyTLV> tlvs;

   private HAProxyMessage(HAProxyProtocolVersion var1, HAProxyCommand var2, HAProxyProxiedProtocol var3, String var4, String var5, String var6, String var7) {
      this(var1, var2, var3, var4, var5, portStringToInt(var6), portStringToInt(var7));
   }

   public HAProxyMessage(HAProxyProtocolVersion var1, HAProxyCommand var2, HAProxyProxiedProtocol var3, String var4, String var5, int var6, int var7) {
      this(var1, var2, var3, var4, var5, var6, var7, Collections.emptyList());
   }

   public HAProxyMessage(
      HAProxyProtocolVersion var1,
      HAProxyCommand var2,
      HAProxyProxiedProtocol var3,
      String var4,
      String var5,
      int var6,
      int var7,
      List<? extends HAProxyTLV> var8
   ) {
      ObjectUtil.checkNotNull(var1, "protocolVersion");
      ObjectUtil.checkNotNull(var3, "proxiedProtocol");
      ObjectUtil.checkNotNull(var8, "tlvs");
      HAProxyProxiedProtocol.AddressFamily var9 = var3.addressFamily();
      checkAddress(var4, var9);
      checkAddress(var5, var9);
      checkPort(var6, var9);
      checkPort(var7, var9);
      this.protocolVersion = var1;
      this.command = var2;
      this.proxiedProtocol = var3;
      this.sourceAddress = var4;
      this.destinationAddress = var5;
      this.sourcePort = var6;
      this.destinationPort = var7;
      this.tlvs = Collections.unmodifiableList(var8);
      this.leak = leakDetector.track(this);
   }

   static HAProxyMessage decodeHeader(ByteBuf var0) {
      ObjectUtil.checkNotNull(var0, "header");
      if (var0.readableBytes() < 16) {
         throw new HAProxyProtocolException("incomplete header: " + var0.readableBytes() + " bytes (expected: 16+ bytes)");
      } else {
         var0.skipBytes(12);
         byte var1 = var0.readByte();

         HAProxyProtocolVersion var2;
         try {
            var2 = HAProxyProtocolVersion.valueOf(var1);
         } catch (IllegalArgumentException var16) {
            throw new HAProxyProtocolException(var16);
         }

         if (var2 != HAProxyProtocolVersion.V2) {
            throw new HAProxyProtocolException("version 1 unsupported: 0x" + Integer.toHexString(var1));
         } else {
            HAProxyCommand var3;
            try {
               var3 = HAProxyCommand.valueOf(var1);
            } catch (IllegalArgumentException var15) {
               throw new HAProxyProtocolException(var15);
            }

            if (var3 == HAProxyCommand.LOCAL) {
               return unknownMsg(HAProxyProtocolVersion.V2, HAProxyCommand.LOCAL);
            } else {
               HAProxyProxiedProtocol var4;
               try {
                  var4 = HAProxyProxiedProtocol.valueOf(var0.readByte());
               } catch (IllegalArgumentException var14) {
                  throw new HAProxyProtocolException(var14);
               }

               if (var4 == HAProxyProxiedProtocol.UNKNOWN) {
                  return unknownMsg(HAProxyProtocolVersion.V2, HAProxyCommand.PROXY);
               } else {
                  int var5 = var0.readUnsignedShort();
                  int var9 = 0;
                  int var10 = 0;
                  HAProxyProxiedProtocol.AddressFamily var11 = var4.addressFamily();
                  String var6;
                  String var7;
                  if (var11 == HAProxyProxiedProtocol.AddressFamily.AF_UNIX) {
                     if (var5 < 216 || var0.readableBytes() < 216) {
                        throw new HAProxyProtocolException(
                           "incomplete UNIX socket address information: " + Math.min(var5, var0.readableBytes()) + " bytes (expected: 216+ bytes)"
                        );
                     }

                     int var12 = var0.readerIndex();
                     int var13 = var0.forEachByte(var12, 108, ByteProcessor.FIND_NUL);
                     int var8;
                     if (var13 == -1) {
                        var8 = 108;
                     } else {
                        var8 = var13 - var12;
                     }

                     var6 = var0.toString(var12, var8, CharsetUtil.US_ASCII);
                     var12 += 108;
                     var13 = var0.forEachByte(var12, 108, ByteProcessor.FIND_NUL);
                     if (var13 == -1) {
                        var8 = 108;
                     } else {
                        var8 = var13 - var12;
                     }

                     var7 = var0.toString(var12, var8, CharsetUtil.US_ASCII);
                     var0.readerIndex(var12 + 108);
                  } else {
                     byte var18;
                     if (var11 == HAProxyProxiedProtocol.AddressFamily.AF_IPv4) {
                        if (var5 < 12 || var0.readableBytes() < 12) {
                           throw new HAProxyProtocolException(
                              "incomplete IPv4 address information: " + Math.min(var5, var0.readableBytes()) + " bytes (expected: 12+ bytes)"
                           );
                        }

                        var18 = 4;
                     } else {
                        if (var11 != HAProxyProxiedProtocol.AddressFamily.AF_IPv6) {
                           throw new HAProxyProtocolException("unable to parse address information (unknown address family: " + var11 + ')');
                        }

                        if (var5 < 36 || var0.readableBytes() < 36) {
                           throw new HAProxyProtocolException(
                              "incomplete IPv6 address information: " + Math.min(var5, var0.readableBytes()) + " bytes (expected: 36+ bytes)"
                           );
                        }

                        var18 = 16;
                     }

                     var6 = ipBytesToString(var0, var18);
                     var7 = ipBytesToString(var0, var18);
                     var9 = var0.readUnsignedShort();
                     var10 = var0.readUnsignedShort();
                  }

                  List var20 = readTlvs(var0);
                  return new HAProxyMessage(var2, var3, var4, var6, var7, var9, var10, var20);
               }
            }
         }
      }
   }

   private static List<HAProxyTLV> readTlvs(ByteBuf var0) {
      HAProxyTLV var1 = readNextTLV(var0);
      if (var1 == null) {
         return Collections.emptyList();
      } else {
         ArrayList var2 = new ArrayList(4);

         do {
            var2.add(var1);
            if (var1 instanceof HAProxySSLTLV) {
               var2.addAll(((HAProxySSLTLV)var1).encapsulatedTLVs());
            }
         } while ((var1 = readNextTLV(var0)) != null);

         return var2;
      }
   }

   private static HAProxyTLV readNextTLV(ByteBuf var0) {
      if (var0.readableBytes() < 4) {
         return null;
      } else {
         byte var1 = var0.readByte();
         HAProxyTLV.Type var2 = HAProxyTLV.Type.typeForByteValue(var1);
         int var3 = var0.readUnsignedShort();
         switch (var2) {
            case PP2_TYPE_SSL:
               ByteBuf var4 = var0.retainedSlice(var0.readerIndex(), var3);
               ByteBuf var5 = var0.readSlice(var3);
               byte var6 = var5.readByte();
               int var7 = var5.readInt();
               if (var5.readableBytes() < 4) {
                  return new HAProxySSLTLV(var7, var6, Collections.emptyList(), var4);
               }

               ArrayList var8 = new ArrayList(4);

               do {
                  HAProxyTLV var9 = readNextTLV(var5);
                  if (var9 == null) {
                     break;
                  }

                  var8.add(var9);
               } while (var5.readableBytes() >= 4);

               return new HAProxySSLTLV(var7, var6, var8, var4);
            case PP2_TYPE_ALPN:
            case PP2_TYPE_AUTHORITY:
            case PP2_TYPE_SSL_VERSION:
            case PP2_TYPE_SSL_CN:
            case PP2_TYPE_NETNS:
            case OTHER:
               return new HAProxyTLV(var2, var1, var0.readRetainedSlice(var3));
            default:
               return null;
         }
      }
   }

   static HAProxyMessage decodeHeader(String var0) {
      if (var0 == null) {
         throw new HAProxyProtocolException("header");
      } else {
         String[] var1 = var0.split(" ");
         int var2 = var1.length;
         if (var2 < 2) {
            throw new HAProxyProtocolException("invalid header: " + var0 + " (expected: 'PROXY' and proxied protocol values)");
         } else if (!"PROXY".equals(var1[0])) {
            throw new HAProxyProtocolException("unknown identifier: " + var1[0]);
         } else {
            HAProxyProxiedProtocol var3;
            try {
               var3 = HAProxyProxiedProtocol.valueOf(var1[1]);
            } catch (IllegalArgumentException var6) {
               throw new HAProxyProtocolException(var6);
            }

            if (var3 != HAProxyProxiedProtocol.TCP4 && var3 != HAProxyProxiedProtocol.TCP6 && var3 != HAProxyProxiedProtocol.UNKNOWN) {
               throw new HAProxyProtocolException("unsupported v1 proxied protocol: " + var1[1]);
            } else if (var3 == HAProxyProxiedProtocol.UNKNOWN) {
               return unknownMsg(HAProxyProtocolVersion.V1, HAProxyCommand.PROXY);
            } else if (var2 != 6) {
               throw new HAProxyProtocolException("invalid TCP4/6 header: " + var0 + " (expected: 6 parts)");
            } else {
               try {
                  return new HAProxyMessage(HAProxyProtocolVersion.V1, HAProxyCommand.PROXY, var3, var1[2], var1[3], var1[4], var1[5]);
               } catch (RuntimeException var5) {
                  throw new HAProxyProtocolException("invalid HAProxy message", var5);
               }
            }
         }
      }
   }

   private static HAProxyMessage unknownMsg(HAProxyProtocolVersion var0, HAProxyCommand var1) {
      return new HAProxyMessage(var0, var1, HAProxyProxiedProtocol.UNKNOWN, null, null, 0, 0);
   }

   private static String ipBytesToString(ByteBuf var0, int var1) {
      StringBuilder var2 = new StringBuilder();
      byte var3 = 4;
      byte var4 = 8;
      if (var1 == 4) {
         for (int var5 = 0; var5 < 4; var5++) {
            var2.append(var0.readByte() & 255);
            var2.append('.');
         }
      } else {
         for (int var6 = 0; var6 < 8; var6++) {
            var2.append(Integer.toHexString(var0.readUnsignedShort()));
            var2.append(':');
         }
      }

      var2.setLength(var2.length() - 1);
      return var2.toString();
   }

   private static int portStringToInt(String var0) {
      int var1;
      try {
         var1 = Integer.parseInt(var0);
      } catch (NumberFormatException var3) {
         throw new IllegalArgumentException("invalid port: " + var0, var3);
      }

      if (var1 > 0 && var1 <= 65535) {
         return var1;
      } else {
         throw new IllegalArgumentException("invalid port: " + var0 + " (expected: 1 ~ 65535)");
      }
   }

   private static void checkAddress(String var0, HAProxyProxiedProtocol.AddressFamily var1) {
      ObjectUtil.checkNotNull(var1, "addrFamily");
      switch (var1) {
         case AF_UNSPEC:
            if (var0 != null) {
               throw new IllegalArgumentException("unable to validate an AF_UNSPEC address: " + var0);
            }

            return;
         case AF_UNIX:
            ObjectUtil.checkNotNull(var0, "address");
            if (var0.getBytes(CharsetUtil.US_ASCII).length > 108) {
               throw new IllegalArgumentException("invalid AF_UNIX address: " + var0);
            }

            return;
         default:
            ObjectUtil.checkNotNull(var0, "address");
            switch (var1) {
               case AF_IPv4:
                  if (!NetUtil.isValidIpV4Address(var0)) {
                     throw new IllegalArgumentException("invalid IPv4 address: " + var0);
                  }
                  break;
               case AF_IPv6:
                  if (!NetUtil.isValidIpV6Address(var0)) {
                     throw new IllegalArgumentException("invalid IPv6 address: " + var0);
                  }
                  break;
               default:
                  throw new IllegalArgumentException("unexpected addrFamily: " + var1);
            }
      }
   }

   private static void checkPort(int var0, HAProxyProxiedProtocol.AddressFamily var1) {
      switch (var1) {
         case AF_UNSPEC:
         case AF_UNIX:
            if (var0 != 0) {
               throw new IllegalArgumentException("port cannot be specified with addrFamily: " + var1);
            }
            break;
         case AF_IPv4:
         case AF_IPv6:
            if (var0 < 0 || var0 > 65535) {
               throw new IllegalArgumentException("invalid port: " + var0 + " (expected: 0 ~ 65535)");
            }
            break;
         default:
            throw new IllegalArgumentException("unexpected addrFamily: " + var1);
      }
   }

   public HAProxyProtocolVersion protocolVersion() {
      return this.protocolVersion;
   }

   public HAProxyCommand command() {
      return this.command;
   }

   public HAProxyProxiedProtocol proxiedProtocol() {
      return this.proxiedProtocol;
   }

   public String sourceAddress() {
      return this.sourceAddress;
   }

   public String destinationAddress() {
      return this.destinationAddress;
   }

   public int sourcePort() {
      return this.sourcePort;
   }

   public int destinationPort() {
      return this.destinationPort;
   }

   public List<HAProxyTLV> tlvs() {
      return this.tlvs;
   }

   int tlvNumBytes() {
      int var1 = 0;

      for (int var2 = 0; var2 < this.tlvs.size(); var2++) {
         var1 += this.tlvs.get(var2).totalNumBytes();
      }

      return var1;
   }

   public HAProxyMessage touch() {
      this.tryRecord();
      return (HAProxyMessage)super.touch();
   }

   public HAProxyMessage touch(Object var1) {
      if (this.leak != null) {
         this.leak.record(var1);
      }

      return this;
   }

   public HAProxyMessage retain() {
      this.tryRecord();
      return (HAProxyMessage)super.retain();
   }

   public HAProxyMessage retain(int var1) {
      this.tryRecord();
      return (HAProxyMessage)super.retain(var1);
   }

   @Override
   public boolean release() {
      this.tryRecord();
      return super.release();
   }

   @Override
   public boolean release(int var1) {
      this.tryRecord();
      return super.release(var1);
   }

   private void tryRecord() {
      if (this.leak != null) {
         this.leak.record();
      }
   }

   @Override
   protected void deallocate() {
      try {
         for (HAProxyTLV var2 : this.tlvs) {
            var2.release();
         }
      } finally {
         ResourceLeakTracker var4 = this.leak;
         if (var4 != null) {
            boolean var5 = var4.close(this);

            assert var5;
         }
      }
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder(256)
         .append(StringUtil.simpleClassName(this))
         .append("(protocolVersion: ")
         .append(this.protocolVersion)
         .append(", command: ")
         .append(this.command)
         .append(", proxiedProtocol: ")
         .append(this.proxiedProtocol)
         .append(", sourceAddress: ")
         .append(this.sourceAddress)
         .append(", destinationAddress: ")
         .append(this.destinationAddress)
         .append(", sourcePort: ")
         .append(this.sourcePort)
         .append(", destinationPort: ")
         .append(this.destinationPort)
         .append(", tlvs: [");
      if (!this.tlvs.isEmpty()) {
         for (HAProxyTLV var3 : this.tlvs) {
            var1.append(var3).append(", ");
         }

         var1.setLength(var1.length() - 2);
      }

      var1.append("])");
      return var1.toString();
   }
}
