## Task -

	Implement the static semantics of Cool.
	It should reject erronous programs.
	For correct programs, it should annotate the AST with correct types.

## Basic Overview- 

	First of all, we build the Inheritance Graph for classes.
	Then, we check the graph for cycles.
	If cycles are found, we print appropriate errors and exit.
	Else, we populate a table for containing useful information of all classes.
	Now, we traverse the AST using the information in the table.
	We check for type and other semantic errors.
	If found, we print useful error messages and attempt to recover using predefined generic types.
	At the end, we output type annotated AST for correct programs and some useful errors for incorrect programs.

## Inheritance Graph-
	



## File Structure- 
	The file structure is as follows:

	1. AST.java - 
	2. 
