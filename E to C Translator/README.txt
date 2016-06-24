Huy Nguyen
Manuel Martinez
Anthony Bell
Kevin Erickson

Part 1: We just simply added in the missing tokens inside Scan.java and Tk.java. We believe that
everything should works.

Part 2: For part 2, we pretty much followed exactly how the assignment's BNF rule 
was described. So each function just calling the others. Everything in part 2 should work perfectly.

Part 3: For part 3, we put our redeclaration check inside the declaration(). Basically, we will just
check whether if the current symbol is already in our symbol table or not. If it is, then we just 
return the error. If not, then we just added the token into our table. For the other type of errors, 
we decided to handle them inside ref_id(). When the current symbol is an ID with a tilde, if it contains number, 
we converted it to integer and check to see whether if this ID is in scope or not. If not, then we 
return an error saying that there is no such variable exists. For the case that an ID without the number, but also
has a tilde, we check it by looking at the global block in our stack to see wheter if the variable has been declared
up there or not. If not, simply return an error. Otherwise, if the ID is just simply a variable without numbers and
tilde, then we have to check if it is in the symbol table or not. If not, return an error saying that the variable
is an undeclared variable. Everything should work for part 3.

Part 4: For part 4, we just simply translate to C by priting the commands out. The hardest part of implementation for
part 4 is when dealing with the variable's scope. Basically, we have to somehow bind an ID with its scope so the C 
compiler will know which variable are we focusing on. For the case that an ID with a tilde and a number, we have to 
look for its scope by writing a small function and passing in the integer that we converted from ref_id(). Inside this small 
function, we are going to check whether if the current scope is greater than or equal to the scope that we passed in or not. 
If it is then we going to return the current scope minus the passed in scope. Else, just simply return a random negative number.
For the other case, where there is an ID without the tilde, we have to look where this ID is located inside the symbol table by
moving backward in our symbol table and look for it. The location of the token is actually its scope. After all that, we just simply
print out the ID tagging along with its scope. Everything should work for part 4.

Part 5 BNF:

program ::= block
block ::= declaration_list_statement_list
declareation_list ::= {declaration}
statement_list ::= {statement}

declaration ::= '@' id {',' id}

statement ::= assignment | print | do | if | for
print ::= '!' expr
assignment ::= ref_id '=' expr
ref_id ::= [ '~' [ number ] ] id
do ::= '<' guarded_command '>'
if ::= '[' guarded_command { '|' guarded_command } [ '%' block ] ']'
for ::= '$' expr block '}'
guarded_command ::= expr ':' block

expr ::= term { addop term }
term ::= factor { multop factor }
factor ::= '(' expr ')' | ref_id | number | '=' | '<'
addop ::= '+' | '-'
multop ::= '*' | '/'

Implementation: Basically, what I did was to add another non-terminal into the statement_list function. 
Then, for the case 'for', I just simply call expr() and block (). I modified the factor() function a bit
by adding two more cases, assign and less. Also, i put in a recursive call to expr() in each of the 
cases inside factor(). This way of implementation will reduce a lot of work for the for loop, and it is really
efficient by just doing recursive call over and over again until it hit the closing loop symbol.

