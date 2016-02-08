package util;

import java.io.File;
import java.io.FileFilter;
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
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

public class JsonLayer {
	private Queue<File> processedFiles;
	public JsonLayer(Path processedFileDir) {
		this(processedFileDir, new FileFilter() {
			@Override
			public boolean accept(File f) {
				return true; 
			}});
	}
	public JsonLayer(Path processedFileDir, String matchRegex) {
		this(processedFileDir, new FileFilter() {
			@Override
			public boolean accept(File f) {
				return Pattern.matches(matchRegex, f.getName()); 
			}});
	}
	private JsonLayer(Path processedFileDir, FileFilter f) {
		try {
			processedFiles = new LinkedList<File>(Arrays.asList(processedFileDir.toFile().listFiles(f)));
		}
		catch (NullPointerException npe) {
			System.err.println("Folder at path: "+processedFileDir.toString()+ " was not found!");
			System.exit(1);
		}
		catch (Exception e) {
			System.err.println("Error in reading: "+processedFileDir.toString());
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
//	public static void undoDamage(Path inpDirectory) {
//		File[] listOfFiles = inpDirectory.toFile().listFiles();
//		for (File f : listOfFiles) {
//			undoFileDamage(f);
//		}
//	}
//	private static void undoFileDamage(File f) {
//		try {
//			List<String> lines = Files.readAllLines(f.toPath());
//			if (lines.isEmpty()) {
//				System.err.println(f.toPath()+ " is empty, can't process");
//				return;
//			}
//			FileWriter fw = new FileWriter(f.toPath().toString());
//			fw.write("["+System.getProperty("line.separator"));
//			for (int i = 0; i < lines.size(); i++) {
//				if (lines.get(i).contains("]") || lines.get(i).contains("[")) {
//					continue;
//				}
//				if (i == lines.size()-2) {
//					fw.write(lines.get(i).substring(0, lines.get(i).length()-1)+System.getProperty("line.separator"));
//				}
//				else {
//					fw.write(lines.get(i).substring(0, lines.get(i).length()-3)+"},"+System.getProperty("line.separator"));
//				}
//			}
//			fw.write("]");
//			fw.close();
//		} catch (IOException e) {
//			System.err.println("Error reading "+f.toPath());
//			System.err.println(e.getMessage());
//			return;
//		}
//	}
	private static void processFile(File f, String mod) {
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
			processFile(f, mod);
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
	public static void processAndSplit(Path inpFolder, Path outFolder) {
		Scanner s;
		Path writePath = null;
		FileWriter fr1;
		File[] files = inpFolder.toFile().listFiles();
		try {		
			for (File inp : files) {
				int fileNum = 0;
				String numAppend = String.format("%03d", fileNum);
				s = new Scanner(inp);
				
				writePath = outFolder.resolve(inp.getName()+numAppend+".mod");
				
				List<String> lines = new ArrayList<String>();
				while (s.hasNextLine()) {
					String line = s.nextLine();
					if (line.trim().isEmpty()) {
						continue;
					}
					lines.add(line);
					if (lines.size() >= 10000) {
						fr1 = new FileWriter(writePath.toFile());
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
						numAppend = String.format("%03d", fileNum);
						writePath = outFolder.resolve(inp.getName()+numAppend+".mod");
					}
				}
				//final write
				fr1 = new FileWriter(writePath.toFile());
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
		JsonArray ja = null;
		try {
			ja = Json.createReader(fr).readArray();
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
		}
		catch (javax.json.stream.JsonParsingException jpe) {
			System.err.println(f.toPath());
			System.err.println(jpe.getMessage());
			System.exit(1);
		}
	//	System.out.println("Transformed into Map finished");
		return allMessages;
	}
	public List<JsonReadable> getReadableByName(String f) {
		File r = null;
		for (File s : processedFiles) {
			if (s.getName().contains(f)) {
				r = s;
				break;
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
