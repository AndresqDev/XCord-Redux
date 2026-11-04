package xcord;

import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.epoll.EpollTcpInfo;
import xcord.SocketCheck;
import xcord.XCord;

public class EpollSocketCheck<T extends EpollSocketChannel>
implements SocketCheck<T> {
    @Override
    public void close(EpollSocketChannel epollSocketChannel) {
        if (epollSocketChannel.isActive()) {
            epollSocketChannel.config().setSoLinger(0);
            epollSocketChannel.close();
        }
    }

    @Override
    public int getPing(EpollSocketChannel epollSocketChannel) {
        return (int)(epollSocketChannel.tcpInfo().rtt() / 1000L);
    }

    @Override
    public boolean check(EpollSocketChannel epollSocketChannel) {
        if (XCord.getConfig().getAntiBotLinuxOnlySocketCheckEnabled() && (XCord.getAntiBotManager().isAttackActive() || !XCord.getConfig().getAntiBotLinuxOnlySocketCheckEnableDuringBotAttackOnly()) && Math.max(XCord.getAntiBotManager().getConnectionCount(), XCord.getAntiBotManager().getLastConnectionCount()) >= XCord.getConfig().getAntiBotLinuxOnlySocketCheckRequiredConnectionRate()) {
            String string;
            EpollTcpInfo epollTcpInfo = epollSocketChannel.tcpInfo();
            int n = (int)Math.abs(epollTcpInfo.advmss() - epollTcpInfo.sndMss());
            if (n > 127 && XCord.getConfig().getAntiBotLinuxOnlySocketCheckDetectProxiesAndVps()) {
                string = "Proxy";
            } else if (epollTcpInfo.sndMss() < 700L && XCord.getConfig().getAntiBotLinuxOnlySocketCheckDetectSuspiciousMss()) {
                string = "Suspicious-MSS";
            } else if (epollTcpInfo.options() == 0 && XCord.getConfig().getAntiBotLinuxOnlySocketCheckDetectSuspicious()) {
                string = "Suspicious-Connection";
            } else if (epollTcpInfo.options() == 7 && XCord.getConfig().getAntiBotLinuxOnlySocketCheckDetectNonWindows()) {
                string = "Non-Windows";
            } else {
                return false;
            }
            if (!XCord.getConfig().getAntiBotLinuxOnlySocketCheckLowerThresholdsOfBotChecksInsteadOfBlacklist()) {
                XCord.getBlacklistManager().isBlacklisted(epollSocketChannel.remoteAddress(), "Socket-Check: " + string);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isSuspiciousMss(EpollSocketChannel epollSocketChannel) {
        EpollTcpInfo epollTcpInfo = epollSocketChannel.tcpInfo();
        int n = (int)Math.abs(epollTcpInfo.advmss() - epollTcpInfo.sndMss());
        return n > XCord.getConfig().getAntiBotVerifyV2BlockPotentialProxiesproxyCheck1Leniency();
    }
}

