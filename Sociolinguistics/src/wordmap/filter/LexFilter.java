package wordmap.filter;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import filter.FilterFunction;
import filter.Filterable;
import filter.GenericFilter;
import wordmap.Combinable;
import wordmap.WordMap;

public class LexFilter extends GenericFilter<Entry<String, Combinable>, WordMap> {
	private static final Pattern wordDef = Pattern.compile("^[a-zA-Z]+$");
	private static boolean isWord(Map.Entry<String, Combinable> s) {
		return wordDef.matcher(s.getKey()).matches();
	}
	@Override
	protected FilterFunction<Entry<String, Combinable>> createFilter(Filterable<Entry<String, Combinable>> collection) {
		return LexFilter::isWord;
	}
}
