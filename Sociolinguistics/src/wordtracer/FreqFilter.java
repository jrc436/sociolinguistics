package wordtracer;

import java.util.Map.Entry;

import util.WordMap;

public class FreqFilter extends WordMapFilter {
	private static final double lowerboundFreq = 0.0001;
	private static final double upperboundFreq = 0.005;
	private int getMapFreqSum(WordMap wm) {
		int sum = 0;
		for (String s : wm.keySet()) {
			sum += wm.get(s);
		}
		return sum;
	}
	private static boolean isBetween(double lowerBound, double upperBound, int count, int total) {
		double rate = ((double)count) / ((double) total);
		return rate >= lowerBound && rate <= upperBound;
	}
	@Override
	protected IWordMapFilter createFilter(WordMap wm) {
		int freqSum = getMapFreqSum(wm);
		return new IWordMapFilter() {
			@Override
			public boolean goodEntry(Entry<String, Integer> entry) {
				return isBetween(lowerboundFreq, upperboundFreq, entry.getValue(), freqSum);
			}
		};
	}
}
