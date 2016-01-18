package util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
			jr = Json.createReader(new FileReader(p.toFile()));
		}
		catch (FileNotFoundException fe) {
			System.err.println("No readable file at path: "+p.toString()+" was found");
			System.exit(1);
		} 
	}
	public static void preProcess(Path inp, Path outFolder) {
		Scanner s;
		Path toRet = null;
		FileWriter fr1;
		try {
			int fileNum = 0;
			String numAppend = String.format("%03d", fileNum);
			s = new Scanner(inp.toFile());
			toRet = outFolder.resolve(inp.getFileName()+"-mod"+numAppend);
			
			List<String> lines = new ArrayList<String>();
			while (s.hasNextLine()) {
				String line = s.nextLine();
				if (line.trim().isEmpty()) {
					continue;
				}
				lines.add(line);
				if (lines.size() >= 10000) {
					fr1 = new FileWriter(toRet.toFile());
					fr1.write("["+System.getProperty("line.separator"));
					for (int i = 0; i < lines.size(); i++) {
						if (i == lines.size() -1) {
							fr1.write(lines.get(i)+System.getProperty("line.separator"));
						}
						else {
							fr1.write(lines.get(i)+","+System.getProperty("line.separator"));
						}
					}
					fr1.write("]");
					fr1.close();
					lines.clear();
					fileNum++;
					numAppend = String.format("%2d", fileNum);
					toRet = outFolder.resolve(inp.getFileName()+"-mod"+numAppend);
				}
			}
			//final write
			fr1 = new FileWriter(toRet.toFile());
			fr1.write("["+System.getProperty("line.separator"));
			for (int i = 0; i < lines.size(); i++) {
				if (i == lines.size() -1) {
					fr1.write(lines.get(i)+System.getProperty("line.separator"));
				}
				else {
					fr1.write(lines.get(i)+","+System.getProperty("line.separator"));
				}
			}
			fr1.write("]");
			fr1.close();
			s.close();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
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
