package org.eclipse.aether.impl;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.RepositoryConnector;
import org.eclipse.aether.transfer.NoRepositoryConnectorException;

public interface RepositoryConnectorProvider {
   RepositoryConnector newRepositoryConnector(RepositorySystemSession var1, RemoteRepository var2) throws NoRepositoryConnectorException;
}
