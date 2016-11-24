package subredditanalysis.users;

import java.io.File;

import util.data.dsv.UserConfusionCSV;
import util.data.maps.UserList;
import util.sys.FileProcessor;

public class SubredditAlignmentProcessor extends FileProcessor<UserList, UserConfusionCSV>{
	public SubredditAlignmentProcessor(String inpDir, String outDir) {
		super(inpDir, outDir, new UserConfusionCSV(true));
	}
	public SubredditAlignmentProcessor() {
		super();
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
	public void map(UserList newData, UserConfusionCSV threadAggregate) {
		threadAggregate.uploadUserList(newData);
		threadAggregate.computeConfusion();
	}

	@Override
	public void reduce(UserConfusionCSV threadAggregate) {
		synchronized(processAggregate) {
			processAggregate.absorb(threadAggregate);
			processAggregate.computeConfusion();
		}
		
	}

}
