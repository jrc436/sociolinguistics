package armedconflicts;

import util.data.json.JsonKeyMap;
import util.data.json.JsonList;
import util.data.json.MergeData;
import util.sys.Executor;

public class ConflictMergeMain extends Executor<MergeData, JsonList, JsonKeyMap> {

	public ConflictMergeMain() {
		super("conflicts", 20, MergeData.class, JsonList.class, JsonKeyMap.class);
	}
	public static void main(String[] args) {
		ConflictMergeMain csv = new ConflictMergeMain();
		csv.initializeFromCmdLine(args);
		csv.run();
	}
}
