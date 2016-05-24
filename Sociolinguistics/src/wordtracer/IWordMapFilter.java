package wordtracer;

import java.util.Map;

@FunctionalInterface
public interface IWordMapFilter {
	public boolean goodEntry(Map.Entry<String, Integer> entry);	
}
