package subsubreddit;

import java.io.File;

import util.data.dsv.UserConfusionCSV;
import util.sys.FileProcessor;

public class SubsetProcessor extends FileProcessor<UserConfusionCSV, SubCollection> {
	
	public SubsetProcessor() {
		super();
	}
	public SubsetProcessor(String inpDir, String outDir) {
		super(inpDir, outDir, new SubCollection());
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserConfusionCSV getNextData() {
		File f = super.getNextFile();
		if (f == null) {
			return null;
		}
		return UserConfusionCSV.fromFile(f);
	}

	@Override
	public void map(UserConfusionCSV newData, SubCollection threadAggregate) {
		for (String subreddit : newData.getKeysetOne()) {
			Integer totUsers = newData.get(subreddit, subreddit);
			threadAggregate.inputSubreddit(subreddit, totUsers);
			double totalUsers = (double) totUsers;
			for (String otherSub : newData.getFullKeySet()) {
				if (otherSub.equals(subreddit)) {
					continue;
				}
				double sharedUsers = (double) newData.get(subreddit, otherSub);
				if (sharedUsers / totalUsers > 0.25) {
					threadAggregate.add(subreddit, otherSub);
				}
			}
		}
	}

	@Override
	public void reduce(SubCollection threadAggregate) {
		synchronized(processAggregate) {
			processAggregate.absorbSubs(threadAggregate);
			for (String key : threadAggregate.keySet()) {
				if (processAggregate.containsKey(key)) {
					processAggregate.get(key).addAll(threadAggregate.get(key));
				}
				else {
					processAggregate.put(key, threadAggregate.getCopyCollection(key));
				}
			}
//			for (String sup : processAggregate.keySet()) {
//				Set<String> removes = new HashSet<String>();
//				for (String sub : processAggregate.get(sup)) {
//					if (processAggregate.invalidSubset(sup, sub)) {
//						removes.add(sub);
//						System.err.println(sup+" has fewer members than "+sub);
//					}
//				}
//				processAggregate.get(sup).removeAll(removes);
//			}
		}
	}

}
