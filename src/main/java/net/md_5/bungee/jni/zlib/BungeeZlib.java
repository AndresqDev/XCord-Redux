package net.md_5.bungee.jni.zlib;

import io.netty.buffer.ByteBuf;
import java.util.zip.DataFormatException;

public interface BungeeZlib {
   boolean requiresDirectMemory();

   void init(boolean var1, int var2);

   void free();

   void inflate(ByteBuf var1, ByteBuf var2, int var3) throws DataFormatException;

   void deflate(ByteBuf var1, ByteBuf var2) throws DataFormatException;
}
