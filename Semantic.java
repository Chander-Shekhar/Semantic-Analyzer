package cool;
import java.util.*;

public class Semantic{
	private boolean errorFlag = false;
	public void reportError(String filename, int lineNo, String error){
		errorFlag = true;
		System.err.println(filename+":"+lineNo+": "+error);
	}
	public boolean getErrorFlag(){
		return errorFlag;
	}

/*
	Don't change code above this line
*/

	private ScopeTable<AST.attr> scopeTable;
	public BuildTable Table;
	private String filename;
	public Semantic(AST.program program){
		//Write Semantic analyzer code here
		Table = new BuildTable();
		scopeTable = new ScopeTable<AST.attr>();
		Inheritance_graph inGraph = new Inheritance_graph(classList);
		inGraph.buildGraph(program.classes);
		inGraph.isDAG();
		inGraph.insert_classes(classList);
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

	private void Annonate(AST.class_ cl)
	{
		for(AST.feature f : cl.features)
		{
			if(f instanceof AST.attr)
				Annotate((AST.attr) f);
			else if(f instanceof AST.method)
				Annotate((AST.method) f);
			else 
				reportError(cl.filename, cl.lineNo, "Your parser is doing some shady stuff.");
		}
	}

	private void Annotate(AST.method m)
	{
		scopeTable.enterScope();

		for(AST.formal f : m.formals)
		{
			AST.attr at = scopeTable.lookUpLocal(f.name); 
			if(at == null)
				scopeTable.insert(f.name, new AST.attr(f.name, f.typeid, new AST.no_expr(f,lineNo), f.lineNo));
			else 
				reportError(filename, f.lineNo, "Formal parameter " + f.name + " is multiply defined.");

		}

		Annotate(m.body);

		if(!conformsTo(m.typeid, m.body.type))
			reportError(filename, m.body.lineNo, "Inferred return type " + m.body.type + " of method " + m.name + " does not conform to declared return type " + m.typeid);
		scopeTable.exitScope();
	}

	private void Annonate(AST.attr a)
	{
		if(a.value instanceof AST.no_expr) a.value.type = "_no_type";
		else 
		{
			Annonate(a.value);
			if(!conformsTo(a.value.type, a.typeid))
				reportError(filename, a.value.lineNo, "Inferred type " + a.value.type + " of initialization of attribute "+ a.name + " does not conform to declared type " + a.typeid);
		}
	}

	private void Annotate(AST.assign assign)
	{
		Annotate(assign.e1);
		AST.attr tmp = scopeTable.lookUpGlobal(assign.name);
		if(tmp == null)
			reportError(filename, assign.lineNo, "Assignment to undeclared variable " + assign.name);
		else if(!conformsTo(tmp.typeid, assign.e1.type))
			reportError(filename, assign.lineNo, "Type " + assign.e1.type + " of assigned expression does not conform to declared type " + tmp.typeid + " of identifier " + tmp.name);

		assign.type = assign.e1.type;
	}
}
