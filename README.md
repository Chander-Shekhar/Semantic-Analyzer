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

## Inheritance Graph
For building the inheritance graph we traverse through the tree two times once for putting all the classes in our data structure that is we use 3 hashmaps for retrieving data from classes to be put into a Graph(In graphs terminology we say it is a adjacency list) and also we intialize the ArrayList for a graph node at in this traversal. In the second pass we populate the nodes in the graph with integer to the child nodes for a parent node. For checking cycles we have implemented depth-first search algorithm which calls on every unvisited node. At a point if we get that a node is a member of the cycle we return the names of Strongly connected components. Using this inheritance graph we populate the classList by going through the graph in a breadth-first search manner.




## File Structure- 
The file structure is as follows:

- **AST.java**

	This files contains nodes definitions for AST.
	No changes have been made to this file at all.
			
- **BuildTable.java**

	This file mainly maintains a HashMap <String ,AST.class_>.
	The key is name of the class and the value contains all the information of the class as given in the AST.
	In addition to that, its features list also contains the attributes and methods of the class that it inherites from.
	At the time of insertion of a class into this HashMap, it checks for multiple definitions of attributes and methods and 	return proper errors.

