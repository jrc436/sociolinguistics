package armedconflicts;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import util.CSVReader;
import util.JsonLayer;
import util.KeywordOrganizer;
import util.KeywordOrganizingWorker;
import util.StringCleaner;
import wordmap.WordMap;


public class CommentOrganizer {
	public static void main(String[] args) {
		//args[0] will be the input directory
		//args[1] will be the output directory
		//args[2] will be the path to the csvfile
		//args[3] will be the type of csvfile
		List<KeywordOrganizer> keywords = null;
		switch (CsvType.fromString(args[3])) {
			case conflicts:
				keywords = initializeConflicts(Paths.get(args[2]), Paths.get(args[1]));
				break;
			case simple:
				keywords = simpleInitialize(Paths.get(args[2]), Paths.get(args[1]));
				break;
			case wordmap:
				keywords = wordmapInitialize(Paths.get(args[2]), Paths.get(args[1]));
				break;
			default:
				System.err.println("This code should be unreachable!");
				break;	
		}
		JsonLayer jl = new JsonLayer(Paths.get(args[0]));
		ExecutorService es = Executors.newCachedThreadPool();
		for (int i = 0; i < 8; i++) {
			es.execute(new KeywordOrganizingWorker(i, jl, new ArrayList<KeywordOrganizer>(keywords)));
		}
		es.shutdown();
	}
	private enum CsvType {
		simple, //just a list of keywords
		wordmap,
		conflicts; //see the google doc with ying
		public static CsvType fromString(String inp) {
			String valueString = "";
			for (CsvType csv : CsvType.values()) {
				valueString+=simple+";";
				if (csv.toString().equals(inp)) {
					return csv;
				}
			}	
			System.err.println(inp+" is not a valid type of Csv.");
			System.err.println("Valid Types: "+valueString);
			System.exit(1);
			
			return null;
		}
	}
	private static List<KeywordOrganizer> wordmapInitialize(Path listfile, Path outDir) {
		try {
			List<String> lines = Files.readAllLines(listfile);
			String header = lines.remove(0);
			WordMap wm = new WordMap(header);
			for (String s : lines) {
				wm.addFromString(s);
			}
			return simpleHelp(wm.keySet(), outDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	private static List<KeywordOrganizer> simpleHelp(Collection<String> lines, Path outDir) throws IOException {
		List<KeywordOrganizer> keywords = new ArrayList<KeywordOrganizer>();
		for (String line : lines) {
			keywords.add(new KeywordOrganizer(outDir, StringCleaner.sanitizeForFiles(line+".txt"), line));
		}
		return keywords;
	}
	private static List<KeywordOrganizer> simpleInitialize(Path listFile, Path outDir) {
		try {
			return simpleHelp(Files.readAllLines(listFile), outDir);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("bad keyword list file path: "+listFile);
			System.exit(1);
		}
		return null;
	}
	private static List<KeywordOrganizer> initializeConflicts(Path csvFile, Path outDir) {
		CSVReader csv = new CSVReader(csvFile, '$');
		List<KeywordOrganizer> conflicts = new ArrayList<KeywordOrganizer>();
		String[] conflictNames = csv.getColumnByTitle("Conflict/Country Name");
		String[] keywords = csv.getColumnByTitle("Alias/Keywords");
		for (int i = 0; i < conflictNames.length; i++) {
			try {
				conflicts.add(new KeywordOrganizer(outDir, StringCleaner.sanitizeForFiles(conflictNames[i])+".txt", keywords[i].split(",")));
			} catch (IOException e) {
				System.err.println("Conflict: "+conflictNames[i]+" failed to initialize");
				e.printStackTrace();
			}
		}
		return conflicts;
	}
	
//	@SuppressWarnings("unused")
//	private static boolean test(String inpFilePath) {
//		JsonLayer jl = new JsonLayer(Paths.get(inpFilePath));
//		List<JsonReadable> comments = jl.getNextReadable();
//		String printVal1 = JsonLayer.collectJsons(comments);
//		
//		JsonReader reader1 = Json.createReader(new ByteArrayInputStream(printVal1.getBytes(StandardCharsets.UTF_8)));
//		JsonArray array1 = reader1.readArray();
//		
//		List<JsonReadable> comments2 = new ArrayList<JsonReadable>();
//		Iterator<JsonValue> it = array1.iterator();
//		while (it.hasNext()) {
//			JsonReadable message = new JsonReadable();
//			JsonValue jv = it.next();
//			try {
//				JsonObject jo = (JsonObject) jv;
//				for (String key : jo.keySet()) {
//					message.put(key, jo.get(key).toString());
//				}
//				comments2.add(message);
//			}
//			catch (Exception e) {
//				System.err.println(e);
//				System.err.println(jv.getClass());
//				System.exit(1);
//			}
//		}
//		String printVal2 = JsonLayer.collectJsons(comments2);
//		return printVal1.equals(printVal2);
//	}
}
