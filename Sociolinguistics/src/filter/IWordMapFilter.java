package filter;

import java.util.Map;

import wordmap.Combinable;

@FunctionalInterface
public interface IWordMapFilter {
	public boolean goodEntry(Map.Entry<String, Combinable> entry);	
}
