/* *** This file is given as part of the programming assignment. *** */
import java.util.*;
public class Parser {


    // tok is global to all these parsing methods;
    // scan just calls the scanner's scan method and saves the result in tok.
    private Token tok; // the current token
    private void scan() {
	tok = scanner.scan();
    }

    private Scan scanner;
    Parser(Scan scanner) {
	this.scanner = scanner;
	scan();
	program();
	if( tok.kind != TK.EOF )
	    parse_error("junk after logical end of program");
    }
    private ArrayList<ArrayList<String>> stack = new ArrayList<ArrayList<String>>();
    private ArrayList<String> symbol;
    boolean minus_flag = false;
    boolean comma_flag = false;
 
    private void program() {
	System.out.print("#include <stdio.h>\n");
	System.out.print("int main()\n{\n");
	block();
	System.out.print("}\n");
    }

    private boolean inTable(String name){
        int check = 0;
        for(int i = 0; i < stack.size(); i++){
            check = stack.get(i).indexOf(name);
            if(check != -1){
            	return true;
            }
        }
        return false;
    }

    private int scope_detect(int scope){
        int current = stack.size() - 1;
        if(current >= scope)
        	return (current - scope);
        else
            	return -20;
        
    }

    private int tok_location(String name){
        int check = 0;
        for(int i = (stack.size() - 1); i >= 0; i--){
            	check = stack.get(i).indexOf(name);
            	if(check != -1)
            		return i;         	
        }
        return -20;
    }

    
    private boolean inScope(int scope){
        int current = (stack.size() - 1) - scope;
        int value = 0;
        if(current < 0)
            	return false;
        else{
            value = stack.get(current).indexOf(tok.string);
            if(value == -1)
            	    return false;    
            else
            	    return true;
        }
    }

    private int check_current(String name){
	int check;
	check = stack.get(stack.size()-1).indexOf(name);
	return check;
    }

    private void block(){
	symbol = new ArrayList<String>();
	stack.add(symbol);
	declaration_list();
	statement_list();
	stack.remove(stack.size()-1);
    }

    private void declaration_list() {
	// below checks whether tok is in first set of declaration.
	// here, that's easy since there's only one token kind in the set.
	// in other places, though, there might be more.
	// so, you might want to write a general function to handle that.
	while( is(TK.DECLARE) ) {
	    declaration();
	}
    }

    private void declaration() {
	mustbe(TK.DECLARE);
 	int redec_check = check_current(tok.string);
	if (redec_check != -1){
		System.err.println("redeclaration of variable " + tok.string);
	}
	else if (redec_check == -1){
		symbol.add(tok.string);
		System.out.print("int x_"+tok.string+"_"+(stack.size()-1));
		
 	}
	mustbe(TK.ID);
        
	while( is(TK.COMMA) ) {
	    mustbe(TK.COMMA);
	    comma_flag = true;
	    int redec_check2 = check_current(tok.string);
	    if( redec_check2 != -1){
		System.err.println("redeclaration of variable " + tok.string);
	    }
 	    else if( redec_check2 == -1){
		symbol.add(tok.string);
		if(redec_check != -1){
			System.out.print("int x_"+tok.string+"_"+(stack.size()-1));
		}
	    	else{ 
			System.out.print(", ");
			System.out.print("x_"+tok.string+"_"+(stack.size()-1));
		}
	    }
	    mustbe(TK.ID);
	}
	if( tok.kind != TK.COMMA && comma_flag ==false)
		System.out.print(";\n");
	else
		System.out.print(";\n");
	comma_flag = false;
	
    }

    //EDITTING PART!!!
    private void statement_list() {
     	 while( is(TK.ID) || is(TK.TILDE) || is(TK.PRINT) || is(TK.DO) || is(TK.IF)){
		statement();
     	 }
    }

    private void statement(){
	if( is(TK.PRINT)){
		mustbe(TK.PRINT);
		System.out.print("printf(\"%d\\n\", ");
		expr();
		System.out.print(");\n");
	    
	}
	
	else if( is(TK.ID) || is(TK.TILDE)){
		ref_id();
		System.out.print(" = ");
		mustbe(TK.ASSIGN);
 		expr();
		System.out.print(";\n");
	}
	
	else if( is(TK.DO)){
		System.out.print("while(");
		mustbe(TK.DO);
		guarded_command();
		mustbe(TK.ENDDO);
	}

	else if( is(TK.IF)){
		System.out.print("if(");
		mustbe(TK.IF);
		guarded_command();
		
		while( is(TK.ELSEIF)){
			System.out.print("else if(");
			mustbe(TK.ELSEIF);
			guarded_command();	
		}
		if( is(TK.ELSE)){
			System.out.print("else\n{\n");
			mustbe(TK.ELSE);
			block();
			System.out.print("}\n");
		}
		mustbe(TK.ENDIF);
	}	
    }
	
    private void guarded_command() {
	expr();
	if(minus_flag ==true)
		System.out.print(" <= 0)\n{\n");
	else
		System.out.print(" == 0)\n{\n");
	mustbe(TK.THEN);
	block();
	System.out.print("}\n");
    }

    private void ref_id() {

        int scope = 0;
        int detect = 0;

        if( is(TK.TILDE)){
            mustbe(TK.TILDE);
            detect = 0;

            if( is(TK.NUM)){

                scope = Integer.parseInt(tok.string);
                detect = scope_detect(scope);
                mustbe(TK.NUM);
                if(!inScope(scope)){
                    System.err.println("no such variable ~" + scope
                        + tok.string + " on line " + tok.lineNumber);
                    System.exit(1);
                }

            }
            else{
                if(!inScope(stack.size() - 1)){
                    System.err.println("no such variable ~"
                        + tok.string + " on line " + tok.lineNumber);
                    System.exit(1);
                }
            }
        }
        else{
            detect = tok_location(tok.string);
        }
 
        if(!inTable(tok.string)){
            System.err.println(tok.string + " is an undeclared variable on line "
                                + tok.lineNumber);
            System.exit(1);
        }
        else{

        }
        System.out.print("x_" + tok.string + "_" + detect );
        mustbe(TK.ID);

    }

    private void expr() {
	term();
	while( is(TK.PLUS) || is(TK.MINUS)){
		addop();
		term();
	}
    }
	
    private void term() {
	factor();
	while( is(TK.TIMES) || is(TK.DIVIDE)){
		multop();
		factor();
	}
    }

    private void factor() {
	if( is(TK.LPAREN)){
		System.out.print("(");
		mustbe(TK.LPAREN);
		expr();
		
		if( is(TK.RPAREN)){
			System.out.print(")");
			mustbe(TK.RPAREN);
		}
	}
	else if( is(TK.ID) || is(TK.TILDE)){
		ref_id();
	}
	else if( is(TK.NUM)){
		System.out.print(tok.string);
		mustbe(TK.NUM);
	}
    }

    private void addop() {
	if( is(TK.PLUS)){
		System.out.print(" + ");
		mustbe(TK.PLUS);
	}
	else if( is(TK.MINUS)){
		minus_flag =true;
		System.out.print(" - ");
		mustbe(TK.MINUS);
	}
    }

    private void multop() {
	if( is(TK.TIMES)){
		System.out.print(" * ");
		mustbe(TK.TIMES);
	}
	else if( is(TK.DIVIDE)){
		System.out.print(" / ");
		mustbe(TK.DIVIDE);
	}
    }

	
    // is current token what we want?
    private boolean is(TK tk) {
        return tk == tok.kind;
    }

    // ensure current token is tk and skip over it.
    private void mustbe(TK tk) {
	if( tok.kind != tk ) {
	    System.err.println( "mustbe: want " + tk + ", got " +
				    tok);
	    parse_error( "missing token (mustbe)" );
	}
	scan();
    }

    private void parse_error(String msg) {
	System.err.println( "can't parse: line "
			    + tok.lineNumber + " " + msg );
	System.exit(1);
    }
}
