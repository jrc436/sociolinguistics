package util;

public class ResourceAllocator {
	public static int getSuggestedNumThreads(long memPerThread) {
		long allMem = Runtime.getRuntime().maxMemory();
		int memory = (int) (allMem / memPerThread);
		int processors = Runtime.getRuntime().availableProcessors();
		int numRuns = Math.min(memory, processors);
		return Math.max(numRuns, 1);
	}
}
