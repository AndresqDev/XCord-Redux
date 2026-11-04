package net.md_5.bungee.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

public class BufUtil {
   public static String dump(ByteBuf var0, int var1) {
      return ByteBufUtil.hexDump(var0, 0, Math.min(var0.writerIndex(), var1));
   }
}
