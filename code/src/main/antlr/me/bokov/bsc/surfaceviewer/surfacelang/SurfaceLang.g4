grammar SurfaceLang;

@header {
    package me.bokov.bsc.surfaceviewer.surfacelang;
}

world :
    (expression | material | light | prefab | resourceTexture)+
    ;

light :
    KW_LIGHT lightType (lightAlias)? lightDef
    ;

lightType :
    IDENTIFIER
    ;

lightAlias :
    '"' (IDENTIFIER)+ '"'
    ;

lightDef :
    LCURLY (lightParamList)? RCURLY
    ;

lightParamList :
    lightParam (COMMA lightParam)*
    ;

lightParam :
    lightParamName COLON (
    numberValue
        | vec2Value
        | vec3Value
        | vec4Value
        | mat2Value
        | mat3Value
        | mat4Value
        | boolValue
        | stringValue
        | expression
    )
    ;

lightParamName :
    IDENTIFIER
    ;

material :
    KW_MATERIAL materialType (materialAlias)? materialDef
    ;

materialType :
    IDENTIFIER
    ;

materialAlias :
    '"' (IDENTIFIER)+ '"'
    ;

materialDef :
    LCURLY (materialParamList)? RCURLY
    ;

materialParamList :
    materialParam (COMMA materialParam)*
    ;

materialParam :
    materialParamName COLON (
    numberValue
        | vec2Value
        | vec3Value
        | vec4Value
        | mat2Value
        | mat3Value
        | mat4Value
        | boolValue
        | stringValue
        | expression
    )
    ;

materialParamName :
    IDENTIFIER
    ;

prefab :
    KW_PREFAB prefabName LCURLY expression RCURLY
    ;

prefabName :
    IDENTIFIER
    ;

expression :
    KW_OBJECT? expressionName (expressionAlias)? (expressionTransform)? (expressionProperties)? (expressionPorts)? (expressionChildren)?
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
    | (defaultPortSpec)?
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

defaultPortSpec :
    expression
    ;

resourceTexture
    : KW_RESOURCE KW_TEXTURE resourceTextureName LPAREN resourceTextureLocation RPAREN
    ;

resourceTextureName
    : IDENTIFIER
    ;

resourceTextureLocation
    : stringValue
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
    | stringValue
    ;

numberValue :
    NUMBER
    ;

boolValue :
    KW_TRUE
    | KW_FALSE
    ;

vec2Value :
    KW_NORM? LPAREN x=numberValue COMMA y=numberValue RPAREN
    ;

vec3Value :
    KW_NORM? LPAREN x=numberValue COMMA y=numberValue COMMA z=numberValue RPAREN
    ;

vec4Value :
    KW_NORM? LPAREN x=numberValue COMMA y=numberValue COMMA z=numberValue COMMA w=numberValue RPAREN
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

stringValue :
    STRING
    ;

KW_AT : 'AT' | 'at';
KW_POSITION : 'POSITION' | 'position';
KW_SCALE : 'SCALE' | 'scale';
KW_ROTATE : 'ROTATE' | 'rotate';
KW_AROUND : 'AROUND' | 'around';
KW_BY : 'BY' | 'by';
KW_RADIANS : 'RADIANS' | 'radians';
KW_DEGREES : 'DEGREES' | 'degrees';
KW_TRUE : 'true' | 'TRUE';
KW_FALSE : 'false' | 'FALSE';
KW_MATERIAL : 'MATERIAL' | 'material';
KW_LIGHT : 'LIGHT' | 'light';
KW_OBJECT : 'OBJECT' | 'object';
KW_NORM : 'NORM' | 'norm';
KW_PREFAB : 'PREFAB' | 'prefab';
KW_RESOURCE : 'RESOURCE' | 'resource';
KW_TEXTURE : 'TEXTURE' | 'texture';

NUMBER : SUB? [0-9]+(('.')?[0-9]+)?;
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

STRING
    : '"' ~'"'* '"'
    ;
WS
   : [ \r\n\t] + -> skip
   ;


QUOTE : '"';