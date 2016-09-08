package util.listdata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
		//addRepaths(outputDirectory, fp, rebase);
		Collections.sort(fp, FileParts.getFileNumComparator());
		fp.get(0).readFile();
		//fp.get(0).writeFile();
		int lastKeyLine = 0;
		for (int i = 0; i < fp.get(0).lines.size(); i++) {
			if (DataCollection.isKeyLine(fp.get(0).lines.get(i))) {
				lastKeyLine = i;
			}
		}
		for (int i = 1; i < fp.size(); i++) {
			fp.get(i).readFile();
			lastKeyLine = fixLists(fp.get(i-1).lines, fp.get(i).lines, lastKeyLine);
			if (lastKeyLine == -1) {
				fp.remove(i);
				i--;
			}
			//fp.get(i).writeFile();
		}
		for (int i = 0; i < fp.size(); i++) {
			fp.get(i).fileNum = i;
		}
		addRepaths(outputDirectory, fp, rebase);
		for (FileParts f : fp) {
			f.writeFile();
		}
		//fp.get(0).writeFile();
		//for (int i = 1; i < fp.size(); i++) {
		//	fp.get(i).readFile();
		//	fixLists(fp.get(i-1).lines, fp.get(i).lines);
		//	fp.get(i).writeFile();
		//}
		//copyAll(fp);
	}
	private static int fixLists(List<String> first, List<String> second, int lastKeyLine1) {
		if (!DataCollection.isKeyLine(first.get(0))) {
			throw new IllegalArgumentException("The 'first' list has still not been fixed, so this call is invalid");
		}
		int firstKeyLine = 0;
		int lastKeyLine2 = -1;
		while (true) {
			if (firstKeyLine >= second.size()) {
				break;
			}
			if (DataCollection.isKeyLine(second.get(firstKeyLine))) {
				lastKeyLine2 = firstKeyLine;
			}
			if (lastKeyLine2 == -1) {
				firstKeyLine++;
			}
		}
		//if there are more of this type of element in first than in second, append to first
		//otherwise, append the ones from first to second
		if (first.size() - lastKeyLine1 > firstKeyLine) {
			for (int i = 0; i < firstKeyLine; i++) {
				//append all of the ones before the keyline to the first one
				first.add(second.remove(0));
			}
		}
		else {
			for (int i = lastKeyLine1; i < first.size(); i++) {
				second.add(i - lastKeyLine1, first.remove(lastKeyLine1));
			}
		}
		return lastKeyLine2;
	}

//	private static void fixLists(List<String> first, List<String> second) {
//		if (!DataCollection.isKeyLine(first.get(0))) {
//			throw new IllegalArgumentException("The 'first' list has still not been fixed, so this call is invalid");
//		}
//		int j = 0;
//		while (true) {
//			if (DataCollection.isKeyLine(second.get(j))) {
//				break;
//			}
//			j++;
//		}
//		for (int i = 0; i < j; i++) {
//			//append all of the ones before the keyline to the first one
//			first.add(second.remove(0));
//		}
//	}
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
		String numAndExt = "";
		String baseName = "";
		if (dashParts.length >= 2) {
			numAndExt = dashParts[dashParts.length-1];
			for (int i = 0; i < dashParts.length-1; i++) {
				baseName += dashParts[i] + "-";
			}
			baseName = baseName.substring(0, baseName.length()-1);
		}
		else {
			System.err.println("Warning: no dashes found in file. Parsing could be incorrect (parsed from : "+fileName+")");
			if (!fileName.contains(".")) {
				return new FileParts(fileName, "", -1, f);
			}
			String[] parts = fileName.split("\\.");
			baseName = parts[0];
			for (int i = 1; i < parts.length; i++) {
				numAndExt += parts[i];
			}
		}
		String[] extParse = numAndExt.split("\\.");
		if (extParse.length > 2) {
			System.err.println("Warning: multiple periods in file extension. Parsing could be incorrect (parsed from: "+numAndExt+")");
		}
		else if (extParse.length < 2) {
			System.err.println("Warning: no file extension found. Parsing could be incorrect (parsed from: "+numAndExt+")");
			if (numAndExt.matches("\\d+")) {
				return new FileParts(baseName, "", Integer.parseInt(numAndExt), f);
			}
			System.err.println("Warning: no number found. Parsing could be incorrect (parsed from: "+numAndExt+")");
			return new FileParts(baseName, "", -1, f);
		}
		int num = extParse[0].matches("\\d+") ? Integer.parseInt(extParse[0]) : -1;
		int s = num == -1 ? 0 : 1;
		String ext = "";
		for (int i = s; i < extParse.length; i++) {
			ext += extParse[i] + ".";
		}
		ext = ext.substring(0, extParse.length-1);
		return new FileParts(baseName, ext, num, f);
	}
	static class FileParts {
		private final String baseName;
		private int fileNum;
		private final String extension;
		private final File origFile;
		private Path repath;
		private List<String> lines;
		public static Comparator<FileParts> getFileNumComparator() {
			return new Comparator<FileParts>() {
				@Override
				public int compare(FileParts o1, FileParts o2) {
					if (o1.fileNum == o2.fileNum) {
						return 0;
					}
					else if (o1.fileNum > o2.fileNum) {
						return 1;
					}
					return -1;
				}
			};
		}
		public FileParts(String baseName, String extension, int fileNum, File origFile) {
			this.baseName = baseName;
			this.extension = extension;
			this.fileNum = fileNum;
			this.origFile = origFile;
		}
		private String extString() {
			return extension == null || extension.isEmpty() ? "" : "."+extension;
		}
		public String toString() {
			if (fileNum == -1) {
				return baseName + extString();
			}
			return baseName + "-" + fileNum  + extString();
		}
		public String format(int totalNumberOfFiles) {
			if (fileNum == -1) {
				if (totalNumberOfFiles != 1) {
					System.err.println("Warning: naming scheme is assigning a non-numbered file, despite having multiple files");
				}
				return baseName + extString();
			}
			int digitsNeeded = 1 + ((int)Math.floor(Math.log10(totalNumberOfFiles)));
			String formatNumber = String.format("%0"+digitsNeeded+"d", fileNum);
			return baseName + "-" + formatNumber + extString();
		}
		public String rebaseFormat(String newBase, int totalNumberOfFiles) {
			return new FileParts(newBase, extension, fileNum, origFile).format(totalNumberOfFiles);
		}
		public void readFile() {
			try {
				this.lines = Files.readAllLines(origFile.toPath());
			}
			catch (IOException ie) {
				System.err.println("Unable to read file: "+origFile);
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
