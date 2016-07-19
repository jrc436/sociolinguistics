package util.sys;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public abstract class FileProcessor<E extends DataType, V extends DataType> implements CmdLineInit {
	private final Queue<File> files;
	protected final Path outputDir;
	protected V processAggregate;
	private V initialAggregate;
	private boolean initialized = false;
	/**
	 * dummy constructor
	 */
	protected FileProcessor() {
		this.files = null;
		this.outputDir = null;
	}
	@SuppressWarnings("unchecked")
	protected void setInitialValue(V aggregate) {
		this.processAggregate = aggregate;
		this.initialAggregate = (V) aggregate.deepCopy();
		initialized = true;
	}
	protected FileProcessor(String inputDir, String outDir) {
		this.outputDir = Paths.get(outDir);
		
		if (!outputDir.toFile().isDirectory() && !outputDir.toFile().mkdirs()) {
			throw new IllegalArgumentException("Output Dir should always refer to an existing directory, or a path to a createable one");
		}
		this.files = makeInputFileQueue(inputDir);
	}
	protected Queue<File> makeInputFileQueue(String inpDir) {
		Path inDir = Paths.get(inpDir);
		File inDirF = inDir.toFile();
		if (!inDirF.exists()) {
			throw new IllegalArgumentException(inpDir+" does not refer to an existing file of any sort, please check for typos.");
		}
		if (!inDir.toFile().isDirectory()) {
			throw new IllegalArgumentException("Input Path should always refer to an existing directory, rather than a single input file");
		}
		return new LinkedList<File>(Arrays.asList(inDir.toFile().listFiles()));
	}
	public FileProcessor(String inputDir, String outDir, V initialValue) {
		this(inputDir, outDir);
		setInitialValue(initialValue);		
	}
	protected File getNextFile() {
		File f;
		synchronized (this.files) {
			if (this.files.peek() == null) {
				return null;
			}
			else {
				f = this.files.poll();
			}
		}
		return f;
	}
	protected File peek() {
		return this.files.peek();
	}
	public int numReadableRemaining() {
		return files.size();
	}
	@SuppressWarnings("unchecked")
	protected V getInitialThreadValue() {
		if (!initialized) {
			System.err.println("This method will not work as intended, because processor was not initialized correctly");
		}
		return (V) initialAggregate.deepCopy();//whatever the initial value is. 
	}
	public abstract E getNextData(); //thread should then do its map, IMPLEMENT TO BE THREADSAFE
	public abstract void map(E newData, V threadAggregate); //thread's map, DOES NOT NEED TO BE THREADSAFE
	public abstract void reduce(V threadAggregate); //processor will do its reduce, IMPLEMENT TO BE THREADSAFE	
	
	protected final static int DEFAULT = -77;
	protected int overrideInputArgs() {
		return DEFAULT;
	}
	protected int overrideOutputArgs() {
		return DEFAULT;
	}
	
	private static final int maxCharPerFile = 1000000;
	
	protected String getFileExt() {
		return ".txt";
	}
	
	/**
	 * Need to handle some special cases
	 */
	public File write() {
		System.err.println("Beginning write");
		ArrayList<String> all = processAggregate.getDataWriteLines();
		//String[] all = allc.toArray(new String[allc.size()]);
		int fileNum = 0;
		String base = outputDir.getFileName().toString();
		int runningChars = 0;
		List<String> lines = new ArrayList<String>();
		for (int i = 0; i < all.size(); i++) {
			lines.add(all.get(i));
			runningChars += all.get(i).length();
			if (runningChars > maxCharPerFile) {
				System.err.println("Preparing to write to a file");
				String fileName = base+"-"+fileNum+getFileExt();
				writeToFile(outputDir.resolve(fileName), lines, processAggregate.getHeaderLine(), processAggregate.getFooterLine());
				lines.clear();
				runningChars = 0;
				fileNum++;
			}
		}
		String lastFileName = fileNum == 0 ? base+getFileExt() : base+"-"+fileNum+getFileExt();
		System.err.println("Preparing to write to final file");
		writeToFile(outputDir.resolve(lastFileName), lines, processAggregate.getHeaderLine(), processAggregate.getFooterLine());
		return outputDir.toFile();
	}
	private void writeToFile(Path outFile, List<String> lines, String header, String footer) {
		try {
			FileWriter fw = new FileWriter(outFile.toFile());
			if (header != null) {
				fw.write(header + System.getProperty("line.separator"));
			}
			for (String line : lines) {
				fw.write(line + System.getProperty("line.separator"));
			}
			if (footer != null) {
				fw.write(footer + System.getProperty("line.separator"));
			}
			fw.close();
		}
		catch (IOException io) {
			io.printStackTrace();
			System.err.println("Error writing to file: "+outFile);
			System.exit(1);
		}
	}
}
