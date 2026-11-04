package xcord.network;

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
    public String extractAddress(DefaultDnsResponse response) {
        String address = null;
        int recordCount = response.count(DnsSection.ANSWER);
        for (int index = 0; index < recordCount; index++) {
            DnsRecord record = response.recordAt(DnsSection.ANSWER, index);
            if (record.type() == DnsRecordType.A) {
                DnsRawRecord rawRecord = (DnsRawRecord) record;
                address = NetUtil.bytesToIpAddress(ByteBufUtil.getBytes(rawRecord.content()));
            }
        }
        return address;
    }

    public CompletableFuture<String> resolve(String host, EventLoop eventLoop) {
        CompletableFuture<String> result = new CompletableFuture<>();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoop).channel(PipelineUtils.getChannel(null)).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast("timeout", new ReadTimeoutHandler(10));
                pipeline.addLast(new TcpDnsQueryEncoder()).addLast(new TcpDnsResponseDecoder()).addLast(new SimpleChannelInboundHandler<DefaultDnsResponse>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext context, DefaultDnsResponse response) {
                        try {
                            result.complete(TcpDnsResolver.this.extractAddress(response));
                        } catch (Exception exception) {
                            result.completeExceptionally(exception);
                        } finally {
                            context.close();
                        }
                    }
                });
                pipeline.addLast(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
                        result.completeExceptionally(cause);
                    }
                });
            }
        });
        bootstrap.connect("8.8.8.8", 53).addListener(future -> {
            if (future.isSuccess()) {
                Channel channel = ((ChannelFuture) future).channel();
                int queryId = new Random().nextInt(59000) + 1000;
                DnsQuery query = new DefaultDnsQuery(queryId, DnsOpCode.QUERY).setRecord(DnsSection.QUESTION, new DefaultDnsQuestion(host, DnsRecordType.A));
                query.setRecursionDesired(true);
                channel.writeAndFlush(query);
            } else {
                result.completeExceptionally(future.cause());
            }
        });
        return result;
    }
}
