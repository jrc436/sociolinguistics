package util;

import java.math.BigDecimal;

public class ResourceAllocator {
	public static int getSuggestedNumThreads(int memGBPerThread) {
		 long systemMemB = Runtime.getRuntime().maxMemory();
         BigDecimal memReqGB = new BigDecimal(memGBPerThread);
         BigDecimal bytesPerGB = new BigDecimal(1000000000);
         BigDecimal memoryInGB = new BigDecimal(systemMemB);
         BigDecimal divided = memoryInGB.divide(bytesPerGB).divide(memReqGB);
         int memoryAllows = divided.intValue();
         int processors = Runtime.getRuntime().availableProcessors();
         return Math.max(1, Math.min(memoryAllows, processors));
	}
}
