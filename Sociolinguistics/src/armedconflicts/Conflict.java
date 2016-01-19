package armedconflicts;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import util.StringCleaner;

public class Conflict {
	private final Set<String> keywords;
	private final String name;
	private final FileWriter fw;
	public Conflict(Path outFolder, String name, String...keywords) throws IOException {
		for (int i = 0; i < keywords.length; i++) {
			keywords[i] = StringCleaner.cleanPhrase(keywords[i]);
		}
		this.keywords = new HashSet<String>(Arrays.asList(keywords));
		this.name = name;
		this.fw = new FileWriter(outFolder.resolve(name).toFile());
	}
	public boolean relevant(String commentText) {
		String comment = StringCleaner.cleanPhrase(commentText);
		for (String key : keywords) {
			if (comment.contains(key)) {
				return true;
			}
		}
		return false;
	}
	public void writeComment(String fullCommentJson) {
		try {
			fw.write(fullCommentJson + System.getProperty("line.separator"));
			fw.flush();
		} catch (IOException e) {
			System.err.println("Conflict: "+name+" failed to write");
			System.err.println(e.getMessage());
		}
	}
}
