package instances;

import java.time.Instant;

public class InstanceInfo  {

	private final Instant time;
	private final String subreddit;
	private final String user;
	public InstanceInfo(Instant time, String subreddit, String user) {
		this.time = time;
		this.subreddit = subreddit;
		this.user = user;
	}
	public String toString() {
		return time.toString()+","+subreddit+","+user;
	}
//	@Override
//	public int getNumFixedArgs() {
//		return 0;
//	}
//
//	@Override
//	public boolean hasNArgs() {
//		return false;
//	}
//
//	@Override
//	public String getConstructionErrorMsg() {
//		return "needs no args";
//	}
//
//	@Override
//	public Iterator<String> getStringIter() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public DataType deepCopy() {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
