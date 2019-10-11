package cool;
import java.util.*;
import cool.AST;

public class ClassDecl
{
  public String name;
  public String pname = null;
  public HashMap<String, AST.attr> aList;
  public HashMap<String, AST.method> mList;
  public Integer height;
  
  public ClassDecl (String cname, String parent, HashMap<String, AST.attr> attrs, HashMap<String, AST.method> methods, Integer h) {
     name = cname;
     pname = parent;
     aList = new HashMap<String, AST.attr>();
     mList = new HashMap<String, AST.method>();
     aList.putAll(attrs);
     mList.putAll(methods);
     height = h;
  }
}
