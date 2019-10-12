package cool;
import java.util.*;

public class Error {
	public String filename;
	public int line;
	public String error;
	Error(String f, int l, String er){
		filename=new String(f);
		line=l;
		error=new String(er);
	}
}
