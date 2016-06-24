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

    private void program() {
	block();
    }

    private boolean inTable(String name){
        int check = 0;
        for(int i = 0; i < stack.size(); i++){
            check = stack.get(i).indexOf(name);
            if(check != -1)
                return true;
        }
        return false;
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
	//System.out.print("LEVEL: "+ scope_level+"\n");
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
		
 	}
	mustbe(TK.ID);
        
	while( is(TK.COMMA) ) {
	    mustbe(TK.COMMA);
	    int redec_check2 = check_current(tok.string);
	    if( redec_check2 != -1){
		System.err.println("redeclaration of variable " + tok.string);
	    }
 	    else if( redec_check2 == -1){
		symbol.add(tok.string);
	    }
	    mustbe(TK.ID);
	}
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
		expr();
	}
	
	else if( is(TK.ID) || is(TK.TILDE)){
		ref_id();;
		mustbe(TK.ASSIGN);
 		expr();
	}
	
	else if( is(TK.DO)){
		mustbe(TK.DO);
		guarded_command();
		mustbe(TK.ENDDO);
	}

	else if( is(TK.IF)){
		mustbe(TK.IF);
		guarded_command();
		
		while( is(TK.ELSEIF)){
			mustbe(TK.ELSEIF);
			guarded_command();	
		}
		if( is(TK.ELSE)){
			mustbe(TK.ELSE);
			block();
		}
		mustbe(TK.ENDIF);
	}	
    }
	
    private void guarded_command() {
	expr();
	mustbe(TK.THEN);
	block();
    }

    private void ref_id() {

        int scope = 0;

        if( is(TK.TILDE)){
            mustbe(TK.TILDE);

            if( is(TK.NUM)){

                scope = Integer.parseInt(tok.string);
                mustbe(TK.NUM);
                if(!inScope(scope)){
                    System.err.println("no such variable ~" + scope + tok.string + " on line " + tok.lineNumber);
                    System.exit(1);
                }

            }
            else{
                if(!inScope(stack.size() - 1)){
                    System.err.println("no such variable ~" + tok.string + " on line " + tok.lineNumber);
                    System.exit(1);
                }
            }
        }
        if(!inTable(tok.string)){
            System.err.println(tok.string + " is an undeclared variable on line " + tok.lineNumber);
            System.exit(1);
        }
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
		mustbe(TK.LPAREN);
		expr();
		
		if( is(TK.RPAREN)){
			mustbe(TK.RPAREN);
		}
	}
	else if( is(TK.ID) || is(TK.TILDE)){
		ref_id();
	}
	else if( is(TK.NUM)){
		mustbe(TK.NUM);
	}
    }

    private void addop() {
	if( is(TK.PLUS)){
		mustbe(TK.PLUS);
	}
	else if( is(TK.MINUS)){
		mustbe(TK.MINUS);
	}
    }

    private void multop() {
	if( is(TK.TIMES)){
		mustbe(TK.TIMES);
	}
	else if( is(TK.DIVIDE)){
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
