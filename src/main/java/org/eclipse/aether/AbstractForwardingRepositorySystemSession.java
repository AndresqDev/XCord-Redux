package org.eclipse.aether;

import java.util.Map;
import org.eclipse.aether.artifact.ArtifactTypeRegistry;
import org.eclipse.aether.collection.DependencyGraphTransformer;
import org.eclipse.aether.collection.DependencyManager;
import org.eclipse.aether.collection.DependencySelector;
import org.eclipse.aether.collection.DependencyTraverser;
import org.eclipse.aether.collection.VersionFilter;
import org.eclipse.aether.repository.AuthenticationSelector;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.MirrorSelector;
import org.eclipse.aether.repository.ProxySelector;
import org.eclipse.aether.repository.WorkspaceReader;
import org.eclipse.aether.resolution.ArtifactDescriptorPolicy;
import org.eclipse.aether.resolution.ResolutionErrorPolicy;
import org.eclipse.aether.transfer.TransferListener;
import org.eclipse.aether.transform.FileTransformerManager;

public abstract class AbstractForwardingRepositorySystemSession implements RepositorySystemSession {
   protected AbstractForwardingRepositorySystemSession() {
   }

   protected abstract RepositorySystemSession getSession();

   @Override
   public boolean isOffline() {
      return this.getSession().isOffline();
   }

   @Override
   public boolean isIgnoreArtifactDescriptorRepositories() {
      return this.getSession().isIgnoreArtifactDescriptorRepositories();
   }

   @Override
   public ResolutionErrorPolicy getResolutionErrorPolicy() {
      return this.getSession().getResolutionErrorPolicy();
   }

   @Override
   public ArtifactDescriptorPolicy getArtifactDescriptorPolicy() {
      return this.getSession().getArtifactDescriptorPolicy();
   }

   @Override
   public String getChecksumPolicy() {
      return this.getSession().getChecksumPolicy();
   }

   @Override
   public String getUpdatePolicy() {
      return this.getSession().getUpdatePolicy();
   }

   @Override
   public LocalRepository getLocalRepository() {
      return this.getSession().getLocalRepository();
   }

   @Override
   public LocalRepositoryManager getLocalRepositoryManager() {
      return this.getSession().getLocalRepositoryManager();
   }

   @Override
   public WorkspaceReader getWorkspaceReader() {
      return this.getSession().getWorkspaceReader();
   }

   @Override
   public RepositoryListener getRepositoryListener() {
      return this.getSession().getRepositoryListener();
   }

   @Override
   public TransferListener getTransferListener() {
      return this.getSession().getTransferListener();
   }

   @Override
   public Map<String, String> getSystemProperties() {
      return this.getSession().getSystemProperties();
   }

   @Override
   public Map<String, String> getUserProperties() {
      return this.getSession().getUserProperties();
   }

   @Override
   public Map<String, Object> getConfigProperties() {
      return this.getSession().getConfigProperties();
   }

   @Override
   public MirrorSelector getMirrorSelector() {
      return this.getSession().getMirrorSelector();
   }

   @Override
   public ProxySelector getProxySelector() {
      return this.getSession().getProxySelector();
   }

   @Override
   public AuthenticationSelector getAuthenticationSelector() {
      return this.getSession().getAuthenticationSelector();
   }

   @Override
   public ArtifactTypeRegistry getArtifactTypeRegistry() {
      return this.getSession().getArtifactTypeRegistry();
   }

   @Override
   public DependencyTraverser getDependencyTraverser() {
      return this.getSession().getDependencyTraverser();
   }

   @Override
   public DependencyManager getDependencyManager() {
      return this.getSession().getDependencyManager();
   }

   @Override
   public DependencySelector getDependencySelector() {
      return this.getSession().getDependencySelector();
   }

   @Override
   public VersionFilter getVersionFilter() {
      return this.getSession().getVersionFilter();
   }

   @Override
   public DependencyGraphTransformer getDependencyGraphTransformer() {
      return this.getSession().getDependencyGraphTransformer();
   }

   @Override
   public SessionData getData() {
      return this.getSession().getData();
   }

   @Override
   public RepositoryCache getCache() {
      return this.getSession().getCache();
   }

   @Override
   public FileTransformerManager getFileTransformerManager() {
      return this.getSession().getFileTransformerManager();
   }
}
