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
	public Semantic(AST.program program){
		//Write Semantic analyzer code here

		// initialize classList here
		BuildTable Table = new BuildTable();
		Inheritance_graph inGraph = new Inheritance_graph(program.classes, Table);
		inGraph.buildGraph(program.classes);
		inGraph.isDAG();
		inGraph.insert_classes(Table);

	}
}
