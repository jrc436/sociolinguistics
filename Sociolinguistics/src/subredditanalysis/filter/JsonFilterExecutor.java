package subredditanalysis.filter;

import util.data.json.JsonList;
import util.sys.Executor;

public class JsonFilterExecutor extends Executor<JsonFilter, JsonList, JsonList> {

	public JsonFilterExecutor() {
		super("jsonfilter", 20, JsonFilter.class, JsonList.class, JsonList.class);
	}
	
	public static void main(String[] args) {
		JsonFilterExecutor jfe = new JsonFilterExecutor();
		jfe.initializeFromCmdLine(args);
		jfe.run();
	}

}
