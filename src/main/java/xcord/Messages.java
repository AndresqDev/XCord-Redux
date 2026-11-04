package xcord;

import java.io.File;
import net.md_5.bungee.conf.YamlConfig;
import xcord.MessageTemplate;

public class Messages {
    private final MessageTemplate slowLogin;
    private final MessageTemplate failedConnection;
    private final MessageTemplate linuxCheckFail;
    private final MessageTemplate speedJoinFail;
    private final MessageTemplate shortName;
    private final MessageTemplate illegalName;
    private final MessageTemplate vpn;
    private final MessageTemplate highPing;
    private final MessageTemplate proxyLogicVerifyV2;

    public Messages() {
        YamlConfig yamlConfig = new YamlConfig(new File("xcord-messages.yml"));
        yamlConfig.load(false);
        String string = "kick-messages.";
        this.slowLogin = new MessageTemplate(yamlConfig.getString(string + "slow-login", "You took too long to join! (Lag?)"));
        this.failedConnection = new MessageTemplate(yamlConfig.getString(string + "failed-connection", "You failed to connect to the server, please try again in a few seconds!"));
        this.linuxCheckFail = new MessageTemplate(yamlConfig.getString(string + "linux-check-fail", "You were detected as a bot. Please wait before re-connecting..."));
        this.speedJoinFail = new MessageTemplate(yamlConfig.getString(string + "speed-join-fail", "You're connecting too quickly! Slow down or you'll be temporarily blacklisted"));
        this.shortName = new MessageTemplate(yamlConfig.getString(string + "short-name", "Your name must be over 2 characters long"));
        this.illegalName = new MessageTemplate(yamlConfig.getString(string + "illegal-name", "Your name contains illegal characters"));
        this.vpn = new MessageTemplate(yamlConfig.getString(string + "vpn", "VPN/Proxy's are not allowed on this server"));
        this.highPing = new MessageTemplate(yamlConfig.getString(string + "high-ping", "Your ping is too high to play. Try again later"));
        this.proxyLogicVerifyV2 = new MessageTemplate(yamlConfig.getString(string + "proxy-logic-detection-verifyv2", "Strange connection, appears to be proxy? Try to login again if this is an error."));
    }

    public MessageTemplate getProxyLogicVerifyV2() {
        return this.proxyLogicVerifyV2;
    }

    public MessageTemplate getHighPing() {
        return this.highPing;
    }

    public MessageTemplate getVpn() {
        return this.vpn;
    }

    public MessageTemplate getSlowLogin() {
        return this.slowLogin;
    }

    public MessageTemplate getFailedConnection() {
        return this.failedConnection;
    }

    public MessageTemplate getLinuxCheckFail() {
        return this.linuxCheckFail;
    }

    public MessageTemplate getSpeedJoinFail() {
        return this.speedJoinFail;
    }

    public MessageTemplate getShortName() {
        return this.shortName;
    }
}

