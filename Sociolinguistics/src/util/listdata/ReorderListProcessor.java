package util.listdata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * This class does not extend the typical FileProcessor interface because it is NOT threadsafe. It relies on the files to be processed in order. 
 * However, it could be thought of a FileProcessor from DataCollection<E> to DataCollection<E>
 * @author jrc436
 *
 */
public class ReorderListProcessor {
	
	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.err.println("Specify inputDirectory first, outputDirectory second, optionally a new basename for the file third");
			System.exit(1);
		}
		File inputDirectory = new File(args[0]);
		File outputDirectory = new File(args[1]);
		String rebase = args.length > 2 ? args[2] : null;
		List<FileParts> fp = captureAll(inputDirectory);
		addRepaths(outputDirectory, fp, rebase);
		fp.get(0).readFile();
		fp.get(0).writeFile();
		for (int i = 1; i < fp.size(); i++) {
			fp.get(i).readFile();
			fixLists(fp.get(i-1).lines, fp.get(i).lines);
			fp.get(i).writeFile();
		}
		//copyAll(fp);
	}
	private static void fixLists(List<String> first, List<String> second) {
		if (!DataCollection.isKeyLine(first.get(0))) {
			throw new IllegalArgumentException("The 'first' list has still not been fixed, so this call is invalid");
		}
		for (String s : second) {
			if (DataCollection.isKeyLine(s)) {
				break;
			}
			first.add(second.remove(0));
		}
	}
	public static List<FileParts> captureAll(File inputDirectory) {
		List<FileParts> fileparts = new ArrayList<FileParts>();
		for (File f : inputDirectory.listFiles()) {
			fileparts.add(capture(f));
		}
		return fileparts;
	}
	private static Path getNewFilePath(File outputDir, FileParts fp, String rebase, int totalFiles) {
		if (rebase != null) {
			return outputDir.toPath().resolve(fp.rebaseFormat(rebase, totalFiles));
		}
		return outputDir.toPath().resolve(fp.format(totalFiles));
	}
	public static void addRepaths(File outputDir, List<FileParts> fp, String rebase) {
		int totalFiles = fp.size();
		for (FileParts f : fp) {
			f.repath = getNewFilePath(outputDir, f, rebase, totalFiles);
		}
	}
	public static void copyAll(List<FileParts> fp) throws IOException {
		for (FileParts f : fp)  {
			Files.copy(f.origFile.toPath(), f.repath);
		}
	}
	
	/**
	 * The naming conventions of files require there to be a basename-##.ext .
	 */
	private static FileParts capture(File f) {
		String fileName = f.getName();
		String[] dashParts = fileName.split("-");
		String numAndExt = dashParts[dashParts.length-1];
		String baseName = "";
		for (int i = 0; i < dashParts.length-1; i++) {
			baseName += dashParts[i] + "-";
		}
		baseName = baseName.substring(0, baseName.length()-1);
		String[] extParse = numAndExt.split(".");
		System.err.println("Warning: multiple periods in file extension. Parsing could be incorrect");
		int num = Integer.parseInt(extParse[0]);
		String ext = "";
		for (int i = 1; i < extParse.length; i++) {
			ext += extParse[i] + ".";
		}
		ext = ext.substring(0, extParse.length-1);
		return new FileParts(baseName, ext, num, f);
		
	}
	static class FileParts {
		private final String baseName;
		private final int fileNum;
		private final String extension;
		private final File origFile;
		private Path repath;
		private List<String> lines;
		public FileParts(String baseName, String extension, int fileNum, File origFile) {
			this.baseName = baseName;
			this.extension = extension;
			this.fileNum = fileNum;
			this.origFile = origFile;
		}
		public String toString() {
			return baseName + "-" + fileNum + "." + extension;
		}
		public String format(int totalNumberOfFiles) {
			int digitsNeeded = 1 + totalNumberOfFiles / 10;
			String formatNumber = String.format("%0"+digitsNeeded+"d", fileNum);
			return baseName + "-" + formatNumber + "." + extension;
		}
		public String rebaseFormat(String newBase, int totalNumberOfFiles) {
			return new FileParts(newBase, extension, fileNum, origFile).format(totalNumberOfFiles);
		}
		public void readFile() {
			try {
				this.lines = Files.readAllLines(origFile.toPath());
			}
			catch (IOException ie) {
				System.err.println("Unable tor ead file: "+origFile);
				ie.printStackTrace();
				System.exit(1);
			}
		}
		public List<String> getLines() {
			return lines;
		}
		public void writeFile() {
			try {
				FileWriter fw = new FileWriter(repath.toFile());
				for (String s : lines) {
					fw.write(s + System.getProperty("line.separator"));
				}
				fw.close();
			} catch (IOException e) {
				System.err.println("Unable to write to file: "+repath);
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
