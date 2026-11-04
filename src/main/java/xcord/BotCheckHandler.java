package xcord;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.SettingsChangedEvent;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;
import net.md_5.bungee.protocol.packet.ClientSettings;
import net.md_5.bungee.protocol.packet.KeepAlive;
import net.md_5.bungee.protocol.packet.Login;
import net.md_5.bungee.protocol.packet.PlayerPosition;
import net.md_5.bungee.protocol.packet.PlayerPositionAndLook;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.protocol.packet.RawDataPacket;

public abstract class BotCheckHandler extends PacketHandler {
   protected static final TIntObjectMap<Object> LOGIN_DIMENSIONS = new TIntObjectHashMap<>();
   private static final PacketRegistry<RawDataPacket> CBP_REGISTRY = new PacketTransformer<>(RawDataPacket.class, Direction.TO_CLIENT, "CBP_1");
   protected static final PacketRegistry<RawDataPacket> DBP_REGISTRY = new PacketTransformer<RawDataPacket>(
      RawDataPacket.class, Direction.TO_CLIENT, "DBP_1_0_1"
   ) {
      @Override
      public ByteBuf transform(ByteBuf var1, Class var2, Direction var3, int var4, int var5) {
         ByteBuf var6 = UnpooledByteBufAllocator.DEFAULT.heapBuffer(var1.readableBytes());
         Login var7 = new Login();
         var7.read(var1, var3, var4);
         var1.release();

         for (int var9 : ProtocolConstants.SUPPORTED_VERSION_IDS) {
            if (var9 >= var4) {
               if (var9 >= var5) {
                  break;
               }

               BotCheckHandler.LOGIN_DIMENSIONS.put(var9, var7.getDimension());
            }
         }

         var7.setEntityId(-100);
         var7.setViewDistance(1);
         var7.setSimulationDistance(1);
         VarInts.writeVarInt(var6, Protocol.GAME.TO_CLIENT.getId(Login.class, var4));
         var7.write(var6, var3, var4);
         return var6;
      }
   };
   protected final int checkId;
   protected final UserConnection user;
   protected final InitialHandler initialHandler;
   protected Runnable callback;
   protected volatile boolean verified = false;
   protected volatile boolean sentPayload = !XCord.getConfig().getAntiBotVerifyV2VerifyBrand();
   protected volatile boolean sentInitials = !XCord.getConfig().getAntiBotVerifyV2VerifyPackets();
   private int ping = -1;
   protected int protocolVersion;
   private boolean sentFakeLobby = false;
   private int verifyAttempts = 0;
   private int fakeLobbyY = 0;
   protected int keepAliveId = -1;
   protected boolean awaitingPosition = false;
   protected boolean receivedKeepAlive = false;
   private double lastY;
   private double lastDeltaY = 5000.0;
   private int fallCount = 0;
   private long lastPacketTime = 0L;
   private boolean advancedCheckEnabled = true;
   private boolean challengeSent = false;
   private boolean challengeAnswered = false;
   private long challengeSentTime = 0L;
   private int expectedKeepAliveId = 0;
   private int challengeAttempts = 0;
   private boolean positionChecked = false;
   private int invalidKeepAlives = 0;

   public BotCheckHandler(InitialHandler var1, UserConnection var2, int var3, int var4, Runnable var5) {
      this.initialHandler = var1;
      this.user = var2;
      this.checkId = var3;
      this.protocolVersion = var4;
      this.callback = var5;
   }

   protected void writePacket(Channel var1, ByteBuf var2) {
      if (BungeeCord.getInstance().getConfig().getCompressionThreshold() <= 0 || this.protocolVersion <= 5) {
         var1.writeAndFlush(var2, this.user.getCh().getChannel().voidPromise());
      } else if (XCord.getConfig().getPerformanceOptimizationsUseMergedEncodingPipeline()) {
         var1.pipeline().addBefore("compress", "frame-prepender", PipelineUtils.framePrepender);
         var1.pipeline().context("compress").writeAndFlush(var2, this.user.getCh().getChannel().voidPromise());
         var1.pipeline().remove("frame-prepender");
      } else {
         this.user.getCh().getHandle().pipeline().context("compress").writeAndFlush(var2, this.user.getCh().getChannel().voidPromise());
      }
   }

   public void setAdvancedCheck(boolean var1) {
      this.advancedCheckEnabled = var1;
   }

   public void handlePing(int var1) {
      this.ping = var1;
      this.challengeAttempts++;
      if (this.advancedCheckEnabled && !this.positionChecked && var1 > 80 && this.user.getCh().getHandle().isActive()) {
         int var2 = PipelineUtils.CHANNEL_UTILITY.getPing(this.user.getCh().getHandle());
         if (var1 - var2 > XCord.getConfig().getAntiBotVerifyV2BlockPotentialProxiesproxyCheck2Leniency()) {
            if (this.challengeAttempts >= XCord.getConfig().getAntiBotVerifyV2BlockPotentialProxiesproxyCheck2MaxAttempts()) {
               this.user.getCh().setProtocol(Protocol.GAME);
               System.out
                  .println(
                     "Disconnected "
                        + this.user.getName()
                        + ", failed verification (VerifyV2): 'block-potential-proxies-2' [Disable in XCord.yml if incorrect] --> "
                        + (var1 - var2)
                  );
               ConnectionStats var3 = XCord.getAntiBotManager().getStats(this.user.getAddress().getHostString());
               if (var3.addViolations(CheckType.VERIFY, 1) > XCord.getConfig().getAntiBotVerifyV2AdvancedLobbyCheckMaxAttempts()) {
                  XCord.getBlacklistManager().isBlacklisted(this.user.getAddress(), "Verify-Proxy");
               }

               this.user.disconnect(XCord.getMessages().getProxyLogicVerifyV2());
            } else {
               this.user.getCh().getHandle().eventLoop().schedule(() -> {
                  if (this.user.getCh().getHandle().isActive()) {
                     this.sendChallenge();
                  }
               }, 2L, TimeUnit.SECONDS);
            }
         } else {
            this.positionChecked = true;
         }
      } else {
         this.positionChecked = true;
      }

      if (this.positionChecked && this.challengeSent) {
         this.finishVerification();
      }
   }

   @Override
   public void handle(PluginMessage var1) throws Exception {
      this.sentPayload = true;
      this.initialHandler.relayMessage(var1);
   }

   @Override
   public void handle(KeepAlive var1) throws Exception {
      if (this.awaitingPosition && (long)this.keepAliveId == var1.getRandomId()) {
         this.receivedKeepAlive = true;
         this.handlePing((int)Math.max(1L, System.currentTimeMillis() - this.lastPacketTime));
      } else if (this.challengeAnswered && (long)this.expectedKeepAliveId == var1.getRandomId()) {
         this.challengeAnswered = false;
         this.handlePing((int)Math.max(1L, System.currentTimeMillis() - this.challengeSentTime));
      } else if (var1.getRandomId() > 1L && ++this.invalidKeepAlives > 5) {
         this.failVerification(5);
      }
   }

   protected void finishVerification() {
      if (!this.user.getCh().isClosing()) {
         if (XCord.getConfig().getAntiBotVerifyV2MaxPing() < this.ping) {
            this.user.disconnect(XCord.getMessages().getHighPing());
         } else {
            if (XCord.getConfig().getAntiBotVerifyV2AdvancedLobbyCheckEnabled()) {
               XCord.getAntiBotManager().whitelist(this.user.getName(), this.user.getAddress().getAddress().getHostAddress());
               if (this.protocolVersion < 759) {
                  this.user.unsafe().sendPacket(XCord.getConfig().getVerifiedPacket());
               }
            }

            this.user.getCh().setProtocol(Protocol.GAME);
            this.completeVerification();
         }
      }
   }

   @Override
   public boolean shouldHandle(PacketWrapper var1) throws Exception {
      return true;
   }

   @Override
   public void handle(ClientSettings var1) throws Exception {
      this.sentInitials = true;
      this.user.setSettings(var1);
      SettingsChangedEvent var2 = new SettingsChangedEvent(this.user);
      BungeeCord.getInstance().getPluginManager().callEvent(var2);
   }

   protected void runCallback() {
      this.verified = true;
      if (this.callback != null) {
         this.callback.run();
         this.callback = null;
      }
   }

   private void sendChallenge() {
      this.challengeSent = true;
      this.challengeAnswered = true;
      this.expectedKeepAliveId = ThreadLocalRandom.current().nextInt(1000);
      this.user
         .getCh()
         .getHandle()
         .writeAndFlush(new KeepAlive((long)this.expectedKeepAliveId))
         .addListener(var1 -> this.challengeSentTime = System.currentTimeMillis());
   }

   @Override
   public void handle(PacketWrapper var1) throws Exception {
      super.handle(var1);
      if (this.isVerified() && this.callback != null && !this.challengeSent) {
         if (!XCord.getConfig().getAntiBotVerifyV2AdvancedLobbyCheckEnabled()) {
            if (this.advancedCheckEnabled && !this.positionChecked) {
               this.sendChallenge();
            } else {
               this.finishVerification();
            }
         } else if (!this.sentFakeLobby) {
            Channel var2 = this.user.getCh().getHandle();
            this.user.getCh().setProtocol(Protocol.BOT_CHECK);
            this.sentFakeLobby = true;
            RawDataPacket var3 = CBP_REGISTRY.get(this.protocolVersion);
            this.writePacket(var2, var3.getRawData().copy());
            var2.write(new PlayerPositionAndLook(8.0, (double)(this.fakeLobbyY = 8 + ThreadLocalRandom.current().nextInt(6)), 8.0, 0.0F, 0.0F, 5, false));
            this.lastY = (double)this.fakeLobbyY;
            if (this.protocolVersion < 759) {
               var2.write(XCord.getConfig().getVerifyingPacket());
            }

            var2.flush();
         } else if (var1.packet instanceof PlayerPosition) {
            PlayerPosition var5 = (PlayerPosition)var1.packet;
            if (var1.packet instanceof PlayerPositionAndLook && this.awaitingPosition && var5.getY() == (double)this.fakeLobbyY) {
               this.awaitingPosition = false;
               if (this.receivedKeepAlive) {
                  if (this.fallCount < 6) {
                     this.failVerification(2);
                  } else if (!this.advancedCheckEnabled) {
                     this.finishVerification();
                  } else {
                     this.sendChallenge();
                  }

                  return;
               }
            }

            if (!this.awaitingPosition && var5.isOnGround() && var5.getY() == 4.25) {
               if (this.verifyAttempts > XCord.getConfig().getAntiBotVerifyV2AdvancedLobbyCheckMaxAttempts()) {
                  this.failVerification(4);
                  return;
               }

               this.receivedKeepAlive = false;
               this.awaitingPosition = true;
               this.keepAliveId = ThreadLocalRandom.current().nextInt(1000);
               this.fakeLobbyY = (int)(this.lastY + 5.0);
               this.verifyAttempts++;
               this.user.getCh().getHandle().write(new PlayerPositionAndLook(8.0, (double)this.fakeLobbyY, 8.0, 0.0F, 0.0F, 6 + this.verifyAttempts, false));
               this.user.getCh().getHandle().write(new KeepAlive((long)this.keepAliveId));
               this.user.getCh().getHandle().flush();
               this.lastPacketTime = System.currentTimeMillis();
            } else if (!XCord.getConfig().getAntiBotVerifyV2AdvancedLobbyCheckTestCollision()
               || (!var5.isOnGround() || !(var5.getY() < 5.0) || var5.getY() == 4.25) && !(var5.getY() < 4.25)) {
               if (this.verifyAttempts == 0 && var5.getY() > 5.0 && var5.getY() != (double)this.fakeLobbyY) {
                  double var6 = var5.getY() - this.lastY;
                  if (var6 < this.lastDeltaY && var6 < -0.1) {
                     this.fallCount++;
                  }

                  if (var6 <= -5.0) {
                     this.failVerification(5);
                     return;
                  }

                  this.lastDeltaY = var6;
               }
            } else {
               this.failVerification(1);
            }

            this.lastY = var5.getY();
         }
      }
   }

   private void failVerification(int var1) {
      this.user.getCh().setProtocol(Protocol.GAME);
      System.out.println("Disconnected " + this.user.getName() + ", failed advanced-verify bot-check ID:" + var1);
      ConnectionStats var2 = XCord.getAntiBotManager().getStats(this.user.getAddress().getHostString());
      if (var2.addViolations(CheckType.VERIFY, 1) > XCord.getConfig().getAntiBotVerifyV2AdvancedLobbyCheckMaxAttempts()) {
         XCord.getBlacklistManager().isBlacklisted(this.user.getAddress(), "Verify-" + var1);
      }

      this.user.disconnect(XCord.getMessages().getFailedConnection());
   }

   public Runnable getCallback() {
      return this.callback;
   }

   public abstract void completeVerification();

   public boolean isVerified() {
      return this.verified && this.sentInitials && this.sentPayload;
   }

   public int getPing() {
      return this.ping;
   }

   @Override
   public void disconnected(ChannelWrapper var1) throws Exception {
      if ((this.user.getPendingConnection() == null || !this.user.getPendingConnection().isPassthroughThenKick())
         && XCord.getConfig().getCompatibilityAlwaysFireQuitEvent()) {
         try {
            PlayerDisconnectEvent var2 = new PlayerDisconnectEvent(this.user);
            BungeeCord.getInstance().getPluginManager().callEvent(var2);
         } catch (Exception var3) {
            if (XCord.getConfig().getDebugDebugMode()) {
               var3.printStackTrace();
            }
         }
      }

      this.user.getCh().close();
   }

   public String getDebugInfo() {
      return "Protocol: "
         + this.user.getPendingConnection().getVersion()
         + " > Sent-Initials: "
         + this.sentInitials
         + " > Sent-Payload: "
         + this.sentPayload
         + " > Verified: "
         + this.verified
         + " Ping: "
         + this.ping;
   }
}
