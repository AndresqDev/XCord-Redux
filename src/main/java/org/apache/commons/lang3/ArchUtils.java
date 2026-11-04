package org.apache.commons.lang3;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.arch.Processor;

public class ArchUtils {
   private static final Map<String, Processor> ARCH_TO_PROCESSOR = new HashMap<>();

   private static void init() {
      init_X86_32Bit();
      init_X86_64Bit();
      init_IA64_32Bit();
      init_IA64_64Bit();
      init_PPC_32Bit();
      init_PPC_64Bit();
   }

   private static void init_X86_32Bit() {
      Processor var0 = new Processor(Processor.Arch.BIT_32, Processor.Type.X86);
      addProcessors(var0, "x86", "i386", "i486", "i586", "i686", "pentium");
   }

   private static void init_X86_64Bit() {
      Processor var0 = new Processor(Processor.Arch.BIT_64, Processor.Type.X86);
      addProcessors(var0, "x86_64", "amd64", "em64t", "universal");
   }

   private static void init_IA64_32Bit() {
      Processor var0 = new Processor(Processor.Arch.BIT_32, Processor.Type.IA_64);
      addProcessors(var0, "ia64_32", "ia64n");
   }

   private static void init_IA64_64Bit() {
      Processor var0 = new Processor(Processor.Arch.BIT_64, Processor.Type.IA_64);
      addProcessors(var0, "ia64", "ia64w");
   }

   private static void init_PPC_32Bit() {
      Processor var0 = new Processor(Processor.Arch.BIT_32, Processor.Type.PPC);
      addProcessors(var0, "ppc", "power", "powerpc", "power_pc", "power_rs");
   }

   private static void init_PPC_64Bit() {
      Processor var0 = new Processor(Processor.Arch.BIT_64, Processor.Type.PPC);
      addProcessors(var0, "ppc64", "power64", "powerpc64", "power_pc64", "power_rs64");
   }

   private static void addProcessor(String var0, Processor var1) {
      if (ARCH_TO_PROCESSOR.containsKey(var0)) {
         throw new IllegalStateException("Key " + var0 + " already exists in processor map");
      } else {
         ARCH_TO_PROCESSOR.put(var0, var1);
      }
   }

   private static void addProcessors(Processor var0, String... var1) {
      for (String var5 : var1) {
         addProcessor(var5, var0);
      }
   }

   public static Processor getProcessor() {
      return getProcessor(SystemUtils.OS_ARCH);
   }

   public static Processor getProcessor(String var0) {
      return ARCH_TO_PROCESSOR.get(var0);
   }

   static {
      init();
   }
}
