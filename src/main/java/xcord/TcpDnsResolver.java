package xcord;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.dns.DefaultDnsQuery;
import io.netty.handler.codec.dns.DefaultDnsQuestion;
import io.netty.handler.codec.dns.DefaultDnsResponse;
import io.netty.handler.codec.dns.DnsOpCode;
import io.netty.handler.codec.dns.DnsQuery;
import io.netty.handler.codec.dns.DnsRawRecord;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.handler.codec.dns.TcpDnsQueryEncoder;
import io.netty.handler.codec.dns.TcpDnsResponseDecoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.NetUtil;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import net.md_5.bungee.netty.PipelineUtils;

public class TcpDnsResolver {
   public String a(DefaultDnsResponse var1) {
      String var2 = null;
      int var3 = 0;

      for (int var4 = var1.count(DnsSection.ANSWER); var3 < var4; var3++) {
         DnsRecord var5 = var1.recordAt(DnsSection.ANSWER, var3);
         if (var5.type() == DnsRecordType.A) {
            DnsRawRecord var6 = (DnsRawRecord)var5;
            var2 = NetUtil.bytesToIpAddress(ByteBufUtil.getBytes(var6.content()));
         }
      }

      return var2;
   }

   public CompletableFuture<String> a(String var1, EventLoop var2) {
      final CompletableFuture var3 = new CompletableFuture();
      Bootstrap var4 = new Bootstrap();
      var4.group(var2).channel(PipelineUtils.getChannel(null)).handler(new ChannelInitializer<SocketChannel>() {
         protected void initChannel(SocketChannel var1) {
            ChannelPipeline var2 = var1.pipeline();
            var2.addLast("timeout", new ReadTimeoutHandler(10));
            var2.addLast(new TcpDnsQueryEncoder()).addLast(new TcpDnsResponseDecoder()).addLast(new SimpleChannelInboundHandler<DefaultDnsResponse>() {
               protected void channelRead0(ChannelHandlerContext var1, DefaultDnsResponse var2) {
                  try {
                     var3.complete(TcpDnsResolver.this.a(var2));
                  } catch (Exception var7) {
                     var3.completeExceptionally(var7);
                  } finally {
                     var1.close();
                  }
               }
            });
            var2.addLast(new ChannelInboundHandlerAdapter() {
               @Override
               public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
                  var3.completeExceptionally(var2);
               }
            });
         }
      });
      var4.connect("8.8.8.8", 53).addListener(var2x -> {
         if (var2x.isSuccess()) {
            Channel var3x = ((ChannelFuture)var2x).channel();
            int var4x = new Random().nextInt(59000) + 1000;
            DnsQuery var5 = new DefaultDnsQuery(var4x, DnsOpCode.QUERY).setRecord(DnsSection.QUESTION, new DefaultDnsQuestion(var1, DnsRecordType.A));
            var5.setRecursionDesired(true);
            var3x.writeAndFlush(var5);
         } else {
            var3.completeExceptionally(var2x.cause());
         }
      });
      return var3;
   }
}
