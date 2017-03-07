package subredditanalysis.users;

import java.io.File;

import util.data.maps.UserList;
import util.sys.LineProcessor;

public class CountProcessor extends LineProcessor<UserList, UserCountMap> {
	
	public CountProcessor() {
		super();
	}
	public CountProcessor(String inputDir, String outputDir) {
		super(inputDir, outputDir, new UserCountMap());
	}
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
		return "needs no args";
	}

	@Override
	public UserList getNextData() {
		File f = super.getNextFile();
		if (f == null) {
			return null;
		}
		return UserList.createFromFile(f);
	}

	@Override
	public void map(UserList newData, UserCountMap threadAggregate) {
		for (String key : newData.keySet()) {
			threadAggregate.put(key, newData.get(key).size());
		}
	}

}
