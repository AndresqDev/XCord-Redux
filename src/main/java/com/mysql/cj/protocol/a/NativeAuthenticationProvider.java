package com.mysql.cj.protocol.a;

import com.mysql.cj.Constants;
import com.mysql.cj.Messages;
import com.mysql.cj.callback.MysqlCallbackHandler;
import com.mysql.cj.callback.UsernameCallback;
import com.mysql.cj.conf.PropertyDefinitions;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.UnableToConnectException;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.AuthenticationPlugin;
import com.mysql.cj.protocol.AuthenticationProvider;
import com.mysql.cj.protocol.Protocol;
import com.mysql.cj.protocol.ServerSession;
import com.mysql.cj.protocol.a.authentication.AuthenticationKerberosClient;
import com.mysql.cj.protocol.a.authentication.AuthenticationLdapSaslClientPlugin;
import com.mysql.cj.protocol.a.authentication.AuthenticationOciClient;
import com.mysql.cj.protocol.a.authentication.CachingSha2PasswordPlugin;
import com.mysql.cj.protocol.a.authentication.MysqlClearPasswordPlugin;
import com.mysql.cj.protocol.a.authentication.MysqlNativePasswordPlugin;
import com.mysql.cj.protocol.a.authentication.MysqlOldPasswordPlugin;
import com.mysql.cj.protocol.a.authentication.Sha256PasswordPlugin;
import com.mysql.cj.protocol.a.result.OkPacket;
import com.mysql.cj.util.StringUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NativeAuthenticationProvider implements AuthenticationProvider<NativePacketPayload> {
   private static final int AUTH_411_OVERHEAD = 33;
   private static final String NONE = "none";
   private String seed;
   private String username;
   private String password;
   private String database;
   private boolean useConnectWithDb;
   private ExceptionInterceptor exceptionInterceptor;
   private PropertySet propertySet;
   private Protocol<NativePacketPayload> protocol;
   private Map<String, AuthenticationPlugin<NativePacketPayload>> authenticationPlugins = null;
   private String clientDefaultAuthenticationPluginName = null;
   private boolean clientDefaultAuthenticationPluginExplicitelySet = false;
   private String serverDefaultAuthenticationPluginName = null;
   private MysqlCallbackHandler callbackHandler = cb -> {
      if (cb instanceof UsernameCallback) {
         this.username = ((UsernameCallback)cb).getUsername();
      }
   };

   @Override
   public void init(Protocol<NativePacketPayload> prot, PropertySet propSet, ExceptionInterceptor excInterceptor) {
      this.protocol = prot;
      this.propertySet = propSet;
      this.exceptionInterceptor = excInterceptor;
   }

   @Override
   public void connect(String user, String pass, String db) {
      ServerSession sessState = this.protocol.getServerSession();
      this.username = user;
      this.password = pass;
      this.database = db;
      NativeCapabilities capabilities = (NativeCapabilities)sessState.getCapabilities();
      NativePacketPayload buf = capabilities.getInitialHandshakePacket();
      PropertyDefinitions.SslMode sslMode = this.propertySet.<PropertyDefinitions.SslMode>getEnumProperty(PropertyKey.sslMode).getValue();
      int capabilityFlags = capabilities.getCapabilityFlags();
      if ((capabilityFlags & 2048) == 0 && sslMode != PropertyDefinitions.SslMode.DISABLED && sslMode != PropertyDefinitions.SslMode.PREFERRED) {
         throw (UnableToConnectException)ExceptionFactory.createException(
            UnableToConnectException.class, Messages.getString("MysqlIO.15"), this.getExceptionInterceptor()
         );
      } else if ((capabilityFlags & 32768) == 0) {
         throw (UnableToConnectException)ExceptionFactory.createException(
            UnableToConnectException.class, "CLIENT_SECURE_CONNECTION is required", this.getExceptionInterceptor()
         );
      } else if ((capabilityFlags & 524288) == 0) {
         throw (UnableToConnectException)ExceptionFactory.createException(
            UnableToConnectException.class, "CLIENT_PLUGIN_AUTH is required", this.getExceptionInterceptor()
         );
      } else {
         sessState.setStatusFlags(capabilities.getStatusFlags());
         int authPluginDataLength = capabilities.getAuthPluginDataLength();
         StringBuilder fullSeed = new StringBuilder(authPluginDataLength > 0 ? authPluginDataLength : 20);
         fullSeed.append(capabilities.getSeed());
         fullSeed.append(
            authPluginDataLength > 0
               ? buf.readString(NativeConstants.StringLengthDataType.STRING_FIXED, "ASCII", authPluginDataLength - 8)
               : buf.readString(NativeConstants.StringSelfDataType.STRING_TERM, "ASCII")
         );
         this.seed = fullSeed.toString();
         this.useConnectWithDb = this.database != null
            && this.database.length() > 0
            && !this.propertySet.getBooleanProperty(PropertyKey.createDatabaseIfNotExist).getValue();
         long clientParam = (long)(
            557056
               | capabilityFlags & 1
               | capabilityFlags & 512
               | capabilityFlags & 8192
               | capabilityFlags & 131072
               | capabilityFlags & 262144
               | capabilityFlags & 4
               | capabilityFlags & 16777216
               | capabilityFlags & 2097152
               | capabilityFlags & 134217728
               | (this.propertySet.getBooleanProperty(PropertyKey.useCompression).getValue() ? capabilityFlags & 32 : 0)
               | (this.useConnectWithDb ? capabilityFlags & 8 : 0)
               | (this.propertySet.getBooleanProperty(PropertyKey.useAffectedRows).getValue() ? 0 : capabilityFlags & 2)
               | (
                  !this.propertySet.getBooleanProperty(PropertyKey.allowLoadLocalInfile).getValue()
                        && !this.propertySet.getStringProperty(PropertyKey.allowLoadLocalInfileInPath).isExplicitlySet()
                     ? 0
                     : capabilityFlags & 128
               )
               | (this.propertySet.getBooleanProperty(PropertyKey.interactiveClient).getValue() ? capabilityFlags & 1024 : 0)
               | (this.propertySet.getBooleanProperty(PropertyKey.allowMultiQueries).getValue() ? capabilityFlags & 65536 : 0)
               | (this.propertySet.getBooleanProperty(PropertyKey.disconnectOnExpiredPasswords).getValue() ? 0 : capabilityFlags & 4194304)
               | ("none".equals(this.propertySet.getStringProperty(PropertyKey.connectionAttributes).getValue()) ? 0 : capabilityFlags & 1048576)
               | (this.propertySet.getEnumProperty(PropertyKey.sslMode).getValue() != PropertyDefinitions.SslMode.DISABLED ? capabilityFlags & 2048 : 0)
               | (this.propertySet.getBooleanProperty(PropertyKey.trackSessionState).getValue() ? capabilityFlags & 8388608 : 0)
         );
         sessState.setClientParam(clientParam);
         if ((clientParam & 2048L) != 0L) {
            this.protocol.negotiateSSLConnection();
         }

         if (buf.isOKPacket()) {
            throw ExceptionFactory.createException(
               Messages.getString("AuthenticationProvider.UnexpectedAuthenticationApproval"), this.getExceptionInterceptor()
            );
         } else {
            this.proceedHandshakeWithPluggableAuthentication(buf);
            this.password = null;
         }
      }
   }

   private void loadAuthenticationPlugins() {
      RuntimeProperty<String> defaultAuthenticationPluginProp = this.propertySet.getStringProperty(PropertyKey.defaultAuthenticationPlugin);
      String defaultAuthenticationPluginValue = defaultAuthenticationPluginProp.getValue();
      if (defaultAuthenticationPluginValue != null && !"".equals(defaultAuthenticationPluginValue.trim())) {
         String disabledPlugins = this.propertySet.getStringProperty(PropertyKey.disabledAuthenticationPlugins).getValue();
         List<String> disabledAuthenticationPlugins;
         if (disabledPlugins != null && !"".equals(disabledPlugins)) {
            disabledAuthenticationPlugins = StringUtils.split(disabledPlugins, ",", true);
         } else {
            disabledAuthenticationPlugins = Collections.EMPTY_LIST;
         }

         this.authenticationPlugins = new HashMap<>();
         List<AuthenticationPlugin<NativePacketPayload>> pluginsToInit = new LinkedList<>();
         pluginsToInit.add(new MysqlNativePasswordPlugin());
         pluginsToInit.add(new MysqlClearPasswordPlugin());
         pluginsToInit.add(new Sha256PasswordPlugin());
         pluginsToInit.add(new CachingSha2PasswordPlugin());
         pluginsToInit.add(new MysqlOldPasswordPlugin());
         pluginsToInit.add(new AuthenticationLdapSaslClientPlugin());
         pluginsToInit.add(new AuthenticationKerberosClient());
         pluginsToInit.add(new AuthenticationOciClient());
         String authenticationPluginClasses = this.propertySet.getStringProperty(PropertyKey.authenticationPlugins).getValue();
         if (authenticationPluginClasses != null && !"".equals(authenticationPluginClasses.trim())) {
            for (String className : StringUtils.split(authenticationPluginClasses, ",", true)) {
               try {
                  pluginsToInit.add((AuthenticationPlugin<NativePacketPayload>)Class.forName(className).newInstance());
               } catch (Throwable var14) {
                  throw (WrongArgumentException)ExceptionFactory.createException(
                     WrongArgumentException.class,
                     Messages.getString("AuthenticationProvider.BadAuthenticationPlugin", new Object[]{className}),
                     var14,
                     this.exceptionInterceptor
                  );
               }
            }
         }

         boolean defaultFound = false;

         for (AuthenticationPlugin<NativePacketPayload> plugin : pluginsToInit) {
            String pluginProtocolName = plugin.getProtocolPluginName();
            String pluginClassName = plugin.getClass().getName();
            boolean disabledByProtocolName = disabledAuthenticationPlugins.contains(pluginProtocolName);
            boolean disabledByClassName = disabledAuthenticationPlugins.contains(pluginClassName);
            if (disabledByProtocolName || disabledByClassName) {
               if (!defaultFound && (defaultAuthenticationPluginValue.equals(pluginProtocolName) || defaultAuthenticationPluginValue.equals(pluginClassName))) {
                  throw (WrongArgumentException)ExceptionFactory.createException(
                     WrongArgumentException.class,
                     Messages.getString(
                        "AuthenticationProvider.BadDisabledAuthenticationPlugin", new Object[]{disabledByClassName ? pluginClassName : pluginProtocolName}
                     ),
                     this.getExceptionInterceptor()
                  );
               }
            } else {
               this.authenticationPlugins.put(pluginProtocolName, plugin);
               if (!defaultFound && (defaultAuthenticationPluginValue.equals(pluginProtocolName) || defaultAuthenticationPluginValue.equals(pluginClassName))) {
                  this.clientDefaultAuthenticationPluginName = pluginProtocolName;
                  this.clientDefaultAuthenticationPluginExplicitelySet = defaultAuthenticationPluginProp.isExplicitlySet();
                  defaultFound = true;
               }
            }
         }

         if (!defaultFound) {
            throw (WrongArgumentException)ExceptionFactory.createException(
               WrongArgumentException.class,
               Messages.getString("AuthenticationProvider.DefaultAuthenticationPluginIsNotListed", new Object[]{defaultAuthenticationPluginValue}),
               this.getExceptionInterceptor()
            );
         }
      } else {
         throw (WrongArgumentException)ExceptionFactory.createException(
            WrongArgumentException.class,
            Messages.getString("AuthenticationProvider.BadDefaultAuthenticationPlugin", new Object[]{defaultAuthenticationPluginValue}),
            this.getExceptionInterceptor()
         );
      }
   }

   private AuthenticationPlugin<NativePacketPayload> getAuthenticationPlugin(String pluginName) {
      AuthenticationPlugin<NativePacketPayload> plugin = this.authenticationPlugins.get(pluginName);
      if (plugin == null) {
         return null;
      } else {
         if (!plugin.isReusable()) {
            try {
               plugin = (AuthenticationPlugin<NativePacketPayload>)plugin.getClass().newInstance();
            } catch (Throwable var4) {
               throw (WrongArgumentException)ExceptionFactory.createException(
                  WrongArgumentException.class,
                  Messages.getString("AuthenticationProvider.BadAuthenticationPlugin", new Object[]{plugin.getClass().getName()}),
                  var4,
                  this.getExceptionInterceptor()
               );
            }
         }

         plugin.init(this.protocol, this.callbackHandler);
         return plugin;
      }
   }

   private void checkConfidentiality(AuthenticationPlugin<?> plugin) {
      if (plugin.requiresConfidentiality() && !this.protocol.getSocketConnection().isSSLEstablished()) {
         throw ExceptionFactory.createException(
            Messages.getString("AuthenticationProvider.AuthenticationPluginRequiresSSL", new Object[]{plugin.getProtocolPluginName()}),
            this.getExceptionInterceptor()
         );
      }
   }

   private void proceedHandshakeWithPluggableAuthentication(NativePacketPayload challenge) {
      ServerSession serverSession = this.protocol.getServerSession();
      if (this.authenticationPlugins == null) {
         this.loadAuthenticationPlugins();
      }

      boolean forChangeUser = true;
      if (challenge != null) {
         this.serverDefaultAuthenticationPluginName = challenge.readString(NativeConstants.StringSelfDataType.STRING_TERM, "ASCII");
         forChangeUser = false;
      }

      serverSession.getCharsetSettings().configurePreHandshake(forChangeUser);
      String pluginName;
      if (this.clientDefaultAuthenticationPluginExplicitelySet) {
         pluginName = this.clientDefaultAuthenticationPluginName;
      } else {
         pluginName = this.serverDefaultAuthenticationPluginName != null
            ? this.serverDefaultAuthenticationPluginName
            : this.clientDefaultAuthenticationPluginName;
      }

      AuthenticationPlugin<NativePacketPayload> plugin = this.getAuthenticationPlugin(pluginName);
      if (plugin == null) {
         pluginName = this.clientDefaultAuthenticationPluginName;
         plugin = this.getAuthenticationPlugin(pluginName);
      }

      boolean skipPassword = false;
      if (pluginName.equals(Sha256PasswordPlugin.PLUGIN_NAME)
         && !pluginName.equals(this.clientDefaultAuthenticationPluginName)
         && !this.protocol.getSocketConnection().isSSLEstablished()
         && this.propertySet.getStringProperty(PropertyKey.serverRSAPublicKeyFile).getValue() == null
         && !this.propertySet.getBooleanProperty(PropertyKey.allowPublicKeyRetrieval).getValue()) {
         plugin = this.getAuthenticationPlugin(this.clientDefaultAuthenticationPluginName);
         skipPassword = true;
      }

      this.checkConfidentiality(plugin);
      NativePacketPayload fromServer = new NativePacketPayload(StringUtils.getBytes(this.seed));
      String sourceOfAuthData = this.serverDefaultAuthenticationPluginName;
      boolean old_raw_challenge = false;
      NativePacketPayload last_sent = null;
      NativePacketPayload last_received = challenge;
      ArrayList<NativePacketPayload> toServer = new ArrayList<>();

      int counter;
      for (counter = 100; 0 < counter--; sourceOfAuthData = pluginName) {
         plugin.setAuthenticationParameters(this.username, skipPassword ? null : this.password);
         plugin.setSourceOfAuthData(sourceOfAuthData);
         plugin.nextAuthenticationStep(fromServer, toServer);
         if (toServer.size() > 0) {
            if (forChangeUser) {
               last_sent = this.createChangeUserPacket(serverSession, plugin.getProtocolPluginName(), toServer);
               this.protocol.send(last_sent, last_sent.getPosition());
               forChangeUser = false;
            } else if (!last_received.isAuthMethodSwitchRequestPacket() && !last_received.isAuthMoreData() && !old_raw_challenge) {
               last_sent = this.createHandshakeResponsePacket(serverSession, plugin.getProtocolPluginName(), toServer);
               this.protocol.send(last_sent, last_sent.getPosition());
            } else {
               for (NativePacketPayload buffer : toServer) {
                  this.protocol.send(buffer, buffer.getPayloadLength());
               }
            }
         }

         last_received = this.protocol.checkErrorMessage();
         old_raw_challenge = false;
         if (last_received.isOKPacket()) {
            OkPacket ok = OkPacket.parse(last_received, null);
            serverSession.setStatusFlags(ok.getStatusFlags(), true);
            serverSession.getServerSessionStateController().setSessionStateChanges(ok.getSessionStateChanges());
            plugin.destroy();
            break;
         }

         if (last_received.isAuthMethodSwitchRequestPacket()) {
            skipPassword = false;
            pluginName = last_received.readString(NativeConstants.StringSelfDataType.STRING_TERM, "ASCII");
            if (plugin.getProtocolPluginName().equals(pluginName)) {
               plugin.reset();
            } else {
               plugin.destroy();
               plugin = this.getAuthenticationPlugin(pluginName);
               if (plugin == null) {
                  throw (WrongArgumentException)ExceptionFactory.createException(
                     WrongArgumentException.class,
                     Messages.getString("AuthenticationProvider.BadAuthenticationPlugin", new Object[]{pluginName}),
                     this.getExceptionInterceptor()
                  );
               }
            }

            this.checkConfidentiality(plugin);
            fromServer = new NativePacketPayload(last_received.readBytes(NativeConstants.StringSelfDataType.STRING_EOF));
         } else {
            if (!this.protocol.versionMeetsMinimum(5, 5, 16)) {
               old_raw_challenge = true;
               last_received.setPosition(last_received.getPosition() - 1);
            }

            fromServer = new NativePacketPayload(last_received.readBytes(NativeConstants.StringSelfDataType.STRING_EOF));
         }
      }

      if (counter == 0) {
         throw (WrongArgumentException)ExceptionFactory.createException(
            WrongArgumentException.class, Messages.getString("CommunicationsException.TooManyAuthenticationPluginNegotiations"), this.getExceptionInterceptor()
         );
      } else {
         this.protocol.afterHandshake();
         if (!this.useConnectWithDb) {
            this.protocol.changeDatabase(this.database);
         }
      }
   }

   private Map<String, String> getConnectionAttributesMap(String attStr) {
      Map<String, String> attMap = new HashMap<>();
      if (attStr != null) {
         String[] pairs = attStr.split(",");

         for (String pair : pairs) {
            int keyEnd = pair.indexOf(":");
            if (keyEnd > 0 && keyEnd + 1 < pair.length()) {
               attMap.put(pair.substring(0, keyEnd), pair.substring(keyEnd + 1));
            }
         }
      }

      attMap.put("_client_name", "MySQL Connector/J");
      attMap.put("_client_version", "8.0.27");
      attMap.put("_runtime_vendor", Constants.JVM_VENDOR);
      attMap.put("_runtime_version", Constants.JVM_VERSION);
      attMap.put("_client_license", "GPL");
      return attMap;
   }

   private void appendConnectionAttributes(NativePacketPayload buf, String attributes, String enc) {
      NativePacketPayload lb = new NativePacketPayload(100);
      Map<String, String> attMap = this.getConnectionAttributesMap(attributes);

      for (String key : attMap.keySet()) {
         lb.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, StringUtils.getBytes(key, enc));
         lb.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, StringUtils.getBytes(attMap.get(key), enc));
      }

      buf.writeInteger(NativeConstants.IntegerDataType.INT_LENENC, (long)lb.getPosition());
      buf.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, lb.getByteBuffer(), 0, lb.getPosition());
   }

   public ExceptionInterceptor getExceptionInterceptor() {
      return this.exceptionInterceptor;
   }

   @Override
   public void changeUser(String user, String pass, String db) {
      this.username = user;
      this.password = pass;
      this.database = db;
      this.proceedHandshakeWithPluggableAuthentication(null);
      this.password = null;
   }

   private NativePacketPayload createHandshakeResponsePacket(ServerSession serverSession, String pluginName, ArrayList<NativePacketPayload> toServer) {
      long clientParam = serverSession.getClientParam();
      int collationIndex = serverSession.getCharsetSettings().configurePreHandshake(false);
      String enc = serverSession.getCharsetSettings().getPasswordCharacterEncoding();
      int userLength = this.username == null ? 0 : this.username.length();
      NativePacketPayload last_sent = new NativePacketPayload(88 + 3 * userLength + (this.useConnectWithDb ? 3 * this.database.length() : 0));
      last_sent.writeInteger(NativeConstants.IntegerDataType.INT4, clientParam);
      last_sent.writeInteger(NativeConstants.IntegerDataType.INT4, 16777215L);
      last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, (long)collationIndex);
      last_sent.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, new byte[23]);
      last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_TERM, StringUtils.getBytes(this.username, enc));
      if ((clientParam & 2097152L) != 0L) {
         last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, toServer.get(0).readBytes(NativeConstants.StringSelfDataType.STRING_EOF));
      } else {
         last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, (long)toServer.get(0).getPayloadLength());
         last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_EOF, toServer.get(0).getByteBuffer());
      }

      if (this.useConnectWithDb) {
         last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_TERM, StringUtils.getBytes(this.database, enc));
      }

      last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_TERM, StringUtils.getBytes(pluginName, enc));
      if ((clientParam & 1048576L) != 0L) {
         this.appendConnectionAttributes(last_sent, this.propertySet.getStringProperty(PropertyKey.connectionAttributes).getValue(), enc);
      }

      return last_sent;
   }

   private NativePacketPayload createChangeUserPacket(ServerSession serverSession, String pluginName, ArrayList<NativePacketPayload> toServer) {
      long clientParam = serverSession.getClientParam();
      int collationIndex = serverSession.getCharsetSettings().configurePreHandshake(false);
      String enc = serverSession.getCharsetSettings().getPasswordCharacterEncoding();
      NativePacketPayload last_sent = new NativePacketPayload(88 + 3 * this.username.length() + (this.useConnectWithDb ? 3 * this.database.length() : 1) + 1);
      last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, 17L);
      last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_TERM, StringUtils.getBytes(this.username, enc));
      if (toServer.get(0).getPayloadLength() < 256) {
         last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, (long)toServer.get(0).getPayloadLength());
         last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_EOF, toServer.get(0).getByteBuffer(), 0, toServer.get(0).getPayloadLength());
      } else {
         last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, 0L);
      }

      if (this.useConnectWithDb) {
         last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_TERM, StringUtils.getBytes(this.database, enc));
      } else {
         last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, 0L);
      }

      last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, (long)collationIndex);
      last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, 0L);
      last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_TERM, StringUtils.getBytes(pluginName, enc));
      if ((clientParam & 1048576L) != 0L) {
         this.appendConnectionAttributes(last_sent, this.propertySet.getStringProperty(PropertyKey.connectionAttributes).getValue(), enc);
      }

      return last_sent;
   }
}
