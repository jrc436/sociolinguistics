package wordtracer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import util.JsonLayer;
import util.JsonReadable;
import util.Logger;
import util.ResourceAllocator;
import util.WordMap;

public class CountMain {
	public static void main(String[] args) {
        String errMessage = "Required parameters: Input Folder (JSON) and Output File (Count-Format)";
        if (args.length != 2) {
                System.out.println(errMessage);
                System.exit(1);
        }
        JsonLayer jl = new JsonLayer(Paths.get(args[0]));
        WordMap counts = new WordMap();
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
			tasks.add(new Worker(i, messages, jl, counts));
//			es.execute(new Worker(i, messages, jl, counts));	
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
			Set<Entry<String, Integer>> entries = counts.getEntrySet();
			messages.add("Entry set complete.");
			try {
				int count = 0;
				for (Entry<String, Integer> entry : entries) {
					String line = entry.getKey() + WordMap.splitter + entry.getValue();
					fw.write(line+System.getProperty("line.separator"));
					fw.flush();
					if (count % 1000 == 0) {
						messages.add(count+ " words have been accounted for");	
					}
					count++;
				}
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
	public Worker(int threadNum, BlockingQueue<String> log, JsonLayer jl, WordMap full) {
		this.threadNum = threadNum;
		this.log = log;
		this.jl = jl;
		this.joinedMap = full;
	}
	public String call() {
		run();
		return "";
	}
	@Override
	public void run() {
		log.add("Thread"+threadNum+" is beginning its run");
		WordMap thisMap = new WordMap();
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
				thisMap.addSentence(j.get("body"));
			}
		}
		log.add("Thread"+threadNum+" is beginning its combination.");
		joinedMap.combine(thisMap);
		log.add("Thread"+threadNum+" is exiting.");
	}		
}
