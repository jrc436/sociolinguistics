package wordmap.filter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map.Entry;

import filter.FilterFunction;
import filter.Filterable;
import filter.GenericFilter;
import wordmap.Combinable;
import wordmap.EarlyDateCombine;
import wordmap.WordMap;

public class DateFilter extends GenericFilter<Entry<String, Combinable>, WordMap> {
	private static final Instant earliestDate = LocalDate.of(2013, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant();
	private static final Instant latestDate = LocalDate.of(2014, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant();
	@Override
	protected FilterFunction<Entry<String, Combinable>> createFilter(Filterable<Entry<String, Combinable>> f) {
		WordMap wm = (WordMap) f;
		return new FilterFunction<Entry<String, Combinable>>() {
			@Override
			public boolean good(Entry<String, Combinable> entry) {
				Instant postTime =  ((EarlyDateCombine) wm.getBy(entry.getKey(), EarlyDateCombine.class)).getTime();
				return postTime.isAfter(earliestDate) && postTime.isBefore(latestDate);
			}
		};
	}
	
}
