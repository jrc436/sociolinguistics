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
		return word + "," + originationEvent + "," + subredditAdoptionEvent+","+adopterNumber+","+thisEvent+","+usageNumber+longString;
	}
	public static RedditEvent fromString(String s) {
		String[] parts = s.split(",");
		if (parts.length != 12 && parts.length != 13) {
			throw new IllegalArgumentException("String: "+s+" doesn't encode a revent, has: "+parts.length+" parts");
		}
		String word = parts[0];
		InstanceInfo[] events = new InstanceInfo[3];
		int start = 1;
		int adopt = -1;
		for (int i = start; i < events.length; i+=3) {
			if (i == 7) { //getting fukin hacky
				adopt = Integer.parseInt(parts[7]);
				i++; //then 8,9,10
			}
			events[i] = InstanceInfo.fromString(parts[i] + "," + parts[i+1] + "," + parts[i+2]);
		}
		int use = Integer.parseInt(parts[11]);
		long delay = parts.length == 13 ? Long.parseLong(parts[12]) : 0;
		return new RedditEvent(word, events[0], events[1], events[2], use, adopt, delay);
	}
}
