package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

public class JsonLayer {
	private Queue<File> processedFiles;
	public JsonLayer(Path processedFileDir) {
		try {
			processedFiles = new LinkedList<File>(Arrays.asList(processedFileDir.toFile().listFiles()));
		}
		catch (NullPointerException npe) {
			System.err.println("Folder at path: "+processedFileDir.toString()+ " was not found!");
		}
		catch (Exception e) {
			System.err.println("Error in reading: "+processedFileDir.toString());
			System.err.println(e.getMessage());
		}
	}
	public static void processIfNeeded(File f, String mod) {
		try {
			List<String> lines = Files.readAllLines(f.toPath());
			if (lines.isEmpty()) {
				System.err.println(f.toPath()+ " is empty, can't process");
				return;
			}
			FileWriter fw = new FileWriter(f.toPath().toString()+mod);
			fw.write("["+System.getProperty("line.separator"));
			for (int i = 0; i < lines.size(); i++) {
				if (i == lines.size()-1) {
					fw.write(lines.get(i)+System.getProperty("line.separator"));
				}
				else {
					fw.write(lines.get(i)+","+System.getProperty("line.separator"));
				}
			}
			fw.write("]");
			fw.close();
		} catch (IOException e) {
			System.err.println("Error reading "+f.toPath());
			System.err.println(e.getMessage());
			return;
		}
	}	
	/**
	 * small files
	 */
	public static void processInPlace(Path inpDirectory, String mod) {
		File[] listOfFiles = inpDirectory.toFile().listFiles();
		for (File f : listOfFiles) {
			processIfNeeded(f);
		}
	}
	public static String collectJsons(List<JsonReadable> transformation) {
		String ret = "["+System.getProperty("line.separator");
		for (int i = 0; i < transformation.size(); i++) {
			if (i == transformation.size() -1) {
				ret += transformation.get(i).toString()+System.getProperty("line.separator");
			}
			else {
				ret += transformation.get(i).toString()+","+System.getProperty("line.separator");
			}
		}
		ret += "]";
		return ret;
	}
	/**
	 * big files
	 * @param inp
	 * @param outFolder
	 */
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
	public int numReadableRemaining() {
		return processedFiles.size();
	}
	private List<JsonReadable> getReadable(File f) {
		List<JsonReadable> allMessages = new ArrayList<JsonReadable>();
		FileReader fr = null;
		try {
			fr = new FileReader(f);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.err.println("Skipping file that's supposed to be at: "+f.getAbsolutePath());
			return allMessages;
		}
		JsonArray ja = Json.createReader(fr).readArray();
		//System.out.println("Reading JsonArray finished");
		Iterator<JsonValue> it = ja.iterator();
		while (it.hasNext()) {
			JsonReadable message = new JsonReadable();
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
	//	System.out.println("Transformed into Map finished");
		return allMessages;
	}
	public List<JsonReadable> getReadableByName(String f) {
		File r = null;
		synchronized (this) {
			for (File s : processedFiles) {
				if (s.getName().equals(f)) {
					r = s;
					break;
				}
			}		
		}
		return r == null ? null : getReadable(r);
	}
	public List<JsonReadable> getNextReadable() {	
		File f = null;
		synchronized(this) {
			if (processedFiles.peek() == null) {
				return null;
			}
			f = processedFiles.poll();
		}
		return getReadable(f);	
	}
}
