package cool;

import java.util.*;

public class Inheritance_graph{
	private Integer size;

	private Map<AST.class_, Integer> Class_to_Int;

	private Map<Integer, AST.class_> Int_to_Class;

	// private Map<String, AST.class_> Name_to_Class;

	private List< List <Integer> > Graph;

	public Inheritance_graph(){
		Class_to_Int = new HashMap<AST.class_, Integer>;

		Int_to_Class = new HashMap<Integer, AST.class_>;

		// Name_to_Class = new HashMap<String, AST.class_>;

		graph = new ArrayList< ArrayList <Integer> >();

		Class_to_Int.put(classList.get("Object"),0);
		Int_to_Class.put(0,classList.get("Object"));

		Class_to_Int.put(classList.get("IO"),1);
		Int_to_Class.put(1,classList("IO"));

		graph.add(new ArrayList <Integer> (Arrays.asList(1)));
		graph.add(new ArrayList <Integer>);

		size=2;
		for(AST.class_ class_object : AST.program.classes){
			if(class_object.name == "Object" ||class_object.name == "IO" ||class_object.name == "String" ||class_object.name == "Bool" ||class_object.name == "Int"){
				System.err.println(class_object.filename+" : "+class_object.lineNo+" : Class redefinition of basic class "+ class_object.name);
				System.exit(1);
			}else if(class_object.parent == "String" ||class_object.parent == "Bool" ||class_object.parent == "Int"){
				System.err.println(class_object.filename+" : "+class_object.lineNo+" : Class "class_object.name" cannot inherit class "+ class_object.parent);
				System.exit(1);
			}else if(Class_to_Int.containsKey(class_object)){
				System.err.println(class_object.filename+" : "+class_object.lineNo+" : Class " +class_object.name + " was previously defined");
				System.exit(1);
			}else if(!Class_to_Int.containsKey(class_object)){
				Class_to_Int.put(class_object,size);
				Int_to_Class.put(size,class_object);
				size++;
				graph.add(new ArrayList<Integer>());
			}
		}
	}

	public void buildGraph(){
		for(AST.class_ class_object : AST.program.classes){
			if(!Class_to_Int.containsKey(class_object.parent)){
				System.err.println(class_object.filename+" : "+class_object.lineNo+" : Class " +class_object.name + " inherits from an undefined class");
				System.exit(1);
			}
			graph[Class_to_Int(class_object.parent)].add(Class_to_Int(class_object));
		}
	}

	public void isDAG(){
		boolean visited[]= new boolean[size];
		boolean flag=false;
		dfs(0,visited,flag);
		if(flag==true){
			System.exit(1);
		}
	}

	public void dfs(int v, boolean  visited[], boolean flag){
		if(visited[v]==true){
			System.err.println(class_object.filename+" : "+class_object.lineNo+" : Class " +class_object.name + ", or an ancestor of "+ class_object.name+ ", is involved in an inheritance cycle");
			flag=true;
		}
		visited[v]=true;
		for(int i=0;i<graph[v].size();i++){
			int n=graph[v][i];
			if(!visited[n]){
				dfs(n,visited,flag);
			}
		}
	}
}