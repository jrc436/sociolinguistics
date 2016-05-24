package wordtracer;

import java.util.Map;
import java.util.regex.Pattern;

import util.WordMap;

public class LexFilter extends WordMapFilter {
	private static final Pattern wordDef = Pattern.compile("^[a-zA-Z]+$");
	public static boolean isWord(Map.Entry<String, Integer> s) {
		return wordDef.matcher(s.getKey()).matches();
	}
	@Override
	protected IWordMapFilter createFilter(WordMap wm) {
		return LexFilter::isWord;
	}
}
