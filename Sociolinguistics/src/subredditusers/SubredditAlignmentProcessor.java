package subredditusers;

import java.io.File;

import util.csv.ConfusionCSV;
import util.listdata.UserList;
import util.sys.FileProcessor;

public class SubredditAlignmentProcessor extends FileProcessor<UserList, ConfusionCSV<String>>{

	@Override
	public int getNumFixedArgs() {
		return 0;
	}

	@Override
	public boolean hasNArgs() {
		return false;
	}

	@Override
	public String getConstructionErrorMsg() {
		return "SubredditAlignmentProcessor requires no additional arguments";
	}

	@Override
	public UserList getNextData() {
		File f = super.getNextFile();
		if ( f == null) {
			return null;
		}
		return UserList.createFromFile(f);
	}

	@Override
	public void map(UserList newData, ConfusionCSV<String> threadAggregate) {
		for (String subreddit : newData.keySet()) {
			for (String othersubreddit : newData.keySet()) {
				for (String user : newData.get(subreddit)) {
					if (newData.get(othersubreddit).contains(user)) {
						//append to their confusion
						threadAggregate.put(subreddit, othersubreddit, threadAggregate.get(subreddit, othersubreddit)+1);
					}
				}
			}
		}
	}

	@Override
	public void reduce(ConfusionCSV<String> threadAggregate) {
		synchronized(processAggregate) {
			processAggregate.absorb(threadAggregate);
		}
		
	}

}
