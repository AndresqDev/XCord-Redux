package xcord;

import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.resolver.ResolvedAddressTypes;
import io.netty.resolver.dns.DnsAddressResolverGroup;
import io.netty.resolver.dns.DnsNameResolverBuilder;
import io.netty.resolver.dns.DnsServerAddressStreamProviders;
import java.net.InetAddress;
import net.md_5.bungee.netty.PipelineUtils;
import xcord.TcpDnsResolver;
import xcord.XCord;

public class DnsResolver {
    private static final DnsNameResolverBuilder BUILDER = new DnsNameResolverBuilder().resolvedAddressTypes(ResolvedAddressTypes.IPV4_PREFERRED).queryTimeoutMillis(5000L).channelType(PipelineUtils.getDatagramChannel()).channelFactory(PipelineUtils.getDatagramChannelFactory()).socketChannelType(PipelineUtils.usingEpoll() ? EpollSocketChannel.class : NioSocketChannel.class).nameServerProvider(DnsServerAddressStreamProviders.platformDefault()).maxQueriesPerResolve(5).ttl(30, Integer.MAX_VALUE).negativeTtl(0);
    private static final DnsAddressResolverGroup RESOLVER_GROUP = new DnsAddressResolverGroup(BUILDER);

    public static DnsAddressResolverGroup getResolverGroup() {
        return RESOLVER_GROUP;
    }

    public static InetAddress resolve(String string) {
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(string);
        }
        catch (Exception exception) {
            XCord.log("Failed to resolve " + string + " using UDP. Attempting TCP...");
        }
        if (inetAddress == null) {
            try {
                inetAddress = InetAddress.getByName(new TcpDnsResolver().a(string, PipelineUtils.newEventLoopGroup(1, null).next()).get());
            }
            catch (Exception exception) {
                XCord.log("Failed to resolve " + string + " using TCP + Google's DNS.");
            }
        }
        return inetAddress;
    }
}

