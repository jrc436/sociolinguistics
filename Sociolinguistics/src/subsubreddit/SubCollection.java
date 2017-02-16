package subsubreddit;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import util.data.maps.DataCollection;
import util.sys.DataType;

public class SubCollection extends DataCollection<String> {

	private static final long serialVersionUID = -4608587073204169551L;
	private final Map<String, Integer> totalUsersMap = new HashMap<String, Integer>();
	
	public SubCollection() {
		super();
	}
	public SubCollection(SubCollection other) {
		super(other);
		totalUsersMap.putAll(other.totalUsersMap);
	}
	public SubCollection(List<String> lines) {
		super(lines);
	}
	@Override
	public DataType deepCopy() {
		return new SubCollection(this);
	}
	public void inputSubreddit(String sub, int totalUsers) {
		if (totalUsersMap.containsKey(sub)) {
			throw new IllegalArgumentException("Sub already has info");
		}
		totalUsersMap.put(sub, totalUsers);
	}
	public void absorbSubs(SubCollection other) {
		for (Entry<String, Integer> en : other.totalUsersMap.entrySet()) {
			this.inputSubreddit(en.getKey(), en.getValue());
		}
	}

	public boolean invalidSubset(String superset, String subset) {
		if (!totalUsersMap.containsKey(superset) || !totalUsersMap.containsKey(subset)) {
			return false;
		}
		return totalUsersMap.get(superset) < totalUsersMap.get(subset);
	}

	@Override
	protected Collection<String> getEmptyCollection() {
		return new HashSet<String>();
	}

}
