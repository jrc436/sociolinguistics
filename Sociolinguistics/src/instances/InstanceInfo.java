package instances;

import java.time.Instant;

public class InstanceInfo implements Comparable<InstanceInfo> {

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
	public String getSubreddit() {
		return subreddit;
	}
	@Override
	public int compareTo(InstanceInfo arg0) {
		return this.time.compareTo(arg0.time);
	}

}
