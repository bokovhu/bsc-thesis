grammar SurfaceLang;

@header {
    package me.bokov.bsc.surfaceviewer.surfacelang;
}

surfaceSpecification :
    topLevelStatement+
    ;

/// functionDeclarationStatement: sdf3d sphere(float radius): P -> { return length(P) - radius; }
/// constantDeclarationStatement: const float MAX_RADIUS = 0.5;

topLevelStatement :
    surfaceDeclarationStatement
    | functionDeclarationStatement
    | constantDeclarationStatement
    ;

constantDeclarationStatement :
    KW_CONST constantType constantName EQUALS singleExpression
    ;

constantType :
    IDENTIFIER
    ;

constantName :
    IDENTIFIER
    ;

functionDeclarationStatement :
    typeName functionName functionParameterList functionBody
    ;

surfaceDeclarationStatement :
    typeName functionName functionParameterList COLON surfacePointVariable ARROW functionBody
    ;

typeName :
    IDENTIFIER
    ;
functionName :
    IDENTIFIER
    ;

functionParameterList :
    LPAREN (singleFunctionParameter (COMMA singleFunctionParameter)*)? RPAREN
    ;

/// Eg. float radius
singleFunctionParameter :
    functionParameterType functionParameterName
    ;


surfacePointVariable : IDENTIFIER
    ;

functionParameterType : IDENTIFIER;
functionParameterName : IDENTIFIER;

/// {
/// statement1;
/// statement2;
/// ...
/// }
functionBody :
    BLOCK_START blockStatements BLOCK_END
    ;

blockStatements :
    (singleStatement SEMICOLON)*
    ;

/// variableDeclarationStatement: float a
/// variableDeclarationStatement: float a = 1.0
/// assignmentStatement: a = 1.0
/// returnStatement: return 500
/// functionCallStatement: sphere[P](1.0)
singleStatement :
    returnStatement
    | variableDeclarationStatement
    | assignmentStatement
    | functionCallStatement
    | surfaceCallStatement
    ;

assignmentStatement :
    variableName assignmentOperator singleExpression
    ;

assignmentOperator :
    EQUALS
    | OP_ASSIGN
    ;

returnStatement :
    KW_RETURN singleExpression
    ;

functionCallStatement :
    functionCallAtom
    ;

surfaceCallStatement :
    surfaceCallAtom
    ;

variableDeclarationStatement :
    variableTypeName variableName (variableInitialValueAssignment)?
    ;

variableInitialValueAssignment :
    EQUALS singleExpression
    ;

variableTypeName :
    IDENTIFIER
    ;

variableName :
    IDENTIFIER
    ;

singleExpression :
    expressionAtom (expressionOperator expressionAtom)*
    ;

expressionOperator :
    ADD | SUB | MUL | DIV | MOD | POW
    ;

expressionAtom :
    numberLiteral
    | functionCallAtom
    | surfaceCallAtom
    | memberAtom
    | LPAREN singleExpression RPAREN
    ;

numberLiteral :
    NUMBER
    | '+' NUMBER
    | '-' NUMBER
    ;

functionCallAtom :
    IDENTIFIER functionCallParameterList
    ;

surfaceCallAtom :
    IDENTIFIER surfaceCallParameterList
    ;

surfaceCallParameterList :
    functionCallParameterList? LBRACKET singleExpression RBRACKET
    ;

functionCallParameterList :
    LPAREN (singleExpression (COMMA singleExpression)*)? RPAREN
    ;

memberAtom :
    IDENTIFIER (DOT memberAtom)?
    ;

KW_RETURN : 'return';
KW_CONST : 'const';

NUMBER : [0-9]+(('.')?[0-9]+)?;
IDENTIFIER : [a-zA-Z]([a-zA-Z0-9_]*);

LPAREN : '(';
RPAREN : ')';
LBRACKET : '[';
RBRACKET : ']';
COMMA : ',';
COLON : ':';
SEMICOLON : ';';
DOT : '.';
ARROW : '->';
BLOCK_START : '{';
BLOCK_END : '}';
OP_ASSIGN : '+=' | '-=' | '*=' | '/=';
EQUALS : '=';
ADD : '+';
SUB : '-';
DIV : '/';
MUL : '*';
POW : '^';
MOD : '%';

WS
   : [ \r\n\t] + -> skip
   ;