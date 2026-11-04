package net.md_5.bungee.jni.cipher;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.security.GeneralSecurityException;
import javax.crypto.SecretKey;

public interface BungeeCipher {
   void init(boolean var1, SecretKey var2) throws GeneralSecurityException;

   void free();

   void cipher(ByteBuf var1, ByteBuf var2) throws GeneralSecurityException;

   ByteBuf cipher(ChannelHandlerContext var1, ByteBuf var2) throws GeneralSecurityException;
}
