package subsubreddit;

import subredditanalysis.users.UserCountMap;
import util.collections.Pair;
import util.sys.DataType;

public class RelativizedFlatKey extends FlatKeyMap {

	private static final long serialVersionUID = 4904347527891962541L;
	private final UserCountMap ucm;
	public RelativizedFlatKey(UserCountMap ucm) {
		this.ucm = ucm;
	}
	public RelativizedFlatKey() {
		this.ucm = null;
	}
	public RelativizedFlatKey(RelativizedFlatKey other) {
		super(other);
		this.ucm = other.ucm;
	}
	
	@Override
	public int getNumFixedArgs() {
		return 1;
	}
	@Override
	protected String entryString(Pair<StringState, StringState> ent) {
		StringState first = ent.one();
		StringState second = ent.two();
		String val = "";
		boolean filt = false;
		if (this.isSymmetric()) {
			throw new RuntimeException("Relativized doesn't support symmetry");
		}
		else {
			double firstVal = this.get(first, second);
			double secondVal = this.containsKey(second, first) ? this.get(second, first) : 0;
			if ((first.isSub() && second.isSub()) || (!first.isSub() && !second.isSub())) {
				throw new RuntimeException("Pair is double sub");
			}
			firstVal /= ucm.get(first.toString());
			secondVal /= ucm.get(second.toString());
			if (first.isSub()) {
				val = firstVal + "," + secondVal;
			}
			else {
				val = secondVal + "," + firstVal;
				StringState tmp = second;
				second = first;
				first = tmp;
			}
			if (firstVal == 0 && secondVal == 0) {
				filt = true;
			}
		}
		if (filt) {
			return "";
		}
		return first + "," + val + "," + second;
	}
	@Override
	public DataType deepCopy() {
		return new RelativizedFlatKey(this);
	}
	
}
