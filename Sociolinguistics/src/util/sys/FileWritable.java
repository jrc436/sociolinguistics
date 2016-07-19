package util.sys;

import java.util.ArrayList;

public interface FileWritable {
	public String getFileExt();
	public ArrayList<String> getDataWriteLines();
	public String getHeaderLine();
	public String getFooterLine();
}
