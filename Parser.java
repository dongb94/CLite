import java.util.*;

public class Parser {
    // Recursive descent parser that inputs a C++Lite program and 
    // generates its abstract syntax.  Each method corresponds to
    // a concrete syntax grammar rule, which appears as a comment
    // at the beginning of the method.
  
    Token token;          // current token from the input stream
    Lexer lexer;
  
    public Parser(Lexer ts) { // Open the C++Lite source program
        lexer = ts;                          // as a token stream, and
        token = lexer.next();            // retrieve its first Token
    }
  
    private String match (TokenType t) {
        String value = token.value();
        if (token.type().equals(t))
            token = lexer.next();
        else
            error(t);
        return value;
    }
  
    private void error(TokenType tok) {
        System.err.println("Syntax error: expecting: " + tok 
                           + "; saw: " + token);
        System.exit(1);
    }
  
    private void error(String tok) {
        System.err.println("Syntax error: expecting: " + tok 
                           + "; saw: " + token);
        System.exit(1);
    }
  
    public Program program() {
        // Program --> void main ( ) '{' Declarations Statements '}'
        TokenType[ ] header = {TokenType.Int, TokenType.Main,
                          TokenType.LeftParen, TokenType.RightParen};
        for (int i=0; i<header.length; i++)   // bypass "int main ( )"
            match(header[i]);
        match(TokenType.LeftBrace);
        // student exercise
        Declarations D = declarations ();
        Block B = statements();
        
        Program P = new Program(D,B);
        // student exercise*/
        match(TokenType.RightBrace);
        return P;  // student exercise
    }
  
    private Declarations declarations () {
        // Declarations --> { Declaration }
    	Declarations D = new Declarations();
    	while(isType()){
    	 	declaration(D);
    	}
        return D;  // student exercise
    }
  
    private void declaration (Declarations ds) {
        // Declaration  --> Type Identifier { , Identifier } ;
    	Type t = type();
    	String var = match(TokenType.Identifier);
    	ds.add(new Declaration(new Variable(var),t));
    	while(token.type().equals(TokenType.Comma)) {
    		token = lexer.next();
    		var = match(TokenType.Identifier);
    		ds.add(new Declaration(new Variable(var),t));
    	}
    	match(TokenType.Semicolon);

        // student exercise
    }
  
    private Type type () {
        // Type  -->  int | bool | float | char 
        Type t = null;
        if(token.type().equals(TokenType.Int)) {
        	token = lexer.next();
        	t = Type.INT;
        }else if(token.type().equals(TokenType.Bool)) {
        	token = lexer.next();
        	t = Type.BOOL;
        }else if(token.type().equals(TokenType.Char)) {
        	token = lexer.next();
        	t = Type.CHAR;
        }else if(token.type().equals(TokenType.Float)) {
        	token = lexer.next();
        	t = Type.FLOAT;
        }else {
        	error("Type");
        }
        // student exercise
        return t;          
    }

    private Block statements () {
        // statements --> { Statement }
        Block b = new Block();
        while(!token.type().equals(TokenType.RightBrace))
        	b.members.add(statement());
        // student exercise
        return b;
    }
    
    private Statement statement() {
        // Statement --> ; | Block | Assignment | IfStatement | WhileStatement
        Statement s = new Skip();
        if(token.type().equals(TokenType.Semicolon)) {
        	token = lexer.next();
        	return s;
        }else if(token.type().equals(TokenType.LeftBrace)) {
        	s = block();
        }else if(token.type().equals(TokenType.Identifier)) {
        	s = assignment();
        }else if(token.type().equals(TokenType.If)) {
        	s = ifStatement();
        }else if(token.type().equals(TokenType.While)) {
        	s = whileStatement();
        }else {
        	error("statement");
        }
        // student exercise
        return s;
    }
    
    private Block block () {
        // Block --> '{' Statements '}'
        Block b = new Block();
        match(TokenType.LeftBrace);
        while(!token.type().equals(TokenType.RightBrace))
        	b.members.add(statement());
        match(TokenType.RightBrace);
        // student exercise
        return b;
    }
  
    private Assignment assignment () {
        // Assignment --> Identifier = Expression ;
    	Variable t = new Variable(match(TokenType.Identifier));
    	match(TokenType.Assign);
    	Expression e = expression();
    	match(TokenType.Semicolon);
    	
        return new Assignment(t,e);  // student exercise
    }
  
    private Conditional ifStatement () {
        // IfStatement --> if ( Expression ) Statement [ else Statement ]
    	match(TokenType.If);
    	match(TokenType.LeftParen);
    	Expression t = expression();
    	match(TokenType.RightParen);
    	Statement tp = statement();
    	if(token.type().equals(TokenType.Else)) {
    		token = lexer.next();
    		Statement ep = statement();
    		return new Conditional(t,tp,ep);
    	}
        return new Conditional(t,tp);  // student exercise
    }
  
    private Loop whileStatement () {
        // WhileStatement --> while ( Expression ) Statement
    	match(TokenType.While);
    	match(TokenType.LeftParen);
    	Expression t = expression();
    	match(TokenType.RightParen);
    	Statement b = statement();
    	
        return new Loop(t,b);  // student exercise
    }

    private Expression expression () {
        // Expression --> Conjunction { || Conjunction }
    	Expression e = conjunction();
    	while(token.type().equals(TokenType.Or)) {
    		Operator op = new Operator(match(token.type()));
    		Expression e2 = conjunction();
    		e = new Binary(op, e, e2);
    	}
        return e;  // student exercise
    }
  
    private Expression conjunction () {
        // Conjunction --> Equality { && Equality }
    	Expression e = equality();
    	while(token.type().equals(TokenType.And)) {
    		Operator op = new Operator(match(token.type()));
    		Expression e2 = equality();
    		e = new Binary(op, e, e2);
    	}
        return e;  // student exercise
    }
  
    private Expression equality () {
        // Equality --> Relation [ EquOp Relation ]
    	Expression e = relation();
    	if(isEqualityOp()) {
    		Operator op = new Operator(match(token.type()));
    		Expression e2 = relation();
    		e = new Binary(op, e, e2);
    	}
        return e;  // student exercise
    }

    private Expression relation (){
        // Relation --> Addition [RelOp Addition] 
    	Expression e = addition();
    	if(isRelationalOp()) {
    		Operator op = new Operator(match(token.type()));
    		Expression e2 = relation();
    		e = new Binary(op, e, e2);
    	}
        return e;  // student exercise
    }
  
    private Expression addition () {
        // Addition --> Term { AddOp Term }
        Expression e = term();
        while (isAddOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term2 = term();
            e = new Binary(op, e, term2);
        }
        return e;
    }
  
    private Expression term () {
        // Term --> Factor { MultiplyOp Factor }
        Expression e = factor();
        while (isMultiplyOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term2 = factor();
            e = new Binary(op, e, term2);
        }
        return e;
    }
  
    private Expression factor() {
        // Factor --> [ UnaryOp ] Primary 
        if (isUnaryOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term = primary();
            return new Unary(op, term);
        }
        else return primary();
    }
  
    private Expression primary () {
        // Primary --> Identifier | Literal | ( Expression )
        //             | Type ( Expression )
        Expression e = null;
        if (token.type().equals(TokenType.Identifier)) {
            e = new Variable(match(TokenType.Identifier));
        } else if (isLiteral()) {
            e = literal();
        } else if (token.type().equals(TokenType.LeftParen)) {
            token = lexer.next();
            e = expression();       
            match(TokenType.RightParen);
        } else if (isType( )) {
            Operator op = new Operator(match(token.type()));
            match(TokenType.LeftParen);
            Expression term = expression();
            match(TokenType.RightParen);
            e = new Unary(op, term);
        } else error("Identifier | Literal | ( | Type");
        return e;
    }

    private Value literal( ) {
    	Value v = null;
    	if(token.type().equals(TokenType.IntLiteral)) {
    		v = new IntValue(Integer.parseInt(match(TokenType.IntLiteral)));
    	}else if(token.type().equals(TokenType.FloatLiteral)) {
    		v = new FloatValue(Float.parseFloat(match(TokenType.FloatLiteral)));
    	}else if(token.type().equals(TokenType.CharLiteral)) {
    		v = new CharValue(match(TokenType.CharLiteral).charAt(0));
    	}else if(isBooleanLiteral()) {
    		if(token.type().equals(TokenType.False)) {
    			v = new BoolValue(Boolean.parseBoolean(match(TokenType.False)));
    		}
    		else{
    			v = new BoolValue(Boolean.parseBoolean(match(TokenType.True)));
    		}
    	}else {
    		error("literal");
    	}
        return v;  // student exercise
    }
  

    private boolean isAddOp( ) {
        return token.type().equals(TokenType.Plus) ||
               token.type().equals(TokenType.Minus);
    }
    
    private boolean isMultiplyOp( ) {
        return token.type().equals(TokenType.Multiply) ||
               token.type().equals(TokenType.Divide);
    }
    
    private boolean isUnaryOp( ) {
        return token.type().equals(TokenType.Not) ||
               token.type().equals(TokenType.Minus);
    }
    
    private boolean isEqualityOp( ) {
        return token.type().equals(TokenType.Equals) ||
            token.type().equals(TokenType.NotEqual);
    }
    
    private boolean isRelationalOp( ) {
        return token.type().equals(TokenType.Less) ||
               token.type().equals(TokenType.LessEqual) || 
               token.type().equals(TokenType.Greater) ||
               token.type().equals(TokenType.GreaterEqual);
    }
    
    private boolean isType( ) {
        return token.type().equals(TokenType.Int)
            || token.type().equals(TokenType.Bool) 
            || token.type().equals(TokenType.Float)
            || token.type().equals(TokenType.Char);
    }
    
    private boolean isLiteral( ) {
        return token.type().equals(TokenType.IntLiteral) ||
            isBooleanLiteral() ||
            token.type().equals(TokenType.FloatLiteral) ||
            token.type().equals(TokenType.CharLiteral);
    }
    
    private boolean isBooleanLiteral( ) {
        return token.type().equals(TokenType.True) ||
            token.type().equals(TokenType.False);
    }
    
    public static void main(String args[]) {
        Parser parser  = new Parser(new Lexer(args[0]));
        Program prog = parser.program();
        prog.display();           // display abstract syntax tree
    } //main

} // Parser
