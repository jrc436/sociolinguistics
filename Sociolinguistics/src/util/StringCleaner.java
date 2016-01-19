package util;

public class StringCleaner {
	public static String sanitizeForFiles(String futureFilePath) {
		return futureFilePath.replace(" (", "-").replace(' ', '_').replace(")", "").replace('(', '-').replace('/', '-');
	}
	public static String cleanPhrase(String comment) {
		String[] words = comment.split(" ");
		String ret = "";
		for (String word : words) {
			String cleaned = cleanWord(word);
			if (!cleaned.isEmpty()) {
				ret += cleanWord(word) + " ";
			}
		}
		return ret.isEmpty() ? "" : ret.substring(0, ret.length()-1);
	}
	public static String cleanWord(String word) {
		return superTrim(word).toLowerCase();
	}
	private static String superTrim(String word) {
		word = word.trim();
		while (!word.isEmpty() && illegalCharacterCheck(word.charAt(0))) {
			word = word.substring(1);
		}
		while (!word.isEmpty() && illegalCharacterCheck(word.charAt(word.length()-1))) {
			word = word.substring(0, word.length()-1);
		}
		return word;
	}
	private static boolean illegalCharacterCheck(char c) {
		return (c == '"' || c == '\'' || c == '.' || c == ',' || c == '\\' || c == '/' || c == '?' || c == '!' || c == '~' || c == '#' || c=='(' || c==')');
	}
}
