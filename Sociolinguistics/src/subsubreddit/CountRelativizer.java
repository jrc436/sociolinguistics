package subsubreddit;

import java.io.File;
import java.nio.file.Paths;

import subredditanalysis.users.UserCountMap;
import util.sys.FileProcessor;

public class CountRelativizer extends FileProcessor<FlatKeyMap, RelativizedFlatKey> {
	
	public CountRelativizer() {
		super();
	}
	public CountRelativizer(String input, String output, String[] countsFile) {
		super(input, output, new RelativizedFlatKey(UserCountMap.fromFile(Paths.get(countsFile[0]).toFile())));
	}
	
	@Override
	public int getNumFixedArgs() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasNArgs() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getConstructionErrorMsg() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FlatKeyMap getNextData() {
		File f = super.getNextFile();
		if (f == null) {
			return null;
		}
		return FlatKeyMap.fromFile(f);
	}

	@Override
	public void map(FlatKeyMap newData, RelativizedFlatKey threadAggregate) {
		threadAggregate.absorb(newData);
	}

	@Override
	public void reduce(RelativizedFlatKey threadAggregate) {
		synchronized(super.processAggregate) {
			processAggregate.absorb(threadAggregate);
		}
	}

}
