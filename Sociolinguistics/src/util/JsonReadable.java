package util;

import java.util.HashMap;

public class JsonReadable extends HashMap<String, String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6456148281092346639L;

	public String toString() {
		String ret = "";
		ret += "{";
		for (Entry<String, String> entries : this.entrySet()) {
			ret += "\""+entries.getKey()+"\"";
			ret += ":";
			//ret += "\"" + entries.getValue() + "\"";
			ret += entries.getValue();
			ret += ",";
		}
		ret = ret.substring(0, ret.length()-1);
		ret += "}";
		return ret;
	}
}
