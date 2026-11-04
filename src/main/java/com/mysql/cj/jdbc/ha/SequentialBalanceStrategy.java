package com.mysql.cj.jdbc.ha;

import com.mysql.cj.jdbc.ConnectionImpl;
import com.mysql.cj.jdbc.JdbcConnection;
import java.lang.reflect.InvocationHandler;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class SequentialBalanceStrategy implements BalanceStrategy {
   private int currentHostIndex = -1;

   public ConnectionImpl pickConnection(
      InvocationHandler proxy, List<String> configuredHosts, Map<String, JdbcConnection> liveConnections, long[] responseTimes, int numRetries
   ) throws SQLException {
      int numHosts = configuredHosts.size();
      SQLException ex = null;
      Map<String, Long> blockList = ((LoadBalancedConnectionProxy)proxy).getGlobalBlocklist();
      int attempts = 0;

      ConnectionImpl conn;
      while (true) {
         if (attempts >= numRetries) {
            if (ex != null) {
               throw ex;
            }

            return null;
         }

         label129: {
            if (numHosts == 1) {
               this.currentHostIndex = 0;
            } else if (this.currentHostIndex == -1) {
               int random = (int)Math.floor(Math.random() * (double)numHosts);

               for (int i = random; i < numHosts; i++) {
                  if (!blockList.containsKey(configuredHosts.get(i))) {
                     this.currentHostIndex = i;
                     break;
                  }
               }

               if (this.currentHostIndex == -1) {
                  for (int ix = 0; ix < random; ix++) {
                     if (!blockList.containsKey(configuredHosts.get(ix))) {
                        this.currentHostIndex = ix;
                        break;
                     }
                  }
               }

               if (this.currentHostIndex == -1) {
                  blockList = ((LoadBalancedConnectionProxy)proxy).getGlobalBlocklist();

                  try {
                     Thread.sleep(250L);
                  } catch (InterruptedException var16) {
                  }
                  break label129;
               }
            } else {
               int ixx = this.currentHostIndex + 1;

               for (foundGoodHost = false; ixx < numHosts; ixx++) {
                  if (!blockList.containsKey(configuredHosts.get(ixx))) {
                     this.currentHostIndex = ixx;
                     foundGoodHost = true;
                     break;
                  }
               }

               if (!foundGoodHost) {
                  for (int var19 = 0; var19 < this.currentHostIndex; var19++) {
                     if (!blockList.containsKey(configuredHosts.get(var19))) {
                        this.currentHostIndex = var19;
                        foundGoodHost = true;
                        break;
                     }
                  }
               }

               if (!foundGoodHost) {
                  blockList = ((LoadBalancedConnectionProxy)proxy).getGlobalBlocklist();

                  try {
                     Thread.sleep(250L);
                  } catch (InterruptedException var15) {
                  }
                  break label129;
               }
            }

            String hostPortSpec = configuredHosts.get(this.currentHostIndex);
            conn = (ConnectionImpl)liveConnections.get(hostPortSpec);
            if (conn != null) {
               break;
            }

            try {
               conn = ((LoadBalancedConnectionProxy)proxy).createConnectionForHost(hostPortSpec);
               break;
            } catch (SQLException var17) {
               ex = var17;
               if (!((LoadBalancedConnectionProxy)proxy).shouldExceptionTriggerConnectionSwitch(var17)) {
                  throw var17;
               }

               ((LoadBalancedConnectionProxy)proxy).addToGlobalBlocklist(hostPortSpec);

               try {
                  Thread.sleep(250L);
               } catch (InterruptedException var14) {
               }
            }
         }

         attempts++;
      }

      return conn;
   }
}
