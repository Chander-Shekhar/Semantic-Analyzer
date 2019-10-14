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

## InheritanceGraph.java
For building the inheritance graph, we traverse through the AST twice. Once for putting all the classes in our data structures i.e. we use 3 hashmaps for retrieving data from classes to be put into a Graph (In graphs terminology we say it is an adjacency list) and also we intialize the ArrayList for a graph node at in this traversal. In the second pass we populate the nodes in the graph with integer to the child nodes for a parent node. For checking cycles, we have implemented depth-first search algorithm which calls on every unvisited node. At a point if we get that a node is a member of the cycle we return the names of Strongly connected components. Using this inheritance graph, we populate the classList which is a HashMap<String, AST.class_> by going through the graph in breadth-first manner.

## BuildTable.java
This file mainly maintains a HashMap <String ,AST.class_>.
 The key is name of the class and the value contains all the information of the class.
 Its constructor initializes all the Basic COOL classes i.e. **Object, IO, Bool, String, Int**.
 It has a *insert* function which takes object of type <AST.class_>. It checks for multiple definitions errors of attributes and methods in the given class. Then, it looks into the HashMap for its parent class and put the inherited attributes and methods into it after checking for multiple definition errors.
 It also contains some simple methods to share the information of HashMap with *Semantic.java* for annotating the AST.
 
 ## Semantic.java
 

* The design decisions and code structure are explained thoroughly in the code using comments. *
