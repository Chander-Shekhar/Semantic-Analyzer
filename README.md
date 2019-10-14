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

