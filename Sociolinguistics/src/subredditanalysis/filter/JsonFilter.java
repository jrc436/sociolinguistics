package subredditanalysis.filter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import util.data.json.JsonLayer;
import util.data.json.JsonList;
import util.data.json.JsonReadable;
import util.sys.LineProcessor;

public class JsonFilter extends LineProcessor<JsonList, JsonList> {
	private final Set<String> acceptedSubreddits;
	public JsonFilter() {
		super();
		acceptedSubreddits = new HashSet<String>();
	}
	public JsonFilter(String inpDir, String outDir, String[] userlistPath) {
		super(inpDir, outDir, new JsonList());
		Set<String> subs = null;
		try {
			subs = new HashSet<String>(Files.readAllLines(Paths.get(userlistPath[0])));
		} catch (IOException e) {
			System.err.println("Error reading subreddit list from file: "+userlistPath[0]);
			e.printStackTrace();
			System.exit(1);
		}
		acceptedSubreddits = subs;
	}
	//needs the comment format and a path to the userlist...
	@Override
	public int getNumFixedArgs() {
		return 2;
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
	public boolean hasNArgs() {
		return false;
	}
	@Override
	public String getConstructionErrorMsg() {
		return "Please specify the path to the UserList";
	}
	@Override
	public JsonList getNextData() {
		File f = super.getNextFile();
		if ( f == null) {
			return null;
		}
		return JsonLayer.getReadable(f);	
	}

}
