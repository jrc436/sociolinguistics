package filter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import util.JsonLayer;
import util.JsonReadable;
import util.Logger;
import util.RedditComment;
import util.ResourceAllocator;
import wordmap.Combinable;
import wordmap.WordMap;

public class WordDataFinder {
	@SuppressWarnings("unchecked")
	protected static WordMap getNew(String[] classes) {
		Set<Class<? extends Combinable>> clses = new HashSet<Class<? extends Combinable>>();
		for (String cls : classes) {
			try {
				clses.add((Class<? extends Combinable>) Class.forName(cls));
			} catch (ClassNotFoundException e) {
				System.err.println("Class: "+cls+" not found or class doesn't implement Combinable");
				System.exit(1);
			}
		}
		return new WordMap(clses);
	}
	public static void main(String[] args) {
        String errMessage = "Required parameters: Input Folder (JSON) and Output File (Count-Format) and at least one type of Data (Combinable)";
        if (args.length <= 2) {
                System.out.println(errMessage);
                System.exit(1);
        }
        JsonLayer jl = new JsonLayer(Paths.get(args[0]));
        String[] classes = new String[args.length-2];
        for (int i = 2; i < args.length; i++) {
        	classes[i-2] = args[i];
        }
        WordMap counts = getNew(classes);
        BlockingQueue<String> messages = new LinkedBlockingQueue<String>();
		Thread log; 
		FileWriter fw = null;
		try {
			fw = new FileWriter(args[1]);
			log = new Thread(new Logger(messages, "log.txt"));
			log.setDaemon(true);
			log.start();
		} catch (IOException e) {
			System.err.println("Error initializing output. Check your output paths");
			System.exit(1);
		}
		
		int numRuns = ResourceAllocator.getSuggestedNumThreads(3);
		ExecutorService es = Executors.newCachedThreadPool();
		List<Worker> tasks = new ArrayList<Worker>();
		for (int i = 0; i < numRuns; i++) {
			tasks.add(new Worker(i, messages, jl, counts, classes));
		}
//		es.shutdown();
		try {
			es.invokeAll(tasks);
//			es.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e) {			
			System.err.println("Program was interrupted, attempting to write...");
		}
		finally {
			messages.add("Invocation complete. Writing!");
			//List<String> lines = counts.getStringLines();
			
		//	Set<Entry<String, Integer>> entries = counts.getEntrySet();
			messages.add("Entry set complete.");
			try {
				counts.writeUnsortedToFile(fw);
				fw.close();
				messages.add("All lines written");
			} catch (IOException e) {
				e.printStackTrace();
			}
            
		}
		messages.add("Exiting.");
	}
}
class Worker implements Runnable,Callable<String> {
	private final int threadNum;
	private final BlockingQueue<String> log;
	private final JsonLayer jl;
	private final WordMap joinedMap;
	private final String[] classes;
	public Worker(int threadNum, BlockingQueue<String> log, JsonLayer jl, WordMap full, String[] classes) {
		this.threadNum = threadNum;
		this.log = log;
		this.jl = jl;
		this.joinedMap = full;
		this.classes = classes;
	}
	public String call() {
		run();
		return "";
	}
	@Override
	public void run() {
		log.add("Thread"+threadNum+" is beginning its run");
		WordMap thisMap = WordDataFinder.getNew(classes);
		while (true) {
			log.add("Thread"+threadNum+" is waiting to acquire another file. "+jl.numReadableRemaining()+ " files remain.");
			List<JsonReadable> jsons = jl.getNextReadable();			
			if (jsons == null) {
				log.add("Thread"+threadNum+" has failed to find another list. It's ending its run.");
				break;
			}
			else {
				log.add("Thread"+threadNum+" has acquired another list of jsons");		
			}
			for (JsonReadable j : jsons) {
				RedditComment poop = new RedditComment(j);
				thisMap.addComment(poop);
			}
		}
		log.add("Thread"+threadNum+" is beginning its combination.");
		joinedMap.combine(thisMap);
		log.add("Thread"+threadNum+" is exiting.");
	}		
}
