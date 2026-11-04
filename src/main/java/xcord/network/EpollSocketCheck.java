package xcord.network;

import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.epoll.EpollTcpInfo;
import xcord.XCord;

public class EpollSocketCheck<T extends EpollSocketChannel> implements SocketCheck<T> {
    @Override
    public void close(EpollSocketChannel channel) {
        if (channel.isActive()) {
            channel.config().setSoLinger(0);
            channel.close();
        }
    }

    @Override
    public int getPing(EpollSocketChannel channel) {
        return (int) (channel.tcpInfo().rtt() / 1000L);
    }

    @Override
    public boolean check(EpollSocketChannel channel) {
        if (!XCord.getConfig().getAntiBotLinuxOnlySocketCheckEnabled()) {
            return false;
        }
        if (!XCord.getAntiBotManager().isAttackActive() && XCord.getConfig().getAntiBotLinuxOnlySocketCheckEnableDuringBotAttackOnly()) {
            return false;
        }
        int connectionRate = Math.max(XCord.getAntiBotManager().getConnectionCount(), XCord.getAntiBotManager().getLastConnectionCount());
        if (connectionRate < XCord.getConfig().getAntiBotLinuxOnlySocketCheckRequiredConnectionRate()) {
            return false;
        }
        EpollTcpInfo tcpInfo = channel.tcpInfo();
        int mssDifference = (int) Math.abs(tcpInfo.advmss() - tcpInfo.sndMss());
        String reason;
        if (mssDifference > 127 && XCord.getConfig().getAntiBotLinuxOnlySocketCheckDetectProxiesAndVps()) {
            reason = "Proxy";
        } else if (tcpInfo.sndMss() < 700L && XCord.getConfig().getAntiBotLinuxOnlySocketCheckDetectSuspiciousMss()) {
            reason = "Suspicious-MSS";
        } else if (tcpInfo.options() == 0 && XCord.getConfig().getAntiBotLinuxOnlySocketCheckDetectSuspicious()) {
            reason = "Suspicious-Connection";
        } else if (tcpInfo.options() == 7 && XCord.getConfig().getAntiBotLinuxOnlySocketCheckDetectNonWindows()) {
            reason = "Non-Windows";
        } else {
            return false;
        }
        if (!XCord.getConfig().getAntiBotLinuxOnlySocketCheckLowerThresholdsOfBotChecksInsteadOfBlacklist()) {
            XCord.getBlacklistManager().isBlacklisted(channel.remoteAddress(), "Socket-Check: " + reason);
        }
        return true;
    }

    @Override
    public boolean isSuspiciousMss(EpollSocketChannel channel) {
        EpollTcpInfo tcpInfo = channel.tcpInfo();
        int mssDifference = (int) Math.abs(tcpInfo.advmss() - tcpInfo.sndMss());
        return mssDifference > XCord.getConfig().getAntiBotVerifyV2BlockPotentialProxiesProxyCheck1Leniency();
    }
}
