package cool;

import java.util.*;

public class Inheritance_graph{
	private Integer size;

	private Map<AST.class_, Integer> Class_to_Int;

	private Map<Integer, AST.class_> Int_to_Class;

	private Map<String, AST.class_> Name_to_Class;

	private ArrayList< ArrayList <Integer> > Graph;

	public Inheritance_graph(List<AST.class_> classes, BuildTable Table){
		Class_to_Int = new HashMap<AST.class_, Integer>();

		Int_to_Class = new HashMap<Integer, AST.class_>();

		Name_to_Class = new HashMap<String, AST.class_>();
		List <String> no_redef = Arrays.asList("Object", "String", "Int", "Bool", "IO");
		List <String> no_inherit = Arrays.asList("String", "Int", "Bool");
		Graph = new ArrayList< ArrayList <Integer> >();

		Class_to_Int.put(Table.getClass("Object"),0);
		Int_to_Class.put(0,Table.getClass("Object"));
		Name_to_Class.put(Table.getClass("Object").name,Table.getClass("Object"));

		Class_to_Int.put(Table.getClass("IO"),1);
		Int_to_Class.put(1,Table.getClass("IO"));	
		Name_to_Class.put(Table.getClass("IO").name,Table.getClass("IO"));
		
		Graph.add(new ArrayList <Integer> (Arrays.asList(1)));
		Graph.add(new ArrayList <Integer>());

		size=2;
		for(AST.class_ class_object : classes){
			if(no_redef.contains(class_object.name)){
				System.err.println(class_object.filename+" : "+class_object.lineNo+" : Class redefinition of basic class "+ class_object.name);
				System.exit(1);
			}else if(no_inherit.contains(class_object.parent)){
				System.err.println(class_object.filename+" : "+class_object.lineNo+" : Class "+class_object.name+" cannot inherit class "+ class_object.parent);
				System.exit(1);
			}else if(Name_to_Class.containsKey(class_object.name)){
				System.err.println(class_object.filename+" : "+class_object.lineNo+" : Class " +class_object.name + " was previously defined");
				System.exit(1);
			}else if(!Name_to_Class.containsKey(class_object.name)){
				Class_to_Int.put(class_object,size);
				Int_to_Class.put(size,class_object);
				Name_to_Class.put(class_object.name,class_object);
				size++;
				Graph.add(new ArrayList<Integer>());
			}
		}
	}

	public void buildGraph(List<AST.class_> classes){
		for(AST.class_ class_object : classes){
			if(!Name_to_Class.containsKey(class_object.parent)){
				System.err.println(class_object.filename+" : "+class_object.lineNo+" : Class " +class_object.name + " inherits from an undefined class " + class_object.parent);
				System.exit(1);
			}
			Graph.get(Class_to_Int.get(Name_to_Class.get(class_object.parent))).add(Class_to_Int.get(class_object));
		}
	}

	public void isDAG(){
		boolean visited[]= new boolean[size+1];
		for(int i=0;i<size;i++){
			if(!visited[i]){
				dfs(i,visited);
			}
		}
		if(visited[size]) System.exit(1);
	}

	public void dfs(int v, boolean  visited[]){
		if(visited[v]){
			visited[size]=true;
			boolean visit[]= new boolean[size];
			ArrayList<String> Cycle = new ArrayList<String>();
			print_cycle_classes(v,visit,Cycle);
			System.err.println(Int_to_Class.get(v).filename+" : "+Int_to_Class.get(v).lineNo+" :Classes "+ Cycle+" or their ancestors are a part of a cycle");
			// System.err.println(Int_to_Class.get(v).filename+" : "+Int_to_Class.get(v).lineNo+" : Class " +Int_to_Class.get(v).name + ", or an ancestor of "+ Int_to_Class.get(v).name+ ", is involved in an inheritance cycle");
			return ;
		}
		visited[v]=true;
		for(int i=0;i<Graph.get(v).size();i++){
			int n=Graph.get(v).get(i);
			dfs(n,visited);
		}
	}

	public void print_cycle_classes(int v, boolean visited[],ArrayList<String> Cycle){
		visited[v]=true;
		Cycle.add(Int_to_Class.get(v).name);
		// System.err.println(Int_to_Class.get(v).filename+" : "+Int_to_Class.get(v).lineNo+" : Class " +Int_to_Class.get(v).name + ", or an ancestor of "+ Int_to_Class.get(v).name+ ", is involved in an inheritance cycle");
		for(int i=0;i<Graph.get(v).size();i++){
			int n=Graph.get(v).get(i);
			if(!visited[n]) print_cycle_classes(n,visited,Cycle);
		}
	}

	public void insert_classes(BuildTable Table){
		Queue<Integer> Q = new LinkedList<>();
		Q.add(0);
		while(!Q.isEmpty()){
			int i=Q.poll();
			// System.err.println(Int_to_Class.get(i).name+" "+ Int_to_Class.get(i).parent);
			if(i>1) Table.insert(Int_to_Class.get(i));
			for(int j=0;j<Graph.get(i).size();j++){
				Q.add(Graph.get(i).get(j));
			}
		}
	}
}