package wordcounter;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import util.JsonLayer;
import util.WordMap;

public class CountMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JsonLayer jl = new JsonLayer(Paths.get("/work/research/sociolinguistics/WordPropagation/jsontest"));
		List<Map<String, String>> jsons = jl.getReadable();
		WordMap counts = new WordMap();
		for (Map<String, String> json : jsons) {
			counts.addSentence(json.get("body"));
		}
		System.out.println(counts.toString());
	}

}
