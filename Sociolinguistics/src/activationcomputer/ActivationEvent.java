package activationcomputer;

public class ActivationEvent extends RedditEvent {
	private final double activation;
	private final String extra;
	public ActivationEvent(double activation, RedditEvent revent) {
		super(revent);
		this.activation = activation;
		this.extra = "";
	}
	public ActivationEvent(ActivationEvent e) {
		this(e, "");
	}
	public ActivationEvent(ActivationEvent e, String extra) {
		super(e);
		this.activation = e.activation;
		this.extra = extra;
	}
	
	public String toString() {
		return super.toString()+","+activation+extra;
	}
	public static ActivationEvent fromString(String s) {
		String[] parts = s.split(",");
		if (parts.length < 14) {
			throw new IllegalArgumentException("Doesn't encode an activation event");
		}
		String revent = "";
		for (int i = 0; i < 13; i++) {
			revent += parts[i] + ",";
		}
		RedditEvent rev = RedditEvent.fromString(revent.substring(0, revent.length()-1)); //trailing comma
		double activation = Double.parseDouble(parts[13]);
		ActivationEvent e = new ActivationEvent(activation, rev);
		String extra = "";
		for (int i = 14; i < parts.length; i++) {
			extra += "," + parts[i];
		}
		return new ActivationEvent(e, extra);
	}
}
