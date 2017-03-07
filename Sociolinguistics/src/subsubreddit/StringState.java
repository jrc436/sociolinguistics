package subsubreddit;

public class StringState {
	private final String string;
	private final boolean isSub;
	public StringState(String dat, boolean isSub) {
		this.string = dat;
		this.isSub = isSub;
	}
	public String toString() {
		return string;
	}
	public boolean isSub() {
		return isSub;
	}
	public int hashCode() {
		int hash = string.hashCode();
		int multiplier = isSub ? 53 : 13;
		return hash * multiplier;
	}
	public boolean equals(Object other) {
		if (other instanceof StringState) {
			StringState oth = (StringState) other;
			return oth.string.equals(this.string) && oth.isSub == this.isSub;
		}
		return false;
	}
}
