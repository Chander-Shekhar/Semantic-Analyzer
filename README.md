## Task 

1. Implement the static semantics of Cool.
2. It should reject erronous programs.
3. For correct programs, it should annotate the AST with correct types.

## Basic Overview 

First of all, we build the Inheritance Graph for classes.
Then, we check the graph for cycles.
If cycles are found, we print appropriate errors and exit.
Else, we populate a table for containing useful information of all classes.
Now, we traverse the AST using the information in the table.
We check for type and other semantic errors.
If found, we print useful error messages and attempt to recover using predefined generic types.
At the end, we output type annotated AST for correct programs and some useful errors for incorrect programs.

## File Structure- 
The file structure is as follows:

-**AST.java**

This files contains nodes definitions for AST. No changes have been made to this file at all.
	
-**BuildTable.java**			
	
This file mainly maintains a HashMap <String ,AST.class_> and contains some useful functions to retrieve information from this HashMap at the time of annotating the AST.

-**Inheritance_graph.java**

Builds the inheritance graph for classes and check for cycles. Also insert the classes into the classList.

-**ScopeTable.java**

We added two methods to this file namely *getScope()* and *getMap()* which are used to populate the ScopeTable with attributes of a given class.

-**Semantic.java**

It annotates all the identifiers with appropriate types and calls functions from *Inheritance_graph.java* to check for cycles in the inheritance graph.

## Inheritance_graph.java
For building the inheritance graph, we traverse through the AST twice. Once for putting all the classes in our data structures i.e. three hashmaps: two of them are for mapping class to integer and vice versa, and one for retrieving class if given the name of class in constant time. Graph is stored as an Adjacency List of Integers corresponding to classes. In the second pass, for a given class we add nodes corresponding to child classes in the adjacency list. For checking cycles, we have implemented depth-first search algorithm which calls on every unvisited node. At a point if we find a back edge, we return the names of the nodes that are reachable from that particular node. Using this inheritance graph, we populate the classList which is a HashMap<String, AST.class_> from *BuildTable.java* by going through the graph in breadth-first manner.

## BuildTable.java
This file mainly maintains a HashMap <String ,AST.class_>.
 The key is name of the class and the value contains all the information of the class.
 Its constructor initializes all the Basic COOL classes i.e. **Object, IO, Bool, String, Int**.
 It has a *insert* function which takes object of type <AST.class_>. It checks for multiple definitions errors of attributes and methods in the given class. Then, it looks into the HashMap for its parent class and put the inherited attributes and methods into it after checking for multiple definition errors.
 It also contains some simple methods to share the information of HashMap with *Semantic.java* for annotating the AST.
 
 ## Semantic.java
 This is the main file for this Semantic Analyzer.
- Its constructor calls appropriate functions from **Inheritance_graph.java** to build the Inheritance Graph, check for cycles and populate the HashMap containing information of classes.
- The next task is to annotate the AST with correct types and check for type errors.
- For every class in the AST, it creates a new scope in the scopetable, put all of the attributes of this class into the scopeTable and calls the annotate function which uses Divide and Conquer approach to annotate the corresponding attributes and methods which eventually breaks into expressions.
- It annotates the identifiers in bottom-up manner. This helps to annotate the bigger block. For example - To annotate a cond, we annotate the ifbody and elsebody seperately. Check if the predicate is bool. The lowest common ancestor of ifbody.type and elsebody.type is used as cond.type.

*The design decisions and code structure are explained thoroughly in the code using comments.*
