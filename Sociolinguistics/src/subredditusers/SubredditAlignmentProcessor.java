package subredditusers;

import java.io.File;

import util.csv.SConfusionCSV;
import util.listdata.UserList;
import util.sys.FileProcessor;

public class SubredditAlignmentProcessor extends FileProcessor<UserList, SConfusionCSV>{

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
	public void map(UserList newData, SConfusionCSV threadAggregate) {
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
	public void reduce(SConfusionCSV threadAggregate) {
		synchronized(processAggregate) {
			processAggregate.absorb(threadAggregate);
		}
		
	}

}
