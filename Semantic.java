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
		// Initializing attributes
		Table = new BuildTable();	
		scopeTable = new ScopeTable<AST.attr>();
		Inheritance_graph inGraph = new Inheritance_graph(program.classes, Table);// intializing the inheritance graph
		inGraph.buildGraph(program.classes); //Builds the inheritance graph in this step
		inGraph.isDAG();	//checks for cycles if they are present the program is halted 
		inGraph.insert_classes(Table);	// using inheritance graph each class is added in the table and their feature list is updated
		
		if(Table.getErrorFlag()) //If there was some error while populating the Table with classes, sets the errorFlag.
			errorFlag = true;
		
		for(AST.class_ e : program.classes) {
			filename = e.filename;
			//For each class, enter a new scope in the scopeTable.
			scopeTable.enterScope();	
			scopeTable.insert("self", new AST.attr("self", e.name, new AST.no_expr(e.lineNo), e.lineNo));
			//Add all the attributes of the class to the scopeTable including self.
			scopeTable.getMap().get(scopeTable.getScope()).putAll(Table.getAttrs(e.name));
			Annotate(e);	//Annotate the class .
			scopeTable.exitScope();	//Exit the current scope.	
		}
		if(!Table.isPresent("Main")){	//throw error if Main class is absent
			reportError(filename, 1, "Program does not contain class 'Main'");
		}else
		{
			AST.method m = Table.getMethod("Main","main");
			if(m == null){		//throw error if main method is not there in class Main.
				reportError(filename, 1, "'Main' class does not contain 'main' method");
			}
			else if(!m.formals.isEmpty()) // throw error if arguments are passed to main
			{
				reportError(filename, 1, "'main' method in class Main should have no arguments.");
			}
		}
	}


	private void Annotate(AST.class_ cl) //Calls Annotate on every feature of the class i.e. attributes and methods.
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

	private void Annotate(AST.attr a)	// Checks for type errors of an attribute and annotates it with appropriate type.
	{
		AST.attr a_self = scopeTable.lookUpLocal("self");
		if(!Table.isPresent(a.typeid))	// Error if declared type of an attribute is undefined.
			reportError(filename, a.lineNo, " Class " + a.typeid+" of attribute "+a.name+" is undefined");
		if(a.value instanceof AST.no_expr) a.value.type = "_no_type";
		else 
		{
			Annotate(a.value);
			//Error If the type annotated to value of attribute doesn't conform with the declared type.
			if(!Table.conformsTo(a.value.type, a.typeid))
				reportError(filename, a.value.lineNo, "Inferred type " + a.value.type + " of initialization of attribute "+ a.name + " does not conform to declared type " + a.typeid);
		}
	}

	private void Annotate(AST.method m)  // Checks for type errors of an method.
	{
		AST.attr a_self = scopeTable.lookUpLocal("self");
		scopeTable.enterScope();

		for(AST.formal f : m.formals) //Error if a particular parameter of a method is defined multiple times Otherwise add it to the scopeTable.
		{
			AST.attr at = scopeTable.lookUpLocal(f.name); 
			if(at == null)
				scopeTable.insert(f.name, new AST.attr(f.name, f.typeid, new AST.no_expr(f.lineNo), f.lineNo));
			else 
				reportError(filename, f.lineNo, "Formal parameter " + f.name + " is defined multiple times in method "+ m.name);

		}
		if(!Table.isPresent(m.typeid)){  // Error if return type of method is undefined.
			reportError(filename, m.lineNo, " Return type " + m.typeid+" of method "+m.name+" is undefined");
			return;
		}

		Annotate(m.body); 	//Annotate the body.
		//Error If the type annotated to body of method doesn't conform with the declared return type.
		if(!Table.conformsTo(m.body.type, m.typeid))
			reportError(filename, m.body.lineNo, "Inferred return type " + m.body.type + " of method " + m.name + " does not conform to declared return type " + m.typeid);
		scopeTable.exitScope();
	}

	private void Annotate(AST.expression expr){
		/*
		Checking the type of the expression and calling the corresponding Annonate function to associate the type.
		After annotating the type with basic expressions, 
		we can check for type errors in bigger blocks such as attributes, methods, dispatch etc and associate type with them.
		*/
	 	if(expr instanceof AST.int_const)
			expr.type="Int";
		else if(expr instanceof AST.string_const)
			expr.type="String";
		else if(expr instanceof AST.bool_const || expr instanceof AST.isvoid )
			expr.type="Bool";
		else if(expr instanceof AST.object)
			Annotate((AST.object)expr);
	 	else if(expr instanceof AST.assign)
			Annotate((AST.assign)expr);
		else if(expr instanceof AST.dispatch)
			Annotate((AST.dispatch)expr);
		else if(expr instanceof AST.static_dispatch)
			Annotate((AST.static_dispatch)expr);
		else if(expr instanceof AST.cond)
			Annotate((AST.cond)expr);
		else if(expr instanceof AST.loop)
			Annotate((AST.loop)expr);
		else if(expr instanceof AST.typcase)
			Annotate((AST.typcase)expr);
		else if(expr instanceof AST.block)
			Annotate((AST.block)expr);
		else if(expr instanceof AST.let)
			Annotate((AST.let)expr);
		else if(expr instanceof AST.new_)
			Annotate((AST.new_)expr);
		else if(expr instanceof AST.plus)
			Annotate((AST.plus)expr);
		else if(expr instanceof AST.sub)
			Annotate((AST.sub)expr);
		else if(expr instanceof AST.mul)
			Annotate((AST.mul)expr);
		else if(expr instanceof AST.divide)
			Annotate((AST.divide)expr);
		else if(expr instanceof AST.comp)
			Annotate((AST.comp)expr);
		else if(expr instanceof AST.lt)
			Annotate((AST.lt)expr);
		else if(expr instanceof AST.leq)
			Annotate((AST.leq)expr);
		else if(expr instanceof AST.eq)
			Annotate((AST.eq)expr);
		else if(expr instanceof AST.neg)
			Annotate((AST.neg)expr);
	}

	private void Annotate(AST.dispatch dispatch){
		AST.method m;
		Annotate(dispatch.caller);
		for(AST.expression expr : dispatch.actuals)	// Annotate all of the actual parameters of the method dispatch.
			Annotate(expr);
		// Error if type of caller is undefined.
		if(!Table.isPresent(dispatch.caller.type))
			reportError(filename, dispatch.lineNo, "Dispatch to undefined class " + dispatch.caller.type);
		else {
			m = Table.getMethod(dispatch.caller.type, dispatch.name); //Gets the dispatch method from classList of BuildTable.java
			if(m != null) {
				dispatch.type = m.typeid;	// If such a method exists, associate its type with dispatch.
				
				//Error if method is called with wrong no. of arguments or arguments of wrong type.
				if(dispatch.actuals.size() != m.formals.size())
					reportError(filename, dispatch.lineNo, "Method " + m.name + " invoked with wrong number of arguments.");
				else {
					for(int i = 0; i < dispatch.actuals.size(); i++) {
						if(!Table.conformsTo(dispatch.actuals.get(i).type, m.formals.get(i).typeid))
							reportError(filename, dispatch.lineNo, "In call of method " + m.name + ", type " + dispatch.actuals.get(i).type + " does not conform to declared type " + m.formals.get(i).typeid);			
					}
				}
				return ;	
			}
			//If no method corresponding to dispatch is found, Error.
			reportError(filename, dispatch.lineNo, "Dispatch to undefined method " + dispatch.name);
		}
		//If type couldn't be associated with the dispatch, default : Object.
		dispatch.type = "Object";
	}

	private void Annotate(AST.static_dispatch static_dispatch) {
		AST.method m;
		Annotate(static_dispatch.caller);				
		
		for(AST.expression expr : static_dispatch.actuals)	// Annotate all of the actual parameters of the static dispatch of method.
			Annotate(expr);
		// Error if typeid of static_dispatch is undefined.
		if(!Table.isPresent(static_dispatch.typeid))
			reportError(filename, static_dispatch.lineNo, "Static dispatch to undefined class " + static_dispatch.typeid);
		// Error if type of caller doesn't conform with static_dispatch.
		else if(!Table.conformsTo(static_dispatch.caller.type, static_dispatch.typeid))
			reportError(filename, static_dispatch.lineNo, "Expression type " + static_dispatch.caller.type + " does not conform to declared static dispatch type " + static_dispatch.typeid);
		
		else {
			m = Table.getMethod(static_dispatch.typeid, static_dispatch.name);  //Gets the method from classList of BuildTable.java
			if(m != null) {
				static_dispatch.type = m.typeid;  // If such a method exists, associate its type with static_dispatch
				
				//Error if method is called with wrong no. of arguments or arguments of wrong type.
				if(static_dispatch.actuals.size() != m.formals.size())
					reportError(filename, static_dispatch.lineNo, "Method " + m.name + " invoked with wrong number of arguments.");
				else {
					for(int i = 0; i < static_dispatch.actuals.size(); i++) {
						if(!Table.conformsTo(static_dispatch.actuals.get(i).type, m.formals.get(i).typeid))
							reportError(filename, static_dispatch.lineNo, "In call of method " + m.name + ", type " + static_dispatch.actuals.get(i).type + " does not conform to declared type " + m.formals.get(i).typeid);			
					}
				}
				return ;	
			}
			//If no method corresponding to dispatch is found, Error.
			reportError(filename, static_dispatch.lineNo, "Static dispatch to undefined method " + static_dispatch.name);
		}
		//If type couldn't be associated with the dispatch, default : Object
		static_dispatch.type = "Object";
	}
	
	private void Annotate(AST.assign assign)
	{
		Annotate(assign.e1);
		AST.attr tmp = scopeTable.lookUpGlobal(assign.name);
		if(tmp == null)
			reportError(filename, assign.lineNo, "Assignment to undeclared variable " + assign.name);
		else if(!Table.conformsTo(assign.e1.type, tmp.typeid))
			reportError(filename, assign.lineNo, "Type " + assign.e1.type + " of assigned expression does not conform to declared type " + tmp.typeid + " of identifier " + tmp.name);

		assign.type = assign.e1.type;
	}
	
	private void Annotate(AST.cond cond){
		Annotate(cond.predicate);
		if(!cond.predicate.type.equals("Bool")){
			reportError(filename, cond.predicate.lineNo, "Predicate of 'if' doesn't have type bool" );
		}
		Annotate(cond.ifbody);
		Annotate(cond.elsebody);
		cond.type = Table.commonAncestor(cond.ifbody.type, cond.elsebody.type);


	}
	private void Annotate(AST.typcase typcase){
		//this is case in cool
		Annotate(typcase.predicate);//first evaluate predicate
		for(AST.branch br : typcase.branches) {
			scopeTable.enterScope();//we enter into new scope
			if(!Table.isPresent(br.type)){//if given branch type is not present throw error else insert type into scopeTable
				reportError(filename, br.lineNo, "Class " + br.type + " of case branch is undefined.");
				scopeTable.insert(br.name, new AST.attr(br.name, "Object", br.value, br.lineNo));	
			}
			else scopeTable.insert(br.name, new AST.attr(br.name, br.type, br.value, br.lineNo));
			Annotate(br.value);
			scopeTable.exitScope();
		}
		if(typcase.branches.size()==0){//if no branch is present 
			reportError(filename, typcase.lineNo, " Case Statement does not have any branch");
			return;
		}
		HashMap <String, Boolean> br_types = new HashMap<String, Boolean> ();//hashmap for storing branch types and then checking if the value is present in O(1) time  
		String typ = typcase.branches.get(0).value.type;
		
		for(AST.branch br : typcase.branches) {//throws error if there are duplicate branches
			if(br_types.containsKey(br.type) == false)
				br_types.put(br.type, true);
			else
				reportError(filename, br.lineNo, "Duplicate branch " + br.type + " in case statement.");
			typ = Table.commonAncestor(typ, br.value.type);//store the common ancestor of all the branches and return it as the type of case.
		}
		typcase.type = typ;
	}

	private void Annotate(AST.loop loop) {
		Annotate(loop.predicate);
		if(!loop.predicate.type.equals("Bool")) {
			reportError(filename, loop.predicate.lineNo, "Loop condition does not have type Bool.");
		}
		Annotate(loop.body);
		loop.type = "Object";
	}

	private void Annotate(AST.object object)
	{
		AST.attr at = scopeTable.lookUpGlobal(object.name);
		if(at == null) {
			reportError(filename, object.lineNo, "Undeclared identifier " + object.name);
			object.type = "Object";
		}
		else
			object.type = at.typeid;
	}

	private void Annotate(AST.block block) {
		for(AST.expression expr : block.l1)
			Annotate(expr);
		block.type = block.l1.get(block.l1.size()-1).type; // Type of the block is the type of its last expression.
	}

	private void Annotate(AST.let let){
		if (!(let.value instanceof AST.no_expr)){
			Annotate(let.value);
			if(!Table.conformsTo(let.value.type, let.typeid))
				reportError(filename, let.lineNo, "Inferred type of " + let.value.type + " of initialization of " + let.name + " in 'let' does not conform to idenitifier's declared type " + let.typeid);
		}
		scopeTable.enterScope();
		scopeTable.insert(let.name, new AST.attr(let.name, let.typeid, let.value, let.lineNo));
		Annotate(let.body);
		let.type = let.body.type;
		scopeTable.exitScope();
	}
	private void Annotate(AST.new_ new_){
		if(!Table.isPresent(new_.typeid)){
			reportError(filename, new_.lineNo, "'new' used with undefined class " + new_.typeid);
			new_.type="Object";
		}
		else 
			new_.type=new_.typeid;
	}
	private void Annotate(AST.isvoid isvoid){
		isvoid.type= "Bool";
	}
	private void Annotate(AST.plus plus){
		Annotate(plus.e1);
		Annotate(plus.e2);
		if(!plus.e1.type.equals("Int") && !plus.e2.type.equals("Int")){
			reportError(filename, plus.lineNo, "non-Int arguments: " + plus.e1.type + " , " + plus.e2.type + " on operator '+'");
		}else if(!plus.e1.type.equals("Int")){
			reportError(filename, plus.lineNo, "non-Int argument: in LHS of type " + plus.e1.type+ " on '+'");
		}else if(!plus.e2.type.equals("Int")){
			reportError(filename, plus.lineNo, "non-Int argument: in RHS of type " + plus.e2.type+ " on '+'");
		}
		plus.type="Int";
	}
	private void Annotate(AST.sub sub){
		Annotate(sub.e1);
		Annotate(sub.e2);
		if(!sub.e1.type.equals("Int") && !sub.e2.type.equals("Int")){
			reportError(filename, sub.lineNo, "non-Int arguments: " + sub.e1.type + " , " + sub.e2.type+ " on operator '-'");
		}else if(!sub.e1.type.equals("Int")){
			reportError(filename, sub.lineNo, "non-Int argument: in LHS of type " + sub.e1.type+ " on operator '-'");
		}else if(!sub.e2.type.equals("Int")){
			reportError(filename, sub.lineNo, "non-Int argument: in RHS of type" + sub.e2.type+ " on operator '-'");
		}
		sub.type="Int";
	}
	private void Annotate(AST.mul mul){
		Annotate(mul.e1);
		Annotate(mul.e2);
		if(!mul.e1.type.equals("Int") && !mul.e2.type.equals("Int")){
			reportError(filename, mul.lineNo, "non-Int arguments: " + mul.e1.type + " , " + mul.e2.type+ " on operator '*'");
		}else if(!mul.e1.type.equals("Int")){
			reportError(filename, mul.lineNo, "non-Int argument: in LHS of type " + mul.e1.type+ " on operator '*'");
		}else if(!mul.e2.type.equals("Int")){
			reportError(filename, mul.lineNo, "non-Int argument: in RHS of type " + mul.e2.type+ " on operator '*'");
		}
		mul.type="Int";
	}
	private void Annotate(AST.divide divide){
		Annotate(divide.e1);
		Annotate(divide.e2);
		if(!divide.e1.type.equals("Int") && !divide.e2.type.equals("Int")){
			reportError(filename, divide.lineNo, "non-Int arguments: " + divide.e1.type + " , " + divide.e2.type+ " on operator '/'");
		}else if(!divide.e1.type.equals("Int")){
			reportError(filename, divide.lineNo, "non-Int argument: in LHS of type" + divide.e1.type+ " on operator '/'");
		}else if(!divide.e2.type.equals("Int")){
			reportError(filename, divide.lineNo, "non-Int argument: in RHS of type" + divide.e2.type+ " on operator '/'");
		}
		divide.type="Int";
	}
	private void Annotate(AST.comp comp){
		Annotate(comp.e1);
		if(!comp.e1.type.equals("Bool"))
			reportError(filename, comp.lineNo, "Argument of 'not' has type " + comp.e1.type + " instead of Bool.");
		comp.type = "Bool";
	}
	private void Annotate(AST.lt lt){
		Annotate(lt.e1);
		Annotate(lt.e2);
		if(!lt.e1.type.equals("Int") && !lt.e1.type.equals("Int")){
			reportError(filename, lt.lineNo, "non-Int arguments: " + lt.e1.type + " , " + lt.e2.type);
		}else if(!lt.e1.type.equals("Int")){
			reportError(filename, lt.lineNo, "non-Int argument: " + lt.e1.type);
		}else if(!lt.e2.type.equals("Int")){
			reportError(filename, lt.lineNo, "non-Int argument: " + lt.e2.type);
		}
		lt.type="Bool";
	}
	private void Annotate(AST.leq leq){
		Annotate(leq.e1);
		Annotate(leq.e2);
		if(!leq.e1.type.equals("Int") && !leq.e1.type.equals("Int")){
			reportError(filename, leq.lineNo, "non-Int arguments: " + leq.e1.type + " , " + leq.e2.type);
		}else if(!leq.e1.type.equals("Int")){
			reportError(filename, leq.lineNo, "non-Int argument: " + leq.e1.type);
		}else if(!leq.e2.type.equals("Int")){
			reportError(filename, leq.lineNo, "non-Int argument: " + leq.e2.type);
		}
		leq.type="Bool";
	}
	private void Annotate(AST.eq eq){
		Annotate(eq.e1);
		Annotate(eq.e2);
		List <String> basic_types = Arrays.asList("String", "Int", "Bool");
		if(basic_types.contains(eq.e1.type) || basic_types.contains(eq.e2.type)) {
			if(!eq.e1.type.equals(eq.e2.type)) {
				reportError(filename, eq.lineNo, "Illegal comparison with a basic type.");
			}
		}
		eq.type = "Bool";
	}
	private void Annotate(AST.neg neg){
		Annotate(neg.e1);
		if(!neg.e1.type.equals("Int"))
			reportError(filename, neg.lineNo, "Argument of '~' has type " + neg.e1.type + " instead of Int");
		neg.type = "Int";
	}
}
