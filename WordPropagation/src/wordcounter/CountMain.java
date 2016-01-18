package wordcounter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import util.JsonLayer;
import util.WordMap;

public class CountMain {

	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//        String errMessage = "Required parameters: Input (JSON) and Output (Counts)";
//        boolean needsConversion = true;
//        if (args.length == 0 || args.length == 1) {
//                System.out.println(errMessage);
//                System.exit(1);
//        }
//        else if (args.length == 2) {
//                System.out.println("Assuming value: "+needsConversion+" to determine whether conversion is needed");
//        }
//        else if (args.length == 3) {
//                needsConversion = Boolean.valueOf(args[2]);
//        }
//        else {
//                System.out.println("All arguments beyond three are being ignored");
//        }
//        JsonLayer jl = new JsonLayer(Paths.get(args[0]), needsConversion);
//        List<Map<String, String>> jsons = jl.getReadable();
//        WordMap counts = new WordMap();
//        System.out.println("Beginning to add counts");
//        int total = counts.size();
//        for (Map<String, String> json : jsons) {
//                counts.addSentence(json.get("body"));
//                total--;
//                if (total % 100 == 0) {
//                        System.out.println(total + " remaining");
//                }
//        }
//        try {
//                FileWriter fw = new FileWriter(args[1]);
//                fw.write(counts.toString());
//                fw.close();
//        }
//        catch (IOException ie) {
//                ie.printStackTrace();
//        }

		File folder = new File(args[0]);
		File[] listOfFiles = folder.listFiles();
		for (File bigRed : listOfFiles) {
			JsonLayer.preProcess(bigRed.toPath(), Paths.get(args[1]));
		}
	}

}
