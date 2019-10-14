package cool;

import java.util.*;

public class Inheritance_graph{
	private Integer size;                          // size it is used to populate the graph and other maps it is has the dynamic size of entered classes

	private Map<AST.class_, Integer> Class_to_Int; // Map for containing hashmap from class to int 

	private Map<Integer, AST.class_> Int_to_Class; // Map for containing hashmap from int to class

	private Map<String, AST.class_> Name_to_Class; // Map for containing hashmap from class name to class

	private ArrayList< ArrayList <Integer> > Graph;//List of Lists to store the child nodes in a graph as we know we have one
												   // common root Object which can be later used for traversing through the graph  

	public Inheritance_graph(List<AST.class_> classes, BuildTable Table){//constructor intializes all the maps and graph 
				 														 // along with this it populates the inheritance graph or we can say the List of Lists 
		Class_to_Int = new HashMap<AST.class_, Integer>();

		Int_to_Class = new HashMap<Integer, AST.class_>();

		Name_to_Class = new HashMap<String, AST.class_>();
		Graph = new ArrayList< ArrayList <Integer> >();

		/*first adding Object and IO in the graph as other classes can inherit from Object and IO and these are predefined
		  alongwith giving them an integer which records the order in which they came in 
		*/
		Class_to_Int.put(Table.getClass("Object"),0); 
		Int_to_Class.put(0,Table.getClass("Object"));
		Name_to_Class.put(Table.getClass("Object").name,Table.getClass("Object"));

		Class_to_Int.put(Table.getClass("IO"),1);
		Int_to_Class.put(1,Table.getClass("IO"));	
		Name_to_Class.put(Table.getClass("IO").name,Table.getClass("IO"));
		
		Graph.add(new ArrayList <Integer> (Arrays.asList(1)));
		Graph.add(new ArrayList <Integer>());

		List <String> no_redef = Arrays.asList("Object", "String", "Int", "Bool", "IO");// According to manual these classes cannot have redefintions
		List <String> no_inherit = Arrays.asList("String", "Int", "Bool");// No class can  inherit from this set of classes
		size=2;
		for(AST.class_ class_object : classes){//first traversal through AST 
			if(no_redef.contains(class_object.name)){//throwing error on redefinition of basic class
				System.err.println(class_object.filename+" : "+class_object.lineNo+" : Class redefinition of basic class "+ class_object.name);
				System.exit(1);
			}else if(no_inherit.contains(class_object.parent)){//throwing error on inheriting from string,int or bool
				System.err.println(class_object.filename+" : "+class_object.lineNo+" : Class "+class_object.name+" cannot inherit class "+ class_object.parent);
				System.exit(1);
			}else if(Name_to_Class.containsKey(class_object.name)){////throwing error on redefinition of a class
				System.err.println(class_object.filename+" : "+class_object.lineNo+" : Class " +class_object.name + " was previously defined");
				System.exit(1);
			}else if(!Name_to_Class.containsKey(class_object.name)){// adding the new class into hashmaps and intializing array for storing child classes
				Class_to_Int.put(class_object,size);
				Int_to_Class.put(size,class_object);
				Name_to_Class.put(class_object.name,class_object);
				size++;
				Graph.add(new ArrayList<Integer>());
			}
		}
	}

	public void buildGraph(List<AST.class_> classes){
		for(AST.class_ class_object : classes){//second traversal through AST. Building inheritance graph needs two traversals because while traversing the first time we don't know which classes will come on later stage
			if(!Name_to_Class.containsKey(class_object.parent)){//if parent is not present raise an error
				System.err.println(class_object.filename+" : "+class_object.lineNo+" : Class " +class_object.name + " inherits from an undefined class " + class_object.parent);
				System.exit(1);
			}
			Graph.get(Class_to_Int.get(Name_to_Class.get(class_object.parent))).add(Class_to_Int.get(class_object));//adding integer which corresponds to child of a class
		}
	}

	public void isDAG(){// this program checks whether their is a cycle involved in the inheritance graph using DFS, if yes it halts the program 
		boolean visited[]= new boolean[size+1];// a flag is stored on the last index of the array it will let us know whether is cycle involved or not
		for(int i=0;i<size;i++){
			if(!visited[i]){
				dfs(i,visited);//calling dfs over all unvisited classes rather than object because their is a possibility of having a cycle none of which have ancestor pointing towards 'Object' this means that part will not be connected to the root(Object) in any way 
			}
		}
		if(visited[size]) System.exit(1);
	}

	public void dfs(int v, boolean  visited[]){// in dfs if we find a back edge then that node is a part of cycle formed in inheritance graph so traversing through 									
		if(visited[v]){			   // nodes via dfs on the node which  we found exists in a cycle we can extract all classes(or their ancestors) that are involved in ac cycle.
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

	public void print_cycle_classes(int v, boolean visited[],ArrayList<String> Cycle){// another dfs function to extract elements involved in a cycle
		visited[v]=true;
		Cycle.add(Int_to_Class.get(v).name);
		// System.err.println(Int_to_Class.get(v).filename+" : "+Int_to_Class.get(v).lineNo+" : Class " +Int_to_Class.get(v).name + ", or an ancestor of "+ Int_to_Class.get(v).name+ ", is involved in an inheritance cycle");
		for(int i=0;i<Graph.get(v).size();i++){
			int n=Graph.get(v).get(i);
			if(!visited[n]) print_cycle_classes(n,visited,Cycle);
		}
	}

	public void insert_classes(BuildTable Table){// using bfs we trverse over the inheritance graph and build a Table which contains data of all the classes thus formed
		Queue<Integer> Q = new LinkedList<>();
		Q.add(0);
		while(!Q.isEmpty()){
			int i=Q.poll();
			// System.err.println(Int_to_Class.get(i).name+" "+ Int_to_Class.get(i).parent);
			if(i>1) Table.insert(Int_to_Class.get(i));//Object and IO are manually added into this table
			for(int j=0;j<Graph.get(i).size();j++){
				Q.add(Graph.get(i).get(j));
			}
		}
	}
}
