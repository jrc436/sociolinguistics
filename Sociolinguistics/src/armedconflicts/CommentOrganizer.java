package armedconflicts;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import util.CSVReader;
import util.JsonLayer;
import util.JsonReadable;
import util.StringCleaner;


class CommentWriteThread implements Runnable {
	private final JsonLayer jl;
	private final int threadNum;
	private final List<Conflict> copyConflicts;
	public CommentWriteThread(int threadNum, JsonLayer jl, List<Conflict> copyOfConflicts) {
		this.jl = jl;
		copyConflicts = new ArrayList<Conflict>(copyOfConflicts);
		this.threadNum = threadNum;
	}
	@Override
	public void run() {
		List<JsonReadable> comments = jl.getNextReadable();
		while (comments != null) {
			System.out.println("Thread"+threadNum+" has acquired another set of comments");
			System.out.println("There are "+jl.numReadableRemaining()+" files remaining");
			for (JsonReadable comment : comments) {
				for (Conflict c : copyConflicts) {
					if (c.relevant(comment.get("body"))) {
						c.writeComment(comment.toString());
					}
				}
			}
			comments = jl.getNextReadable();
		}
		System.out.println("Thread"+threadNum+ " will close now, as there are no more files to process");
	}
}
public class CommentOrganizer {
	public static void main(String[] args) {
		//args[0] will be the input directory
		//args[1] will be the output directory
		//args[2] will be the path to the csvfile
		
		List<Conflict> conflicts = initializeConflicts(Paths.get(args[2]), Paths.get(args[1]));
		JsonLayer jl = new JsonLayer(Paths.get(args[0]));
		ExecutorService es = Executors.newCachedThreadPool();
		for (int i = 0; i < 8; i++) {
			es.execute(new CommentWriteThread(i, jl, new ArrayList<Conflict>(conflicts)));
		}
		es.shutdown();
	}
	private static List<Conflict> initializeConflicts(Path csvFile, Path outDir) {
		CSVReader csv = new CSVReader(csvFile, '$');
		List<Conflict> conflicts = new ArrayList<Conflict>();
		String[] conflictNames = csv.getVectorByTitle("Conflict/Country Name");
		String[] keywords = csv.getVectorByTitle("Alias/Keywords");
		for (int i = 0; i < conflictNames.length; i++) {
			try {
				conflicts.add(new Conflict(outDir, StringCleaner.sanitizeForFiles(conflictNames[i])+".txt", keywords[i].split(",")));
			} catch (IOException e) {
				System.err.println("Conflict: "+conflictNames[i]+" failed to initialize");
				e.printStackTrace();
			}
		}
		return conflicts;
	}
	
	@SuppressWarnings("unused")
	private static boolean test(String inpFilePath) {
		JsonLayer jl = new JsonLayer(Paths.get(inpFilePath));
		List<JsonReadable> comments = jl.getNextReadable();
		String printVal1 = JsonLayer.collectJsons(comments);
		
		JsonReader reader1 = Json.createReader(new ByteArrayInputStream(printVal1.getBytes(StandardCharsets.UTF_8)));
		JsonArray array1 = reader1.readArray();
		
		List<JsonReadable> comments2 = new ArrayList<JsonReadable>();
		Iterator<JsonValue> it = array1.iterator();
		while (it.hasNext()) {
			JsonReadable message = new JsonReadable();
			JsonValue jv = it.next();
			try {
				JsonObject jo = (JsonObject) jv;
				for (String key : jo.keySet()) {
					message.put(key, jo.get(key).toString());
				}
				comments2.add(message);
			}
			catch (Exception e) {
				System.err.println(e);
				System.err.println(jv.getClass());
				System.exit(1);
			}
		}
		String printVal2 = JsonLayer.collectJsons(comments2);
		return printVal1.equals(printVal2);
	}
}
