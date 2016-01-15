package wordcounter;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.io.FileWriter;
import util.JsonLayer;
import util.WordMap;

public class CountMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JsonLayer jl = new JsonLayer(Paths.get(args[0]));
		List<Map<String, String>> jsons = jl.getReadable();
		WordMap counts = new WordMap();
		for (Map<String, String> json : jsons) {
			counts.addSentence(json.get("body"));
		}
		try {
			FileWriter fw = new FileWriter(args[1]);
			fw.write(counts.toString());
			fw.close();
		}
		catch (IOException ie) {
			ie.printStackTrace();
		}
	}

}
