package org.eclipse.aether.connector.basic;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.ArtifactDownload;
import org.eclipse.aether.spi.connector.ArtifactUpload;
import org.eclipse.aether.spi.connector.MetadataDownload;
import org.eclipse.aether.spi.connector.MetadataUpload;
import org.eclipse.aether.spi.connector.RepositoryConnector;
import org.eclipse.aether.spi.connector.checksum.ChecksumPolicy;
import org.eclipse.aether.spi.connector.checksum.ChecksumPolicyProvider;
import org.eclipse.aether.spi.connector.layout.RepositoryLayout;
import org.eclipse.aether.spi.connector.layout.RepositoryLayoutProvider;
import org.eclipse.aether.spi.connector.transport.GetTask;
import org.eclipse.aether.spi.connector.transport.PeekTask;
import org.eclipse.aether.spi.connector.transport.PutTask;
import org.eclipse.aether.spi.connector.transport.Transporter;
import org.eclipse.aether.spi.connector.transport.TransporterProvider;
import org.eclipse.aether.spi.io.FileProcessor;
import org.eclipse.aether.transfer.ChecksumFailureException;
import org.eclipse.aether.transfer.NoRepositoryConnectorException;
import org.eclipse.aether.transfer.NoRepositoryLayoutException;
import org.eclipse.aether.transfer.NoTransporterException;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferResource;
import org.eclipse.aether.transform.FileTransformer;
import org.eclipse.aether.util.ChecksumUtils;
import org.eclipse.aether.util.ConfigUtils;
import org.eclipse.aether.util.concurrency.RunnableErrorForwarder;
import org.eclipse.aether.util.concurrency.WorkerThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class BasicRepositoryConnector implements RepositoryConnector {
   private static final String CONFIG_PROP_THREADS = "aether.connector.basic.threads";
   private static final String CONFIG_PROP_RESUME = "aether.connector.resumeDownloads";
   private static final String CONFIG_PROP_RESUME_THRESHOLD = "aether.connector.resumeThreshold";
   private static final String CONFIG_PROP_SMART_CHECKSUMS = "aether.connector.smartChecksums";
   private static final Logger LOGGER = LoggerFactory.getLogger(BasicRepositoryConnector.class);
   private final FileProcessor fileProcessor;
   private final RemoteRepository repository;
   private final RepositorySystemSession session;
   private final Transporter transporter;
   private final RepositoryLayout layout;
   private final ChecksumPolicyProvider checksumPolicyProvider;
   private final PartialFile.Factory partialFileFactory;
   private final int maxThreads;
   private final boolean smartChecksums;
   private final boolean persistedChecksums;
   private Executor executor;
   private boolean closed;

   BasicRepositoryConnector(
      RepositorySystemSession var1,
      RemoteRepository var2,
      TransporterProvider var3,
      RepositoryLayoutProvider var4,
      ChecksumPolicyProvider var5,
      FileProcessor var6
   ) throws NoRepositoryConnectorException {
      try {
         this.layout = var4.newRepositoryLayout(var1, var2);
      } catch (NoRepositoryLayoutException var12) {
         throw new NoRepositoryConnectorException(var2, var12.getMessage(), var12);
      }

      try {
         this.transporter = var3.newTransporter(var1, var2);
      } catch (NoTransporterException var11) {
         throw new NoRepositoryConnectorException(var2, var11.getMessage(), var11);
      }

      this.checksumPolicyProvider = var5;
      this.session = var1;
      this.repository = var2;
      this.fileProcessor = var6;
      this.maxThreads = ConfigUtils.getInteger(var1, 5, "aether.connector.basic.threads", "maven.artifact.threads");
      this.smartChecksums = ConfigUtils.getBoolean(var1, true, "aether.connector.smartChecksums");
      this.persistedChecksums = ConfigUtils.getBoolean(var1, true, "aether.connector.persistedChecksums");
      boolean var7 = ConfigUtils.getBoolean(var1, true, "aether.connector.resumeDownloads." + var2.getId(), "aether.connector.resumeDownloads");
      long var8 = ConfigUtils.getLong(var1, 65536L, "aether.connector.resumeThreshold." + var2.getId(), "aether.connector.resumeThreshold");
      int var10 = ConfigUtils.getInteger(var1, 1800000, "aether.connector.requestTimeout." + var2.getId(), "aether.connector.requestTimeout");
      this.partialFileFactory = new PartialFile.Factory(var7, var8, var10);
   }

   private Executor getExecutor(Collection<?> var1, Collection<?> var2) {
      if (this.maxThreads <= 1) {
         return BasicRepositoryConnector.DirectExecutor.INSTANCE;
      } else {
         int var3 = safe(var1).size() + safe(var2).size();
         if (var3 <= 1) {
            return BasicRepositoryConnector.DirectExecutor.INSTANCE;
         } else {
            if (this.executor == null) {
               this.executor = new ThreadPoolExecutor(
                  this.maxThreads,
                  this.maxThreads,
                  3L,
                  TimeUnit.SECONDS,
                  new LinkedBlockingQueue<>(),
                  new WorkerThreadFactory(this.getClass().getSimpleName() + '-' + this.repository.getHost() + '-')
               );
            }

            return this.executor;
         }
      }
   }

   @Override
   protected void finalize() throws Throwable {
      try {
         this.close();
      } finally {
         super.finalize();
      }
   }

   @Override
   public void close() {
      if (!this.closed) {
         this.closed = true;
         if (this.executor instanceof ExecutorService) {
            ((ExecutorService)this.executor).shutdown();
         }

         this.transporter.close();
      }
   }

   @Override
   public void get(Collection<? extends ArtifactDownload> var1, Collection<? extends MetadataDownload> var2) {
      if (this.closed) {
         throw new IllegalStateException("connector closed");
      } else {
         Executor var3 = this.getExecutor(var1, var2);
         RunnableErrorForwarder var4 = new RunnableErrorForwarder();

         for (MetadataDownload var6 : safe(var2)) {
            URI var7 = this.layout.getLocation(var6.getMetadata(), false);
            TransferResource var8 = this.newTransferResource(var7, var6.getFile(), var6.getTrace());
            TransferEvent.Builder var9 = this.newEventBuilder(var8, false, false);
            MetadataTransportListener var10 = new MetadataTransportListener(var6, this.repository, var9);
            ChecksumPolicy var11 = this.newChecksumPolicy(var6.getChecksumPolicy(), var8);
            List var12 = null;
            if (var11 != null) {
               var12 = this.layout.getChecksums(var6.getMetadata(), false, var7);
            }

            BasicRepositoryConnector.GetTaskRunner var13 = new BasicRepositoryConnector.GetTaskRunner(var7, var6.getFile(), var11, var12, var10);
            var3.execute(var4.wrap(var13));
         }

         for (ArtifactDownload var15 : safe(var1)) {
            URI var16 = this.layout.getLocation(var15.getArtifact(), false);
            TransferResource var17 = this.newTransferResource(var16, var15.getFile(), var15.getTrace());
            TransferEvent.Builder var18 = this.newEventBuilder(var17, false, var15.isExistenceCheck());
            ArtifactTransportListener var19 = new ArtifactTransportListener(var15, this.repository, var18);
            Object var20;
            if (var15.isExistenceCheck()) {
               var20 = new BasicRepositoryConnector.PeekTaskRunner(var16, var19);
            } else {
               ChecksumPolicy var21 = this.newChecksumPolicy(var15.getChecksumPolicy(), var17);
               List var22 = null;
               if (var21 != null) {
                  var22 = this.layout.getChecksums(var15.getArtifact(), false, var16);
               }

               var20 = new BasicRepositoryConnector.GetTaskRunner(var16, var15.getFile(), var21, var22, var19);
            }

            var3.execute(var4.wrap((Runnable)var20));
         }

         var4.await();
      }
   }

   @Override
   public void put(Collection<? extends ArtifactUpload> var1, Collection<? extends MetadataUpload> var2) {
      if (this.closed) {
         throw new IllegalStateException("connector closed");
      } else {
         for (ArtifactUpload var4 : safe(var1)) {
            URI var5 = this.layout.getLocation(var4.getArtifact(), true);
            TransferResource var6 = this.newTransferResource(var5, var4.getFile(), var4.getTrace());
            TransferEvent.Builder var7 = this.newEventBuilder(var6, true, false);
            ArtifactTransportListener var8 = new ArtifactTransportListener(var4, this.repository, var7);
            List var9 = this.layout.getChecksums(var4.getArtifact(), true, var5);
            BasicRepositoryConnector.PutTaskRunner var10 = new BasicRepositoryConnector.PutTaskRunner(
               var5, var4.getFile(), var4.getFileTransformer(), var9, var8
            );
            var10.run();
         }

         for (MetadataUpload var12 : safe(var2)) {
            URI var13 = this.layout.getLocation(var12.getMetadata(), true);
            TransferResource var14 = this.newTransferResource(var13, var12.getFile(), var12.getTrace());
            TransferEvent.Builder var15 = this.newEventBuilder(var14, true, false);
            MetadataTransportListener var16 = new MetadataTransportListener(var12, this.repository, var15);
            List var17 = this.layout.getChecksums(var12.getMetadata(), true, var13);
            BasicRepositoryConnector.PutTaskRunner var18 = new BasicRepositoryConnector.PutTaskRunner(var13, var12.getFile(), var17, var16);
            var18.run();
         }
      }
   }

   private static <T> Collection<T> safe(Collection<T> var0) {
      return (Collection<T>)(var0 != null ? var0 : Collections.emptyList());
   }

   private TransferResource newTransferResource(URI var1, File var2, RequestTrace var3) {
      return new TransferResource(this.repository.getId(), this.repository.getUrl(), var1.toString(), var2, var3);
   }

   private TransferEvent.Builder newEventBuilder(TransferResource var1, boolean var2, boolean var3) {
      TransferEvent.Builder var4 = new TransferEvent.Builder(this.session, var1);
      if (var2) {
         var4.setRequestType(TransferEvent.RequestType.PUT);
      } else if (!var3) {
         var4.setRequestType(TransferEvent.RequestType.GET);
      } else {
         var4.setRequestType(TransferEvent.RequestType.GET_EXISTENCE);
      }

      return var4;
   }

   private ChecksumPolicy newChecksumPolicy(String var1, TransferResource var2) {
      return this.checksumPolicyProvider.newChecksumPolicy(this.session, this.repository, var2, var1);
   }

   @Override
   public String toString() {
      return String.valueOf(this.repository);
   }

   private static class DirectExecutor implements Executor {
      static final Executor INSTANCE = new BasicRepositoryConnector.DirectExecutor();

      @Override
      public void execute(Runnable var1) {
         var1.run();
      }
   }

   class GetTaskRunner extends BasicRepositoryConnector.TaskRunner implements ChecksumValidator.ChecksumFetcher, PartialFile.RemoteAccessChecker {
      private final File file;
      private final ChecksumValidator checksumValidator;

      GetTaskRunner(URI var2, File var3, ChecksumPolicy var4, List<RepositoryLayout.Checksum> var5, TransferTransportListener<?> var6) {
         super(var2, var6);
         this.file = Objects.requireNonNull(var3, "destination file cannot be null");
         this.checksumValidator = new ChecksumValidator(var3, BasicRepositoryConnector.this.fileProcessor, this, var4, BasicRepositoryConnector.safe(var5));
      }

      @Override
      public void checkRemoteAccess() throws Exception {
         BasicRepositoryConnector.this.transporter.peek(new PeekTask(this.path));
      }

      @Override
      public boolean fetchChecksum(URI var1, File var2) throws Exception {
         try {
            BasicRepositoryConnector.this.transporter.get(new GetTask(var1).setDataFile(var2));
            return true;
         } catch (Exception var4) {
            if (BasicRepositoryConnector.this.transporter.classify(var4) == 1) {
               return false;
            } else {
               throw var4;
            }
         }
      }

      @Override
      protected void runTask() throws Exception {
         BasicRepositoryConnector.this.fileProcessor.mkdirs(this.file.getParentFile());
         PartialFile var1 = BasicRepositoryConnector.this.partialFileFactory.newInstance(this.file, this);
         if (var1 == null) {
            BasicRepositoryConnector.LOGGER.debug("Concurrent download of {} just finished, skipping download", this.file);
         } else {
            try {
               File var2 = var1.getFile();
               this.listener.setChecksumCalculator(this.checksumValidator.newChecksumCalculator(var2));
               byte var3 = 0;
               byte var4 = 1;
               int var5 = var3;

               while (true) {
                  boolean var6 = var1.isResume() && var5 <= var3;
                  GetTask var7 = new GetTask(this.path).setDataFile(var2, var6).setListener(this.listener);
                  BasicRepositoryConnector.this.transporter.get(var7);

                  try {
                     this.checksumValidator.validate(this.listener.getChecksums(), BasicRepositoryConnector.this.smartChecksums ? var7.getChecksums() : null);
                     break;
                  } catch (ChecksumFailureException var13) {
                     boolean var9 = var5 < var4 && var13.isRetryWorthy();
                     if (!var9 && !this.checksumValidator.handle(var13)) {
                        throw var13;
                     }

                     this.listener.transferCorrupted(var13);
                     if (!var9) {
                        break;
                     }

                     this.checksumValidator.retry();
                     var5++;
                  }
               }

               BasicRepositoryConnector.this.fileProcessor.move(var2, this.file);
               if (BasicRepositoryConnector.this.persistedChecksums) {
                  this.checksumValidator.commit();
               }
            } finally {
               var1.close();
               this.checksumValidator.close();
            }
         }
      }
   }

   class PeekTaskRunner extends BasicRepositoryConnector.TaskRunner {
      PeekTaskRunner(URI var2, TransferTransportListener<?> var3) {
         super(var2, var3);
      }

      @Override
      protected void runTask() throws Exception {
         BasicRepositoryConnector.this.transporter.peek(new PeekTask(this.path));
      }
   }

   class PutTaskRunner extends BasicRepositoryConnector.TaskRunner {
      private final File file;
      private final FileTransformer fileTransformer;
      private final Collection<RepositoryLayout.Checksum> checksums;

      PutTaskRunner(URI var2, File var3, List<RepositoryLayout.Checksum> var4, TransferTransportListener<?> var5) {
         this(var2, var3, null, var4, var5);
      }

      PutTaskRunner(URI var2, File var3, FileTransformer var4, List<RepositoryLayout.Checksum> var5, TransferTransportListener<?> var6) {
         super(var2, var6);
         this.file = Objects.requireNonNull(var3, "source file cannot be null");
         this.fileTransformer = var4;
         this.checksums = BasicRepositoryConnector.safe(var5);
      }

      @Override
      protected void runTask() throws Exception {
         if (this.fileTransformer != null) {
            ByteArrayOutputStream var1 = new ByteArrayOutputStream();
            byte[] var2 = new byte[1024];

            int var5;
            try (InputStream var3 = this.fileTransformer.transformData(this.file)) {
               while ((var5 = var3.read(var2, 0, var2.length)) != -1) {
                  var1.write(var2, 0, var5);
               }
            }

            byte[] var15 = var1.toByteArray();
            BasicRepositoryConnector.this.transporter.put(new PutTask(this.path).setDataBytes(var15).setListener(this.listener));
            this.uploadChecksums(this.file, var15);
         } else {
            BasicRepositoryConnector.this.transporter.put(new PutTask(this.path).setDataFile(this.file).setListener(this.listener));
            this.uploadChecksums(this.file, null);
         }
      }

      private void uploadChecksums(File var1, byte[] var2) {
         if (!this.checksums.isEmpty()) {
            try {
               HashSet var3 = new HashSet();

               for (RepositoryLayout.Checksum var5 : this.checksums) {
                  var3.add(var5.getAlgorithm());
               }

               Map var8;
               if (var2 != null) {
                  var8 = ChecksumUtils.calc(var2, var3);
               } else {
                  var8 = ChecksumUtils.calc(var1, var3);
               }

               for (RepositoryLayout.Checksum var6 : this.checksums) {
                  this.uploadChecksum(var6.getLocation(), var8.get(var6.getAlgorithm()));
               }
            } catch (IOException var7) {
               BasicRepositoryConnector.LOGGER.warn("Failed to upload checksums for {}", var1, var7);
            }
         }
      }

      private void uploadChecksum(URI var1, Object var2) {
         try {
            if (var2 instanceof Exception) {
               throw (Exception)var2;
            }

            BasicRepositoryConnector.this.transporter.put(new PutTask(var1).setDataString((String)var2));
         } catch (Exception var4) {
            BasicRepositoryConnector.LOGGER.warn("Failed to upload checksum to {}", var1, var4);
         }
      }
   }

   abstract class TaskRunner implements Runnable {
      protected final URI path;
      protected final TransferTransportListener<?> listener;

      TaskRunner(URI var2, TransferTransportListener<?> var3) {
         this.path = var2;
         this.listener = var3;
      }

      @Override
      public void run() {
         try {
            this.listener.transferInitiated();
            this.runTask();
            this.listener.transferSucceeded();
         } catch (Exception var2) {
            this.listener.transferFailed(var2, BasicRepositoryConnector.this.transporter.classify(var2));
         }
      }

      protected abstract void runTask() throws Exception;
   }
}
