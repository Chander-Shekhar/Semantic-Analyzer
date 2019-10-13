package cool;
import java.util.*;
import java.util.Map.Entry;

public class BuildTable
{
	private List<Error> err = new ArrayList<Error>();
	private HashMap<String, AST.class_> classList = new HashMap<String, AST.class_>();

	public BuildTable()
	{
		List<AST.feature> mObject = new ArrayList<AST.feature>();
		mObject.add(new AST.method("abort", new ArrayList<AST.formal>(), "Object", new AST.no_expr(0), 0));
		mObject.add(new AST.method("type_name", new ArrayList<AST.formal>(), "String", new AST.no_expr(0), 0));
		classList.put("Object", new AST.class_("Object", "", "Object", mObject, 0));

		List<AST.feature> mIO = new ArrayList<AST.feature>();
		List<AST.formal> out_stringFormals = new ArrayList<AST.formal>();
		out_stringFormals.add(new AST.formal("String","String", 0));
		List<AST.formal> out_intFormals = new ArrayList<AST.formal>();
		out_intFormals.add(new AST.formal("Int", "Int", 0));

		mIO.add(new AST.method("out_string", out_stringFormals, "IO", new AST.no_expr(0), 0));
		mIO.add(new AST.method("in_string", new ArrayList<AST.formal>(), "String",new AST.no_expr(0), 0));
		mIO.add(new AST.method("out_int", out_intFormals, "IO", new AST.no_expr(0), 0));
		mIO.add(new AST.method("in_int", new ArrayList<AST.formal>(), "Int",new AST.no_expr(0), 0));
		classList.put("IO", new AST.class_("IO", "", "Object", mIO, 0));
		classList.get("IO").features.addAll(mObject);

		classList.put("Bool", new AST.class_("Bool", "", "Object", new ArrayList<AST.feature>(), 0));
		classList.get("Bool").features.addAll(mObject);

		List<AST.feature> mString = new ArrayList<AST.feature>();
		List<AST.formal> concatFormals = new ArrayList<AST.formal>();
		concatFormals.add(new AST.formal("String", "String", 0));
		List<AST.formal> substrFormals = new ArrayList<AST.formal>();
		substrFormals.add(new AST.formal("Int1", "Int", 0));
		substrFormals.add(new AST.formal("Int2", "Int", 0));
		mString.add(new AST.method("length", new ArrayList<AST.formal>(), "Int", new AST.no_expr(0), 0));
		mString.add(new AST.method("concat", concatFormals, "String", new AST.no_expr(0), 0));
		mString.add(new AST.method("substr", substrFormals, "String", new AST.no_expr(0), 0));
		classList.put("String", new AST.class_("String", "", "Object", mString, 0));
		classList.get("String").features.addAll(mObject);

		classList.put("Int", new AST.class_("Int", "", "Object", new ArrayList<AST.feature>(), 0));
		classList.get("Int").features.addAll(mObject);
	}

	public AST.class_ getObject()
	{
		return classList.get("Object");
	}

	public AST.class_ getIO()
	{
		return classList.get("IO");
	}

	public void insert(AST.class_ cl)
	{
		AST.class_ newClass = new AST.class_(cl.name, cl.filename, cl.parent, classList.get(cl.parent).features, cl.lineNo);

		HashMap<String, AST.attr> newaList = new HashMap<String, AST.attr>();
		HashMap<String, AST.method> newmList = new HashMap<String, AST.method>();

		for(AST.feature f : cl.features)
		{
			if(f instanceof AST.attr)
			{
				AST.attr variable = (AST.attr) f;
				if(newaList.containsKey(variable.name))
					err.add(new Error(cl.filename, variable.lineNo, "Attribute " + variable.name + " is multiply defined in class."));
				else
					newaList.put(variable.name, variable);
			}
			else if(f instanceof AST.method)
			{
				AST.method method = (AST.method) f;
				if(newmList.containsKey(method.name))
					err.add(new Error(cl.filename, method.lineNo, "Method " + method.name + " is multiply defined."));
				else
					newmList.put(method.name, method);
			}
		}

		for(AST.feature f : newClass.features)
		{
			if(f instanceof AST.attr)
			{
				AST.attr att = (AST.attr) f;
				if(newaList.containsKey(att.name))
					err.add(new Error(cl.filename, newaList.get(att.name).lineNo, "Attribute " + att.name + " is an attribute of an inherited class"));
				else newaList.put(att.name, att);
			}
			else if(f instanceof AST.method)
			{
				boolean flag = false;
				AST.method m = (AST.method) f;
				if(newmList.containsKey(m.name))
				{
					AST.method mchild = newmList.get(m.name);
					if(m.formals.size() != mchild.formals.size())
					{
						err.add(new Error(cl.filename, mchild.lineNo, "Incompatible number of formal parameters in redefined method " + m.name));
						flag = true;
					}
					else 
					{
						if(m.typeid.equals(mchild.typeid) == false)
						{
							err.add(new Error(cl.filename, mchild.lineNo,  "In redefined method " + m.name + ", return type "
								+ mchild.typeid + " is different from original return type " + m.typeid));
							flag = true;
						}
						for(int i=0;i<m.formals.size();i++)
						{
							if(m.formals.get(i).typeid.equals(mchild.formals.get(i).typeid) == false)
							{
								err.add(new Error(cl.filename, mchild.lineNo, "In redefined method " + m.name + ", parameter type"
									+ mchild.formals.get(i).typeid + " is different from original type " + m.formals.get(i).typeid));
								flag = true;
							}
						}
					}
				}
				else newmList.put(m.name, m);
				if(flag) newmList.put(m.name, m);
			}
		}
		newClass.features.clear();
		newClass.features.addAll(newaList.values());
		newClass.features.addAll(newmList.values());
		classList.put(cl.name, newClass);

	}

	List<Error> getErrors()
	{
		return err;
	}

	List<AST.attr> getAttr(String cname)
	{
		List<AST.attr> Attrs = new ArrayList<AST.attr>();
		for(AST.feature f : classList.get(cname).features)
			if(f instanceof AST.attr)
				Attrs.add((AST.attr) f);
		return Attrs;
	}

	public boolean isPresent(String cname){
		return classList.containsKey(cname);
	}

	public AST.class_ getClass(String cname)
	{
		return classList.get(cname);
	}

	boolean conformsTo(String c1, String c2)
	{
		if(c1 == null) return false;
		if(c1.equals(c2)) return true;
		else 
		{
			c1 = classList.get(c1).parent;
			if(c1 == null) return false;
			else return conformsTo(c1, c2);
		}
	}

	String commonAncestor(String c1, String c2)
	{
		if(conformsTo(c1, c2)) return c2;
		else if(conformsTo(c2, c1)) return c1;
		else return commonAncestor(classList.get(c1).parent, c2);
	}

}
