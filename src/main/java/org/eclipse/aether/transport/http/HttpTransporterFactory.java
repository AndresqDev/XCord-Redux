package org.eclipse.aether.transport.http;

import java.util.Objects;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.transport.Transporter;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transfer.NoTransporterException;
import xcord.Named;

@Named("http")
public final class HttpTransporterFactory implements TransporterFactory {
   private float priority = 5.0F;

   @Override
   public float getPriority() {
      return this.priority;
   }

   public HttpTransporterFactory setPriority(float var1) {
      this.priority = var1;
      return this;
   }

   @Override
   public Transporter newInstance(RepositorySystemSession var1, RemoteRepository var2) throws NoTransporterException {
      Objects.requireNonNull("session", "session cannot be null");
      Objects.requireNonNull("repository", "repository cannot be null");
      return new HttpTransporter(var2, var1);
   }
}
