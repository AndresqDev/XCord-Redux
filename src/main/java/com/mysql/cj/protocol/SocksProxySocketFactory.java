package com.mysql.cj.protocol;

import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.Proxy.Type;

public class SocksProxySocketFactory extends StandardSocketFactory {
   @Override
   protected Socket createSocket(PropertySet props) {
      String socksProxyHost = props.getStringProperty(PropertyKey.socksProxyHost).getValue();
      int socksProxyPort = props.getIntegerProperty(PropertyKey.socksProxyPort).getValue();
      return new Socket(new Proxy(Type.SOCKS, new InetSocketAddress(socksProxyHost, socksProxyPort)));
   }
}
