package keywords;

import java.util.ArrayList;

import filter.StringCleaner;
import util.data.Comment;
import util.data.CommentFormat;
import util.json.JsonLayer;
import util.json.JsonList;
import util.json.JsonReadable;
import util.listdata.KeywordList;

public class KeywordMatchProcessor extends JsonLayer<KeywordList> {
	public KeywordMatchProcessor(String inpDir, String outDir, String[] wmArgs, String[] keywords) {
		super(inpDir, outDir, new KeywordList(keywords, CommentFormat.fromString(wmArgs[0])), wmArgs[0]);
	}
	public KeywordMatchProcessor() {
		super();
	}
	//map is building a wordmap from keywords to comments that match them
	@Override
	public void map(JsonList newData, KeywordList threadAggregate) {
		for (JsonReadable jr : newData) {
			Comment c = getAsComment(jr);
			String comment = StringCleaner.cleanPhrase(c.getText());
			for (String keyword : threadAggregate.getRelevantKeywords(comment)) {
				threadAggregate.add(keyword, c);
			}
		}
	}
	@Override
	public boolean hasNArgs() {
		return false;
	}
	@Override
	public int getNumFixedArgs() {
		return 1; //gets commentformat from the wm
	}
	@Override
	public String getConstructionErrorMsg() {
		return super.getConstructionErrorMsg()+"; The comment format should be specified.";
	}

	@Override
	public void reduce(KeywordList threadAggregate) {
		synchronized(processAggregate) {
			for (String key : threadAggregate.keySet()) {
				if (processAggregate.containsKey(key)) {
					processAggregate.get(key).addAll(threadAggregate.get(key));
				}
				else {
					processAggregate.put(key, new ArrayList<Comment>(threadAggregate.get(key)));
				}
			}
		}
	}

}
