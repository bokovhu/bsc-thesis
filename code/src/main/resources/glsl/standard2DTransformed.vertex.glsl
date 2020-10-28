#version 410

layout(location = 0) in vec2 a_vertexPosition;
layout(location = 1) in vec2 a_vertexTexCoords;
layout(location = 2) in vec4 a_vertexColor;

uniform mat4 u_MVP;

varying vec2 v_UV;
varying vec4 v_color;

void main() {
    gl_Position = u_MVP * vec4(a_vertexPosition, 0.0, 1.0);
    v_UV = a_vertexTexCoords;
    v_color = a_vertexColor;
}