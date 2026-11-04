package io.netty.handler.codec.protobuf;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

@ChannelHandler.Sharable
public class ProtobufDecoder extends MessageToMessageDecoder<ByteBuf> {
   private static final boolean HAS_PARSER;
   private final MessageLite prototype;
   private final ExtensionRegistryLite extensionRegistry;

   public ProtobufDecoder(MessageLite var1) {
      this(var1, null);
   }

   public ProtobufDecoder(MessageLite var1, ExtensionRegistry var2) {
      this(var1, (ExtensionRegistryLite)var2);
   }

   public ProtobufDecoder(MessageLite var1, ExtensionRegistryLite var2) {
      this.prototype = ObjectUtil.checkNotNull(var1, "prototype").getDefaultInstanceForType();
      this.extensionRegistry = var2;
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      int var6 = var2.readableBytes();
      byte[] var4;
      int var5;
      if (var2.hasArray()) {
         var4 = var2.array();
         var5 = var2.arrayOffset() + var2.readerIndex();
      } else {
         var4 = ByteBufUtil.getBytes(var2, var2.readerIndex(), var6, false);
         var5 = 0;
      }

      if (this.extensionRegistry == null) {
         if (HAS_PARSER) {
            var3.add(this.prototype.getParserForType().parseFrom(var4, var5, var6));
         } else {
            var3.add(this.prototype.newBuilderForType().mergeFrom(var4, var5, var6).build());
         }
      } else if (HAS_PARSER) {
         var3.add(this.prototype.getParserForType().parseFrom(var4, var5, var6, this.extensionRegistry));
      } else {
         var3.add(this.prototype.newBuilderForType().mergeFrom(var4, var5, var6, this.extensionRegistry).build());
      }
   }

   static {
      boolean var0 = false;

      try {
         MessageLite.class.getDeclaredMethod("getParserForType");
         var0 = true;
      } catch (Throwable var2) {
      }

      HAS_PARSER = var0;
   }
}
