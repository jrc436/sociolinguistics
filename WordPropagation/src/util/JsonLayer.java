package util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

public class JsonLayer {
	private JsonReader jr;
	public JsonLayer(Path p) {
		try {
			jr = Json.createReader(new FileReader(preProcess(p).toFile()));
		}
		catch (FileNotFoundException fe) {
			System.err.println("No readable file at path: "+p.toString()+" was found");
			System.exit(1);
		} 
	}
	private static Path preProcess(Path p) {
		Scanner s;
		Path toRet = null;
		FileWriter fr1;
		try {
			s = new Scanner(p.toFile());
			toRet = p.resolve("-mod");
			fr1 = new FileWriter(toRet.toFile());
			fr1.write("[");
			while (s.hasNextLine()) {
				fr1.write(s.nextLine()+","+System.getProperty("line.separator"));
			}
			fr1.write("]");
			s.close();
			fr1.close();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return toRet;
	}
	public List<Map<String, String>> getReadable() {
		List<Map<String, String>> allMessages = new ArrayList<Map<String, String>>();
		JsonArray ja = jr.readArray();
		 
		Iterator<JsonValue> it = ja.iterator();
		while (it.hasNext()) {
			Map<String, String> message = new HashMap<String, String>();
			JsonValue jv = it.next();
			try {
				JsonObject jo = (JsonObject) jv;
				for (String key : jo.keySet()) {
					message.put(key, jo.get(key).toString());
				}
				allMessages.add(message);
			}
			catch (Exception e) {
				System.err.println(e);
				System.err.println(jv.getClass());
				System.exit(1);
			}
		}
		return allMessages;
	}
}