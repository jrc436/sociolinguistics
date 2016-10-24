package subredditanalysis.filter;

import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import util.json.JsonLayer;
import util.json.JsonList;
import util.json.JsonReadable;
import util.listdata.UserList;

public class JsonFilter extends JsonLayer<JsonList> {
	private final Set<String> acceptedSubreddits;
	public JsonFilter() {
		super();
		acceptedSubreddits = new HashSet<String>();
	}
	public JsonFilter(String inpDir, String outDir, String[] commentFormat) {
		super(inpDir, outDir, new JsonList(), commentFormat[0]);
		UserList ul = UserList.createFromFile(Paths.get(commentFormat[1]).toFile());
		acceptedSubreddits = new HashSet<String>(ul.keySet());
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
