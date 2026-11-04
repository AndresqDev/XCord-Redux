package org.eclipse.sisu.space;

import java.net.URL;

public interface SpaceVisitor {
   void enterSpace(ClassSpace var1);

   ClassVisitor visitClass(URL var1);

   void leaveSpace();
}
