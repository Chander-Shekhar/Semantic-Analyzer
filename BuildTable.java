package cool;
import java.util.*;
import java.util.Map.Entry;

public class BuildTable
{
	private HashMap<String, AST.class_> classList = new HashMap<String, AST.class_>(); // maintains information of all the classes.
	private boolean errFlag = false;	// Stores if there is any error during insertion of a new class.
	public void reportError(String filename, int lineNo, String error){	// copy of reportError as given in Semantic.java to print errors
		errFlag = true;
		System.err.println(filename+":"+lineNo+": "+error);
	}

	public BuildTable()
	{
		// In the constructor of BuildTable, we would like to add all the default classes of COOL into the classList.
		// So for all basic classes, we make a List<AST.feature> to contain the features of a given basic class.
		// mObject contains methods of Object Class.
		List<AST.feature> mObject = new ArrayList<AST.feature>();
		mObject.add(new AST.method("abort", new ArrayList<AST.formal>(), "Object", new AST.no_expr(0), 0));
		mObject.add(new AST.method("type_name", new ArrayList<AST.formal>(), "String", new AST.no_expr(0), 0));
		classList.put("Object", new AST.class_("Object", "", null, mObject, 0));  // Put the basic class Object in the classList.
	
		//mIO contains methods of IO class.
		List<AST.feature> mIO = new ArrayList<AST.feature>();
		List<AST.formal> out_stringFormals = new ArrayList<AST.formal>();
		out_stringFormals.add(new AST.formal("String","String", 0));
		List<AST.formal> out_intFormals = new ArrayList<AST.formal>();
		out_intFormals.add(new AST.formal("Int", "Int", 0));

		mIO.add(new AST.method("out_string", out_stringFormals, "IO", new AST.no_expr(0), 0));
		mIO.add(new AST.method("in_string", new ArrayList<AST.formal>(), "String",new AST.no_expr(0), 0));
		mIO.add(new AST.method("out_int", out_intFormals, "IO", new AST.no_expr(0), 0));
		mIO.add(new AST.method("in_int", new ArrayList<AST.formal>(), "Int",new AST.no_expr(0), 0));
		classList.put("IO", new AST.class_("IO", "", "Object", mIO, 0)); // Put the basic class IO in the classList.
		classList.get("IO").features.addAll(mObject);	// Add features of Parent class Object into IO.
		
		classList.put("Bool", new AST.class_("Bool", "", "Object", new ArrayList<AST.feature>(), 0));	// Put the basic class Bool in the classList.
		classList.get("Bool").features.addAll(mObject);	// Add features of Parent class Object into Bool.

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
		classList.get("String").features.addAll(mObject);	// Add features of Parent class Object into String.

		classList.put("Int", new AST.class_("Int", "", "Object", new ArrayList<AST.feature>(), 0));
		classList.get("Int").features.addAll(mObject);	// Add features of Parent class Object into Int.
	}
	
	public boolean getErrorFlag()	//Returns if any error was encountered during insertion of a new class.
	{
		return errFlag;
	}
	
	public void insert(AST.class_ cl)  // Given a AST.class_ object, insert it into the classList while checking for multiple definition and inheritance related errors.
	{
		/*
		Note: Using HashMaps for storing the methods temperarily before putting it into the newClass gives us the advantage 
		of constant time searching for a method name if instead a list was used the time would have been O(n).		
		*/
		
		// A new class with all the properties of class passed but with an empty feature list.
		AST.class_ newClass = new AST.class_(cl.name, cl.filename, cl.parent, new ArrayList<AST.feature>(), cl.lineNo);
		// The next two HashMaps will maintain the attribute and method lists of the class.
		HashMap<String, AST.attr> newaList = new HashMap<String, AST.attr>();
		HashMap<String, AST.method> newmList = new HashMap<String, AST.method>();

		for(AST.feature f : cl.features)
		{	
			if(f instanceof AST.attr) // Returns error if an attribute is defined multiple times otherwise put it in the HashMap of Attributes.
			{
				AST.attr variable = (AST.attr) f;
				if(newaList.containsKey(variable.name))
					reportError(cl.filename, variable.lineNo, "Attribute " + variable.name + " is defined multiple times in class "+ cl.name);
				else
					newaList.put(variable.name, variable);
			}
			else if(f instanceof AST.method) // Returns error if a mehtod is defined multiple times otherwise put it in the HashMap of methods.
			{
				AST.method method = (AST.method) f;
				if(newmList.containsKey(method.name))
					reportError(cl.filename, method.lineNo, "Method " + method.name + " is defined multiple times in class "+ cl.name);
				else
					newmList.put(method.name, method);
			}
		}

		for(AST.feature f : classList.get(cl.parent).features) // Runs a loop on the inherited features of the class.
		{
			if(f instanceof AST.attr) // Returns error if an inherited attribute is redefined otherwise put the inherited attribute in the HashMap of Attributes.
			{
				AST.attr att = (AST.attr) f;
				if(newaList.containsKey(att.name))
					reportError(cl.filename, newaList.get(att.name).lineNo, "Cannot redifine : Attribute " + att.name + " of class " + cl.name + " is an attribute of the inherited class " + cl.parent);
				else newaList.put(att.name, att);
			}
			else if(f instanceof AST.method) // Returns error if an inherited method is not redefined properly.
			{
				boolean flag = false;	// Stores if the inherited method is not redefined properly.
				AST.method m = (AST.method) f;
				if(newmList.containsKey(m.name)) // If inherited method is redefined :
				{
					AST.method mchild = newmList.get(m.name);  // Stores Redefinition of method
					if(m.formals.size() != mchild.formals.size()) //Error if no. of parameters are not equal.
					{
						reportError(cl.filename, mchild.lineNo, "Incompatible number of formal parameters in redefined method " + m.name);
						flag = true;
					}
					else 
					{
						if(m.typeid.equals(mchild.typeid) == false) // Error if return type of method is changed.
						{
							reportError(cl.filename, mchild.lineNo,  "In redefined method " + m.name + ", return type "	+ mchild.typeid + " is different from original return type " + m.typeid);
							flag = true;
						}
						for(int i=0;i<m.formals.size();i++)	// Error if type of parameters are changed.
						{
							if(m.formals.get(i).typeid.equals(mchild.formals.get(i).typeid) == false)
							{
								reportError(cl.filename, mchild.lineNo, "In redefined method " + m.name + ", parameter type " + mchild.formals.get(i).typeid + " is different from original type " + m.formals.get(i).typeid);
								flag = true;
							}
						}
					}
				}
				else newmList.put(m.name, m); // If inherited method is not redefined, add it to the list.
				//If the inherited method is not defined properly, add the parent method by default. Otherwise, the redefined method is already in the list.
				if(flag) newmList.put(m.name, m); 
			}
		}
		// Add the Attributes and methods to the features list in the class and add the class to classList.
		newClass.features.addAll(newaList.values());
		newClass.features.addAll(newmList.values());
		classList.put(cl.name, newClass);
		// System.out.println(newClass.name);  // Just some error checking loop.
		// for(AST.feature f : newClass.features)
		// {
		// 	if(f instanceof AST.attr)
		// 	System.out.print(((AST.attr)f).name + " ");
		// 	else if(f instanceof AST.method)
		// 	System.out.print(((AST.method)f).name + " ");
		// }
		// System.out.println();
	}

	public HashMap<String, AST.attr> getAttrs(String cname) // Returns newly defined as well as inherited attributes of a class.
	{
		HashMap<String, AST.attr> Attrs = new HashMap<String, AST.attr>();
		AST.class_ cl = getClass(cname);
		for(AST.feature f : cl.features)
		{
			if(f instanceof AST.attr)
			{
				AST.attr e = (AST.attr) f;
				Attrs.put(e.name, e);
			}
		}
		return Attrs;
	}

	public boolean isPresent(String cname){  // Returns if a class with given name is defined or not.
		return classList.containsKey(cname);
	}

	public AST.class_ getClass(String cname) // Returns a class with given class name.
	{
		return classList.get(cname);
	}

	public boolean conformsTo(String c1, String c2) // Checks if class c1 is a descendant of class c2 i.e. if c1 can be used in place of c2.
	{
		if(c1 == null) return false;
		if(c1.equals(c2)) return true;
		else 
		{
			if(classList.containsKey(c1)){
				c1 = classList.get(c1).parent;
				return conformsTo(c1, c2);
			}
			return false;
		}
	}

	public String commonAncestor(String c1, String c2) // Returns the lowest common ancestor of class c1 & c2.
	{	// Used in annotating cond and typcase.
		if(conformsTo(c1, c2)) return c2;
		else if(conformsTo(c2, c1)) return c1;
		else return commonAncestor(classList.get(c1).parent, c2);
	}

	public AST.method getMethod(String cname, String mname) // Returns a method of given name in a given class. 
	{
		AST.class_ cl = classList.get(cname);
		for(AST.feature f : cl.features)
		{
			if(f instanceof AST.method &&  mname.equals(((AST.method) f).name))
				return (AST.method) f;
		}
		return null;
	}
}
