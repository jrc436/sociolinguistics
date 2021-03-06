package instances;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class InstanceInfo implements Comparable<InstanceInfo> {

	private final Instant time;
	private final String subreddit;
	private final String user;
	public InstanceInfo(Instant time, String user, String subreddit) {
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
	public Instant getTime() {
		return time;
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
		Instant t = null;
		try {
			t = Instant.parse(parts[0]);
		}
		catch (DateTimeParseException de) {
			//long shot guess into moojan formatting?
			LocalDate ld = LocalDate.parse(parts[0].substring(1, parts[0].length()-1), DateTimeFormatter.ISO_LOCAL_DATE);
			t = ld.atStartOfDay().toInstant(ZoneOffset.UTC);
		}
		return new InstanceInfo(t, parts[1], parts[2]);
	}
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof InstanceInfo)) {
			return false;
		}
		InstanceInfo wii = (InstanceInfo) other;
		return wii.time.equals(this.time) && wii.subreddit.equals(this.subreddit) && wii.user.equals(this.user);
	}
	@Override
	public int hashCode() {
		return this.time.hashCode() + this.subreddit.hashCode() * 7 + this.user.hashCode() * 3;
	}

}
