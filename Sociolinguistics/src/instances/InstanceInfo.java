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
	public long computeDifference(InstanceInfo other) {
		return this.time.getEpochSecond() - other.time.getEpochSecond();
	}
	public static InstanceInfo fromString(String s) {
		String[] parts = s.split(",");
		if (parts.length != 3) {
			throw new IllegalArgumentException("String: "+s+" does not encode an InstanceInfo");
		}
		Instant t = Instant.parse(parts[0]);
		return new InstanceInfo(t, parts[1], parts[2]);
	}

}
