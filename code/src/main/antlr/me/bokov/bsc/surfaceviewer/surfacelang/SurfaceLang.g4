grammar SurfaceLang;

@header {
    package me.bokov.bsc.surfaceviewer.surfacelang;
}

world :
    (expression)+
    ;

expression :
    expressionName (expressionAlias)? (expressionTransform)? (expressionProperties)? (expressionPorts)? (expressionChildren)?
    ;

expressionName :
    IDENTIFIER
    ;

expressionAlias :
    '"' (IDENTIFIER)+ '"'
    ;

expressionTransform :
    KW_AT (positionTransform)? (scaleTransform)? (rotationTransform)?
    ;

positionTransform :
    KW_POSITION position=vec3Value
    ;

scaleTransform :
    KW_SCALE scale=numberValue
    ;

rotationTransform :
    KW_ROTATE KW_AROUND vec3Value KW_BY numberValue (KW_RADIANS | KW_DEGREES)
    ;

expressionProperties :
    LPAREN propertyMap RPAREN
    ;

expressionPorts :
    LCURLY portMap RCURLY
    ;

expressionChildren :
    LBRACKET childList RBRACKET
    ;

propertyMap :
    (propertySpec (COMMA propertySpec)*)?
    ;

portMap :
    (portSpec (COMMA portSpec)*)?
    ;

childList :
    (expression (COMMA expression)*)?
    ;

propertySpec :
    IDENTIFIER COLON propertyValue
    ;

portSpec :
    IDENTIFIER COLON expression
    ;

propertyValue :
    numberValue
    | vec2Value
    | vec3Value
    | vec4Value
    | mat2Value
    | mat3Value
    | mat4Value
    | boolValue
    ;

numberValue :
    NUMBER
    ;

boolValue :
    KW_TRUE
    | KW_FALSE
    ;

vec2Value :
    LPAREN x=numberValue COMMA y=numberValue RPAREN
    ;

vec3Value :
    LPAREN x=numberValue COMMA y=numberValue COMMA z=numberValue RPAREN
    ;

vec4Value :
    LPAREN x=numberValue COMMA y=numberValue COMMA z=numberValue COMMA w=numberValue RPAREN
    ;

mat2Value :
    LPAREN col0=vec2Value COMMA col1=vec2Value RPAREN
    ;

mat3Value :
    LPAREN col0=vec3Value COMMA col1=vec3Value COMMA col2=vec3Value RPAREN
    ;

mat4Value :
    LPAREN col0=vec4Value COMMA col1=vec4Value COMMA col2=vec4Value COMMA col3=vec4Value RPAREN
    ;

KW_AT : 'AT';
KW_POSITION : 'POSITION';
KW_SCALE : 'SCALE';
KW_ROTATE : 'ROTATE';
KW_AROUND : 'AROUND';
KW_BY : 'BY';
KW_RADIANS : 'RADIANS';
KW_DEGREES : 'DEGREES';
KW_TRUE : 'true';
KW_FALSE : 'false';

NUMBER : [0-9]+(('.')?[0-9]+)?;
IDENTIFIER : [a-zA-Z]([a-zA-Z0-9_]*);

LPAREN : '(';
RPAREN : ')';
LBRACKET : '[';
RBRACKET : ']';
LCURLY : '{';
RCURLY : '}';
COMMA : ',';
COLON : ':';
SEMICOLON : ';';
DOT : '.';
ARROW : '->';
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