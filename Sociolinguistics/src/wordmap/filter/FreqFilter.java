package wordmap.filter;

import java.util.Map.Entry;

import filter.FilterFunction;
import filter.Filterable;
import filter.GenericFilter;
import wordmap.Combinable;
import wordmap.CountCombine;
import wordmap.WordMap;

public class FreqFilter extends GenericFilter<Entry<String, Combinable>, WordMap>  {

	private static final int min = 100;
	private static final int max = Integer.MAX_VALUE;
	private static boolean isBetween(int lowerBound, int upperBound, int count) {

		return count >= lowerBound && count <= upperBound;
	}
	@Override
	protected FilterFunction<Entry<String, Combinable>> createFilter(Filterable<Entry<String, Combinable>> f) {
		WordMap wm = (WordMap) f;
		return new FilterFunction<Entry<String, Combinable>>() {
			public boolean good(Entry<String, Combinable> entry) {
				return isBetween(min, max, ((CountCombine) wm.getBy(entry.getKey(), CountCombine.class)).getCount());
			}
		};
	}
}
