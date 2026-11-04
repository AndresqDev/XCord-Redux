package io.netty.channel.socket.nio;

import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.util.internal.SuppressJava6Requirement;
import java.io.IOException;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.channels.Channel;
import java.nio.channels.NetworkChannel;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.Set;

@SuppressJava6Requirement(
   reason = "Usage explicit by the user"
)
public final class NioChannelOption<T> extends ChannelOption<T> {
   private final SocketOption<T> option;

   private NioChannelOption(SocketOption<T> var1) {
      super(var1.name());
      this.option = var1;
   }

   public static <T> ChannelOption<T> of(SocketOption<T> var0) {
      return new NioChannelOption<>(var0);
   }

   @SuppressJava6Requirement(
      reason = "Usage guarded by java version check"
   )
   static <T> boolean setOption(Channel var0, NioChannelOption<T> var1, T var2) {
      NetworkChannel var3 = (NetworkChannel)var0;
      if (!var3.supportedOptions().contains(var1.option)) {
         return false;
      } else if (var3 instanceof ServerSocketChannel && var1.option == StandardSocketOptions.IP_TOS) {
         return false;
      } else {
         try {
            var3.setOption(var1.option, (T)var2);
            return true;
         } catch (IOException var5) {
            throw new ChannelException(var5);
         }
      }
   }

   @SuppressJava6Requirement(
      reason = "Usage guarded by java version check"
   )
   static <T> T getOption(Channel var0, NioChannelOption<T> var1) {
      NetworkChannel var2 = (NetworkChannel)var0;
      if (!var2.supportedOptions().contains(var1.option)) {
         return null;
      } else if (var2 instanceof ServerSocketChannel && var1.option == StandardSocketOptions.IP_TOS) {
         return null;
      } else {
         try {
            return var2.getOption(var1.option);
         } catch (IOException var4) {
            throw new ChannelException(var4);
         }
      }
   }

   @SuppressJava6Requirement(
      reason = "Usage guarded by java version check"
   )
   static ChannelOption[] getOptions(Channel var0) {
      NetworkChannel var1 = (NetworkChannel)var0;
      Set var2 = var1.supportedOptions();
      if (var1 instanceof ServerSocketChannel) {
         ArrayList var7 = new ArrayList(var2.size());

         for (SocketOption var9 : var2) {
            if (var9 != StandardSocketOptions.IP_TOS) {
               var7.add(new NioChannelOption(var9));
            }
         }

         return var7.toArray(new ChannelOption[0]);
      } else {
         ChannelOption[] var3 = new ChannelOption[var2.size()];
         int var4 = 0;

         for (SocketOption var6 : var2) {
            var3[var4++] = new NioChannelOption(var6);
         }

         return var3;
      }
   }
}
