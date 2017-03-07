package subredditanalysis.comments;

import subredditanalysis.users.UserCountMap;
import util.data.json.JsonLayer;
import util.data.json.JsonList;
import util.data.json.JsonReadable;

public class CommentCounter extends JsonLayer<UserCountMap> {

	public CommentCounter() {
		super();
	}
	
	public CommentCounter(String input, String output) {
		super(input, output, new UserCountMap(), "reddit");
	}
	@Override
	public int getNumFixedArgs() {
		return 0;
	}
	
	@Override
	public void map(JsonList newData, UserCountMap threadAggregate) {
		for (JsonReadable jr : newData) {
			String sub = jr.get("subreddit");
			if (threadAggregate.containsKey(sub)) {
				threadAggregate.put(sub, threadAggregate.get(sub)+1);
			}
			else {
				threadAggregate.put(sub, 1);
			}
		}
	}

	@Override
	public void reduce(UserCountMap threadAggregate) {
		synchronized(super.processAggregate) {
			processAggregate.absorb(threadAggregate);
		}
		
	}

}
