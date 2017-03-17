package activationcomputer;

import instances.InstanceInfo;

public class RedditEvent {
	private final InstanceInfo originationEvent;
	private final InstanceInfo subredditAdoptionEvent;
	private final InstanceInfo thisEvent;
	private final String word;
	private final int usageNumber;
	private final int adopterNumber; //just for mooj
	private long delay;
	public RedditEvent(String word, InstanceInfo origination, InstanceInfo adoption, InstanceInfo ev, int use, int adopt) {
		this(word, origination, adoption, ev, use, adopt, -99);
	}
	public RedditEvent(String word, InstanceInfo origination, InstanceInfo adoption, InstanceInfo ev, int use, int adopt, long delay) {
		this.originationEvent = origination;
		this.subredditAdoptionEvent = adoption;
		this.thisEvent = ev;
		this.word = word;
		this.usageNumber = use;
		this.adopterNumber = adopt;
		this.delay = delay;
	}
	public RedditEvent(RedditEvent other) {
		this(other.word, other.originationEvent, other.subredditAdoptionEvent, other.thisEvent, other.usageNumber, other.adopterNumber, other.delay);
	}
	protected void setDelay(RedditEvent previous) {
		this.delay = computeDelay(previous);
	}
	
	public String getWord() {
		return word;
	}
	public int getAdopterNumber() {
		return adopterNumber;
	}
	private long computeDelay(RedditEvent previous) {
		return previous.thisEvent.computeDifference(this.thisEvent);
	}
	public long timeSinceOrigination() {
		return this.originationEvent.computeDifference(this.thisEvent);
	}

	public long getElapsedTime() {
		if (delay == -99) {
			throw new RuntimeException("Delay is not set.");
		}
		return delay;
	}
	public String toString() {
		String longString = delay == -99 ? "" : ","+delay;
		return originationEvent+","+subredditAdoptionEvent+","+thisEvent+","+word+","+usageNumber+","+adopterNumber+longString;
	}
	public static RedditEvent fromString(String s) {
		String[] parts = s.split(",");
		if (parts.length != 12 && parts.length != 13) {
			throw new IllegalArgumentException("String doesn't encode a revent");
		}
		InstanceInfo event1 = InstanceInfo.fromString(parts[0] + "," + parts[1] + "," + parts[2]);
		InstanceInfo event2 = InstanceInfo.fromString(parts[3] + "," + parts[4] + "," + parts[5]);
		InstanceInfo event3 = InstanceInfo.fromString(parts[6] + "," + parts[7] + "," + parts[8]);
		String word = parts[9];
		int use = Integer.parseInt(parts[10]);
		int adopt = Integer.parseInt(parts[11]);
		long delay = parts.length == 13 ? Long.parseLong(parts[12]) : 0;
		return new RedditEvent(word, event1, event2, event3, use, adopt, delay);
	}
}
