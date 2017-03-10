package instances;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.collections.DoubleKeyMap;
import util.sys.DataType;

public class AggInstanceInfo implements DataType {
	
	private final Map<String, InstanceInfo> wordOriginationEvents;
	private final DoubleKeyMap<String, String, InstanceInfo> subAdoptionEvents;
	private final Map<String, List<InstanceInfo>> allEvents;
	public AggInstanceInfo() {
		this.wordOriginationEvents = new HashMap<String, InstanceInfo>();
		this.subAdoptionEvents = new DoubleKeyMap<String, String, InstanceInfo>();
		this.allEvents = new HashMap<String, List<InstanceInfo>>();
	}
	public AggInstanceInfo(AggInstanceInfo other) {
		this();
		merge(other);
	}
	
	public void addEvent(String word, InstanceInfo instance) {
		if (!wordOriginationEvents.containsKey(word) || wordOriginationEvents.get(word).compareTo(instance) > 0) {
			wordOriginationEvents.put(word, instance);
		}
		if (!subAdoptionEvents.containsKey(word, instance.getSubreddit()) || subAdoptionEvents.get(word, instance.getSubreddit()).compareTo(instance) > 0) {
			subAdoptionEvents.put(word, instance.getSubreddit(), instance);
		}
		if (!allEvents.containsKey(word)) {
			allEvents.put(word, new ArrayList<InstanceInfo>());
		}
		allEvents.get(word).add(instance);
	}
	public void merge(AggInstanceInfo other) {
		for (String word : other.allEvents.keySet()) {
			for (InstanceInfo i : other.allEvents.get(word)) {
				this.addEvent(word, i);
			}
			Collections.sort(allEvents.get(word));
		}
	}
	//after running this, we know the useNumber, and the InstanceInfo already provide sthe userID and the useInstant
	public void sortAll() {
		for (String key : allEvents.keySet()) {
			Collections.sort(allEvents.get(key));
		}
	}
	
	@Override
	public String getHeaderLine() {
		return "origination_time,origination_subreddit,origination_user,adoption_time,adoption_subreddit,adoption_user,adoption_number,usage_time,usage_subreddit,usage_user,usage_number";
	}
	
	//assume sorted
	public List<String> oneWordToString(String word) {
		List<InstanceInfo> allInstances = allEvents.get(word);
		List<String> retval = new ArrayList<String>();
		String originationPart = wordOriginationEvents.get(word).toString()+",";
		String subredditPart = "";
		Set<String> touchedSubs = new HashSet<String>();
		int adopterNumber = -1;
		int eventNumber = 0;
		for (InstanceInfo ii : allInstances) {
			if (!touchedSubs.contains(ii.getSubreddit())) {
				adopterNumber++;
				touchedSubs.add(ii.getSubreddit());
			}
			subredditPart = subAdoptionEvents.get(word, ii.getSubreddit()).toString() + "," + adopterNumber + ",";
			retval.add(originationPart+subredditPart+ii.toString()+","+eventNumber);
			eventNumber++;
		}
		return retval;
	}

	@Override
	public int getNumFixedArgs() {
		return 0;
	}

	@Override
	public boolean hasNArgs() {
		return false;
	}

	@Override
	public String getConstructionErrorMsg() {
		return "needs no further arguments";
	}

	@Override
	public Iterator<String> getStringIter() {
		Iterator<String> outerIterator = this.allEvents.keySet().iterator();
		return new Iterator<String>() {
			private Iterator<String> innerIterator = outerIterator.hasNext() ? oneWordToString(outerIterator.next()).iterator() : null;
			@Override
			public boolean hasNext() {
				if (innerIterator == null) {
					return false;
				}
				return innerIterator.hasNext() || outerIterator.hasNext();
			}

			@Override
			public String next() {
				if (!innerIterator.hasNext()) {
					//we know then that outerIterator does have next!
					innerIterator = oneWordToString(outerIterator.next()).iterator(); //to get added in the first place, must have one event.. so... shouldn't be empty
				}
				return innerIterator.next();
			}
		
		};
	}

	@Override
	public DataType deepCopy() {
		return new AggInstanceInfo(this);
	}
	
	//with just the word we can learn:
	//              originating subreddit
	//              word_id
	//              origination date
	
	//with the word and the subreddit we can learn
	//              subreddit id
	//              subreddit adoption instant
	//              adopter number
	
	//we want to add to that...
	//             useNumber
	//             useInstant
	//             userIDls
	
}
