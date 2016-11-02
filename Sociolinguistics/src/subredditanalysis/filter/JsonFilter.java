package subredditanalysis.filter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import util.data.json.JsonLayer;
import util.data.json.JsonList;
import util.data.json.JsonReadable;

public class JsonFilter extends JsonLayer<JsonList> {
	private final Set<String> acceptedSubreddits;
	public JsonFilter() {
		super();
		acceptedSubreddits = new HashSet<String>();
	}
	public JsonFilter(String inpDir, String outDir, String[] commentFormat) {
		super(inpDir, outDir, new JsonList(), commentFormat[0]);
		Set<String> subs = null;
		try {
			subs = new HashSet<String>(Files.readAllLines(Paths.get(commentFormat[1])));
		} catch (IOException e) {
			System.err.println("Error reading subreddit list from file: "+commentFormat[1]);
			e.printStackTrace();
			System.exit(1);
		}
		acceptedSubreddits = subs;
	}
	//needs the comment format and a path to the userlist...
	@Override
	public int getNumFixedArgs() {
		return 1 + super.getNumFixedArgs();
	}
	@Override
	public void map(JsonList newData, JsonList threadAggregate) {
		for (JsonReadable jr : newData) {
			if (acceptedSubreddits.contains(jr.get("subreddit"))) {
				threadAggregate.add(jr);
			}
		}
	}

	@Override
	public void reduce(JsonList threadAggregate) {
		synchronized(processAggregate) {
			processAggregate.addAll(threadAggregate);
		}
	}

}
