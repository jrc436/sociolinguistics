package keywords;

import util.data.json.JsonList;
import util.data.maps.KeywordList;
import util.sys.Executor;


public class KeywordOrganizerMain extends Executor<KeywordMatchProcessor, JsonList, KeywordList> {
	private static final String name = "keywords";
	private static final int gbPerThread = 40;
	public KeywordOrganizerMain() {
		super(name, gbPerThread, KeywordMatchProcessor.class, JsonList.class, KeywordList.class);
	}

	public static void main(String args[]) {
		KeywordOrganizerMain fm = new KeywordOrganizerMain();
		fm.initializeFromCmdLine(args);
		fm.run();
	}
}
