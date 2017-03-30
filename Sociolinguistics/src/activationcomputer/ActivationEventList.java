package activationcomputer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import util.generic.data.GenericList;
import util.sys.DataType;

public class ActivationEventList extends GenericList<ActivationEvent> {

	private static final long serialVersionUID = -4914666959885542901L;

	public ActivationEventList() {
		super();
	}
	public ActivationEventList(ActivationEventList o) {
		super(o);
	}
	
	@Override
	public DataType deepCopy() {
		return new ActivationEventList(this);
	}

	@Override
	public int getNumFixedArgs() {
		return 0;
	}

	@Override
	public boolean hasNArgs() {
		return false;
	}

	@Override
	public String getConstructionErrorMsg() {
		return "requires nothing";
	}
	
	public static ActivationEventList fromFile(File f) {
		ActivationEventList ael = new ActivationEventList();
		List<String> lines = null;
		try {
			lines = Files.readAllLines(f.toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		for (String line : lines) {
			ael.add(ActivationEvent.fromString(line));
		}
		return ael;
	}

}
