package org.eclipse.sisu.wire;

import com.google.inject.Key;
import java.util.Map;
import org.eclipse.sisu.Parameters;

public interface ParameterKeys {
   Key<Map> PROPERTIES = Key.get(Map.class, Parameters.class);
   Key<String[]> ARGUMENTS = Key.get(String[].class, Parameters.class);
}
